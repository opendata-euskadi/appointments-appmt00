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
		
		Gson gson = new Gson();
		//TODO test this change to return tracking info (create/update time) with ISO8601 format to avoid moment.js warning pasted above
		//Dates are parsed to JSON as DateFormat.MEDIUM (that means: Apr 8 2019 11:23:02 AM )
		
		//moment.js says
		//Deprecation warning: value provided is not in a recognized RFC2822 or ISO format. 
		//moment construction falls back to js Date(), which is not reliable across all browsers and versions. 
		//Non RFC2822/ISO date formats are discouraged and will be removed in an upcoming major release. 
		//Please refer to http://momentjs.com/guides/#/warnings/js-date/ 
		
//		Gson gson = new GsonBuilder()
//                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//                .create();
		String json = gson.toJson(obj);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
}
