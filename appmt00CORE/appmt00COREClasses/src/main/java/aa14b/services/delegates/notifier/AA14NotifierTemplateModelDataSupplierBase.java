package aa14b.services.delegates.notifier;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import aa14f.model.AA14NotificationOperation;
import aa14f.model.summaries.AA14SummarizedAppointment;
import r01f.locale.Language;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;
import r01f.util.types.Dates;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.locale.Languages;

abstract class AA14NotifierTemplateModelDataSupplierBase
  	implements AA14NotifierTemplateModelDataSupplier {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	protected static final String DAY_FORMAT= "yyyyMMdd";
	protected static final String HOUR_FORMAT= "hhmmss";

	protected static String[] DAY_OF_WEEK_ES = {"Domingo","Lunes","Martes", "Mi�rcoles","Jueves","Viernes","S�bado"};
	protected static String[] DAY_OF_WEEK_EU = {"Igandea","Astelehena","Asteartea", "Asteazkena","Osteguna","Ostirala","Larunbata"};
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	protected static String _dayOfWeekName(final Date d,
										   final Language lang) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		return lang == Language.BASQUE ? DAY_OF_WEEK_EU[cal.get(Calendar.DAY_OF_WEEK) - 1]
									   : DAY_OF_WEEK_ES[cal.get(Calendar.DAY_OF_WEEK) - 1];	
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Map<String,Object> supplyModelDataForCreate(final AA14SummarizedAppointment appointment,
												  	   final Language lang,
												  	   final Collection<EMail> mails,final Collection<Phone> phones) {
	    Map<String,Object> model = _supplyCommonModelData(AA14NotificationOperation.CREATE,
	    												  appointment,
	    												  lang,
	    												  mails,phones);
    	return model;
	}
	@Override
	public Map<String,Object> supplyModelDataForUpdate(final AA14SummarizedAppointment appointment,
													   final Language lang,
													   final Collection<EMail> mails,final Collection<Phone> phones) {
	    Map<String,Object> model = _supplyCommonModelData(AA14NotificationOperation.UPDATE,
	    												  appointment,
	    												  lang,
	    												  mails,phones);
    	return model;
	}
	@Override
	public Map<String,Object> supplyModelDataForDelete(final AA14SummarizedAppointment appointment,
													   final Language lang,
													   final Collection<EMail> mails,final Collection<Phone> phones) {
	    Map<String,Object> model = _supplyCommonModelData(AA14NotificationOperation.DELETE,
	    												  appointment,
	    												  lang,
	    												  mails,phones);
	    return model;
	}
	@Override
	public Map<String, Object> supplyModelDataForRemindTomorrow(final AA14SummarizedAppointment appointment, 
																final Language lang,
																final Collection<EMail> mails,final  Collection<Phone> phones) {
	    Map<String,Object> model = _supplyCommonModelData(AA14NotificationOperation.REMIND_TOMORROW,
	    												  appointment,
	    												  lang,
	    												  mails,phones);
	    return model;
	}
	@Override
	public Map<String, Object> supplyModelDataForRemindToday(final AA14SummarizedAppointment appointment, 
															 final Language lang,
															 final Collection<EMail> mails,final Collection<Phone> phones) {
	    Map<String,Object> model = _supplyCommonModelData(AA14NotificationOperation.REMIND_TODAY,
	    												  appointment,
	    												  lang,
	    												  mails,phones);
	    return model;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	protected Map<String,Object> _supplyCommonModelData(final AA14NotificationOperation op,
														final AA14SummarizedAppointment appointment,
													    final Language lang,
													    final Collection<EMail> mails,final Collection<Phone> phones) {
		Map<String,Object> model = new HashMap<String,Object>();
	    
	    // Header
		model.put("serviceLbl",(lang == Language.BASQUE ? "HITZORDUA ESKATZEA" 
														: "SERVICIO DE CITA PREVIA"));
	    model.put("serviceName",appointment.getService().getName());
	    switch (op) {
		case DELETE:
			model.put("operation",lang == Language.BASQUE ? "HITZORDU EZEZTATUTA" 
														  : "CITA CANCELADA");
			break;
		case REMIND_TODAY:
		case REMIND_TOMORROW:
			model.put("operation",lang == Language.BASQUE ? "HITZORDUA GOGORARAZTEKO" 
														  : "RECORDATORIO DE CITA");
			break;

		default:
			//new appointment, no operation
			break;
		}
	    
	    

	    model.put("appointmentDate",_dayOfWeekName(appointment.getStartDate(),lang) + " " + appointment.getStartDateFormatted(lang));
	    model.put("appointmentTime",Strings.customized("{}:{}",
	    											   StringUtils.leftPad(Integer.toString(appointment.getStartTime().getHourOfDay()),2,'0'),
	    											   StringUtils.leftPad(Integer.toString(appointment.getStartTime().getMinuteOfHour()),2,'0')));
	    
	    model.put("appointmentId",appointment.getId().asString());
	    
    	model.put("appointmentOrg",appointment.getOrganization().getName());
    	model.put("appointmentDiv",appointment.getDivision().getName());
    	model.put("appointmentService",appointment.getService().getName());
    	model.put("appointmentLoc",appointment.getLocation().getName());
    	
    	model.put("appointmentStartDay",Dates.format(appointment.getStartDate(),DAY_FORMAT));
    	model.put("appointmentStartHour",Dates.format(appointment.getStartDate(),HOUR_FORMAT));
    	model.put("appointmentEndDay",Dates.format(appointment.getEndDate(),DAY_FORMAT));
    	model.put("appointmentEndHour",Dates.format(appointment.getEndDate(),HOUR_FORMAT));
    	
    	// Location data
	    model.put("textLocationData",lang == Language.BASQUE ? "Hitzorduaren lekua" : "Lugar de la cita");

	    model.put("textDepartamento",lang == Language.BASQUE ? "Saila" : "Departamento");
    	model.put("valDepartamento",Strings.customized("{} - {}",
    													appointment.getOrganization().getName(),
    													appointment.getDivision().getName()));

    	model.put("textServicio",lang == Language.BASQUE ? "Zerbitzua" : "Servicio");
    	model.put("valServicio",appointment.getService().getName());
	    
    	model.put("textTerritorio",lang == Language.BASQUE ? "Lurraldea" : "Territorio Hist�rico");
		model.put("appointmentValTerritorio",appointment.getLocation().getCounty());
		
	    model.put("textTramite",lang == Language.BASQUE ? "Izapidea" : "Tr�mite");
    	model.put("valTramite",appointment.getService().getProcedure() != null ? appointment.getService().getProcedure() : "");
	    
    	model.put("textDireccion",lang == Language.BASQUE ? "Helbidea" : "Direcci�n");
    	model.put("appointmentValDireccion",appointment.getLocation().getStreet());
		
    	model.put("textPoblacion",lang == Language.BASQUE ? "Herria" : "Poblaci�n");
    	model.put("appointmentValPoblacion",appointment.getLocation().getMunicipality());
	    
    	model.put("textProvincia",lang == Language.BASQUE ? "Probintzia" : "Provincia");
    	model.put("appointmentValProvincia",appointment.getLocation().getCounty());
	    
    	model.put("textTelefono",lang == Language.BASQUE ? "Telefonoa" : "Tel�fono");
    	model.put("appointmentValTelefono",appointment.getLocation().getPhone());
    	
    	// Personal data
	    model.put("textPersonalData",lang == Language.BASQUE ? "Harremanetarako datuak" : "Datos personales");
	    
	    model.put("textDNI",lang == Language.BASQUE ? "NAN/IFZ/Pasaportea" : "DNI/NIF/Pasaporte");
    	model.put("appointmentDNI",appointment.getPerson().getId().asString());
    	
    	model.put("textLocator", lang == Language.BASQUE ? "Jarraipen kodea" : "Localizador");
    	model.put("locator",appointment.getPersonLocatorId() != null ? appointment.getPersonLocatorId() : "");
    	
	    model.put("textName",lang == Language.BASQUE ? "Izena" : "Nombre");
    	model.put("appointmentName",appointment.getPerson().getName());
	    model.put("textSurName",lang == Language.BASQUE ? "Abizenak" : "Apellidos");
    	model.put("appointmentSurName",appointment.getPerson().getSurname());
    	
    	// details
	    model.put("textDetails",lang == Language.BASQUE ? "Xehetasunak" : "Detalles");
	    if (appointment.getSubject() != null) {
	    	if (appointment.getSubject().getId() != null && !appointment.getSubject().getId().getRaw().equals("-")) {
	    		model.put("textAppointmentExpID",lang == Language.BASQUE ? "Espediente-zenbakia" : "N�mero de expediente");
	    		model.put("appointmentExpID",appointment.getSubject().getId());
	    	}
	    	if (Strings.isNOTNullOrEmpty(appointment.getSubject().getDescription())) {
	    		model.put("appointmentExpDescription",appointment.getSubject().getDescription());
	    	}
	    }
	    
    	// contact info
	    model.put("textPhone",lang == Language.BASQUE ? "Telefonoa" : "Tel�fono");
    	String lstPhones = CollectionUtils.of(phones).toStringCommaSeparated();
    	model.put("appointmentPhone",lstPhones);
	    model.put("textEmail",lang == Language.BASQUE ? "Helbide elektronikoa" : "Correo electr�nico");
    	model.put("appointmentEmail",mails);
    	
    	// Common Footer
    	model.put("includeCancelFooter",op.isIn(AA14NotificationOperation.CREATE,
    										    AA14NotificationOperation.UPDATE,
    										    AA14NotificationOperation.REMIND_TOMORROW) ? true : false);
    	model.put("textCancel",lang == Language.BASQUE ? "Hartu duzun hitzordua baliogabetu edo aldatu nahi baduzu" 
    													: "Si desea  anular o modificar su cita previa");
    	model.put("cancelPhone",lang == Language.BASQUE ? "deitu 012 (94501800) telefono-zenbakira"
    															: "llame al tel&eacute;fono 012 (94501800)");
    	model.put("cancelLocation",lang == Language.BASQUE ? "jo Zuzeneanen arreta-bulego batera"
    															   : "acuda a una oficina de atenci�n de Zuzenean");
    	model.put("cancelWebText",lang == Language.BASQUE ? "joan zaitez "
    														  : "vaya a la web de ");
    	model.put("cancelWebTextTail",lang == Language.BASQUE ? "en webguera"
    														  : "");
    	model.put("cancelWebTextLink", "Zuzenean");
    	
    	// URL Zuzenean
    	model.put("cancelLocList",lang == Language.BASQUE ? "lurraldea" : "ver delegaciones");
    	String urlLocZuzenean = Strings.customized("http://www.zuzenean.euskadi.eus/s68-conpres/{}/contenidos/informacion/zuzenean_atencion_presencial/{}_atencion/oficinas.html",
    											    Languages.countryLowerCase(lang),Languages.countryLowerCase(lang));
    	model.put("cancelUrlLoc",urlLocZuzenean);
    	String urlWebZuzenean = lang == Language.BASQUE ? "http://www.zuzenean.euskadi.eus/hitzorduaeskatzea"
    													: "http://www.zuzenean.euskadi.eus/citaprevia";
    	model.put("cancelUrlWeb",urlWebZuzenean);
    	
    	return model;
    	
	}
}
