package aa14f.client.servicesproxy.rest;

import aa14f.client.servicesproxy.rest.AA14RESTServiceResourceUrlPathBuilderBases.AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase;
import aa14f.common.internal.AA14AppCodes;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.oids.AA14OIDs.AA14AppointmentSubjectID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import r01f.bootstrap.services.config.client.ServicesCoreModuleExpositionAsRESTServices;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceEndPointUrl;
import r01f.types.contact.PersonID;
import r01f.types.url.UrlPath;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

  class AA14RESTServiceResourceUrlPathBuilderForBookedSlot
extends AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase<AA14SlotOID,AA14SlotID> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14RESTServiceResourceUrlPathBuilderForBookedSlot(final XMLPropertiesForAppComponent clientProps) {
		super(new RESTServiceEndPointUrl(clientProps,
										 "persistence"),
			  UrlPath.from(AA14AppCodes.APPOINTMENTS_MODULE_STR));
	}
	public AA14RESTServiceResourceUrlPathBuilderForBookedSlot(final ServicesCoreModuleExpositionAsRESTServices coreModuleRESTExposition) {
		super(new RESTServiceEndPointUrl(coreModuleRESTExposition),
			  UrlPath.from(AA14AppCodes.APPOINTMENTS_MODULE_STR));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlPath pathOfAppointmentsByLocation(final AA14OrgDivisionServiceLocationOID locationOid) {
		return this.pathOfEntityList().joinedWith("byLocation",locationOid);
	}
	public UrlPath pathOfSummariesByCustomerId(final PersonID personId) {
		return this.pathOfEntityList().joinedWith("byCustomerId",personId,
				   								  "summarized");
	}
	public UrlPath pathOfSummariesBySubjectId(final AA14AppointmentSubjectID subjectId) {
		return this.pathOfEntityList().joinedWith("bySubjectId",subjectId,
				   								  "summarized");
	}
	public UrlPath pathOfSummariesForServiceByCustomerId(final AA14OrgDivisionServiceID serviceId,
														 final PersonID personId) {
		return this.pathOfEntityList().joinedWith("service",serviceId,
				   								  "byCustomerId",personId,
				   								  "summarized");
	}
	public UrlPath pathOfSummariesBySubjectId(final AA14OrgDivisionServiceID serviceId,
											 final AA14AppointmentSubjectID subjectId) {
		return this.pathOfEntityList().joinedWith("service",serviceId,
				   								  "bySubjectId",subjectId,
				   								  "summarized");
	}
}