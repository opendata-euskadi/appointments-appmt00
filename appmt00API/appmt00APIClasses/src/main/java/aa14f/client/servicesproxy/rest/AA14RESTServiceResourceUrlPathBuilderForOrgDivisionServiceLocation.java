package aa14f.client.servicesproxy.rest;

import aa14f.client.servicesproxy.rest.AA14RESTServiceResourceUrlPathBuilderBases.AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import r01f.bootstrap.services.config.client.ServicesCoreModuleExpositionAsRESTServices;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceEndPointUrl;
import r01f.types.url.UrlPath;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

  class AA14RESTServiceResourceUrlPathBuilderForOrgDivisionServiceLocation
extends AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14RESTServiceResourceUrlPathBuilderForOrgDivisionServiceLocation(final XMLPropertiesForAppComponent clientProps) {
		super(new RESTServiceEndPointUrl(clientProps,
										 "persistence"),
			  UrlPath.from("locations"));
	}
	public AA14RESTServiceResourceUrlPathBuilderForOrgDivisionServiceLocation(final ServicesCoreModuleExpositionAsRESTServices coreModuleRESTExposition) {
		super(new RESTServiceEndPointUrl(coreModuleRESTExposition),
			  UrlPath.from("locations"));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlPath pathOfEntityListByOrgDivisionService(final AA14OrgDivisionServiceOID serviceOid) {
		return this.pathOfEntityList().joinedWith("byService",serviceOid);
	}
	public UrlPath pathOfSummariesByOrgDivisionService(final AA14OrgDivisionServiceOID serviceOid) {
		return this.pathOfEntityListByOrgDivisionService(serviceOid).joinedWith("summarized");
	}
}