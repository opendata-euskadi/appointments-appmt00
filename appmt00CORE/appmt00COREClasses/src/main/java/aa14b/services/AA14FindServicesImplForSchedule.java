package aa14b.services;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;
import com.google.inject.persist.Transactional;

import aa14b.services.delegates.persistence.AA14CRUDServicesDelegateForSchedule;
import aa14b.services.delegates.persistence.AA14FindServicesDelegateForSchedule;
import aa14f.api.interfaces.AA14FindServicesForSchedule;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.persistence.FindResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.persistence.ServiceDelegateProvider;


/**
 * Implements the find-related services which in turn are
 * delegated to {@link AA14CRUDServicesDelegateForSchedule}
 */
@Singleton
@Accessors(prefix="_")
public class AA14FindServicesImplForSchedule
     extends AA14FindServicesImplBase<AA14ScheduleOID,AA14ScheduleID,AA14Schedule>
  implements AA14FindServicesForSchedule {
/////////////////////////////////////////////////////////////////////////////////////////
//	DELEGATE PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ServiceDelegateProvider<AA14FindServicesDelegateForSchedule> _delegateProvider;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14FindServicesImplForSchedule(							final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
										   @ModelObjectsMarshaller 	final Marshaller modelObjectsMarshaller,
																	final EventBus eventBus,
																	final Provider<EntityManager> entityManagerProvider) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  entityManagerProvider);
		_delegateProvider = new ServiceDelegateProvider<AA14FindServicesDelegateForSchedule>() {
									@Override
									public AA14FindServicesDelegateForSchedule createDelegate(final SecurityContext securityContext) {
										return new AA14FindServicesDelegateForSchedule(_coreConfig,
																					   AA14FindServicesImplForSchedule.this.getFreshNewEntityManager(),
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
	public FindResult<AA14Schedule> findByBusinessId(final SecurityContext securityContext,
										  			 final AA14BusinessID businessId) {
		return this.forSecurityContext(securityContext)
				   .createDelegateAs(AA14FindServicesForSchedule.class)
				   .findByBusinessId(securityContext,
						   			 businessId);
	}
}
