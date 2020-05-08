package aa14b.notifier.scheduler.quartz;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.quartz.Scheduler;

import aa14b.notifier.config.AA14SchedulerNotifierConfig;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.scheduler.aboutschedulableobj.QuartzSchedulerWrapperAboutSchedulableObjectBase;


/**
 * Implements the {@link AA14NotifierScheduler} interface on a quartz scheduler
 */
@Singleton
@Accessors(prefix="_")
public class AA14NotifierQuartzSchedulerWrapper 
	 extends QuartzSchedulerWrapperAboutSchedulableObjectBase<AA14OrgDivisionServiceLocationOID>
  implements AA14NotifierScheduler {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS 
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final AA14SchedulerNotifierConfig _config;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14NotifierQuartzSchedulerWrapper(final AA14SchedulerNotifierConfig cfg,
									 		  final Scheduler scheduler) {
		super(cfg.getSchedulerConfig(),
			  scheduler);
		_config = cfg;
	}
}
