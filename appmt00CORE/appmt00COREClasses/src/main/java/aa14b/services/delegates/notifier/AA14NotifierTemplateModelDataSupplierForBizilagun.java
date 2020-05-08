package aa14b.services.delegates.notifier;

import java.util.Collection;
import java.util.Map;

import aa14f.model.AA14NotificationOperation;
import aa14f.model.summaries.AA14SummarizedAppointment;
import r01f.locale.Language;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

public class AA14NotifierTemplateModelDataSupplierForBizilagun
     extends AA14NotifierTemplateModelDataSupplierBase {
	
	
	@Override
	protected Map<String,Object> _supplyCommonModelData(final AA14NotificationOperation op,
															final AA14SummarizedAppointment appointment,
														    final Language lang,
														    final Collection<EMail> mails,final Collection<Phone> phones) {
		Map<String,Object> model = super._supplyCommonModelData(op, appointment, lang, mails, phones);
		
		//XXX temporal restriction only by telephone
    		
    	model.put("instructions",lang == Language.BASQUE ? "Telefonoz deituko dizugu erreserbatutako egunean eta orduan."
    														  : "Le llamaremos por teléfono el día y hora reservado.");
    	
    	model.put("warningTitle", lang == Language.BASQUE ? "TELEFONO BIDEZKO ARRETA SOILIK"
    														  : "ATENCIÓN EXCLUSIVAMENTE TELEFÓNICA");
    		
    	model.put("warningMessage", lang == Language.BASQUE ? "Berriro abisatu arte, zerbitzu horretako hitzorduak telefono bidezkoak izango dira. Hitzordu bat erreserbatuta baduzu, telefonoz deituko dizugu erreserbatutako egunean eta orduan."
    														  : "Hasta nuevo aviso, las citas de este servicio serán telefónicas. Si ya tienes reservada una cita, te llamaremos por teléfono el día y hora reservado.");
		return model;
	}
}
