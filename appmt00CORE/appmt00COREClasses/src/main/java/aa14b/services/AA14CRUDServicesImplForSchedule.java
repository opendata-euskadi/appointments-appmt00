package aa14b.services;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;
import com.google.inject.persist.Transactional;

import aa14b.services.delegates.persistence.AA14CRUDServicesDelegateForOrgDivisionServiceLocation;
import aa14b.services.delegates.persistence.AA14CRUDServicesDelegateForSchedule;
import aa14f.api.interfaces.AA14CRUDServicesForSchedule;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.persistence.CRUDResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.persistence.ServiceDelegateProvider;

/**
 * Implements the persistence-related services which in turn are
 * delegated to {@link AA14CRUDServicesDelegateForOrgDivisionServiceLocation}
 */
@Singleton
@Accessors(prefix="_")
public class AA14CRUDServicesImplForSchedule
     extends AA14CRUDServicesImplBase<AA14ScheduleOID,AA14ScheduleID,AA14Schedule>
  implements AA14CRUDServicesForSchedule {
/////////////////////////////////////////////////////////////////////////////////////////
//	DELEGATE PROVIDER: called at every services impl method to create a fresh new 
//					   EntityManager and avoid transactional issues
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ServiceDelegateProvider<AA14CRUDServicesDelegateForSchedule> _delegateProvider;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14CRUDServicesImplForSchedule(							final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
										   @ModelObjectsMarshaller 	final Marshaller modelObjectsMarshaller,
																	final EventBus eventBus,
																	final Provider<EntityManager> entityManagerProvider) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  entityManagerProvider);
		_delegateProvider = new ServiceDelegateProvider<AA14CRUDServicesDelegateForSchedule>() {
									@Override
									public AA14CRUDServicesDelegateForSchedule createDelegate(final SecurityContext securityContext) {
										return new AA14CRUDServicesDelegateForSchedule(_coreConfig,
																					   AA14CRUDServicesImplForSchedule.this.getFreshNewEntityManager(),
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
	public CRUDResult<AA14Schedule> linkScheduleToServiceLocations(final SecurityContext securityContext,
																   final AA14ScheduleOID schOid,final Collection<AA14OrgDivisionServiceLocationOID> locOids) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14CRUDServicesForSchedule.class)
							.linkScheduleToServiceLocations(securityContext,
															schOid,locOids);
	}
}
