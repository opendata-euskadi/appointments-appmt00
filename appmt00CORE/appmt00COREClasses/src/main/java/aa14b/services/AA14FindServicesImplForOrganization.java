package aa14b.services;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;
import com.google.inject.persist.Transactional;

import aa14b.services.delegates.persistence.AA14CRUDServicesDelegateForOrganization;
import aa14b.services.delegates.persistence.AA14FindServicesDelegateForOrganization;
import aa14f.api.interfaces.AA14FindServicesForOrganization;
import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.locale.Language;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.persistence.FindSummariesResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.persistence.ServiceDelegateProvider;

/**
 * Implements the find-related services which in turn are
 * delegated to {@link AA14CRUDServicesDelegateForOrganization}
 */
@Singleton
@Accessors(prefix="_")
public class AA14FindServicesImplForOrganization
     extends AA14FindServicesImplForOrganizationalEntityBase<AA14OrganizationOID,AA14OrganizationID,AA14Organization>
  implements AA14FindServicesForOrganization {
/////////////////////////////////////////////////////////////////////////////////////////
//	DELEGATE PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ServiceDelegateProvider<AA14FindServicesDelegateForOrganization> _delegateProvider;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14FindServicesImplForOrganization(							final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
											   @ModelObjectsMarshaller 	final Marshaller modelObjectsMarshaller,
																		final EventBus eventBus,
																		final Provider<EntityManager> entityManagerProvider) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  entityManagerProvider);
		_delegateProvider = new ServiceDelegateProvider<AA14FindServicesDelegateForOrganization>() {
									@Override
									public AA14FindServicesDelegateForOrganization createDelegate(final SecurityContext securityContext) {
										return new AA14FindServicesDelegateForOrganization(_coreConfig,
																						   AA14FindServicesImplForOrganization.this.getFreshNewEntityManager(),
																						   _modelObjectsMarshaller,_eventBus);
									}
							};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES EXTENSION
// 	IMPORTANT!!! Do NOT put any logic in these methods ONLY DELEGATE!!!
/////////////////////////////////////////////////////////////////////////////////////////
	@Transactional
	@Override
	public FindSummariesResult<AA14Organization> findSummaries(final SecurityContext securityContext,
															   final Language lang) {
		// simply delegate
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForOrganization.class)
						   .findSummaries(securityContext,
								   		  lang);
				   
	}
}
