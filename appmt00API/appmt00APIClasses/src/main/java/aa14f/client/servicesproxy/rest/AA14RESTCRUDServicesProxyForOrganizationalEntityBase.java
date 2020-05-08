package aa14f.client.servicesproxy.rest;

import aa14f.api.interfaces.AA14CRUDServicesForOrganizationalEntityBase;
import aa14f.client.servicesproxy.rest.AA14RESTServiceResourceUrlPathBuilderBases.AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.model.PersistableModelObject;
import r01f.objectstreamer.Marshaller;


abstract class AA14RESTCRUDServicesProxyForOrganizationalEntityBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,M extends PersistableModelObject<O>>
	   extends AA14RESTCRUDServicesProxyBase<O,ID,M>
    implements AA14CRUDServicesForOrganizationalEntityBase<O,ID,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public <P extends AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase<O,ID>>
		   AA14RESTCRUDServicesProxyForOrganizationalEntityBase(final Marshaller marshaller,
												  				final Class<M> modelObjectType,
												  				final P servicesRESTResourceUrlPathBuilder) {
		super(marshaller,
			  modelObjectType,
			  servicesRESTResourceUrlPathBuilder);
	}
}
