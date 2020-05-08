package aa14b.services.delegates.notifier;

import java.util.Collection;
import java.util.Map;

import aa14f.model.AA14NotificationOperation;
import aa14f.model.summaries.AA14SummarizedAppointment;
import r01f.locale.Language;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

public class AA14NotifierTemplateModelDataSupplierForMedicalService
     extends AA14NotifierTemplateModelDataSupplierBase {
	@Override
	protected Map<String,Object> _supplyCommonModelData(final AA14NotificationOperation op,
															final AA14SummarizedAppointment appointment,
														    final Language lang,
														    final Collection<EMail> mails,final Collection<Phone> phones) {
		Map<String,Object> model = super._supplyCommonModelData(op, appointment, lang, mails, phones);
	
    	//SMS
    	model.put("textCancelSMS",lang == Language.BASQUE ? "EZIN bazara bertaratu, mesedez ezezta ezazu zure zita." 
    													: "Si NO puedes asistir, por favor anula tu cita.");
    		
    	model.put("textPhone",lang == Language.BASQUE ? "Telefonoak" : "Teléfonos");
    		 
    	//footer 		
    	model.put("textCancel",lang == Language.BASQUE ? "Ezein arrazoigatik EZIN bazara bertaratu, mesedez ezezta ezazu zure aurretiko zita." 
    													: "Si por alg&uacute;n motivo NO puedes asistir, por favor anula tu cita previa.");
    		
    	model.put("cancelUrlWeb", lang == Language.BASQUE ? "http://www.euskadi.eus/zerbitzu-medikoa/"
    													: "http://www.euskadi.eus/servicio-medico/");
    		
    	model.put("cancelWebText",lang == Language.BASQUE ? "joan zaitez "
    														  : "vaya a la web de ");
    	model.put("cancelWebTextTail",lang == Language.BASQUE ? "ren webguera"
    														  : "");
    	model.put("cancelWebTextLink", lang == Language.BASQUE ? "Zerbitzu medikoa"
    														  : "Servicio médico");
    	return model;
	}
}
