package aa14b.services;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;
import com.google.inject.persist.Transactional;

import aa14b.services.delegates.persistence.AA14CRUDServicesDelegateForOrgDivisionServiceLocation;
import aa14f.api.interfaces.AA14CRUDServicesForOrgDivisionServiceLocation;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
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
public class AA14CRUDServicesImplForOrgDivisionServiceLocation
     extends AA14CRUDServicesImplForOrganizationalEntityBase<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID,AA14OrgDivisionServiceLocation>
  implements AA14CRUDServicesForOrgDivisionServiceLocation {
/////////////////////////////////////////////////////////////////////////////////////////
//	DELEGATE PROVIDER: called at every services impl method to create a fresh new 
//					   EntityManager and avoid transactional issues
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ServiceDelegateProvider<AA14CRUDServicesDelegateForOrgDivisionServiceLocation> _delegateProvider;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14CRUDServicesImplForOrgDivisionServiceLocation(							final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
														     @ModelObjectsMarshaller 	final Marshaller modelObjectsMarshaller,
																						final EventBus eventBus,
																						final Provider<EntityManager> entityManagerProvider) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  entityManagerProvider);
		_delegateProvider = new ServiceDelegateProvider<AA14CRUDServicesDelegateForOrgDivisionServiceLocation>() {
									@Override
									public AA14CRUDServicesDelegateForOrgDivisionServiceLocation createDelegate(final SecurityContext securityContext) {
										return new AA14CRUDServicesDelegateForOrgDivisionServiceLocation(_coreConfig,
																										 AA14CRUDServicesImplForOrgDivisionServiceLocation.this.getFreshNewEntityManager(),
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
	public CRUDResult<AA14OrgDivisionServiceLocation> linkLocationToSchedules(final SecurityContext securityContext,
																			  final AA14OrgDivisionServiceLocationOID locOid,final Collection<AA14ScheduleOID> schOids) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14CRUDServicesForOrgDivisionServiceLocation.class)
							.linkLocationToSchedules(securityContext,
													 locOid,schOids);
	}


}
