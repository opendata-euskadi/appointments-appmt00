package aa14a.ui.servlet.delegate;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aa14a.ui.servlet.AA14ControllerOperation;
import aa14a.ui.servlet.AA14ReqParamToType;
import aa14f.client.api.AA14ClientAPI;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.summaries.AA14SummarizedAppointment;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.servlet.HttpRequestParamsWrapper;
import r01f.types.contact.NIFPersonID;
import r01f.types.contact.PersonID;
import r01f.util.types.collections.CollectionUtils;

@RequiredArgsConstructor
public class AA14ControllerServletDelegateForDataValidation
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
		
		// Get the person id (dni)
		PersonID personId = reqParams.getMandatoryParameter("personId")
 									 .asOid(NIFPersonID.class)
 									 .using(AA14ReqParamToType.transform(NIFPersonID.class));
		switch (op) {
		case VALIDATE_MAX_WEEK_PERSON_APPOINTMENTS:
			// validates if a [personId] has a max number of appointments in a given week
			// http://apps.localhost:81/appmtXXUIWar/AA14ControllerServlet?op=VALIDATE_MAX_WEEK_PERSON_APPOINTMENTS&personId=30639781D&year=2020&weekOfYear=18&locId=JUSTIZIA_REGISTRO_REPARTO_AM&max=5
			AA14OrgDivisionServiceLocationID locId = reqParams.getMandatoryParameter("locId")
						 									  .asOid(AA14OrgDivisionServiceLocationID.class)
						 									  .using(AA14ReqParamToType.transform(AA14OrgDivisionServiceLocationID.class));
			int year = reqParams.getMandatoryParameter("year")
	 							.asInteger();
			int weekOfYear = reqParams.getMandatoryParameter("weekOfYear")
									  .asInteger();
			int max = reqParams.getMandatoryParameter("max")
							   .asInteger();
			Collection<AA14SummarizedAppointment> apps = _clientAPI.bookedSlotsAPI()
																   .getForFind()
																   .findWeekBookedAppointmentsFor(locId, 
																		   						  personId,year,weekOfYear,
																		   						  lang);
			int appsCount = CollectionUtils.hasData(apps) ? apps.size() : 0;
			_returnJsonResponse(response,
								appsCount < max);
			break;
		case VALIDATE_PERSONID:
			// validate a personId format
			boolean outValid = _validatePersonId(personId);
			// return the json to the client
			_returnJsonResponse(response,
								outValid);
			break;
		default:
			throw new IllegalArgumentException();
		}

	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private static boolean _validatePersonId(final PersonID personId) {
		return personId != null ? personId.isValid()
								: false;
	}
}
