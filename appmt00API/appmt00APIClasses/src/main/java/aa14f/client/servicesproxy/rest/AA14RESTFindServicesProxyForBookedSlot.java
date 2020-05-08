package aa14f.client.servicesproxy.rest;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import aa14f.api.interfaces.AA14FindServicesForBookedSlot;
import aa14f.model.AA14Appointment;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14BookedSlotType;
import aa14f.model.AA14NonBookableSlot;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14PeriodicSlotSerieOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import aa14f.model.search.AA14AppointmentFilter;
import r01f.locale.Language;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.persistence.FindOIDsResult;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindSummariesResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.types.Range;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import r01f.xmlproperties.annotations.XMLPropertiesComponent;


@Singleton
public class AA14RESTFindServicesProxyForBookedSlot
	 extends AA14RESTFindServicesProxyBase<AA14SlotOID,AA14SlotID,AA14BookedSlot> 
  implements AA14FindServicesForBookedSlot {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14RESTFindServicesProxyForBookedSlot(@XMLPropertiesComponent("client") final XMLPropertiesForAppComponent clientProps,
												  @ModelObjectsMarshaller 			final Marshaller marshaller) {
		super(marshaller,
			  AA14BookedSlot.class,
			  new AA14RESTServiceResourceUrlPathBuilderForBookedSlot(clientProps));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BY SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14BookedSlot> findRangeBookedSlotsFor(final SecurityContext securityContext,
										   					  final AA14ScheduleOID schOid,
										   					  final Range<Date> dateRange) {
		throw new UnsupportedOperationException("Not yet implemented!");			// TODO finish!
	}
	@Override
	public FindSummariesResult<AA14BookedSlot> findRangeBookedSlotsSummarizedFor(final SecurityContext securityContext,
																				 final Language lang,
																				 final AA14ScheduleOID schOid,
																				 final Range<Date> dateRange) {
		throw new UnsupportedOperationException("Not yet implemented!");			// TODO finish!
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BY LOCATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindOIDsResult<AA14SlotOID> findRangeBookedSlotsFor(final SecurityContext securityContext,
															   final AA14OrgDivisionServiceLocationOID locationOid,
										   					   final Range<Date> dateRange,
										   					   final AA14BookedSlotType slotType) {
		throw new UnsupportedOperationException("Not yet implemented!");			// TODO finish!
	}
	@Override
	public FindResult<AA14BookedSlot> findRangeBookedSlotsFor(final SecurityContext securityContext,
										   					  final AA14OrgDivisionServiceLocationOID locationOid,
										   					  final Range<Date> dateRange) {
		throw new UnsupportedOperationException("Not yet implemented!");			// TODO finish!
	}
	@Override
	public FindSummariesResult<AA14BookedSlot> findRangeBookedSlotsSummarizedFor(final SecurityContext securityContext,
																				 final Language lang,
																				 final AA14OrgDivisionServiceLocationOID locationOid,
																				 final Range<Date> dateRange) {
		throw new UnsupportedOperationException("Not yet implemented!");			// TODO finish!
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BY LOCATION & SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14BookedSlot> findRangeBookedSlotsFor(final SecurityContext securityContext,
										   					  final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
										   					  final Range<Date> dateRange) {
		throw new UnsupportedOperationException("Not yet implemented!");			// TODO finish!
	}
	@Override
	public FindSummariesResult<AA14BookedSlot> findRangeBookedSlotsSummarizedFor(final SecurityContext securityContext,
																				 final Language lang,
																				 final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
																				 final Range<Date> dateRange) {
		throw new UnsupportedOperationException("Not yet implemented!");			// TODO finish!
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14BookedSlot> findBookedSlotsOverlappingRange(final SecurityContext securityContext,
																	  final AA14ScheduleOID schOid,
																	  final Range<Date> dateRange) {
		throw new UnsupportedOperationException("Not yet implemented!");			// TODO finish!		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public FindSummariesResult<AA14Appointment> findAppointmentsBy(final SecurityContext securityContext,
																   final AA14AppointmentFilter filter,
																   final Language lang) {
		throw new UnsupportedOperationException("Not yet implemented!");			// TODO finish!
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindOIDsResult<AA14SlotOID> findNonBookablePeriodicSlotsOids(final SecurityContext securityContext,
																	    final AA14PeriodicSlotSerieOID serieOid) {
		throw new UnsupportedOperationException("Not yet implemented!");			// TODO finish!
	}
	@Override
	public FindResult<AA14NonBookableSlot> findNonBookablePeriodicSlots(final SecurityContext securityContext,
																	    final AA14PeriodicSlotSerieOID serieOid) {
		throw new UnsupportedOperationException("Not yet implemented!");			// TODO finish!		
	}
}
