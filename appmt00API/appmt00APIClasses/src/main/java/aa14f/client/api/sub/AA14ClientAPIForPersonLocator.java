package aa14f.client.api.sub;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14PersonLocatorServices;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14IDs.AA14PersonLocatorID;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.api.delegates.ClientAPIServiceDelegateBase;
import r01f.types.contact.EMail;
import r01f.types.contact.PersonID;

/**
 * Client implementation of [person locator] services
 */
@Accessors(prefix="_")
public class AA14ClientAPIForPersonLocator
     extends ClientAPIServiceDelegateBase<AA14PersonLocatorServices> {		
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIForPersonLocator(final Provider<SecurityContext> securityContextProvider,
								    	 final Marshaller modelObjectsMarshaller,
								    	 final AA14PersonLocatorServices personLocatorServicesProxy) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  personLocatorServicesProxy); // reference to other client apis
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds or creates a [person locator] for the given [person id] (dni / nie...)
	 * @param personId
	 * @param contactEMail
	 * @return
	 */
	public AA14PersonLocatorID findPersonLocatorFor(final PersonID personId,final EMail contactEMail) {
		return this.getServiceProxyAs(AA14PersonLocatorServices.class)
						.findPersonLocatorFor(this.getSecurityContext(),
									      	  personId,contactEMail)
						.getOrThrow();
	}
	/**
	 * Finds or creates a [person locator] for the given [person id] (dni / nie...)
	 * @param personId
	 * @param contactEMail
	 * @return
	 */
	public AA14PersonLocatorID findOrCreatePersonLocatorFor(final PersonID personId,final EMail contactEMail) {
		AA14PersonLocatorID existingLocator = this.findPersonLocatorFor(personId,contactEMail);
		return existingLocator != null ? existingLocator
									   : AA14PersonLocatorID.supplyFor(personId);
	}
	/**
	 * Reminds the [person locator] for the given [person id] (dni / nie...) and [contact mean] (email / phone)
	 * 	1. finds or creates the [person locator] 
	 *  2. if there's any appointment without [person locator] it updates it
	 *  3. Sends an email to the [person] with the [person locator]
	 * BEWARE!!!
	 * 		If there already exists [appointments] for the given [personId] some situations can arise:
	 * 		a) All the [appointments] have the SAME [contact mean] (email / phone)
	 * 		   -> a SINGLE [person locator] is generated
	 * 			  ... so a SINGLE [email / sms] is sent when a [person locator] remind request is received
	 * 		b) Some [appointments] have a [contact mean] (say A) and other [appointments] have a different [contact mean] (say B)
	 * 		   -> MANY [person locator] are generated: one for every combination of [personId] / [contact mean]
	 * 			  ... so when a [person locator] remind request is received with a [personId] & [contact mean], 
	 * 				  only an [email / sms] is sent to the given [contact mean] 
	 *      c) Some [appointments] do NOT have [person locator] (maybe it was not generated) 
	 * 		   -> This method tries to generate a [person locator] BUT a condition MUST be met:
	 * 					ALL appointments MUST have the SAME [contact mean] (email / phone) 
	 *			  		if any appointment have a DIFFERENT [contact mean] we cannot be sure which one is the legitimate one
	 *					and the eMail / sms is NOT sent
	 *		
	 *			  Remember that a [person locator] is generated for every combination of [person id] & [contact mean]
	 * 			  ... so it's VERY IMPORTANT that all [appointments] have the SAME [contact mean]
	 * 
	 * We MUST be sure to send the [locator] ONLY to a legitimate [contact mean]  
	 * 
	 * @param orgId the org from which the user is requesting the [person locator] (although it's org-independent this is used to guess the email sender)
	 * @param personId
	 * @param contactEMail an email / phone associated with the person id
	 * @param lang
	 * @return true if the reminder could be sent
	 */
	public boolean remindPersonLocatorFor(final AA14OrganizationID orgId,
										  final PersonID personId,final EMail contactEMail,final Language lang) {
		Language theLang = lang != null ? lang : Language.DEFAULT;
		boolean couldRemind = this.getServiceProxyAs(AA14PersonLocatorServices.class)
									.remindPersonLocatorFor(this.getSecurityContext(),
															orgId,
												      	  	personId,contactEMail,theLang)
									.getOrThrow();
		return couldRemind;
	}
}
