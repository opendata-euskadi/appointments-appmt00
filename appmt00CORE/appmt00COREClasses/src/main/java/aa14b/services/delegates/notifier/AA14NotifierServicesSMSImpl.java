package aa14b.services.delegates.notifier;

import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Maps;

import aa14b.events.AA14NotificationMessageAboutAppointment;
import aa14b.notifier.config.AA14NotifierConfigForSMS;
import aa14f.model.AA14NotificationOperation;
import aa14f.model.summaries.AA14SummarizedAppointment;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import r01f.core.services.notifier.NotifierServiceForSMS;
import r01f.core.services.notifier.config.NotifierConfigForSMS;
import r01f.locale.Language;
import r01f.patterns.Factory;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Latinia notifier, sends SMS to Latinia services.
 */
@Singleton
@Slf4j
public class AA14NotifierServicesSMSImpl
     extends AA14NotifierServicesDelegateImplBase<NotifierConfigForSMS> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final NotifierServiceForSMS _smsNotifier;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14NotifierServicesSMSImpl(final NotifierConfigForSMS notifierConfig,final NotifierServiceForSMS notifier,
									   final VelocityEngine templateEngine) {
		super(notifierConfig,
			  templateEngine,
			  new AA14NotifierTemplateSelectorSMSImpl(notifierConfig.getAppConfigAs(AA14NotifierConfigForSMS.class)));
		_smsNotifier = notifier;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void sendNotification(final AA14NotificationOperation op,
								 final AA14NotificationMessageAboutAppointment message) {
		if (!this.isEnabledConsidering(_smsNotifier)) {
			log.warn("SMS notifier is DISABLED!");
			return;
		}
		log.warn("[SMS Notifier ({})]================================================",
				 _smsNotifier.getClass().getSimpleName());
		Collection<Phone> phones = message.getPhonesSanitized();
		if (CollectionUtils.isNullOrEmpty(phones)) {
			log.warn("... NO phones to notify to");
			return;
		}
		_smsNotifier.notifyAll(_config.getFrom(),phones,
							   new Factory<String>() {
										@Override
										public String create() {
											return AA14NotifierServicesSMSImpl.this.composeMessageBody(op,
																									   message.getAppointment(),
																									   message.getMails(),message.getPhones());
										}
							   });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PRIVATE METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected Map<String,Object> _modelDataFor(final AA14NotificationOperation op,
											   final AA14SummarizedAppointment appointment,
											   final Language lang,
											   final Collection<EMail> mail,final Collection<Phone> phones) {
		// get the model data
	    Map<String,Object> model = super._modelDataFor(op,
	    											   appointment,
	    											   lang,
	    											   mail,phones);
	    // remove accents since SMS service does NOT support them
	    Map<String,Object> outModel = Maps.newHashMapWithExpectedSize(model.size());
	    for (Map.Entry<String,Object> me : model.entrySet()) {
	    	if (me.getValue() == null) continue;
	    	outModel.put(me.getKey(),
	    			     Strings.removeAccents(me.getValue().toString()));
	    }
	    // return
	    return outModel;
	}
}
