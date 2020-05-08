package aa14f.api.interfaces;

import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14IDs.AA14PersonLocatorID;
import r01f.locale.Language;
import r01f.model.services.COREServiceMethodExecResult;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ExposedServiceInterface;
import r01f.types.contact.EMail;
import r01f.types.contact.PersonID;

@ExposedServiceInterface
public interface AA14PersonLocatorServices
         extends AA14ServiceInterface {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds a [person locator] for the given [person id] (dni / nie...)
	 * @param securityContext
	 * @param personId
	 * @param contactEMail an email / phone that will be associated with the [personId] and [person locator] so if the user
	 * 		  later request the [person locator] it can be sent to that [contact mean] (emai / phone) ONLY if the personId & contact mean matches
	 * @return
	 */
	public COREServiceMethodExecResult<AA14PersonLocatorID> findPersonLocatorFor(final SecurityContext securityContext,
																	 			 final PersonID personId,final EMail contactEMail);
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
	 * @param securityContext
	 * @param orgId just used to know where the user is requesting the [person locator] (although it's org-independent this is used to guess the email sender)
	 * @param personId
	 * @param contactEMail an email / phone associated with the person id
	 * @param lang
	 * @return true if the reminder could be sent
	 */
	public COREServiceMethodExecResult<Boolean> remindPersonLocatorFor(final SecurityContext securityContext,
																	   final AA14OrganizationID orgId,
																	   final PersonID personId,final EMail contactEMail,final Language lang);
}
