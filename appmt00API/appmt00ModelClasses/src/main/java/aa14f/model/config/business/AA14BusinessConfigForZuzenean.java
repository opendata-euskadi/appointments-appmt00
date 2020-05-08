package aa14f.model.config.business;

import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;

public class AA14BusinessConfigForZuzenean 
	 extends A14BusinessConfigWrappedBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final AA14OrganizationID ORG_ID = AA14OrganizationID.forId("ORG_ZUZENEAN");
	
	// division & service
	public static final AA14OrgDivisionID ZUZENEAN_ID = AA14OrgDivisionID.forId("EJGV_ZUZENEAN");
	public static final AA14OrgDivisionServiceID SERVICE_ID = AA14OrgDivisionServiceID.forId("ZUZENEAN_PRESENCIAL");
	
	// locations & schedules
	public static final AA14OrgDivisionServiceLocationID LOC_PRESENCIAL_AR_ID = AA14OrgDivisionServiceLocationID.forId("ZUZENEAN_PRESENCIAL_AR");
	public static final AA14ScheduleID SCH_PRESENCIAL_AR_ID = AA14ScheduleID.forId("SCH_ZUZENEAN_PRESENCIAL_AR");

	public static final AA14OrgDivisionServiceLocationID LOC_PRESENCIAL_GI_ID = AA14OrgDivisionServiceLocationID.forId("ZUZENEAN_PRESENCIAL_GI");
	public static final AA14ScheduleID SCH_PRESENCIAL_GI_ID = AA14ScheduleID.forId("SCH_ZUZENEAN_PRESENCIAL_GI");
	
	public static final AA14OrgDivisionServiceLocationID LOC_PRESENCIAL_BIZ_ID = AA14OrgDivisionServiceLocationID.forId("ZUZENEAN_PRESENCIAL_BIZ");
	public static final AA14ScheduleID SCH_PRESENCIAL_BIZ_ID = AA14ScheduleID.forId("SCH_ZUZENEAN_PRESENCIAL_BIZ");
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14BusinessConfigForZuzenean(final AA14BusinessConfig wrapped) {
		super(wrapped);
	}
	public static AA14BusinessConfigForZuzenean wrap(final AA14BusinessConfig wrapped) {
		return new AA14BusinessConfigForZuzenean(wrapped);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrgDivisionService getFace2FaceService() {
		return _wrappedConfig.getServiceFor(SERVICE_ID);
	}
	public AA14OrgDivisionService getComunidadesAlquileresService() {
		return _wrappedConfig.getServiceFor(SERVICE_ID);
	}
	public AA14OrgDivisionServiceLocation getPresencialArabaLocation() {
		return _wrappedConfig.getLocationFor(LOC_PRESENCIAL_AR_ID);
	}
	public AA14OrgDivisionServiceLocation getPresencialGipuzkoaLocation() {
		return _wrappedConfig.getLocationFor(LOC_PRESENCIAL_GI_ID);
	}
	public AA14OrgDivisionServiceLocation getPresencialBizkaiaLocation() {
		return _wrappedConfig.getLocationFor(LOC_PRESENCIAL_BIZ_ID);
	}
}
