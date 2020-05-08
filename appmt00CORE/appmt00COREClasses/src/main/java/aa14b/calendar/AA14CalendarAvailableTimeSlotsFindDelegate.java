package aa14b.calendar;

import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.timeslots.AA14DayRangeTimeSlots;
import r01f.securitycontext.SecurityContext;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;

/**
 * Util type that findls available timeslots
 */
public interface AA14CalendarAvailableTimeSlotsFindDelegate {
	/**
	 * Finds all the available time slots for a given range; if the range does NOT starts with days that contains available slots, 
	 * this method slips the range to find a first day with at least one available slot
	 * <pre>
	 * If the requested range is like:
	 *                         Requested Range
	 * 					  /=======================\
	 * 						Day 1 | Day 2 | Day 3 | Day 4 | Day 5 | Day 6
	 *      				------|-------|-------|-------|-------|-------
	 *   available slots: 	  0   |   0   |   5   |   0   |   0   |   1
	 *                                    \=======================/
	 *                                           Returned Range
	 * since the Day 1 and Day 2 does NOT contains available slots, the range is slipped 2 days starting from the end of the range
	 * </pre>
	 * @param securityContext
	 * @param loc
	 * @param sch
	 * @param numberOfAdjacentSlots
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @param numberOfDays
	 * @return
	 */
	public AA14DayRangeTimeSlots nearestRangeWithAvailableTimeSlots(final SecurityContext securityContext,
												   					final AA14OrgDivisionServiceLocation loc,final AA14Schedule sch,
												   					final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   					final int numberOfDays);
	/**
	 * Finds all the available time slots for a given range; if the range does NOT starts with days that contains available slots, 
	 * this method DOES NOT slip the range to find a first day with at least one available slot
	 * @param securityContext
	 * @param loc
	 * @param sch
	 * @param numberOfAdjacentSlots
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @param numberOfDays

	 * @return
	 */
	public AA14DayRangeTimeSlots availableTimeSlotsForRange(final SecurityContext securityContext,
												   			final AA14OrgDivisionServiceLocation loc,final AA14Schedule sch,
												   			final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   			final int numberOfDays);
	
}
