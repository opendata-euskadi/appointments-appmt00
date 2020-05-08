package aa14b.services.delegates.notifier;

import aa14f.model.config.business.AA14BusinessConfigForBizilagun;
import aa14f.model.config.business.AA14BusinessConfigForBloodDonation;
import aa14f.model.config.business.AA14BusinessConfigForJustizia;
import aa14f.model.config.business.AA14BusinessConfigForMedicalService;
import aa14f.model.config.business.AA14BusinessConfigForTrafikoa;
import aa14f.model.config.business.AA14BusinessConfigForZuzenean;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.types.contact.EMail;
import r01f.util.types.Strings;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
abstract class AA14NotifierEMailFromSelector {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static EMail eMailFromFor(final AA14OrganizationID org,
								  	 final EMail defaultEMail) {
		EMail outEMail = null;
		if (org.is(AA14BusinessConfigForTrafikoa.ORG_ID)) {
			outEMail = EMail.of("trafikoa-noreply@euskadi.eus");
		} 
		else if (org.is(AA14BusinessConfigForBizilagun.ORG_ID)) {
			outEMail = EMail.of("bizilagun-noreply@euskadi.eus");
		}
		else if (org.is(AA14BusinessConfigForBloodDonation.ORG_ID)) {
			outEMail = EMail.of("odolematea-noreply@euskadi.eus");
		} 
		else if (org.is(AA14BusinessConfigForMedicalService.ORG_ID)) {
			outEMail = EMail.of("ejgv-noreply@euskadi.eus");
		} 
		else if (org.is(AA14BusinessConfigForZuzenean.ORG_ID)) {
			outEMail = EMail.of("zuzenean-noreply@euskadi.eus");
		}
		else if (org.is(AA14BusinessConfigForJustizia.ORG_ID)) {
			outEMail = EMail.of("justizia-noreply@justizia.eus");
		}
		else {
			throw new IllegalArgumentException(Strings.customized("NO [from] EMail for org={}",
											   					  org));
		}
		return outEMail;
	}
}
