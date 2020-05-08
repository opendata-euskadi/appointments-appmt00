package aa14b.services;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14b.services.delegates.persistence.AA14CRUDServicesDelegateForOrgDivision;
import aa14b.services.delegates.persistence.AA14FindServicesDelegateForOrgDivision;
import aa14f.api.interfaces.AA14FindServicesForOrgDivision;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
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
 * delegated to {@link AA14CRUDServicesDelegateForOrgDivision}
 */
@Singleton
@Accessors(prefix="_")
public class AA14FindServicesImplForOrgDivision
     extends AA14FindServicesImplForOrganizationalEntityBase<AA14OrgDivisionOID,AA14OrgDivisionID,AA14OrgDivision>
  implements AA14FindServicesForOrgDivision {
/////////////////////////////////////////////////////////////////////////////////////////
//	DELEGATE PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ServiceDelegateProvider<AA14FindServicesDelegateForOrgDivision> _delegateProvider;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14FindServicesImplForOrgDivision(							final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
											  @ModelObjectsMarshaller 	final Marshaller modelObjectsMarshaller,
																		final EventBus eventBus,
																	    final Provider<EntityManager> entityManagerProvider) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  entityManagerProvider);
		_delegateProvider = new ServiceDelegateProvider<AA14FindServicesDelegateForOrgDivision>() {
									@Override
									public AA14FindServicesDelegateForOrgDivision createDelegate(final SecurityContext securityContext) {
										return new AA14FindServicesDelegateForOrgDivision(_coreConfig,
																						  AA14FindServicesImplForOrgDivision.this.getFreshNewEntityManager(),
																						  _modelObjectsMarshaller,_eventBus);
									}
							};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES EXTENSION
// 	IMPORTANT!!! Do NOT put any logic in these methods ONLY DELEGATE!!!
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14OrgDivision> findByOrganization(final SecurityContext securityContext,
													   	  final AA14OrganizationOID orgOid) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForOrgDivision.class)
							.findByOrganization(securityContext,
												orgOid);
	}
	@Override
	public FindSummariesResult<AA14OrgDivision> findSummariesByOrganization(final SecurityContext securityContext,
																		 	final AA14OrganizationOID orgOid,
																		 	final Language lang) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForOrgDivision.class)
							.findSummariesByOrganization(securityContext,
														 orgOid,lang);
	}
}
