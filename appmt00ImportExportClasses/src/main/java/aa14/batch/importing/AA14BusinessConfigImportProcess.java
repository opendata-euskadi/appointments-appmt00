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
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.inject.Injector;

import aa14.batch.AA14BatchProcessBase;
import aa14f.client.api.AA14ClientAPI;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14OrganizationalModelObjectRef;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.AA14ScheduleBookingConfig;
import aa14f.model.config.AA14ScheduleBookingLimit;
import aa14f.model.config.business.AA14BusinessConfig;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
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
import r01f.types.contact.ContactInfo;
import r01f.types.contact.ContactInfoUsage;
import r01f.types.contact.ContactMail;
import r01f.types.contact.ContactPhone;
import r01f.types.datetime.Time;
import r01f.types.geo.GeoCountry;
import r01f.types.geo.GeoCounty;
import r01f.types.geo.GeoMunicipality;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoZipCode;
import r01f.types.geo.GeoPosition;
import r01f.types.geo.GeoState;
import r01f.types.geo.GeoStreet;
import r01f.util.types.Dates;
import r01f.util.types.Strings;

/**
 * Imports business configuration from a CSV file (; delimited)
 * The csv file MUST have the following structure:
 * 
 * 		BUSINESS_ID;
 * 		ORG_ID;ORG_NAME_ES;ORG_NAME_EU;
 * 		DIVISION_ID;DIVISION_NAME_ES;DIVISION_NAME_EU;
 * 		SERVICE_ID;SERVICE_NAME_ES;SERVICE_NAME_EU;
 * 		LOCATION_ID;LOCATION_NAME_ES;LOCATION_NAME_EU;
 * 		STREET_ES;STREET_EU;ZIPCODE;MUNICIPALITY_ES;MUNICIPALITY_EU;COUNTY_OID;COUNTY_ES;COUNTY_EU;STATE_ES;STATE_EU;COUNTRY_ES;COUNTRY_EU;PHONE;E-MAIL;
 * 		COLOR;
 * 		SCHEDULE_ID;SCHEDULE_NAME_ES;SCHEDULE_NAME_EU;
 * 		MIN_BOOKABLE_HOUR;MAX_BOOKABLE_HOUR;
 * 		SLOT_LENGHT;
 * 		MAX_APS;
 * 		DATE_LIMIT;
 * 		DAYS_LIMIT
 *
 * Make sure the file has a suitable encoding (i.e: ISO-8859-1)
 * Configuration parameters: test = true will only process the file
 * 		   					 test = false will process the file AND persists any new config 
 * Run:
 * 		JVM argument: -javaagent:d:/eclipse/local_libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true 
 */
@Slf4j
public class AA14BusinessConfigImportProcess 
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
			Path csvFilePath = Path.from("/home/develop/projects_aa14/aa14b/aa14bDocs/test_data/2020_aa14_justizia_servicioInformacion_config.csv");
			Collection<AA14BusinessConfig> createdData = _createBusinessConfigFromCSVFile(api,
																						 csvFilePath);
			log.info("{} lines procesed!", createdData.size());
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
	private static Collection<AA14BusinessConfig> _createBusinessConfigFromCSVFile(final AA14ClientAPI api,
																			  	   final Path filePath) throws IOException {
		log.info("[Business config import] file {}",
				 filePath.asAbsoluteString());
		
		// [1] - Load data from the CSV file
		Collection<BusinessConfigCSVData> businessConfigAtCSV = _loadBusinessConfigFromCSVFile(filePath);
		
		log.info("...{} records will be imported",
				 businessConfigAtCSV);
		
		// [2] - Create the appointments
		Collection<AA14BusinessConfig> outAppointments = FluentIterable.from(businessConfigAtCSV)
																	// filter nulls
																	.filter(new Predicate<BusinessConfigCSVData>() {
																					@Override
																					public boolean apply(final BusinessConfigCSVData csvData) {
																						return csvData != null;
																					}
																			})
																	// Transform from CSV to appointment
																	.transform(new Function<BusinessConfigCSVData,AA14BusinessConfig>() {
																						@Override
																						public AA14BusinessConfig apply(final BusinessConfigCSVData appointmentCSVData) {
																							return _createBussinesConfigFrom(api,
																														  	appointmentCSVData);
																						}
																			   })
																	// filter nulls
																	.filter(new Predicate<AA14BusinessConfig>() {
																					@Override
																					public boolean apply(final AA14BusinessConfig businessConfig) {
																						return businessConfig != null;
																					}
																			})
																	// Persist
																	.transform(new Function<AA14BusinessConfig,AA14BusinessConfig>() {
																						@Override
																						public AA14BusinessConfig apply(final AA14BusinessConfig businessConfig) {
//																							log.info("\t...appointment for schedule oid={} at location oid={} at {}/{}/{} ({}:{}) for dni={}",
//																									 appointment.getScheduleOid(),appointment.getOrgDivisionServiceLocationOid(),
//																									 appointment.getYear(),appointment.getMonthOfYear(),appointment.getDayOfMonth(),
//																									 appointment.getHourOfDay(),appointment.getMinuteOfHour(),
//																									 appointment.getSubject().getId());
																							if (test) {
																								return businessConfig;
																							}
																							else {
																								return _loadBusinessConfigToDB(api, businessConfig);
																							}
																						}
																			   })
																	.toList();
		
		return outAppointments;
	}
	private static Collection<BusinessConfigCSVData> _loadBusinessConfigFromCSVFile(final Path filePath) throws IOException {
		@Cleanup
		Reader in = Files.newReader(new File(filePath.asAbsoluteString()),
									Charset.defaultCharset());
		Iterable<CSVRecord> csvRecords = CSVFormat.RFC4180
											   .withDelimiter(';')
											   .withHeader()
											   .parse(in);
		return FluentIterable.from(csvRecords)
							 .transform(new Function<CSVRecord,BusinessConfigCSVData>() {
												@Override
												public BusinessConfigCSVData apply(final CSVRecord csvRecord) {
													CSVRecordWrapper record = new CSVRecordWrapper(csvRecord);
													BusinessConfigCSVData outCSVData = null;
													try {
														String businessId = record.get("BUSINESS_ID");
														String orgId = record.get("ORG_ID");
														String orgNameEs = record.get("ORG_NAME_ES");
														String orgNameEu = record.get("ORG_NAME_EU");
														String divisionId = record.get("DIVISION_ID");
														String divisionNameEs = record.get("DIVISION_NAME_ES");
														String divisionNameEu = record.get("DIVISION_NAME_EU");
														String serviceId = record.get("SERVICE_ID");
														String serviceNameEs = record.get("SERVICE_NAME_ES");
														String serviceNameEu = record.get("SERVICE_NAME_EU");
														String locationId = record.get("LOCATION_ID");
														String locationNameEs = record.get("LOCATION_NAME_ES");
														String locationNameEu = record.get("LOCATION_NAME_EU");
														String streetEs = record.get("STREET_ES");
														String streetEu = record.get("STREET_EU");
														String zipcode = record.get("ZIPCODE");
														String municipalityEs = record.get("MUNICIPALITY_ES");
														String municipalityEu = record.get("MUNICIPALITY_EU");
														String countyOid = record.get("COUNTY_OID");
														String countyEs = record.get("COUNTY_ES");
														String countyEu = record.get("COUNTY_EU");
														String stateEs = record.get("STATE_ES");
														String stateEu = record.get("STATE_EU");
														String countryEs = record.get("COUNTRY_ES");
														String countryEu = record.get("COUNTRY_EU");
														String phone = record.get("PHONE");
														String email = record.get("E-MAIL");
													 	String color = record.get("COLOR");
													 	String scheduleId = record.get("SCHEDULE_ID");
													 	String scheduleNameEs = record.get("SCHEDULE_NAME_ES");
													 	String scheduleNameEu = record.get("SCHEDULE_NAME_EU");
													 	String minBookableHour = record.get("MIN_BOOKABLE_HOUR");
													 	String maxBookableHour = record.get("MAX_BOOKABLE_HOUR");
													 	String slotLenght = record.get("SLOT_LENGHT");
													 	String maxAppointmentsPerSlot = record.get("MAX_APS");
													 	String dateLimit = record.get("DATE_LIMIT");
													 	String daysLimit = record.get("DAYS_LIMIT");
														
													    outCSVData = new BusinessConfigCSVData(businessId, 
													    									   orgId, orgNameEs, orgNameEu, 
													    									   divisionId, divisionNameEs, divisionNameEu, 
													    									   serviceId, serviceNameEs, serviceNameEu, 
													    									   locationId, locationNameEs, locationNameEu, streetEs, streetEu, zipcode, municipalityEs, municipalityEu, Integer.valueOf(countyOid), countyEs, countyEu, stateEs, stateEu, countryEs, countryEu, phone, email, color, 
													    									   scheduleId, scheduleNameEs, scheduleNameEu, minBookableHour, maxBookableHour, Integer.valueOf(slotLenght), Integer.valueOf(maxAppointmentsPerSlot), dateLimit, Integer.valueOf(daysLimit));
													} catch(Throwable th) {
														th.printStackTrace(System.out);
														log.error("Erroneous csv record: {}",th.getMessage(),th);
													}
													return outCSVData;
												}
							 			})
							 .toList();
	}
	private static AA14BusinessConfig _createBussinesConfigFrom(final AA14ClientAPI api,
														  final BusinessConfigCSVData businessConfigCSVData) {
		if (!businessConfigCSVData.isValid()) {
			log.info("\tNOT VALID RECORD!!");
			return null;
		}
		
		AA14BusinessConfig outBusinessConfig = null;
		try {
			// Id and organization
			outBusinessConfig =  api.configAPI().getConfigFor(AA14BusinessID.fromString(businessConfigCSVData.getBusinessId()));
			if (outBusinessConfig == null) {
				log.info("Preparing to persist new [Business Config]: {}", businessConfigCSVData.getBusinessId());
				outBusinessConfig = new AA14BusinessConfig();
				outBusinessConfig.setId(AA14BusinessID.fromString(businessConfigCSVData.getBusinessId()));
				AA14Organization outOrg = new AA14Organization();
				outOrg.setOid(AA14OrganizationOID.supply());
				outOrg.setId(AA14OrganizationID.fromString(businessConfigCSVData.getOrgId()));
				outOrg.setBusinessId(outBusinessConfig.getId());
				outOrg.setNameByLanguage(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
										   .add(Language.SPANISH,businessConfigCSVData.getOrgNameEs())
										   .add(Language.BASQUE,businessConfigCSVData.getOrgNameEu()));	
				outBusinessConfig.setOrganization(outOrg);
			}
			else {
				log.info("Preparing to add new config to {} [Business Config]", businessConfigCSVData.getBusinessId());
				if (!outBusinessConfig.getOrganization().getId().equals(AA14OrganizationID.fromString(businessConfigCSVData.getOrgId()))) 
				throw new IllegalStateException("Organization " + businessConfigCSVData.getOrgId() + " is NOT valid for businessConfig " + outBusinessConfig.getId());
			}

			
			// Division
			AA14OrgDivision outDiv = null;
			outDiv = outBusinessConfig.getDivisionFor(AA14OrgDivisionID.fromString(businessConfigCSVData.getDivisionId()));
			if (outDiv == null) {
				log.info("Preparing to add new [Division] {} to {} business config", 
						 businessConfigCSVData.getDivisionId(),
						 businessConfigCSVData.getBusinessId());
				outDiv = new AA14OrgDivision();
				outDiv.setOid(AA14OrgDivisionOID.supply());
				outDiv.setId(AA14OrgDivisionID.fromString(businessConfigCSVData.getDivisionId()));
				outDiv.setBusinessId(outBusinessConfig.getId());
				outDiv.setOrgRef(new AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID>(
										outBusinessConfig.getOrganization().getOid(),
										outBusinessConfig.getOrganization().getId()));
				outDiv.setNameByLanguage(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
										   .add(Language.SPANISH,businessConfigCSVData.getOrgNameEs())
										   .add(Language.BASQUE,businessConfigCSVData.getOrgNameEu()));		
				Collection<AA14OrgDivision> divisions = Sets.newHashSet(outBusinessConfig.getDivisions()); //inmutable
				divisions.add(outDiv);
				outBusinessConfig.setDivisions(divisions);
			}
			
			// Service
			AA14OrgDivisionService outSrv = null;
			outSrv = outBusinessConfig.getServiceFor(AA14OrgDivisionServiceID.fromString(businessConfigCSVData.getServiceId()));
			if (outSrv == null) {
				log.info("Preparing to add new [Service] {} to {} business config", 
						 businessConfigCSVData.getServiceId(),
						 businessConfigCSVData.getBusinessId());
				outSrv = new AA14OrgDivisionService();
				outSrv.setOid(AA14OrgDivisionServiceOID.supply());
				outSrv.setId(AA14OrgDivisionServiceID.fromString(businessConfigCSVData.getServiceId()));
				outSrv.setBusinessId(outBusinessConfig.getId());
				outSrv.setOrgRef(new AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID>(
										outBusinessConfig.getOrganization().getOid(),
										outBusinessConfig.getOrganization().getId()));
				outSrv.setOrgDivisionRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionOID,AA14OrgDivisionID>(
										outDiv.getOid(),
										outDiv.getId()));
				outSrv.setNameByLanguage(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
										   .add(Language.SPANISH,businessConfigCSVData.getServiceNameEs())
										   .add(Language.BASQUE,businessConfigCSVData.getServiceNameEu()));		
				Collection<AA14OrgDivisionService> services = Sets.newHashSet(outBusinessConfig.getServices()); //inmutable
				services.add(outSrv);
				outBusinessConfig.setServices(services);
			}
			
			// Location 
			AA14OrgDivisionServiceLocation outLoc = null;
			outLoc = outBusinessConfig.getLocationFor(AA14OrgDivisionServiceLocationID.forId(businessConfigCSVData.getLocationId()));
			if (outLoc == null) {
				log.info("Preparing to add new [Location] {} to {} business config", 
						 businessConfigCSVData.getLocationId(),
						 businessConfigCSVData.getBusinessId());
				outLoc = new AA14OrgDivisionServiceLocation();
				outLoc.setOid(AA14OrgDivisionServiceLocationOID.supply());
				outLoc.setId(AA14OrgDivisionServiceLocationID.fromString(businessConfigCSVData.getLocationId()));
				outLoc.setBusinessId(outBusinessConfig.getId());
				outLoc.setOrgRef(new AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID>(
										outBusinessConfig.getOrganization().getOid(),
										outBusinessConfig.getOrganization().getId()));
				outLoc.setOrgDivisionRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionOID,AA14OrgDivisionID>(
										outDiv.getOid(),
										outDiv.getId()));
				outLoc.setOrgDivisionServiceRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID>(
										outSrv.getOid(),
										outSrv.getId()));
				outLoc.setNameByLanguage(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
										   .add(Language.SPANISH,businessConfigCSVData.getLocationNameEs())
										   .add(Language.BASQUE,businessConfigCSVData.getLocationNameEu()));	
				outLoc.setPosition(GeoPosition.create()
											  .withCountry(GeoCountry.create()
											  		 				 .withNameInLang(Language.SPANISH, businessConfigCSVData.getCountryEs())
											  		 				 .withNameInLang(Language.BASQUE, businessConfigCSVData.getCountryEu()))
											  .withState(GeoState.create()
												  		 		 .withNameInLang(Language.SPANISH,businessConfigCSVData.getStateEs())
												  		 		 .withNameInLang(Language.BASQUE, businessConfigCSVData.getStateEu()))
											  .withCounty(GeoCounty.create(GeoCountyID.forId(businessConfigCSVData.getCountyOid()))
													  		  	   .withNameInLang(Language.SPANISH, businessConfigCSVData.getCountyEs())
													  		  	   .withNameInLang(Language.BASQUE, businessConfigCSVData.getCountyEu()))
											  .withMunicipality(GeoMunicipality.create()
																		  	   .withNameInLang(Language.SPANISH, businessConfigCSVData.getMunicipalityEs())
																		  	   .withNameInLang(Language.BASQUE, businessConfigCSVData.getMunicipalityEu()))
											  .withStreet(GeoStreet.create()
														  	   .withNameInLang(Language.SPANISH, businessConfigCSVData.getStreetEs())
												  			   .withNameInLang(Language.BASQUE, businessConfigCSVData.getStreetEu()))
											  .withZipCode(GeoZipCode.forId(businessConfigCSVData.getZipcode())));
				
				
				if (Strings.isNOTNullOrEmpty(businessConfigCSVData.getPhone())){
					ContactInfo contactInfo = outLoc.getContactInfo();
					if (contactInfo == null) {
						contactInfo = ContactInfo.create();
					}
					contactInfo.addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
						  								 			   .withNumber(businessConfigCSVData.getPhone()));
					outLoc.setContactInfo(contactInfo);
				}
				
				if (Strings.isNOTNullOrEmpty(businessConfigCSVData.getEmail())){
					ContactInfo contactInfo = outLoc.getContactInfo();
					if (contactInfo == null) {
						contactInfo = ContactInfo.create();
					}
					contactInfo.addMailAddress(ContactMail.createToBeUsedFor(ContactInfoUsage.WORK)
						  								 					.mailTo(businessConfigCSVData.getEmail()));
					outLoc.setContactInfo(contactInfo);
				}
						  						
				Collection<AA14OrgDivisionServiceLocation> locations = Sets.newHashSet(outBusinessConfig.getLocations()); //inmutable
				locations.add(outLoc);
				outBusinessConfig.setLocations(locations);
				
			}
			// Schedule 
			AA14Schedule outSch = null;
			outSch = outBusinessConfig.getScheduleFor(AA14ScheduleID.fromString(businessConfigCSVData.getScheduleId()));
			if (outSch != null) {
				System.out.println (outSch.getServiceLocationsIds().size());
			}
			if (outSch == null) {
				log.info("Preparing to add new [Schedule] {} to {} business config", 
						 businessConfigCSVData.getScheduleId(),
						 businessConfigCSVData.getBusinessId());
				outSch = new AA14Schedule();
				outSch.setOid(AA14ScheduleOID.supply());
				outSch.setId(AA14ScheduleID.fromString(businessConfigCSVData.getScheduleId()));
				outSch.setBusinessId(outBusinessConfig.getId());
				outSch.addServiceLocationRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID>(
										outLoc.getOid(),
										outLoc.getId()));
				outSch.setNameByLanguage(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
										   .add(Language.SPANISH,businessConfigCSVData.getScheduleNameEs())
										   .add(Language.BASQUE,businessConfigCSVData.getScheduleNameEu()));
				AA14ScheduleBookingLimit outBookingLimit = null;
				if (businessConfigCSVData.getDateLimit() != null ||
					businessConfigCSVData.getDaysLimit() != null) {
					outBookingLimit = new AA14ScheduleBookingLimit();
					if (businessConfigCSVData.getDateLimit() !=null) {
						outBookingLimit.setDateLimit(Dates.fromFormatedString(businessConfigCSVData.getDateLimit(), Dates.DEFAULT_FORMAT));
					}
					if (businessConfigCSVData.getDaysLimit() > -1) {
						outBookingLimit.setDaysInFutureLimit(businessConfigCSVData.getDaysLimit());
					}
				}
				AA14ScheduleBookingConfig outSchConfig = new AA14ScheduleBookingConfig(Time.of(businessConfigCSVData.getMinBookableHour()), 
																					   Time.of(businessConfigCSVData.getMaxBookableHour()), 
																					   businessConfigCSVData.getSlotLenght().intValue(), 
																					   businessConfigCSVData.getMaxAppointmentsPerSlot().intValue(), 
																					   outBookingLimit);
				outSch.setBookingConfig(outSchConfig);
				Collection<AA14Schedule> schedules = Sets.newHashSet(outBusinessConfig.getSchedules()); //inmutable
				schedules.add(outSch);
				outBusinessConfig.setSchedules(schedules);
			}
			else  if (!outSch.getServiceLocationsIds().contains(outLoc.getId())){
				  log.info("Preparing to add new [Location] {} to {} [Schedule] in {} business config", 
						  					businessConfigCSVData.getLocationId(),
						  					businessConfigCSVData.getScheduleId(),
						  					businessConfigCSVData.getBusinessId());
				  outSch.addServiceLocationRef(outLoc.getOid(), outLoc.getId());
			}
			
		} catch(Throwable th) {
			th.printStackTrace(System.out);
			log.error("Error while transforming the csv record into a businessConfig: {}",th.getMessage(),th);
			outBusinessConfig = null;
		}
		return outBusinessConfig;
	}
	
	/**
	 * Ensures the required config is present testing the presence at the DB
	 * of the required records
	 * @param businessId
	 * @param builder
	 */
	private static AA14BusinessConfig _loadBusinessConfigToDB(final AA14ClientAPI api,
													  final AA14BusinessConfig config) {
		
		boolean needsReloading = false;
		//Business id //TODO persist this if is new too
		AA14BusinessID businessId = config.getId();
		log.info("Processing data for businessId {}", businessId.asString());
		// --- Organization
		AA14Organization org =  config.getOrganization();
		AA14Organization dbOrg = api.organizationsAPI()
				  						   .getForCRUD()
				  						   .loadByIdOrNull(org.getId());
		if (dbOrg == null) {
			log.info("[Org]: {} did NOT previously exists... creating it",org.getId());
			dbOrg = api.organizationsAPI()		// beware! use the returned object (the oid might change)
								  .getForCRUD()
								  .save(org);
			needsReloading=true;
		} else {
			log.info("[Org]: {} exists... skip it",org.getId());
		}
		org.setOid(dbOrg.getOid());// beware! use the returned object (the oid might change)
		
		// --- Divisions
		for (AA14OrgDivision div : config.getDivisions()) {
			AA14OrgDivision dbDiv = api.orgDivisionsAPI()
											  .getForCRUD()
											  .loadByIdOrNull(div.getId());
			if (dbDiv == null) {
				log.info("\t[Division]: {} did NOT previously exists... creating it",div.getId());
				dbDiv = api.orgDivisionsAPI()
										  .getForCRUD()
										  .save(div);
				needsReloading = true;
			} else {
				log.info("\t[Division]: {} exists... skip it",div.getId());
			}
			div.setOid(dbDiv.getOid());	// beware! use the returned object (the oid might change)
		}
		// --- Services
		for (AA14OrgDivisionService srvc : config.getServices()) {
			AA14OrgDivisionService dbSrvc = api.orgDivisionServicesAPI()
											  		  .getForCRUD()
											  		  .loadByIdOrNull(srvc.getId());
			if (dbSrvc == null) {
				log.info("\t\t[Service]: {} did NOT previously exists... creating it",srvc.getId());
				dbSrvc = api.orgDivisionServicesAPI()
										  .getForCRUD()
										  .save(srvc);
				needsReloading = true;
			} else {
				log.info("\t\t[Service]: {} exists... skip it",srvc.getId());
			}
			srvc.setOid(dbSrvc.getOid());// beware! use the returned object (the oid might change)
		}
		// --- Locations
		for (AA14OrgDivisionServiceLocation loc : config.getLocations()) {
			AA14OrgDivisionServiceLocation dbLoc = api.orgDivisionServiceLocationsAPI()
													  		  .getForCRUD()
													  		  .loadByIdOrNull(loc.getId());
			if (dbLoc == null) {
				log.info("\t\t\t[Location]: {} did NOT previously exists... creating it",loc.getId());
				dbLoc = api.orgDivisionServiceLocationsAPI()
										  .getForCRUD()
										  .save(loc);
			} else {
				log.info("\t\t\t[Location]: {} exists... skip it",loc.getId());
			}
			loc.setOid(dbLoc.getOid());// beware! use the returned object (the oid might change)
		}
		// --- Schedules
		for (AA14Schedule sch : config.getSchedules()) {
			final AA14Schedule dbSch = api.schedulesAPI()
								  		   .getForCRUD()
								  		   .loadByIdOrNull(sch.getId());
			if (dbSch == null) {
				log.info("\t\t\t[Schedule]: {} did NOT previously exists... creating it",sch.getId());
				api.schedulesAPI()
								  .getForCRUD()
								  .save(sch);
			} else {
				if (!dbSch.getServiceLocationsIds().containsAll(sch.getServiceLocationsIds())){
					log.info("dbSch: " + dbSch.getServiceLocationsIds());
					log.info("sch: " + sch.getServiceLocationsIds());
					log.info("\t\t\t[Schedule]: {} exists... update location references",sch.getId());
					Collection<AA14OrgDivisionServiceLocationID> newLocationsForSch = FluentIterable.from(sch.getServiceLocationsIds())
																									.filter((locationId) -> !dbSch.getServiceLocationsIds().contains(locationId))
																									.toList(); 
					newLocationsForSch.forEach((newLocationId) -> {
													log.info("\\t\\t\\t[Schedule]: {} adding location {}", dbSch.getId(), newLocationId);
													AA14OrgDivisionServiceLocation dbLoc = api.orgDivisionServiceLocationsAPI()
													  		  .getForCRUD()
													  		  .loadByIdOrNull(newLocationId); 
													dbSch.addServiceLocationRef(dbLoc.getOid(), dbLoc.getId());

					});
					// FIXME trckReceivedRecord.getTrackingStatus().isThisNew() always returns NEW!!
				
					api.schedulesAPI()
								  .getForCRUD()
								  .update(dbSch);
				}
				else {
					log.info("\t\t\t[Schedule]: {} exists... skip it",sch.getId());
				}
			}
		}
		if (needsReloading) {
			api.configAPI().forceReloadConfig();
		}
		return config;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Pattern DATE_PATTERN = Pattern.compile("(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/((?:19|20)?\\d\\d)");
	private static final Pattern HOUR_PATTERN = Pattern.compile("([0-9]|0[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):00");
	
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	private static class BusinessConfigCSVData {
		@Getter private final String _businessId;
		@Getter private final String _orgId;
		@Getter private final String _orgNameEs;
		@Getter private final String _orgNameEu;
		@Getter private final String _divisionId;
		@Getter private final String _divisionNameEs;
		@Getter private final String _divisionNameEu;
		@Getter private final String _serviceId;
		@Getter private final String _serviceNameEs;
		@Getter private final String _serviceNameEu;
		@Getter private final String _locationId;
		@Getter private final String _locationNameEs;
		@Getter private final String _locationNameEu;
		@Getter private final String _streetEs;
		@Getter private final String _streetEu;
		@Getter private final String _zipcode;
		@Getter private final String _municipalityEs;
		@Getter private final String _municipalityEu;
		@Getter private final Integer _countyOid;
		@Getter private final String _countyEs;
		@Getter private final String _countyEu;
		@Getter private final String _stateEs;
		@Getter private final String _stateEu;
		@Getter private final String _countryEs;
		@Getter private final String _countryEu;
		@Getter private final String _phone;
		@Getter private final String _email;
		@Getter private final String _color;
		@Getter private final String _scheduleId;
		@Getter private final String _scheduleNameEs;
		@Getter private final String _scheduleNameEu;
		@Getter private final String _minBookableHour;
		@Getter private final String _maxBookableHour;
		@Getter private final Integer _slotLenght;
		@Getter private final Integer _maxAppointmentsPerSlot;
		@Getter private final String _dateLimit;
		@Getter private final Integer _daysLimit;
		
		public boolean isValid() {
			boolean dateLimitValid = false;
			if ((Strings.isNOTNullOrEmpty(_dateLimit) && _dateLimit.matches(DATE_PATTERN.toString()) 
				|| Strings.isNullOrEmpty(_dateLimit))){
				dateLimitValid = true;
			}
			return Strings.isNOTNullOrEmpty(_businessId) &&
					Strings.isNOTNullOrEmpty(_orgId) && //org
						Strings.isNOTNullOrEmpty(_orgNameEs) &&
						Strings.isNOTNullOrEmpty(_orgNameEu) &&
					Strings.isNOTNullOrEmpty(_divisionId) && //division
						Strings.isNOTNullOrEmpty(_divisionNameEs) && 
						Strings.isNOTNullOrEmpty(_divisionNameEu) &&
					Strings.isNOTNullOrEmpty(_serviceId) && //service
						Strings.isNOTNullOrEmpty(_serviceNameEs) &&
						Strings.isNOTNullOrEmpty(_serviceNameEu) &&
					Strings.isNOTNullOrEmpty(_locationId) && //location
						Strings.isNOTNullOrEmpty(_locationNameEs) &&
						Strings.isNOTNullOrEmpty(_locationNameEu) &&
						Strings.isNOTNullOrEmpty(_streetEs) &&
						Strings.isNOTNullOrEmpty(_streetEu) &&
						Strings.isNOTNullOrEmpty(_zipcode) &&
						Strings.isNOTNullOrEmpty(_municipalityEs) &&
						Strings.isNOTNullOrEmpty(_municipalityEu) &&
						_countyOid> 0 &&
						Strings.isNOTNullOrEmpty(_countyEs) &&
						Strings.isNOTNullOrEmpty(_countyEu) &&
						Strings.isNOTNullOrEmpty(_stateEs) &&
						Strings.isNOTNullOrEmpty(_stateEu) &&
						Strings.isNOTNullOrEmpty(_countryEs) &&
						Strings.isNOTNullOrEmpty(_countryEu) &&
					Strings.isNOTNullOrEmpty(_scheduleId) && //schedule
						Strings.isNOTNullOrEmpty(_minBookableHour) && _minBookableHour.matches(HOUR_PATTERN.toString()) &&
						Strings.isNOTNullOrEmpty(_maxBookableHour) && _maxBookableHour.matches(HOUR_PATTERN.toString()) &&
						_slotLenght > 0
						&& dateLimitValid;
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
