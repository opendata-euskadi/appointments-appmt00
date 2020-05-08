package aa14f.client.servicesproxy.rest;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import aa14f.api.interfaces.AA14CRUDServicesForOrgDivisionServiceLocation;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.persistence.CRUDResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import r01f.xmlproperties.annotations.XMLPropertiesComponent;


@Singleton
public class AA14RESTCRUDServicesProxyForOrgDivsionServiceLocation
	 extends AA14RESTCRUDServicesProxyForOrganizationalEntityBase<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID,AA14OrgDivisionServiceLocation>
  implements AA14CRUDServicesForOrgDivisionServiceLocation {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14RESTCRUDServicesProxyForOrgDivsionServiceLocation(@XMLPropertiesComponent("client") final XMLPropertiesForAppComponent clientProps,
												    			 @ModelObjectsMarshaller 		   final Marshaller marshaller) {
		super(marshaller,
			  AA14OrgDivisionServiceLocation.class,
			  new AA14RESTServiceResourceUrlPathBuilderForOrgDivisionServiceLocation(clientProps));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDResult<AA14OrgDivisionServiceLocation> linkLocationToSchedules(final SecurityContext securityContext,
																			  final AA14OrgDivisionServiceLocationOID locOid,
																			  final Collection<AA14ScheduleOID> schOids) {
		throw new UnsupportedOperationException("NOT implemented");
	}

}
