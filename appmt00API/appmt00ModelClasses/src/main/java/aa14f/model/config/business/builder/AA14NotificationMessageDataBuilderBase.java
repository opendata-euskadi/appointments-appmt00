package aa14f.model.config.business.builder;

import java.util.Collection;

import aa14f.model.config.AA14NotificationMessageData;
import r01f.util.types.collections.Lists;

public abstract class AA14NotificationMessageDataBuilderBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public Collection<AA14NotificationMessageData> createCommonMessageData() {
		Collection<AA14NotificationMessageData> outData = Lists.newArrayList();
	    
	    // Header
		outData.add(AA14NotificationMessageData.create("serviceLbl",
													   "SERVICIO DE CITA PREVIA","HITZORDUA ESKATZEA"));

    	// Location data
	    outData.add(AA14NotificationMessageData.create("textLocationData",
	    											   "Lugar de la cita","Hitzorduaren lekua"));

	    outData.add(AA14NotificationMessageData.create("textDepartamento",
	    											   "Departamento","Saila"));

    	outData.add(AA14NotificationMessageData.create("textServicio",
    												   "Servicio","Zerbitzua"));
	    
    	outData.add(AA14NotificationMessageData.create("textTerritorio",
    												   "Territorio Hist�rico","Lurraldea"));    	
    	outData.add(AA14NotificationMessageData.create("textProvincia",
    												   "Provincia","Probintzia"));
    	outData.add(AA14NotificationMessageData.create("textPoblacion",
    												   "Poblaci�n","Herria"));
    	outData.add(AA14NotificationMessageData.create("textDireccion",
    												   "Direcci�n","Helbidea"));
	    
    	outData.add(AA14NotificationMessageData.create("textTelefono",
    												   "Tel�fono","Telefonoa"));
    	
    	// Personal data
	    outData.add(AA14NotificationMessageData.create("textPersonalData",
	    											   "Datos personales","Harremanetarako datuak"));
	    
	    outData.add(AA14NotificationMessageData.create("textDNI",
	    											   "DNI/NIF/Pasaporte","NAN/IFZ/Pasaportea"));
    	
	    outData.add(AA14NotificationMessageData.create("textName",
	    											   "Nombre","Izena"));
	    outData.add(AA14NotificationMessageData.create("textSurName",
	    											   "Apellidos","Abizenak"));
	    
    	outData.add(AA14NotificationMessageData.create("textLocator", 
    												   "Localizador","Jarraipen kodea"));
    	
    	// details
	    outData.add(AA14NotificationMessageData.create("textDetails",
	    											   "Detalles","Xehetasunak"));
	    
	    outData.add(AA14NotificationMessageData.create("textTramite",
	    											   "Tr�mite","Izapidea"));
	    outData.add(AA14NotificationMessageData.create("textAppointmentExpID",
	    											   "Número de expediente","Espediente-zenbakia"));
	    
    	// contact info
	    outData.add(AA14NotificationMessageData.create("textPhone",
	    											   "Tel�fono","Telefonoa"));
	    outData.add(AA14NotificationMessageData.create("textEmail",
	    											   "Correo electr�nico","Helbide elektronikoa"));
    	
	    // footer
    	outData.add(AA14NotificationMessageData.create("textCancel",
    												   "Si desea  anular o modificar su cita previa","Hartu duzun hitzordua baliogabetu edo aldatu nahi baduzu"));
    	outData.add(AA14NotificationMessageData.create("cancelPhone",
    												   "llame al tel&eacute;fono 012 (94501800)","deitu 012 (94501800) telefono-zenbakira"));
    	outData.add(AA14NotificationMessageData.create("cancelLocation",
    												   "acuda a una oficina de atenci�n de Zuzenean","jo Zuzeneanen arreta-bulego batera"));
    	outData.add(AA14NotificationMessageData.create("cancelWebText",
    												   "vaya a la web de ","joan zaitez "));
    	outData.add(AA14NotificationMessageData.create("cancelWebTextTail",
    												   "","en webguera"));
    	outData.add(AA14NotificationMessageData.create("cancelWebTextLink", 
    												   "Zuzenean","Zuzenean"));
    	// URL 
    	outData.add(AA14NotificationMessageData.create("cancelLocList",
    												   "ver delegaciones","lurraldea"));
    	return outData;
	}
	public Collection<AA14NotificationMessageData> createCreateMessageData() {
		Collection<AA14NotificationMessageData> outData = Lists.newArrayList();
		return outData;
	}
	public Collection<AA14NotificationMessageData> createUpdateMessageData() {
		Collection<AA14NotificationMessageData> outData = Lists.newArrayList();
		return outData;
	}
	public Collection<AA14NotificationMessageData> createDeleteMessageData() {
		Collection<AA14NotificationMessageData> outData = Lists.newArrayList();
		outData.add(AA14NotificationMessageData.create("appointmentDeleted",
													   "CITA CANCELADA","HITZORDU EZEZTATUTA"));
		return outData;
	}
	public Collection<AA14NotificationMessageData> createRemindMessageData() {
		Collection<AA14NotificationMessageData> outData = Lists.newArrayList();
		outData.add(AA14NotificationMessageData.create("appointmentReminderForToday",
													   "RECORDATORIO DE CITA","HITZORDUA GOGORARAZTEKO"));
		return outData;
	}
}
