package aa14f.client.api.sub.delegates;

import java.util.Collection;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14FindServicesForBookedSlot;
import aa14f.model.AA14Appointment;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14BookedSlotType;
import aa14f.model.AA14NonBookableSlot;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14PeriodicSlotSerieOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import aa14f.model.search.AA14AppointmentFilter;
import aa14f.model.summaries.AA14SummarizedAppointment;
import aa14f.model.summaries.AA14SummarizedBookedSlot;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.model.persistence.FindOIDsResult;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindSummariesResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.types.Range;
import r01f.types.contact.PersonID;
import r01f.util.types.Dates;

@Slf4j
public class AA14ClientAPIDelegateForBookedSlotFindServices
	 extends AA14ClientAPIDelegateForFindServicesBase<AA14SlotOID,AA14SlotID,AA14BookedSlot> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIDelegateForBookedSlotFindServices(final Provider<SecurityContext> securityContextProvider,
														  final Marshaller modelObjectsMarshaller,
														  final AA14FindServicesForBookedSlot findServicesProxy) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  findServicesProxy);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BY SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return all slots in a schedule for a certain day
	 * @param schOid
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Collection<AA14BookedSlot> findDayBookedSlotsFor(final AA14ScheduleOID schOid,
															final int year,final int monthOfYear,final int dayOfMonth) {
		LocalDate day = new LocalDate(year,monthOfYear,dayOfMonth);
		DateTime dayStart = day.toDateTimeAtStartOfDay(DateTimeZone.getDefault());
		DateTime nextDayStart = dayStart.plusDays(1)
										.minusMillis(2);
		Range<Date> dateRange = Range.closed(dayStart.toDate(),nextDayStart.toDate());
		return this.findRangeBookedSlotsFor(schOid,
											dateRange);
	}
	/**
	 * Returns summaries for all slots in a schedule for a certain day
	 * @param lang
	 * @param schOid
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Collection<AA14SummarizedBookedSlot> findDayBookedSlotsSummarizedFor(final Language lang,
																				final AA14ScheduleOID schOid,
																				final int year,final int monthOfYear,final int dayOfMonth) {
		LocalDate day = new LocalDate(year,monthOfYear,dayOfMonth);
		DateTime dayStart = day.toDateTimeAtStartOfDay(DateTimeZone.getDefault());
		DateTime nextDayStart = dayStart.plusDays(1)
										.minusMillis(2);
		Range<Date> dateRange = Range.closed(dayStart.toDate(),nextDayStart.toDate());
		return this.findRangeBookedSlotsSummarizedFor(lang,
													  schOid,
													  dateRange);
	}
	/**
	 * Return all slots in a service location for a certain day
	 * @param schOid
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Collection<AA14BookedSlot> findWeekBookedSlotsForSchedule(final AA14ScheduleOID schOid,
																	 final int year,final int weekOfYear) {
		Date weekFirstInstant = Dates.weekFirstInstant(year,weekOfYear).toDate();
		Date weekLastInstant = Dates.weekLastInstant(year,weekOfYear).toDate();
		Range<Date> dateRange = Range.closed(weekFirstInstant,weekLastInstant);
		return this.findRangeBookedSlotsFor(schOid,dateRange);
	}
	/**
	 * Returns summaries for all slots in a schedule for a certain day
	 * @param schOid
	 * @param lang
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Collection<AA14SummarizedBookedSlot> findWeekBookedSlotsSummarizedFor(final Language lang,
																				 final AA14ScheduleOID schOid,
																				 final int year,final int weekOfYear) {
		Date weekFirstInstant = Dates.weekFirstInstant(year,weekOfYear).toDate();
		Date weekLastInstant = Dates.weekLastInstant(year,weekOfYear).toDate();
		Range<Date> dateRange = Range.closed(weekFirstInstant,weekLastInstant);
		return this.findRangeBookedSlotsSummarizedFor(lang,
													  schOid,
													  dateRange);
	}
	/**
	 * Return all slots in a service location for a certain day
	 * @param lang
	 * @param schOid
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Collection<AA14BookedSlot> findMonthBookedSlotsFor(final Language lang,
															  final AA14ScheduleOID schOid,
															  final int year,final int monthOfYear) {
		Date monthFirstInstant = Dates.monthFirstInstant(year,monthOfYear).toDate();
		Date monthLastInstant = Dates.monthLastInstant(year,monthOfYear).toDate();
		Range<Date> dateRange = Range.closed(monthFirstInstant,monthLastInstant);
		return this.findRangeBookedSlotsFor(schOid,dateRange);
	}
	/**
	 * Returns summaries for all slots in a schedule for a certain day
	 * @param lang
	 * @param schOid
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Collection<AA14SummarizedBookedSlot> findMonthBookedSlotsSummarizedFor(final Language lang,
																				  final AA14ScheduleOID schOid,
																				  final int year,final int monthOfYear) {
		Date monthFirstInstant = Dates.monthFirstInstant(year,monthOfYear).toDate();
		Date monthLastInstant = Dates.monthLastInstant(year,monthOfYear).toDate();
		Range<Date> dateRange = Range.closed(monthFirstInstant,monthLastInstant);
		return this.findRangeBookedSlotsSummarizedFor(lang,
													  schOid,
													  dateRange);
	}
	/**
	 * Return all slots in a schedule for a certain date range
	 * @param schOid
	 * @param dateRange
	 * @return
	 */
	public Collection<AA14BookedSlot> findRangeBookedSlotsFor(final AA14ScheduleOID schOid,
															  final Range<Date> dateRange) {
		FindResult<AA14BookedSlot> findResult = this.getServiceProxyAs(AA14FindServicesForBookedSlot.class)
															.findRangeBookedSlotsFor(this.getSecurityContext(),
																					 schOid,
																					 dateRange);
		return findResult.getOrThrow();
	}
	/**
	 * Returns summaries for all slots in a service location for a certain date range
	 * @param lang
	 * @param schOid
	 * @param dateRange
	 * @return
	 */
	public Collection<AA14SummarizedBookedSlot> findRangeBookedSlotsSummarizedFor(final Language lang,
																				  final AA14ScheduleOID schOid,
																				  final Range<Date> dateRange) {
		FindSummariesResult<AA14BookedSlot> findResult = this.getServiceProxyAs(AA14FindServicesForBookedSlot.class)
																	.findRangeBookedSlotsSummarizedFor(this.getSecurityContext(),
																									   lang,
																				   			 		   schOid,
																				   			 		   dateRange);
		return findResult.getSummariesOrThrow();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BY LOCATION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return all slots in a service location for a certain week
	 * @param locationOid
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Collection<AA14BookedSlot> findDayBookedSlotsFor(final AA14OrgDivisionServiceLocationOID locationOid,
															final int year,final int monthOfYear,final int dayOfMonth) {
		LocalDate day = new LocalDate(year,monthOfYear,dayOfMonth);
		DateTime dayStart = day.toDateTimeAtStartOfDay(DateTimeZone.getDefault());
		DateTime nextDayStart = dayStart.plusDays(1)
										.minusMillis(2);
		Range<Date> dateRange = Range.closed(dayStart.toDate(),nextDayStart.toDate());
		return this.findRangeBookedSlotsFor(locationOid,
											dateRange);
	}
	/**
	 * Returns summaries for all slots in a service location for a certain week
	 * @param lang
	 * @param locationOid
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Collection<AA14SummarizedBookedSlot> findDayBookedSlotsSummarizedFor(final Language lang,
																				final AA14OrgDivisionServiceLocationOID locationOid,
																				final int year,final int monthOfYear,final int dayOfMonth) {
		LocalDate day = new LocalDate(year,monthOfYear,dayOfMonth);
		DateTime dayStart = day.toDateTimeAtStartOfDay(DateTimeZone.getDefault());
		DateTime nextDayStart = dayStart.plusDays(1)
										.minusMillis(2);
		Range<Date> dateRange = Range.closed(dayStart.toDate(),nextDayStart.toDate());
		return this.findRangeBookedSlotsSummarizedFor(lang,
													  locationOid,
													  dateRange);
	}
	/**
	 * Return all slots in a service location for a certain week
	 * @param locationOid
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Collection<AA14BookedSlot> findWeekBookedSlotsFor(final AA14OrgDivisionServiceLocationOID locationOid,
															 final int year,final int weekOfYear) {
		Date weekFirstInstant = Dates.weekFirstInstant(year,weekOfYear).toDate();
		Date weekLastInstant = Dates.weekLastInstant(year,weekOfYear).toDate();
		Range<Date> dateRange = Range.closed(weekFirstInstant,weekLastInstant);
		return this.findRangeBookedSlotsFor(locationOid,dateRange);
	}
	/**
	 * Returns summaries for all slots in a service location for a certain day
	 * @param locationOid
	 * @param lang
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Collection<AA14SummarizedBookedSlot> findWeekBookedSlotsSummarizedFor(final Language lang,
																				 final AA14OrgDivisionServiceLocationOID locationOid,
																				 final int year,final int weekOfYear) {
		Date weekFirstInstant = Dates.weekFirstInstant(year,weekOfYear).toDate();
		Date weekLastInstant = Dates.weekLastInstant(year,weekOfYear).toDate();
		Range<Date> dateRange = Range.closed(weekFirstInstant,weekLastInstant);
		return this.findRangeBookedSlotsSummarizedFor(lang,
													  locationOid,
													  dateRange);
	}
	/**
	 * Return all slots in a service location for a certain day
	 * @param lang
	 * @param locationOid
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Collection<AA14BookedSlot> findMonthBookedSlotsFor(final Language lang,
															  final AA14OrgDivisionServiceLocationOID locationOid,
															  final int year,final int monthOfYear) {
		Date monthFirstInstant = Dates.monthFirstInstant(year,monthOfYear).toDate();
		Date monthLastInstant = Dates.monthLastInstant(year,monthOfYear).toDate();
		Range<Date> dateRange = Range.closed(monthFirstInstant,monthLastInstant);
		return this.findRangeBookedSlotsFor(locationOid,dateRange);
	}
	/**
	 * Returns summaries for all slots in a service location for a certain day
	 * @param lang
	 * @param locationOid
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Collection<AA14SummarizedBookedSlot> findMonthBookedSlotsSummarizedFor(final Language lang,
																				  final AA14OrgDivisionServiceLocationOID locationOid,
																				  final int year,final int monthOfYear) {
		Date monthFirstInstant = Dates.monthFirstInstant(year,monthOfYear).toDate();
		Date monthLastInstant = Dates.monthLastInstant(year,monthOfYear).toDate();
		Range<Date> dateRange = Range.closed(monthFirstInstant,monthLastInstant);
		return this.findRangeBookedSlotsSummarizedFor(lang,
													  locationOid,
													  dateRange);
	}
	/**
	 * Return all slots oids in a service location for a certain date range
	 * @param locationOid
	 * @param dateRange
	 * @param slotType
	 * @return
	 */
	public Collection<AA14SlotOID> findRangeBookedSlotsOidsFor(final AA14OrgDivisionServiceLocationOID locationOid,
															   final Range<Date> dateRange,
															   final AA14BookedSlotType slotType) {
		FindOIDsResult<AA14SlotOID> findOidsResult = this.getServiceProxyAs(AA14FindServicesForBookedSlot.class)
															.findRangeBookedSlotsFor(this.getSecurityContext(),
																					 locationOid,
																					 dateRange,
																					 slotType);
		return findOidsResult.getOrThrow();
	}
	/**
	 * Return all slots in a service location for a certain date range
	 * @param locationOid
	 * @param dateRange
	 * @return
	 */
	public Collection<AA14BookedSlot> findRangeBookedSlotsFor(final AA14OrgDivisionServiceLocationOID locationOid,
															  final Range<Date> dateRange) {
		FindResult<AA14BookedSlot> findResult = this.getServiceProxyAs(AA14FindServicesForBookedSlot.class)
															.findRangeBookedSlotsFor(this.getSecurityContext(),
																					 locationOid,
																					 dateRange);
		return findResult.getOrThrow();
	}
	/**
	 * Returns summaries for all slots in a service location for a certain date range
	 * @param lang
	 * @param locationOid
	 * @param dateRange
	 * @return
	 */
	public Collection<AA14SummarizedBookedSlot> findRangeBookedSlotsSummarizedFor(final Language lang,
																				  final AA14OrgDivisionServiceLocationOID locationOid,
																				  final Range<Date> dateRange) {
		FindSummariesResult<AA14BookedSlot> findResult = this.getServiceProxyAs(AA14FindServicesForBookedSlot.class)
																	.findRangeBookedSlotsSummarizedFor(this.getSecurityContext(),
																									   lang,
																				   			 		   locationOid,
																				   			 		   dateRange);
		return findResult.getSummariesOrThrow();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BY LOCATION & SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return all slots in a service location for a certain day
	 * @param locationOid
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Collection<AA14BookedSlot> findDayBookedSlotsFor(final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
															final int year,final int monthOfYear,final int dayOfMonth) {
		LocalDate day = new LocalDate(year,monthOfYear,dayOfMonth);
		DateTime dayStart = day.toDateTimeAtStartOfDay(DateTimeZone.getDefault());
		DateTime nextDayStart = dayStart.plusDays(1)
										.minusMillis(2);
		Range<Date> dateRange = Range.closed(dayStart.toDate(),nextDayStart.toDate());
		return this.findRangeBookedSlotsFor(locationOid,schOid,
											dateRange);
	}
	/**
	 * Returns summaries for all slots in a service location for a certain day
	 * @param lang
	 * @param locationOid
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Collection<AA14SummarizedBookedSlot> findDayBookedSlotsSummarizedFor(final Language lang,
																				final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
																				final int year,final int monthOfYear,final int dayOfMonth) {
		LocalDate day = new LocalDate(year,monthOfYear,dayOfMonth);
		DateTime dayStart = day.toDateTimeAtStartOfDay(DateTimeZone.getDefault());
		DateTime nextDayStart = dayStart.plusDays(1)
										.minusMillis(2);
		Range<Date> dateRange = Range.closed(dayStart.toDate(),nextDayStart.toDate());
		return this.findRangeBookedSlotsSummarizedFor(lang,
													  locationOid,schOid,
													  dateRange);
	}
	/**
	 * Return all slots in a service location for a certain day
	 * @param locationOid
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Collection<AA14BookedSlot> findWeekBookedSlotsFor(final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
															 final int year,final int weekOfYear) {
		Date weekFirstInstant = Dates.weekFirstInstant(year,weekOfYear).toDate();
		Date weekLastInstant = Dates.weekLastInstant(year,weekOfYear).toDate();
		Range<Date> dateRange = Range.closed(weekFirstInstant,weekLastInstant);
		return this.findRangeBookedSlotsFor(locationOid,schOid,
											dateRange);
	}
	/**
	 * Returns summaries for all slots in a service location for a certain day
	 * @param locationOid
	 * @param lang
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Collection<AA14SummarizedBookedSlot> findWeekBookedSlotsSummarizedFor(final Language lang,
																				 final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
																				 final int year,final int weekOfYear) {
		Date weekFirstInstant = Dates.weekFirstInstant(year,weekOfYear).toDate();
		Date weekLastInstant = Dates.weekLastInstant(year,weekOfYear).toDate();
		Range<Date> dateRange = Range.closed(weekFirstInstant,weekLastInstant);
		return this.findRangeBookedSlotsSummarizedFor(lang,
													  locationOid,schOid,
													  dateRange);
	}
	/**
	 * Return all slots in a service location for a certain day
	 * @param lang
	 * @param locationOid
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Collection<AA14BookedSlot> findMonthBookedSlotsFor(final Language lang,
															  final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
															  final int year,final int monthOfYear) {
		Date monthFirstInstant = Dates.monthFirstInstant(year,monthOfYear).toDate();
		Date monthLastInstant = Dates.monthLastInstant(year,monthOfYear).toDate();
		Range<Date> dateRange = Range.closed(monthFirstInstant,monthLastInstant);
		return this.findRangeBookedSlotsFor(locationOid,schOid,
											dateRange);
	}
	/**
	 * Returns summaries for all slots in a service location for a certain day
	 * @param lang
	 * @param locationOid
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public Collection<AA14SummarizedBookedSlot> findMonthBookedSlotsSummarizedFor(final Language lang,
																				  final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
																				  final int year,final int monthOfYear) {
		Date monthFirstInstant = Dates.monthFirstInstant(year,monthOfYear).toDate();
		Date monthLastInstant = Dates.monthLastInstant(year,monthOfYear).toDate();
		Range<Date> dateRange = Range.closed(monthFirstInstant,monthLastInstant);
		return this.findRangeBookedSlotsSummarizedFor(lang,
													  locationOid,schOid,
													  dateRange);
	}
	/**
	 * Return all slots in a service location for a certain date range
	 * @param locationOid
	 * @param dateRange
	 * @return
	 */
	public Collection<AA14BookedSlot> findRangeBookedSlotsFor(final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
															  final Range<Date> dateRange) {
		FindResult<AA14BookedSlot> findResult = this.getServiceProxyAs(AA14FindServicesForBookedSlot.class)
															.findRangeBookedSlotsFor(this.getSecurityContext(),
																					 locationOid,schOid,
																					 dateRange);
		return findResult.getOrThrow();
	}
	/**
	 * Returns summaries for all slots in a service location for a certain date range
	 * @param lang
	 * @param locationOid
	 * @param dateRange
	 * @return
	 */
	public Collection<AA14SummarizedBookedSlot> findRangeBookedSlotsSummarizedFor(final Language lang,
																				  final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
																				  final Range<Date> dateRange) {
		FindSummariesResult<AA14BookedSlot> findResult = this.getServiceProxyAs(AA14FindServicesForBookedSlot.class)
																	.findRangeBookedSlotsSummarizedFor(this.getSecurityContext(),
																									   lang,
																				   			 		   locationOid,schOid,
																				   			 		   dateRange);
		return findResult.getSummariesOrThrow();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OVERLAPPING WITH RANGE 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds slots overlapping a given range
	 * <pre>
	 * [1] |-------------------------------------------|
	 * [2] |-----------|
	 * [3]                |------------|
 	 * [4]                                 |-----------|
	 *          [===============================]
	 * </pre>
	 * @param schOid
	 * @param dateRange
	 * @return
	 */
	public Collection<AA14BookedSlot> findBookedSlotsOverlappingRange(final AA14ScheduleOID schOid,
																	  final Range<Date> dateRange) {
		FindResult<AA14BookedSlot> findResult = this.getServiceProxyAs(AA14FindServicesForBookedSlot.class)
															.findBookedSlotsOverlappingRange(this.getSecurityContext(),
																					 		 schOid,
																					 		 dateRange);
		return findResult.getOrThrow();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FIND APPOINTMENTS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns summaries for all active slots for a person 
	 * @param lang
	 * @param personId
	 * @return summarized appointment collection
	 */
	public Collection<AA14SummarizedAppointment> findAppointmentsBy(final AA14AppointmentFilter filter,
																	final Language lang) {
		FindSummariesResult<AA14Appointment> findResult = this.getServiceProxyAs(AA14FindServicesForBookedSlot.class)
															  .findAppointmentsBy(this.getSecurityContext(), 
																	  			  filter, 
																	  			  lang);
		return findResult.getSummariesOrThrow();
	}
	/**
	 * Return all appointments in a service location for a certain person in a certain week
	 * @param locationOid
	 * @param personId
	 * @param year
	 * @param weekOfYear
	 * @param lang
	 * @return
	 */
	public Collection<AA14SummarizedAppointment> findWeekBookedAppointmentsFor(final AA14OrgDivisionServiceLocationID locationId,final PersonID personId,
																			   final int year,final int weekOfYear,
																			   final Language lang) {
		Date weekFirstInstant = Dates.weekFirstInstant(year,weekOfYear).toDate();
		Date weekLastInstant = Dates.weekLastInstant(year,weekOfYear).toDate();
		Range<Date> dateRange = Range.closed(weekFirstInstant,weekLastInstant);
		AA14AppointmentFilter filter = new AA14AppointmentFilter(null,locationId,
																 personId,
																 dateRange);
		return this.findAppointmentsBy(filter,
									   lang);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds a non-bookable periodic slot (all related by a serieoid)
	 * @param serieOid
	 * @return
	 */
	public Collection<AA14SlotOID> findNonBookablePeriodicSlotsOids(final AA14PeriodicSlotSerieOID serieOid) {
		FindOIDsResult<AA14SlotOID> findOidResult = this.getServiceProxyAs(AA14FindServicesForBookedSlot.class)
															.findNonBookablePeriodicSlotsOids(this.getSecurityContext(),
																							  serieOid);
		return findOidResult.getOrThrow();
	}													
	/**
	 * Finds a non-bookable periodic slot (all related by a serieoid)
	 * @param serieOid
	 * @return
	 */
	public Collection<AA14NonBookableSlot> findNonBookablePeriodicSlots(final AA14PeriodicSlotSerieOID serieOid) {
		FindResult<AA14NonBookableSlot> findResult = this.getServiceProxyAs(AA14FindServicesForBookedSlot.class)
																.findNonBookablePeriodicSlots(this.getSecurityContext(),
																							  serieOid);
		return findResult.getOrThrow();
	}
}
