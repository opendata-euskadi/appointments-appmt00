package aa14f.client.api.sub;

import java.util.Map;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14CRUDServicesForOrgDivisionService;
import aa14f.api.interfaces.AA14FindServicesForOrgDivisionService;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForOrgDivisionServiceCRUDServices;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForOrgDivisionServiceFindServices;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.ClientSubAPIBase;
import r01f.services.interfaces.ServiceInterface;

/**
 * Client implementation of services maintenance.
 */
@Accessors(prefix="_")
public class AA14ClientAPIForOrgDivisionServices
     extends ClientSubAPIBase {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final AA14ClientAPIDelegateForOrgDivisionServiceCRUDServices _forCRUD;
	@Getter private final AA14ClientAPIDelegateForOrgDivisionServiceFindServices _forFind;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("rawtypes")
	public AA14ClientAPIForOrgDivisionServices(final Provider<SecurityContext> securityContextProvider,
											   final Marshaller modelObjectsMarshaller,
											   final Map<Class,ServiceInterface> srvcIfaceMappings) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  srvcIfaceMappings);	// reference to other client apis

		_forCRUD = new AA14ClientAPIDelegateForOrgDivisionServiceCRUDServices(securityContextProvider,
																			  modelObjectsMarshaller,
																			  this.getServiceInterfaceCoreImplOrProxy(AA14CRUDServicesForOrgDivisionService.class));
		_forFind = new AA14ClientAPIDelegateForOrgDivisionServiceFindServices(securityContextProvider,
																			  modelObjectsMarshaller,
															  		     	  this.getServiceInterfaceCoreImplOrProxy(AA14FindServicesForOrgDivisionService.class));
	}
}
