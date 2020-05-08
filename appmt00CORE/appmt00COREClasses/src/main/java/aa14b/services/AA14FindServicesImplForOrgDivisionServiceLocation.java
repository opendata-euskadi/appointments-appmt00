package aa14b.services;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14b.services.delegates.persistence.AA14CRUDServicesDelegateForOrgDivisionServiceLocation;
import aa14b.services.delegates.persistence.AA14FindServicesDelegateForOrgDivisionServiceLocation;
import aa14f.api.interfaces.AA14FindServicesForOrgDivisionServiceLocation;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
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
 * delegated to {@link AA14CRUDServicesDelegateForOrgDivisionServiceLocation}
 */
@Singleton
@Accessors(prefix="_")
public class AA14FindServicesImplForOrgDivisionServiceLocation
     extends AA14FindServicesImplForOrganizationalEntityBase<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID,AA14OrgDivisionServiceLocation>
  implements AA14FindServicesForOrgDivisionServiceLocation {
/////////////////////////////////////////////////////////////////////////////////////////
//	DELEGATE PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ServiceDelegateProvider<AA14FindServicesDelegateForOrgDivisionServiceLocation> _delegateProvider;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14FindServicesImplForOrgDivisionServiceLocation(							final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
														     @ModelObjectsMarshaller 	final Marshaller modelObjectsMarshaller,
																						final EventBus eventBus,
																						final Provider<EntityManager> entityManagerProvider) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  entityManagerProvider);
		_delegateProvider = new ServiceDelegateProvider<AA14FindServicesDelegateForOrgDivisionServiceLocation>() {
									@Override
									public AA14FindServicesDelegateForOrgDivisionServiceLocation createDelegate(final SecurityContext securityContext) {
										return new AA14FindServicesDelegateForOrgDivisionServiceLocation(_coreConfig,
																										 AA14FindServicesImplForOrgDivisionServiceLocation.this.getFreshNewEntityManager(),
																										 _modelObjectsMarshaller,_eventBus);
									}
							};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES EXTENSION
// 	IMPORTANT!!! Do NOT put any logic in these methods ONLY DELEGATE!!!
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14OrgDivisionServiceLocation> findByOrgDivisionService(final SecurityContext securityContext,
										   					 				   final AA14OrgDivisionServiceOID serviceOid) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForOrgDivisionServiceLocation.class)
							.findByOrgDivisionService(securityContext,
											   		  serviceOid);
	}
	@Override
	public FindSummariesResult<AA14OrgDivisionServiceLocation> findSummariesByOrgDivisionService(final SecurityContext securityContext,
																  			   	  				 final AA14OrgDivisionServiceOID serviceOid,
																  			   	  				 final Language lang) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14FindServicesForOrgDivisionServiceLocation.class)
							.findSummariesByOrgDivisionService(securityContext,
													 		   serviceOid,
													 		   lang);
	}
}
