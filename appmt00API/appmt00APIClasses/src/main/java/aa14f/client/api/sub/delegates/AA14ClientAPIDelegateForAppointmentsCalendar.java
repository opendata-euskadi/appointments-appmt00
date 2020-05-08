package aa14f.client.api.sub.delegates;

import java.util.Date;

import javax.inject.Singleton;

import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Period;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14BookedSlotsCalendarServices;
import aa14f.model.AA14NumberOfAdjacentSlots;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.timeslots.AA14DayRangeTimeSlots;
import lombok.experimental.Accessors;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.api.delegates.ClientAPIServiceDelegateBase;
import r01f.types.Range;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;

/**
 * Client implementation of appointments calendar.
 */
@Singleton
@Accessors(prefix="_")
public class AA14ClientAPIDelegateForAppointmentsCalendar
     extends ClientAPIServiceDelegateBase<AA14BookedSlotsCalendarServices> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIDelegateForAppointmentsCalendar(final Provider<SecurityContext> securityContextProvider,
													    final Marshaller modelObjectsMarshaller,
								  						final AA14BookedSlotsCalendarServices calendarServices) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  calendarServices);	// reference to other client apis
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BY LOCATION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the day by day time slots available for appointments in the provided date range
	 * @param locOid the location oid for which the calendar is requested
	 * @param numberOfAdjacentSlots the number of adjacent free slots to consider a free slot
	 * @param dateRange
	 * @param slip the range to find the first available slot if the given range does NOT contains any available slot
	 * @return
	 */
	public AA14DayRangeTimeSlots timeSlotsForRange(final AA14OrgDivisionServiceLocationOID locOid,
												   final AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   final Range<Date> dateRange,
												   final boolean slipDateRangeToFindFirstAvailableSlot) {
		return this.timeSlotsForRange(locOid,
									  numberOfAdjacentSlots,
									  dateRange,
									  null,		// no preferred schedule id
									  slipDateRangeToFindFirstAvailableSlot);
	}
	/**
	 * Returns the day by day time slots available for appointments in the provided date range
	 * @param locOid the location oid for which the calendar is requested
	 * @param numberOfAdjacentSlots the number of adjacent free slots to consider a free slot
	 * @param dateRange
	 * @param prefSchOid
	 * @param slip the range to find the first available slot if the given range does NOT contains any available slot
	 * @return
	 */
	public AA14DayRangeTimeSlots timeSlotsForRange(final AA14OrgDivisionServiceLocationOID locOid,
												   final AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   final Range<Date> dateRange,
												   final AA14ScheduleOID prefSchOid,
												   final boolean slipDateRangeToFindFirstAvailableSlot) {
		LocalDate startDate = new LocalDate(dateRange.getLowerBound());
		LocalDate endDate = new LocalDate(dateRange.getUpperBound());
		Interval interval = new Interval(startDate.toDate().getTime(),endDate.toDate().getTime());
		Period period = interval.toPeriod();
		int numberOfDays = period.getDays();
		
		return this.timeSlotsForRange(locOid,prefSchOid,
									  numberOfAdjacentSlots,
									  Year.of(startDate),MonthOfYear.of(startDate),DayOfMonth.of(startDate),
									  numberOfDays,
									  slipDateRangeToFindFirstAvailableSlot);
	}
	/**
	 * Returns the day by day time slots available for appointments in the provided date range
	 * @param locOid the location oid for which the calendar is requested
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @param numberOfDays
	 * @param numberOfAdjacentSlots the number of adjacent free slots to consider a free slot
	 * @param slip the range to find the first available slot if the given range does NOT contains any available slot
	 * @return
	 */
	public AA14DayRangeTimeSlots timeSlotsForRange(final AA14OrgDivisionServiceLocationOID locOid,	
												   final AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   final int numberOfDays,
												   final boolean slipDateRangeToFindFirstAvailableSlot) {
		return this.timeSlotsForRange(locOid,(AA14ScheduleOID)null,// no preferred schedule oid	
									  numberOfAdjacentSlots,
									  year,monthOfYear,dayOfMonth,
									  numberOfDays,
									  slipDateRangeToFindFirstAvailableSlot);	
	}
	/**
	 * Returns the day by day time slots available for appointments in the provided date range
	 * @param locOid the location oid for which the calendar is requested
	 * @param prefSchOid
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @param numberOfDays
	 * @param numberOfAdjacentSlots the number of adjacent free slots to consider a free slot
	 * @param slip the range to find the first available slot if the given range does NOT contains any available slot
	 * @return
	 */
	public AA14DayRangeTimeSlots timeSlotsForRange(final AA14OrgDivisionServiceLocationOID locOid,	
												   final AA14ScheduleOID prefSchOid,
												   final AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   final int numberOfDays,
												   final boolean slipDateRangeToFindFirstAvailableSlot) {
		return this.getServiceProxy()
						.availableTimeSlotsForRange(this.getSecurityContext(),
													locOid,prefSchOid,
													numberOfAdjacentSlots,
													year,monthOfYear,dayOfMonth,
													numberOfDays,
													slipDateRangeToFindFirstAvailableSlot);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BY SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the day by day time slots available for appointments in the provided date range
	 * @param schOid the location oid for which the calendar is requested
	 * @param numberOfAdjacentSlots the number of adjacent free slots to consider a free slot
	 * @param dateRange
	 * @param slip the range to find the first available slot if the given range does NOT contains any available slot
	 * @return
	 */
	public AA14DayRangeTimeSlots timeSlotsForRange(final AA14ScheduleOID schOid,
												   final AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   final Range<Date> dateRange,
												   final boolean slipDateRangeToFindFirstAvailableSlot) {
		return this.timeSlotsForRange(schOid,(AA14OrgDivisionServiceLocationOID)null,	// no preferred location
									  numberOfAdjacentSlots,
									  dateRange,
									  slipDateRangeToFindFirstAvailableSlot);		
	}
	/**
	 * Returns the day by day time slots available for appointments in the provided date range
	 * @param schOid the location oid for which the calendar is requested
	 * @param prefLocOid
	 * @param numberOfAdjacentSlots the number of adjacent free slots to consider a free slot
	 * @param dateRange
	 * @param slip the range to find the first available slot if the given range does NOT contains any available slot
	 * @return
	 */
	public AA14DayRangeTimeSlots timeSlotsForRange(final AA14ScheduleOID schOid,
												   final AA14OrgDivisionServiceLocationOID prefLocOid,
												   final AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   final Range<Date> dateRange,
												   final boolean slipDateRangeToFindFirstAvailableSlot) {
		LocalDate startDate = new LocalDate(dateRange.getLowerBound());
		LocalDate endDate = new LocalDate(dateRange.getUpperBound());
		Interval interval = new Interval(startDate.toDate().getTime(),endDate.toDate().getTime());
		Period period = interval.toPeriod();
		int numberOfDays = period.getDays();
		
		return this.timeSlotsForRange(schOid,prefLocOid,
									  numberOfAdjacentSlots,
									  Year.of(startDate.getYear()),MonthOfYear.of(startDate.getMonthOfYear()),DayOfMonth.of(startDate.getDayOfMonth()),
									  numberOfDays,
									  slipDateRangeToFindFirstAvailableSlot);
	}
	/**
	 * Returns the day by day time slots available for appointments in the provided date range
	 * @param schOid the schedule oid
	 * @param numberOfAdjacentSlots the number of adjacent free slots to consider a free slot
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @param numberOfDays
	 * @param slip the range to find the first available slot if the given range does NOT contains any available slot
	 * @return
	 */
	public AA14DayRangeTimeSlots timeSlotsForRange(final AA14ScheduleOID schOid,	
												   final AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   final int numberOfDays,
												   final boolean slipDateRangeToFindFirstAvailableSlot) {
		return this.timeSlotsForRange(schOid,(AA14OrgDivisionServiceLocationOID)null,	// no preferred location
									  numberOfAdjacentSlots,
									  year,monthOfYear,dayOfMonth,
									  numberOfDays,
									  slipDateRangeToFindFirstAvailableSlot);			
	}
	/**
	 * Returns the day by day time slots available for appointments in the provided date range
	 * @param schOid the schedule oid
	 * @param prefLocOid
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @param numberOfDays
	 * @param numberOfAdjacentSlots the number of adjacent free slots to consider a free slot
	 * @param slip the range to find the first available slot if the given range does NOT contains any available slot
	 * @return
	 */
	public AA14DayRangeTimeSlots timeSlotsForRange(final AA14ScheduleOID schOid,
												   final AA14OrgDivisionServiceLocationOID prefLocOid,	
												   final AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   final int numberOfDays,
												   final boolean slipDateRangeToFindFirstAvailableSlot) {
		return this.getServiceProxy()
						.availableTimeSlotsForRange(this.getSecurityContext(),
													schOid,prefLocOid,
													numberOfAdjacentSlots,
													year,monthOfYear,dayOfMonth,
													numberOfDays,
													slipDateRangeToFindFirstAvailableSlot);
	}
}
