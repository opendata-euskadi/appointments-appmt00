package aa14b.calendar;

import java.util.Collection;
import java.util.Date;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import aa14f.api.interfaces.AA14FindServicesForBookedSlot;
import aa14f.model.AA14BookedSlot;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.AA14ScheduleBookingLimit;
import aa14f.model.timeslots.AA14DayRangeTimeSlots;
import aa14f.model.timeslots.AA14DayTimeSlots;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.model.persistence.FindResult;
import r01f.securitycontext.SecurityContext;
import r01f.types.Range;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;

/**
 * Finds available time slots using db stored data
 */
@Slf4j
@RequiredArgsConstructor
public class AA14CalendarAvailableTimeSlotsDBFindDelegate
     extends AA14CalendarAvailableTimeSlotsFindDelegateBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14FindServicesForBookedSlot _slotFind;
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AA14DayRangeTimeSlots availableTimeSlotsForRange(final SecurityContext securityContext,
												   			final AA14OrgDivisionServiceLocation loc,final AA14Schedule sch,
												   			final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   			final int numberOfDays) {
		// 1) Create a stream of day-available slots
		Observable<AA14DayTimeSlots> rangeAvailableSlots = _createDateRangeAvailableSlotsStream(securityContext,
																							    loc,sch,
																							    year,monthOfYear,dayOfMonth,
																							    numberOfDays);
		// 2) build the AA14DayRangeTimeSlots
		int slotsSizeInMinutes = sch.getBookingConfig().getSlotDefaultLengthMinutes();

		final AA14DayRangeTimeSlots outRangeAvailableSlots = new AA14DayRangeTimeSlots(slotsSizeInMinutes,
																					   year,monthOfYear,dayOfMonth,
																					   numberOfDays);
		rangeAvailableSlots.subscribe(new Observer<AA14DayTimeSlots>() {
												@Override
												public void onSubscribe(final Disposable d) {
													// subscribed
												}
												@Override
												public void onComplete() {
													// finsh
												}
												@Override
												public void onNext(final AA14DayTimeSlots thisDaySlots) {
													// add the collection of AA14DayTimeSlots to the out AA14DayRangeTimeSlots
													outRangeAvailableSlots.add(thisDaySlots);
												}
												@Override
												public void onError(final Throwable th) {
													th.printStackTrace(System.out);
												}
									  });
		// 3) return
		// set if there's more & less slots available
		LocalDate initDate = new LocalDate(year.getYear(),monthOfYear.getMonthOfYear(),dayOfMonth.getDayOfMonth());
		LocalDate nextDayDate = new LocalDate(outRangeAvailableSlots.getRequestedDateRange().getUpperBound()).plusDays(1);
		outRangeAvailableSlots.setMoreAvailable(!_isOverTheFutureLimit(nextDayDate,initDate, 
													  				  sch.getBookingConfig().getBookingLimit()));		
		
		return outRangeAvailableSlots;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates an stream of day-available slots
	 * @param securityContext
	 * @param loc
	 * @param sch
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @param numberOfDays
	 * @return
	 */
	protected Observable<AA14DayTimeSlots> _createDateRangeAvailableSlotsStream(final SecurityContext securityContext,
																			    final AA14OrgDivisionServiceLocation loc,final AA14Schedule sch,
												   							    final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   							    final int numberOfDays) {
		log.info("Retrieving the bookable slots at locationId={}/scheduleId={} ({} days from {}/{}/{}) from DDBB",
				 loc.getId(),sch.getId(),
				 numberOfDays,
				 year,monthOfYear,dayOfMonth);
		
		// Get a date range BEWARE the limit over which an appointment cannot be booked
		int realNumberOfDaysInRange = _daysInRange(year,monthOfYear,dayOfMonth,
												   numberOfDays,
												   sch.getBookingConfig().getBookingLimit());
		LocalDate startDate = new LocalDate(year.getYear(),monthOfYear.getMonthOfYear(),dayOfMonth.getDayOfMonth());
		final Range<Date> dateRange = Range.closed(startDate.toDate(),
											 	   startDate.plusDays(realNumberOfDaysInRange).toDate());
		
		// use the db stored data to get the available slots
		// 1) get the NON-AVAILABLE slots by schedule
		FindResult<AA14BookedSlot> bookedSlotsFindResult = _slotFind.findRangeBookedSlotsFor(securityContext,
												 										  	 sch.getOid(),	
												 										  	 dateRange);
		final Collection<AA14BookedSlot> bookedSlots = bookedSlotsFindResult.getOrThrow();
		
		// 2) transform the bookedSlots collection (NON-BOOKABLE slots) into a stream of AVAILABLE slots (a AA14DayTimeSlots flowable)
		log.debug("...composing the slot calendar for dateRange={}",
				  dateRange);
		
		Observable<AA14DayTimeSlots> rangeAvailableSlots 
			= Observable
					  // 2.1 - create an stream of AA14BookedSlot by day: DayBookedSlots
					  //	   (emits a collection of {@link AA14BookedSlot} for each 
					  //		day in the given date range)
					  .create(AA14DayBookedSlots.createObservableOnSubscribe(dateRange,bookedSlots))
					  // 2.2 - transform the stream of DayBookedSlots into an stream of AA14DayTimeSlots
					  //		(transform from booked slots into available slots)
					  .map(AA14DayBookedSlots.createTransformFunction(sch));
		return rangeAvailableSlots;
	}
	/**
	 * Returns the days in the date range having into account the booking date limits
	 * @return
	 */
	private static int _daysInRange(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
							 		final int numberOfDays,
							 		final AA14ScheduleBookingLimit bookingLimit) {
		// BEWARE the limit over which an appointment cannot be booked
		int realNumberOfDaysInRange = numberOfDays;
		LocalDate startDate = new LocalDate(year.getYear(),monthOfYear.getMonthOfYear(),dayOfMonth.getDayOfMonth());
		if (bookingLimit != null) {
			// get the date limit
			LocalDate startDatePlusLimit = bookingLimit.getDaysInFutureLimit() > 0
												? startDate.plusDays(bookingLimit.getDaysInFutureLimit())
												: null;
			LocalDate configuredDateLimit = bookingLimit.getDateLimit() != null
												? new LocalDate(bookingLimit.getDateLimit())
												: null;
			LocalDate dateLimit = null;
			if (startDatePlusLimit != null && configuredDateLimit != null) {
				dateLimit = startDatePlusLimit.isBefore(configuredDateLimit) ? startDatePlusLimit : configuredDateLimit;
			} else if (startDatePlusLimit != null) {
				dateLimit = startDatePlusLimit;
			} else if (configuredDateLimit != null) {
				dateLimit = configuredDateLimit;
			}
			
			if (dateLimit != null 
			 && dateLimit.isAfter(startDate)) {
				int daysToLimit = Days.daysBetween(startDate,dateLimit)
									  .getDays();
				if (daysToLimit < numberOfDays) {
					log.info("...requested avaliable slots for {} days from {} BUT the limit is {} days ahead at {}: only slots for {} days will be returned!",
						     numberOfDays,startDate,
						     daysToLimit,dateLimit,
						     daysToLimit);
					realNumberOfDaysInRange = daysToLimit;
				}
			}
		}
		return realNumberOfDaysInRange;
	}
}
