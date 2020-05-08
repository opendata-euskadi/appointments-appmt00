package aa14b.services.delegates.notifier;

import aa14b.notifier.config.AA14NotifierConfigForEMail;
import aa14f.model.AA14NotificationOperation;
import aa14f.model.config.business.AA14BusinessConfigForBizilagun;
import aa14f.model.config.business.AA14BusinessConfigForBloodDonation;
import aa14f.model.config.business.AA14BusinessConfigForJustizia;
import aa14f.model.config.business.AA14BusinessConfigForMedicalService;
import aa14f.model.config.business.AA14BusinessConfigForTrafikoa;
import aa14f.model.config.business.AA14BusinessConfigForZuzenean;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.types.Path;
import r01f.util.types.Strings;

@RequiredArgsConstructor
public class AA14NotifierTemplateSelectorEMailImpl 
  implements AA14NotifierTemplateSelector {
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14NotifierConfigForEMail _config;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Path createTemplatePathFor(final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									  final Language lang) {
		return _templatePathFor(AA14NotificationOperation.CREATE,
								org,div,srvc,loc,
						 		lang);
	}

	@Override
	public Path updateTemplatePathFor(final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									  final Language lang) {
		return _templatePathFor(AA14NotificationOperation.UPDATE,
								org,div,srvc,loc,
						 		lang);
	}

	@Override
	public Path deleteTemplatePathFor(final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									  final Language lang) {
		return _templatePathFor(AA14NotificationOperation.DELETE,
								org,div,srvc,loc,
						 		lang);
	}
	@Override
	public Path remindTomorrowTemplatePathFor(final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									  		  final Language lang) {
		return _templatePathFor(AA14NotificationOperation.REMIND_TOMORROW,
								org,div,srvc,loc,
						 		lang);
	}
	@Override
	public Path remindTodayTemplatePathFor(final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
									  	   final Language lang) {
		return _templatePathFor(AA14NotificationOperation.REMIND_TODAY,
								org,div,srvc,loc,
						 		lang);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	private Path _templatePathFor(final AA14NotificationOperation op,
								  final AA14OrganizationID org,final AA14OrgDivisionID div,final AA14OrgDivisionServiceID srvc,final AA14OrgDivisionServiceLocationID loc,
								  final Language lang) {
		if (srvc.is(AA14BusinessConfigForTrafikoa.SERVICE_ID)) {
			return Path.from("aa14b/notifier/AA14MailTemplateForTrafikoa.vm");
		} 
		else if (srvc.isContainedIn(AA14BusinessConfigForBizilagun.SERVICE_FIANZAS_DEPOSITO_CONTRATOS_ID,
									AA14BusinessConfigForBizilagun.SERVICE_COMUNIDADES_ALQUILERES_ID)) {
			return Path.from("aa14b/notifier/AA14MailTemplateForBizilagunByPhoneOnly.vm");	 //XXX temporaly 	
		}
		else if (srvc.is(AA14BusinessConfigForBloodDonation.SERVICE_ID)) {
			return Path.from("aa14b/notifier/AA14MailTemplateForBloodDonations.vm");
		} 		
		else if (srvc.isContainedIn(AA14BusinessConfigForMedicalService.SERVICE_DOCTOR_VISIT_ID,
									AA14BusinessConfigForMedicalService.SERVICE_NURSE_VISIT_ID)) {
			return Path.from("aa14b/notifier/AA14MailTemplateForMedicalService.vm");
		}
		else if (srvc.is(AA14BusinessConfigForZuzenean.SERVICE_ID)) {
			return Path.from("aa14b/notifier/AA14MailTemplateForZuzenean.vm");
		}
		else if (srvc.is(AA14BusinessConfigForJustizia.SERVICE_ID)) {
			return Path.from("aa14b/notifier/AA14MailTemplateForJustizia.vm");
		}
		else {
			throw new IllegalArgumentException(Strings.customized("NO template for org={}, div={}, srvc={}, loc={} and lang={}",
											   					  org,div,srvc,loc,
											   					  lang));
		}
	}

}
