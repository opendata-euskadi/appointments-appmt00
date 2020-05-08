package aa14f.model.timeslots;

import java.util.Collection;
import java.util.Date;

import org.joda.time.LocalDate;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.patterns.IsBuilder;
import r01f.types.Range;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Time;
import r01f.types.datetime.Year;
import r01f.util.types.collections.CollectionUtils;

/**
 * Builds a {@link AA14DayTimeSlots} object
 * <pre class='brush:java'>
 *	AA14DayRangeTimeSlots dayRangeSlots = AA14TimeSlotsBuilder.dayRangeTimeSlotsBuilder(timeSlotsSizeInMinutes)
 *															  .daySlotsFor(2015,8,19)
 *															  		.addAvailableSlotAt(9,0).withDefaultSize()
 *															  		.addAvailableSlotAt(9,30).withDefaultSize()
 *															  		.addAvailableSlotAt(10,0).withDefaultSize()
 *															  		.addAvailableSlotAt(10,30).withDefaultSize()
 *															  		.end()
 *															  .daySlotsFor(2015,8,19)
 *															  		.addAvailableSlotAt(9,0).withDefaultSize()
 *															  		.addAvailableSlotAt(9,30).withDefaultSize()
 *															  		.addAvailableSlotAt(10,0).withDefaultSize()
 *															  		.addAvailableSlotAt(10,30).withDefaultSize()
 *															  		.end()
 *															  .build();
 * </pre>
 * Another way:
 * <pre class='brush:java'>
 *		// Create an Iterable from range start day to range end day
 *		final DateTime date1 = new DateTime(range.getLowerBound());
 *		final DateTime date2 = new DateTime(range.getUpperBound());
 *	
 *		final int daysBetween = Days.daysBetween(date1.toLocalDate(),
 *										   		 date2.toLocalDate()).getDays();
 *		Iterable<AA14DayTimeSlots> daysIterable = new Iterable<AA14DayTimeSlots>() {
 *														private int _currDay = 0;
 *												
 *														@Override
 *														public Iterator<AA14DayTimeSlots> iterator() {
 *															return new Iterator<AA14DayTimeSlots>() {
 *																			@Override
 *																			public boolean hasNext() {
 *																				return _currDay < daysBetween;
 *																			}
 *																			@Override
 *																			public AA14DayTimeSlots next() {
 *																				DateTime date = date1.plusDays(_currDay);
 *																				_currDay = _currDay + 1;
 *																				AA14DayTimeSlots outDaySlots = new AA14DayTimeSlots(date.getYear(),date.getMonthOfYear(),date.getDayOfMonth());
 *																				outDaySlots.add(AA14TimeSlot.createAvailableAt(9,0));
 *																				outDaySlots.add(AA14TimeSlot.createAvailableAt(9,30));
 *																				outDaySlots.add(AA14TimeSlot.createAvailableAt(10,0));
 *																				outDaySlots.add(AA14TimeSlot.createAvailableAt(10,30));
 *																				
 *																				return outDaySlots;
 *																			}
 *																			@Override
 *																			public void remove() {
 *																				throw new UnsupportedOperationException();
 *																			}
 *																   };
 *														}			
 *									  };
 *		AA14DayRangeTimeSlots dayRangeSlots = AA14TimeSlotsBuilder.dayRangeTimeSlotsBuilder(timeSlotsSizeInMinutes)
 *																  		.daysSlotsFor(daysIterable)
 *																  .build();
 * </pre>
 */
@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class AA14TimeSlotsBuilder
           implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return an instance of the builder
	 */
	public static AA14DayRangeTimeSlotsBuilder dayRangeTimeSlotsBuilder(final int timeSlotsSizeInMinutes) {
		return new AA14TimeSlotsBuilder() { /* nothing */ }
					.new AA14DayRangeTimeSlotsBuilder(timeSlotsSizeInMinutes);
	}
	public static AA14DayTimeSlotsBuilderIndependent dayTimeSlotsBuilder(final Year year,
															  			 final MonthOfYear monthOfYear,
															  			 final DayOfMonth dayOfMonth) {
		return new AA14TimeSlotsBuilder() { /* nothing */ }
						.new AA14DayTimeSlotsBuilderIndependent(year,monthOfYear,dayOfMonth,
										   			 			Lists.<AA14TimeSlot>newArrayList());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class AA14DayRangeTimeSlotsBuilder {
		private final int _timeSlotsSizeInMinutes;
		private final Collection<AA14DayTimeSlots> _daysSlots = Lists.newArrayList();
		
		public AA14DayTimeSlotsBuilder daySlotsFor(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth) {
			return new AA14DayTimeSlotsBuilder(year,monthOfYear,dayOfMonth,
											   Lists.<AA14TimeSlot>newArrayList());
		}
		public AA14DayRangeTimeSlotsBuilder daysSlotsFor(final Iterable<AA14DayTimeSlots> days) {
			for (AA14DayTimeSlots day : days) {
				_daysSlots.add(day);
			}
			return this;
		}
		public AA14DayRangeTimeSlots build() {
			// Ensure the slots are ordered
			Collection<AA14DayTimeSlots> orderedDays = ImmutableList.copyOf(new Ordering<AA14DayTimeSlots>() {
																						@Override
																						public int compare(final AA14DayTimeSlots left,final AA14DayTimeSlots right) {
																							return left.compareTo(right);
																						}
																			 }.sortedCopy(_daysSlots));
			// get the range
			Range<Date> requestedRange = null; 
			if (CollectionUtils.hasData(orderedDays)) {
				AA14DayTimeSlots first = FluentIterable.from(orderedDays).first().orNull();
				AA14DayTimeSlots last = FluentIterable.from(orderedDays).last().orNull();
				LocalDate requestedStartDate = new LocalDate(first.getYear().asInteger(),
															 first.getMonthOfYear().asInteger(),
															 first.getDayOfMonth().asInteger());
				LocalDate requestedEndDate = new LocalDate(last.getYear().asInteger(),
														   last.getMonthOfYear().asInteger(),
														   last.getDayOfMonth().asInteger());
				requestedRange = Range.closed(requestedStartDate.toDateTimeAtStartOfDay().toDate(),
											  requestedEndDate.toDateTimeAtStartOfDay().plusHours(24).toDate());
			}
			return new AA14DayRangeTimeSlots(_timeSlotsSizeInMinutes,
											 requestedRange,
											 orderedDays);
		}
		public AA14DayRangeTimeSlots build(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
									 	   final int numberOfDays) {
			// Ensure the slots are ordered
			Collection<AA14DayTimeSlots> orderedDays = ImmutableList.copyOf(new Ordering<AA14DayTimeSlots>() {
																						@Override
																						public int compare(final AA14DayTimeSlots left,final AA14DayTimeSlots right) {
																							return left.compareTo(right);
																						}
																			 }.sortedCopy(_daysSlots));
			return new AA14DayRangeTimeSlots(_timeSlotsSizeInMinutes,
											 year,monthOfYear,dayOfMonth,
											 numberOfDays,
											 orderedDays);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public abstract class AA14DayTimeSlotsBuilderBase<SELF_TYPE extends AA14DayTimeSlotsBuilderBase<SELF_TYPE>> {
		protected final Year _year;
		protected final MonthOfYear _monthOfYear;
		protected final DayOfMonth _dayOfMonth;		
		protected final Collection<AA14TimeSlot> _slots;
		
		@SuppressWarnings("unchecked")
		public SELF_TYPE addSlot(final AA14TimeSlot slot) {
			_slots.add(slot);
			return (SELF_TYPE)this;
		}
		@SuppressWarnings("unchecked")
		public SELF_TYPE addSlots(final AA14TimeSlot... slots) {
			if (CollectionUtils.hasData(slots)) {
				for(AA14TimeSlot slot : slots) {
					this.addSlot(slot);
				}
			}
			return (SELF_TYPE)this;
		}
		@SuppressWarnings("unchecked")
		public SELF_TYPE addSlots(final Iterable<AA14TimeSlot> slots) {
			for (AA14TimeSlot slot : slots) this.addSlot(slot);
			return (SELF_TYPE)this;
		}
		@SuppressWarnings("unchecked")
		public SELF_TYPE addSlots(final Observable<AA14TimeSlot> slotsObservable) {
			slotsObservable.subscribe(new Observer<AA14TimeSlot>() {
											@Override
											public void onNext(final AA14TimeSlot slot) {
												AA14DayTimeSlotsBuilderBase.this.addSlot(slot);
											}
											@Override
											public void onError(final Throwable e) {
												log.error("Error {}",e.getMessage(),e);
											}
											@Override
											public void onSubscribe(final Disposable subs) {
												// subscribed!
											}
											@Override
											public void onComplete() {
												// completed!
											}
									  });
			return (SELF_TYPE)this;
		}
		public AA14DayTimeSlotsBuilderScheduleStep addAvailableSlotAt(final Time time) {
			return this.addAvailableSlotAt(HourOfDay.of(time.getHourOfDay()),MinuteOfHour.of(time.getMinuteOfHour()));
		}
		public AA14DayTimeSlotsBuilderScheduleStep addAvailableSlotAt(final HourOfDay hourOfDay,final MinuteOfHour minuteOfHour) {
			return new AA14DayTimeSlotsBuilderScheduleStep(hourOfDay,minuteOfHour,
													   	   true);	// available
		}
		public AA14DayTimeSlotsBuilderScheduleStep addNotAvailableSlotAt(final HourOfDay hourOfDay,final MinuteOfHour minuteOfHour) {
			return new AA14DayTimeSlotsBuilderScheduleStep(hourOfDay,minuteOfHour,
													   	   false);	// not available			
		}
		
		// ----
		@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
		public class AA14DayTimeSlotsBuilderScheduleStep {
			private final HourOfDay _hourOfDay;
			private final MinuteOfHour _minuteOfHour;
			private final boolean _available;
			
			public AA14DayTimeSlotsBuilderSlotStep forSchedule(final AA14ScheduleOID schOid) {
				return new AA14DayTimeSlotsBuilderSlotStep(schOid,
														   _hourOfDay,_minuteOfHour,
														   _available);
			}
		}
		// ----
		@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
		public class AA14DayTimeSlotsBuilderSlotStep {
			private final AA14ScheduleOID _schOid;
			private final HourOfDay _hourOfDay;
			private final MinuteOfHour _minuteOfHour;
			private final boolean _available;
			
			public SELF_TYPE withSizeInMinutes(final int sizeInMinutes) {
				return AA14DayTimeSlotsBuilderBase.this.addSlot(new AA14TimeSlot(_schOid,
																				 _hourOfDay,_minuteOfHour,
										    			  		 			 	 sizeInMinutes,
										    			  		 			 	 _available));
			}
			public SELF_TYPE withDefaultSize() {
				return AA14DayTimeSlotsBuilderBase.this.addSlot(new AA14TimeSlot(_schOid,
																				 _hourOfDay,_minuteOfHour,
															     			 	 _available));
			}
		}
	}
	public class AA14DayTimeSlotsBuilder
		  extends AA14DayTimeSlotsBuilderBase<AA14DayTimeSlotsBuilder> {
		
		private AA14DayTimeSlotsBuilder(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
									    final Collection<AA14TimeSlot> slots) {
			super(year,monthOfYear,dayOfMonth,
				  slots);
		}
		public AA14DayTimeSlots end() {
			// Ensure the slots are ordered
			Collection<AA14TimeSlot> orderedSlots = ImmutableList.copyOf(new Ordering<AA14TimeSlot>() {
																					@Override
																					public int compare(final AA14TimeSlot left,final AA14TimeSlot right) {
																						return left.compareTo(right);
																					}
																		 }.sortedCopy(_slots));
			return new AA14DayTimeSlots(_year,_monthOfYear,_dayOfMonth,
									    orderedSlots);
		}
	}
	public class AA14DayTimeSlotsBuilderIndependent
		  extends AA14DayTimeSlotsBuilderBase<AA14DayTimeSlotsBuilderIndependent> {
		private AA14DayTimeSlotsBuilderIndependent(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
									   			   final Collection<AA14TimeSlot> slots) {
			super(year,monthOfYear,dayOfMonth,
				  slots);
		}
		public AA14DayTimeSlots build() {
			// Ensure the slots are ordered
			Collection<AA14TimeSlot> orderedSlots = ImmutableList.copyOf(new Ordering<AA14TimeSlot>() {
																					@Override
																					public int compare(final AA14TimeSlot left,final AA14TimeSlot right) {
																						return left.compareTo(right);
																					}
																		 }.sortedCopy(_slots));
			return new AA14DayTimeSlots(_year,_monthOfYear,_dayOfMonth,
										orderedSlots);
		}
	}
}
