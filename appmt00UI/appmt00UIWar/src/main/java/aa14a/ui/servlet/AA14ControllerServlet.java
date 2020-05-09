package aa14a.ui.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import aa14a.ui.servlet.delegate.AA14ControllerServletDelegateForAppointmentCreate;
import aa14a.ui.servlet.delegate.AA14ControllerServletDelegateForAppointmentCustomerDataUpdate;
import aa14a.ui.servlet.delegate.AA14ControllerServletDelegateForAppointmentDateTimeUpdate;
import aa14a.ui.servlet.delegate.AA14ControllerServletDelegateForAppointmentFind;
import aa14a.ui.servlet.delegate.AA14ControllerServletDelegateForAppointmentNotify;
import aa14a.ui.servlet.delegate.AA14ControllerServletDelegateForConfig;
import aa14a.ui.servlet.delegate.AA14ControllerServletDelegateForCustomerFind;
import aa14a.ui.servlet.delegate.AA14ControllerServletDelegateForDataValidation;
import aa14a.ui.servlet.delegate.AA14ControllerServletDelegateForNonBookableSlotReserve;
import aa14a.ui.servlet.delegate.AA14ControllerServletDelegateForPersonLocator;
import aa14a.ui.servlet.delegate.AA14ControllerServletDelegateForSlotListing;
import aa14a.ui.servlet.delegate.AA14ControllerServletDelegateForSlotRelease;
import aa14f.client.api.AA14ClientAPI;
import lombok.extern.slf4j.Slf4j;
import r01f.servlet.HttpRequestParamsWrapper;


/**
 * Servlet implementation class AA14ControllerServlet
 */
@Singleton
@Slf4j
public class AA14ControllerServlet 
     extends HttpServlet {

	private static final long serialVersionUID = 715036869738597415L;
/////////////////////////////////////////////////////////////////////////////////////////
//  INJECTED FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private AA14ClientAPI _clientAPI;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    public AA14ControllerServlet() {
        super();
    } 
    @Inject
    public AA14ControllerServlet(final AA14ClientAPI api) {
    	_clientAPI = api;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
	protected void doPost(final HttpServletRequest request,final HttpServletResponse response) throws ServletException,
																									  IOException {
		_do(request,response);	
	}
    @Override
	protected void doGet(final HttpServletRequest request,final HttpServletResponse response) throws ServletException, 
																									 IOException {
		_do(request,response);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private void _do(final HttpServletRequest request,final HttpServletResponse response) throws ServletException, 
																								 IOException {
		try {
			if (_clientAPI == null) throw new IllegalStateException("Client API was NOT injected!!!");
			
			// get a request params wrapper that provides easier params access
			HttpRequestParamsWrapper reqParams = new HttpRequestParamsWrapper(request);
			
			// get the operation
			AA14ControllerOperation op = AA14ControllerOperation.from(request); 
			
			switch (op) {
				case INIT_DB:
				case RELOAD_CONFIG: {
					AA14ControllerServletDelegateForConfig configDelegate = new AA14ControllerServletDelegateForConfig(_clientAPI);
					configDelegate.executeOp(request,response,
											 op,reqParams);
					break;
				}
				case GENERATE_PERSON_LOCATOR: 
				case REMIND_PERSON_LOCATOR:
					AA14ControllerServletDelegateForPersonLocator locatorDelegate = new AA14ControllerServletDelegateForPersonLocator(_clientAPI);
					locatorDelegate.executeOp(request,response,
											   op,reqParams);
					break;
				case VALIDATE_MAX_WEEK_PERSON_APPOINTMENTS:
				case VALIDATE_PERSONID:
					AA14ControllerServletDelegateForDataValidation validateDelegate = new AA14ControllerServletDelegateForDataValidation(_clientAPI);
					validateDelegate.executeOp(request,response,
											   op,reqParams);
					break;
				case OBTENERCITAS:{
					AA14ControllerServletDelegateForSlotListing slotListingDelegate = new AA14ControllerServletDelegateForSlotListing(_clientAPI);
					slotListingDelegate.executeOp(request,response,
											  	  op,reqParams);
					break;
				}
				case CONFIRMARCITA:{
					AA14ControllerServletDelegateForAppointmentCreate appointmentCreateDelegate = new AA14ControllerServletDelegateForAppointmentCreate(_clientAPI);
					appointmentCreateDelegate.executeOp(request,response,
														op,reqParams);
					break;
				}
				case ACTUALIZAR_FECHA_HORA_CITA:{
					AA14ControllerServletDelegateForAppointmentDateTimeUpdate appointmentDateTimeChangeDelegate = new AA14ControllerServletDelegateForAppointmentDateTimeUpdate(_clientAPI);
					appointmentDateTimeChangeDelegate.executeOp(request,response, 
																op,reqParams);
					break;
				}
				case ACTUALIZAR_DATOS_PERSONALES_CITA:{
					AA14ControllerServletDelegateForAppointmentCustomerDataUpdate appointmentCustomerDataChangeDelegate = new AA14ControllerServletDelegateForAppointmentCustomerDataUpdate(_clientAPI);
					appointmentCustomerDataChangeDelegate.executeOp(request, response, 
																	op, reqParams);
					break;
				}
				case BUSCARCITAS:{
					AA14ControllerServletDelegateForAppointmentFind appointmentFindDelegate = new AA14ControllerServletDelegateForAppointmentFind(_clientAPI);
					appointmentFindDelegate.executeOp(request,response,
												  	  op,reqParams);
					break;
				}
				case BUSCAR_PERSONAS:{
					AA14ControllerServletDelegateForCustomerFind customerFindDelegate = new AA14ControllerServletDelegateForCustomerFind();
					customerFindDelegate.executeOp(request,response,
												  	  op,reqParams);
					break;
				}
				case SEND_NOTIFICATION:{
					AA14ControllerServletDelegateForAppointmentNotify notifierDelegate = new AA14ControllerServletDelegateForAppointmentNotify(_clientAPI);
					notifierDelegate.executeOp(request,response,
										   	   op,reqParams);
					break;
				}
				case RESERVAR_SLOT:{
					AA14ControllerServletDelegateForNonBookableSlotReserve nonBookableSlotReserveDelegate = new AA14ControllerServletDelegateForNonBookableSlotReserve(_clientAPI);
					nonBookableSlotReserveDelegate.executeOp(request,response,
												  		 	 op,reqParams);
					break;
				}
				case LIBERAR_SLOT:{
					AA14ControllerServletDelegateForSlotRelease appointmentCancelDelegate = new AA14ControllerServletDelegateForSlotRelease(_clientAPI);
					appointmentCancelDelegate.executeOp(request,response,
														op,reqParams);
					break;
				}
				default:{
					throw new IllegalArgumentException(op + " is NOT a recognized operation!!");
				}
			}
		
		} catch(ServletException servletEx) {
			log.error("Servlet error: {}",servletEx.getMessage(),servletEx);
			servletEx.printStackTrace(System.out);
			throw servletEx;
		} catch(IOException ioEx) {
			log.error("IO error: {}",ioEx.getMessage(),ioEx);
			ioEx.printStackTrace(System.out);
			throw ioEx;
		} catch(Throwable th) {
			log.error("Unknown error: {}",th.getMessage(),th);
			th.printStackTrace(System.out);
		}
	}
    
}
