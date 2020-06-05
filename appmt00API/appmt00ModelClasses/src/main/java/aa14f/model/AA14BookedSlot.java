package aa14f.model;

import java.util.Date;

import org.joda.time.LocalTime;

import com.google.common.collect.Range;

import aa14f.model.metadata.AA14MetaDataForSlot;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import r01f.debug.Debuggable;
import r01f.facets.Summarizable.HasSummaryFacet;
import r01f.locale.Language;
import r01f.model.metadata.annotations.ModelObjectData;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshalTypeInfoIncludeCase;
import r01f.objectstreamer.annotations.MarshallPolymorphicTypeInfo.MarshallTypeInfoInclude;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;

@MarshallPolymorphicTypeInfo(includeTypeInfo=@MarshallTypeInfoInclude(type=MarshalTypeInfoIncludeCase.ALWAYS))	
@ModelObjectData(AA14MetaDataForSlot.class)
public interface AA14BookedSlot 
         extends AA14EntityModelObject<AA14SlotOID,AA14SlotID>,
 				 HasSummaryFacet,
 				 Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  TYPE
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14BookedSlotType getType();
	public void setType(AA14BookedSlotType type);
/////////////////////////////////////////////////////////////////////////////////////////
//  TARGET SERVICE LOCATION & SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrgDivisionServiceLocationOID getOrgDivisionServiceLocationOid();
	public void setOrgDivisionServiceLocationOid(AA14OrgDivisionServiceLocationOID locOid);
	
	public AA14ScheduleOID getScheduleOid();
	public void setScheduleOid(AA14ScheduleOID oid);
/////////////////////////////////////////////////////////////////////////////////////////
//  DATE / TIME
/////////////////////////////////////////////////////////////////////////////////////////
	public Year getYear();
	public void setYear(Year year);

	public MonthOfYear getMonthOfYear();
	public void setMonthOfYear(MonthOfYear monthOfYear);
	
	public DayOfMonth getDayOfMonth();
	public void setDayOfMonth(DayOfMonth dayOfMonth);
	
	public HourOfDay getHourOfDay();
	public void setHourOfDay(HourOfDay hourOfDay);
	
	public MinuteOfHour getMinuteOfHour();
	public void setMinuteOfHour(MinuteOfHour minutesOfHour);
	
	public int getDurationMinutes();
	public void setDurationMinutes(int minutes);
	
	/**
	 * Sets the appointment date from the year, month of year and day of month
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 */
	public void setDate(Year year,MonthOfYear monthOfYear,DayOfMonth dayOfMonth);
	/**
	 * Sets the appointment date
	 * @param date
	 */
	public void setDate(Date date);
	public Date getStartDate();
	public Date getEndDate();
	public Range<Date> getDateRange();

	public LocalTime getStartTime();
	public LocalTime getEndTime();	
	
	/**
	 * Sets the start date and duratinon from a date-range
	 * @param dateRange
	 */
	public void setDateRange(final Range<Date> dateRange);
/////////////////////////////////////////////////////////////////////////////////////////
//  SUMMARY & SUBJECT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the slot subject in the given language
	 * @param lang
	 * @return
	 */
	public String getSubjectIn(Language lang);
	/**
	 * Returns the slot summary in the given language
	 * @param lang
	 * @return
	 */
	public String getSummaryIn(Language lang);
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14PeriodicSlotData getPeriodicSlotData();
	public void setPeriodicSlotData(final AA14PeriodicSlotData periodicData);
	public boolean isPeriodic();
	
	/**
	 * Returns the number of adjacent slots booked
	 * @return enumerate representing an integer between 1 and 10 
	 */
	public AA14NumberOfAdjacentSlots getNumberOfAdjacentSlots();
	
	/**
	 * Sets the number of adjacent slots booked
	 * @param numberOfAdjacentSlots	enumerate representing an integer between 1 and 10 
	 */
	public void setNumberOfAdjacentSlots (AA14NumberOfAdjacentSlots numberOfAdjacentSlots);
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public <S extends AA14BookedSlot> S as(final Class<S> type);
	public boolean isAppointment();
	public boolean isNonBookableSlot();
}
