package aa14b.events;

import java.util.Collection;

import com.google.common.collect.Lists;

import aa14b.services.internal.AA14BookedSlotSummarizerService;
import aa14f.model.AA14Appointment;
import aa14f.model.summaries.AA14SummarizedAppointment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.patterns.IsBuilder;
import r01f.securitycontext.SecurityContext;
import r01f.types.contact.EMail;
import r01f.util.types.collections.CollectionUtils;

/**
 * Builds a {@link AA14NotificationMessage} from an {@link AA14Appointment}
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class AA14NotificationMessageBuilder
  implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static AA14NotificationMessageBuilderAppointmentStep using(final AA14BookedSlotSummarizerService summarizerService) {
		return new AA14NotificationMessageBuilder() {/* nothing */}
						.new AA14NotificationMessageBuilderAppointmentStep(summarizerService,
														   			 	   new AA14NotificationMessageAboutAppointment());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class AA14NotificationMessageBuilderAppointmentStep {
		private final AA14BookedSlotSummarizerService _summarizerService;
		private final AA14NotificationMessageAboutAppointment _message;
		
		public AA14NotificationMessageAboutAppointment createForAppointment(final SecurityContext securityContext,
															final AA14Appointment appointment) {
			// Compose an appointment summary	
			AA14SummarizedAppointment appointmentSummary = _summarizerService.summarizedAppointmentFor(securityContext,
																							    	   appointment);
			_message.setAppointment(appointmentSummary);
			
			if (appointment.getContactInfo() != null) {
				// Get the list of phones to notify
				_message.setPhones(appointment.getContactInfo().getPhones());
				
				// Compose the list of mails to notify: the end-user mail 
				Collection<EMail> endUserEmails = CollectionUtils.hasData(appointment.getContactInfo().getMailAddresses()) ? appointment.getContactInfo().getMailAddresses()
																														   : null;
//				//and the service internal mail (if available)
//				Collection<EMail> locEmails = _summarizerService.locationContactMails(securityContext,
//																					  appointment.getOrgDivisionServiceLocationOid());
				Collection<EMail> allEmails = Lists.newArrayList();
//				if (CollectionUtils.hasData(locEmails)) allEmails.addAll(locEmails);
				if (CollectionUtils.hasData(endUserEmails)) allEmails.addAll(endUserEmails);
				
				if (CollectionUtils.hasData(allEmails)) _message.setMails(allEmails);
			}
			
			// Return
			return _message;
		}
	}
}
