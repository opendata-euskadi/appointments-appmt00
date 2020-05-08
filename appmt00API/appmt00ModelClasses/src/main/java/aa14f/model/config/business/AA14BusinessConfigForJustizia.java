package aa14f.model.config.business;

import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;

public class AA14BusinessConfigForJustizia
	 extends A14BusinessConfigWrappedBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final AA14OrganizationID ORG_ID = AA14OrganizationID.forId("ORG_JUSTIZIA");
	
	// division & service
	public static final AA14OrgDivisionID JUSTIZIA_ID = AA14OrgDivisionID.forId("EJGV_JUSTIZIA");
	public static final AA14OrgDivisionServiceID SERVICE_ID = AA14OrgDivisionServiceID.forId("JUSTIZIA_REGISTRO_REPARTO");
	
	// locations & schedules
	public static final AA14OrgDivisionServiceLocationID LOC_JUSTIZIA_REGISTRO_REPARTO_BIL_ID = AA14OrgDivisionServiceLocationID.forId("JUSTIZIA_REGISTRO_REPARTO_BIL");
	public static final AA14ScheduleID SCH_JUSTIZIA_REGISTRO_REPARTO_BIL_ID = AA14ScheduleID.forId("JUSTIZIA_REGISTRO_REPARTO_BIL");
	
	public static final AA14OrgDivisionServiceLocationID LOC_JUSTIZIA_REGISTRO_REPARTO_GTX_ID = AA14OrgDivisionServiceLocationID.forId("JUSTIZIA_REGISTRO_REPARTO_GTX");
	public static final AA14ScheduleID SCH_JUSTIZIA_REGISTRO_REPARTO_GTX_ID = AA14ScheduleID.forId("SCH_JUSTIZIA_REGISTRO_REPARTO_GTX");

	public static final AA14OrgDivisionServiceLocationID LOC_JUSTIZIA_REGISTRO_REPARTO_GER_ID = AA14OrgDivisionServiceLocationID.forId("JUSTIZIA_REGISTRO_REPARTO_GER");
	public static final AA14ScheduleID SCH_JUSTIZIA_REGISTRO_REPARTO_GER_ID = AA14ScheduleID.forId("SCH_JUSTIZIA_REGISTRO_REPARTO_GER");
	
	public static final AA14OrgDivisionServiceLocationID LOC_JUSTIZIA_REGISTRO_REPARTO_DUR_ID = AA14OrgDivisionServiceLocationID.forId("JUSTIZIA_REGISTRO_REPARTO_DUR");
	public static final AA14ScheduleID SCH_JUSTIZIA_REGISTRO_REPARTO_DUR_ID = AA14ScheduleID.forId("SCH_JUSTIZIA_REGISTRO_REPARTO_DUR");
	
	public static final AA14ScheduleID SCH_JUSTIZIA_REGISTRO_REPARTO_BAL_ID = AA14ScheduleID.forId("SCH_JUSTIZIA_REGISTRO_REPARTO_BAL");
	public static final AA14OrgDivisionServiceLocationID LOC_JUSTIZIA_REGISTRO_REPARTO_BAL_ID = AA14OrgDivisionServiceLocationID.forId("JUSTIZIA_REGISTRO_REPARTO_BAL");
	
	public static final AA14OrgDivisionServiceLocationID LOC_JUSTIZIA_REGISTRO_REPARTO_BK_ID = AA14OrgDivisionServiceLocationID.forId("JUSTIZIA_REGISTRO_REPARTO_BK");
	public static final AA14ScheduleID SCH_JUSTIZIA_REGISTRO_REPARTO_BK_ID = AA14ScheduleID.forId("SCH_JUSTIZIA_REGISTRO_REPARTO_BK");
	
	public static final AA14OrgDivisionServiceLocationID LOC_JUSTIZIA_REGISTRO_REPARTO_AM_ID = AA14OrgDivisionServiceLocationID.forId("JUSTIZIA_REGISTRO_REPARTO_AM");
	public static final AA14ScheduleID SCH_JUSTIZIA_REGISTRO_REPARTO_AM_ID = AA14ScheduleID.forId("SCH_JUSTIZIA_REGISTRO_REPARTO_AM");
	
	public static final AA14OrgDivisionServiceLocationID LOC_JUSTIZIA_REGISTRO_REPARTO_VI_ID = AA14OrgDivisionServiceLocationID.forId("JUSTIZIA_REGISTRO_REPARTO_VI");
	public static final AA14ScheduleID SCH_JUSTIZIA_REGISTRO_REPARTO_VI_ID = AA14ScheduleID.forId("SCH_JUSTIZIA_REGISTRO_REPARTO_VI");
	
	public static final AA14OrgDivisionServiceLocationID LOC_JUSTIZIA_REGISTRO_REPARTO_AZ_ID = AA14OrgDivisionServiceLocationID.forId("JUSTIZIA_REGISTRO_REPARTO_AZ");
	public static final AA14ScheduleID SCH_JUSTIZIA_REGISTRO_REPARTO_AZ_ID = AA14ScheduleID.forId("SCH_JUSTIZIA_REGISTRO_REPARTO_AZ");
	
	public static final AA14OrgDivisionServiceLocationID LOC_JUSTIZIA_REGISTRO_REPARTO_BER_ID = AA14OrgDivisionServiceLocationID.forId("JUSTIZIA_REGISTRO_REPARTO_BER");
	public static final AA14ScheduleID SCH_JUSTIZIA_REGISTRO_REPARTO_BER_ID = AA14ScheduleID.forId("SCH_JUSTIZIA_REGISTRO_REPARTO_BER");
	
	public static final AA14OrgDivisionServiceLocationID LOC_JUSTIZIA_REGISTRO_REPARTO_IRU_ID = AA14OrgDivisionServiceLocationID.forId("JUSTIZIA_REGISTRO_REPARTO_IRU");
	public static final AA14ScheduleID SCH_JUSTIZIA_REGISTRO_REPARTO_IRU_ID = AA14ScheduleID.forId("SCH_JUSTIZIA_REGISTRO_REPARTO_IRU");
	
	public static final AA14OrgDivisionServiceLocationID LOC_JUSTIZIA_REGISTRO_REPARTO_EIB_ID = AA14OrgDivisionServiceLocationID.forId("JUSTIZIA_REGISTRO_REPARTO_EI");
	public static final AA14ScheduleID SCH_JUSTIZIA_REGISTRO_REPARTO_EIB_ID = AA14ScheduleID.forId("SCH_JUSTIZIA_REGISTRO_REPARTO_EI");
	
	public static final AA14OrgDivisionServiceLocationID LOC_JUSTIZIA_REGISTRO_REPARTO_TO_ID = AA14OrgDivisionServiceLocationID.forId("JUSTIZIA_REGISTRO_REPARTO_TO");
	public static final AA14ScheduleID SCH_JUSTIZIA_REGISTRO_REPARTO_TO_ID = AA14ScheduleID.forId("SCH_JUSTIZIA_REGISTRO_REPARTO_TO");
	
	public static final AA14OrgDivisionServiceLocationID LOC_JUSTIZIA_REGISTRO_REPARTO_DON_ID = AA14OrgDivisionServiceLocationID.forId("JUSTIZIA_REGISTRO_REPARTO_DON");
	public static final AA14ScheduleID SCH_JUSTIZIA_REGISTRO_REPARTO_DON_ID = AA14ScheduleID.forId("SCH_JUSTIZIA_REGISTRO_REPARTO_DON");
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14BusinessConfigForJustizia(final AA14BusinessConfig wrapped) {
		super(wrapped);
	}
	public static AA14BusinessConfigForJustizia wrap(final AA14BusinessConfig wrapped) {
		return new AA14BusinessConfigForJustizia(wrapped);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrgDivisionService getAtencionPublicoService() {
		return _wrappedConfig.getServiceFor(SERVICE_ID);
	}
	public AA14OrgDivisionServiceLocation getRegistrationAndDeliveryDonostiaLocation() {
		return _wrappedConfig.getLocationFor(LOC_JUSTIZIA_REGISTRO_REPARTO_DON_ID);
	}
	public AA14OrgDivisionServiceLocation getRegistrationAndDeliveryTolosaLocation() {
		return _wrappedConfig.getLocationFor(LOC_JUSTIZIA_REGISTRO_REPARTO_TO_ID);
	}
	public AA14OrgDivisionServiceLocation getRegistrationAndDeliveryEibarLocation() {
		return _wrappedConfig.getLocationFor(LOC_JUSTIZIA_REGISTRO_REPARTO_EIB_ID);
	}
	public AA14OrgDivisionServiceLocation getRegistrationAndDeliveryIrunLocation() {
		return _wrappedConfig.getLocationFor(LOC_JUSTIZIA_REGISTRO_REPARTO_IRU_ID);
	}
	public AA14OrgDivisionServiceLocation getRegistrationAndDeliveryBergaraLocation() {
		return _wrappedConfig.getLocationFor(LOC_JUSTIZIA_REGISTRO_REPARTO_BER_ID);
	}
	public AA14OrgDivisionServiceLocation getRegistrationAndDeliveryAzpeitiaLocation() {
		return _wrappedConfig.getLocationFor(LOC_JUSTIZIA_REGISTRO_REPARTO_AZ_ID);
	}
	public AA14OrgDivisionServiceLocation getRegistrationAndDeliveryVitoriaLocation() {
		return _wrappedConfig.getLocationFor(LOC_JUSTIZIA_REGISTRO_REPARTO_VI_ID);
	}
	public AA14OrgDivisionServiceLocation getRegistrationAndDeliveryAmurrioLocation() {
		return _wrappedConfig.getLocationFor(LOC_JUSTIZIA_REGISTRO_REPARTO_AM_ID);
	}
	public AA14OrgDivisionServiceLocation getRegistrationAndDeliveryBarakaldoLocation() {
		return _wrappedConfig.getLocationFor(LOC_JUSTIZIA_REGISTRO_REPARTO_BK_ID);
	}
	public AA14OrgDivisionServiceLocation getRegistrationAndDeliveryBalmasedaLocation() {
		return _wrappedConfig.getLocationFor(LOC_JUSTIZIA_REGISTRO_REPARTO_BAL_ID);
	}
	public AA14OrgDivisionServiceLocation getRegistrationAndDeliveryDurangoLocation() {
		return _wrappedConfig.getLocationFor(LOC_JUSTIZIA_REGISTRO_REPARTO_DUR_ID);
	}
	public AA14OrgDivisionServiceLocation getRegistrationAndDeliveryGernikaLumoLocation() {
		return _wrappedConfig.getLocationFor(LOC_JUSTIZIA_REGISTRO_REPARTO_GER_ID);
	}
	public AA14OrgDivisionServiceLocation getRegistrationAndDeliveryGetxoLocation() {
		return _wrappedConfig.getLocationFor(LOC_JUSTIZIA_REGISTRO_REPARTO_GTX_ID);
	}
	public AA14OrgDivisionServiceLocation getRegistrationAndDeliveryBilbaoLocation() {
		return _wrappedConfig.getLocationFor(LOC_JUSTIZIA_REGISTRO_REPARTO_BIL_ID);
	}
}
