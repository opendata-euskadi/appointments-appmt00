package aa14f.model.summaries;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import com.google.common.collect.Range;

import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14BookedSlotType;
import aa14f.model.AA14NumberOfAdjacentSlots;
import aa14f.model.AA14PeriodicSlotData;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.types.Color;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;
import r01f.util.types.Dates;
import r01f.util.types.Strings;

@Accessors(prefix="_")
public abstract class AA14SummarizedBookedSlotBase<M extends AA14BookedSlot,
										    	   SELF_TYPE extends AA14SummarizedBookedSlotBase<M,SELF_TYPE>> 
	          extends AA14SummarizedModelObjectBase<AA14SlotOID,AA14SlotID,M,
	 									   	 		SELF_TYPE> {

	private static final long serialVersionUID = -4373243410730886004L;
/////////////////////////////////////////////////////////////////////////////////////////
//  TYPE
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="type",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AA14BookedSlotType _type;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS: ORG
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="organization")
	@Getter @Setter private AA14SummarizedOrganization _organization;

	@MarshallField(as="division")
	@Getter @Setter private AA14SummarizedOrgDivision _division;
	
	@MarshallField(as="service")
	@Getter @Setter private AA14SummarizedOrgDivisionService _service;
	
	@MarshallField(as="location")
	@Getter @Setter private AA14SummarizedOrgDivisionServiceLocation _location;
	
	@MarshallField(as="schedule")
	@Getter @Setter private AA14SummarizedSchedule _schedule;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS: DATE / TIME
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
	
	@MarshallField(as="hourOfDay",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private HourOfDay _hourOfDay;
	
	@MarshallField(as="minuteOfHour",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private MinuteOfHour _minuteOfHour;
	
	@MarshallField(as="durationMinutes",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private int _durationMinutes;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  PERIODIC SLOT SERIAL OID
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * When a booked slot is a periodic one, all the individual slots
	 * are related by a serie oid
	 */
	@MarshallField(as="periodicSlotData",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AA14PeriodicSlotData _periodicSlotData;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  OTHER FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="numberOfAdjacentSlots",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter private AA14NumberOfAdjacentSlots _numberOfAdjacentSlots;
	
	@MarshallField(as="presentationColor",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Color _presentationColor;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14SummarizedBookedSlotBase(final Class<M> modelObjectType,
										final AA14BookedSlotType type) {
		super(modelObjectType);
		_type = type;
		_presentationColor = _colorFrom(type);
		_numberOfAdjacentSlots = AA14NumberOfAdjacentSlots.ONE;
	}
	
	private Color _colorFrom(final AA14BookedSlotType type) {
		switch(type) {
		case APPOINTMENT:
			return Color.from("blue");
		case NON_BOOKABLE:
			return Color.from("red");
		default:
			return Color.from("green");
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14BusinessID getBusinessId() {
		AA14BusinessID outBusinessId = null;
		if (outBusinessId == null && this.getOrganization() != null) outBusinessId = this.getOrganization().getBusinessId();
		if (outBusinessId == null && this.getDivision() != null)	 outBusinessId = this.getDivision().getBusinessId();
		if (outBusinessId == null && this.getService() != null)		 outBusinessId = this.getService().getBusinessId();
		if (outBusinessId == null && this.getLocation() != null)	 outBusinessId = this.getLocation().getBusinessId();
		if (outBusinessId == null && this.getSchedule() != null)	 outBusinessId = this.getSchedule().getBusinessId();
		return outBusinessId;
	}
	public Date getStartDate() {
		DateTime start = new DateTime(_year.getYear(),_monthOfYear.getMonthOfYear(),_dayOfMonth.getDayOfMonth(),
									  _hourOfDay.getHourOfDay(),_minuteOfHour.getMinuteOfHour());
		return start.toDate();
	}
	public Date getEndDate() {
		DateTime end = new DateTime(this.getStartDate()).plusMinutes(_durationMinutes * _numberOfAdjacentSlots.getValue());
		return end.toDate();
	}
	public Range<Date> getDateRange() {
		return Range.closed(this.getStartDate(),
							this.getEndDate());
	}
	public boolean isPeriodic() {
		return _periodicSlotData != null && _periodicSlotData.getSerieOid() != null;
	}
	public LocalTime getStartTime() {
		return new LocalTime(_hourOfDay.getHourOfDay(),_minuteOfHour.getMinuteOfHour());
	}
	public LocalTime getEndTime() {
		int bookedSlotDurationInMinutes=_durationMinutes * _numberOfAdjacentSlots.getValue();
		return this.getStartTime().plusMinutes(bookedSlotDurationInMinutes);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FORMATTED
/////////////////////////////////////////////////////////////////////////////////////////
	public String getStartDateFormatted(final Language lang) {
		return _formatDate(this.getStartDate(),lang);
	}
	public String getEndDateFormatted(final Language lang) {
		return _formatDate(this.getEndDate(),lang);
	}
	private static String _formatDate(final Date date,final Language lang) {
		return lang == Language.SPANISH ? Dates.format(date,"dd/MM/yyyy")
										: Dates.format(date,"yyyy/MM/dd");
	}
	public String getStartTimeFormatted() {
		return _formatTime(this.getStartTime());
	}
	public String getEndTimeFormatted() {
		return _formatTime(this.getEndTime());
	}
	private static String _formatTime(final LocalTime time) {
		return Strings.customized("{}:{}",
								  StringUtils.leftPad(Integer.toString(time.getHourOfDay()),2,'0'),
								  StringUtils.leftPad(Integer.toString(time.getMinuteOfHour()),2,'0'));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT API
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public SELF_TYPE forOrganization(final AA14SummarizedOrganization org) {
		_organization = org;
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE forOrgDivision(final AA14SummarizedOrgDivision div) {
		_division = div;
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE forOrgDivisionService(final AA14SummarizedOrgDivisionService srvc) {
		_service = srvc;
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE forOrgDivisionServiceLocation(final AA14SummarizedOrgDivisionServiceLocation loc) {
		_location = loc;
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE usingSchedule(final AA14SummarizedSchedule sch) {
		_schedule = sch;
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE at(final AA14SummarizedOrganization org,
						final AA14SummarizedOrgDivision div,
						final AA14SummarizedOrgDivisionService srvc,
						final AA14SummarizedOrgDivisionServiceLocation loc,final AA14SummarizedSchedule sch) {
		_organization = org;
		_division = div;
		_service = srvc;
		_location = loc;
		_schedule = sch;
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE day(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth) {
		_year = year;
		_monthOfYear = monthOfYear;
		_dayOfMonth = dayOfMonth;
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE hour(final HourOfDay hourOfDay,final MinuteOfHour minuteOfHour) {
		_hourOfDay = hourOfDay;
		_minuteOfHour = minuteOfHour;
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE duringMinutes(final int minutes) {
		_durationMinutes = minutes;
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE using(final AA14NumberOfAdjacentSlots numberOfAdjacentSlots) {
		_numberOfAdjacentSlots = numberOfAdjacentSlots;
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE presentationColor(final Color color) {
		_presentationColor = color;
		return (SELF_TYPE)this;
	}
}
