package aa14b.services.delegates.persistence;

import aa14f.api.interfaces.AA14FindServicesForOrganizationalEntityBase;
import aa14f.model.config.AA14OrganizationalModelObject;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.locale.Language;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindResultBuilder;
import r01f.persistence.db.DBFindForModelObject;
import r01f.securitycontext.SecurityContext;


abstract class AA14FindServicesDelegateForOrganizationalEntityBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,M extends AA14OrganizationalModelObject<O,ID>>
	   extends AA14FindServicesDelegateBase<O,ID,M>
    implements AA14FindServicesForOrganizationalEntityBase<O,ID,M> {

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14FindServicesDelegateForOrganizationalEntityBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
															   final Class<M> modelObjectType,
									    					   final DBFindForModelObject<O,M> find) {
		super(coreCfg,
			  modelObjectType,
			  find);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public FindResult<M> findByBusinessId(final SecurityContext securityContext,
										  final AA14BusinessID businessId) {
		if (businessId == null) throw new IllegalArgumentException("businessId is mandatory!");
		
		// simply delegate
		return this.getServiceImplAs(AA14FindServicesForOrganizationalEntityBase.class)
				   .findByBusinessId(securityContext,
						   			 businessId);
	}
	@Override @SuppressWarnings("unchecked")
	public FindResult<M> findByNameIn(final SecurityContext securityContext,
									  final Language lang,final String name) {
		if (name == null || lang == null) {
			return FindResultBuilder.using(securityContext)
								    .on(_modelObjectType)
								    .errorFindingEntities()
								   		.causedByClientBadRequest("The name or lang cannot be null");
		}
		return this.getServiceImplAs(AA14FindServicesForOrganizationalEntityBase.class)
						.findByNameIn(securityContext,
								   	  lang,name);
	}
}
