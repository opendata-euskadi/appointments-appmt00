 package aa14.batch.importing;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.inject.Injector;

import aa14.batch.AA14BatchProcessBase;
import aa14f.client.api.AA14ClientAPI;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14OrganizationalModelObjectRef;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.AA14ScheduleBookingConfig;
import aa14f.model.config.AA14ScheduleBookingLimit;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.LanguageTextsMapBacked;
import r01f.types.Path;
import r01f.types.datetime.Time;
import r01f.util.types.Dates;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Imports business configuration from a CSV file (; delimited)
 * The csv file MUST have the following structure:
 * 
 * 		BUSINESS_ID;SCHEDULE_ID;SCHEDULE_NAME_ES;SCHEDULE_NAME_EU;MIN_BOOKABLE_HOUR;MAX_BOOKABLE_HOUR;SLOT_LENGHT;MAX_APS;DATE_LIMIT;DAYS_LIMIT;LOCATION_IDS
 *
 * Where LOCATION_IDS: locationIds delimited by comma (,)
 * Make sure the file has a suitable encoding (i.e: ISO-8859-1)
 * Configuration parameters: test = true will only process the file
 * 		   					 test = false will process the file AND persists any new config 
 * Run:
 * 		JVM argument: -javaagent:{dev-home}/local_libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true 
 */
@Slf4j
public class AA14BusinessConfigSchedulessImportProcess 
	 extends AA14BatchProcessBase {
	
	public static boolean test = false;
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIN
/////////////////////////////////////////////////////////////////////////////////////////	
	public static void main(final String[] args) {
		try {
			Injector injector = _createInjector();
			AA14ClientAPI api = injector.getInstance(AA14ClientAPI.class);
			
			log.warn("Process is {}!!", (test? "SIMULATED (will not insert anything)" : "NOT SIMULATED. CHANGES will be MADE"));
			// import
			//Path csvFilePath = Path.from("/home/develop/projects_aa14/aa14b/aa14bDocs/test_data/Justizia-sch.csv");
			Path csvFilePath = Path.from("c:/develop/projects/legacy/aa14/aa14bDocs/test_data/2020_aa14_justizia_servicioInformacion_config.csv");
			Collection<AA14Schedule> createdData = _createSchedulesFromCSVFile(api,
																			   csvFilePath);
			log.info("{} schedules procesed!", 
					 createdData.size());
		} catch(Throwable th) {
			th.printStackTrace(System.out);
			log.error("Error while importing CONFIG DATA: {}",
					  th.getMessage(),
					  th);	
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static Collection<AA14Schedule> _createSchedulesFromCSVFile(final AA14ClientAPI api,
																			 final Path filePath) throws IOException {
		log.info("[Business config import] file {}",
				 filePath.asAbsoluteString());
		
		// [1] - Load data from the CSV file
		Collection<ScheduleCSVData> scheduleDataAtCSV = _loadSchedulesDataFromCSVFile(filePath);
		
		log.info("...{} records will be imported",
				 scheduleDataAtCSV.size());
		
		// [2] - Create the appointments
		Collection<AA14Schedule> outSchs = FluentIterable.from(scheduleDataAtCSV)
												// filter nulls
												.filter(new Predicate<ScheduleCSVData>() {
																@Override
																public boolean apply(final ScheduleCSVData csvData) {
																	return csvData != null;
																}
														})
												// Transform from CSV to appointment
												.transform(new Function<ScheduleCSVData,AA14Schedule>() {
																	@Override
																	public AA14Schedule apply(final ScheduleCSVData scheduleCSVData) {
																		return _buildScheduleFrom(api,
																								  scheduleCSVData);
																	}
														   })
												// filter nulls
												.filter(Predicates.notNull())
												// Persist
												.transform(new Function<AA14Schedule,AA14Schedule>() {
																	@Override
																	public AA14Schedule apply(final AA14Schedule sch) {
																		if (test) return sch;
																		AA14Schedule outSch = api.schedulesAPI()
																					  		     .getForCRUD()
																					  		     .save(sch);
																		return outSch;
																	}
														   })
												.toList();
		return outSchs;
	}
	@SuppressWarnings("resource")
	private static Collection<ScheduleCSVData> _loadSchedulesDataFromCSVFile(final Path filePath) throws IOException {
		@Cleanup
		Reader in = Files.newReader(new File(filePath.asAbsoluteString()),
									Charset.defaultCharset());
		Iterable<CSVRecord> csvRecords = CSVFormat.RFC4180
											   .withDelimiter(';')
											   .withHeader()
											   .parse(in);
		return FluentIterable.from(csvRecords)
							 .transform(new Function<CSVRecord,ScheduleCSVData>() {
												@Override
												public ScheduleCSVData apply(final CSVRecord csvRecord) {
													CSVRecordWrapper record = new CSVRecordWrapper(csvRecord);
													ScheduleCSVData outCSVData = null;
													try {
														String businessId = record.get("BUSINESS_ID");
													 	String scheduleId = record.get("SCHEDULE_ID");
													 	String scheduleNameEs = record.get("SCHEDULE_NAME_ES");
													 	String scheduleNameEu = record.get("SCHEDULE_NAME_EU");
													 	String minBookableHour = record.get("MIN_BOOKABLE_HOUR");
													 	String maxBookableHour = record.get("MAX_BOOKABLE_HOUR");
													 	String slotLenght = record.get("SLOT_LENGHT");
													 	String maxAppointmentsPerSlot = record.get("MAX_APS");
													 	String dateLimit = record.get("DATE_LIMIT");
													 	String daysLimit = record.get("DAYS_LIMIT");
													 	Collection<String> locationIds = Lists.newArrayList(record.get("LOCATION_IDS").split(","));
														
													    outCSVData = new ScheduleCSVData(businessId, 
													    									  scheduleId, scheduleNameEs, scheduleNameEu, minBookableHour, maxBookableHour, Integer.valueOf(slotLenght), Integer.valueOf(maxAppointmentsPerSlot), dateLimit, Integer.valueOf(daysLimit),
													    									  locationIds);
													} catch(Throwable th) {
														th.printStackTrace(System.out);
														log.error("Erroneous csv record: {}",th.getMessage(),th);
													}
													return outCSVData;
												}
							 			})
							 .toList();
	}
	private static AA14Schedule _buildScheduleFrom(final AA14ClientAPI api,
												   final ScheduleCSVData schCSVData) {
		if (!schCSVData.isValid()) {
			log.info("\tNOT VALID RECORD!!");
			return null;
		}
		
		try {
			// Schedule
			AA14Schedule outSch = api.schedulesAPI()
									 .getForCRUD()
									 .loadByIdOrNull(AA14ScheduleID.fromString(schCSVData.getScheduleId()));
			if (outSch == null) {
				log.info("Creating new [Schedule] {}", 
						 schCSVData.getScheduleId());
				outSch = new AA14Schedule();
				outSch.setBusinessId(AA14BusinessID.fromString(schCSVData.getBusinessId()));
				outSch.setOid(AA14ScheduleOID.supply());
				outSch.setId(AA14ScheduleID.fromString(schCSVData.getScheduleId()));				 
				
				outSch.setNameByLanguage(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
													   .add(Language.SPANISH,schCSVData.getScheduleNameEs())
													   .add(Language.BASQUE,schCSVData.getScheduleNameEu()));
				AA14ScheduleBookingLimit outBookingLimit = null;
				if (schCSVData.getDateLimit() != null ||
					schCSVData.getDaysLimit() != null) {
					outBookingLimit = new AA14ScheduleBookingLimit();
					if (schCSVData.getDateLimit() !=null) {
						outBookingLimit.setDateLimit(Dates.fromFormatedString(schCSVData.getDateLimit(),Dates.DEFAULT_FORMAT));
					}
					if (schCSVData.getDaysLimit() > -1) {
						outBookingLimit.setDaysInFutureLimit(schCSVData.getDaysLimit());
					}
				}
				AA14ScheduleBookingConfig outSchConfig = new AA14ScheduleBookingConfig(Time.of(schCSVData.getMinBookableHour()), 
																					   Time.of(schCSVData.getMaxBookableHour()), 
																					   schCSVData.getSlotLenght().intValue(), 
																					   schCSVData.getMaxAppointmentsPerSlot().intValue(), 
																					   outBookingLimit);
				outSch.setBookingConfig(outSchConfig);
				
				// add a reference to the location
				Collection<AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,
												 			AA14OrgDivisionServiceLocationID>> locRefs = FluentIterable.from(schCSVData.getLocationIds())
													 														   .transform(new Function<String,
													 																   				   AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,
													 																   				   									AA14OrgDivisionServiceLocationID>>() {
																																@Override
																																public AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID, 
																																									    AA14OrgDivisionServiceLocationID> apply(final String locId) {
																																	AA14OrgDivisionServiceLocation loc = api.configAPI()
																																											 .getLocationFor(AA14OrgDivisionServiceLocationID.fromString(locId));
																																	if (loc == null) {
																																		log.error("[location] id={} does NOT exists",
																																				  locId);
																																		return null;
																																	}
																																	return loc.getReference();
																																}
													 															   
													 														   			  })
													 														   .filter(Predicates.notNull())
													 														   .toList();
				if (CollectionUtils.hasData(locRefs)) {
					for (AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,
												 			AA14OrgDivisionServiceLocationID> locRef : locRefs) {
						outSch.addServiceLocationRef(locRef);
					}
				}
			} else {
				log.info("[Schedule] {} ALREADY exists", 
						 schCSVData.getScheduleId());
			}
			return outSch;
			
		} catch(Throwable th) {
			th.printStackTrace(System.out);
			log.error("Error while transforming the csv record into a businessConfig: {}",th.getMessage(),th);
			
		}
		return null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Pattern DATE_PATTERN = Pattern.compile("(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/((?:19|20)?\\d\\d)");
	private static final Pattern HOUR_PATTERN = Pattern.compile("([0-9]|0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):00");
	
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	private static class ScheduleCSVData {
		@Getter private final String _businessId;
		@Getter private final String _scheduleId;
		@Getter private final String _scheduleNameEs;
		@Getter private final String _scheduleNameEu;
		@Getter private final String _minBookableHour;
		@Getter private final String _maxBookableHour;
		@Getter private final Integer _slotLenght;
		@Getter private final Integer _maxAppointmentsPerSlot;
		@Getter private final String _dateLimit;
		@Getter private final Integer _daysLimit;
		@Getter private final Collection<String> _locationIds;
		
		public boolean isValid() {
			boolean dateLimitValid = false;
			if ((Strings.isNOTNullOrEmpty(_dateLimit) && _dateLimit.matches(DATE_PATTERN.toString()) 
				|| Strings.isNullOrEmpty(_dateLimit))){
				dateLimitValid = true;
			}
			return Strings.isNOTNullOrEmpty(_businessId)
				// schedule
				&& Strings.isNOTNullOrEmpty(_scheduleId)
				&& Strings.isNOTNullOrEmpty(_minBookableHour) && _minBookableHour.matches(HOUR_PATTERN.toString()) 
				&& Strings.isNOTNullOrEmpty(_maxBookableHour) && _maxBookableHour.matches(HOUR_PATTERN.toString())
				&& _slotLenght > 0
				&& dateLimitValid
				&& !_locationIds.isEmpty();
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
