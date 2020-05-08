package aa14f.api.interfaces;

import aa14f.model.AA14NumberOfAdjacentSlots;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.timeslots.AA14DayRangeTimeSlots;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ExposedServiceInterface;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;

@ExposedServiceInterface
public interface AA14BookedSlotsCalendarServices 
		 extends AA14ServiceInterface {
	/**
	 * Returns the day by day time slots available for appointments in the provided date range
	 * <pre>
	 * 						  | LOC 1 | LOC 2 | LOC 3
	 *                        |-------|-------|------
	 *                   SHC1 |   X   |       |   X
	 *                   -----|-------|-------|------
	 *                   SCH2 |       |   X   |   X
	 *                   -----|-------|-------|------
	 *                   SCH3 |   X   |   X   | 
	 *                            ^
	 *                            |
	 *                       find by col
	 * </pre>
	 * In the example above, this method returns all available slots at LOC 1
	 * whether they're in SCH1 or SCH3
	 *                           
	 * @param securityContext
	 * @param locOid the location oid for which the calendar is requested
	 * @param prefSchOid preferred schedule oid in case there's more than a single option
	 * @param numberOfAdjacentSlots the number of adjacent free slots to be returned
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @param numberOfDays
	 * @param slip the range to find the first available slot if the given range does NOT contains any available slot
	 * @return
	 */
	public AA14DayRangeTimeSlots availableTimeSlotsForRange(SecurityContext securityContext,
												   			AA14OrgDivisionServiceLocationOID locOid,
												   			AA14ScheduleOID prefSchOid,
												   			AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   			Year year,MonthOfYear monthOfYear,DayOfMonth dayOfMonth,
												   			int numberOfDays,
												   			boolean slipDateRangeToFindFirstAvailableSlot);
	/**
	 * Returns the day by day time slots available for appointments in the provided date range
	 * <pre>
	 * 						  | LOC 1 | LOC 2 | LOC 3
	 *                        |-------|-------|------
	 *                   SHC1 |   X   |       |   X    <-- find by row
	 *                   -----|-------|-------|------
	 *                   SCH2 |       |   X   |   X
	 *                   -----|-------|-------|------
	 *                   SCH3 |   X   |   X   | 
	 * </pre>
	 * In the example above, this method returns all available slots at SCH 1
	 * whether they're for LOC1 or LOC 3
	 * @param securityContext
	 * @param schOid the schedule oid for which the calendar is requested
	 * @param prefLocOid preferred schedule oid in case there's more than a single option
	 * @param numberOfAdjacentSlots the number of adjacent free slots to be returned
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @param numberOfDays
	 * @param slip the range to find the first available slot if the given range does NOT contains any available slot
	 * @return
	 */
	public AA14DayRangeTimeSlots availableTimeSlotsForRange(SecurityContext securityContext,
												   			AA14ScheduleOID schOid,
												   			AA14OrgDivisionServiceLocationOID prefLocOid,
												   			AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   			Year year,MonthOfYear monthOfYear,DayOfMonth dayOfMonth,
												   			int numberOfDays,
												   			boolean slipDateRangeToFindFirstAvailableSlot);
}
