package aa14b.bootstrap.core;


import aa14b.db.search.AA14DBSearcherProviders.AA14DBSearcherProvider;
import lombok.EqualsAndHashCode;
import r01f.bootstrap.persistence.SearchGuiceModuleBase;
import r01f.bootstrap.persistence.SearcherProviderBinding;
import r01f.persistence.search.config.SearchModuleConfig;
import r01f.util.types.collections.Lists;

@EqualsAndHashCode(callSuper=true)				// This is important for guice modules
  class AA14SearchGuiceModule 
extends SearchGuiceModuleBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14SearchGuiceModule(final SearchModuleConfig cfg) {
		super(cfg,
			  Lists.<SearcherProviderBinding<?,?>>newArrayList(
					  									AA14DBSearcherProvider.createGuiceBinding()
			  ));
	}
}
