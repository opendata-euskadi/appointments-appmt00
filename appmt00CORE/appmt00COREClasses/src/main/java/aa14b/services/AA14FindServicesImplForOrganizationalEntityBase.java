package aa14b.services;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;
import com.google.inject.persist.Transactional;

import aa14f.api.interfaces.AA14FindServicesForOrganizationalEntityBase;
import aa14f.model.AA14EntityModelObject;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.locale.Language;
import r01f.model.persistence.FindResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;

/**
 * Implements the find-related services which in turn are
 * delegated to {@link AA14CRUDServicesDelegateForAgent}
 */
@Accessors(prefix="_")
public abstract class AA14FindServicesImplForOrganizationalEntityBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,M extends AA14EntityModelObject<O,ID>>
     		  extends AA14FindServicesImplBase<O,ID,M>
  		   implements AA14FindServicesForOrganizationalEntityBase<O,ID,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor
	 * @param coreCfg
	 * @param modelObjectsMarshaller annotated with @ModelObjectsMarshaller
	 * @param eventBus
	 * @param persistenceProperties annotated with @XMLPropertiesComponent("dbpersistence")
	 * @param entityManagerProvider
	 */
	public AA14FindServicesImplForOrganizationalEntityBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
														   final Marshaller modelObjectsMarshaller,
														   final EventBus eventBus,
														   final Provider<EntityManager> entityManagerProvider) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  entityManagerProvider);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES EXTENSION
// 	IMPORTANT!!! Do NOT put any logic in these methods ONLY DELEGATE!!!
/////////////////////////////////////////////////////////////////////////////////////////
	@Transactional
	@Override @SuppressWarnings("unchecked")
	public FindResult<M> findByBusinessId(final SecurityContext securityContext,
										  final AA14BusinessID businessId) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForOrganizationalEntityBase.class)
							.findByBusinessId(securityContext,
										      businessId);
	}
	@Transactional
	@Override @SuppressWarnings("unchecked")
	public FindResult<M> findByNameIn(final SecurityContext securityContext,
									  final Language lang,final String name) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForOrganizationalEntityBase.class)
							.findByNameIn(securityContext,
										  lang,name);
	}
	
}
