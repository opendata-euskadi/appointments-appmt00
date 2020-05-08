package aa14f.client.servicesproxy.rest;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import aa14f.api.interfaces.AA14CRUDServicesForSchedule;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.persistence.CRUDResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import r01f.xmlproperties.annotations.XMLPropertiesComponent;


@Singleton
public class AA14RESTCRUDServicesProxyForSchedule
	 extends AA14RESTCRUDServicesProxyBase<AA14ScheduleOID,AA14ScheduleID,AA14Schedule>
  implements AA14CRUDServicesForSchedule {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14RESTCRUDServicesProxyForSchedule(@XMLPropertiesComponent("client") final XMLPropertiesForAppComponent clientProps,
												@ModelObjectsMarshaller 		  final Marshaller marshaller) {
		super(marshaller,
			  AA14Schedule.class,
			  new AA14RESTServiceResourceUrlPathBuilderForSchedule(clientProps));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDResult<AA14Schedule> linkScheduleToServiceLocations(final SecurityContext securityContext,
																   final AA14ScheduleOID schOid,
																   final Collection<AA14OrgDivisionServiceLocationOID> locOids) {
		throw new UnsupportedOperationException("NOT implemented");
	}
}
