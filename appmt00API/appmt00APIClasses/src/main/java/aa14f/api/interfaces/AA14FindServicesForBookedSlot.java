package aa14f.api.interfaces;

import java.util.Date;

import aa14f.model.AA14Appointment;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14BookedSlotType;
import aa14f.model.AA14NonBookableSlot;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14PeriodicSlotSerieOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import aa14f.model.search.AA14AppointmentFilter;
import aa14f.model.search.AA14BookedSlotFilter;
import r01f.locale.Language;
import r01f.model.persistence.FindOIDsResult;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindSummariesResult;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ExposedServiceInterface;
import r01f.types.Range;

@ExposedServiceInterface
public interface AA14FindServicesForBookedSlot
         extends AA14FindServicesBase<AA14SlotOID,AA14SlotID,AA14BookedSlot> {
/////////////////////////////////////////////////////////////////////////////////////////
//  RANGE BOOKED SLOTS BY SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds slots in a date range
	 * @param securityContext
	 * @param schOid
	 * @param dateRange
	 * @return
	 */
	public FindResult<AA14BookedSlot> findRangeBookedSlotsFor(final SecurityContext securityContext,
										   					  final AA14ScheduleOID schOid,
										   					  final Range<Date> dateRange);
	/**
	 * Finds slots in a date range
	 * @param securityContext
	 * @param schOid
	 * @param dateRange
	 * @return
	 */
	public FindSummariesResult<AA14BookedSlot> findRangeBookedSlotsSummarizedFor(final SecurityContext securityContext,
																				 final Language lang,
																  				 final AA14ScheduleOID schOid,
																  				 final Range<Date> dateRange);
/////////////////////////////////////////////////////////////////////////////////////////
//  RANGE BOOKED SLOTS BY SERVICE LOCATION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds slots of a certain type in a date range
	 * @param securityContext
	 * @param locationOid
	 * @param dateRange
	 * @param slotType
	 * @return
	 */
	public FindOIDsResult<AA14SlotOID> findRangeBookedSlotsFor(final SecurityContext securityContext,
															   final AA14OrgDivisionServiceLocationOID locationOid,
										   					   final Range<Date> dateRange,
										   					   final AA14BookedSlotType slotType);
	/**
	 * Finds slots in a date range
	 * @param securityContext
	 * @param locationOid
	 * @param dateRange
	 * @return
	 */
	public FindResult<AA14BookedSlot> findRangeBookedSlotsFor(final SecurityContext securityContext,
										   					  final AA14OrgDivisionServiceLocationOID locationOid,
										   					  final Range<Date> dateRange);
	/**
	 * Finds slots in a date range
	 * @param securityContext
	 * @param locationOid
	 * @param dateRange
	 * @return
	 */
	public FindSummariesResult<AA14BookedSlot> findRangeBookedSlotsSummarizedFor(final SecurityContext securityContext,
																				 final Language lang,
																  				 final AA14OrgDivisionServiceLocationOID locationOid,
																  				 final Range<Date> dateRange);
/////////////////////////////////////////////////////////////////////////////////////////
//  RANGE BOOKED SLOTS BY SERVICE LOCATION & SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds slots in a date range
	 * @param securityContext
	 * @param locationOid
	 * @param range
	 * @return
	 */
	public FindResult<AA14BookedSlot> findRangeBookedSlotsFor(final SecurityContext securityContext,
										   					  final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
										   					  final Range<Date> range);
	/**
	 * Finds slots in a date range
	 * @param securityContext
	 * @param locationOid
	 * @param dateRange
	 * @return
	 */
	public FindSummariesResult<AA14BookedSlot> findRangeBookedSlotsSummarizedFor(final SecurityContext securityContext,
																				 final Language lang,
																  				 final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID schOid,
																  				 final Range<Date> dateRange);
/////////////////////////////////////////////////////////////////////////////////////////
//	OVERLAPPING BOOKED SLOTS 
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
	 * @param securityContext
	 * @param schOid
	 * @param dateRange
	 * @return
	 */
	public FindResult<AA14BookedSlot> findBookedSlotsOverlappingRange(final SecurityContext securityContext,
																	  final AA14ScheduleOID schOid,
																	  final Range<Date> dateRange);
/////////////////////////////////////////////////////////////////////////////////////////
//  FIND APPOINTMENTS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns summaries for appointments matching the given filter criteria
	 * @param securityContext	security context
	 * @param filter the filter
	 * @param lang	language
	 * @return appointments collection
	 */
	public FindSummariesResult<AA14Appointment> findAppointmentsBy(final SecurityContext securityContext,
																   final AA14AppointmentFilter filter,
																   final Language lang);
	/**
	 * Returns the oids of the [booked slots] matching the given filter
	 * @param securityContext
	 * @param filter
	 * @return
	 */
	public FindOIDsResult<AA14SlotOID> findBookedSlotsBy(final SecurityContext securityContext,
														 final AA14BookedSlotFilter filter);
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds alls periodic non-bookable slots (all them are related by the serieOid)
	 * @param securityContext
	 * @param serieOid
	 * @return
	 */
	public FindOIDsResult<AA14SlotOID> findNonBookablePeriodicSlotsOids(final SecurityContext securityContext,
																	    final AA14PeriodicSlotSerieOID serieOid);
	/**
	 * Finds alls periodic non-bookable slots (all them are related by the serieOid)
	 * @param securityContext
	 * @param serieOid
	 * @return
	 */
	public FindResult<AA14NonBookableSlot> findNonBookablePeriodicSlots(final SecurityContext securityContext,
																	    final AA14PeriodicSlotSerieOID serieOid);	
}