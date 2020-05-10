package aa14f.model;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import com.google.common.collect.Range;

import aa14f.model.config.AA14ScheduleBookingConfig;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;
import r01f.validation.ObjectValidationResult;
import r01f.validation.SelfValidates;

@ConvertToDirtyStateTrackable			// changes in state are tracked
@Accessors(prefix="_")
public abstract class AA14BookedSlotBase<SELF_TYPE extends AA14BookedSlotBase<SELF_TYPE>>
              extends AA14EntityModelObjectBase<AA14SlotOID,AA14SlotID,
     								            SELF_TYPE>
           implements AA14BookedSlot,
           			  SelfValidates<SELF_TYPE> {

	private static final long serialVersionUID = 2981113515733558022L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Range<Integer> DURATION_MINUTES_RANGE = Range.closed(10,60*24);
	
/////////////////////////////////////////////////////////////////////////////////////////
//  TYPE
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="type")
	@Getter @Setter protected AA14BookedSlotType _type;
/////////////////////////////////////////////////////////////////////////////////////////
//  TARGET SERVICE LOCATION & SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="scheduleOid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected AA14ScheduleOID _scheduleOid;
	
	@MarshallField(as="locationOid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected AA14OrgDivisionServiceLocationOID _orgDivisionServiceLocationOid;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  DATE / TIME
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="year",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected Year _year;

	@MarshallField(as="monthOfYear",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected MonthOfYear _monthOfYear;
	
	@MarshallField(as="dayOfMonth",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected DayOfMonth _dayOfMonth;
	
	@MarshallField(as="hourOfDay",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected HourOfDay _hourOfDay;
	
	@MarshallField(as="minuteOfHour",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected MinuteOfHour _minuteOfHour;
	
	@MarshallField(as="durationMinutes",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected int _durationMinutes = AA14ScheduleBookingConfig.DEFAULT_SLOT_DURATION_IN_MINUTES;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  PERIODIC SLOT SERIAL OID
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * When a non bookable slot is a periodic one, all the individual non-bookable slots
	 * are related by a serie oid
	 */
	@MarshallField(as="periodicSlotData")
	@Getter @Setter protected AA14PeriodicSlotData _periodicSlotData;

/////////////////////////////////////////////////////////////////////////////////////////
//  OTHER
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallField(as="numberOfAdjacentSlots",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AA14NumberOfAdjacentSlots _numberOfAdjacentSlots = AA14NumberOfAdjacentSlots.ONE;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	protected AA14BookedSlotBase(final AA14BookedSlotType type) {
		_type = type;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DATE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setDate(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth) {
		_year = year;
		_monthOfYear = monthOfYear;
		_dayOfMonth = dayOfMonth;
	}
	@Override
	public void setDate(final Date date) {
		LocalDate lDate = new LocalDate(date);
		_year = Year.of(lDate);
		_monthOfYear = MonthOfYear.of(lDate);
		_dayOfMonth = DayOfMonth.of(lDate);
	}
	@Override
	public Date getStartDate() {
		DateTime start = new DateTime(_year.getYear(),_monthOfYear.getMonthOfYear(),_dayOfMonth.getDayOfMonth(),
									  _hourOfDay.getHourOfDay(),_minuteOfHour.getMinuteOfHour());
		return start.toDate();
	}	
	@Override
	public Date getEndDate() {
		DateTime end = new DateTime(this.getStartDate()).plusMinutes(_durationMinutes * _numberOfAdjacentSlots.getValue());
		return end.toDate();
	}
	@Override
	public Range<Date> getDateRange() {
		return Range.closed(this.getStartDate(),
							this.getEndDate());
	}
	@Override
	public LocalTime getStartTime() {
		return LocalDateTime.fromDateFields(this.getStartDate())
							.toLocalTime();
	}
	@Override
	public LocalTime getEndTime() {
		return LocalDateTime.fromDateFields(this.getEndDate())
							.toLocalTime();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isPeriodic() {
		return _periodicSlotData != null 
			&& _periodicSlotData.getSerieOid() != null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  VALIDATION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if the appointment is valid
	 */
	@Override @SuppressWarnings("unchecked")
	public ObjectValidationResult<SELF_TYPE> validate() {
		Object bookedSlotValidators = AA14BookedSlotValidators.createSlotBaseValidator()
															  .validate(this);
		return (ObjectValidationResult<SELF_TYPE>) bookedSlotValidators;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public <S extends AA14BookedSlot> S as(final Class<S> type) {
		return (S)this;
	}
	@Override
	public boolean isAppointment() {
		return this.getClass() == AA14Appointment.class;
	}
	@Override
	public boolean isNonBookableSlot() {
		return this.getClass() == AA14NonBookableSlot.class;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return this.getDateRange().toString();
	}
	
}
