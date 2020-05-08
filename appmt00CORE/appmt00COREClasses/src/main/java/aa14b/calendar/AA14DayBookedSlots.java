package aa14b.calendar;

import java.util.Collection;
import java.util.Date;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import aa14f.model.AA14BookedSlot;
import aa14f.model.config.AA14Schedule;
import aa14f.model.timeslots.AA14DayTimeSlots;
import aa14f.model.timeslots.AA14TimeSlot;
import aa14f.model.timeslots.AA14TimeSlotsBuilder;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.types.Range;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;


@Slf4j
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
abstract class AA14DayBookedSlots {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Contains the {@link AA14BookedSlot}s ina certain day
     */
    @RequiredArgsConstructor
    static class DayBookedSlots {
        final Year year;
        final MonthOfYear monthOfYear;
        final DayOfMonth dayOfMonth;
        final Collection<AA14BookedSlot> bookedSlots;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Creates a {@link ObservableOnSubscribe} that emits a collection of {@link AA14BookedSlot} for each
     * day in the given date range
     * @param dateRange
     * @param bookedSlots
     * @return
     */
    public static ObservableOnSubscribe<DayBookedSlots> createObservableOnSubscribe(final Range<Date> dateRange,
                                                                                    final Collection<AA14BookedSlot> bookedSlots ) {
        return new ObservableOnSubscribe<DayBookedSlots>() {
                        @Override
                        public void subscribe(final ObservableEmitter<DayBookedSlots> emitter) throws Exception {
                            // group the booked slots by date
                            LocalDate startDate = new LocalDate(dateRange.getLowerBound());
                            LocalDate endDate = new LocalDate(dateRange.getUpperBound());
                            LocalDate currDate = startDate;
                            for (int i=1; i <= Days.daysBetween(startDate,endDate).getDays(); i++) {
                                // within the date-range booked slots, filter the ones at currDate and emit
                                DayBookedSlots thisDayBookedSlots = _dayBookedSlotsFiltering(bookedSlots,
                                                                                             currDate);
                                emitter.onNext(thisDayBookedSlots);	// emit

                                // move to next day
                                currDate = currDate.plusDays(1);
                            }
                            emitter.onComplete();
                        }
                };
    }
    /**
     * Given a db-stored booked slots within a date range this method filters the booked slots at an also given day
     * @param rangeBookedSlots
     * @param date
     * @return
     */
    private static DayBookedSlots _dayBookedSlotsFiltering(final Collection<AA14BookedSlot> rangeBookedSlots,
                                                           final LocalDate date) {
        DayBookedSlots dayBookedSlots = new DayBookedSlots(Year.of(date),MonthOfYear.of(date),DayOfMonth.of(date),
                                                           Lists.<AA14BookedSlot>newArrayList());
        if (CollectionUtils.hasData(rangeBookedSlots)) {
            // filter this day db-stored booked slots (within all booked slots in the given range, filter the ones at this day)
            for (AA14BookedSlot bookedSlot : rangeBookedSlots) {
                LocalDate bookedSlotDate = new LocalDate(bookedSlot.getStartDate());
                if (bookedSlotDate.isEqual(date)) {
                    dayBookedSlots.bookedSlots.add(bookedSlot);
                }
            }
        }
        return dayBookedSlots;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Transforms a {@link DayBookedSlots} object into a {@link AA14DayTimeSlots} object
     */
    public static Function<DayBookedSlots,AA14DayTimeSlots> createTransformFunction(final AA14Schedule sch) {
        return new Function<DayBookedSlots,AA14DayTimeSlots>() {
            @Override
            public AA14DayTimeSlots apply(final DayBookedSlots dayBookedSlots) throws Exception {
                log.debug("\t{}-{}-{}",
                          dayBookedSlots.year,dayBookedSlots.monthOfYear,dayBookedSlots.dayOfMonth);
                return AA14TimeSlotsBuilder.dayTimeSlotsBuilder(dayBookedSlots.year,
                                                                dayBookedSlots.monthOfYear,
                                                                dayBookedSlots.dayOfMonth)
                           .addSlots(// all the day slots (now it's not known if the slot is booked or available)
                        		   
                                // [1] create flowable of bookable time slots
                                // 	(at this moment this flowable only emits time-slots: it's not known if they're available or not)
                                sch.getBookingConfig().getBookableTimeSlots(sch.getOid())

                                // [2] if the slot is contained in one of the day booked slots... it's NOT available
                                // 	... unless there can be multiple appointments at the same slot
                                    .filter(new Predicate<AA14TimeSlot>() {
                                                @Override
                                                public boolean test(final AA14TimeSlot daySlot) throws Exception {
                                                    // if it's saturday or sunday the time slot is NOT available
                                                    LocalDate thisDay = new LocalDate(dayBookedSlots.year.getYear(),
                                                                                      dayBookedSlots.monthOfYear.getMonthOfYear(),
                                                                                      dayBookedSlots.dayOfMonth.getDayOfMonth());
                                                    if (thisDay.getDayOfWeek() == 6		// saturday
                                                     || thisDay.getDayOfWeek() == 7) {	// sunday
                                                        return false;
                                                    }
                                                    // if it's today, the slots before now are NOT available
                                                    LocalDate today = new LocalDate();
                                                    LocalTime now = new LocalTime();
                                                    if ((thisDay.isEqual(today))
                                                     && (now.isAfter(daySlot.getStartTime()))) {
                                                        return false;
                                                    }

                                                    // if ther's no appointments the slot is available
                                                    if (CollectionUtils.isNullOrEmpty(dayBookedSlots.bookedSlots)) return true;

                                                    // ...else try to see if there's an appointment overlapping the slot
                                                    int numAppointmentsInSlot = 0;
                                                    for (AA14BookedSlot thisDayBookedSlot : dayBookedSlots.bookedSlots) {
                                                        // booked slot
                                                        LocalTime  bookedSlotStart = thisDayBookedSlot.getStartTime();
                                                        LocalTime bookedSlotEnd = thisDayBookedSlot.getEndTime();
                                                        // slot
                                                        LocalTime daySlotStart = daySlot.getStartTime();
                                                        LocalTime daySlotEnd = daySlot.getEndTime();

                                                        // see if the slot has a booked slot inside
                                                        //        |================== slot ==================|
                                                        //		  |--- bookedSlot ---|			   					        [0]
                                                        //                                |--- bookedSlot ---|              [1]
                                                        //               |--- bookedSlot ---|                               [2]
                                                        //                                     |------ bookedSlot ------|   [3]
                                                        // |------ bookedSlot------|                                        [4]
                                                        // |----------------------- bookedSlot -------------------------|   [5]

                                                        // TODO multiple events at a slot
                                                        if ((bookedSlotStart.isEqual(daySlotStart))											// [0]
                                                         || (bookedSlotEnd.isEqual(daySlotEnd))												// [1]
                                                         || (bookedSlotStart.isAfter(daySlotStart) && bookedSlotEnd.isBefore(daySlotEnd))   // [2]
                                                         || (bookedSlotStart.isAfter(daySlotStart) && bookedSlotStart.isBefore(daySlotEnd)) // [3]
                                                         || (bookedSlotEnd.isAfter(daySlotStart) && bookedSlotEnd.isBefore(daySlotEnd))     // [4]
                                                         || (bookedSlotStart.isBefore(daySlotStart) && bookedSlotEnd.isAfter(daySlotEnd))) {// [5]
                                                            numAppointmentsInSlot++;
                                                        }
                                                    }
                                                    // check if there's less than the max number of appointments
                                                    int maxAppointmentsInSlot = sch.getBookingConfig().getMaxAppointmentsInSlot();
                                                    boolean isAvailable = maxAppointmentsInSlot > numAppointmentsInSlot;

                                                    log.debug("\t\t...{} - {} available: {}",
                                                                  daySlot.getStartTime(),daySlot.getEndTime(),
                                                                  isAvailable);
                                                        return isAvailable;
                                                    }
                                          }))
                               .build();
                    }
        };	// Function
    }
}
