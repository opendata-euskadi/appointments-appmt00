package aa14b.services.internal;

import java.util.Collection;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import aa14f.api.interfaces.AA14CRUDServicesForOrgDivision;
import aa14f.api.interfaces.AA14CRUDServicesForOrgDivisionService;
import aa14f.api.interfaces.AA14CRUDServicesForOrgDivisionServiceLocation;
import aa14f.api.interfaces.AA14CRUDServicesForOrganization;
import aa14f.api.interfaces.AA14CRUDServicesForSchedule;
import aa14f.model.AA14Appointment;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14BookedSlotType;
import aa14f.model.AA14NonBookableSlot;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.summaries.AA14SummarizedAppointment;
import aa14f.model.summaries.AA14SummarizedBookedSlot;
import aa14f.model.summaries.AA14SummarizedBookedSlotBase;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.securitycontext.SecurityContext;
import r01f.types.Color;
import r01f.types.contact.EMail;

/**
 * An internal service that summarizes an appointment
 */
@Singleton
public class AA14BookedSlotSummarizerService {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final  AA14CRUDServicesForOrganization _orgCRUD;
	private final AA14CRUDServicesForOrgDivision _divCRUD;
	private final AA14CRUDServicesForOrgDivisionService _srvcCRUD;
	private final AA14CRUDServicesForOrgDivisionServiceLocation _locCRUD;
	private final AA14CRUDServicesForSchedule _schCRUD;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14BookedSlotSummarizerService(final AA14CRUDServicesForOrganization orgCRUD,
										   final AA14CRUDServicesForOrgDivision divCRUD,
										   final AA14CRUDServicesForOrgDivisionService srvcCRUD,
										   final AA14CRUDServicesForOrgDivisionServiceLocation locCRUD,
										   final AA14CRUDServicesForSchedule schCRUD) {
		_orgCRUD = orgCRUD;
		_divCRUD = divCRUD;
		_srvcCRUD = srvcCRUD;
		_locCRUD = locCRUD;
		_schCRUD = schCRUD;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14SummarizedAppointment summarizedAppointmentFor(final SecurityContext securityContext,
													   		  final AA14Appointment appointment) {
		return this.summarizedAppointmentFor(securityContext,
									  		 appointment,
									  		 null);	// takes the pref lang of the appointment
	}
	public AA14SummarizedAppointment summarizedAppointmentFor(final SecurityContext securityContext,
													   		  final AA14Appointment appointment,
													   		  final Language lang) {
		// Get the org hierarchy 
		AA14Hierarchy hierarchy = new AA14Hierarchy(securityContext,
													appointment.getOrgDivisionServiceLocationOid(),appointment.getScheduleOid());
		// Create the summary
		AA14SummarizedAppointment outSummary = AA14SummarizedAppointment.create()
																.withOid(appointment.getOid())
																.withId(appointment.getId())
																.day(appointment.getYear(),appointment.getMonthOfYear(),appointment.getDayOfMonth())
																.hour(appointment.getHourOfDay(),appointment.getMinuteOfHour())
																.duringMinutes(appointment.getDurationMinutes())
																.using(appointment.getNumberOfAdjacentSlots())
																.forPerson(appointment.getPerson())
																.withPersonLocatorId(appointment.getPersonLocatorId())
																.subject(appointment.getSubject())
																.withEMails(appointment.getContactInfo() != null ? appointment.getContactInfo().getMailAddresses()
																												 : null)
																.withPhones(appointment.getContactInfo() != null ? appointment.getContactInfo().getPhones()
																												 : null);
		// Depending on the preferred user language compose the organizational entities summaries
		Language prefLang = appointment.getContactInfo().getPreferedLanguage();
		if (prefLang == null) prefLang = lang != null ? lang : Language.DEFAULT;
		_setDataOrg(hierarchy,
					outSummary,
					prefLang);
		_setPresentationColor(hierarchy,
							  outSummary);
		
		return outSummary;
	}
	public AA14SummarizedBookedSlot summarizedSlotFor(final SecurityContext securityContext,
													  final AA14BookedSlot slot,
													  final Language lang) {
		// Get the org hierarchy 
		AA14Hierarchy hierarchy = new AA14Hierarchy(securityContext,
													slot.getOrgDivisionServiceLocationOid(),slot.getScheduleOid());
		// Create the summary
		AA14SummarizedBookedSlot outSummary = AA14SummarizedBookedSlot.create(slot.getType())
																.withOid(slot.getOid())
																.withId(slot.getId())
																.day(slot.getYear(),slot.getMonthOfYear(),slot.getDayOfMonth())
																.hour(slot.getHourOfDay(),slot.getMinuteOfHour())
																.duringMinutes(slot.getDurationMinutes())
																.using(slot.getNumberOfAdjacentSlots())
																.subject(slot.getSubjectIn(lang))
																.summary(slot.getSummaryIn(lang));
		// Set the org data & color
		_setDataOrg(hierarchy,
					outSummary,
					lang);
		_setPresentationColor(hierarchy,
							  outSummary);
		// if the slot is periodic
		outSummary.setPeriodicSlotData(slot.getPeriodicSlotData());
		return outSummary;
	}
	/**
	 * Returns the emails asociated with a certain location
	 * @param securityContext
	 * @param locOid
	 * @return
	 */
	public Collection<EMail> locationContactMails(final SecurityContext securityContext,
												  final AA14OrgDivisionServiceLocationOID locOid) {
		AA14OrgDivisionServiceLocation loc = _locCRUD.load(securityContext,
														   locOid)
													 .getOrThrow();
		return loc.getContactInfo() != null ? loc.getContactInfo().getMailAddresses() 
											: null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private void _setDataOrg(final AA14Hierarchy hierarchy,
						   	 final AA14SummarizedBookedSlotBase<?,?> summary,
						   	 final Language prefLang) {		
		summary.setSchedule(hierarchy.getSch().getSummarizedIn(prefLang));
		
		// only appointments have hierarchy (non-bookable slots only have schedule)
		if (hierarchy.getLoc() != null) summary.setLocation(hierarchy.getLoc().getSummarizedIn(prefLang));
		if (hierarchy.getSrvc() != null) summary.setService(hierarchy.getSrvc().getSummarizedIn(prefLang));
		if (hierarchy.getDiv() != null) summary.setDivision(hierarchy.getDiv().getSummarizedIn(prefLang));
		if (hierarchy.getOrg() != null) summary.setOrganization(hierarchy.getOrg().getSummarizedIn(prefLang));	
	}
	private void _setPresentationColor(final AA14Hierarchy hierarchy,
									   final AA14SummarizedBookedSlotBase<?,?> summary) {
		// set the presentation color
		Color thePresentationColor = null;
		
		if (summary.getType() == AA14BookedSlotType.NON_BOOKABLE) {
			thePresentationColor = AA14NonBookableSlot.DEFAULT_PRESENTATION_COLOR;
			
		} else if (hierarchy.getLoc().getPresentationConfig() == null 
				|| hierarchy.getLoc().getPresentationConfig().getColor() == null) {
			thePresentationColor = AA14Appointment.DEFAULT_PRESENTATION_COLOR;
			
		} else {
			thePresentationColor = hierarchy.getLoc().getPresentationConfig().getColor();
		}
		summary.setPresentationColor(thePresentationColor);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	private class AA14Hierarchy {
		@Getter private final AA14Organization _org;
		@Getter private final AA14OrgDivision _div;
		@Getter private final AA14OrgDivisionService _srvc;
		@Getter private final AA14OrgDivisionServiceLocation _loc;
		@Getter private final AA14Schedule _sch;
		
		private AA14Hierarchy(final SecurityContext securityContext,
						   	  final AA14OrgDivisionServiceLocationOID locOid,final AA14ScheduleOID schOid) {
			_sch = _schCRUD.load(securityContext,
								 schOid)
						   .getOrThrow();
			_loc = locOid != null ? _locCRUD.load(securityContext,
							     				  locOid)
						   					.getOrThrow()
						   		  : null;
			AA14OrgDivisionServiceOID srvcOid = _loc != null ? _loc.getOrgDivisionServiceRef().getOid()
															 : null;
			_srvc = srvcOid != null ? _srvcCRUD.load(securityContext,
								   					 srvcOid)								    
								   			   .getOrThrow()
								    : null;
			AA14OrgDivisionOID divOid = _loc != null ? _loc.getOrgDivisionRef().getOid() : null;
			_div = divOid != null ? _divCRUD.load(securityContext,
								 				  divOid)
						   					.getOrThrow()
						   		  : null;
			AA14OrganizationOID orgOid = _loc != null ? _loc.getOrgRef().getOid() : null;
			_org = orgOid != null ? _orgCRUD.load(securityContext,
								 				  orgOid)
						   					.getOrThrow()
						   		  : null;
			
		}
	}
}
