package aa14b.services.delegates.notifier;

import java.util.Collection;
import java.util.Map;

import aa14f.model.summaries.AA14SummarizedAppointment;
import r01f.locale.Language;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

/**
 * Supplies the model data to be used by the templating engine
 */
public interface AA14NotifierTemplateModelDataSupplier {
	
	public Map<String,Object> supplyModelDataForCreate(final AA14SummarizedAppointment appointment,
												 	   final Language lang,
												 	   final Collection<EMail> mails,final Collection<Phone> phones);
	
	public Map<String,Object> supplyModelDataForUpdate(final AA14SummarizedAppointment appointment,
												 	   final Language lang,
												 	   final Collection<EMail> mails,final Collection<Phone> phones);
	
	public Map<String,Object> supplyModelDataForDelete(final AA14SummarizedAppointment appointment,
												 	   final Language lang,
												 	   final Collection<EMail> mails,final Collection<Phone> phones);
	public Map<String,Object> supplyModelDataForRemindTomorrow(final AA14SummarizedAppointment appointment,
												 	   		   final Language lang,
												 	   		   final Collection<EMail> mails,final Collection<Phone> phones);
	public Map<String,Object> supplyModelDataForRemindToday(final AA14SummarizedAppointment appointment,
												 	   		final Language lang,
												 	   		final Collection<EMail> mails,final Collection<Phone> phones);
}
