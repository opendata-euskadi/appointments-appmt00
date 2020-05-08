package aa14b.notifier.config;

import java.text.ParseException;

import org.quartz.CronExpression;

import aa14f.model.AA14NotificationOperation;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.config.ContainsConfigData;
import r01f.debug.Debuggable;
import r01f.scheduler.CronExpressionExplained;
import r01f.scheduler.SchedulerConfig;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
@Accessors(prefix="_")
public class AA14SchedulerNotifierConfig 
  implements ContainsConfigData,
  			 Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	// Scheduler config
	@Getter private final SchedulerConfig _schedulerConfig;
	
	// Cron expression for rendering dynamic partials
	@Getter private final CronExpressionExplained _remindTomorrowCron;
	
	// Cron expression for rendering dynamic partials
	@Getter private final CronExpressionExplained _remindTodayCron;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public CronExpression getCronExpressionFor(final AA14NotificationOperation op) {
		CronExpression cronExpr = null;
		if (op.is(AA14NotificationOperation.REMIND_TOMORROW)) {
			cronExpr = this.getRemindTomorrowCron()
						   .getExpression();
		} else if (op.is(AA14NotificationOperation.REMIND_TODAY)) {
			cronExpr = this.getRemindTodayCron()
						   .getExpression();
		} else {
			throw new IllegalArgumentException("Not a valid notification scheduler operation: " + op);
		}
		return cronExpr;
	}
	@Override
	public CharSequence debugInfo() {
		StringBuilder sb = new StringBuilder();
		if (_schedulerConfig != null) {
			sb.append("Scheduler: ").append(_schedulerConfig.debugInfo());
			sb.append("\n");
		}
		sb.append("Remind tomorrow cron: ").append(_remindTomorrowCron != null ? _remindTomorrowCron.debugInfo() : "null")
											.append("\n");
		sb.append("Remind today cron: ").append(_remindTodayCron != null ? _remindTodayCron.debugInfo() : "null");
		return sb;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14SchedulerNotifierConfig(final SchedulerConfig schConfig,
										  final CronExpressionExplained remindTomorrowCronExpression,
										  final CronExpressionExplained remindTodayCronExpression) {
		_schedulerConfig = schConfig;
		_remindTomorrowCron = remindTomorrowCronExpression;
		_remindTodayCron = remindTodayCronExpression;
	}
	public static AA14SchedulerNotifierConfig createFrom(final XMLPropertiesForAppComponent xmlProps) {
		// scheduler config
		SchedulerConfig schCfg = SchedulerConfig.from("/notifier/remindScheduler",
													  xmlProps);
		// notifier cron
		CronExpressionExplained tomorrowCron = _cron(xmlProps,
													 AA14NotificationOperation.REMIND_TOMORROW);
		CronExpressionExplained todayCron = _cron(xmlProps,
										 		  AA14NotificationOperation.REMIND_TODAY);
		
		// return
		return new AA14SchedulerNotifierConfig(schCfg,
												  tomorrowCron,todayCron);
	}
	private static CronExpressionExplained _cron(final XMLPropertiesForAppComponent xmlProps,
												 final AA14NotificationOperation op) {
		// [0] - Create the default values
		String defaultCronExpr = null;
		String defaultCronExprDesc = null;
		String cronId = null;
		if (op == AA14NotificationOperation.REMIND_TOMORROW) {
			cronId = "tomorrow";
			defaultCronExpr = "0 0 20 1/1 * ? *";	// every day at 20:00 (see http://www.cronmaker.com/)
			defaultCronExprDesc = "Every day at 20:00";
		} else if (op == AA14NotificationOperation.REMIND_TODAY) {
			cronId = "today";
			defaultCronExpr = "	0 0 0/2 1/1 * ? *";	// every 2 hours (see http://www.cronmaker.com/)
			defaultCronExprDesc = "Every hour from 7:00";
		} else {
			throw new IllegalArgumentException("Not a valid notification operation: " + op);
		}
		
		// [1] - Load the config from the properties files
		String exprXPath = Strings.customized("/notifier/remindScheduler/cron/{}/@expr",cronId);
		String descXPath = Strings.customized("/notifier/remindScheduler/cron/{}/",cronId);
		String cronExprStr = xmlProps.propertyAt(exprXPath)
									 .asString();
		String cronExprDesc = xmlProps.propertyAt(descXPath)
									  .asString();
		if (cronExprStr == null) cronExprStr = defaultCronExpr;
		if (cronExprDesc == null) cronExprDesc = defaultCronExprDesc;
		
		// [3]- Parse
        CronExpression cronExpr = null;
        try {
        	cronExpr = new CronExpression(cronExprStr);
        } catch(ParseException parseEx) {
        	log.error("Error parsing notifier scheduler cron expression={} at {}: {}",
        			  cronExprStr,exprXPath,
        			  parseEx.getMessage(),parseEx);
        	// default
        	try {
        		cronExpr = new CronExpression(defaultCronExpr);	
        	} catch(ParseException ignored) {
        		// this MUST  NOT fail
        	}
        }
        return new CronExpressionExplained(cronExpr,cronExprDesc);
	}
}
