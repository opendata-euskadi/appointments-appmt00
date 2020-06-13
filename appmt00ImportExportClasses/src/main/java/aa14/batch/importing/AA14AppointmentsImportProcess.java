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
import aa14f.model.AA14Appointment;
import aa14f.model.AA14AppointmentSubject;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14AppointmentSubjectID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.patterns.Memoized;
import r01f.types.Path;
import r01f.types.contact.ContactInfo;
import r01f.types.contact.ContactInfoUsage;
import r01f.types.contact.ContactPhone;
import r01f.types.contact.ContactPhoneType;
import r01f.types.contact.NIFPersonID;
import r01f.types.contact.PersonBuilder;
import r01f.types.contact.PersonSalutation;
import r01f.types.contact.Phone;
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
 * 		LOC_ID;SCH_ID;DATE;HOUR;DURATION;DNI/NIE;NAME;SURNAME;PHONE
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
 * 		DATE: the dni date as yyyy/MM/dd
 * 		HOUR: the hour as HH:mm
 * 
 * Run:
 * 		JVM argument: -javaagent:d:/eclipse/local_libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true 
 */
@Slf4j
public class AA14AppointmentsImportProcess 
	 extends AA14BatchProcessBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIN
/////////////////////////////////////////////////////////////////////////////////////////	
	public static void main(final String[] args) {
		try {
			Injector injector = _createInjector();
			AA14ClientAPI api = injector.getInstance(AA14ClientAPI.class);
			
			// import
			Path csvFilePath = Path.from("c:/develop/projects/legacy/aa14/aa14bDocs/test_data/2019_aa14_medicalservice_festivos_araba.csv");
			Collection<AA14Appointment> createdAppointments = _createAppointmentsFromCSVFile(api,
																							 csvFilePath);
			log.warn("{} appointments imported!",createdAppointments.size());
		} catch(Throwable th) {
			th.printStackTrace(System.out);
			log.error("Error while importing APPOINTMENT DATA: {}",
					  th.getMessage(),
					  th);	
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static Collection<AA14Appointment> _createAppointmentsFromCSVFile(final AA14ClientAPI api,
																			  final Path filePath) throws IOException {
		log.info("[Appointment import] file {}",
				 filePath.asAbsoluteString());
		
		// [1] - Load data from the CSV file
		Collection<AppointmentCSVData> appointmentsAtCSV = _loadAppointmentsFromCSVFile(filePath);
		
		log.info("...{} records will be imported",
				 appointmentsAtCSV);
		
		// [2] - Create the appointments
		Collection<AA14Appointment> outAppointments = FluentIterable.from(appointmentsAtCSV)
																	// filter nulls
																	.filter(new Predicate<AppointmentCSVData>() {
																					@Override
																					public boolean apply(final AppointmentCSVData csvData) {
																						return csvData != null;
																					}
																			})
																	// Transform from CSV to appointment
																	.transform(new Function<AppointmentCSVData,AA14Appointment>() {
																						@Override
																						public AA14Appointment apply(final AppointmentCSVData appointmentCSVData) {
																							return _createAppointmentFrom(api,
																														  appointmentCSVData);
																						}
																			   })
																	// filter nulls
																	.filter(new Predicate<AA14Appointment>() {
																					@Override
																					public boolean apply(final AA14Appointment appointment) {
																						return appointment != null;
																					}
																			})
																	// Persist
																	.transform(new Function<AA14Appointment,AA14Appointment>() {
																						@Override
																						public AA14Appointment apply(final AA14Appointment appointment) {
																							log.info("\t...appointment for schedule oid={} at location oid={} at {}/{}/{} ({}:{}) for dni={}",
																									 appointment.getScheduleOid(),appointment.getOrgDivisionServiceLocationOid(),
																									 appointment.getYear(),appointment.getMonthOfYear(),appointment.getDayOfMonth(),
																									 appointment.getHourOfDay(),appointment.getMinuteOfHour(),
																									 appointment.getSubject().getId());
//																							return appointment;
																							return api.bookedSlotsAPI()
																											.getForCRUD()
																												.create(appointment)
																												.as(AA14Appointment.class);
																						}
																			   })
																	.toList();
		
		return outAppointments;
	}
	private static Collection<AppointmentCSVData> _loadAppointmentsFromCSVFile(final Path filePath) throws IOException {
		@Cleanup
		Reader in = Files.newReader(new File(filePath.asAbsoluteString()),
									Charset.defaultCharset());
		Iterable<CSVRecord> csvRecords = CSVFormat.RFC4180
											   .withDelimiter(';')
											   .withHeader()
											   .parse(in);
		return FluentIterable.from(csvRecords)
							 .transform(new Function<CSVRecord,AppointmentCSVData>() {
												@Override
												public AppointmentCSVData apply(final CSVRecord csvRecord) {
													CSVRecordWrapper record = new CSVRecordWrapper(csvRecord);
													AppointmentCSVData outCSVData = null;
													try {
														String locId = record.get("LOC_ID");
														String schId = record.get("SCH_ID");
													    String date = record.get("DATE");
													    String hour = record.get("HOUR");
													    String duration = record.get("DURATION");
													    String personId = record.get("DNI/NIE");
													    String name = record.get("NAME");
													    String surname = record.get("SURNAME");
													    String phone = record.get("PHONE");
														
													    outCSVData = new AppointmentCSVData(locId,schId,
							    													  	    date,hour,duration,
							    													  	    personId,name,surname,phone);
													} catch(Throwable th) {
														th.printStackTrace(System.out);
														log.error("Erroneous csv record: {}",th.getMessage(),th);
													}
													return outCSVData;
												}
							 			})
							 .toList();
	}
	private static AA14Appointment _createAppointmentFrom(final AA14ClientAPI api,
														  final AppointmentCSVData appointmentCSVData) {
		if (!appointmentCSVData.isValid()) {
			log.warn("\tNOT VALID RECORD!!");
			return null;
		}
		
		AA14Appointment outAppointment = null;
		try {
			// Oid
			outAppointment = new AA14Appointment();
			outAppointment.setOid(AA14SlotOID.supply());
			
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
			
			outAppointment.setOrgDivisionServiceLocationOid(loc.getOid());	// mandatory
			outAppointment.setScheduleOid(sch.getOid());
			
			// Date
			outAppointment.setYear(appointmentCSVData.getYear());
			outAppointment.setMonthOfYear(appointmentCSVData.getMonthOfYear());
			outAppointment.setDayOfMonth(appointmentCSVData.getDayOfMonth());
			outAppointment.setHourOfDay(appointmentCSVData.getHourOfDay());
			outAppointment.setMinuteOfHour(appointmentCSVData.getMinuteOfHour());
			outAppointment.setDurationMinutes(Integer.parseInt(appointmentCSVData.getDuration()));
			
			// Subject
			outAppointment.setSubject(new AA14AppointmentSubject(AA14AppointmentSubjectID.forId(appointmentCSVData.getDni()),
																 "Revisi�n del expediente"));
			
			// Requestor
			outAppointment.setPerson(PersonBuilder.createPersonWithId(NIFPersonID.forId(appointmentCSVData.getDni()))
												  .withName(appointmentCSVData.getName())
												  .withSurname(appointmentCSVData.getSurname())
												  .useSalutation(PersonSalutation.NONE)
												  .preferredLanguage(Language.DEFAULT)
												  .withDetails(null)
												  .build());
			outAppointment.setContactInfo(ContactInfo.create()
													 .addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.OTHER)
															 			   .type(ContactPhoneType.MOBILE)
															 			   .withNumber(Phone.create(appointmentCSVData.getPhone()))
															 			   .useAsDefault()));
		} catch(Throwable th) {
			th.printStackTrace(System.out);
			log.error("Error while transforming the csv record into an appointment: {}",th.getMessage(),th);
			outAppointment = null;
		}
		return outAppointment;
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
	private static class AppointmentCSVData {
		@Getter private final String _locId;
		@Getter private final String _schId;
		@Getter private final String _date;
		@Getter private final String _time;
		@Getter private final String _duration;
		@Getter private final String _dni;
		@Getter private final String _name;
		@Getter private final String _surname;
		@Getter private final String _phone;
		
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
				&& Strings.isNOTNullOrEmpty(_dni)
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
