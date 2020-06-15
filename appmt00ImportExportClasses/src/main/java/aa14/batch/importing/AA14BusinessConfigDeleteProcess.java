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
import com.google.common.base.Predicates;
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
import r01f.util.types.collections.CollectionUtils;

/**
 * Imports business config from a CSV file (; delimited)
 * The csv file MUST have the following structure:
 * 
 * 		BUSINESS_ID;ORG_ID;DIVISION_ID;SERVICE_ID;LOCATION_ID;SCHEDULE_ID;

 * Config: test = true will only process the file
 * 		   test = false will process the file AND delete the entities and schedules 
 * Run:
 * 		JVM argument: -javaagent:{dev-home}/local_libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true 
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
			
			log.warn("Process is {}!!", (test? "SIMULATED (will not deleting anything)" : "NOT SIMULATED. CHANGES will be MADE"));
			// import
			//Path csvFilePath = Path.from("/home/develop/projects_aa14/aa14b/aa14bDocs/test_data/justizia-delete.csv");
			Path csvFilePath = Path.from("c:/develop/projects/legacy/aa14/aa14bDocs/test_data/2020_aa14_justizia_servicioInformacion_config.csv");
			
			// Load the business config from the csv file
			AA14BusinessConfig businessConfig = _loadBusinessConfigFromCSVFile(api,
																			   csvFilePath);
			int deletedRecords = _deleteBusinessConfigFromDB(api,
															 businessConfig);
			log.info("{} lines procesed!",
					 deletedRecords);
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
	private static AA14BusinessConfig _loadBusinessConfigFromCSVFile(final AA14ClientAPI api,
																	 final Path filePath) throws IOException {
		log.info("[Business config import] file {}",
				 filePath.asAbsoluteString());
		
		// [1] - Load data from the CSV file
		Collection<AA14BusinessConfigToDeleteCSVData> businessConfigAtCSV = _loadBusinessConfigFromCSVFile(filePath);
		
		log.info("...{} records will be imported",
				 businessConfigAtCSV);
		
		// [2] - Create the appointments
		Collection<AA14BusinessConfig> businessConfigs = FluentIterable.from(businessConfigAtCSV)
																// filter nulls
																.filter(new Predicate<AA14BusinessConfigToDeleteCSVData>() {
																				@Override
																				public boolean apply(final AA14BusinessConfigToDeleteCSVData csvData) {
																					return csvData != null;
																				}
																		})
																// Transform from CSV to business config
																.transform(new Function<AA14BusinessConfigToDeleteCSVData,AA14BusinessConfig>() {
																					@Override
																					public AA14BusinessConfig apply(final AA14BusinessConfigToDeleteCSVData appointmentCSVData) {
																						return _createBussinesConfigFrom(api,
																													  	appointmentCSVData);
																					}
																		   })
																// filter nulls
																.filter(Predicates.notNull())
																// return
																.toList();
		// [3] - Consolidate in a single [business config]
		AA14BusinessConfig outCfg = new AA14BusinessConfig();
		outCfg.setOrganization(null);
		outCfg.setDivisions(Sets.newHashSet());
		outCfg.setServices(Sets.newHashSet());
		outCfg.setLocations(Sets.newHashSet());
		outCfg.setSchedules(Sets.newHashSet());
		
		for (AA14BusinessConfig cfg : businessConfigs) {
			if (outCfg.getId() == null) outCfg.setId(cfg.getId());	// all items should have the same id
			
			AA14Organization org = cfg.getOrganization();
			AA14OrgDivision div = CollectionUtils.pickOneAndOnlyElement(cfg.getDivisions());
			AA14OrgDivisionService srvc = CollectionUtils.pickOneAndOnlyElement(cfg.getServices());
			AA14OrgDivisionServiceLocation loc = CollectionUtils.pickOneAndOnlyElement(cfg.getLocations());
			AA14Schedule sch = CollectionUtils.pickOneAndOnlyElement(cfg.getSchedules());
			
			if (outCfg.getOrganization() == null) outCfg.setOrganization(org);
			if (outCfg.getDivisionFor(div.getId()) == null) outCfg.getDivisions().add(div);
			if (outCfg.getServiceFor(srvc.getId()) == null) outCfg.getServices().add(srvc);
			if (outCfg.getLocationFor(loc.getId()) == null) outCfg.getLocations().add(loc);
			if (outCfg.getScheduleFor(sch.getId()) == null) outCfg.getSchedules().add(sch);
		}
		return outCfg;
	}
	@SuppressWarnings("resource")
	private static Collection<AA14BusinessConfigToDeleteCSVData> _loadBusinessConfigFromCSVFile(final Path filePath) throws IOException {
		@Cleanup
		Reader in = Files.newReader(new File(filePath.asAbsoluteString()),
									Charset.defaultCharset());
		Iterable<CSVRecord> csvRecords = CSVFormat.RFC4180
											   .withDelimiter(';')
											   .withHeader()
											   .parse(in);
		return FluentIterable.from(csvRecords)
							 .transform(new Function<CSVRecord,AA14BusinessConfigToDeleteCSVData>() {
												@Override
												public AA14BusinessConfigToDeleteCSVData apply(final CSVRecord csvRecord) {
													AA14CSVRecordWrapper record = new AA14CSVRecordWrapper(csvRecord);
													AA14BusinessConfigToDeleteCSVData outCSVData = null;
													try {
														String businessId = record.get("BUSINESS_ID");
														String orgId = record.get("ORG_ID");
														String divisionId = record.get("DIVISION_ID");
														String serviceId = record.get("SERVICE_ID");
														String locationId = record.get("LOCATION_ID");
														String scheduleId = record.get("SCHEDULE_ID");
													 	
													    outCSVData = new AA14BusinessConfigToDeleteCSVData(businessId, 
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
														  		final AA14BusinessConfigToDeleteCSVData businessConfigCSVData) {
		if (!businessConfigCSVData.isValid()) {
			log.info("\tNOT VALID RECORD!!");
			return null;
		}
		
		AA14BusinessConfig outCfg = new AA14BusinessConfig();
		outCfg.setId(AA14BusinessID.fromString(businessConfigCSVData.getBusinessId()));
		outCfg.setDivisions(Sets.newHashSet());
		outCfg.setServices(Sets.newHashSet());
		outCfg.setLocations(Sets.newHashSet());
		outCfg.setSchedules(Sets.newHashSet());
		
		// organization
		AA14Organization org = new AA14Organization();
		org.setBusinessId(outCfg.getId());
		org.setOid(AA14OrganizationOID.supply());
		org.setId(AA14OrganizationID.fromString(businessConfigCSVData.getOrgId()));
		outCfg.setOrganization(org);

		// Division
		AA14OrgDivision div = new AA14OrgDivision();
		div.setBusinessId(outCfg.getId());
		div.setOid(AA14OrgDivisionOID.supply());
		div.setId(AA14OrgDivisionID.fromString(businessConfigCSVData.getDivisionId()));
		div.setOrgRef(new AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID>(outCfg.getOrganization().getOid(),
																								   outCfg.getOrganization().getId()));
		outCfg.getDivisions()
						 .add(div);
		
		// Service
		AA14OrgDivisionService srvc = new AA14OrgDivisionService();
		srvc.setBusinessId(outCfg.getId());
		srvc.setOid(AA14OrgDivisionServiceOID.supply());
		srvc.setId(AA14OrgDivisionServiceID.fromString(businessConfigCSVData.getServiceId()));
		srvc.setOrgRef(new AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID>(outCfg.getOrganization().getOid(),
																									outCfg.getOrganization().getId()));
		srvc.setOrgDivisionRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionOID,AA14OrgDivisionID>(div.getOid(),
																										  div.getId()));
		outCfg.getServices()
						 .add(srvc);

		// Location 
		AA14OrgDivisionServiceLocation loc = new AA14OrgDivisionServiceLocation();
		loc.setBusinessId(outCfg.getId());
		loc.setOid(AA14OrgDivisionServiceLocationOID.supply());
		loc.setId(AA14OrgDivisionServiceLocationID.fromString(businessConfigCSVData.getLocationId()));
		loc.setOrgRef(new AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID>(outCfg.getOrganization().getOid(),
																								   outCfg.getOrganization().getId()));
		loc.setOrgDivisionRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionOID,AA14OrgDivisionID>(div.getOid(),
																									     div.getId()));
		loc.setOrgDivisionServiceRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID>(srvc.getOid(),
																															  srvc.getId()));		
		outCfg.getLocations()
						 .add(loc);

		// Schedule 
		AA14Schedule sch = new AA14Schedule();
		sch.setBusinessId(outCfg.getId());
		sch.setOid(AA14ScheduleOID.supply());
		sch.setId(AA14ScheduleID.fromString(businessConfigCSVData.getScheduleId()));
		sch.addServiceLocationRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID>(loc.getOid(),
																																		   loc.getId()));
		outCfg.getSchedules()
			  .add(sch);
		return outCfg;
	}
	private static int _deleteBusinessConfigFromDB(final AA14ClientAPI api,
												   final AA14BusinessConfig cfg) {
		
		AA14BusinessID businessId = cfg.getId();
		log.info("DELETE businessId {} objects",
				 businessId.asString());
	
		int numOfObjsDeleted = 0;
		
		// --- Schedules
		for (AA14Schedule sch : cfg.getSchedules()) {
			AA14Schedule dbSch = api.schedulesAPI()
						  		    .getForCRUD()
						  		    .loadByIdOrNull(sch.getId());
			if (dbSch != null) {
				log.info("\t\t\t[Schedule]: {} exists... deleting it",sch.getId());
				dbSch = api.schedulesAPI()
						   .getForCRUD()
						   .delete(dbSch);
				numOfObjsDeleted++;
			} else {
				log.info("\t\t\t[Schedule]: {} does NOT exist... skip it",sch.getId());
			}
		}
		// --- Locations
		for (AA14OrgDivisionServiceLocation loc : cfg.getLocations()) {
			AA14OrgDivisionServiceLocation dbLoc = api.orgDivisionServiceLocationsAPI()
													  		  .getForCRUD()
													  		  .loadByIdOrNull(loc.getId());
			if (dbLoc != null) {
				log.info("\t\t\t[Location]: {}  exists... deleting it",loc.getId());
				dbLoc = api.orgDivisionServiceLocationsAPI()
						   .getForCRUD()
						   .delete(dbLoc);
				numOfObjsDeleted++;
			} else {
				log.info("\t\t\t[Location]: {} does NOT exist... skip it",loc.getId());
			}
		}
		// --- Services
		for (AA14OrgDivisionService srvc : cfg.getServices()) {
			AA14OrgDivisionService dbSrvc = api.orgDivisionServicesAPI()
											  		  .getForCRUD()
											  		  .loadByIdOrNull(srvc.getId());
			if (dbSrvc != null) {
				log.info("\t\t[Service]: {} exists... deleting it",srvc.getId());
				dbSrvc = api.orgDivisionServicesAPI()
								  .getForCRUD()
								  .delete(dbSrvc);
				numOfObjsDeleted++;
			} else {
				log.info("\t\t[Service]: {} does NOT exists... skip it",srvc.getId());
			}
		}
		// --- Divisions
		for (AA14OrgDivision div : cfg.getDivisions()) {
			AA14OrgDivision dbDiv = api.orgDivisionsAPI()
											  .getForCRUD()
											  .loadByIdOrNull(div.getId());
			if (dbDiv != null) {
				log.info("\t[Division]: {} exists... deleting it",div.getId());
				dbDiv = api.orgDivisionsAPI()
										  .getForCRUD()
										  .delete(dbDiv);
				numOfObjsDeleted++;
			} else {
				log.info("\t[Division]: {} does NOT exists... skip it",div.getId());
			}
		}

		// --- Organization
//		AA14Organization org =  cfg.getOrganization();
//		AA14Organization dbOrg = api.organizationsAPI()
//				  						   .getForCRUD()
//				  						   .loadByIdOrNull(org.getId());
//		if (dbOrg != null) {
//			log.info("[Org]: {} exists... deleting it",org.getId());
//			dbOrg = api.organizationsAPI()		// beware! use the returned object (the oid might change)
//								  .getForCRUD()
//								  .delete(dbOrg);
//			numOfObjsDeleted++;
//		} else {
//			log.info("[Org]: {} does NOT exists... skip it",org.getId());
//		}
		if (numOfObjsDeleted > 0) {
			api.configAPI().forceReloadConfig();
		}
		return numOfObjsDeleted;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	private static class AA14BusinessConfigToDeleteCSVData {
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
}
