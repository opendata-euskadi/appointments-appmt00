package aa14f.client.servicesproxy.rest;

import aa14f.api.interfaces.AA14CRUDServicesBase;
import aa14f.client.servicesproxy.rest.AA14RESTServiceResourceUrlPathBuilderBases.AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.httpclient.HttpResponse;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.servicesproxy.rest.DelegateForRawREST;
import r01f.services.client.servicesproxy.rest.RESTServicesForDBCRUDProxyBase;
import r01f.types.url.Url;


abstract class AA14RESTCRUDServicesProxyBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,M extends PersistableModelObject<O>>
	   extends RESTServicesForDBCRUDProxyBase<O,M>
    implements AA14CRUDServicesBase<O,ID,M>,
    		   AA14RESTServiceProxy {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public <P extends AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase<O,ID>>
		   AA14RESTCRUDServicesProxyBase(final Marshaller marshaller,
										 final Class<M> modelObjectType,
										 final P servicesRESTResourceUrlPathBuilder) {
		super(marshaller,
			  modelObjectType,
			  servicesRESTResourceUrlPathBuilder);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDResult<M> loadById(final SecurityContext securityContext,
								  final ID id) {
		Url restResourceUrl = this.composeURIFor(this.getServicesRESTResourceUrlPathBuilderAs(AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase.class)
															   	.pathOfEntityById(id));
		String ctxXml = _marshaller.forWriting().toXml(securityContext);
		HttpResponse httpResponse = DelegateForRawREST.GET(restResourceUrl,
										 				   ctxXml);
		// map the response
		CRUDResult<M> outResponse = this.getResponseToCRUDResultMapperForModelObject()
											.mapHttpResponseForEntity(securityContext,
											  				  		  PersistenceRequestedOperation.LOAD,
											  				  		  restResourceUrl,httpResponse)
										    .identifiedOnErrorBy(id);
		// log & return
		_logResponse(restResourceUrl,outResponse);
		return outResponse;
	}
}
