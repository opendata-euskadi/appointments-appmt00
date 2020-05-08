package aa14b.services;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14b.services.delegates.persistence.AA14PersonLocatorServicesDelegate;
import aa14f.api.interfaces.AA14FindServicesForBookedSlot;
import aa14f.api.interfaces.AA14PersonLocatorServices;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14IDs.AA14PersonLocatorID;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.locale.Language;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.services.COREServiceMethodExecResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.persistence.CorePersistenceServicesBase;
import r01f.services.persistence.ServiceDelegateProvider;
import r01f.types.contact.EMail;
import r01f.types.contact.PersonID;

/**
 * Implements the {@link AA14SearchServicesForEntityModelObject}s search-related services which in turn are delegated
 * {@link AA14SearchServicesDelegateForEntityModelObject} 
 */
@Singleton
@Accessors(prefix="_")
public class AA14PersonLocatorServicesImpl 
     extends CorePersistenceServicesBase					  
  implements AA14PersonLocatorServices,
  			 AA14ServiceInterfaceImpl {
/////////////////////////////////////////////////////////////////////////////////////////
//	DELEGATE PROVIDER: called at every services impl method to create a fresh new 
//					   EntityManager and avoid transactional issues
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ServiceDelegateProvider<AA14PersonLocatorServices> _delegateProvider;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR 
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14PersonLocatorServicesImpl(				final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
							   @ModelObjectsMarshaller 	final Marshaller modelObjectsMarshaller,
														final EventBus eventBus,
														final Provider<EntityManager> entityManagerProvider,
														final AA14FindServicesForBookedSlot bookedSlotsFind) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  entityManagerProvider);
		_delegateProvider = new ServiceDelegateProvider<AA14PersonLocatorServices>() {
									@Override
									public AA14PersonLocatorServices createDelegate(final SecurityContext securityContext) {
										return new AA14PersonLocatorServicesDelegate(_coreConfig,
																					 AA14PersonLocatorServicesImpl.this.getFreshNewEntityManager(),
																					 _modelObjectsMarshaller,
																					 _eventBus,
																					 bookedSlotsFind);
									}
							};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES EXTENSION
// 	IMPORTANT!!! Do NOT put any logic in these methods ONLY DELEGATE!!!
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public COREServiceMethodExecResult<AA14PersonLocatorID> findPersonLocatorFor(final SecurityContext securityContext,
																	 			 final PersonID personId,final EMail contactEMail) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14PersonLocatorServices.class)
							.findPersonLocatorFor(securityContext,
											  	  personId,contactEMail);
	}
	@Override
	public COREServiceMethodExecResult<Boolean> remindPersonLocatorFor(final SecurityContext securityContext,
																	   final AA14OrganizationID orgId,
																	   final PersonID personId,final EMail contactEMail,final Language lang) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14PersonLocatorServices.class)
							.remindPersonLocatorFor(securityContext,
													orgId,
											  	  	personId,contactEMail,lang);
	}
}
