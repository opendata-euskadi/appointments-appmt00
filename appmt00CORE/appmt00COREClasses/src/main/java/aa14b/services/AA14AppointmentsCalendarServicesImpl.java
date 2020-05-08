package aa14b.services;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14b.calendar.AA14CalendarService;
import aa14b.services.delegates.persistence.AA14AppointmentsCalendarServicesDelegate;
import aa14b.services.internal.AA14BookedSlotSummarizerService;
import aa14f.api.interfaces.AA14BookedSlotsCalendarServices;
import aa14f.model.AA14NumberOfAdjacentSlots;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.timeslots.AA14DayRangeTimeSlots;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.persistence.CorePersistenceServicesBase;
import r01f.services.persistence.ServiceDelegateProvider;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;

@Singleton
@Accessors(prefix="_")
public class AA14AppointmentsCalendarServicesImpl
	 extends CorePersistenceServicesBase
  implements AA14BookedSlotsCalendarServices,
  			 AA14ServiceInterfaceImpl {

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The calendar service 
	 */
	private final AA14CalendarService _calendarService;
	/**
	 * The slot summarizer service
	 */
	private final AA14BookedSlotSummarizerService _slotSummarizerService;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14AppointmentsCalendarServicesImpl(						 final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
												@ModelObjectsMarshaller	 final Marshaller modelObjectsMarshaller,
																		 final EventBus eventBus,
																		 final Provider<EntityManager> entityManagerProvider,
																		 final AA14CalendarService calendarService,
																		 final AA14BookedSlotSummarizerService slotSummarizerService) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  entityManagerProvider);
		_calendarService = calendarService;
		_slotSummarizerService = slotSummarizerService;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DELEGATE PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ServiceDelegateProvider<AA14AppointmentsCalendarServicesDelegate> _delegateProvider =
								new ServiceDelegateProvider<AA14AppointmentsCalendarServicesDelegate>() {
										@Override
										public AA14AppointmentsCalendarServicesDelegate createDelegate(final SecurityContext securityContext) {
											return new AA14AppointmentsCalendarServicesDelegate(_coreConfig,
																								AA14AppointmentsCalendarServicesImpl.this.getFreshNewEntityManager(),
																								_modelObjectsMarshaller,
																								_eventBus,
																								_slotSummarizerService,
																					 			_calendarService);
										}
									};
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AA14DayRangeTimeSlots availableTimeSlotsForRange(final SecurityContext securityContext,
												   			final AA14OrgDivisionServiceLocationOID locOid,
												   			final AA14ScheduleOID prefSchOid,
												   			final AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   			final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   			final int numberOfDays,
												   			final boolean slipDateRangeToFindFirstAvailableSlot) {
		return _delegateProvider.createDelegate(securityContext)
								.availableTimeSlotsForRange(securityContext,
												   			locOid,prefSchOid,
												   			numberOfAdjacentSlots,
												   			year,monthOfYear,dayOfMonth,
												   			numberOfDays,
												   			slipDateRangeToFindFirstAvailableSlot);
	}
	@Override
	public AA14DayRangeTimeSlots availableTimeSlotsForRange(final SecurityContext securityContext,
												   			final AA14ScheduleOID schOid,
												   			final AA14OrgDivisionServiceLocationOID prefLocOid,
												   			final AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   			final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   			final int numberOfDays,
												   			final boolean slipDateRangeToFindFirstAvailableSlot) {
		return _delegateProvider.createDelegate(securityContext)
								.availableTimeSlotsForRange(securityContext,
												   			schOid,prefLocOid,
												   			numberOfAdjacentSlots,
												   			year,monthOfYear,dayOfMonth,
												   			numberOfDays,
												   			slipDateRangeToFindFirstAvailableSlot);
	}
}
