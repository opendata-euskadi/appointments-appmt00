package aa14b.services;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;
import com.google.inject.persist.Transactional;

import aa14b.services.delegates.persistence.AA14BusinessConfigServicesDelegate;
import aa14f.api.interfaces.AA14BusinessConfigServices;
import aa14f.api.interfaces.AA14FindServicesForOrgDivision;
import aa14f.api.interfaces.AA14FindServicesForOrgDivisionService;
import aa14f.api.interfaces.AA14FindServicesForOrgDivisionServiceLocation;
import aa14f.api.interfaces.AA14FindServicesForOrganization;
import aa14f.api.interfaces.AA14FindServicesForSchedule;
import aa14f.model.config.business.AA14BusinessConfigs;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.persistence.PersistenceOperationResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.persistence.CorePersistenceServicesBase;
import r01f.services.persistence.ServiceDelegateProvider;

/**
 * Implements the {@link AA14SearchServicesForEntityModelObject}s search-related services which in turn are delegated
 * {@link AA14SearchServicesDelegateForEntityModelObject} 
 */
@Singleton
@Accessors(prefix="_")
public class AA14BusinessConfigServicesImpl 
     extends CorePersistenceServicesBase					  
  implements AA14BusinessConfigServices,
  			 AA14ServiceInterfaceImpl {
/////////////////////////////////////////////////////////////////////////////////////////
//	DELEGATE PROVIDER: called at every services impl method to create a fresh new 
//					   EntityManager and avoid transactional issues
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ServiceDelegateProvider<AA14BusinessConfigServices> _delegateProvider;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR 
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14BusinessConfigServicesImpl(				final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
							   @ModelObjectsMarshaller 	final Marshaller modelObjectsMarshaller,
														final EventBus eventBus,
														final Provider<EntityManager> entityManagerProvider,
														// crud services
														final AA14FindServicesForOrganization orgFind,
														final AA14FindServicesForOrgDivision orgDivFind,
														final AA14FindServicesForOrgDivisionService orgDivSrvcFind,
														final AA14FindServicesForOrgDivisionServiceLocation orgDivSrvcLocFind,
														final AA14FindServicesForSchedule schFind) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  entityManagerProvider);
		// Delegate
		_delegateProvider = new ServiceDelegateProvider<AA14BusinessConfigServices>() {
									@Override
									public AA14BusinessConfigServices createDelegate(final SecurityContext securityContext) {
										return new AA14BusinessConfigServicesDelegate(_coreConfig,
																					  AA14BusinessConfigServicesImpl.this.getFreshNewEntityManager(),
																					  _modelObjectsMarshaller,
																					  _eventBus,
																					  // crud services
																					  orgFind,
																					  orgDivFind,
																					  orgDivSrvcFind,
																					  orgDivSrvcLocFind,
																					  schFind);
									}
							};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES EXTENSION
// 	IMPORTANT!!! Do NOT put any logic in these methods ONLY DELEGATE!!!
/////////////////////////////////////////////////////////////////////////////////////////
	@Transactional
	@Override
	public PersistenceOperationResult<AA14BusinessConfigs> loadConfig(final SecurityContext securityContext) {
		return this.forSecurityContext(securityContext)
					.createDelegateAs(AA14BusinessConfigServices.class)
						.loadConfig(securityContext);
	}
	@Transactional
	@Override
	public PersistenceOperationResult<Date> getLastUpdateDate(final SecurityContext securityContext) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14BusinessConfigServices.class)
							.getLastUpdateDate(securityContext);
	}
	@Transactional
	@Override
	public PersistenceOperationResult<Date> updateLastUpdateDate(final SecurityContext securityContext,
																 final Date date) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14BusinessConfigServices.class)
							.updateLastUpdateDate(securityContext,
												  date);
	}
}
