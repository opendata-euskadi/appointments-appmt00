package aa14f.client.servicesproxy.rest;

import javax.inject.Inject;
import javax.inject.Singleton;

import aa14f.api.interfaces.AA14CRUDServicesForOrganization;
import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import r01f.xmlproperties.annotations.XMLPropertiesComponent;


@Singleton
public class AA14RESTCRUDServicesProxyForOrganization
	 extends AA14RESTCRUDServicesProxyForOrganizationalEntityBase<AA14OrganizationOID,AA14OrganizationID,AA14Organization>
  implements AA14CRUDServicesForOrganization {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14RESTCRUDServicesProxyForOrganization(@XMLPropertiesComponent("client") final XMLPropertiesForAppComponent clientProps,
												    @ModelObjectsMarshaller 		  final Marshaller marshaller) {
		super(marshaller,
			  AA14Organization.class,
			  new AA14RESTServiceResourceUrlPathBuilderForOrganization(clientProps));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
}
