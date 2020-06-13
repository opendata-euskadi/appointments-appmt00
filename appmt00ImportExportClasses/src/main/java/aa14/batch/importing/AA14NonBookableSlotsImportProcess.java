package aa14.batch.importing;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.google.inject.Injector;

import aa14.batch.AA14BatchProcessBase;
import aa14f.client.api.AA14ClientAPI;
import aa14f.model.AA14NonBookableSlot;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.UserCode;
import r01f.patterns.Memoized;
import r01f.types.Path;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;
import r01f.util.types.Strings;

/**
 * Imports appointments from a CSV file (; delimited)
 * The csv file MUST have the following structure:
 * 
 * 		LOC_ID;SCH_ID;DATE;HOUR;DURATION;NAME
 * 
 * where:
 * 		LOC_ID: location id
 * 					EJGV_SEGURIDAD_TRAFIKOA_BILBAO|CATIT|DONOSTIA|GASTEIZ
 * 					BIZILAGUN_COMUNIDADES_ALQUILERES_{AR|GI|BIZ}
 * 					BIZILAGUN_FIANZAS_DEPOSITO_CONTRATOS_{AR|GI|BIZ}
 * 		SCH_ID: schedule id
 * 					SCH_EJGV_SEGURIDAD_TRAFIKOA_{BILBAO|CATIT|DONOSTIA|GASTEIZ}
 * 					SCH_BIZILAGUN_FIANZAS_DEPOSITO_CONTRATOS_{AR|GI|BIZ}
 * 					SCH_BIZILAGUN_COMUNIDADES_ALQUILERES_{AR|GI|BIZ}
 * 
 * Run:
 * 		JVM argument: -javaagent:d:/eclipse/local_libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true 
 */
@Slf4j
public class AA14NonBookableSlotsImportProcess
	 extends AA14BatchProcessBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIN
/////////////////////////////////////////////////////////////////////////////////////////	
	public static void main(final String[] args) {
		try {
			Injector injector = _createInjector();
			AA14ClientAPI api = injector.getInstance(AA14ClientAPI.class);
			
			// import
			Path csvFilePath = Path.from("d:/eclipse/projects_aa14/aa14/aa14bDocs/test_data/2020_aa14_bizilagun_festivos_bizkaia.csv");
			Collection<AA14NonBookableSlot> createdNonBookableSlots = _createNonBookableSlotsFromCSVFile(api,
																							 		 	 csvFilePath);
			log.warn("{} appointments imported!",createdNonBookableSlots.size());
		} catch(Throwable th) {
			th.printStackTrace(System.out);
			log.error("Error while importing NON BOOKABLE SLOTS data: {}",
					  th.getMessage(),
					  th);	
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static Collection<AA14NonBookableSlot> _createNonBookableSlotsFromCSVFile(final AA14ClientAPI api,
																			  		  final Path filePath) throws IOException {
		log.info("[NonBookable slots import] file {}",
				 filePath.asAbsoluteString());
		
		// [1] - Load data from the CSV file
		Collection<NonBookableSlotCSVData> nonBookableSlotsAtCSV = _loadNonBookableSlotsFromCSVFile(filePath);
		
		log.info("...{} records will be imported",
				 nonBookableSlotsAtCSV);
		
		// [2] - Create the appointments
		Collection<AA14NonBookableSlot> outNonBookableSlots = FluentIterable.from(nonBookableSlotsAtCSV)
																	// filter nulls
																	.filter(new Predicate<NonBookableSlotCSVData>() {
																					@Override
																					public boolean apply(final NonBookableSlotCSVData csvData) {
																						return csvData != null;
																					}
																			})
																	// Transform from CSV to appointment
																	.transform(new Function<NonBookableSlotCSVData,AA14NonBookableSlot>() {
																						@Override
																						public AA14NonBookableSlot apply(final NonBookableSlotCSVData appointmentCSVData) {
																							return _createNonBookableSlotFrom(api,
																														  appointmentCSVData);
																						}
																			   })
																	// filter nulls
																	.filter(new Predicate<AA14NonBookableSlot>() {
																					@Override
																					public boolean apply(final AA14NonBookableSlot nontBookableSlot) {
																						return nontBookableSlot != null;
																					}
																			})
																	// Persist
																	.transform(new Function<AA14NonBookableSlot,AA14NonBookableSlot>() {
																						@Override
																						public AA14NonBookableSlot apply(final AA14NonBookableSlot nonBookableSlot) {
																							log.info("\t...non bookable slot for schedule oid={} at location oid={} at {}/{}/{} ({}:{}) for {}",
																									 nonBookableSlot.getScheduleOid(),nonBookableSlot.getOrgDivisionServiceLocationOid(),
																									 nonBookableSlot.getYear(),nonBookableSlot.getMonthOfYear(),nonBookableSlot.getDayOfMonth(),
																									 nonBookableSlot.getHourOfDay(),nonBookableSlot.getMinuteOfHour(),
																									 nonBookableSlot.getSubject());
//																							return appointment;
																							return api.bookedSlotsAPI()
																											.getForCRUD()
																												.create(nonBookableSlot)
																												.as(AA14NonBookableSlot.class);
																						}
																			   })
																	.toList();
		
		return outNonBookableSlots;
	}
	private static Collection<NonBookableSlotCSVData> _loadNonBookableSlotsFromCSVFile(final Path filePath) throws IOException {
		@Cleanup
		Reader in = Files.newReader(new File(filePath.asAbsoluteString()),
									Charset.defaultCharset());
		Iterable<CSVRecord> csvRecords = CSVFormat.RFC4180
											   .withDelimiter(';')
											   .withHeader()
											   .parse(in);
		return FluentIterable.from(csvRecords)
							 .transform(new Function<CSVRecord,NonBookableSlotCSVData>() {
												@Override
												public NonBookableSlotCSVData apply(final CSVRecord csvRecord) {
													CSVRecordWrapper record = new CSVRecordWrapper(csvRecord);
													NonBookableSlotCSVData outCSVData = null;
													try {
														String locId = record.get("LOC_ID");
														String schId = record.get("SCH_ID");
													    String date = record.get("DATE");
													    String hour = record.get("HOUR");
													    String duration = record.get("DURATION");
													    String name = record.get("NAME");
														
													    outCSVData = new NonBookableSlotCSVData(locId,schId,
							    													  	    	date,hour,duration,
							    													  	    	name);
													} catch(Throwable th) {
														th.printStackTrace(System.out);
														log.error("Erroneous csv record: {}",th.getMessage(),th);
													}
													return outCSVData;
												}
							 			})
							 .toList();
	}
	private static AA14NonBookableSlot _createNonBookableSlotFrom(final AA14ClientAPI api,
														  	  	  final NonBookableSlotCSVData appointmentCSVData) {
		if (!appointmentCSVData.isValid()) {
			log.warn("\tNOT VALID RECORD!!");
			return null;
		}
		
		AA14NonBookableSlot outNonBookableSlot = null;
		try {
			// Oid
			outNonBookableSlot = new AA14NonBookableSlot();
			outNonBookableSlot.setOid(AA14SlotOID.supply());
			
			// location & shcedule
			AA14OrgDivisionServiceLocationID locId = AA14OrgDivisionServiceLocationID.forId(appointmentCSVData.getLocId());
			AA14ScheduleID schId = AA14ScheduleID.forId(appointmentCSVData.getSchId());
			
			AA14Schedule sch = api.schedulesAPI()
										.getForCRUD()
											.loadById(schId);
			if (!sch.getServiceLocationsIds().contains(locId)) throw new IllegalStateException("Schedule " + schId + " is NOT valid for location " + locId);
			
			AA14OrgDivisionServiceLocation loc = api.orgDivisionServiceLocationsAPI()
														.getForCRUD()
															.loadById(locId);
			
			outNonBookableSlot.setOrgDivisionServiceLocationOid(loc.getOid());	// mandatory
			outNonBookableSlot.setScheduleOid(sch.getOid());
			outNonBookableSlot.setUserCode(UserCode.forId("system"));
			
			// Date
			outNonBookableSlot.setYear(appointmentCSVData.getYear());
			outNonBookableSlot.setMonthOfYear(appointmentCSVData.getMonthOfYear());
			outNonBookableSlot.setDayOfMonth(appointmentCSVData.getDayOfMonth());
			outNonBookableSlot.setHourOfDay(appointmentCSVData.getHourOfDay());
			outNonBookableSlot.setMinuteOfHour(appointmentCSVData.getMinuteOfHour());
			outNonBookableSlot.setDurationMinutes(Integer.parseInt(appointmentCSVData.getDuration()));
			
			// Subject
			outNonBookableSlot.setSubject(appointmentCSVData.getName());
		} catch(Throwable th) {
			th.printStackTrace(System.out);
			log.error("Error while transforming the csv record into a non bookable slot: {}",th.getMessage(),th);
			outNonBookableSlot = null;
		}
		return outNonBookableSlot;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	// yyyy/MM/dd: ((?:19|20)\d\d)([- /.])(0[1-9]|1[012])\2(0[1-9]|[12][0-9]|3[01])
	// dd/MM/yyyy: (0[1-9]|[12][0-9]|3[01])([- /.])(0[1-9]|1[012])\2((?:19|20)\d\d)
	private static final Pattern DATE_PATTERN = Pattern.compile("(0[1-9]|[12][0-9]|3[01])([- /.])(0[1-9]|1[012])\\2((?:19|20)\\d\\d)");
	private static final Pattern HOUR_PATTERN = Pattern.compile("([0-9]|0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])");
	
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	private static class NonBookableSlotCSVData {
		@Getter private final String _locId;
		@Getter private final String _schId;
		@Getter private final String _date;
		@Getter private final String _time;
		@Getter private final String _duration;
		@Getter private final String _name;
		
		private final Memoized<LocalDate> _theDate = new Memoized<LocalDate>() {
														@Override
														public LocalDate supply() {
															Matcher m = DATE_PATTERN.matcher(_date);
															if (m.find()) {
																String dayOfMonth = m.group(1);
																String monthOfYear = m.group(3);
																String year = m.group(4);
																return new LocalDate(Integer.parseInt(year),
																					 Integer.parseInt(monthOfYear),
																					 Integer.parseInt(dayOfMonth));
															} 
															throw new IllegalArgumentException(_date + " does NOT match a date format!!");
														}
										  		};
		private final Memoized<LocalTime> _theTime = new Memoized<LocalTime>() {
														@Override
														public LocalTime supply() {
															Matcher m = HOUR_PATTERN.matcher(_time);
															if (m.find()) {
																String hourOfDay = m.group(1);	
																String minutesOfHour = m.group(2);
																return new LocalTime(Integer.parseInt(hourOfDay),
																					 Integer.parseInt(minutesOfHour),
																					 0);
															} 
															throw new IllegalArgumentException(_time + " does NOT match a time format!!");
														}
											   };
		
		public boolean isValid() {
			return Strings.isNOTNullOrEmpty(_locId) && Strings.isNOTNullOrEmpty(_schId)
				&& Strings.isNOTNullOrEmpty(_date) && Strings.isNOTNullOrEmpty(_time) && Strings.isNOTNullOrEmpty(_duration)
				&& Strings.isNOTNullOrEmpty(_name);
		}
		public Year getYear() {
			return Year.of(_theDate.get());
		}
		public MonthOfYear getMonthOfYear() {
			return MonthOfYear.of(_theDate.get());
		}
		public DayOfMonth getDayOfMonth() {
			return DayOfMonth.of(_theDate.get());
		}
		public HourOfDay getHourOfDay() {
			return HourOfDay.of(_theTime.get());
		}
		public MinuteOfHour getMinuteOfHour() {
			return MinuteOfHour.of(_theTime.get());
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	private static class CSVRecordWrapper {
		private final CSVRecord _record;
		public String get(final String colName) {
			String val = _record.get(colName);
			return Strings.isNOTNullOrEmpty(val) ? val.trim() : null;
		}
	}
}
