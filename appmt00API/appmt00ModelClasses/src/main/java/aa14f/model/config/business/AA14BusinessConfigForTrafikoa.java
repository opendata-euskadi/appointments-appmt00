package aa14f.model.config.business;

import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;

public class AA14BusinessConfigForTrafikoa 
	 extends A14BusinessConfigWrappedBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final AA14OrganizationID ORG_ID = AA14OrganizationID.forId("ORG_TRAFIKOA");
	
	public static final AA14OrgDivisionID DIVISION_ID = AA14OrgDivisionID.forId("EJGV_SEGURIDAD");
	public static final AA14OrgDivisionServiceID SERVICE_ID = AA14OrgDivisionServiceID.forId("EJGV_SEGURIDAD_TRAFIKOA");
	              
	public static final AA14OrgDivisionServiceLocationID LOC_BILBAO_ID = AA14OrgDivisionServiceLocationID.forId("EJGV_SEGURIDAD_TRAFIKOA_BILBAO");
	public static final AA14ScheduleID SCH_BILBAO_ID = AA14ScheduleID.forId("SCH_EJGV_SEGURIDAD_TRAFIKOA_BILBAO");
	               
	public static final AA14OrgDivisionServiceLocationID LOC_BILBAO_CATIT_ID = AA14OrgDivisionServiceLocationID.forId("EJGV_SEGURIDAD_TRAFIKOA_BILBAO_CATIT");
	public static final AA14ScheduleID SCH_BILBAO_CATIT_ID = AA14ScheduleID.forId("SCH_EJGV_SEGURIDAD_TRAFIKOA_BILBAO_CATIT");
	               
	public static final AA14OrgDivisionServiceLocationID LOC_DONOSTIA_ID = AA14OrgDivisionServiceLocationID.forId("EJGV_SEGURIDAD_TRAFIKOA_DONOSTIA");
	public static final AA14ScheduleID SCH_DONOSTIA_ID = AA14ScheduleID.forId("SCH_EJGV_SEGURIDAD_TRAFIKOA_DONOSTIA");
	               
	public static final AA14OrgDivisionServiceLocationID LOC_GASTEIZ_ID = AA14OrgDivisionServiceLocationID.forId("EJGV_SEGURIDAD_TRAFIKOA_GASTEIZ");
	public static final AA14ScheduleID SCH_GASTEIZ_ID = AA14ScheduleID.forId("SCH_EJGV_SEGURIDAD_TRAFIKOA_GASTEIZ");
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14BusinessConfigForTrafikoa(final AA14BusinessConfig wrapped) {
		super(wrapped);
	}
	public static AA14BusinessConfigForTrafikoa wrap(final AA14BusinessConfig wrapped) {
		return new AA14BusinessConfigForTrafikoa(wrapped);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GETTERS
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrgDivisionService getTrafikoaService() {
		return _wrappedConfig.getServiceFor(SERVICE_ID);
	}
	public AA14OrgDivisionServiceLocation getBilbaoLocation() {
		return _wrappedConfig.getLocationFor(LOC_BILBAO_ID);
	}
	public AA14OrgDivisionServiceLocation getBilbaoCATITLocation() {
		return _wrappedConfig.getLocationFor(LOC_BILBAO_CATIT_ID);
	}
	public AA14OrgDivisionServiceLocation getGasteizLocation() {
		return _wrappedConfig.getLocationFor(LOC_GASTEIZ_ID);
	}
	public AA14OrgDivisionServiceLocation getDonostiaLocation() {
		return _wrappedConfig.getLocationFor(LOC_DONOSTIA_ID);
	}
}
