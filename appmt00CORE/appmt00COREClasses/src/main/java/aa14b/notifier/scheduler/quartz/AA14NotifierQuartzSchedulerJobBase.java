/**
 *
 */
package aa14b.notifier.scheduler.quartz;

import java.util.Collection;
import java.util.Date;

import org.quartz.JobExecutionContext;

import aa14f.api.context.AA14SecurityContext;
import aa14f.api.context.AA14SecurityContextBuilder;
import aa14f.api.interfaces.AA14CRUDServicesForBookedSlot;
import aa14f.api.interfaces.AA14FindServicesForBookedSlot;
import aa14f.api.interfaces.AA14NotifierServices;
import aa14f.common.internal.AA14AppCodes;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14BookedSlotType;
import aa14f.model.AA14NotificationOperation;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.scheduler.QuartzSchedulerJobFactory;
import r01f.scheduler.SchedulerConfig;
import r01f.scheduler.aboutschedulableobj.QuartzSchedulerJobAboutSchedulableObjectBase;
import r01f.types.Range;

/**
 * Quartz Job executed by the quartz scheduler when a trigger is raised
 * The executer is created each time the quartz scheduler executes the job
 * 
 * BEWARE!!!                                                                                  
 * 		In order for this instance to be injected by guice it MUST be created by guice             
 * 		... so use a job factory : {@link QuartzSchedulerJobFactory}                                           
 * 		(see http://javaeenotes.blogspot.com.es/2011/09/inject-instances-in-quartz-jobs-with.html) 
 */
@Slf4j
@Accessors(prefix="_")
public abstract class AA14NotifierQuartzSchedulerJobBase 
              extends QuartzSchedulerJobAboutSchedulableObjectBase<AA14OrgDivisionServiceLocationOID,
     													    	   AA14NotifierQuartzSchedulerJobBase> {		// self type
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	private final AA14FindServicesForBookedSlot _bookedSlotsFindServices;
	private final AA14CRUDServicesForBookedSlot _bookedSlotsCRUDServices;
	private final AA14NotifierServices _notifierServices;
	
	private final AA14NotificationOperation _notificationOperation;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14NotifierQuartzSchedulerJobBase(final SchedulerConfig cfg,
										  	  final AA14FindServicesForBookedSlot bookedSlotsFindServices,
										  	  final AA14CRUDServicesForBookedSlot bookedSlotsCRUDServices,
										  	  final AA14NotifierServices notifierServices,
										  	  final AA14NotificationOperation notificationOperation) {
		super(cfg,
			  // creates a reading point id from a string
			  AA14OrgDivisionServiceLocationOID.FACTORY);
		
		_bookedSlotsFindServices = bookedSlotsFindServices;
		_bookedSlotsCRUDServices = bookedSlotsCRUDServices;
		_notifierServices = notifierServices;
		
		_notificationOperation = notificationOperation;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public static <J extends AA14NotifierQuartzSchedulerJobBase> Class<J> jobTypeFor(final AA14NotificationOperation op) {
		Class<? extends AA14NotifierQuartzSchedulerJobBase> outJobType = null;
		if (op.is(AA14NotificationOperation.REMIND_TOMORROW)) {
			outJobType = AA14NotifierQuartzSchedulerJobForRemindTomorrow.class;
		} else if (op.is(AA14NotificationOperation.REMIND_TODAY)) {
			outJobType = AA14NotifierQuartzSchedulerJobForRemindToday.class;
		} else {
			throw new IllegalArgumentException("Not a valid notification scheduler operation: " + op);
		}
		return (Class<J>)outJobType;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the date range whose appointments will be notified
	 * The date range depends on the previous / next scheduler fire time
	 * @param prevFireTime
	 * @param nextFireTime
	 * @return
	 */
	protected Range<Date> _slotFilterRangeIf(final Date prevFireTime,final Date nextFireTime) {
		Date lowerDate = new Date();
		Date upperDate = nextFireTime != null ? nextFireTime : new Date();
		return Range.closed(lowerDate,upperDate);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INTERFAZ JOB
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void execute(final JobExecutionContext context) {
		// [0] - Context data
		// a) Find all location's booked slots
		AA14OrgDivisionServiceLocationOID serviceLocOid = this.getSchedulableObjectOidFrom(context);
		
		// b) Create the range
		Date prevFireTime = context.getPreviousFireTime();
		Date nextFireTime = context.getNextFireTime();
		Range<Date> dateRange = _slotFilterRangeIf(prevFireTime,nextFireTime);
		
		log.warn("********************************************************************************************");
		log.warn("Scheduled job for {} on service location oid={}: notify appointments within {}",
				  _notificationOperation,serviceLocOid,
				  dateRange.asString());
		log.warn("********************************************************************************************");
		
		// [1] - Find all booked slots 
		log.info("[SCHEDULER]: Notify all booked slots for service location oid={}",
				 serviceLocOid);
		AA14SecurityContext securityContext = AA14SecurityContextBuilder.createForApp(AA14AppCodes.CORE_APPCODE);
		Collection<AA14SlotOID> slotsOids = _bookedSlotsFindServices.findRangeBookedSlotsFor(securityContext,
																						 	 serviceLocOid,
																						 	 dateRange,
																						 	 AA14BookedSlotType.APPOINTMENT)
																   .getOrThrow();
		// [2] - Notify each appointment
		log.info("[SCHEDULER]: {} appointments to be notified",
				 slotsOids.size());
		for (AA14SlotOID slotOid : slotsOids) {
			try {
				// a) load the slot
				AA14BookedSlot slot = _bookedSlotsCRUDServices.load(securityContext,
																	slotOid)
															  .getOrThrow();
				AA14BookedSlotType type = slot.getType();
				if (type == AA14BookedSlotType.NON_BOOKABLE) continue;	// ignore non-bookable slots (this should not happen because the slot oids are only for appointments, not for non-bookable slots)
				
				// b) notify
				_notifierServices.sendAppointmentNotification(securityContext,
												   			  _notificationOperation,
												   			  slot.getOid());
			} catch (Throwable th) {
				log.error("[SCHEDULER]: error notifying {} for service location={}: {}",
						  _notificationOperation,serviceLocOid,
						  th.getMessage(),th);
			}
		}
	}
}