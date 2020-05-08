package aa14b.services.delegates.notifier;

import java.util.Collection;
import java.util.Map;

import aa14f.model.AA14NotificationOperation;
import aa14f.model.summaries.AA14SummarizedAppointment;
import r01f.locale.Language;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

public class AA14NotifierTemplateModelDataSupplierForZuzenean
     extends AA14NotifierTemplateModelDataSupplierBase {
	@Override
	protected Map<String,Object> _supplyCommonModelData(final AA14NotificationOperation op,
															final AA14SummarizedAppointment appointment,
														    final Language lang,
														    final Collection<EMail> mails,final Collection<Phone> phones) {
		Map<String,Object> model = super._supplyCommonModelData(op, appointment, lang, mails, phones);
	
    	
    	model.put("cancelUrlWeb", lang == Language.BASQUE ? "http://www.euskadi.eus/zerbitzu-medikoa/"
    													: "http://www.euskadi.eus/citazuzenean");
    	return model;
	}
}
