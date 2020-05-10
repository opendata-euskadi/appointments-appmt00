package aa14b.services.delegates.notifier;

import javax.inject.Inject;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import aa14b.events.AA14PersonLocatorIDRemindMessage;
import aa14b.services.internal.AA14CORESideBusinessConfigServices;
import aa14f.model.config.AA14NotifierFromConfig;
import aa14f.model.oids.AA14IDs.AA14PersonLocatorID;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import r01f.core.services.mail.model.EMailRFC822Address;
import r01f.core.services.mail.notifier.JavaMailSenderNotifierService;
import r01f.core.services.notifier.NotifierServiceForEMail;
import r01f.core.services.notifier.config.NotifierConfigForEMail;
import r01f.locale.Language;
import r01f.patterns.Factory;
import r01f.service.ServiceCanBeDisabled;
import r01f.types.contact.EMail;
import r01f.util.types.Strings;

@Slf4j
public class AA14NotifierServicesForPersonLocator {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	// services needed to send emails
	private final AA14CORESideBusinessConfigServices _businessConfigServices;
	private final NotifierConfigForEMail _notifierConfig;
	private final NotifierServiceForEMail _mailNotifier;
	private final VelocityEngine _templateEngine;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14NotifierServicesForPersonLocator(final AA14CORESideBusinessConfigServices businessConfigServices,
											 	final NotifierConfigForEMail notifierConfig,final NotifierServiceForEMail notifier,
												final VelocityEngine templateEngine) {
		_businessConfigServices = businessConfigServices;
		_notifierConfig = notifierConfig;
		_mailNotifier = notifier;
		_templateEngine = templateEngine;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public void sendPersonLocatorIdRemindMessage(final AA14PersonLocatorIDRemindMessage message) {
		log.info("=======================================================================");
		log.info("send [person locator reminder for personId={} to email={}",message.getPersonId(),message.getContactEMail());
		log.info("=======================================================================");
		boolean isEnabled = _notifierConfig.isEnabled();
		if (_mailNotifier instanceof ServiceCanBeDisabled) {
			ServiceCanBeDisabled serviceCanBeDisabled = (ServiceCanBeDisabled)_mailNotifier;
			if (serviceCanBeDisabled.isDisabled()) isEnabled = false;
		}
		if (isEnabled) {
			_sendEMailMessage(message);
		} else {
			log.warn("Mail sending is DISABLED");
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Send a mail message when an alarm message is received
	 * @param orgId
	 * @param to
	 * @param lang
	 * @param personLocatorId
	 */
	private void _sendEMailMessage(final AA14PersonLocatorIDRemindMessage message) {
		log.info("\t-->sending [person loctor] reminder email to {} using {}",
				 message.getContactEMail(),_mailNotifier.getClass());
		
		// Get the business config where the template id (path) and from address is configured
		AA14NotifierFromConfig notifFromConfig = _businessConfigServices.getCORESideCachedBusinessConfigs()
																		.getFor(message.getOrgId())
																		.getNotifierFromConfigFor(message.getOrgId(),null,null);
		EMail fromEMail = notifFromConfig.getEmailFromAddress();
		String fromOwner = notifFromConfig.getEmailFromAddressOwner();
		EMailRFC822Address fromAddr = fromEMail != null ? EMailRFC822Address.of(fromEMail,
																				fromOwner)
														: _notifierConfig.getFrom() != null ? _notifierConfig.getFrom()	// system-wide from address 
																							: EMailRFC822Address.of("zuzenean-noreplay@euskadi.eus");	// last resource!
		
		// [1] - Create a MimeMessagePreparator
		String subject = _composeMailMessageSubject(message.getLanguage());
		String body = _composeMailMessageBody(message.getLanguage(),
											  message.getPersonLocatorId());
		
		
		EMailRFC822Address emailTo = EMailRFC822Address.of(message.getContactEMail());
		
		// [2] - Send the message
		_mailNotifier.notify(fromAddr,emailTo,
							 // mime message factory
    						 new Factory<MimeMessage>() {
									@Override @SneakyThrows
									public MimeMessage create() {
										JavaMailSender mailSender = ((JavaMailSenderNotifierService)_mailNotifier).getSpringJavaMailSender();
										MimeMessage mimeMessage = mailSender.createMimeMessage();
									    MimeMessageHelper msgHelper = new MimeMessageHelper(mimeMessage,
									    												    true);	// multi-part!!
									    // To & From
									    msgHelper.setTo(emailTo.asRFC822Address());
									    msgHelper.setFrom(fromAddr.asRFC822Address());

									    // Subject & Text
									    msgHelper.setSubject(subject);
									    msgHelper.setText(body,
									    				  true);	// html message
								        return mimeMessage;
									}
							 });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIL MESSAGE COMPOSING
/////////////////////////////////////////////////////////////////////////////////////////	
	private static String _composeMailMessageSubject(final Language lang) {
		String outSubject = Strings.customized("{}: {}",
											   lang == Language.BASQUE ? "HITZORDUA ESKATZEA" : "SERVICIO DE CITA PREVIA",
											   lang == Language.BASQUE ? "Bilagailuaren oroigarria" : "Recordatorio de localizador");
		
		return outSubject;
	}
	private static String _composeMailMessageBody(final Language lang,
												  final AA14PersonLocatorID personLocatorId) {
		String outBody = Strings.customized("{}: {}",
											lang == Language.BASQUE ? "Hemen duzu zure hitzorduak kudeatzeko bilagailua" : "Aqui tienes el localizador que te permitir� gestionar tus citas",
											personLocatorId);
		return outBody;
	}
}
