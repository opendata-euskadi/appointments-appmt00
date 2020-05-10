package aa14b.services.delegates.notifier;

import java.util.Collection;

import javax.inject.Inject;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import aa14b.events.AA14NotificationMessageAboutAppointment;
import aa14b.services.internal.AA14CORESideBusinessConfigServices;
import aa14f.model.AA14NotificationOperation;
import aa14f.model.config.AA14NotifierFromConfig;
import aa14f.model.summaries.AA14SummarizedAppointment;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import r01f.core.services.mail.model.EMailRFC822Address;
import r01f.core.services.mail.notifier.JavaMailSenderNotifierService;
import r01f.core.services.notifier.NotifierServiceForEMail;
import r01f.core.services.notifier.config.NotifierConfigForEMail;
import r01f.locale.Language;
import r01f.patterns.Factory;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * EMail notifier
 */
@Slf4j
public class AA14NotifierServicesEMailImpl
     extends AA14NotifierServicesDelegateImplBase<NotifierConfigForEMail> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final NotifierServiceForEMail _mailNotifier;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14NotifierServicesEMailImpl(final AA14CORESideBusinessConfigServices businessConfigServices,
										 final NotifierConfigForEMail notifierConfig,final NotifierServiceForEMail notifier,										
										 final VelocityEngine templateEngine) {
		super(businessConfigServices,
			  notifierConfig,
			  templateEngine,
			  new AA14NotifierTemplateSelectorEMailImpl(businessConfigServices));
		_mailNotifier = notifier;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void sendNotification(final AA14NotificationOperation op,
								 final AA14NotificationMessageAboutAppointment message) {
		if (!this.isEnabledConsidering(_mailNotifier)) {
			log.warn("EMail notifier is DISABLED!");
			return;
		}
		log.warn("[EMailNotifier ({})]================================================",
				 _mailNotifier.getClass().getSimpleName());
		
		if (CollectionUtils.isNullOrEmpty(message.getMails())) {
			log.warn("\t--> there aren't eMails to send message...");
			return;
		}
		// Get the business config where the template id (path) and from address is configured
		AA14NotifierFromConfig notifFromConfig = _businessConfigServices.getCORESideCachedBusinessConfigs()
																		.getFor(message.getAppointment().getBusinessId())
																		.getNotifierFromConfigFor(message.getAppointment().getOrganization().getOid(),
																								  message.getAppointment().getDivision().getOid(),
																								  message.getAppointment().getService().getOid());
		EMail fromEMail = notifFromConfig.getEmailFromAddress();
		String fromOwner = notifFromConfig.getEmailFromAddressOwner();
		EMailRFC822Address fromAddr = fromEMail != null ? EMailRFC822Address.of(fromEMail,
																				fromOwner)
														: _config.getFrom() != null ? _config.getFrom()	// system-wide from address 
																					: EMailRFC822Address.of("zuzenean-noreplay@euskadi.eus");	// last resource!
		// send the notification
		for (EMail to : message.getMails()) {
			_sendEMailMessage(op,
							  fromAddr,EMailRFC822Address.of(to),
							  message.getAppointment(), 
							  message.getMails(),message.getPhones());
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Send a mail message when an alarm message is received
	 * @param op
	 * @param to
	 * @param org
	 * @param log
	 * @param agent
	 */
	private void _sendEMailMessage(final AA14NotificationOperation op,
								   final EMailRFC822Address from,final EMailRFC822Address to,
								   final AA14SummarizedAppointment appointment,
								   final Collection<EMail> emails,final Collection<Phone> phones) {
		log.info("\t-->sending email to {} using {}",to,_mailNotifier.getClass());

		// [1] - Create the subject & body
		final String subject = _composeMailMessageSubject(appointment);
		final String body = this.composeMessageBody(op,
													appointment,
													emails,phones);
		  // [2] - Send the message
        _mailNotifier.notify(from,to,
    						 // mime message factory
    						 new Factory<MimeMessage>() {
									@Override @SneakyThrows
									public MimeMessage create() {
										JavaMailSender mailSender = ((JavaMailSenderNotifierService)_mailNotifier).getSpringJavaMailSender();
										MimeMessage mimeMessage = mailSender.createMimeMessage();
									    MimeMessageHelper msgHelper = new MimeMessageHelper(mimeMessage,
									    												    true);	// multi-part!!
									    // To & From
									    msgHelper.setTo(to.asRFC822InternetAddress());
									    msgHelper.setFrom(from.asRFC822InternetAddress());

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
	private static String _composeMailMessageSubject(final AA14SummarizedAppointment appointment) {
		Language lang = appointment.getPerson().getPreferredLang() != null ? appointment.getPerson().getPreferredLang()
																		   : Language.DEFAULT;
		String outSubject = Strings.customized("{} - {} > {} {}:{}",
											   lang == Language.BASQUE ? "HITZORDUA ESKATZEA" : "SERVICIO DE CITA PREVIA",
											   appointment.getService().getName().toUpperCase(),
											   appointment.getStartDateFormatted(lang),
											   StringUtils.leftPad(Integer.toString(appointment.getStartTime().getHourOfDay()),2,'0'),
											   StringUtils.leftPad(Integer.toString(appointment.getStartTime().getMinuteOfHour()),2,'0'));
		
		return outSubject;
	}
}
