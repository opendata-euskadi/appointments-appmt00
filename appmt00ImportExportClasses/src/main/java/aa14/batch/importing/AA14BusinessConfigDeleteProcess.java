package aa14.batch.importing;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collection;

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
import r01f.types.Path;
import r01f.util.types.Strings;

/**
 * Imports business config from a CSV file (; delimited)
 * The csv file MUST have the following structure:
 * 
 * 		BUSINESS_ID;ORG_ID;DIVISION_ID;SERVICE_ID;LOCATION_ID;SCHEDULE_ID;

 * Config: test = true will only process the file
 * 		   test = false will process the file AND delete the entities and schedules 
 * Run:
 * 		JVM argument: -javaagent:d:/eclipse/local_libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true 
 */
@Slf4j
public class AA14BusinessConfigDeleteProcess 
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
			Path csvFilePath = Path.from("/home/develop/projects_aa14/aa14b/aa14bDocs/test_data/2020_aa14_justizia_srv_infor_config.csv");
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
																								return _deleteBusinessConfigFromDB(api, businessConfig);
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
														String divisionId = record.get("DIVISION_ID");
														String serviceId = record.get("SERVICE_ID");
														String locationId = record.get("LOCATION_ID");
														String scheduleId = record.get("SCHEDULE_ID");
													 	
													    outCSVData = new BusinessConfigCSVData(businessId, 
													    									   orgId, 
													    									   divisionId, 
													    									   serviceId, 
													    									   locationId, 
													    									   scheduleId);
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
			log.info("Preparing to delete data from [Business Config]: {}", businessConfigCSVData.getBusinessId());
			outBusinessConfig = new AA14BusinessConfig();
			outBusinessConfig.setId(AA14BusinessID.fromString(businessConfigCSVData.getBusinessId()));
			AA14Organization outOrg = new AA14Organization();
			outOrg.setOid(AA14OrganizationOID.supply());
			outOrg.setId(AA14OrganizationID.fromString(businessConfigCSVData.getOrgId()));
			outOrg.setBusinessId(outBusinessConfig.getId());
			outBusinessConfig.setOrganization(outOrg);

			// Division
			AA14OrgDivision outDiv = null;
			log.info("Preparing to delete [Division] {} from {} business config", 
					businessConfigCSVData.getDivisionId(),
					businessConfigCSVData.getBusinessId());
			outDiv = new AA14OrgDivision();
			outDiv.setOid(AA14OrgDivisionOID.supply());
			outDiv.setId(AA14OrgDivisionID.fromString(businessConfigCSVData.getDivisionId()));
			outDiv.setBusinessId(outBusinessConfig.getId());
			outDiv.setOrgRef(new AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID>(
					outBusinessConfig.getOrganization().getOid(),
					outBusinessConfig.getOrganization().getId()));

			Collection<AA14OrgDivision> divisions = Sets.newHashSet(); 
			divisions.add(outDiv);
			outBusinessConfig.setDivisions(divisions);
			
			// Service
			AA14OrgDivisionService outSrv = null;
			log.info("Preparing to delete [Service] {} from {} business config", 
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

			Collection<AA14OrgDivisionService> services = Sets.newHashSet(); 
			services.add(outSrv);
			outBusinessConfig.setServices(services);

			// Location 
			AA14OrgDivisionServiceLocation outLoc = null;
			log.info("Preparing to delete [Location] {} from {} business config", 
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
			Collection<AA14OrgDivisionServiceLocation> locations = Sets.newHashSet(); 
			locations.add(outLoc);
			outBusinessConfig.setLocations(locations);

			// Schedule 
			AA14Schedule outSch = null;
			log.info("Preparing to delete [Schedule] {} from {} business config", 
					businessConfigCSVData.getScheduleId(),
					businessConfigCSVData.getBusinessId());
			outSch = new AA14Schedule();
			outSch.setOid(AA14ScheduleOID.supply());
			outSch.setId(AA14ScheduleID.fromString(businessConfigCSVData.getScheduleId()));
			outSch.setBusinessId(outBusinessConfig.getId());
			outSch.addServiceLocationRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID>(
					outLoc.getOid(),
					outLoc.getId()));
			Collection<AA14Schedule> schedules = Sets.newHashSet();
			schedules.add(outSch);
			outBusinessConfig.setSchedules(schedules);
			
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
	private static AA14BusinessConfig _deleteBusinessConfigFromDB(final AA14ClientAPI api,
													  			 final AA14BusinessConfig configToDelete) {
		
		//boolean needsReloading = false;
		//Business id //TODO delete if if no aditional configuration
		AA14BusinessID businessId = configToDelete.getId();
		log.info("Processing data to DELETE for businessId {}", businessId.asString());
	
		// --- Schedules
		for (AA14Schedule sch : configToDelete.getSchedules()) {
			AA14Schedule dbSch = api.schedulesAPI()
								  		   .getForCRUD()
								  		   .loadByIdOrNull(sch.getId());
			if (dbSch != null) {
				log.info("\t\t\t[Schedule]: {} exists... detaching its locations",sch.getId());
				dbSch.setServiceLocationsRefs(Sets.newHashSet());
				dbSch.asDirtyStateTrackable().touch(); //force an update
				System.out.println(dbSch.asDirtyStateTrackable().isThisDirty());
				dbSch = api.schedulesAPI()
								  .getForCRUD()
								  .update(dbSch);
				log.info("\t\t\t[Schedule]: {} exists... deleting it",sch.getId());
				dbSch = api.schedulesAPI()
								  .getForCRUD()
								  .delete(sch);
			} else {
				log.info("\t\t\t[Schedule]: {} does not exist... skip it",sch.getId());
			}

		}
		// --- Locations
		for (AA14OrgDivisionServiceLocation loc : configToDelete.getLocations()) {
			AA14OrgDivisionServiceLocation dbLoc = api.orgDivisionServiceLocationsAPI()
													  		  .getForCRUD()
													  		  .loadByIdOrNull(loc.getId());
			if (dbLoc != null) {
				log.info("\t\t\t[Location]: {}  exists... deleting it",loc.getId());
				dbLoc = api.orgDivisionServiceLocationsAPI()
										  .getForCRUD()
										  .delete(loc);
			} else {
				log.info("\t\t\t[Location]: {} does not exist... skip it",loc.getId());
			}
			loc.setOid(dbLoc.getOid());// beware! use the returned object (the oid might change)
		}
				// --- Services
//		for (AA14OrgDivisionService srvc : configToDelete.getServices()) {
//			AA14OrgDivisionService dbSrvc = api.orgDivisionServicesAPI()
//											  		  .getForCRUD()
//											  		  .loadByIdOrNull(srvc.getId());
//			if (dbSrvc == null) {
//				log.info("\t\t[Service]: {} did NOT previously exists... creating it",srvc.getId());
//				dbSrvc = api.orgDivisionServicesAPI()
//										  .getForCRUD()
//										  .delete(srvc);
//				needsReloading = true;
//			} else {
//				log.info("\t\t[Service]: {} exists... skip it",srvc.getId());
//			}
//			srvc.setOid(dbSrvc.getOid());// beware! use the returned object (the oid might change)
//		}
//				// --- Divisions
//		for (AA14OrgDivision div : configToDelete.getDivisions()) {
//			AA14OrgDivision dbDiv = api.orgDivisionsAPI()
//											  .getForCRUD()
//											  .loadByIdOrNull(div.getId());
//			if (dbDiv == null) {
//				log.info("\t[Division]: {} did NOT previously exists... creating it",div.getId());
//				dbDiv = api.orgDivisionsAPI()
//										  .getForCRUD()
//										  .save(div);
//				needsReloading = true;
//			} else {
//				log.info("\t[Division]: {} exists... skip it",div.getId());
//			}
//			div.setOid(dbDiv.getOid());	// beware! use the returned object (the oid might change)
//		}
//
//				// --- Organization
//		AA14Organization org =  configToDelete.getOrganization();
//		AA14Organization dbOrg = api.organizationsAPI()
//				  						   .getForCRUD()
//				  						   .loadByIdOrNull(org.getId());
//		if (dbOrg == null) {
//			log.info("[Org]: {} did NOT previously exists... creating it",org.getId());
//			dbOrg = api.organizationsAPI()		// beware! use the returned object (the oid might change)
//								  .getForCRUD()
//								  .save(org);
//			needsReloading=true;
//		} else {
//			log.info("[Org]: {} exists... skip it",org.getId());
//		}
//		org.setOid(dbOrg.getOid());// beware! use the returned object (the oid might change)
//		if (needsReloading) {
//			api.configAPI().forceReloadConfig();
//		}
		return configToDelete;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	private static class BusinessConfigCSVData {
		@Getter private final String _businessId;
		@Getter private final String _orgId;
		@Getter private final String _divisionId;
		@Getter private final String _serviceId;
		@Getter private final String _locationId;
		@Getter private final String _scheduleId;
		
		public boolean isValid() {
			return Strings.isNOTNullOrEmpty(_businessId) && 
					Strings.isNOTNullOrEmpty(_orgId) &&
					Strings.isNOTNullOrEmpty(_divisionId) &&
					Strings.isNOTNullOrEmpty(_serviceId) &&
					Strings.isNOTNullOrEmpty(_locationId) &&
					Strings.isNOTNullOrEmpty(_scheduleId);
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
