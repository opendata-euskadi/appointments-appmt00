package aa14b.services.delegates.notifier;

import java.util.Collection;

import org.quartz.CronExpression;
import org.quartz.SchedulerException;

import aa14b.events.AA14NotificationMessageAboutAppointment;
import aa14b.events.AA14NotificationMessageBuilder;
import aa14b.notifier.scheduler.quartz.AA14NotifierQuartzSchedulerJobBase;
import aa14b.notifier.scheduler.quartz.AA14NotifierScheduler;
import aa14b.services.internal.AA14BookedSlotSummarizerService;
import aa14f.api.interfaces.AA14CRUDServicesForBookedSlot;
import aa14f.api.interfaces.AA14NotifierServices;
import aa14f.model.AA14Appointment;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14BookedSlotType;
import aa14f.model.AA14NotificationOperation;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.extern.slf4j.Slf4j;
import r01f.model.persistence.PersistenceOperationExecResultBuilder;
import r01f.model.services.COREServiceMethod;
import r01f.model.services.COREServiceMethodExecResult;
import r01f.securitycontext.SecurityContext;
import r01f.types.contact.PersonID;


@Slf4j
public class AA14NotifierServicesDelegate 
  implements AA14NotifierServices {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS 
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14CRUDServicesForBookedSlot _bookedSlotsCRUD;
	
	private final AA14BookedSlotSummarizerService _appointmentSummarizerService;
	
	private final AA14NotifierScheduler _notifierScheduler;
	private final Collection<AA14NotifierServicesDelegateImpl> _notifiers;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14NotifierServicesDelegate(final AA14CRUDServicesForBookedSlot bookedSlotsCRUD,
										final AA14BookedSlotSummarizerService appointmentSummarizerService,
										final AA14NotifierScheduler notifierScheduler,
										final Collection<AA14NotifierServicesDelegateImpl> notifiers) {
		super();
		
		_bookedSlotsCRUD = bookedSlotsCRUD;
		
		_appointmentSummarizerService = appointmentSummarizerService;
		
		_notifierScheduler = notifierScheduler;
		_notifiers = notifiers;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  APPOINTMENTS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public COREServiceMethodExecResult<Boolean> sendAppointmentNotification(final SecurityContext securityContext,
																			final AA14NotificationOperation op,
																			final AA14SlotOID slotOid) {
		// [1] - Load the slot
		AA14BookedSlot slot = _bookedSlotsCRUD.load(securityContext,
													slotOid)
											  .getOrThrow();
		// [2] - Ensure it's an appointment
		if (slot.getType() != AA14BookedSlotType.APPOINTMENT) {
			return PersistenceOperationExecResultBuilder.using(securityContext)
														.notExecuted(COREServiceMethod.named("sendNotification"))
														.becauseClientBadRequest("The slot with oid={} is NOT an appointment: only appointments can be notified!",
																				 slotOid);
		}
		// [3] - Send the notification
		AA14Appointment appointment = (AA14Appointment)slot;
	
		// [2]-A bit of logging
		log.info(">> [{}] NOTIFY APPOINTMENT {} on location with oid={} for subject with id={} at {}",
				 this.getClass().getSimpleName(),
				 op,
				 appointment.getOrgDivisionServiceLocationOid(),
				 appointment.getSubject().getId(),
				 appointment.getStartDate());
		
		// [3]-Compose the notification message to be sent
		AA14NotificationMessageAboutAppointment msg = AA14NotificationMessageBuilder.using(_appointmentSummarizerService)
											  	   				    .createForAppointment(securityContext,
											  	   				    					  appointment);
		boolean allOK = true;
		for (AA14NotifierServicesDelegateImpl notifier : _notifiers) {
			try {
				notifier.sendNotification(op,
										  msg);
			} catch(Throwable th) {
				log.error("Error while using notifier impl: {} > {}",
						  notifier.getClass(),
						  th.getMessage(),
						  th);
				allOK = false;
			}
		}
		return PersistenceOperationExecResultBuilder.using(securityContext)
													.executed(COREServiceMethod.named("sendNotification"))
													.returning(allOK);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PERSON LOCATOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public COREServiceMethodExecResult<Boolean> sendPersonLocatorIdReminderNotification(final SecurityContext securityContext,
																						final AA14NotificationOperation op, final PersonID personId) {
		return null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public COREServiceMethodExecResult<Boolean> existsScheduleNotifierJobFor(final SecurityContext securityContext,
																		     final AA14OrgDivisionServiceLocationOID locOid,
																		  	 final AA14NotificationOperation op) {
		if (locOid == null) throw new IllegalArgumentException("The service location oid is mandatory!");
		if (op == null) throw new IllegalArgumentException("The notification operation is mandatory!");
		
		// Check
		try {
			boolean isScheduled = _notifierScheduler.isJobScheduledAboutObject(locOid,
																			  AA14NotifierQuartzSchedulerJobBase.jobTypeFor(op));
			return PersistenceOperationExecResultBuilder.using(securityContext)
														.executed(COREServiceMethod.named("existsScheduleNotifierJobFor"))
														.returning(isScheduled);
		} catch (SchedulerException schEx) {
			log.error("Could NOT check if there exists an scheduled {} notifier about {}: {}",
					  op,locOid,
					  schEx.getMessage(),schEx);
			return PersistenceOperationExecResultBuilder.using(securityContext)
														.notExecuted(COREServiceMethod.named("existsScheduleNotifierJobFor"))
														.because(schEx);			
		}
	}
	@Override
	public COREServiceMethodExecResult<Boolean> scheduleNotifierJobFor(final SecurityContext securityContext,
																	   final AA14OrgDivisionServiceLocationOID locOid,
										  				  			   final AA14NotificationOperation op) {
		if (locOid == null) throw new IllegalArgumentException("The service location oid is mandatory!");
		if (op == null) throw new IllegalArgumentException("The notification operation is mandatory!");
		
		// Get the cron expression
		CronExpression cronExpr = _notifierScheduler.getConfig()
													.getCronExpressionFor(op);
		// schedule the job
		try {
			boolean scheduled = _notifierScheduler.scheduleJobAboutObject(locOid,
																		  AA14NotifierQuartzSchedulerJobBase.jobTypeFor(op),
																		  cronExpr);
			return PersistenceOperationExecResultBuilder.using(securityContext)
														.executed(COREServiceMethod.named("existsScheduleNotifierJobFor"))
														.returning(scheduled);
		} catch (SchedulerException schEx) {
			log.error("Could NOT schedule notifier {} about {}: {}",
					  op,locOid,
					  schEx.getMessage(),schEx);
			return PersistenceOperationExecResultBuilder.using(securityContext)
														.notExecuted(COREServiceMethod.named("existsScheduleNotifierJobFor"))
														.because(schEx);
		}
	}
	@Override
	public COREServiceMethodExecResult<String> schedulerDebugInfo(final SecurityContext securityContext) {
		String dbg = _notifierScheduler.schedulerDebugInfo();
		return PersistenceOperationExecResultBuilder.using(securityContext)
													.executed(COREServiceMethod.named("schedulerDebugInfo"))
													.returning(dbg);
	}
}
