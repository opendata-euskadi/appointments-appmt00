package aa14b.services.internal;

import java.util.Collection;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import aa14f.api.cache.AA14BusinessConfigCache;
import aa14f.model.AA14Appointment;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14BookedSlotType;
import aa14f.model.AA14NonBookableSlot;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.summaries.AA14SummarizedAppointment;
import aa14f.model.summaries.AA14SummarizedBookedSlot;
import aa14f.model.summaries.AA14SummarizedBookedSlotBase;
import aa14f.model.summaries.AA14SummarizedOrgHierarchy;
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
	private final AA14BusinessConfigCache _configCache;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14BookedSlotSummarizerService(final AA14BusinessConfigCache configCache) {
		_configCache = configCache;
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
		
		// Complete data 
		AA14Schedule sch = _configCache.getBusinessConfig()
									   .getScheduleFor(appointment.getScheduleOid());
		outSummary.setSchedule(sch.getSummarizedIn(prefLang));
		
		AA14SummarizedOrgHierarchy orgInfo = _configCache.getBusinessConfig()
														 .getSummarizedOrgHierarchyFor(appointment.getOrgDivisionServiceLocationOid(),
																					   prefLang);
		_setOrgData(orgInfo,
					outSummary,
					prefLang);
		AA14OrgDivisionServiceLocation loc = _configCache.getBusinessConfig()
														 .getLocationFor(appointment.getOrgDivisionServiceLocationOid());
		_setPresentationColor(loc,
							  outSummary);
		
		return outSummary;
	}
	public AA14SummarizedBookedSlot summarizedSlotFor(final SecurityContext securityContext,
													  final AA14BookedSlot slot,
													  final Language lang) {
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
		// Complete the data
		AA14Schedule sch = _configCache.getBusinessConfig()
									   .getScheduleFor(slot.getScheduleOid());
		outSummary.setSchedule(sch.getSummarizedIn(lang));
		
		AA14SummarizedOrgHierarchy orgInfo = _configCache.getBusinessConfig()
														 .getSummarizedOrgHierarchyFor(slot.getOrgDivisionServiceLocationOid(),
																					   lang);
		_setOrgData(orgInfo,
					outSummary,
					lang);
		
		AA14OrgDivisionServiceLocation loc = _configCache.getBusinessConfig()
														 .getLocationFor(slot.getOrgDivisionServiceLocationOid());
		_setPresentationColor(loc,
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
		AA14OrgDivisionServiceLocation loc = _configCache.getBusinessConfig()
														 .getLocationFor(locOid);
		return loc.getContactInfo() != null ? loc.getContactInfo().getMailAddresses() 
											: null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _setOrgData(final AA14SummarizedOrgHierarchy hierarchy,
						   	 		final AA14SummarizedBookedSlotBase<?,?> summary,
						   	 		final Language prefLang) {		
		// only appointments have hierarchy (non-bookable slots only have schedule)
		if (hierarchy.getLocation() != null) summary.setLocation(hierarchy.getLocation());
		if (hierarchy.getService() != null) summary.setService(hierarchy.getService());
		if (hierarchy.getDivision() != null) summary.setDivision(hierarchy.getDivision());
		if (hierarchy.getOrganization() != null) summary.setOrganization(hierarchy.getOrganization());	
	}
	private static void _setPresentationColor(final AA14OrgDivisionServiceLocation loc,
									   		  final AA14SummarizedBookedSlotBase<?,?> summary) {
		// set the presentation color
		Color thePresentationColor = null;
		
		if (summary.getType() == AA14BookedSlotType.NON_BOOKABLE) {
			thePresentationColor = AA14NonBookableSlot.DEFAULT_PRESENTATION_COLOR;
			
		} else if (loc.getPresentationConfig() == null 
				|| loc.getPresentationConfig().getColor() == null) {
			thePresentationColor = AA14Appointment.DEFAULT_PRESENTATION_COLOR;
			
		} else {
			thePresentationColor = loc.getPresentationConfig().getColor();
		}
		summary.setPresentationColor(thePresentationColor);
	}}
