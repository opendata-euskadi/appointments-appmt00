package aa14f.client.servicesproxy.rest;

import aa14f.api.interfaces.AA14FindServicesForOrganizationalEntityBase;
import aa14f.client.servicesproxy.rest.AA14RESTServiceResourceUrlPathBuilderBases.AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.locale.Language;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.FindResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.types.url.Url;
import r01f.types.url.UrlQueryString;
import r01f.types.url.UrlQueryStringParam;


abstract class AA14RESTFindServicesProxyForOrganizationalEntityBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,M extends PersistableModelObject<O>>
	   extends AA14RESTFindServicesProxyBase<O,ID,M>
    implements AA14FindServicesForOrganizationalEntityBase<O,ID,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public <P extends AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase<O,ID>>
		   AA14RESTFindServicesProxyForOrganizationalEntityBase(final Marshaller marshaller,
										 						final Class<M> modelObjectType,
										 						final P servicesRESTResourceUrlPathBuilder) {
		super(marshaller,
			  modelObjectType,
			  servicesRESTResourceUrlPathBuilder);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds org entities of the given type for the given business id
	 * @param securityContext
	 * @param orgEntityType
	 * @param businessId
	 * @return
	 */
	@Override
	public FindResult<M> findByBusinessId(final SecurityContext securityContext,
										  final AA14BusinessID businessId) {
		Url restResourceUrl = this.composeURIFor(this.getServicesRESTResourceUrlPathBuilderAs(AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase.class)
															   			.pathOfEntityListByBusinessId(businessId));
		return _findDelegate.doFindEntities(securityContext,
											restResourceUrl);
	}
	@Override 
	public FindResult<M> findByNameIn(final SecurityContext securityContext,
									  final Language lang,final String name) {
		Url restResourceUrl = this.composeURIFor(this.getServicesRESTResourceUrlPathBuilderAs(AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase.class)
															   			.pathOfEntityListByName(),
												  UrlQueryString.fromParams(UrlQueryStringParam.of(lang),
																		    UrlQueryStringParam.of("name",name)));
		return _findDelegate.doFindEntities(securityContext,
											restResourceUrl);
	}

}
