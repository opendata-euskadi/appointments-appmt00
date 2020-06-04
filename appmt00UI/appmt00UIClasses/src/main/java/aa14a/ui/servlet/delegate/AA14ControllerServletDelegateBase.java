package aa14a.ui.servlet.delegate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import aa14a.ui.servlet.AA14ControllerOperation;
import r01f.servlet.HttpRequestParamsWrapper;

/**
 * Encapsulates some functions of the controller 
 */
abstract class AA14ControllerServletDelegateBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	protected abstract void executeOp(final HttpServletRequest request,final HttpServletResponse response,
						     		  final AA14ControllerOperation op,final HttpRequestParamsWrapper reqParams) throws ServletException, 
																 											   		    IOException;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	protected static void _returnJsonResponse(final HttpServletResponse response,
							   		 		  final Object obj) throws IOException {
		
		Gson gson = new Gson();		//dates are not returned in ISO format, use GsonBuilder().setDateFormat(Dates.ISO8601)
		String json = gson.toJson(obj);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
}
