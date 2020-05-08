package aa14f.api.interfaces;

import java.util.Date;

import org.joda.time.DateTime;

import aa14f.model.AA14BookedSlot;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.oids.AA14OIDs.AA14PeriodicSlotSerieOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.persistence.CRUDOnMultipleResult;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ExposedServiceInterface;

@ExposedServiceInterface
public interface AA14CRUDServicesForBookedSlot
         extends AA14CRUDServicesBase<AA14SlotOID,AA14SlotID,AA14BookedSlot> {
/////////////////////////////////////////////////////////////////////////////////////////
//  NON-BOOKABLE SLOTS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a periodic non-bookable slot
	 * @param securityContext
	 * @param schOid
	 * @param startDate
	 * @param endDate
	 * @param timeStartNonBookable
	 * @param timeEndNonBookable
	 * @param monday
	 * @param tuesday
	 * @param wednesday
	 * @param thursday
	 * @param friday
	 * @param nonBookableSubject
	 * @param userCode
	 * @return
	 */
	public CRUDOnMultipleResult<AA14BookedSlot> createPeriodicNonBookableSlots(final SecurityContext securityContext,
											  								   final AA14ScheduleOID schOid,
											  								   final Date startDate,final Date endDate,
											  								   final DateTime timeStartNonBookable,final DateTime timeEndNonBookable,
											  								   final boolean sunday,final boolean monday,final boolean tuesday,final boolean wednesday,final boolean thursday,final boolean friday,final boolean saturday,
											  								   final String nonBookableSubject,
											  								   final UserCode userCode);
	/**
	 * Deletes all the non-bookable periodic slots that belongs to the same serie
	 * @param securityContext
	 * @param serieOid
	 * @return
	 */
	public CRUDOnMultipleResult<AA14BookedSlot> deletePeriodicNonBookableSlots(final SecurityContext securityContext,
																			   final AA14PeriodicSlotSerieOID serieOid);
}