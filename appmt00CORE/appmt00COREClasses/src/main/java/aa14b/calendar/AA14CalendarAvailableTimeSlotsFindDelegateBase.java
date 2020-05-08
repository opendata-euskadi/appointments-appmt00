package aa14b.calendar;

import java.util.List;

import org.joda.time.LocalDate;

import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.AA14ScheduleBookingLimit;
import aa14f.model.timeslots.AA14DayRangeTimeSlots;
import aa14f.model.timeslots.AA14DayTimeSlots;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.securitycontext.SecurityContext;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;
import r01f.util.types.collections.CollectionUtils;

/**
 * Finds available time slots
 */
@Slf4j
@RequiredArgsConstructor
abstract class AA14CalendarAvailableTimeSlotsFindDelegateBase
    implements AA14CalendarAvailableTimeSlotsFindDelegate {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AA14DayRangeTimeSlots nearestRangeWithAvailableTimeSlots(final SecurityContext securityContext,
												   					final AA14OrgDivisionServiceLocation loc,final AA14Schedule sch,
												   					final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   					final int numberOfDays) {
		
		
		// 0) check if the range doesn't go beyond future limit
		boolean overTheLimit=false; //future date limit
		LocalDate initDate = new LocalDate(year.getYear(),monthOfYear.getMonthOfYear(),dayOfMonth.getDayOfMonth());
		LocalDate rangeStartDate = initDate;
		
		LocalDate nextRangeStartDate = rangeStartDate.plusDays(numberOfDays);	
		overTheLimit = _isOverTheFutureLimit(nextRangeStartDate,initDate,
											sch.getBookingConfig().getBookingLimit());
		
		//over the future limit to fetch. Return!!
		if (overTheLimit) {
			log.debug("Start date goes beyond the limit date. Available time-slots is EMPTY!!! ");	
			return new AA14DayRangeTimeSlots(sch.getBookingConfig().getSlotDefaultLengthMinutes(),
											year,
											monthOfYear,
											dayOfMonth,
											numberOfDays); 
		}
		// 1) find the available time slots for the given range
		AA14DayRangeTimeSlots outRangeAvailableSlots = this.availableTimeSlotsForRange(securityContext,
																					   loc,sch,
																					   year,monthOfYear,dayOfMonth,
																					   numberOfDays);
		log.debug("Available time slots {} days from: {}/{}/{}\n{}",
				  numberOfDays,year,monthOfYear,dayOfMonth,
				  outRangeAvailableSlots.debugInfo());
		
		
		// 2) discard the days that does NOT contain any available slots from the beginning of the range
		//    and complete the range from the end
		
		int discardedDays = 0;
		do {
			discardedDays = 0;
			
			List<AA14DayTimeSlots> dayTimeSlots = (List<AA14DayTimeSlots>)outRangeAvailableSlots.getDayTimeSlots(); 
			
			// starting at the beginning of the range, check if the day contains slots
			// ... if it does NOT, discard the day
			for (int i=0; i < dayTimeSlots.size(); i++) {
				AA14DayTimeSlots currDayTimeSlots = dayTimeSlots.get(i);
				if (CollectionUtils.isNullOrEmpty(currDayTimeSlots.getTimeSlots())) {		// no available slots at this day
					discardedDays = discardedDays + 1;	
				} else {
					break;
				}
			}
			
			if (discardedDays > 0) {
				log.debug("... slip the date range {} days starting at {}/{}/{} since {} days at the start of the range DOES NOT contains available time slots",
						  discardedDays,
						  rangeStartDate.getYear(),rangeStartDate.getMonthOfYear(),rangeStartDate.getDayOfMonth(),
						  discardedDays);
				// ... remove the discarded days
				for (int i=0; i < discardedDays; i++) {
					AA14DayTimeSlots removedDayTimeSlots = dayTimeSlots.remove(0);
					log.info("\t... discard {}",removedDayTimeSlots.getDate());
				}
				
				// ... add starting at the end of the range: 
				// a) the new range starts at the next day starting at the end of the current range
				rangeStartDate = rangeStartDate.plusDays(numberOfDays);	
				
				// b) check if the future limit is not surpassed
				overTheLimit = _isOverTheFutureLimit(rangeStartDate,initDate,
															 sch.getBookingConfig().getBookingLimit());
				if (overTheLimit) break;	// BEWARE!!!breaks the while!
				
				
				// c) query for more available slots to fill the discarded days
				//    (the query ONLY returns AVAILABLE slots)
				AA14DayRangeTimeSlots moreAvailableSlots = this.availableTimeSlotsForRange(securityContext,
																						   loc,sch,
																						   Year.of(rangeStartDate),MonthOfYear.of(rangeStartDate),DayOfMonth.of(rangeStartDate),
																						   discardedDays);
				// d) complete the out range available slots
				for (AA14DayTimeSlots daySlots : moreAvailableSlots.getDayTimeSlots()) {
					outRangeAvailableSlots.add(daySlots);
				}
				log.info("\t... discarded {} days / added {} days",
						 discardedDays,moreAvailableSlots.getDayTimeSlots().size());
			} 
		} while (discardedDays > 0);
		
		// 3) set if there's more slots available
		if(outRangeAvailableSlots.getTimeSlotsDateRange()!=null) {
			LocalDate rangeEndDate = new LocalDate(outRangeAvailableSlots.getTimeSlotsDateRange().getUpperBound());
			outRangeAvailableSlots.setMoreAvailable(!_isOverTheFutureLimit(rangeEndDate,initDate, 
													  				  sch.getBookingConfig().getBookingLimit()) //we can't fech more days in a next request
												&& outRangeAvailableSlots.getDayTimeSlots().size()==numberOfDays);	//we returned less days than requested because there aren't more available
		
			// 4) return!		
			log.debug("The final available time-slots once slipping the range to the first day with available time-slots is: {}-{}-{} to {}-{}-{}:\n{}",
				  rangeStartDate.getYear(),rangeStartDate.getMonthOfYear(),rangeStartDate.getDayOfMonth(),
				  rangeEndDate.getYear(),rangeEndDate.getMonthOfYear(),rangeEndDate.getDayOfMonth(),
				  outRangeAvailableSlots.debugInfo());
		}
		else {
			// 4) over the future limit to fetch		
			log.debug("The final available time-slots once slipping the range to the first day with available time-slots is EMPTY!!! ");
		}
		return outRangeAvailableSlots;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Checks if a future date is over the days in future limit over which no appointment
	 * can be made
	 * @param future
	 * @param init
	 * @param schBookingLimit
	 * @return
	 */
	protected static boolean _isOverTheFutureLimit(final LocalDate future,final LocalDate init,
										  		   final AA14ScheduleBookingLimit schBookingLimit) {
		if (schBookingLimit == null) return false;
		
		// [1] - Check the days in future limit
		boolean overDaysInFutureLimit = false;
		if (schBookingLimit.getDaysInFutureLimit() <= 0) {
			overDaysInFutureLimit = false;	// NO limit!
		} else {
			LocalDate todayPlusLimit = init.plusDays(schBookingLimit.getDaysInFutureLimit());
			overDaysInFutureLimit = future.isAfter(todayPlusLimit);
			if (overDaysInFutureLimit) log.info("\t... the requested date range starting at {} is OVER the future limit at {}: NO more future ranges will be checked",
						 		  				init,todayPlusLimit);
		}
		
		// [2] - Check the date limit
		boolean overDateLimit=false;
		if (schBookingLimit.getDateLimit() == null) {
			overDateLimit = false;			// NO limit
		} else {
			LocalDate dateLimit = new LocalDate(schBookingLimit.getDateLimit());
			overDateLimit = init.isAfter(dateLimit) || future.isAfter(dateLimit);
			if (overDateLimit) log.info("\t... the requested date range starting at {} is OVER the future limit at {}: NO more future ranges will be checked",
						 		  		init,dateLimit);
		}

		// [3] - Return 
		return overDaysInFutureLimit | overDateLimit;
	}
}
