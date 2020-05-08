package aa14f.api.interfaces;

import aa14f.model.AA14NotificationOperation;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import r01f.model.services.COREServiceMethodExecResult;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ExposedServiceInterface;
import r01f.types.contact.PersonID;

@ExposedServiceInterface
public interface AA14NotifierServices 
		 extends AA14ServiceInterface {
/////////////////////////////////////////////////////////////////////////////////////////
//  NOTIFICATIONS ABOUT APPOINTMENT
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Sends a notification about an appointment operation
	 * @param securityContext
	 * @param slotOid
	 * @return
	 */
	public COREServiceMethodExecResult<Boolean> sendAppointmentNotification(final SecurityContext securityContext,
																	 		final AA14NotificationOperation op,
																	 		final AA14SlotOID slotOid);
	/**
	 * Sends a notification about a [person locator]
	 * @param securityContext
	 * @param op
	 * @param personId
	 * @return
	 */
	public COREServiceMethodExecResult<Boolean> sendPersonLocatorIdReminderNotification(final SecurityContext securityContext,
																						final AA14NotificationOperation op,
																						final PersonID personId);
/////////////////////////////////////////////////////////////////////////////////////////
//  SCHEDULED NOTIFICATIONS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Check if it exists a scheduled notifier job for appointments at a given location
	 * @param securityContext
	 * @param locOid
	 * @param op
	 * @return true if the notifier job was scheduled
	 */
	public COREServiceMethodExecResult<Boolean> existsScheduleNotifierJobFor(final SecurityContext securityContext,
																		     final AA14OrgDivisionServiceLocationOID locOid,
																		  	 final AA14NotificationOperation op);
	/**
	 * Schedules a notifier job for appointments at a given location
	 * @param securityContext
	 * @param locOid
	 * @param op
	 * @return true if the notifier job was scheduled
	 */
	public COREServiceMethodExecResult<Boolean> scheduleNotifierJobFor(final SecurityContext securityContext,
																	   final AA14OrgDivisionServiceLocationOID locOid,
										  				  			   final AA14NotificationOperation op);
	/**
	 * Returns debug info about the scheduler status
	 * @param securityContext
	 * @return
	 */
	public COREServiceMethodExecResult<String> schedulerDebugInfo(final SecurityContext securityContext);
}
