package aa14f.client.servicesproxy.rest;

import aa14f.client.servicesproxy.rest.AA14RESTServiceResourceUrlPathBuilderBases.AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import r01f.bootstrap.services.config.client.ServicesCoreModuleExpositionAsRESTServices;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceEndPointUrl;
import r01f.types.url.UrlPath;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

  class AA14RESTServiceResourceUrlPathBuilderForOrganization
extends AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase<AA14OrganizationOID,AA14OrganizationID> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14RESTServiceResourceUrlPathBuilderForOrganization(final XMLPropertiesForAppComponent clientProps) {
		super(new RESTServiceEndPointUrl(clientProps,
										 "persistence"),
			  UrlPath.from("organizations"));
	}
	public AA14RESTServiceResourceUrlPathBuilderForOrganization(final ServicesCoreModuleExpositionAsRESTServices coreModuleRESTExposition) {
		super(new RESTServiceEndPointUrl(coreModuleRESTExposition),
			  UrlPath.from("organizations"));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlPath pathOfSummaries() {
		return this.pathOfEntityList().joinedWith("summarized");
	}
}