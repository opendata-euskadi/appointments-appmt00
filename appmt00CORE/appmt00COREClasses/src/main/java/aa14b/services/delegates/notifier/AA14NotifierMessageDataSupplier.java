package aa14b.services.delegates.notifier;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

import aa14b.services.internal.AA14CORESideBusinessConfigServices;
import aa14f.model.AA14NotificationOperation;
import aa14f.model.config.AA14NotificationMessageData;
import aa14f.model.config.AA14NotifierMessageComposingConfig;
import aa14f.model.summaries.AA14SummarizedAppointment;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;
import r01f.util.types.Dates;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.locale.Languages;

@RequiredArgsConstructor
public class AA14NotifierMessageDataSupplier {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	protected static final String DAY_FORMAT= "yyyyMMdd";
	protected static final String HOUR_FORMAT= "hhmmss";

	protected static String[] DAY_OF_WEEK_ES = {"Domingo","Lunes","Martes", "Miercoles","Jueves","Viernes","S�bado"};
	protected static String[] DAY_OF_WEEK_EU = {"Igandea","Astelehena","Asteartea", "Asteazkena","Osteguna","Ostirala","Larunbata"};
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * provides core-side config access
	 */
	private final AA14CORESideBusinessConfigServices _businessConfigServices;
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	public Map<String,Object> supplyModelDataForCreate(final AA14SummarizedAppointment appointment,
												  	   final Language lang,
												  	   final Collection<EMail> mails,final Collection<Phone> phones) {
	    Map<String,Object> model = _supplyCommonModelData(AA14NotificationOperation.CREATE,
	    												  appointment,
	    												  lang,
	    												  mails,phones);
    	return model;
	}
	public Map<String,Object> supplyModelDataForUpdate(final AA14SummarizedAppointment appointment,
													   final Language lang,
													   final Collection<EMail> mails,final Collection<Phone> phones) {
	    Map<String,Object> model = _supplyCommonModelData(AA14NotificationOperation.UPDATE,
	    												  appointment,
	    												  lang,
	    												  mails,phones);
    	return model;
	}
	public Map<String,Object> supplyModelDataForDelete(final AA14SummarizedAppointment appointment,
													   final Language lang,
													   final Collection<EMail> mails,final Collection<Phone> phones) {
	    Map<String,Object> model = _supplyCommonModelData(AA14NotificationOperation.DELETE,
	    												  appointment,
	    												  lang,
	    												  mails,phones);
	    return model;
	}
	public Map<String, Object> supplyModelDataForRemindTomorrow(final AA14SummarizedAppointment appointment, 
																final Language lang,
																final Collection<EMail> mails,final  Collection<Phone> phones) {
	    Map<String,Object> model = _supplyCommonModelData(AA14NotificationOperation.REMIND_TOMORROW,
	    												  appointment,
	    												  lang,
	    												  mails,phones);
	    return model;
	}
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
		
		// [0] - Get the config
		AA14NotifierMessageComposingConfig notifMessageComposingConfig = _businessConfigServices.getCORESideCachedBusinessConfigs()
																								.getFor(appointment.getBusinessId())
																								.getNotifierMessageComposingConfigFor(appointment.getOrganization().getOid(),
																																	  appointment.getDivision().getOid(),
																																	  appointment.getService().getOid());
		
		// [1] - add the common data to the model
		Map<String,Object> commonModel = _messageDataCollectionToTemplateModel(notifMessageComposingConfig.getCommonMessageData(),
																			   lang);
		if (CollectionUtils.hasData(commonModel)) model.putAll(commonModel);
	    
		// [2] - Add the specific data
	    // Header
	    model.put("serviceName",appointment.getService().getName());
	    
	    Map<String,Object> byOpModel = null;
	    switch (op) {
	    case CREATE:
	    	byOpModel = _messageDataCollectionToTemplateModel(notifMessageComposingConfig.getCreateMessageData(),
	    													  lang);
	    	break;
	    case UPDATE:
	    	byOpModel = _messageDataCollectionToTemplateModel(notifMessageComposingConfig.getUpdateMessageData(),
	    													  lang);
	    	break;
		case DELETE:
	    	byOpModel = _messageDataCollectionToTemplateModel(notifMessageComposingConfig.getDeleteMessageData(),
	    													  lang);
			break;
		case REMIND_TODAY:
		case REMIND_TOMORROW:
		case REMIND_PERSON_LOCATOR:
	    	byOpModel = _messageDataCollectionToTemplateModel(notifMessageComposingConfig.getRemindMessageData(),
	    													  lang);
			break;
		default:
			break;
		}
	    if (CollectionUtils.hasData(byOpModel)) model.putAll(byOpModel);

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
    	model.put("valDepartamento",Strings.customized("{} - {}",
    													appointment.getOrganization().getName(),
    													appointment.getDivision().getName()));
    	model.put("valServicio",appointment.getService().getName());
    	
		model.put("appointmentValTerritorio",appointment.getLocation().getCounty());
    	model.put("appointmentValProvincia",appointment.getLocation().getCounty());
    	model.put("appointmentValPoblacion",appointment.getLocation().getMunicipality());
    	model.put("appointmentValDireccion",appointment.getLocation().getStreet());	    
    	model.put("appointmentValTelefono",appointment.getLocation().getPhone());
    	
    	// Personal data
    	model.put("appointmentDNI",appointment.getPerson().getId().asString());
    	model.put("appointmentName",appointment.getPerson().getName());
    	model.put("appointmentSurName",appointment.getPerson().getSurname());
    	
    	model.put("locator",appointment.getPersonLocatorId() != null ? appointment.getPersonLocatorId() : "");
    	
    	// details
	    model.put("textDetails",lang == Language.BASQUE ? "Xehetasunak" : "Detalles");
    	model.put("valTramite",appointment.getService().getProcedure() != null ? appointment.getService().getProcedure() : "");
	    if (appointment.getSubject() != null) {
	    	if (appointment.getSubject().getId() != null && !appointment.getSubject().getId().getRaw().equals("-")) {	    	
	    		model.put("textAppointmentExpID",lang == Language.BASQUE ? "Espediente-zenbakia" : "Número de expediente");
	    		model.put("appointmentExpID",appointment.getSubject().getId());
	    	}
	    	if (Strings.isNOTNullOrEmpty(appointment.getSubject().getDescription())) {
	    		model.put("appointmentExpDescription",appointment.getSubject().getDescription());
	    	}
	    }
	    
    	// contact info
    	String lstPhones = CollectionUtils.of(phones).toStringCommaSeparated();
    	model.put("appointmentPhone",lstPhones);
    	model.put("appointmentEmail",mails);
    	
    	// Common Footer
    	model.put("includeCancelFooter",op.isIn(AA14NotificationOperation.CREATE,
    										    AA14NotificationOperation.UPDATE,
    										    AA14NotificationOperation.REMIND_TOMORROW) ? true : false);
    	// URL Zuzenean
    	String urlLocZuzenean = Strings.customized("http://www.zuzenean.euskadi.eus/s68-conpres/{}/contenidos/informacion/zuzenean_atencion_presencial/{}_atencion/oficinas.html",
    											    Languages.countryLowerCase(lang),Languages.countryLowerCase(lang));
    	model.put("cancelUrlLoc",urlLocZuzenean);
    	String urlWebZuzenean = lang == Language.BASQUE ? "http://www.zuzenean.euskadi.eus/hitzorduaeskatzea"
    													: "http://www.zuzenean.euskadi.eus/citaprevia";
    	model.put("cancelUrlWeb",urlWebZuzenean);
    	return model;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	protected static Map<String,Object> _messageDataCollectionToTemplateModel(final Collection<AA14NotificationMessageData> data,
																			  final Language lang) {
		if (data == null) return Maps.newHashMap();
		Map<String,Object> outModel = Maps.newHashMapWithExpectedSize(data.size());
		for (AA14NotificationMessageData d : data) {
			if (d.getData() != null 
			 && d.getData().getIn(lang) != null) outModel.put(d.getKey(),d.getData().getIn(lang));
		}
		return outModel;
	}
	protected static String _dayOfWeekName(final Date d,
										   final Language lang) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		return lang == Language.BASQUE ? DAY_OF_WEEK_EU[cal.get(Calendar.DAY_OF_WEEK) - 1]
									   : DAY_OF_WEEK_ES[cal.get(Calendar.DAY_OF_WEEK) - 1];	
	}
}
