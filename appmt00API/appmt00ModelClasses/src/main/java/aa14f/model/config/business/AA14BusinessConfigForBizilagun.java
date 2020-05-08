package aa14f.model.config.business;

import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;

public class AA14BusinessConfigForBizilagun
	 extends A14BusinessConfigWrappedBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final AA14OrganizationID ORG_ID = AA14OrganizationID.forId("ORG_BIZILAGUN");
	
	// division & service
	public static final AA14OrgDivisionID BIZILAGUN_ID = AA14OrgDivisionID.forId("EJGV_BIZILAGUN");
	public static final AA14OrgDivisionServiceID SERVICE_FIANZAS_DEPOSITO_CONTRATOS_ID = AA14OrgDivisionServiceID.forId("BIZILAGUN_FIANZAS_DEPOSITO_CONTRATOS");
	public static final AA14OrgDivisionServiceID SERVICE_COMUNIDADES_ALQUILERES_ID = AA14OrgDivisionServiceID.forId("BIZILAGUN_COMUNIDADES_ALQUILERES");
	
	// locations & schedules
	public static final AA14OrgDivisionServiceLocationID LOC_FIANZAS_DEPOSITO_CONTRATOS_AR_ID = AA14OrgDivisionServiceLocationID.forId("BIZILAGUN_FIANZAS_DEPOSITO_CONTRATOS_AR");
	public static final AA14ScheduleID SCH_FIANZAS_DEPOSITO_CONTRATOS_AR_ID = AA14ScheduleID.forId("SCH_BIZILAGUN_FIANZAS_DEPOSITO_CONTRATOS_AR");

	public static final AA14OrgDivisionServiceLocationID LOC_FIANZAS_DEPOSITO_CONTRATOS_GI_ID = AA14OrgDivisionServiceLocationID.forId("BIZILAGUN_FIANZAS_DEPOSITO_CONTRATOS_GI");
	public static final AA14ScheduleID SCH_FIANZAS_DEPOSITO_CONTRATOS_GI_ID = AA14ScheduleID.forId("SCH_BIZILAGUN_FIANZAS_DEPOSITO_CONTRATOS_GI");
	
	public static final AA14OrgDivisionServiceLocationID LOC_FIANZAS_DEPOSITO_CONTRATOS_BIZ_ID = AA14OrgDivisionServiceLocationID.forId("BIZILAGUN_FIANZAS_DEPOSITO_CONTRATOS_BIZ");
	public static final AA14ScheduleID SCH_FIANZAS_DEPOSITO_CONTRATOS_BIZ_ID = AA14ScheduleID.forId("SCH_BIZILAGUN_FIANZAS_DEPOSITO_CONTRATOS_BIZ");	
	
	
	
	public static final AA14OrgDivisionServiceLocationID LOC_COMUNIDADES_ALQUILERES_AR_ID = AA14OrgDivisionServiceLocationID.forId("BIZILAGUN_COMUNIDADES_ALQUILERES_AR");
	public static final AA14ScheduleID SCH_COMUNIDADES_ALQUILERES_AR_ID = AA14ScheduleID.forId("SCH_BIZILAGUN_COMUNIDADES_ALQUILERES_AR");
	
	public static final AA14OrgDivisionServiceLocationID LOC_COMUNIDADES_ALQUILERES_GI_ID = AA14OrgDivisionServiceLocationID.forId("BIZILAGUN_COMUNIDADES_ALQUILERES_GI");
	public static final AA14ScheduleID SCH_COMUNIDADES_ALQUILERES_GI_ID = AA14ScheduleID.forId("SCH_BIZILAGUN_COMUNIDADES_ALQUILERES_GI");
	
	public static final AA14OrgDivisionServiceLocationID LOC_COMUNIDADES_ALQUILERES_BIZ_ID = AA14OrgDivisionServiceLocationID.forId("BIZILAGUN_COMUNIDADES_ALQUILERES_BIZ");
	public static final AA14ScheduleID SCH_COMUNIDADES_ALQUILERES_BIZ_ID = AA14ScheduleID.forId("SCH_BIZILAGUN_COMUNIDADES_ALQUILERES_BIZ");
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14BusinessConfigForBizilagun(final AA14BusinessConfig wrapped) {
		super(wrapped);
	}
	public static AA14BusinessConfigForBizilagun wrap(final AA14BusinessConfig wrapped) {
		return new AA14BusinessConfigForBizilagun(wrapped);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrgDivisionService getFianzasDepositosContratosService() {
		return _wrappedConfig.getServiceFor(SERVICE_FIANZAS_DEPOSITO_CONTRATOS_ID);
	}
	public AA14OrgDivisionService getComunidadesAlquileresService() {
		return _wrappedConfig.getServiceFor(SERVICE_COMUNIDADES_ALQUILERES_ID);
	}
	public AA14OrgDivisionServiceLocation getFianzasDepositosContratosArabaLocation() {
		return _wrappedConfig.getLocationFor(LOC_FIANZAS_DEPOSITO_CONTRATOS_AR_ID);
	}
	public AA14OrgDivisionServiceLocation getComunidadesAlquileresLocationAraba() {
		return _wrappedConfig.getLocationFor(LOC_COMUNIDADES_ALQUILERES_AR_ID);
	}
	public AA14OrgDivisionServiceLocation getComunidadesAlquileresLocationGipuzkoa() {
		return _wrappedConfig.getLocationFor(LOC_COMUNIDADES_ALQUILERES_GI_ID);
	}
	public AA14OrgDivisionServiceLocation getComunidadesAlquileresLocationBizkaia() {
		return _wrappedConfig.getLocationFor(LOC_COMUNIDADES_ALQUILERES_BIZ_ID);
	}
}
