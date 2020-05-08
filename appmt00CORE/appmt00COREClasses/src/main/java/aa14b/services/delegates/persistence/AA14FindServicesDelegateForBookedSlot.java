package aa14b.services.delegates.persistence;

import java.util.Date;

import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14b.db.find.AA14DBFindForBookedSlot;
import aa14b.services.internal.AA14BookedSlotSummarizerService;
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
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.locale.Language;
import r01f.model.persistence.FindOIDsResult;
import r01f.model.persistence.FindOIDsResultBuilder;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindResultBuilder;
import r01f.model.persistence.FindSummariesResult;
import r01f.model.persistence.FindSummariesResultBuilder;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfigBuilder;
import r01f.securitycontext.SecurityContext;
import r01f.types.Range;

/**
 * Service layer delegated type for CRUD (Create/Read/Update/Delete) operations
 */
public class AA14FindServicesDelegateForBookedSlot
	 extends AA14FindServicesDelegateBase<AA14SlotOID,AA14SlotID,AA14BookedSlot>
  implements AA14FindServicesForBookedSlot {

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14FindServicesDelegateForBookedSlot(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
												 final EntityManager entityManager,
										 		 final Marshaller marshaller,
  			   		   	   	      		 		 final EventBus eventBus,
  			   		   	   	      		 		 final AA14BookedSlotSummarizerService slotSummarizerService) {
		super(coreCfg,
			  AA14BookedSlot.class,
			  new AA14DBFindForBookedSlot(DBModuleConfigBuilder.dbModuleConfigFrom(coreCfg),
					  					  entityManager,
					  					  marshaller,
					  				 	  slotSummarizerService));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BY SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14BookedSlot> findRangeBookedSlotsFor(final SecurityContext securityContext,
															  final AA14ScheduleOID schOid,
															  final Range<Date> dateRange) {
		if (schOid == null) return FindResultBuilder.using(securityContext)
													.on(_modelObjectType)
													.errorFindingEntities()
															.causedByClientBadRequest("The location id is mandatory to find appointments");
		return this.getServiceImplAs(AA14FindServicesForBookedSlot.class)
						.findRangeBookedSlotsFor(securityContext,
										   		 schOid,
										   		 dateRange);
	}
	@Override
	public FindSummariesResult<AA14BookedSlot> findRangeBookedSlotsSummarizedFor(final SecurityContext securityContext,
																				 final Language lang,
																  				 final AA14ScheduleOID schOid,
																  				 final Range<Date> dateRange) {
		if (schOid == null) return FindSummariesResultBuilder.using(securityContext)
																	 .on(_modelObjectType)
																	 .errorFindingSummaries()
																				.causedByClientBadRequest("The location id is mandatory to find appointments");
		return this.getServiceImplAs(AA14FindServicesForBookedSlot.class)
						.findRangeBookedSlotsSummarizedFor(securityContext,
														   lang,
										         		   schOid,
										         		   dateRange);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BY LOCATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindOIDsResult<AA14SlotOID> findRangeBookedSlotsFor(final SecurityContext securityContext,
															   final AA14OrgDivisionServiceLocationOID locationOid,
										   					   final Range<Date> dateRange,
										   					   final AA14BookedSlotType slotType) {
		if (locationOid == null) return FindOIDsResultBuilder.using(securityContext)
													.on(_modelObjectType)
													.errorFindingOids()
															.causedByClientBadRequest("The location id is mandatory to find appointments");
		return this.getServiceImplAs(AA14FindServicesForBookedSlot.class)
						.findRangeBookedSlotsFor(securityContext,
										   		 locationOid,
										   		 dateRange,
										   		 slotType);		
	}
	@Override
	public FindResult<AA14BookedSlot> findRangeBookedSlotsFor(final SecurityContext securityContext,
															  final AA14OrgDivisionServiceLocationOID locationOid,
															  final Range<Date> dateRange) {
		if (locationOid == null) return FindResultBuilder.using(securityContext)
													.on(_modelObjectType)
													.errorFindingEntities()
															.causedByClientBadRequest("The location id is mandatory to find appointments");
		return this.getServiceImplAs(AA14FindServicesForBookedSlot.class)
						.findRangeBookedSlotsFor(securityContext,
										   		 locationOid,
										   		 dateRange);
	}
	@Override
	public FindSummariesResult<AA14BookedSlot> findRangeBookedSlotsSummarizedFor(final SecurityContext securityContext,
																				 final Language lang,
																  				 final AA14OrgDivisionServiceLocationOID locationOid,
																  				 final Range<Date> dateRange) {
		if (locationOid == null) return FindSummariesResultBuilder.using(securityContext)
																	 .on(_modelObjectType)
																	 .errorFindingSummaries()
																				.causedByClientBadRequest("The location id is mandatory to find appointments");
		return this.getServiceImplAs(AA14FindServicesForBookedSlot.class)
						.findRangeBookedSlotsSummarizedFor(securityContext,
														   lang,
										         		   locationOid,
										         		   dateRange);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BY LOCATION & SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14BookedSlot> findRangeBookedSlotsFor(final SecurityContext securityContext,
															  final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
															  final Range<Date> dateRange) {
		if (locationOid == null) return FindResultBuilder.using(securityContext)
													.on(_modelObjectType)
													.errorFindingEntities()
															.causedByClientBadRequest("The location id is mandatory to find slots");
		return this.getServiceImplAs(AA14FindServicesForBookedSlot.class)
						.findRangeBookedSlotsFor(securityContext,
										   		 locationOid,schOid,
										   		 dateRange);
	}
	@Override
	public FindSummariesResult<AA14BookedSlot> findRangeBookedSlotsSummarizedFor(final SecurityContext securityContext,
																				 final Language lang,
																  				 final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
																  				 final Range<Date> dateRange) {
		if (locationOid == null) return FindSummariesResultBuilder.using(securityContext)
																	 .on(_modelObjectType)
																	 .errorFindingSummaries()
																				.causedByClientBadRequest("The location id is mandatory to find slots");
		return this.getServiceImplAs(AA14FindServicesForBookedSlot.class)
						.findRangeBookedSlotsSummarizedFor(securityContext,
														   lang,
										         		   locationOid,schOid,
										         		   dateRange);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OVERLAPPING RANGE 
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14BookedSlot> findBookedSlotsOverlappingRange(final SecurityContext securityContext,
																	  final AA14ScheduleOID schOid,
																	  final Range<Date> dateRange) {
		if (schOid == null) return FindResultBuilder.using(securityContext)
													.on(_modelObjectType)
													.errorFindingEntities()
																.causedByClientBadRequest("The schedule id is mandatory to find overlapping slots");
		return this.getServiceImplAs(AA14FindServicesForBookedSlot.class)
						.findBookedSlotsOverlappingRange(securityContext,
										         		 schOid,
										         		 dateRange);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BY CUSTOMER / SUBJECT
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public FindSummariesResult<AA14Appointment> findAppointmentsBy(final SecurityContext securityContext,
																   final AA14AppointmentFilter filter,
																   final Language lang) {
		return this.getServiceImplAs(AA14FindServicesForBookedSlot.class)
				   .findAppointmentsBy(securityContext,
						   		      filter,
						   		      lang);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindOIDsResult<AA14SlotOID> findNonBookablePeriodicSlotsOids(final SecurityContext securityContext,
																	    final AA14PeriodicSlotSerieOID serieOid) {
		return this.getServiceImplAs(AA14FindServicesForBookedSlot.class)
				   .findNonBookablePeriodicSlotsOids(securityContext,
						   						 	 serieOid);
	}
	@Override
	public FindResult<AA14NonBookableSlot> findNonBookablePeriodicSlots(final SecurityContext securityContext,
																	    final AA14PeriodicSlotSerieOID serieOid) {
		return this.getServiceImplAs(AA14FindServicesForBookedSlot.class)
				   .findNonBookablePeriodicSlots(securityContext,
						   						 serieOid);
	}
}
