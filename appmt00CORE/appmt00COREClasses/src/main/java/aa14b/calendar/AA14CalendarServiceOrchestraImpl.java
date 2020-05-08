package aa14b.calendar;

import java.util.Collection;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

import aa14b.calendar.orchestra.model.AA14OrchestraAvailableSlots;
import aa14b.calendar.orchestra.model.AA14OrchestraBookedSlot;
import aa14b.calendar.orchestra.model.AA14OrchestraIDs.AA14OrchestraAppointmentID;
import aa14b.calendar.orchestra.model.AA14OrchestraIDs.AA14OrchestraBranchID;
import aa14b.calendar.orchestra.model.AA14OrchestraIDs.AA14OrchestraBranchServiceID;
import aa14f.model.AA14BookedSlot;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.timeslots.AA14DayRangeTimeSlots;
import aa14f.model.timeslots.AA14DayTimeSlots;
import aa14f.model.timeslots.AA14TimeSlot;
import aa14f.model.timeslots.AA14TimeSlotsBuilder;
import r01f.exceptions.Throwables;
import r01f.model.persistence.PersistenceOperationExecResultBuilder;
import r01f.model.persistence.PersistenceOperationResult;
import r01f.model.services.COREServiceMethod;
import r01f.securitycontext.SecurityContext;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;
import r01f.util.types.collections.CollectionUtils;

/**
 * Mock implementation of {@link AA14CalendarService} interface
 */
public class AA14CalendarServiceOrchestraImpl
     extends AA14CalendarServiceBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	private AA14QMaticOrchestraMediator _orchestraMediator;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AA14DayRangeTimeSlots timeSlotsFor(final SecurityContext securityContext,
											  final AA14OrgDivisionServiceLocation location,final AA14Schedule sch,
											  final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
											  final int numberOfDays) {
		if (sch.getOrchestraConfig() == null) throw new IllegalStateException(Throwables.message("There's NO orchestra config for schedule {}",sch.getId()));
		
		AA14OrchestraBranchID branchId = AA14OrchestraBranchID.forId(sch.getOrchestraConfig().getBranchId());
		AA14OrchestraBranchServiceID serviceId = AA14OrchestraBranchServiceID.forId(sch.getOrchestraConfig().getServiceId());
		
		AA14DayRangeTimeSlots outDayRangeTimeSlots = new AA14DayRangeTimeSlots(sch.getBookingConfig().getSlotDefaultLengthMinutes(),
																			   year,monthOfYear,dayOfMonth,
																			   numberOfDays);
		
		// Get a collection of AA14DayTimeSlots (one for each day within the range)
		int theNumberOfDays = numberOfDays <= 0 ? 1 : numberOfDays;
		
		// Get a collection of AA14DayTimeSlots
		LocalDate startDate = new LocalDate(year.getYear(),monthOfYear.getMonthOfYear(),dayOfMonth.getDayOfMonth());
		for (int i=0; i < theNumberOfDays; i++) {
			final LocalDate aDay = startDate.plusDays(i);
			
			// Use the orchestra mediator to get the available slots
			AA14OrchestraAvailableSlots dateSlots = _orchestraMediator.listBranchServiceAvailableSlotsAtDate(branchId,serviceId,
																	 										 aDay.getYear(),aDay.getMonthOfYear(),aDay.getDayOfMonth());
			// if the day is today filter slots before this moment 
			// (orchestra returns today slots as available even if they're in a past time)
			Collection<LocalTime> theDateSlots = FluentIterable.from(dateSlots.getSlots())
															   .filter(new Predicate<LocalTime>() {
																   				private LocalDate today = LocalDate.now();
																   				private LocalTime now = LocalTime.now();
																   				
																				@Override
																				public boolean apply(final LocalTime slot) {																					
																					return aDay.isBefore(today) || aDay.isEqual(today) ? slot.isAfter(now)	// if today return only future slots  
																											  						   : true;				// ... else it's a future slot
																				}
															   		   })
															   .toList();
			
			// Transform the orchestra returned data into AA14DayTimeSlots
			AA14DayTimeSlots thisDaySlots = null;
			if (CollectionUtils.hasData(theDateSlots)) {
				// transform orchestra-returned slots into AA14TimeSlot objects
				thisDaySlots = AA14TimeSlotsBuilder.dayTimeSlotsBuilder(Year.of(aDay),MonthOfYear.of(aDay),DayOfMonth.of(aDay))
												   .addSlots(Iterables.transform(theDateSlots,
																				 new Function<LocalTime,AA14TimeSlot>() {
																							@Override
																							public AA14TimeSlot apply(final LocalTime slot) {
																								return new AA14TimeSlot(sch.getOid(),
																														HourOfDay.of(slot.getHourOfDay()),MinuteOfHour.of(slot.getMinuteOfHour()),
																														true);	// available
																							}
																
																				 }))
												   .build();
			} else {
				thisDaySlots = AA14TimeSlotsBuilder.dayTimeSlotsBuilder(Year.of(aDay),MonthOfYear.of(aDay),DayOfMonth.of(aDay))
												   .build();	// no slots
			}
			// add the collection of AA14DayTimeSlots to the out AA14DayRangeTimeSlots
			outDayRangeTimeSlots.add(thisDaySlots);
		}	
		return outDayRangeTimeSlots;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PersistenceOperationResult<AA14SlotID> reserveSlot(final SecurityContext securityContext,
															  final AA14OrgDivisionServiceLocation loc,final AA14Schedule sch,
															  final AA14BookedSlot slot) {
		if (sch.getOrchestraConfig() == null) throw new IllegalStateException(Throwables.message("There's NO orchestra config for schedule {}",sch.getId()));
		
		AA14OrchestraBranchID branchId = AA14OrchestraBranchID.forId(sch.getOrchestraConfig().getBranchId());
		AA14OrchestraBranchServiceID serviceId = AA14OrchestraBranchServiceID.forId(sch.getOrchestraConfig().getServiceId());
		
		// use the orchestra mediator to book an appointment
		// this call could throw an AA14AppointmentCalendarServiceException due to the slot being occupied
		AA14OrchestraBookedSlot orchestraAppointment = _orchestraMediator.reserveSlot(branchId,serviceId,
																					  slot);
		// return the appointment id
		AA14OrchestraAppointmentID orchestraAppointmentId = orchestraAppointment.getId();
		return PersistenceOperationExecResultBuilder.using(securityContext)
													.executed(COREServiceMethod.named("RESERVE SLOT"))
													.returning(orchestraAppointmentId.toAppointmentId());
	}
	@Override
	public PersistenceOperationResult<AA14SlotID> updateSlot(final SecurityContext securityContext,
															 final AA14BookedSlot slot) {
		if (slot.getId() == null) throw new IllegalArgumentException("The slot to be updated does NOT have the calendar id!!");
		// use the orchestra mediator to book an appointment
		// this call could throw an AA14AppointmentCalendarServiceException due to the slot being occupied
		AA14OrchestraBookedSlot orchestraAppointment = _orchestraMediator.updateSlot(slot);
		return PersistenceOperationExecResultBuilder.using(securityContext)
														.executed(COREServiceMethod.named("UPDATE_CALENDAR_APPOINTMENT"))
														.returning(orchestraAppointment.getId()
																					   .toAppointmentId());
	}
	@Override
	public PersistenceOperationResult<Boolean> releaseSlot(final SecurityContext securityContext, 
														   final AA14OrgDivisionServiceLocation location,final AA14Schedule sch,
														   final AA14SlotID id) {
		boolean deleted = _orchestraMediator.releaseSlot(id);
		return PersistenceOperationExecResultBuilder.using(securityContext)
													.executed(COREServiceMethod.named("RELEASE SLOT"))
													.returning(deleted);
	}
}
