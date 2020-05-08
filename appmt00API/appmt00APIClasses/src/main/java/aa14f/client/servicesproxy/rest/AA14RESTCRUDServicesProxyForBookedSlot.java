package aa14f.client.servicesproxy.rest;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.joda.time.DateTime;

import aa14f.api.interfaces.AA14CRUDServicesForBookedSlot;
import aa14f.model.AA14BookedSlot;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.oids.AA14OIDs.AA14PeriodicSlotSerieOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.persistence.CRUDOnMultipleResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import r01f.xmlproperties.annotations.XMLPropertiesComponent;


@Singleton
public class AA14RESTCRUDServicesProxyForBookedSlot
	 extends AA14RESTCRUDServicesProxyBase<AA14SlotOID,AA14SlotID,AA14BookedSlot>
  implements AA14CRUDServicesForBookedSlot {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14RESTCRUDServicesProxyForBookedSlot(@XMLPropertiesComponent("client") final XMLPropertiesForAppComponent clientProps,
												  @ModelObjectsMarshaller 		    final Marshaller marshaller) {
		super(marshaller,
			  AA14BookedSlot.class,
			  new AA14RESTServiceResourceUrlPathBuilderForBookedSlot(clientProps));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDOnMultipleResult<AA14BookedSlot> createPeriodicNonBookableSlots(final SecurityContext securityContext,
											  								   final AA14ScheduleOID schOid,
											  								   final Date startDate,final Date endDate,
											  								   final DateTime timeStartNonBookable,final DateTime timeEndNonBookable,
											  								   final boolean sunday,final boolean monday,final boolean tuesday,final boolean wednesday,final boolean thursday,final boolean friday,final boolean saturday,
											  								   final String nonBookableSubject,
											  								   final UserCode userCode) {
		
		throw new UnsupportedOperationException();
	}
	@Override
	public CRUDOnMultipleResult<AA14BookedSlot> deletePeriodicNonBookableSlots(final SecurityContext securityContext,
																			   final AA14PeriodicSlotSerieOID serieOid) {
		throw new UnsupportedOperationException();
	}
}
