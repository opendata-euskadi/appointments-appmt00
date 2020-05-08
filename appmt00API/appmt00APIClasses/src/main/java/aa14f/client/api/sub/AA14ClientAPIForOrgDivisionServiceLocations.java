package aa14f.client.api.sub;

import java.util.Map;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14CRUDServicesForOrgDivisionServiceLocation;
import aa14f.api.interfaces.AA14FindServicesForOrgDivisionServiceLocation;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForOrgDivisionServiceLocationCRUDServices;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForOrgDivisionServiceLocationFindServices;
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
public class AA14ClientAPIForOrgDivisionServiceLocations
     extends ClientSubAPIBase {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final AA14ClientAPIDelegateForOrgDivisionServiceLocationCRUDServices _forCRUD;
	@Getter private final AA14ClientAPIDelegateForOrgDivisionServiceLocationFindServices _forFind;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("rawtypes")
	public AA14ClientAPIForOrgDivisionServiceLocations(final Provider<SecurityContext> securityContextProvider,
													   final Marshaller modelObjectsMarshaller,
													   final Map<Class,ServiceInterface> srvcIfaceMappings) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  srvcIfaceMappings);	// reference to other client apis

		_forCRUD = new AA14ClientAPIDelegateForOrgDivisionServiceLocationCRUDServices(securityContextProvider,
																					  modelObjectsMarshaller,
													 			 	     			  this.getServiceInterfaceCoreImplOrProxy(AA14CRUDServicesForOrgDivisionServiceLocation.class));
		_forFind = new AA14ClientAPIDelegateForOrgDivisionServiceLocationFindServices(securityContextProvider,
																					  modelObjectsMarshaller,
															  		     			  this.getServiceInterfaceCoreImplOrProxy(AA14FindServicesForOrgDivisionServiceLocation.class));
	}
}
