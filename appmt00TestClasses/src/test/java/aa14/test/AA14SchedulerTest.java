package aa14.test;

import org.junit.Assert;
import org.junit.Test;

import aa14f.client.api.AA14ClientAPI;
import aa14f.model.AA14NotificationOperation;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import lombok.extern.slf4j.Slf4j;
import r01f.concurrent.Threads;
import r01f.types.TimeLapse;

@Slf4j
public class AA14SchedulerTest
	 extends AA14TestBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testScheduler() {
		AA14ClientAPI api = AA14TestBase.getClientApi();
		
		// Create an scheduler job for a service location
		AA14OrgDivisionServiceLocation loc = api.configAPI()
												.getConfig()
												.getForBizilagun()
												.getComunidadesAlquileresLocationAraba();
		boolean scheduled = api.notifierAPI()
							   .scheduleNotifierJobFor(loc.getOid(),
									   				   AA14NotificationOperation.REMIND_TODAY);
		Assert.assertTrue(scheduled);
		// check
		boolean isScheduled = api.notifierAPI()
								 .existsScheduleNotifierJobFor(loc.getOid(),
										 						AA14NotificationOperation.REMIND_TODAY);
		Assert.assertTrue(isScheduled);	
		
		// check false
		boolean isScheduled2 = api.notifierAPI()
								 .existsScheduleNotifierJobFor(loc.getOid(),
										 						AA14NotificationOperation.REMIND_TOMORROW);
		Assert.assertFalse(isScheduled2);	
		
		// A bit of debug
		log.info("Scheduler info:\n{}",
				 api.notifierAPI()
				    .schedulerDebugInfo());
		
		// wait for the scheduler to run
		TimeLapse wait = TimeLapse.of("3m");
		
		log.info("... waiting {} for the scheduler to run",
				 wait.asString());
		Threads.safeSleep(wait);
		log.info("... scheduled jobs should have been executed!");
		
		// A bit of debug
		log.info("Scheduler info:\n{}",
				 api.notifierAPI()
				    .schedulerDebugInfo());
	}
}
