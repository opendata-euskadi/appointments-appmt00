package aa14b.services;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14b.services.delegates.persistence.AA14CRUDServicesDelegateForBookedSlot;
import aa14b.services.delegates.persistence.AA14FindServicesDelegateForBookedSlot;
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
import aa14f.model.search.AA14BookedSlotFilter;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.locale.Language;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.persistence.FindOIDsResult;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindSummariesResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.persistence.ServiceDelegateProvider;
import r01f.types.Range;

/**
 * Implements the find-related services which in turn are
 * delegated to {@link AA14CRUDServicesDelegateForBookedSlot}
 */
@Singleton
@Accessors(prefix="_")
public class AA14FindServicesImplForBookedSlots
     extends AA14FindServicesImplBase<AA14SlotOID,AA14SlotID,AA14BookedSlot>
  implements AA14FindServicesForBookedSlot {
/////////////////////////////////////////////////////////////////////////////////////////
//	DELEGATE PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ServiceDelegateProvider<AA14FindServicesDelegateForBookedSlot> _delegateProvider;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14FindServicesImplForBookedSlots(							final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
											  @ModelObjectsMarshaller 	final Marshaller modelObjectsMarshaller,
																		final EventBus eventBus,
																	    final Provider<EntityManager> entityManagerProvider,
											  							final AA14BookedSlotSummarizerService appointmentSummarizerService) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  entityManagerProvider);
		_delegateProvider = new ServiceDelegateProvider<AA14FindServicesDelegateForBookedSlot>() {
									@Override
									public AA14FindServicesDelegateForBookedSlot createDelegate(final SecurityContext securityContext) {
										return new AA14FindServicesDelegateForBookedSlot(_coreConfig,
																						 AA14FindServicesImplForBookedSlots.this.getFreshNewEntityManager(),
																						 _modelObjectsMarshaller,_eventBus,
																						 appointmentSummarizerService);
									}
							};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BY SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14BookedSlot> findRangeBookedSlotsFor(final SecurityContext securityContext,
										   					  final AA14ScheduleOID schOid,
										   					  final Range<Date> dateRange) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForBookedSlot.class)
							.findRangeBookedSlotsFor(securityContext,
											   		 schOid,
											   		 dateRange);
	}
	@Override
	public FindSummariesResult<AA14BookedSlot> findRangeBookedSlotsSummarizedFor(final SecurityContext securityContext,
																				 final Language lang,
																  			   	 final AA14ScheduleOID schOid,
																  			   	 final Range<Date> dateRange) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForBookedSlot.class)
							.findRangeBookedSlotsSummarizedFor(securityContext,
															   lang,
													 		   schOid,
													 		   dateRange);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BY LOCATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindOIDsResult<AA14SlotOID> findRangeBookedSlotsFor(final SecurityContext securityContext,
															   final AA14OrgDivisionServiceLocationOID locationOid,
										   					   final Range<Date> dateRange,
										   					   final AA14BookedSlotType slotType) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForBookedSlot.class)
							.findRangeBookedSlotsFor(securityContext,
											   	     locationOid,
											   		 dateRange,
											   		 slotType);
	}
	@Override
	public FindResult<AA14BookedSlot> findRangeBookedSlotsFor(final SecurityContext securityContext,
										   					  final AA14OrgDivisionServiceLocationOID locationOid,
										   					  final Range<Date> dateRange) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForBookedSlot.class)
							.findRangeBookedSlotsFor(securityContext,
											   	     locationOid,
											   		 dateRange);
	}
	@Override
	public FindSummariesResult<AA14BookedSlot> findRangeBookedSlotsSummarizedFor(final SecurityContext securityContext,
																				 final Language lang,
																  			   	 final AA14OrgDivisionServiceLocationOID locationOid,
																  			   	 final Range<Date> dateRange) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForBookedSlot.class)
							.findRangeBookedSlotsSummarizedFor(securityContext,
															   lang,
													 		   locationOid,
													 		   dateRange);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BY LOCATION & SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14BookedSlot> findRangeBookedSlotsFor(final SecurityContext securityContext,
										   					  final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
										   					  final Range<Date> dateRange) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForBookedSlot.class)
							.findRangeBookedSlotsFor(securityContext,
											   	     locationOid,schOid,
											   		 dateRange);
	}
	@Override
	public FindSummariesResult<AA14BookedSlot> findRangeBookedSlotsSummarizedFor(final SecurityContext securityContext,
																				 final Language lang,
																  			   	 final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
																  			   	 final Range<Date> dateRange) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForBookedSlot.class)
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
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForBookedSlot.class)
							.findBookedSlotsOverlappingRange(securityContext,
											   	     		 schOid,
											   	     		 dateRange);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BOOKED SLOTS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindSummariesResult<AA14Appointment> findAppointmentsBy(final SecurityContext securityContext,
																   final AA14AppointmentFilter filter,
																   final Language lang) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForBookedSlot.class)
						   .findAppointmentsBy(securityContext,
								   		       filter,
								   		       lang);
	}
	@Override
	public FindOIDsResult<AA14SlotOID> findBookedSlotsBy(final SecurityContext securityContext,
														 final AA14BookedSlotFilter filter) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForBookedSlot.class)
						   .findBookedSlotsBy(securityContext,
								   		      filter);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindOIDsResult<AA14SlotOID> findNonBookablePeriodicSlotsOids(final SecurityContext securityContext,
																	    final AA14PeriodicSlotSerieOID serieOid) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForBookedSlot.class)
						   .findNonBookablePeriodicSlotsOids(securityContext,
								   						 	 serieOid);		
	}
	@Override
	public FindResult<AA14NonBookableSlot> findNonBookablePeriodicSlots(final SecurityContext securityContext,
																	    final AA14PeriodicSlotSerieOID serieOid) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForBookedSlot.class)
						   .findNonBookablePeriodicSlots(securityContext,
								   						 serieOid);		
	}
}
