package aa14b.events;

import javax.inject.Inject;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Singleton;

import aa14b.services.delegates.notifier.AA14NotifierServicesDelegateImpl;
import aa14b.services.internal.AA14BookedSlotSummarizerService;
import aa14f.model.AA14Appointment;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14NotificationOperation;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase;
import r01f.core.services.notifier.annotations.UseEMailNotifier;
import r01f.core.services.notifier.annotations.UseLogNotifier;
import r01f.core.services.notifier.annotations.UseMessagingNotifier;
import r01f.events.COREServiceMethodExecEvents.COREServiceMethodExecOKEvent;
import r01f.events.crud.CRUDOKEventFilter;
import r01f.events.crud.CRUDOKEventListenerBase;
import r01f.model.persistence.CRUDOK;
import r01f.model.services.COREServiceMethodExecOK;

/**
 * Contains event listeners for the alarm events
 * TWO alarm event listeners are available
 * <ul>
 * 		<li>Latinia based: notifies using latinia services to send a SMS / push notification to the security person mobile</li>
 * 		<li>EMail based: notifies using SMTP services to send an email to the security person mobile</li>
 * </ul>
 * Both implements {@link AA14NotifierServicesDelegateImpl} that contains a single method: sendNotification(message)
 *
 * IMPORTANT!!
 * Guava's {@link EventBus} does not cope with generic events: 
 * 		There MUST exist specific event handlers associated with CRUD events for each of the indexable model objects
 * 		These event handlers are registered at {@link BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase} where the @Subscribe annotated method is
 * 		used to know the event-type and associate the event type with the event handler
 * 
 *  	The underlying problem is that if a generic event like CRUDOperationEvent<R extends PersistableModelObject<? extends OID>> is used
 * 		due to type erasure, ALL event handlers would be associated with the SAME raw event-type: {@link CRUDOperationEvent}
 * 		The consequences of this multiple-association is that ALL three event handlers will be called for any event, independently of 
 * 		the event type
 */
@Slf4j
public class AA14CRUDOKEventListenersForNotificationEvents {
/////////////////////////////////////////////////////////////////////////////////////////
//  BASE 
/////////////////////////////////////////////////////////////////////////////////////////
	private static abstract class AA14CRUDOKEventListenersForAppointmentBase
			     		  extends CRUDOKEventListenerBase {
		
		private final AA14BookedSlotSummarizerService _appointmentSummarizerService;
		private final AA14NotifierServicesDelegateImpl _notifierServices;
		
		public AA14CRUDOKEventListenersForAppointmentBase(final AA14BookedSlotSummarizerService appointmentSummarizerService,
														  final AA14NotifierServicesDelegateImpl notifierServices) {
			super(AA14BookedSlot.class,
				  // event filter
				  new CRUDOKEventFilter() {
							@Override @SuppressWarnings("unchecked")
							public boolean hasTobeHandled(final COREServiceMethodExecOKEvent opEvent) {
								// the event refers to a create / update or delete event on an appointment
								COREServiceMethodExecOK<?> opResult = opEvent.getAsCOREServiceMethodExecOK();
								return (opResult instanceof CRUDOK) 
								    && (opResult.as(CRUDOK.class).hasBeenCreated() || opResult.as(CRUDOK.class).hasBeenUpdated() || opResult.as(CRUDOK.class).hasBeenDeleted())
								    && (opResult.as(CRUDOK.class).getObjectType().isAssignableFrom(AA14Appointment.class));
							}
				  });
			_appointmentSummarizerService = appointmentSummarizerService;
			_notifierServices = notifierServices;
		}
		@Subscribe	// subscribes this event listener at the EventBus
		@Override
		public void onPersistenceOperationOK(final COREServiceMethodExecOKEvent opOKEvent) {		
			// Check if the notifier is enabled
			if (!_notifierServices.isEnabled()) {
				log.warn("[notifier event handler]: {} is NOT enabled; the notification will not be sent",
						 _notifierServices.getClass().getSimpleName());
				return;
			}
			// ... upon create or update
			if (_crudOperationOKEventFilter.hasTobeHandled(opOKEvent)) {
				AA14BookedSlot slot = opOKEvent.getAsCOREServiceMethodExecOKOn(AA14BookedSlot.class)
											   .getOrThrow();
				if (!(slot instanceof AA14Appointment)) {
					// do NOT notify for this type of slots
					log.warn("{} slots does NOT have to be notified",
							 slot.getClass().getSimpleName());
					return;
				}
				
				// notify only appointments
				try {					
					// [0]-Check if the notifier is enabled
					if (!_notifierServices.isEnabled()) {
						log.warn(">> {} is NOT enabled; the notification will not be sent",_notifierServices.getClass().getSimpleName());
						return;
					}
					// [1]-Get the alarm event from the bus event
					AA14Appointment appointment = (AA14Appointment)slot;
				
					// [2]-A bit of logging
					log.info(">> [{}] NOTIFY APPOINTMENT {} on location with oid={} for subject with id={} at {}",
							 this.getClass().getSimpleName(),
							 opOKEvent.getCOREServiceMethodExecResult().getCalledMethod(),
							 appointment.getOrgDivisionServiceLocationOid(),
							 appointment.getSubject().getId(),
							 appointment.getStartDate());
					
					// [3]-Compose the notification message to be sent form the event
					AA14NotificationMessageAboutAppointment msg = AA14NotificationMessageBuilder.using(_appointmentSummarizerService)
														  	   				    				.createForAppointment(opOKEvent.getSecurityContext(),
														  	   				    									  appointment);		
					_notifierServices.sendNotification(AA14NotificationOperation.fromCalledMethod(opOKEvent.getAsCOREServiceMethodExecOK()
																										   .getCalledMethod()),
													   msg);
				} catch(Throwable th) {
					log.error("Error notifying appointment {}",
							  th.getMessage(),th);
				}
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  LOG
/////////////////////////////////////////////////////////////////////////////////////////
	@Singleton
	public static class AA14CRUDOKEventListenersForAppointmentLog
			    extends AA14CRUDOKEventListenersForAppointmentBase {		
		@Inject
		public AA14CRUDOKEventListenersForAppointmentLog(				 final AA14BookedSlotSummarizerService appointmentSummarizerService,
														 @UseLogNotifier final AA14NotifierServicesDelegateImpl notifierServices) {
			super(appointmentSummarizerService,
				  notifierServices);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Messaging (SMS)
/////////////////////////////////////////////////////////////////////////////////////////
	@Singleton
	public static class AA14CRUDOKEventListenersForAppointmentNotifyByMessaging
			    extends AA14CRUDOKEventListenersForAppointmentBase {		
		@Inject
		public AA14CRUDOKEventListenersForAppointmentNotifyByMessaging(						  final AA14BookedSlotSummarizerService appointmentSummarizerService,
																 	   @UseMessagingNotifier  final AA14NotifierServicesDelegateImpl notifierServices) {
			super(appointmentSummarizerService,
				  notifierServices);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIL
/////////////////////////////////////////////////////////////////////////////////////////
	@Singleton
	public static class AA14CRUDOKEventListenersForAppointmentNotifyByEMail
			    extends AA14CRUDOKEventListenersForAppointmentBase {		
		@Inject
		public AA14CRUDOKEventListenersForAppointmentNotifyByEMail(					  final AA14BookedSlotSummarizerService appointmentSummarizerService,
																   @UseEMailNotifier  final AA14NotifierServicesDelegateImpl notifierServices) {
			super(appointmentSummarizerService,
				  notifierServices);
		}
	}
}
