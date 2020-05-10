package aa14b.events;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Singleton;

import aa14b.services.delegates.notifier.AA14NotifierServicesForPersonLocator;
import aa14f.api.interfaces.AA14PersonLocatorServices;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.events.COREEventBusEventListener;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;

/**
 * Listens to {@link AA14PersonLocatorIDRemindMessage} events
 */
@Singleton
public class AA14EventListenerForPersonLocatorIDReminder 
  implements COREEventBusEventListener {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final ServicesCoreBootstrapConfigWhenBeanExposed _coreCfg; 
	private final Marshaller _modelObjectsMarshaller; 
	private final Provider<EntityManager> _entityManagerProvider;    
	private final AA14PersonLocatorServices _personLocatorServices;
	
	private final AA14NotifierServicesForPersonLocator _personLocatorIdNotificator;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14EventListenerForPersonLocatorIDReminder(			final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
									   @ModelObjectsMarshaller 	final Marshaller modelObjectsMarshaller,
																final Provider<EntityManager> entityManagerProvider,
																final AA14PersonLocatorServices personLocatorServices,
																// notificator
																final AA14NotifierServicesForPersonLocator personLocatorIdNotificator) {
		_coreCfg = coreCfg;
		_modelObjectsMarshaller = modelObjectsMarshaller;
		_entityManagerProvider = entityManagerProvider;
		_personLocatorServices = personLocatorServices;
		
		// notificator
		_personLocatorIdNotificator = personLocatorIdNotificator;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Subscribe		// subscribes this event listener at the EventBus
	public void onPersonLocatorIDRemindMessage(final AA14PersonLocatorIDRemindMessage opOKEvent) {
		AA14PersonLocatorIDRemindMessage personLocatorIdRemindMessage = opOKEvent;
		// [1] - Get the data
		SecurityContext securityContext = personLocatorIdRemindMessage.getSecurityContext();
		
		// [5] - Send the notification
		_personLocatorIdNotificator.sendPersonLocatorIdRemindMessage(personLocatorIdRemindMessage);
	}
}
