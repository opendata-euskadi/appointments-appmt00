package aa14f.model.config.business;

import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;

public class AA14BusinessConfigForBloodDonation 
	 extends A14BusinessConfigWrappedBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final AA14OrganizationID ORG_ID = AA14OrganizationID.forId("ORG_BLOOD_DONATION");
	
	// division & service
	public static final AA14OrgDivisionID DONATIONS_ID = AA14OrgDivisionID.forId("EJGV_DONACIONES"); //Dpto: Función pública
	public static final AA14OrgDivisionServiceID SERVICE_ID = AA14OrgDivisionServiceID.forId("DONACIONES_SANGRE");
	
	// locations & schedules
	public static final AA14OrgDivisionServiceLocationID LOC_DONACIONES_SANGRE_AR_ID = AA14OrgDivisionServiceLocationID.forId("DONACIONES_SANGRE_AR");
	public static final AA14ScheduleID SCH_DONACIONES_SANGRE_AR_ID = AA14ScheduleID.forId("SCH_DONACIONES_SANGRE_AR");
		
	public static final AA14OrgDivisionServiceLocationID LOC_DONACIONES_SANGRE_GI_ID = AA14OrgDivisionServiceLocationID.forId("DONACIONES_SANGRE_GI");
	public static final AA14ScheduleID SCH_DONACIONES_SANGRE_GI_ID = AA14ScheduleID.forId("SCH_DONACIONES_SANGRE_GI");
	
	public static final AA14OrgDivisionServiceLocationID LOC_DONACIONES_SANGRE_BIZ_ID = AA14OrgDivisionServiceLocationID.forId("DONACIONES_SANGRE_BIZ");
	public static final AA14ScheduleID SCH_DONACIONES_SANGRE_BIZ_ID = AA14ScheduleID.forId("SCH_DONACIONES_SANGRE_BIZ");
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14BusinessConfigForBloodDonation(final AA14BusinessConfig wrapped) {
		super(wrapped);
	}
	public static AA14BusinessConfigForBloodDonation wrap(final AA14BusinessConfig wrapped) {
		return new AA14BusinessConfigForBloodDonation(wrapped);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrgDivisionService getDonacionSangreService() {
		return _wrappedConfig.getServiceFor(SERVICE_ID);
	}
	public AA14OrgDivisionServiceLocation getDonacionSangreArabaLocation() {
		return _wrappedConfig.getLocationFor(LOC_DONACIONES_SANGRE_AR_ID);
	}
	public AA14OrgDivisionServiceLocation getDonacionSangreGipuzkoaLocation() {
		return _wrappedConfig.getLocationFor(LOC_DONACIONES_SANGRE_GI_ID);
	}
	public AA14OrgDivisionServiceLocation getDonacionSangreBizkaiaLocation() {
		return _wrappedConfig.getLocationFor(LOC_DONACIONES_SANGRE_BIZ_ID);
	}
}
