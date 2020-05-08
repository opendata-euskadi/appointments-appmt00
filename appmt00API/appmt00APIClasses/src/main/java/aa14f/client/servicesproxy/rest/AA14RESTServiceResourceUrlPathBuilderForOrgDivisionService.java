package aa14f.client.servicesproxy.rest;

import aa14f.client.servicesproxy.rest.AA14RESTServiceResourceUrlPathBuilderBases.AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import r01f.bootstrap.services.config.client.ServicesCoreModuleExpositionAsRESTServices;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceEndPointUrl;
import r01f.types.url.UrlPath;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

  class AA14RESTServiceResourceUrlPathBuilderForOrgDivisionService
extends AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14RESTServiceResourceUrlPathBuilderForOrgDivisionService(final XMLPropertiesForAppComponent clientProps) {
		super(new RESTServiceEndPointUrl(clientProps,
										 "persistence"),
			  UrlPath.from("services"));
	}
	public AA14RESTServiceResourceUrlPathBuilderForOrgDivisionService(final ServicesCoreModuleExpositionAsRESTServices coreModuleRESTExposition) {
		super(new RESTServiceEndPointUrl(coreModuleRESTExposition),
			  UrlPath.from("services"));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlPath pathOfEntityListByOrgDivision(final AA14OrgDivisionOID divisionOid) {
		return this.pathOfEntityList().joinedWith("byDivision",divisionOid);
	}
	public UrlPath pathOfSummariesByOrgDivision(final AA14OrgDivisionOID divisionOid) {
		return this.pathOfEntityListByOrgDivision(divisionOid).joinedWith("summarized");
	}
}