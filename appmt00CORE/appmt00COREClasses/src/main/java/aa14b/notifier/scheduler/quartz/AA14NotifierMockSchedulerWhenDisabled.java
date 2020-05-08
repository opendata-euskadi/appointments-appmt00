package aa14b.notifier.scheduler.quartz;

import javax.inject.Inject;

import aa14b.notifier.config.AA14SchedulerNotifierConfig;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.scheduler.aboutschedulableobj.QuartzMockSchedulerAboutSchedulableObjectWhenDisabled;

/**
 * A scheduler impl used when the scheduler is disabled (see properties and notifier guice module)
 */
@Accessors(prefix="_")
public class AA14NotifierMockSchedulerWhenDisabled 
	 extends QuartzMockSchedulerAboutSchedulableObjectWhenDisabled<AA14OrgDivisionServiceLocationOID>
  implements AA14NotifierScheduler {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS 
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final AA14SchedulerNotifierConfig _config;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14NotifierMockSchedulerWhenDisabled(final AA14SchedulerNotifierConfig cfg) {
		_config = cfg;
	}
}
