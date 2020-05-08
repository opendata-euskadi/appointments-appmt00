package aa14a.ui.servlet.delegate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aa14a.ui.servlet.AA14ControllerOperation;
import aa14a.ui.servlet.AA14ReqParamToType;
import aa14f.client.api.AA14ClientAPI;
import aa14f.model.AA14NotificationOperation;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.servlet.HttpRequestParamsWrapper;

@Slf4j
@RequiredArgsConstructor
public class AA14ControllerServletDelegateForAppointmentNotify
	 extends AA14ControllerServletDelegateBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14ClientAPI _clientAPI;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void executeOp(final HttpServletRequest request,final HttpServletResponse response,
						  final AA14ControllerOperation op,final HttpRequestParamsWrapper reqParams) throws ServletException, 
																 											IOException {
		log.debug("[init]: Notify Appointments-----------------");
		
		// get params
		AA14NotificationOperation notifOp = reqParams.getMandatoryParameter("notificationOp")
													 .asEnumElement(AA14NotificationOperation.class);
		AA14SlotOID slotOid = reqParams.getMandatoryParameter("slotOid")
										.asType(AA14SlotOID.class)
				 						.using(AA14ReqParamToType.transform(AA14SlotOID.class));
		
		// notify
		boolean notifyResult = _clientAPI.notifierAPI()
										 .sendAppointmentNotification(notifOp,
												 		   slotOid);
			_returnJsonResponse(response,
								notifyResult);  
				
		log.debug("[end]: Notify Appointments-----------------");
	}
}
