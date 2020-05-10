package aa14b.services.delegates.notifier;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import aa14b.events.AA14NotificationMessageAboutAppointment;
import aa14b.services.internal.AA14CORESideBusinessConfigServices;
import aa14f.model.AA14NotificationOperation;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.summaries.AA14SummarizedAppointment;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.core.notifier.NotifierBase;
import r01f.core.services.notifier.config.NotifierConfig;
import r01f.internal.R01F;
import r01f.locale.Language;
import r01f.types.Path;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

/**
 * Base notifier service
 */
@Slf4j
@Accessors(prefix="_")
abstract class AA14NotifierServicesDelegateImplBase<C extends NotifierConfig>
	   extends NotifierBase<C,AA14NotificationMessageAboutAppointment> 
    implements AA14NotifierServicesDelegateImpl {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter protected final AA14CORESideBusinessConfigServices _businessConfigServices; 
	@Getter protected final VelocityEngine _templateEngine;
	@Getter protected final AA14NotifierTemplateSelector _templateSelector;
	@Getter protected final AA14NotifierMessageDataSupplier _templateModelDataSupplier;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14NotifierServicesDelegateImplBase(final AA14CORESideBusinessConfigServices businessConfigServices,
												final C notifierConfig,
												final VelocityEngine templateEngine,
												final AA14NotifierTemplateSelector templateSelector) {
		super(notifierConfig);
		
		_businessConfigServices = businessConfigServices;
		_templateEngine = templateEngine;
		_templateSelector = templateSelector;
		_templateModelDataSupplier = new AA14NotifierMessageDataSupplier(businessConfigServices);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void sendNotification(final AA14NotificationMessageAboutAppointment message) {
		throw new UnsupportedOperationException("Use sendNotification(op,message)");
	}
	public String composeMessageBody(final AA14NotificationOperation op,
								     final AA14SummarizedAppointment appointment, 
									 final Collection<EMail> mails,final Collection<Phone> phones) {
		if (_templateEngine == null || _templateSelector == null || _templateModelDataSupplier == null) {
			throw new IllegalStateException("Not enought state data to apply the template!");
		}
		
		// Get the lang from the preferred customer language
		Language lang = appointment.getPerson().getPreferredLang() != null ? appointment.getPerson().getPreferredLang()
																		   : Language.DEFAULT;
		// Get the model data
	    Map<String,Object> model = _modelDataFor(op,
	    										 appointment,
	    										 lang,
	    										 mails,phones);
    	// apply template
    	Path msgTemplatePath = _msgTemplatePathFor(op,
    											   appointment.getBusinessId(),
    											   appointment.getOrganization().getId(),appointment.getDivision().getId(),appointment.getService().getId(),appointment.getLocation().getId(),
    											   lang);
	    
	    VelocityContext context = new VelocityContext(model);
		StringWriter sw = new StringWriter();
		_templateEngine.mergeTemplate(msgTemplatePath.asRelativeString(),
			  						  R01F.DEFAULT_CHARSET.name(),
			  						  context,
			  						  sw);
		sw.flush();
	    return sw.toString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	private void _debugModelData(final Map<String,Object> model) {
		for (Map.Entry<String,Object> me : model.entrySet()) {
			log.warn("\t\t\t{} = {}",
					 me.getKey(),me.getValue());
		}
	}
	protected Map<String,Object> _modelDataFor(final AA14NotificationOperation op,
											   final AA14SummarizedAppointment appointment,
											   final Language lang,
											   final Collection<EMail> mails,final Collection<Phone> phones) {
	    Map<String,Object> model = null;
	    if (op == AA14NotificationOperation.CREATE) {
	    	model = _templateModelDataSupplier.supplyModelDataForCreate(appointment,
	    													   			lang,
	    													   			mails,phones);
	    } else if (op == AA14NotificationOperation.UPDATE) {
	    	model = _templateModelDataSupplier.supplyModelDataForUpdate(appointment,
	    													   			lang,
	    													   			mails,phones);
	    } else if (op == AA14NotificationOperation.DELETE) {
	    	model = _templateModelDataSupplier.supplyModelDataForDelete(appointment,
	    													   			lang,
	    													   			mails,phones);
	    } else if (op == AA14NotificationOperation.REMIND_TOMORROW) {
	    	model = _templateModelDataSupplier.supplyModelDataForRemindTomorrow(appointment,
	    													   		   			lang,
	    													   		   			mails,phones);
	    } else if (op == AA14NotificationOperation.REMIND_TODAY) {
	    	model = _templateModelDataSupplier.supplyModelDataForRemindToday(appointment,
	    													   				 lang,
	    													   				 mails,phones);
	    } else {
	    	throw new IllegalArgumentException("No template path for operation = " + op);
	    }
	    return model;
	}
	protected Path _msgTemplatePathFor(final AA14NotificationOperation op,
									   final AA14BusinessID businessId,
									   final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									   final Language lang) {
	    Path outMsgTemplatePath;
	    if (op == AA14NotificationOperation.CREATE) {
	    	outMsgTemplatePath = _templateSelector.createTemplateFor(businessId,
	    															 org,div,srvc,loc,
	    														 	 lang);
	    } else if (op == AA14NotificationOperation.UPDATE) {
	    	outMsgTemplatePath = _templateSelector.updateTemplateFor(businessId,
	    															 org,div,srvc,loc,
	    															 lang);
	    } else if (op == AA14NotificationOperation.DELETE) {
	    	outMsgTemplatePath = _templateSelector.deleteTemplateFor(businessId,
	    															 org,div,srvc,loc,
	    															 lang);
	    } else if (op == AA14NotificationOperation.REMIND_TOMORROW) {
	    	outMsgTemplatePath = _templateSelector.remindTomorrowTemplateFor(businessId,
	    																	 org,div,srvc,loc,
	    																	 lang);
	    } else if (op == AA14NotificationOperation.REMIND_TODAY) {
	    	outMsgTemplatePath = _templateSelector.remindTodayTemplateFor(businessId,
	    																  org,div,srvc,loc,
	    																  lang);
	    } else {
	    	throw new IllegalArgumentException("No template path for operation = " + op);
	    }
	    log.info("\t\t... using template at {}",outMsgTemplatePath);
	    return outMsgTemplatePath;
	}
}
