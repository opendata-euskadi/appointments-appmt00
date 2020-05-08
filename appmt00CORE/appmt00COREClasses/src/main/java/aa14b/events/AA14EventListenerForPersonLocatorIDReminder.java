package aa14b.events;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Singleton;

import aa14b.services.delegates.notifier.AA14NotifierServicesForPersonLocator;
import aa14f.api.interfaces.AA14PersonLocatorServices;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14IDs.AA14PersonLocatorID;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.events.COREServiceMethodExecEventListeners.COREServiceMethodExecOKEventListener;
import r01f.events.COREServiceMethodExecEvents.COREServiceMethodExecOKEvent;
import r01f.locale.Language;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.types.contact.EMail;
import r01f.types.contact.PersonID;

/**
 * Listens to {@link AA14PersonLocatorIDRemindEvent} events
 */
@Slf4j
@Singleton
public class AA14EventListenerForPersonLocatorIDReminder 
  implements COREServiceMethodExecOKEventListener {
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
	@Override
	@Subscribe		// subscribes this event listener at the EventBus
	public void onPersistenceOperationOK(final COREServiceMethodExecOKEvent opOKEvent) {
		if (!(opOKEvent instanceof AA14PersonLocatorIDRemindEvent)) return;		// not handled
		
		AA14PersonLocatorIDRemindEvent personLocatorIdRemindEvent = (AA14PersonLocatorIDRemindEvent)opOKEvent;
		// [1] - Get the data
		SecurityContext securityContext = personLocatorIdRemindEvent.getSecurityContext();
		AA14OrganizationID orgId = personLocatorIdRemindEvent.getOrgId();
		PersonID personId = personLocatorIdRemindEvent.getPersonId();
		EMail contactEMail = personLocatorIdRemindEvent.getContactEMail();	
		Language contactLang = personLocatorIdRemindEvent.getLanguage();
		AA14PersonLocatorID personLocatorId = personLocatorIdRemindEvent.getPersonLocatorId();
		
		// [5] - Send the notification
		_personLocatorIdNotificator.sendPersonLocatorIdRemindMessage(orgId,
						   											 personId,contactEMail,contactLang,
						   											 personLocatorId);
	}
}
