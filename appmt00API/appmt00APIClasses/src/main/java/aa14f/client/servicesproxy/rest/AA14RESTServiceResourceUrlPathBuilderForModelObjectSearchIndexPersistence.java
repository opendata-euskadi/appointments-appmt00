package aa14f.client.servicesproxy.rest;

import aa14f.client.servicesproxy.rest.AA14RESTServiceResourceUrlPathBuilderBases.AA14RESTServiceResourceUrlPathBuilderForPersistenceBase;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.bootstrap.services.config.client.ServicesCoreModuleExpositionAsRESTServices;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceEndPointUrl;
import r01f.types.url.UrlPath;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Search index persistence
 */
class AA14RESTServiceResourceUrlPathBuilderForModelObjectSearchIndexPersistence
	 extends AA14RESTServiceResourceUrlPathBuilderForPersistenceBase<AA14ModelObjectOID> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14RESTServiceResourceUrlPathBuilderForModelObjectSearchIndexPersistence(final XMLPropertiesForAppComponent clientProps) {
		super(new RESTServiceEndPointUrl(clientProps,
										 "persistence"),
			  UrlPath.from("index"));
	}
	public AA14RESTServiceResourceUrlPathBuilderForModelObjectSearchIndexPersistence(final ServicesCoreModuleExpositionAsRESTServices coreModuleRESTExposition) {
		super(new RESTServiceEndPointUrl(coreModuleRESTExposition),
			  UrlPath.from("index"));
	}
}