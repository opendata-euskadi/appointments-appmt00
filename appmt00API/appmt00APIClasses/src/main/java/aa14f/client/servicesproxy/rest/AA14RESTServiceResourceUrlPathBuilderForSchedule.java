package aa14f.client.servicesproxy.rest;

import aa14f.client.servicesproxy.rest.AA14RESTServiceResourceUrlPathBuilderBases.AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import r01f.bootstrap.services.config.client.ServicesCoreModuleExpositionAsRESTServices;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceEndPointUrl;
import r01f.types.url.UrlPath;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

  class AA14RESTServiceResourceUrlPathBuilderForSchedule
extends AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase<AA14ScheduleOID,AA14ScheduleID> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14RESTServiceResourceUrlPathBuilderForSchedule(final XMLPropertiesForAppComponent clientProps) {
		super(new RESTServiceEndPointUrl(clientProps,
										 "persistence"),
			  UrlPath.from("schedules"));
	}
	public AA14RESTServiceResourceUrlPathBuilderForSchedule(final ServicesCoreModuleExpositionAsRESTServices coreModuleRESTExposition) {
		super(new RESTServiceEndPointUrl(coreModuleRESTExposition),
			  UrlPath.from("schedules"));
	}
}