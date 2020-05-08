package aa14f.client.api.sub;

import java.util.Collection;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14BusinessConfigServices;
import aa14f.api.interfaces.AA14NotifierServices;
import aa14f.model.AA14NotificationOperation;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.business.AA14BusinessConfigs;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.api.delegates.ClientAPIServiceDelegateBase;
import r01f.types.contact.PersonID;
import r01f.util.types.collections.CollectionUtils;

/**
 * Client implementation of notifier api
 */
@Slf4j
@Accessors(prefix="_")
public class AA14ClientAPIForNotifier
     extends ClientAPIServiceDelegateBase<AA14NotifierServices> {		
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14BusinessConfigServices _configServices;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIForNotifier(final Provider<SecurityContext> securityContextProvider,
								    final Marshaller modelObjectsMarshaller,
								    final AA14NotifierServices notifierServicesProxy,
								    final AA14BusinessConfigServices configServices) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  notifierServicesProxy); // reference to other client apis
		_configServices = configServices;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ABOUT APPOINTMENTS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sends a create notification about an appointment creation
	 * @param slotOid
	 * @return
	 */
	public boolean sendAppointmentCreateNotification(final AA14SlotOID slotOid) {
		return this.sendAppointmentNotification(AA14NotificationOperation.CREATE,
									 			slotOid);
	}
	/**
	 * Sends a create notification about an appointment creation
	 * @param slotOid
	 * @return
	 */
	public boolean sendAppointmentUpdateNotification(final AA14SlotOID slotOid) {
		return this.sendAppointmentNotification(AA14NotificationOperation.UPDATE,
									 			slotOid);
	}
	/**
	 * Sends a create notification about an appointment creation
	 * @param slotOid
	 * @return
	 */
	public boolean sendAppointmentDeleteNotification(final AA14SlotOID slotOid) {
		return this.sendAppointmentNotification(AA14NotificationOperation.DELETE,
									 			slotOid);
	}
	/**
	 * Sends a reminder notification about an appointment
	 * @param slotOid
	 * @return
	 */
	public boolean sendAppointmentRemindTomorrowNotification(final AA14SlotOID slotOid) {
		return this.sendAppointmentNotification(AA14NotificationOperation.REMIND_TOMORROW,
									 			slotOid);
	}
	/**
	 * Sends a reminder notification about an appointment
	 * @param slotOid
	 * @return
	 */
	public boolean sendAppointmentRemindTodayNotification(final AA14SlotOID slotOid) {
		return this.sendAppointmentNotification(AA14NotificationOperation.REMIND_TODAY,
									 			slotOid);
	}
	public boolean sendAppointmentNotification(final AA14NotificationOperation op,
											   final AA14SlotOID slotOid) {
		return this.getServiceProxyAs(AA14NotifierServices.class)
						.sendAppointmentNotification(this.getSecurityContext(),
										  			 op,
										  			 slotOid)
						.getOrThrow();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ABOUT PERSON LOCATOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public boolean sendPersonLocatorReminder(final PersonID personId) {
		return this.getServiceProxyAs(AA14NotifierServices.class)
						.sendPersonLocatorIdReminderNotification(this.getSecurityContext(),
										  			 			 AA14NotificationOperation.REMIND_PERSON_LOCATOR,
										  			 			 personId)
						.getOrThrow();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public boolean existsScheduleNotifierJobFor(final AA14OrgDivisionServiceLocationOID locOid,
												final AA14NotificationOperation op) {
		return this.getServiceProxyAs(AA14NotifierServices.class)
						.existsScheduleNotifierJobFor(this.getSecurityContext(),
										  			  locOid,
										  			  op)
						.getOrThrow();
	}
	public boolean scheduleNotifierJobFor(final AA14OrgDivisionServiceLocationOID locOid,
										  final AA14NotificationOperation op) {
		return this.getServiceProxyAs(AA14NotifierServices.class)
						.scheduleNotifierJobFor(this.getSecurityContext(),
										  	    locOid,
										  		op)
						.getOrThrow();
	}
	public String schedulerDebugInfo() {
		return this.getServiceProxyAs(AA14NotifierServices.class)
						.schedulerDebugInfo(this.getSecurityContext())
						.getOrThrow();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Ensures there exists an [scheduler] config for every [location]
	 */
	public void ensureConfig() {
		// Get all locations
		AA14BusinessConfigs configs = _configServices.loadConfig(this.getSecurityContext())
													 .getOrThrow();
		Collection<AA14OrgDivisionServiceLocation> allConfiguredLocations = configs.getAllLocations();
		
		// ensure there exists an scheduler config for every location
		if (CollectionUtils.hasData(allConfiguredLocations)) {
			String dbgBefore = this.schedulerDebugInfo();
			log.warn("[NOTIFIER SCHEDULER CONFIG (before)]=========================================================\n{}",dbgBefore);
			
			for (AA14OrgDivisionServiceLocation loc : allConfiguredLocations) {
				// Remind today
				//	if (!clientApi.notifierAPI()
				//				  .existsScheduleNotifierJobFor(loc.getOid(),	
				//												AA14NotificationOperation.REMIND_TODAY)) {
				//		clientApi.notifierAPI()
				//				 .scheduleNotifierJobFor(loc.getOid(),
				//						 				 AA14NotificationOperation.REMIND_TODAY);
				//		
				//		log.warn("Scheduling notifier scheduler for {}: {} operation", 
				//				 loc.getId().asString(), AA14NotificationOperation.REMIND_TODAY);
				//	}
				// Remind tomorrow
				if (!this.existsScheduleNotifierJobFor(loc.getOid(),	
													   AA14NotificationOperation.REMIND_TOMORROW)) {
					this.scheduleNotifierJobFor(loc.getOid(),
									 			AA14NotificationOperation.REMIND_TOMORROW);
					
					log.debug("Scheduling notifier scheduler for {}: {} operation", 
							  loc.getId().asString(), AA14NotificationOperation.REMIND_TOMORROW);
				}
			}
			String dbgAfter = this.schedulerDebugInfo();
			log.warn("[NOTIFIER SCHEDULER CONFIG (after)]=========================================================\n{}",dbgAfter);
		} else {
			log.warn("[NOTIFIER SCHEDULER CONFIG: NO locations configured");
		}
	}
}
