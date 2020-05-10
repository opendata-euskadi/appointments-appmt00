package aa14b.services.delegates.notifier;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import aa14b.events.AA14NotificationMessageAboutAppointment;
import aa14b.services.internal.AA14CORESideBusinessConfigServices;
import aa14f.model.AA14NotificationOperation;
import aa14f.model.summaries.AA14SummarizedAppointment;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import r01f.core.services.notifier.NotifierServiceForVoicePhoneCall;
import r01f.core.services.notifier.config.NotifierConfigForVoice;
import r01f.internal.R01F;
import r01f.locale.Language;
import r01f.patterns.Factory;
import r01f.types.Path;
import r01f.util.types.collections.CollectionUtils;

/**
 * A notifier that just logs the message
 */
@Singleton
@Slf4j
public class AA14NotifierServicesVoiceImpl
     extends AA14NotifierServicesDelegateImplBase<NotifierConfigForVoice> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final NotifierServiceForVoicePhoneCall _voiceNotifier;

	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14NotifierServicesVoiceImpl(final AA14CORESideBusinessConfigServices businessConfigServices,
										 final NotifierConfigForVoice config,final NotifierServiceForVoicePhoneCall voiceNotifier,
										 final VelocityEngine templateEngine) {
		super(businessConfigServices,
			  config,
			  templateEngine,
			  new AA14NotifierTemplateSelectorVoiceImpl(businessConfigServices));
		_voiceNotifier = voiceNotifier;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void sendNotification(final AA14NotificationOperation op,
								 final AA14NotificationMessageAboutAppointment message) {
		if (!this.isEnabledConsidering(_voiceNotifier)) {
			log.warn("Voice notifier is DISABLED!");
			return;
		}
		log.warn("[VoiceNotifier ({})]================================================",
				 _voiceNotifier.getClass().getSimpleName());
		if (CollectionUtils.isNullOrEmpty(message.getPhonesSanitized())) {
			log.warn("... NO phones to notify to");
			return;
		}
		_voiceNotifier.notifyAll(_config.getFrom(),message.getPhonesSanitized(),
								 new Factory<String>() {
										@Override
										public String create() {
											return _composeVoiceMessage(_templateEngine,Path.from("todo"),
																		message.getAppointment());
										}
								 });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static String _composeVoiceMessage(final VelocityEngine velocityEngine,final Path alertMsgTemplatePath,
											   final AA14SummarizedAppointment appointment) {
		Language lang = appointment.getPerson().getPreferredLang() != null ? appointment.getPerson().getPreferredLang()
																		   : Language.DEFAULT;
	    // Text... using velocity
	    Map<String,Object> model = new HashMap<String,Object>();
    	model.put("appointmentDate",appointment.getStartDateFormatted(lang));
	    model.put("appointmentId",appointment.getId());
	    
	    VelocityContext context = new VelocityContext(model);
		StringWriter sw = new StringWriter();
		velocityEngine.mergeTemplate(alertMsgTemplatePath.asRelativeString(),
			  						 R01F.DEFAULT_CHARSET.name(),
			  						 context,
			  						 sw);
		sw.flush();
	    return sw.toString();
	}
}
