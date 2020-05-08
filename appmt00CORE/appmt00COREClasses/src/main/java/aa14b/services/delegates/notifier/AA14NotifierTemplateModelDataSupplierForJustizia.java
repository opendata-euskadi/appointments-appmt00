package aa14b.services.delegates.notifier;

import java.util.Collection;
import java.util.Map;

import aa14f.model.AA14NotificationOperation;
import aa14f.model.summaries.AA14SummarizedAppointment;
import r01f.locale.Language;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

public class AA14NotifierTemplateModelDataSupplierForJustizia
     extends AA14NotifierTemplateModelDataSupplierBase {
	
	@Override
	protected Map<String,Object> _supplyCommonModelData(final AA14NotificationOperation op,
														final AA14SummarizedAppointment appointment,
														final Language lang,
														final Collection<EMail> mails,final Collection<Phone> phones) {
		Map<String,Object> model = super._supplyCommonModelData(op, appointment, lang, mails, phones);
		
		model.put("serviceLbl",(lang == Language.BASQUE ? "EUSKADIKO JUSTIZIA ADMINISTRAZIOAN PROZEDURAK HASTEKO IDAZKIAK AURKEZTEKO AURRETIKO HITZORDU ZERBITZUA" 
														: "SERVICIO DE CITA PREVIA PARA PRESENTACIÓN DE ESCRITOS DE INICIO EN LA ADMINISTRACIÓN DE JUSTICIA EN EUSKADI"));
		
		model.put("valServicio",appointment.getLocation().getName());
		
		// Footer
    	model.put("cancelUrlWeb", lang == Language.BASQUE ? "https://www.justizia.eus/hasiera"
    													  : "https://www.justizia.eus/inicio"); 
		
    	model.put("cancelWebText",lang == Language.BASQUE ? "joan zaitez "
    														  : "vaya a la web de ");
    	model.put("cancelWebTextTail",lang == Language.BASQUE ? "ren webguera"
    														  : "");
    	model.put("cancelWebTextLink", "Justizia");
    	
		return model;
	}
}
