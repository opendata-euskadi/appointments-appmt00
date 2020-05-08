package aa14f.client.servicesproxy.rest;

import aa14f.api.interfaces.AA14FindServicesBase;
import aa14f.client.servicesproxy.rest.AA14RESTServiceResourceUrlPathBuilderBases.AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.model.PersistableModelObject;
import r01f.objectstreamer.Marshaller;
import r01f.services.client.servicesproxy.rest.RESTServicesForDBFindProxyBase;


abstract class AA14RESTFindServicesProxyBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,M extends PersistableModelObject<O>>
	   extends RESTServicesForDBFindProxyBase<O,M>
    implements AA14FindServicesBase<O,ID,M>,
    		   AA14RESTServiceProxy {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public <P extends AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase<O,ID>>
		   AA14RESTFindServicesProxyBase(final Marshaller marshaller,
										 final Class<M> modelObjectType,
										 final P servicesRESTResourceUrlPathBuilder) {
		super(marshaller,
			  modelObjectType,
			  servicesRESTResourceUrlPathBuilder);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
}
