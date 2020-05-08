package aa14b.services;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14b.services.delegates.persistence.AA14CRUDServicesDelegateForOrganization;
import aa14f.api.interfaces.AA14CRUDServicesForOrganization;
import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.persistence.ServiceDelegateProvider;

/**
 * Implements the persistence-related services which in turn are
 * delegated to {@link AA14CRUDServicesDelegateForOrganization}
 */
@Singleton
@Accessors(prefix="_")
public class AA14CRUDServicesImplForOrganization
     extends AA14CRUDServicesImplForOrganizationalEntityBase<AA14OrganizationOID,AA14OrganizationID,AA14Organization>
  implements AA14CRUDServicesForOrganization {
/////////////////////////////////////////////////////////////////////////////////////////
//	DELEGATE PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ServiceDelegateProvider<AA14CRUDServicesDelegateForOrganization> _delegateProvider;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14CRUDServicesImplForOrganization(							final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
											   @ModelObjectsMarshaller 	final Marshaller modelObjectsMarshaller,
																		final EventBus eventBus,
																		final Provider<EntityManager> entityManagerProvider) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  entityManagerProvider);
		_delegateProvider = new ServiceDelegateProvider<AA14CRUDServicesDelegateForOrganization>() {
									@Override
									public AA14CRUDServicesDelegateForOrganization createDelegate(final SecurityContext securityContext) {
										return new AA14CRUDServicesDelegateForOrganization(_coreConfig,
																						   AA14CRUDServicesImplForOrganization.this.getFreshNewEntityManager(),
																						   _modelObjectsMarshaller,_eventBus);
									}
							};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES EXTENSION
// 	IMPORTANT!!! Do NOT put any logic in these methods ONLY DELEGATE!!!
/////////////////////////////////////////////////////////////////////////////////////////
}
