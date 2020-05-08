package aa14b.calendar;

import java.util.Iterator;

import org.joda.time.LocalDate;

import aa14f.model.AA14BookedSlot;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.timeslots.AA14DayRangeTimeSlots;
import aa14f.model.timeslots.AA14DayTimeSlots;
import aa14f.model.timeslots.AA14TimeSlot;
import aa14f.model.timeslots.AA14TimeSlotsBuilder;
import lombok.extern.slf4j.Slf4j;
import r01f.model.persistence.PersistenceOperationExecResultBuilder;
import r01f.model.persistence.PersistenceOperationResult;
import r01f.model.services.COREServiceMethod;
import r01f.securitycontext.SecurityContext;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;

/**
 * Mock implementation of {@link AA14CalendarService} interface
 */
@Slf4j
public class AA14CalendarServiceMockImpl
     extends AA14CalendarServiceBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AA14DayRangeTimeSlots timeSlotsFor(final SecurityContext securityContext,
											  final AA14OrgDivisionServiceLocation location,final AA14Schedule sch,
											  final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
											  final int numberOfDays) {
		return _buildMockTimeSlots(sch.getOid(),
								   year,monthOfYear,dayOfMonth,
								   numberOfDays);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PersistenceOperationResult<AA14SlotID> reserveSlot(final SecurityContext securityContext,
															  final AA14OrgDivisionServiceLocation location,final AA14Schedule sch,
															  final AA14BookedSlot slot) {
		AA14SlotID outId = AA14SlotID.forId(location.getId().asString() + "/" + System.nanoTime());
		log.info("... reserving a slot with id={} at {}/{}/{}-{}:{} in the calendar",
				 outId,
				 slot.getYear(),slot.getMonthOfYear(),slot.getDayOfMonth(),
				 slot.getHourOfDay(),slot.getMinuteOfHour());
		
		boolean fail = location.getId().is("FAIL");
		if (!fail) {
			return PersistenceOperationExecResultBuilder.using(securityContext)
														.executed(COREServiceMethod.named("CREATE_CALENDAR_APPOINTMENT"))
														.returning(outId);
		} 
		return PersistenceOperationExecResultBuilder.using(securityContext)
													.notExecuted(COREServiceMethod.named("CREATE_CALENDAR_APPOINTMENT"))
													.because(AA14CalendarServiceException.createForAppointmentSlotOccupied());		
	}
	@Override
	public PersistenceOperationResult<AA14SlotID> updateSlot(final SecurityContext securityContext,
															 final AA14BookedSlot slot) {
		log.info("... updating a slot with id={} at {}/{}/{}-{}:{} in the calendar",
				 slot.getId(),
				 slot.getYear(),slot.getMonthOfYear(),slot.getDayOfMonth(),
				 slot.getHourOfDay(),slot.getMinuteOfHour());
		return PersistenceOperationExecResultBuilder.using(securityContext)
														.executed(COREServiceMethod.named("UPDATE_CALENDAR_APPOINTMENT"))
														.returning(slot.getId());
	}
	@Override
	public PersistenceOperationResult<Boolean> releaseSlot(final SecurityContext securityContext,
														   final AA14OrgDivisionServiceLocation location,final AA14Schedule sch,
														   final AA14SlotID id) {
		log.info("... releasing a slot with id={} from the calendar",
				 id);
		return PersistenceOperationExecResultBuilder.using(securityContext)
														.executed(COREServiceMethod.named("CANCEL_CALENDAR_APPOINTMENT"))
														.returning(new Boolean(true));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static AA14DayRangeTimeSlots _buildMockTimeSlots(final AA14ScheduleOID schOid,
															 final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   			 final int numberOfDays) {
		final LocalDate date1 = new LocalDate(year.getYear(),monthOfYear.getMonthOfYear(),dayOfMonth.getDayOfMonth());
		Iterable<AA14DayTimeSlots> daysIterable = new Iterable<AA14DayTimeSlots>() {
														private int _currDay = 0;
												
														@Override
														public Iterator<AA14DayTimeSlots> iterator() {
															return new Iterator<AA14DayTimeSlots>() {
																			@Override
																			public boolean hasNext() {
																				return _currDay < numberOfDays;
																			}
																			@Override
																			public AA14DayTimeSlots next() {
																				LocalDate date = date1.plusDays(_currDay);
																				_currDay = _currDay + 1;
																				AA14DayTimeSlots outDaySlots = new AA14DayTimeSlots(Year.of(date),MonthOfYear.of(date),DayOfMonth.of(date));
																				outDaySlots.add(AA14TimeSlot.createAvailableAt(schOid,
																															   9,0,
																															   30));
																				outDaySlots.add(AA14TimeSlot.createAvailableAt(schOid,
																															   9,30,	// + 30 min
																															   20));	
																				outDaySlots.add(AA14TimeSlot.createAvailableAt(schOid,
																															   10,0,	// + 30 min
																															   30));	
																				outDaySlots.add(AA14TimeSlot.createAvailableAt(schOid,
																															   10,30,	// + 30 min
																															   30));	
																				
																				return outDaySlots;
																			}
																			@Override
																			public void remove() {
																				throw new UnsupportedOperationException();
																			}
																   };
														}			
									  };
		AA14DayRangeTimeSlots dayRangeSlots = AA14TimeSlotsBuilder.dayRangeTimeSlotsBuilder(30)			// slots of 30 min!!
																  		.daysSlotsFor(daysIterable)
																  .build(year,monthOfYear,dayOfMonth,
																		 numberOfDays);
		
//		AA14DayRangeTimeSlots dayRangeSlots = AA14TimeSlotsBuilder.instance()
//																  .daySlotsFor(date1.getYear(),date1.getMonthOfYear(),date1.getDayOfMonth())
//																  		.addAvailableSlotAt(9,0).withDefaultSize()
//																  		.addAvailableSlotAt(9,30).withDefaultSize()
//																  		.addAvailableSlotAt(10,0).withDefaultSize()
//																  		.addAvailableSlotAt(10,30).withDefaultSize()
//																  		.end()
//																  .daySlotsFor(date2.getYear(),date2.getMonthOfYear(),date2.getDayOfMonth())
//																  		.addAvailableSlotAt(9,0).withDefaultSize()
//																  		.addAvailableSlotAt(9,30).withDefaultSize()
//																  		.addAvailableSlotAt(10,0).withDefaultSize()
//																  		.addAvailableSlotAt(10,30).withDefaultSize()
//																  		.end()
//																  .build();
		return dayRangeSlots;
	}
}
