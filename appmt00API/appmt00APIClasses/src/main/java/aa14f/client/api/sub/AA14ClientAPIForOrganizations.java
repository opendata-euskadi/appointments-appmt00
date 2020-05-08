package aa14f.client.api.sub;

import java.util.Map;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14CRUDServicesForOrganization;
import aa14f.api.interfaces.AA14FindServicesForOrganization;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForOrganizationCRUDServices;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForOrganizationFindServices;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.ClientSubAPIBase;
import r01f.services.interfaces.ServiceInterface;

/**
 * Client implementation of the organizations maintenance
 */
@Accessors(prefix="_")
public class AA14ClientAPIForOrganizations
     extends ClientSubAPIBase {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final AA14ClientAPIDelegateForOrganizationCRUDServices _forCRUD;
	@Getter private final AA14ClientAPIDelegateForOrganizationFindServices _forFind;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("rawtypes")
	public AA14ClientAPIForOrganizations(final Provider<SecurityContext> securityContextProvider,
										 final Marshaller modelObjectsMarshaller,
								  		 final Map<Class,ServiceInterface> srvcIfaceMappings) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  srvcIfaceMappings);	// reference to other client apis

		_forCRUD = new AA14ClientAPIDelegateForOrganizationCRUDServices(securityContextProvider,
																		modelObjectsMarshaller,
													 			 	    this.getServiceInterfaceCoreImplOrProxy(AA14CRUDServicesForOrganization.class));
		_forFind = new AA14ClientAPIDelegateForOrganizationFindServices(securityContextProvider,
																		modelObjectsMarshaller,
															  		    this.getServiceInterfaceCoreImplOrProxy(AA14FindServicesForOrganization.class));
	}
}
