package aa14b.notifier.scheduler.quartz;

import aa14b.notifier.config.AA14SchedulerNotifierConfig;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import r01f.scheduler.aboutschedulableobj.QuartzSchedulerWrapperAboutSchedulableObject;

/**
 * Package scheduler interface
 */
public interface AA14NotifierScheduler
		 extends QuartzSchedulerWrapperAboutSchedulableObject<AA14OrgDivisionServiceLocationOID> {
	/**
	 * @return the scheduler config
	 */
	public AA14SchedulerNotifierConfig getConfig();
}
