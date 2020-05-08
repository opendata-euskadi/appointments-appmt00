package aa14f.client.api;

import java.util.Collection;

import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.business.builder.AA14ConfigBuilder;
import aa14f.model.config.business.builder.AA14ConfigBuilderForBizilagun;
import aa14f.model.config.business.builder.AA14ConfigBuilderForBloodDonation;
import aa14f.model.config.business.builder.AA14ConfigBuilderForJustizia;
import aa14f.model.config.business.builder.AA14ConfigBuilderForMedicalService;
import aa14f.model.config.business.builder.AA14ConfigBuilderForTrafikoa;
import aa14f.model.config.business.builder.AA14ConfigBuilderForZuzenean;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class AA14BusinessConfigCreator {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	private final AA14ClientAPI _clientApi;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public void ensureConfig() {
		// Trafikoa
		_ensureConfig(AA14BusinessID.TRAFIKOA,
					 new AA14ConfigBuilderForTrafikoa());
		// Bizilagun
		_ensureConfig(AA14BusinessID.BIZILAGUN,
					 new AA14ConfigBuilderForBizilagun());
		// Blood Donation
		_ensureConfig(AA14BusinessID.BLOOD_DONATION,
					 new AA14ConfigBuilderForBloodDonation());
		// Medical Service
		_ensureConfig(AA14BusinessID.MEDICAL_SERVICE,
					 new AA14ConfigBuilderForMedicalService());
		// Zuzenean
		_ensureConfig(AA14BusinessID.ZUZENEAN,
					 new AA14ConfigBuilderForZuzenean());
		// Justizia
		_ensureConfig(AA14BusinessID.JUSTIZIA,
					 new AA14ConfigBuilderForJustizia());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Ensures the required config is present testing the presence at the DB
	 * of the required records
	 * @param businessId
	 * @param builder
	 */
	private void _ensureConfig(final AA14BusinessID businessId,
							   final AA14ConfigBuilder builder) {
		// --- Organization
		AA14Organization org = builder.createOrganizationConfig(businessId);
		AA14Organization dbOrg = _clientApi.organizationsAPI()
				  						   .getForCRUD()
				  						   .loadByIdOrNull(org.getId());
		if (dbOrg == null) {
			log.warn("[Org]: {} did NOT previously exists... creating it",org.getId());
			dbOrg = _clientApi.organizationsAPI()		// beware! use the returned object (the oid might change)
								  .getForCRUD()
								  .save(org);
		} else {
			log.warn("[Org]: {} exists... skip it",org.getId());
		}
		org.setOid(dbOrg.getOid());// beware! use the returned object (the oid might change)
		
		// --- Divisions
		Collection<AA14OrgDivision> divs = builder.createOrgDivisionConfigs(org);
		for (AA14OrgDivision div : divs) {
			AA14OrgDivision dbDiv = _clientApi.orgDivisionsAPI()
											  .getForCRUD()
											  .loadByIdOrNull(div.getId());
			if (dbDiv == null) {
				log.warn("\t[Division]: {} did NOT previously exists... creating it",div.getId());
				dbDiv = _clientApi.orgDivisionsAPI()
										  .getForCRUD()
										  .save(div);
			} else {
				log.warn("\t[Division]: {} exists... skip it",div.getId());
			}
			div.setOid(dbDiv.getOid());	// beware! use the returned object (the oid might change)
		}
		// --- Services
		Collection<AA14OrgDivisionService> srvcs = builder.createOrgDivisionServicesConfigs(org,
																							divs);
		for (AA14OrgDivisionService srvc : srvcs) {
			AA14OrgDivisionService dbSrvc = _clientApi.orgDivisionServicesAPI()
											  		  .getForCRUD()
											  		  .loadByIdOrNull(srvc.getId());
			if (dbSrvc == null) {
				log.warn("\t\t[Service]: {} did NOT previously exists... creating it",srvc.getId());
				dbSrvc = _clientApi.orgDivisionServicesAPI()
										  .getForCRUD()
										  .save(srvc);
			} else {
				log.warn("\t\t[Service]: {} exists... skip it",srvc.getId());
			}
			srvc.setOid(dbSrvc.getOid());// beware! use the returned object (the oid might change)
		}
		// --- Locations
		Collection<AA14OrgDivisionServiceLocation> locs = builder.createOrgDivisionServiceLocationsConfigs(org,
																										  divs,
																										  srvcs);
		for (AA14OrgDivisionServiceLocation loc : locs) {
			AA14OrgDivisionServiceLocation dbLoc = _clientApi.orgDivisionServiceLocationsAPI()
													  		  .getForCRUD()
													  		  .loadByIdOrNull(loc.getId());
			if (dbLoc == null) {
				log.warn("\t\t\t[Location]: {} did NOT previously exists... creating it",loc.getId());
				dbLoc = _clientApi.orgDivisionServiceLocationsAPI()
										  .getForCRUD()
										  .save(loc);
			} else {
				log.warn("\t\t\t[Location]: {} exists... skip it",loc.getId());
			}
			loc.setOid(dbLoc.getOid());// beware! use the returned object (the oid might change)
		}
		// --- Schedules
		Collection<AA14Schedule> schs = builder.createSchedulesConfigs(org,
																	   divs,
																	   srvcs,
																	   locs);
		for (AA14Schedule sch : schs) {
			AA14Schedule dbSch = _clientApi.schedulesAPI()
								  		   .getForCRUD()
								  		   .loadByIdOrNull(sch.getId());
			if (dbSch == null) {
				log.warn("\t\t\t[Schedule]: {} did NOT previously exists... creating it",sch.getId());
				dbSch = _clientApi.schedulesAPI()
								  .getForCRUD()
								  .save(sch);
			} else {
				log.warn("\t\t\t[Schedule]: {} exists... skip it",sch.getId());
			}
			sch.setOid(dbSch.getOid());// beware! use the returned object (the oid might change)
		}
	}
}
