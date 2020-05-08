package aa14f.client.api.sub;

import java.util.Map;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14CRUDServicesForOrgDivision;
import aa14f.api.interfaces.AA14FindServicesForOrgDivision;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForOrgDivisionCRUDServices;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForOrgDivisionFindServices;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.ClientSubAPIBase;
import r01f.services.interfaces.ServiceInterface;

/**
 * Client implementation of the divisions maintenance.
 */
@Accessors(prefix="_")
public class AA14ClientAPIForOrgDivisions
     extends ClientSubAPIBase {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final AA14ClientAPIDelegateForOrgDivisionCRUDServices _forCRUD;
	@Getter private final AA14ClientAPIDelegateForOrgDivisionFindServices _forFind;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("rawtypes")
	public AA14ClientAPIForOrgDivisions(final Provider<SecurityContext> securityContextProvider,
										final Marshaller modelObjectsMarshaller,
										final Map<Class,ServiceInterface> srvcIfaceMappings) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  srvcIfaceMappings);	// reference to other client apis

		_forCRUD = new AA14ClientAPIDelegateForOrgDivisionCRUDServices(securityContextProvider,
																	   modelObjectsMarshaller,
													 			 	   this.getServiceInterfaceCoreImplOrProxy(AA14CRUDServicesForOrgDivision.class));
		_forFind = new AA14ClientAPIDelegateForOrgDivisionFindServices(securityContextProvider,
																	   modelObjectsMarshaller,
															  		   this.getServiceInterfaceCoreImplOrProxy(AA14FindServicesForOrgDivision.class));
	}
}
