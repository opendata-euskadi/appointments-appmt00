package aa14a.ui.servlet.delegate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aa14a.ui.servlet.AA14ControllerOperation;
import aa14f.common.internal.AA14AppCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.ejie.xlnets.api.XLNetsAPI;
import r01f.ejie.xlnets.api.XLNetsAPIBuilder;
import r01f.ejie.xlnets.config.XLNetsTokenSource;
import r01f.ejie.xlnets.model.XLNetsUser;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.servlet.HttpRequestParamsWrapper;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesForApp;
import r01f.xmlproperties.XMLPropertiesForAppComponent;


@Slf4j
@RequiredArgsConstructor
public class AA14ControllerServletDelegateForCustomerFind
	 extends AA14ControllerServletDelegateBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final XMLPropertiesForAppComponent _xlnetsProps;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14ControllerServletDelegateForCustomerFind() {																			
		super();
		XMLPropertiesForApp xmlProps = XMLPropertiesBuilder.createForApp(AA14AppCodes.CORE_APPCODE)	// BEWARE!!! aa14b!!!
														   .notUsingCache();
		_xlnetsProps = new XMLPropertiesForAppComponent(xmlProps,	
														AppComponent.forId("xlnets"));
	}
	
	@Override
	public void executeOp(final HttpServletRequest request,final HttpServletResponse response,
						  final AA14ControllerOperation op,final HttpRequestParamsWrapper reqParams) throws ServletException, 
																 											IOException {
		log.debug("[init]: Search customer personal information-----------------");
		response.setCharacterEncoding("UTF-8");
		String term = request.getParameter("term");
		response.setContentType("Application/json");
		PrintWriter out = response.getWriter();
		
		if (null == term) {
			out.write("[]");
			return;
		}						
		
		int numResults = 0;
		try {
			final XLNetsTokenSource _tokenSource = _xlnetsProps.propertyAt("/xlnets/@token")
												   .asEnumFromCode(XLNetsTokenSource.class,
																   XLNetsTokenSource.N38API);
			XLNetsAPI _xlnetsAPI = null;
			
			if (_tokenSource.is(XLNetsTokenSource.N38API)) { 
				_xlnetsAPI = XLNetsAPIBuilder.createFrom(request);
			}
			else { 
				//use mock files
				_xlnetsAPI = XLNetsAPIBuilder.createAsDefinedAt(_xlnetsProps, "");
			}
			Collection<XLNetsUser> outUsers= XLNetsUser.from(_xlnetsAPI.getXLNetsUserQueryDoc(term));
			//Return the data in JSON format
			if (CollectionUtils.hasData(outUsers)) {
			out.write("[\n");	
			for (Iterator<XLNetsUser> it = outUsers.iterator(); it.hasNext(); ) {
					XLNetsUser user = it.next();
					String jsonItem = Strings.customized("{" + "\n" +
				        										"\"label\"		:\"{}\"," 	+ "\n\t" +
				        										"\"nif\"		:\"{}\"," 	+ "\n\t" +
									        					"\"name\"		:\"{}\"," 	+ "\n\t" +
									        					"\"surname\"	:\"{}\"," 	+ "\n\t" +
									        					"\"telephone\"	:\"{}\"," 	+ "\n\t" + 
									        					"\"mail\"		:\"{}\"" 	+ "\n" +
								        				  "}",
						        				  user.getDisplayName()!=null?user.getDisplayName():"",
						        				  user.getDni()!=null?user.getDni():"",
						        				  user.getName()!=null?user.getName():"",
						        				  user.getSurname()!=null?user.getSurname():"",
						        				  user.getTelephone()!=null?user.getTelephone():"",
						        				  user.getMail()!=null?user.getMail():"");
	        		out.write(jsonItem);
	        		if (it.hasNext()) out.write(",\n");
	        		numResults++;
				}
			out.write("\n]");
			}
			else {
				out.write("[]");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			if (numResults > 0) out.write("]");
		}
		log.debug("[end]: Search customer personal information-----------------");		
		out.flush();
		out.close();
		return;		
	}
	
	

}
