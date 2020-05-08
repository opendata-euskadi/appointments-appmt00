package aa14f.client.servicesproxy.rest;

import javax.inject.Inject;
import javax.inject.Singleton;

import aa14f.api.interfaces.AA14FindServicesForOrgDivision;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import r01f.locale.Language;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindSummariesResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.types.url.Url;
import r01f.types.url.UrlQueryString;
import r01f.types.url.UrlQueryStringParam;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import r01f.xmlproperties.annotations.XMLPropertiesComponent;


@Singleton
public class AA14RESTFindServicesProxyForOrgDivision
	 extends AA14RESTFindServicesProxyForOrganizationalEntityBase<AA14OrgDivisionOID,AA14OrgDivisionID,AA14OrgDivision>
  implements AA14FindServicesForOrgDivision {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14RESTFindServicesProxyForOrgDivision(@XMLPropertiesComponent("client") final XMLPropertiesForAppComponent clientProps,
												   @ModelObjectsMarshaller 		     final Marshaller marshaller) {
		super(marshaller,
			  AA14OrgDivision.class,
			  new AA14RESTServiceResourceUrlPathBuilderForOrgDivision(clientProps));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14OrgDivision> findByOrganization(final SecurityContext securityContext,
											  		   	  final AA14OrganizationOID orgOid) {
		Url restResourceUrl = this.composeURIFor(this.getServicesRESTResourceUrlPathBuilderAs(AA14RESTServiceResourceUrlPathBuilderForOrgDivision.class)
			  															  .pathOfEntityListByOrganization(orgOid));
		return _findDelegate.doFindEntities(securityContext,
											restResourceUrl);
	}
	@Override
	public FindSummariesResult<AA14OrgDivision> findSummariesByOrganization(final SecurityContext securityContext,
																		 	final AA14OrganizationOID orgOid,
																		 	final Language lang) {
		Url restResourceUrl = this.composeURIFor(this.getServicesRESTResourceUrlPathBuilderAs(AA14RESTServiceResourceUrlPathBuilderForOrgDivision.class)
			  													  .pathOfSummariesByOrganization(orgOid),
			  									 UrlQueryString.fromParams(UrlQueryStringParam.of(lang)));
		return _findDelegate.doFindSummaries(securityContext,
											 restResourceUrl);
	}

}
