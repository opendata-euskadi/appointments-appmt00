package aa14f.model.timeslots;

import java.util.Comparator;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import com.google.common.base.Objects;

import aa14f.model.AA14ModelObject;
import aa14f.model.config.AA14ScheduleBookingConfig;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.Range;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;

/**
 * Models a time slot available or not for an appointment
 */
@MarshallType(as="timeSlot")
@Accessors(prefix="_")
public class AA14TimeSlot
  implements AA14ModelObject,
  			 Comparable<AA14TimeSlot>,
  			 Debuggable {

	private static final long serialVersionUID = -7401408629138589904L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="schedule",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final AA14ScheduleOID _scheduleOid;
	
	@MarshallField(as="hourOfDay",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final HourOfDay _hourOfDay;
	
	@MarshallField(as="minuteOfHour",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final MinuteOfHour _minuteOfHour;
	
	@MarshallField(as="sizeInMinutes",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final int _sizeInMinutes;
	
	@MarshallField(as="available",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private final boolean _available;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14TimeSlot(final AA14ScheduleOID scheduleOid,
						final HourOfDay hourOfDay,final MinuteOfHour minuteOfHour,
						final Integer sizeInMinutes,
						final Boolean available) {
		// since all constructor's params are primitive types, a constructor 
		// with the equivalent object types is needed in order to find the
		// constructor using reflection
		this(scheduleOid,
			 hourOfDay,minuteOfHour,
			 sizeInMinutes.intValue(),
			 available.booleanValue());
	}
	public AA14TimeSlot(final AA14ScheduleOID scheduleOid,
						final HourOfDay hourOfDay,final MinuteOfHour minuteOfHour,
						final int sizeInMinutes,
						final boolean available) {
		_scheduleOid = scheduleOid;
		_hourOfDay = hourOfDay;
		_minuteOfHour = minuteOfHour;
		_sizeInMinutes = sizeInMinutes;
		_available = available;
	}
	public AA14TimeSlot(final AA14ScheduleOID scheduleOid,
						final HourOfDay hourOfDay,final MinuteOfHour minuteOfHour,
						final boolean available) {
		this(scheduleOid,
			 hourOfDay,minuteOfHour,
			 AA14ScheduleBookingConfig.DEFAULT_SLOT_DURATION_IN_MINUTES,
			 available);
	}
	public static AA14TimeSlot createAvailableAt(final AA14ScheduleOID scheduleOid,
												 final int hourOfDay,final int minuteOfHour,
												 final int sizeInMinutes) {
		return AA14TimeSlot.createAvailableAt(scheduleOid,
											  HourOfDay.of(hourOfDay),MinuteOfHour.of(minuteOfHour),
											  sizeInMinutes);
	}
	public static AA14TimeSlot createAvailableAt(final AA14ScheduleOID scheduleOid,
												 final HourOfDay hourOfDay,final MinuteOfHour minuteOfHour,
												 final int sizeInMinutes) {
		return new AA14TimeSlot(scheduleOid,
								hourOfDay,minuteOfHour,
								sizeInMinutes,
								true);
	}
	public static AA14TimeSlot createNotAvailableAt(final AA14ScheduleOID scheduleOid,
													final int hourOfDay,final int minuteOfHour,
													final int sizeInMinutes) {
		return AA14TimeSlot.createNotAvailableAt(scheduleOid,
											  	 HourOfDay.of(hourOfDay),MinuteOfHour.of(minuteOfHour),
											  	 sizeInMinutes);
	}
	public static AA14TimeSlot createNotAvailableAt(final AA14ScheduleOID scheduleOid,
													final HourOfDay hourOfDay,final MinuteOfHour minuteOfHour,
													final int sizeInMinutes) {
		return new AA14TimeSlot(scheduleOid,
								hourOfDay,minuteOfHour,
								sizeInMinutes,
								false);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a {@link Range} of {@link Date}s for the {@link AA14TimeSlot}
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Range<Date> getDateRangeAt(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth) {
		DateTime startDate = new DateTime(year.getYear(),monthOfYear.getMonthOfYear(),dayOfMonth.getDayOfMonth(),
										  _hourOfDay.getHourOfDay(),_minuteOfHour.getMinuteOfHour());
		DateTime endDate = startDate.plusMinutes(_sizeInMinutes);
		return Range.closed(startDate.toDate(),
							endDate.toDate());
	}
	/**
	 * Checks if this slot starts exactly at the provided hour
	 * @param hourOfDay
	 * @param minuteOfHour
	 * @return
	 */
	public boolean startsAt(final HourOfDay hourOfDay,final MinuteOfHour minuteOfHour) {
		return _hourOfDay.equals(hourOfDay) && _minuteOfHour.equals(minuteOfHour);
	}
	public LocalTime getStartTime() {
		return new LocalTime(_hourOfDay.getHourOfDay(),_minuteOfHour.getMinuteOfHour());
	}
	public LocalTime getEndTime() {
		return this.getStartTime().plusMinutes(_sizeInMinutes);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  COMPARABLE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int compareTo(final AA14TimeSlot other) {
		return new AA14TimeSlotComparator().compare(this,other);
	}
	private class AA14TimeSlotComparator
	   implements Comparator<AA14TimeSlot> {
		@Override
		public int compare(final AA14TimeSlot slot1,final AA14TimeSlot slot2) {
			// slot1 < slot2 ==> return -1
			// slot1 == slot2 => return 0
			// slot1 > slot2 ==> return 1
			if (slot1.getHourOfDay().isBefore(slot2.getHourOfDay())) return -1;
			if (slot1.getHourOfDay().isAfter(slot2.getHourOfDay())) return 1;
			if (slot1.getHourOfDay().equals(slot2.getHourOfDay())) {
				if (slot1.getMinuteOfHour().isBefore(slot2.getMinuteOfHour())) return -1;
				if (slot1.getMinuteOfHour().isAfter(slot2.getMinuteOfHour())) return 1;
				if (slot1.getMinuteOfHour().equals(slot2.getMinuteOfHour())) return 0;
			}
			return 0;
		}
	}
	/**
	 * Returns true if the given timeslot is adjacent (is the next timeslot)
	 * <pre>
	 * Given that this slot is [08:00 - 08:30]
	 * 
	 * 		08:00 - 08:30	<- this slot
	 * 		08:30 - 09:00	<- isAdjacent = true
	 * 
	 * 		08:00 - 08:30	<- this slot
	 * 		08:30 - 09:00
	 * 		09:00 - 09:30	<- isAdjacent = false
	 * </pre>
	 * @param other
	 * @return
	 */
	public boolean isAdjacent(final AA14TimeSlot other) {
		LocalTime endTime = this.getEndTime();
		LocalTime otherStartTime = other.getStartTime();
		return endTime.getHourOfDay() == otherStartTime.getHourOfDay()
			&& endTime.getMinuteOfHour() == otherStartTime.getMinuteOfHour();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
//	public boolean overlapsWith(final AA14TimeSlot otherTS) {
//		LocalTime thisStartTime = new LocalTime(_hourOfDay,_minuteOfHour,0);
//		LocalTime thisEndTime = thisStartTime.plusMinutes(_sizeInMinutes);
//		
//		LocalTime otherStartTime = new LocalTime(otherTS.getHourOfDay(),otherTS.getMinuteOfHour(),0);
//		LocalTime otherEndTime = otherStartTime.plusMinutes(otherTS.getSizeInMinutes());
//				
//		LocalTime aStart = thisStartTime.isBefore(otherEndTime) ? thisStartTime : otherEndTime;
//		LocalTime aEnd = thisStartTime.isBefore(otherEndTime) ? thisEndTime : otherEndTime;
//		
//		LocalTime bStart = aStart == thisStartTime ? otherStartTime : thisStartTime; 
//		LocalTime bEnd = aEnd == thisEndTime ? otherEndTime : thisEndTime;		
//		
//		System.out.println("====>" + bStart + " > " + aEnd);
//		return // [----------------]
//			   //    				 [---------]
//			   (bStart.isAfter(aEnd));
//	}
	public boolean is(final AA14TimeSlot otherTS) {
		if (otherTS == null) return false;
		if (_scheduleOid != null && !_scheduleOid.is(otherTS.getScheduleOid())) return false;
		if (!this.isSameRangeAs(otherTS)) return false;
		if (_available != otherTS.isAvailable()) return false;
		return true;
	}
	public boolean isSameRangeAs(final AA14TimeSlot otherTS) {
		if (_hourOfDay != otherTS.getHourOfDay()) return false;
		if (_minuteOfHour != otherTS.getMinuteOfHour()) return false;
		if (_sizeInMinutes != otherTS.getSizeInMinutes()) return false;
		return true;
	}
	@Override
	public boolean equals(final Object other) {
		if (other == null) return false;
		if (!(other instanceof AA14TimeSlot)) return false;
		
		AA14TimeSlot otherTS = (AA14TimeSlot)other;
		return this.is(otherTS);
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_scheduleOid,
						 		_hourOfDay.asInteger(),_minuteOfHour.asInteger(),
						 		_sizeInMinutes,
						 		_available);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder()
									.append(String.format("%s > %s:%s-%s minutes",_scheduleOid,
											String.format("%02d",_hourOfDay.getHourOfDay()),String.format("%02d",_minuteOfHour.getMinuteOfHour()),_sizeInMinutes));
		if (!_available) sb.append(" (*)");
		return sb;
	}
	public String debugTimeRange() {
		return String.format("%s:%s - %s:%s",
							 String.format("%02d",this.getStartTime().getHourOfDay()),String.format("%02d",this.getStartTime().getMinuteOfHour()),
							 String.format("%02d",this.getEndTime().getHourOfDay()),String.format("%02d",this.getEndTime().getMinuteOfHour()));
	}
}
