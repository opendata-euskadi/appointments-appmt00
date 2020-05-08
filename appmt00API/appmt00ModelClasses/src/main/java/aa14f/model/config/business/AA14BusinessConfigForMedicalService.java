package aa14f.model.config.business;

import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import r01f.guids.CommonOIDs.BuildingCode;

public class AA14BusinessConfigForMedicalService 
	 extends A14BusinessConfigWrappedBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final AA14OrganizationID ORG_ID = AA14OrganizationID.forId("ORG_MEDICAL_SERVICE");
	
	// division & service
	public static final AA14OrgDivisionID MEDICAL_SERVICE_ID = AA14OrgDivisionID.forId("EJGV_SERVICIO_MEDICO"); //Dpto: Función pública
	public static final AA14OrgDivisionServiceID SERVICE_DOCTOR_VISIT_ID = AA14OrgDivisionServiceID.forId("CONSULTA_MEDICA");
	public static final AA14OrgDivisionServiceID SERVICE_NURSE_VISIT_ID = AA14OrgDivisionServiceID.forId("CONSULTA_ENFERMERIA");
	
	// locations & schedules
	public static final AA14OrgDivisionServiceLocationID LOC_DOCTOR_VISIT_AR_ID = AA14OrgDivisionServiceLocationID.forId("CONSULTA_MEDICA_AR");
	public static final AA14ScheduleID SCH_DOCTOR_VISIT_AR_ID = AA14ScheduleID.forId("SCH_CONSULTA_MEDICA_AR");
		
	public static final AA14OrgDivisionServiceLocationID LOC_DOCTOR_VISIT_GI_ID = AA14OrgDivisionServiceLocationID.forId("CONSULTA_MEDICA_GI");
	public static final AA14ScheduleID SCH_DOCTOR_VISIT_GI_ID = AA14ScheduleID.forId("SCH_CONSULTA_MEDICA_GI");
	
	public static final AA14OrgDivisionServiceLocationID LOC_DOCTOR_VISIT_BIZ_ID = AA14OrgDivisionServiceLocationID.forId("CONSULTA_MEDICA_BIZ");
	public static final AA14ScheduleID SCH_DOCTOR_VISIT_BIZ_ID = AA14ScheduleID.forId("SCH_CONSULTA_MEDICA_BIZ");
	
	public static final AA14OrgDivisionServiceLocationID LOC_NURSE_VISIT_AR_ID = AA14OrgDivisionServiceLocationID.forId("CONSULTA_ENFERMERIA_AR");
	public static final AA14ScheduleID SCH_NURSE_VISIT_AR_ID = AA14ScheduleID.forId("SCH_CONSULTA_ENFERMERIA_AR");
		
	public static final AA14OrgDivisionServiceLocationID LOC_NURSE_VISIT_GI_ID = AA14OrgDivisionServiceLocationID.forId("CONSULTA_ENFERMERIA_GI");
	public static final AA14ScheduleID SCH_NURSE_VISIT_GI_ID = AA14ScheduleID.forId("SCH_CONSULTA_ENFERMERIA_GI");
	
	public static final AA14OrgDivisionServiceLocationID LOC_NURSE_VISIT_BIZ_ID = AA14OrgDivisionServiceLocationID.forId("CONSULTA_ENFERMERIA_BIZ");
	public static final AA14ScheduleID SCH_NURSE_VISIT_BIZ_ID = AA14ScheduleID.forId("SCH_CONSULTA_ENFERMERIA_BIZ");
	
	// Users from this building can select any location
	public static final BuildingCode CENTRO_EDUCATIVO_ZUMARRAGA_BUILDING_CODE = BuildingCode.forId("005357");
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14BusinessConfigForMedicalService(final AA14BusinessConfig wrapped) {
		super(wrapped);
	}
	public static AA14BusinessConfigForMedicalService wrap(final AA14BusinessConfig wrapped) {
		return new AA14BusinessConfigForMedicalService(wrapped);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrgDivisionService getDoctorVisitService() {
		return _wrappedConfig.getServiceFor(SERVICE_DOCTOR_VISIT_ID);
	}
	public AA14OrgDivisionService getNurseVisitService() {
		return _wrappedConfig.getServiceFor(SERVICE_NURSE_VISIT_ID);
	}
	public AA14OrgDivisionServiceLocation getDoctorVisitArabaLocation() {
		return _wrappedConfig.getLocationFor(LOC_DOCTOR_VISIT_AR_ID);
	}
	public AA14OrgDivisionServiceLocation getDoctorVisitGipuzkoaLocation() {
		return _wrappedConfig.getLocationFor(LOC_DOCTOR_VISIT_GI_ID);
	}
	public AA14OrgDivisionServiceLocation getDoctorVisitBizkaiaLocation() {
		return _wrappedConfig.getLocationFor(LOC_DOCTOR_VISIT_BIZ_ID);
	}
	public AA14OrgDivisionServiceLocation getNurseVisitArabaLocation() {
		return _wrappedConfig.getLocationFor(LOC_NURSE_VISIT_AR_ID);
	}
	public AA14OrgDivisionServiceLocation getNurseVisitGipuzkoaLocation() {
		return _wrappedConfig.getLocationFor(LOC_NURSE_VISIT_GI_ID);
	}
	public AA14OrgDivisionServiceLocation getNurseVisitBizkaiaLocation() {
		return _wrappedConfig.getLocationFor(LOC_NURSE_VISIT_BIZ_ID);
	}
}
