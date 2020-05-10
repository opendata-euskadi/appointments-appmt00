package aa14a.bootstrap.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Binder;

import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenServletExposed;
import r01f.bootstrap.services.core.ServletImplementedServicesCoreBootstrapGuiceModuleBase;

abstract class AA14ServletServicesBootstrapGuiceModuleBase 
	   extends ServletImplementedServicesCoreBootstrapGuiceModuleBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ServletServicesBootstrapGuiceModuleBase(final ServicesCoreBootstrapConfigWhenServletExposed coreBootstrapCfg) {
		super(coreBootstrapCfg);
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	protected void configureBasicBindings(final Binder binder) {
		// json mapper used at the calendar
		binder.bind(ObjectMapper.class)
			  .toInstance(new ObjectMapper());
	}
}
