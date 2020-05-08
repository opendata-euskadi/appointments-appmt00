package aa14b.services.internal;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

import aa14f.api.interfaces.AA14CRUDServicesForSchedule;
import aa14f.api.interfaces.AA14FindServicesForBookedSlot;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14BookedSlotType;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import lombok.extern.slf4j.Slf4j;
import r01f.model.persistence.FindResult;
import r01f.securitycontext.SecurityContext;
import r01f.types.Range;
import r01f.util.types.collections.CollectionUtils;

/**
 * An INTERNAL service that checks if an slot is really available to put in a new appointment
 */
@Singleton
@Slf4j
public class AA14SlotOverlappingValidatorService {
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14FindServicesForBookedSlot _slotFind;
	private final AA14CRUDServicesForSchedule _scheduleCRUD;
	
	private ConcurrentMap<AA14ScheduleOID,Integer> _configuredMaxAppointmentsInSlotBySchedule = Maps.newConcurrentMap();
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14SlotOverlappingValidatorService(final AA14CRUDServicesForSchedule schCRUD,
											   final AA14FindServicesForBookedSlot slotFind) {
		_slotFind = slotFind;
		_scheduleCRUD = schCRUD;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Checks if an appointment can be created in a slot
	 * (the configured maximum number of appointments -or non bookable slots- has NOT been reached)
	 * @param securityContext
	 * @param slot
	 * @return
	 */
	public boolean isSlotAvailable(final SecurityContext securityContext,
								   final AA14BookedSlot slot) {
		if (slot.getType() == AA14BookedSlotType.NON_BOOKABLE) return true;		// non bookable slots can always be created
		
		// [0] - Find all booked slots that could potentially overlap this one
		log.debug("Checking the slot within {} and {} can be created (there's enougth space for it",
				  slot.getStartDate(),slot.getEndDate());
		FindResult<AA14BookedSlot> existingSlotFind = _slotFind.findBookedSlotsOverlappingRange(securityContext,
										  								   						slot.getScheduleOid(),
										  								   						new Range<Date>(slot.getDateRange()));
		Collection<AA14BookedSlot> existingSlots = existingSlotFind.getOrThrow();
		
		// [1] - Beware to remove the given slot from the existing slots (update)
		Collection<AA14BookedSlot> theExistingSlots = CollectionUtils.hasData(existingSlots)
															? FluentIterable.from(existingSlots)
																			.filter(new Predicate<AA14BookedSlot>() {
																							@Override
																							public boolean apply(final AA14BookedSlot otherSlot) {
																								return otherSlot.getOid().isNOT(slot.getOid());
																							}
																					})
																			.toList()
															: null;
		
		// [2] - Check that the configured maximum number of appointments has NOT been reached
		int configuredMaxAppointments = _configuredMaxAppointmentsInSlotFor(securityContext,
																			slot.getScheduleOid());
		int actualAppointments = CollectionUtils.hasData(theExistingSlots) ? theExistingSlots.size() : 0;
	
		log.debug("\t... configured max appointments in slot: {} - actual appointments in slot: {} > the slot is available: {}",
				  configuredMaxAppointments,actualAppointments,
				  actualAppointments < configuredMaxAppointments);
		return actualAppointments < configuredMaxAppointments;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Avoids loading the schedule config again and again
	 * @param securityContext
	 * @param schOid
	 * @return
	 */
	private int _configuredMaxAppointmentsInSlotFor(final SecurityContext securityContext,
													final AA14ScheduleOID schOid) {
		if (!_configuredMaxAppointmentsInSlotBySchedule.containsKey(schOid)) {
			AA14Schedule sch = _scheduleCRUD.load(securityContext,
												  schOid)
											.getOrThrow();
			_configuredMaxAppointmentsInSlotBySchedule.putIfAbsent(schOid,
																   sch.getBookingConfig().getMaxAppointmentsInSlot());
		}
		return _configuredMaxAppointmentsInSlotBySchedule.get(schOid);
	}
}
