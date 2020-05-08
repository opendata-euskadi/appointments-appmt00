/**
 *
 */
package aa14b.notifier.scheduler.quartz;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import aa14f.api.interfaces.AA14CRUDServicesForBookedSlot;
import aa14f.api.interfaces.AA14FindServicesForBookedSlot;
import aa14f.api.interfaces.AA14NotifierServices;
import aa14f.model.AA14NotificationOperation;
import lombok.experimental.Accessors;
import r01f.scheduler.QuartzSchedulerJobFactory;
import r01f.scheduler.SchedulerConfig;
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
@PersistJobDataAfterExecution   
@DisallowConcurrentExecution    // the job instance does NOT allow concurrency (it's not thread safe) 
@Accessors(prefix="_")
public class AA14NotifierQuartzSchedulerJobForRemindToday 
     extends AA14NotifierQuartzSchedulerJobBase {	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14NotifierQuartzSchedulerJobForRemindToday(final SchedulerConfig cfg,
										  				   final AA14FindServicesForBookedSlot bookedSlotsFindServices,
										  				   final AA14CRUDServicesForBookedSlot bookedSlotsCRUDServices,
										  				   final AA14NotifierServices notifierServices) {
		super(cfg,
			  bookedSlotsFindServices,
			  bookedSlotsCRUDServices,
			  notifierServices,
			  AA14NotificationOperation.REMIND_TODAY);
	}
	
	/**
	 * Returns the date range whose appointments will be notified
	 * @param prevFireTime
	 * @param nextFireTime
	 * @return today at 00:00 to today at 23:59 
	 */
	protected Range<Date> _slotFilterRangeIf(final Date prevFireTime,final Date nextFireTime) {
		
		Calendar lowerDate = Calendar.getInstance();
		lowerDate.set(Calendar.HOUR_OF_DAY, 0);
		lowerDate.set(Calendar.MINUTE, 0);
		Calendar upperDate = Calendar.getInstance();
		upperDate.set(Calendar.HOUR_OF_DAY, 0);
		upperDate.set(Calendar.MINUTE, 0);
		upperDate.add(Calendar.DAY_OF_MONTH, 1);
		upperDate.add(Calendar.MINUTE, -1);
		
		return Range.closed(lowerDate.getTime(),upperDate.getTime());
	}
}