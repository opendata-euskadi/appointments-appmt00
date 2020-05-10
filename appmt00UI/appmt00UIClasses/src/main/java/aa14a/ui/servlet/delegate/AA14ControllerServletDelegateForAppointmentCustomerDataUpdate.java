package aa14a.ui.servlet.delegate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aa14a.ui.servlet.AA14AppointmentFromRequestBuilder;
import aa14a.ui.servlet.AA14ControllerOperation;
import aa14a.ui.servlet.AA14ReqParamToType;
import aa14f.client.api.AA14ClientAPI;
import aa14f.model.AA14Appointment;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.locale.Language;
import r01f.model.persistence.PersistenceException;
import r01f.model.persistence.PersistenceServiceErrorTypes;
import r01f.servlet.HttpRequestParamsWrapper;

@Slf4j
@RequiredArgsConstructor
public class AA14ControllerServletDelegateForAppointmentCustomerDataUpdate
	 extends AA14ControllerServletDelegateBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14ClientAPI _clientAPI;
	private final AA14AppointmentFromRequestBuilder _appointmentFromRequestBuilder;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void executeOp(final HttpServletRequest request,final HttpServletResponse response,
						  final AA14ControllerOperation op,final HttpRequestParamsWrapper reqParams) throws ServletException, 
																 											IOException {
		log.debug("[init]: Update appointment customer personal information -----------------");
				
		Language lang = reqParams.getParameter("lang")
								 .asLanguageFromCountryCode()
								 .orDefault(Language.DEFAULT);
		
		AA14SlotOID slotOid = reqParams.getMandatoryParameter("slotOid")
									   .asOid(AA14SlotOID.class)
			 						   .using(AA14ReqParamToType.transform(AA14SlotOID.class));

		// load appointment
		AA14Appointment appointment = _clientAPI.bookedSlotsAPI()
										.getForCRUD()
										.load(slotOid).as(AA14Appointment.class);
		
		// set appointment data
		_appointmentFromRequestBuilder.setAppointmentDataFromRequest(reqParams,appointment);
		
		// Update!
		try {
			appointment = _clientAPI.bookedSlotsAPI()
							   	   .getForCRUD()
								   .update(appointment).as(AA14Appointment.class);
		
			_returnJsonResponse(response,
								appointment); 

		} catch (PersistenceException persistEx) {
			log.error("Persistence error code={} / ext code={}: {}",
					  persistEx.getCode(),persistEx.getExtendedCode(),
					  persistEx.getMessage(),
					  persistEx);
			if (persistEx.is(PersistenceServiceErrorTypes.ENTITY_ALREADY_EXISTS)
			 && persistEx.getExtendedCode() == 1) {
				// the slot was occupied when it was tried to be booked
				response.getWriter().write("The user MUST select another slot since the selected one was occupied when it was tried to be booked: ERROR_CODE=" + persistEx.getCode()); 
			}
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.flushBuffer();

		} catch (Exception e) {
			e.printStackTrace(System.out);
			response.getWriter().print(Throwables.getStackTraceAsString(e));
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.flushBuffer();
		}
		log.debug("[end]: Update appointment date/time-----------------");
	}
}
