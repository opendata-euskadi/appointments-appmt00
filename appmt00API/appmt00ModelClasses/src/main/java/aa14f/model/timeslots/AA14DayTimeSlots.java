package aa14f.model.timeslots;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import org.joda.time.LocalDate;

import com.google.common.collect.Lists;

import aa14f.model.AA14ModelObject;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Contains the {@link AA14TimeSlot} for a certain day
 */
@MarshallType(as="dayTimeSlots")
@Accessors(prefix="_")
public class AA14DayTimeSlots
  implements AA14ModelObject,
  			 Iterable<AA14TimeSlot>,
  			 Comparable<AA14DayTimeSlots>,
  			 Debuggable {

	private static final long serialVersionUID = -2496728670833964792L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="year",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Year _year;
	
	@MarshallField(as="monthOfYear",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private MonthOfYear _monthOfYear;
	
	@MarshallField(as="dayOfMonth",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private DayOfMonth _dayOfMonth;
	
	@MarshallField(as="slots",
				   whenXml=@MarshallFieldAsXml(collectionElementName="slot"))
	@Getter @Setter private Collection<AA14TimeSlot> _timeSlots;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DayTimeSlots() {
		this(Lists.<AA14TimeSlot>newArrayList());
	}
	public AA14DayTimeSlots(final Collection<AA14TimeSlot> slots) {
		LocalDate date = new LocalDate(new Date());
		_year = Year.of(date);
		_monthOfYear = MonthOfYear.of(date);
		_dayOfMonth = DayOfMonth.of(date);
		
		_timeSlots = slots;
	}
	public AA14DayTimeSlots(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth) {
		this(year,monthOfYear,dayOfMonth,
			 Lists.<AA14TimeSlot>newArrayList());
	}
	public AA14DayTimeSlots(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
							final Collection<AA14TimeSlot> slots) {
		_year = year;
		_monthOfYear = monthOfYear;
		_dayOfMonth = dayOfMonth;
		
		_timeSlots = slots;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean isAt(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth) {
		return _year == year
			&& _monthOfYear == monthOfYear
			&& _dayOfMonth == dayOfMonth;
	}
	public boolean isAt(final Date date) {
		return new LocalDate(date.getTime()).isEqual(new LocalDate(_year.getYear(),_monthOfYear.getMonthOfYear(),_dayOfMonth.getDayOfMonth()));
	}
	/**
	 * The date
	 * @return
	 */
	public Date getDate() {
		return new LocalDate(_year.getYear(),_monthOfYear.getMonthOfYear(),_dayOfMonth.getDayOfMonth())
						.toDate();
	}
	/**
	 * Adds a {@link AA14TimeSlot}
	 * @param slot
	 * @return
	 */
	public AA14DayTimeSlots add(final AA14TimeSlot slot) {
		// check if the new slot overlaps another existing slot
		for (AA14TimeSlot existingSlot : _timeSlots) {
			if (existingSlot.isSameRangeAs(slot)) throw new IllegalArgumentException("Slot at " + existingSlot.getHourOfDay() + ":" + existingSlot.getMinuteOfHour() + " (" + existingSlot.getSizeInMinutes() + " min) " + 
																				     "overlaps with " + slot.getHourOfDay() + ":" + slot.getMinuteOfHour() + " (" + slot.getSizeInMinutes() + "min )");			
		}
		_timeSlots.add(slot);
		return this;
	}
	public AA14TimeSlot getSlotAt(final HourOfDay hourOfDay,final MinuteOfHour minuteOfHour) {
		AA14TimeSlot outSlot = null;
		if (CollectionUtils.hasData(_timeSlots)) {
			for (AA14TimeSlot slot : this) {
				if (slot.startsAt(hourOfDay,minuteOfHour)) {
					outSlot = slot;
					break;
				}
			}
		}
		return outSlot;
	}
	public boolean containsSlotAt(final HourOfDay hourOfDay,final MinuteOfHour minuteOfHour) {
		return this.getSlotAt(hourOfDay,minuteOfHour) != null;
	}
	public boolean containsSlotAt(final AA14ScheduleOID schOid,
								  final HourOfDay hourOfDay,final MinuteOfHour minuteOfHour) {
		boolean contains = false;
		if (CollectionUtils.hasData(_timeSlots)) {
			for (AA14TimeSlot slot : this) {
				if (slot.getScheduleOid().is(schOid)
				 && slot.startsAt(hourOfDay,minuteOfHour)) {
					contains = true;
					break;
				}
			}
		}
		return contains;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Iterable<AA14TimeSlot>
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Iterator<AA14TimeSlot> iterator() {
		return _timeSlots.iterator();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COMPARABLE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int compareTo(final AA14DayTimeSlots other) {
		return new AA14DayTimeSlotComparator().compare(this,other);
	}	
	private class AA14DayTimeSlotComparator
	   implements Comparator<AA14DayTimeSlots> {
		@Override
		public int compare(final AA14DayTimeSlots day1,final AA14DayTimeSlots day2) {
			// slot1 < slot2 ==> return -1
			// slot1 == slot2 => return 0
			// slot1 > slot2 ==> return 1
			if (day1.getYear().isBefore(day2.getYear())) return -1;
			if (day1.getYear().isAfter(day2.getYear())) return 1;
			if (day1.getYear().equals(day2.getYear())) {
				if (day1.getMonthOfYear().isBefore(day2.getMonthOfYear())) return -1;
				if (day1.getMonthOfYear().isAfter(day2.getMonthOfYear())) return 1;
				if (day1.getMonthOfYear().equals(day2.getMonthOfYear())) {
					if (day1.getDayOfMonth().isBefore(day2.getDayOfMonth())) return -1;
					if (day1.getDayOfMonth().isAfter(day2.getDayOfMonth())) return 1;
					if (day1.getDayOfMonth().equals(day2.getDayOfMonth())) return 0;
				}
			}
			return 0;
		}
		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUGGABLE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder(_timeSlots.size() * 15);
		sb.append(Strings.customized("{}-{}-{}",_year,_monthOfYear,_dayOfMonth));
		sb.append(": {");
		for (Iterator<AA14TimeSlot> slotIt = _timeSlots.iterator(); slotIt.hasNext(); ) {
			AA14TimeSlot slot = slotIt.next();
			sb.append(slot.debugInfo());
			if (slotIt.hasNext()) sb.append(", ");
		}
		sb.append("}");
		return sb;
	}
}
