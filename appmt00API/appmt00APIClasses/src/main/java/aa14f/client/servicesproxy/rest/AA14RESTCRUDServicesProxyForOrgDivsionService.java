package aa14f.client.servicesproxy.rest;

import javax.inject.Inject;
import javax.inject.Singleton;

import aa14f.api.interfaces.AA14CRUDServicesForOrgDivisionService;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import r01f.xmlproperties.annotations.XMLPropertiesComponent;


@Singleton
public class AA14RESTCRUDServicesProxyForOrgDivsionService
	 extends AA14RESTCRUDServicesProxyForOrganizationalEntityBase<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID,AA14OrgDivisionService>
  implements AA14CRUDServicesForOrgDivisionService {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14RESTCRUDServicesProxyForOrgDivsionService(@XMLPropertiesComponent("client") final XMLPropertiesForAppComponent clientProps,
												    	 @ModelObjectsMarshaller 		   final Marshaller marshaller) {
		super(marshaller,
			  AA14OrgDivisionService.class,
			  new AA14RESTServiceResourceUrlPathBuilderForOrgDivisionService(clientProps));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////

}
