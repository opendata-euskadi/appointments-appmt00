package aa14a.ui.servlet.delegate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aa14a.ui.servlet.AA14ControllerOperation;
import aa14f.client.api.AA14ClientAPI;
import lombok.RequiredArgsConstructor;
import r01f.servlet.HttpRequestParamsWrapper;

@RequiredArgsConstructor
public class AA14ControllerServletDelegateForConfig
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
		// Ensure config for every service
		_clientAPI.ensureConfig();
		
		// return
		_returnJsonResponse(response,"OK");
	}
}
