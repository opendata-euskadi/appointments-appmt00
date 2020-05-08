package aa14b.services.delegates.search;

import com.google.common.eventbus.EventBus;

import aa14f.api.interfaces.AA14SearchServices;
import aa14f.model.search.AA14SearchFilter;
import aa14f.model.search.AA14SearchResultItem;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.search.Searcher;
import r01f.securitycontext.SecurityContext;
import r01f.services.delegates.persistence.search.SearchServicesForModelObjectDelegateBase;
import r01f.services.delegates.persistence.search.ValidatesSearchFilter;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;


public class AA14SearchServicesDelegate 
     extends SearchServicesForModelObjectDelegateBase<AA14SearchFilter,AA14SearchResultItem>
  implements AA14SearchServices,
  			 ValidatesSearchFilter<AA14SearchFilter> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14SearchServicesDelegate(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
							  	      final Searcher<AA14SearchFilter,AA14SearchResultItem> searcher,
							  	      final Marshaller modelObjectsMarshaller,
							  	      final EventBus eventBus) {
		super(coreCfg,
			  searcher,
			  eventBus);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ObjectValidationResult<AA14SearchFilter> validateSearchFilter(final SecurityContext securityContext, 
																		 final AA14SearchFilter filter) {
		if (filter.getUILanguage() == null) return ObjectValidationResultBuilder.on(filter)
																				.isNotValidBecause("The language is null");
		return ObjectValidationResultBuilder.on(filter)
											.isValid();
	}
}
