package aa14b.services;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;

import aa14b.notifier.scheduler.quartz.AA14NotifierScheduler;
import aa14b.services.delegates.notifier.AA14NotifierServicesDelegate;
import aa14b.services.delegates.notifier.AA14NotifierServicesDelegateImpl;
import aa14b.services.internal.AA14BookedSlotSummarizerService;
import aa14f.api.interfaces.AA14CRUDServicesForBookedSlot;
import aa14f.api.interfaces.AA14NotifierServices;
import aa14f.model.AA14NotificationOperation;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.core.services.notifier.annotations.UseEMailNotifier;
import r01f.core.services.notifier.annotations.UseLogNotifier;
import r01f.core.services.notifier.annotations.UseMessagingNotifier;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.services.COREServiceMethodExecResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.persistence.CoreServicesBase;
import r01f.services.persistence.ServiceDelegateProvider;
import r01f.types.contact.PersonID;

/**
 * Implements the {@link AA14SearchServicesForEntityModelObject}s search-related services which in turn are delegated
 * {@link AA14SearchServicesDelegateForEntityModelObject} 
 */
@Singleton
@Accessors(prefix="_")
public class AA14NotifierServicesImpl 
     extends CoreServicesBase					  
  implements AA14NotifierServices,
  			 AA14ServiceInterfaceImpl {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS  
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14CRUDServicesForBookedSlot _bookedSlotsCRUD;
	
	private final AA14BookedSlotSummarizerService _appointmentSummarizerService;
	
	private final AA14NotifierScheduler _notifierScheduler;
	private final Collection<AA14NotifierServicesDelegateImpl> _notifierServices;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR 
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14NotifierServicesImpl(				final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
							@ModelObjectsMarshaller final Marshaller modelObjectsMarshaller,
													final EventBus eventBus,
													final AA14CRUDServicesForBookedSlot bookedSlotsCRUD,
													final AA14BookedSlotSummarizerService appointmentSummarizerService,
							// Notifier
													// - Scheduler
													final AA14NotifierScheduler notifierScheduler,
													// - Notifiers
							@UseEMailNotifier  		final AA14NotifierServicesDelegateImpl mailNotifierServices,
							@UseMessagingNotifier  	final AA14NotifierServicesDelegateImpl messagingNotifierServices,
							@UseLogNotifier 		final AA14NotifierServicesDelegateImpl logNotifierServices) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus);
		_bookedSlotsCRUD = bookedSlotsCRUD;
		
		_appointmentSummarizerService = appointmentSummarizerService;
		
		_notifierScheduler = notifierScheduler;
		_notifierServices = Lists.newArrayList(mailNotifierServices,
											   messagingNotifierServices,
											   logNotifierServices);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INJECTED STATUS 
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ServiceDelegateProvider<AA14NotifierServices> _delegateProvider = 
							new ServiceDelegateProvider<AA14NotifierServices>() {
									@Override
									public AA14NotifierServices createDelegate(final SecurityContext securityContext) {
										return new AA14NotifierServicesDelegate(_bookedSlotsCRUD,
																				_appointmentSummarizerService,
																				_notifierScheduler,_notifierServices);
									}
						  	};
/////////////////////////////////////////////////////////////////////////////////////////
//	ABOUT APPOINTMENTS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public COREServiceMethodExecResult<Boolean> sendAppointmentNotification(final SecurityContext securityContext,
																			final AA14NotificationOperation op,
																			final AA14SlotOID slotOid) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14NotifierServices.class)
							.sendAppointmentNotification(securityContext,
											  			 op,
											  			 slotOid);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ABOUT PERSON LOCATOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public COREServiceMethodExecResult<Boolean> sendPersonLocatorIdReminderNotification(final SecurityContext securityContext, 
																					    final AA14NotificationOperation op,
																						final PersonID personId) {
		return this.forSecurityContext(securityContext)
				.createDelegateAs(AA14NotifierServices.class)
					.sendPersonLocatorIdReminderNotification(securityContext,
									  			 			 op,
									  			 			 personId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SCHEDULER
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public COREServiceMethodExecResult<Boolean> existsScheduleNotifierJobFor(final SecurityContext securityContext,
																		     final AA14OrgDivisionServiceLocationOID locOid,
																		  	 final AA14NotificationOperation op) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14NotifierServices.class)
							.existsScheduleNotifierJobFor(securityContext,
											  			  locOid,
											  			  op);
	}
	@Override
	public COREServiceMethodExecResult<Boolean> scheduleNotifierJobFor(final SecurityContext securityContext,
																	   final AA14OrgDivisionServiceLocationOID locOid,
										  				  			   final AA14NotificationOperation op) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14NotifierServices.class)
							.scheduleNotifierJobFor(securityContext,
											  		locOid,
											  		op);
	}
	@Override
	public COREServiceMethodExecResult<String> schedulerDebugInfo(final SecurityContext securityContext) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14NotifierServices.class)
						.schedulerDebugInfo(securityContext);
	}
}
