package aa14a.ui.servlet.delegate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aa14a.model.view.AA14PersonLocatorCreationResponse;
import aa14a.ui.servlet.AA14ControllerOperation;
import aa14a.ui.servlet.AA14ReqParamToType;
import aa14f.client.api.AA14ClientAPI;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14IDs.AA14PersonLocatorID;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.servlet.HttpRequestParamsWrapper;
import r01f.types.contact.EMail;
import r01f.types.contact.NIFPersonID;
import r01f.types.contact.PersonID;

@RequiredArgsConstructor
public class AA14ControllerServletDelegateForPersonLocator
     extends AA14ControllerServletDelegateBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14ClientAPI _clientAPI;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void executeOp(final HttpServletRequest request,final HttpServletResponse response,
						  final AA14ControllerOperation op,final HttpRequestParamsWrapper reqParams) throws ServletException, 
																					  						IOException {
		Language lang = reqParams.getParameter("lang")
								 .asLanguageFromCountryCode()
								 .orDefault(Language.SPANISH);
		lang = lang != null ? lang : Language.DEFAULT;
		
		// Get the person id (dni) & email
		PersonID personId = reqParams.getMandatoryParameter("personId")
 									 .asOid(NIFPersonID.class)
 									 .using(AA14ReqParamToType.transform(NIFPersonID.class));
		EMail eMail = reqParams.getMandatoryParameter("email")
 							   .asType(EMail.class)
 							   .using(AA14ReqParamToType.transform(EMail.class));
		switch (op) {
		case GENERATE_PERSON_LOCATOR:
			// generate locator
			AA14PersonLocatorCreationResponse outResponse = _generatePersonLocator(personId,
																   				   eMail);
			// return the json to the client
			_returnJsonResponse(response,
								outResponse);
			break;
		case REMIND_PERSON_LOCATOR:
			// Get the orgId: it's just used downstream to guess the notification email sender
			AA14OrganizationID orgId = reqParams.getMandatoryParameter("orgId")
												.asOid(AA14OrganizationID.class)
												.using(AA14ReqParamToType.transform(AA14OrganizationID.class));
			// remind
			boolean outResult = _remindPersonLocator(orgId,
													 personId,eMail,lang);

			// return the json to the client
			_returnJsonResponse(response,
								outResult);
			break;
		default:
			throw new IllegalArgumentException();
		}

	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private AA14PersonLocatorCreationResponse _generatePersonLocator(final PersonID personId,final EMail eMail) {
		AA14PersonLocatorCreationResponse outResult = new AA14PersonLocatorCreationResponse();
		if (personId == null) {
			outResult.setOk(false);
			outResult.setMessage("DNI / NIE and location MUST NOT be null");
		} else if (!personId.isValid()) {
			outResult.setOk(false);
			outResult.setMessage("<p>DNI/NIE invalido</p><p>NAN/AIZ baliogabea</p>");
		} else {
			// use the API to find or create the [person locator] for the given [person id]
			AA14PersonLocatorID locatorId = _clientAPI.personLocatorAPI()
													  .findOrCreatePersonLocatorFor(personId,eMail);
			outResult.setOk(true);
			outResult.setPersonLocator(locatorId.asString());
		}
		return outResult;
	}
	private boolean _remindPersonLocator(final AA14OrganizationID orgId,
										 final PersonID personId,final EMail eMail,final Language lang) {
		return _clientAPI.personLocatorAPI()
						 .remindPersonLocatorFor(orgId,
								 				 personId,eMail,lang);
	}
}
