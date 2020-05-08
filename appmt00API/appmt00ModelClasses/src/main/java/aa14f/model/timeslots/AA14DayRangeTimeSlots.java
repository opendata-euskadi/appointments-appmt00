package aa14f.model.timeslots;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.joda.time.LocalDate;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import aa14f.model.AA14ModelObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.DateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallCollectionField;
import r01f.objectstreamer.annotations.MarshallField.MarshallDateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.Range;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;
import r01f.util.types.collections.CollectionUtils;

/**
 * Contains {@link AA14DayTimeSlots} for some days
 * @see AA14TimeSlotsBuilder
 */
@MarshallType(as="dayRangeTimeSlots")
@Accessors(prefix="_")
public class AA14DayRangeTimeSlots 
  implements AA14ModelObject,
  			 Iterable<AA14DayTimeSlots>,
  			 Debuggable {

	private static final long serialVersionUID = 6952166159819432079L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The requested start date for the time slots
	 */
	@MarshallField(as="requestedDateRange",dateFormat=@MarshallDateFormat(use=DateFormat.ISO8601))
	@Getter @Setter private Range<Date> _requestedDateRange;
	/**
	 * The slots size in minutes
	 */
	@MarshallField(as="timeSlotsSizeInMinutes",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private int _timeSlotsSizeInMinutes;
	/**
	 * The slots
	 */
	@MarshallField(as="dayTimeSlots",
				   whenCollectionLike=@MarshallCollectionField(useWrapping=false))
	@Getter @Setter private Collection<AA14DayTimeSlots> _dayTimeSlots;
	/**
	 * Are there more slots?
	 */
	@MarshallField(as="moreAvailable",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private boolean _moreAvailable;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DayRangeTimeSlots() {
		_dayTimeSlots = Lists.newArrayList();
	}
	public AA14DayRangeTimeSlots(final int timeSlotsSizeInMinutes,
								 final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
				   			  	 final int reqNumberOfDays) {
		this();
		_timeSlotsSizeInMinutes = timeSlotsSizeInMinutes;
		LocalDate requestedStartDate = new LocalDate(year.getYear(),monthOfYear.getMonthOfYear(),dayOfMonth.getDayOfMonth());
		LocalDate requestedEndDate = requestedStartDate.plusDays(reqNumberOfDays);
		_requestedDateRange = Range.closed(requestedStartDate.toDate(),
										   requestedEndDate.toDate());
	}
	public AA14DayRangeTimeSlots(final int timeSlotsSizeInMinutes,
								 final Range<Date> requestedDateRange,
								 final Collection<AA14DayTimeSlots> dayTimeSlots) {
		_timeSlotsSizeInMinutes = timeSlotsSizeInMinutes;
		_requestedDateRange = requestedDateRange;
		_dayTimeSlots = dayTimeSlots;
	}
	public AA14DayRangeTimeSlots(final int timeSlotsSizeInMinutes,
								 final Range<Date> requestedDateRange,
								 final Collection<AA14DayTimeSlots> dayTimeSlots,
								 final boolean moreAvailable) {
		_timeSlotsSizeInMinutes = timeSlotsSizeInMinutes;
		_requestedDateRange = requestedDateRange;
		_dayTimeSlots = dayTimeSlots;
		_moreAvailable = moreAvailable;
	}
	public AA14DayRangeTimeSlots(final int timeSlotsSizeInMinutes,
								 final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
				   			  	 final int reqNumberOfDays,
				   			  	 final Collection<AA14DayTimeSlots> dayTimeSlots) {
		this(timeSlotsSizeInMinutes,
			 year,monthOfYear,dayOfMonth,
			 reqNumberOfDays);
		_dayTimeSlots = dayTimeSlots;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Adds a {@link AA14DayTimeSlots}
	 * @param daySlots
	 * @return
	 */
	public AA14DayRangeTimeSlots add(final AA14DayTimeSlots daySlots) {
		_dayTimeSlots.add(daySlots);
		return this;
	}
	public AA14DayRangeTimeSlots add(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
									 final AA14TimeSlot slot) {
		// find an existing day time slots or create a new one
		AA14DayTimeSlots dayTimeSlots = this.dayTimeSlotsFor(year,monthOfYear,dayOfMonth);
		if (dayTimeSlots == null) {
			dayTimeSlots = new AA14DayTimeSlots(year,monthOfYear,dayOfMonth);
		}
		// add the new slot
		dayTimeSlots.add(slot);
		return this;
	}
	/**
	 * Finds the day time slots for a given day
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public AA14DayTimeSlots dayTimeSlotsFor(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth) {
		AA14DayTimeSlots outSlots = null;
		for (AA14DayTimeSlots daySlots : _dayTimeSlots) {
			if (daySlots.isAt(year,monthOfYear,dayOfMonth)) {
				outSlots = daySlots;
				break;
			}
		}
		return outSlots;
	}
	/**
	 * @return the date range at which the time slots belong
	 */
	public Range<Date> getTimeSlotsDateRange() {
		if (CollectionUtils.isNullOrEmpty(_dayTimeSlots)) return null;
		Date startOfRange = FluentIterable.from(_dayTimeSlots).first().orNull()
															  .getDate();
		Date endOfRange = FluentIterable.from(_dayTimeSlots).last().orNull()
															.getDate();
		return Range.closed(startOfRange,endOfRange);
	}
	/**
	 * @return true if the returned date range that contains the time-slots is OUTSIDE
	 * 				the requested date-range
	 */
	public boolean isTheDateRangeThatContainsTheReturnedTimeSlotsOutsideTheRequestedDateRange() {
		// the lower bound of the date range that contains the returned time-slots
		// is AFTER the upper bound of the requested date range
		return this.getTimeSlotsDateRange().getLowerBound().after(this.getRequestedDateRange().getUpperBound());
	}
	public boolean isTheDateRangeThatContainsTheReturnedTimeSlotsInsideTheRequestedDateRange() {
		return !this.isTheDateRangeThatContainsTheReturnedTimeSlotsOutsideTheRequestedDateRange();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasDayTimeSlots() {
		return CollectionUtils.hasData(_dayTimeSlots);
	}
	@Override
	public Iterator<AA14DayTimeSlots> iterator() {
		return _dayTimeSlots.iterator();
	}
	@Override
	public CharSequence debugInfo() {
		StringBuffer sb = new StringBuffer(_dayTimeSlots.size() * 100);
		for (Iterator<AA14DayTimeSlots> dayIt = _dayTimeSlots.iterator(); dayIt.hasNext(); ) {
			AA14DayTimeSlots day = dayIt.next();
			sb.append(day.debugInfo());
			if (dayIt.hasNext()) sb.append("\n");
		}
		return sb.toString();
	}
}
