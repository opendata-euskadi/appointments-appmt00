package aa14b.services;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.eventbus.EventBus;

import aa14b.db.search.AA14DBSearcherProviders.AA14DBSearcherProviderForOrganizationalEntity;
import aa14b.services.delegates.search.AA14SearchServicesDelegate;
import aa14f.api.interfaces.AA14SearchServices;
import aa14f.model.search.AA14SearchFilterForOrganizationalEntity;
import aa14f.model.search.AA14SearchResultItemForOrganizationalEntity;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.persistence.CoreSearchServicesForModelObjectBase;
import r01f.services.persistence.ServiceDelegateProvider;

/**
 * Implements the {@link AA14SearchServicesForEntityModelObject}s search-related services which in turn are delegated
 * {@link AA14SearchServicesDelegateForEntityModelObject} 
 */
@Singleton
@Accessors(prefix="_")
public class AA14SearchServicesImpl 
     extends CoreSearchServicesForModelObjectBase<AA14SearchFilterForOrganizationalEntity,AA14SearchResultItemForOrganizationalEntity>					  
  implements AA14SearchServices,
  			 AA14ServiceInterfaceImpl {
/////////////////////////////////////////////////////////////////////////////////////////
//	DELEGATE FACTORY
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ServiceDelegateProvider<AA14SearchServicesDelegate> _delegateProvider;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14SearchServicesImpl(							final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
								  @ModelObjectsMarshaller 	final Marshaller modelObjectsMarshaller,
															final EventBus eventBus,
			  											    final AA14DBSearcherProviderForOrganizationalEntity searcherFactory) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  searcherFactory);
		_delegateProvider = new ServiceDelegateProvider<AA14SearchServicesDelegate>() {
									@Override 
									public AA14SearchServicesDelegate createDelegate(final SecurityContext securityContext) {
										return new AA14SearchServicesDelegate(_coreConfig,
																			  AA14SearchServicesImpl.this.getFreshNewSearcher(),
																			  _modelObjectsMarshaller,
																			  _eventBus);
									}
						  	};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES EXTENSION
// 	IMPORTANT!!! Do NOT put any logic in these methods ONLY DELEGATE!!!
/////////////////////////////////////////////////////////////////////////////////////////
}
