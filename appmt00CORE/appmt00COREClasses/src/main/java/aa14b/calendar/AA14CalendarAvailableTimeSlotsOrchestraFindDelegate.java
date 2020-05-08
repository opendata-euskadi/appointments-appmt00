package aa14b.calendar;

import java.util.Date;

import org.joda.time.LocalDate;

import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.timeslots.AA14DayRangeTimeSlots;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.securitycontext.SecurityContext;
import r01f.types.Range;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;

/**
 * Finds available time slots using a calendar service (ie QMatic Orchestra)
 */
@Slf4j
@RequiredArgsConstructor
public class AA14CalendarAvailableTimeSlotsOrchestraFindDelegate 
     extends AA14CalendarAvailableTimeSlotsFindDelegateBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14CalendarService _calendarService;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AA14DayRangeTimeSlots availableTimeSlotsForRange(final SecurityContext securityContext,
												   			final AA14OrgDivisionServiceLocation loc,final AA14Schedule sch,
												   			final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   			final int numberOfDays) {
		// Get a date range
		LocalDate startDate = new LocalDate(year.getYear(),monthOfYear.getMonthOfYear(),dayOfMonth.getDayOfMonth());
		final Range<Date> dateRange = Range.closed(startDate.toDate(),
											 	   startDate.plusDays(numberOfDays).toDate());
		
		
		// use qmatic orchestra to get the available slots
		log.info("Retrieving the bookable slots at locationId={}/scheduleId={} ({}/{}/{}={}) from QMatic orchestra",
				 loc.getId(),sch.getId(),
				 year,monthOfYear,dayOfMonth,dateRange);
		
		final AA14DayRangeTimeSlots outRangeAvailableSlots; 
		outRangeAvailableSlots = _calendarService.timeSlotsFor(securityContext,
											 				   loc,sch, 
											 				   year,monthOfYear,dayOfMonth,
											 				   numberOfDays);
		return outRangeAvailableSlots;
	}
}
