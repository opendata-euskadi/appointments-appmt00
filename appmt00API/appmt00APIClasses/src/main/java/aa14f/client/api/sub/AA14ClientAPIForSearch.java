package aa14f.client.api.sub;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14SearchServices;
import aa14f.model.search.AA14SearchFilterForOrganizationalEntity;
import aa14f.model.search.AA14SearchResultItemForOrganizationalEntity;
import lombok.experimental.Accessors;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.api.delegates.ClientAPIDelegateForModelObjectSearchServices;

/**
 * Client implementation of search api
 */
@Accessors(prefix="_")
public class AA14ClientAPIForSearch
     extends ClientAPIDelegateForModelObjectSearchServices<AA14SearchFilterForOrganizationalEntity,			
     													   AA14SearchResultItemForOrganizationalEntity> {		
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIForSearch(final Provider<SecurityContext> securityContextProvider,
								  final Marshaller modelObjectsMarshaller,
								  final AA14SearchServices entitySearchServicesProxy) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  entitySearchServicesProxy,	// reference to other client apis
			  AA14SearchFilterForOrganizationalEntity.class,AA14SearchResultItemForOrganizationalEntity.class);
	}
}
