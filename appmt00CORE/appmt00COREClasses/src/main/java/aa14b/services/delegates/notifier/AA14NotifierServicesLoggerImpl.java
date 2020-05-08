package aa14b.services.delegates.notifier;

import javax.inject.Inject;
import javax.inject.Singleton;

import aa14b.events.AA14NotificationMessageAboutAppointment;
import aa14f.model.AA14NotificationOperation;
import lombok.extern.slf4j.Slf4j;
import r01f.core.services.notifier.config.NotifierConfigForLog;

/**
 * A notifier that just logs the message
 */
@Singleton
@Slf4j
public class AA14NotifierServicesLoggerImpl
     extends AA14NotifierServicesDelegateImplBase<NotifierConfigForLog> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14NotifierServicesLoggerImpl(final NotifierConfigForLog config) {
		super(config,
			  null);	// no template engine
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void sendNotification(final AA14NotificationOperation op,
								 final AA14NotificationMessageAboutAppointment message) {
		if (_config.isEnabled()) {
			log.warn("\t->logging notification for appointment with oid={}",
					 message.getAppointment().getOid());
			log.warn("\t>Organization: {}",message.getAppointment().getOrganization().getId());
			log.warn("\t>    Division: {}",message.getAppointment().getDivision().getId());
			log.warn("\t>     Service: {}",message.getAppointment().getService().getId());
			log.warn("\t>    Location: {}",message.getAppointment().getLocation().getId()); 
			
			log.warn("\t>Phones: {}",message.getPhones());
			log.warn("\t>EMails: {}",message.getMails());
		}
	}
}
