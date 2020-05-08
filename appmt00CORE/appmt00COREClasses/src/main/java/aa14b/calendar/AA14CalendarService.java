package aa14b.calendar;

import aa14f.model.AA14BookedSlot;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.timeslots.AA14DayRangeTimeSlots;
import r01f.model.services.COREServiceMethodExecResult;
import r01f.securitycontext.SecurityContext;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;

/**
 * Interface for types that talks to QMatic Orchestra calendars
 */
public interface AA14CalendarService {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the time slots for a location calendar in a date range
	 * @param securityContext
	 * @param location
	 * @param service
	 * @param range
	 * @return
	 */
	public AA14DayRangeTimeSlots timeSlotsFor(final SecurityContext securityContext,
											  final AA14OrgDivisionServiceLocation location,final AA14Schedule sch,
											  final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
											  final int numberOfDays);
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Reserves an slot for an slot
	 * @param securityContext
	 * @param location
	 * @param slot
	 * @return
	 */
	public COREServiceMethodExecResult<AA14SlotID> reserveSlot(final SecurityContext securityContext,
															   final AA14OrgDivisionServiceLocation location,final AA14Schedule sch,
															   final AA14BookedSlot slot);
	/**
	 * Reserves an slot for an slot
	 * @param securityContext
	 * @param location
	 * @param slot
	 * @return
	 */
	public COREServiceMethodExecResult<AA14SlotID> updateSlot(final SecurityContext securityContext,
															  final AA14BookedSlot slot);
	
	/**
	 * Removes a previously created slot
	 * @param securityContext
	 * @param location
	 * @param id
	 * @return
	 */
	public COREServiceMethodExecResult<Boolean> releaseSlot(final SecurityContext securityContext,
														    final AA14OrgDivisionServiceLocation location,final AA14Schedule sch,
															final AA14SlotID id);
}
