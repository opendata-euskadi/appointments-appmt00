package aa14f.client.servicesproxy.rest;

import javax.inject.Inject;
import javax.inject.Singleton;

import aa14f.api.interfaces.AA14SearchServices;
import aa14f.model.search.AA14SearchFilterForOrganizationalEntity;
import aa14f.model.search.AA14SearchResultItemForOrganizationalEntity;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.services.client.servicesproxy.rest.RESTServicesForSearchProxyBase;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import r01f.xmlproperties.annotations.XMLPropertiesComponent;


@Singleton
public class AA14RESTSearchServicesProxy 
	 extends RESTServicesForSearchProxyBase<AA14SearchFilterForOrganizationalEntity,AA14SearchResultItemForOrganizationalEntity> 
  implements AA14SearchServices,
  			 AA14RESTServiceProxy {
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14RESTSearchServicesProxy(@XMLPropertiesComponent("client") final XMLPropertiesForAppComponent clientProps,
									   @ModelObjectsMarshaller 			 final Marshaller marshaller) {
		super(marshaller,
			  // all entities are searched under the same rest resource module called (entities)
			  // ... no mather if they are organizations, divisions or services
			  new AA14RESTServiceResourceUrlPathBuilderForModelObjectSearchIndexPersistence(clientProps));
	}
}
