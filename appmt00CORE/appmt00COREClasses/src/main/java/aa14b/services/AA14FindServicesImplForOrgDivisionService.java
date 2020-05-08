package aa14b.services;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14b.services.delegates.persistence.AA14CRUDServicesDelegateForOrgDivisionService;
import aa14b.services.delegates.persistence.AA14FindServicesDelegateForOrgDivisionService;
import aa14f.api.interfaces.AA14FindServicesForOrgDivisionService;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.locale.Language;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindSummariesResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.persistence.ServiceDelegateProvider;


/**
 * Implements the find-related services which in turn are
 * delegated to {@link AA14CRUDServicesDelegateForOrgDivisionService}
 */
@Singleton
@Accessors(prefix="_")
public class AA14FindServicesImplForOrgDivisionService
     extends AA14FindServicesImplForOrganizationalEntityBase<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID,AA14OrgDivisionService>
  implements AA14FindServicesForOrgDivisionService {
/////////////////////////////////////////////////////////////////////////////////////////
//	DELEGATE PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ServiceDelegateProvider<AA14FindServicesDelegateForOrgDivisionService> _delegateProvider;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14FindServicesImplForOrgDivisionService(							final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
												     @ModelObjectsMarshaller 	final Marshaller modelObjectsMarshaller,
																				final EventBus eventBus,
																				final Provider<EntityManager> entityManagerProvider) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  entityManagerProvider);
		_delegateProvider = new ServiceDelegateProvider<AA14FindServicesDelegateForOrgDivisionService>() {
									@Override
									public AA14FindServicesDelegateForOrgDivisionService createDelegate(final SecurityContext securityContext) {
										return new AA14FindServicesDelegateForOrgDivisionService(_coreConfig,
																								 AA14FindServicesImplForOrgDivisionService.this.getFreshNewEntityManager(),
																								 _modelObjectsMarshaller,_eventBus);
									}
							};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES EXTENSION
// 	IMPORTANT!!! Do NOT put any logic in these methods ONLY DELEGATE!!!
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14OrgDivisionService> findByOrgDivision(final SecurityContext securityContext,
										   					 	final AA14OrgDivisionOID orgDivisionOid) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForOrgDivisionService.class)
							.findByOrgDivision(securityContext,
											   orgDivisionOid);
	}
	@Override
	public FindSummariesResult<AA14OrgDivisionService> findSummariesByOrgDivision(final SecurityContext securityContext,
																  			   	  final AA14OrgDivisionOID orgDivisionOid,
																  			   	  final Language lang) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForOrgDivisionService.class)
							.findSummariesByOrgDivision(securityContext,
													 	orgDivisionOid,
													 	lang);
	}
}
