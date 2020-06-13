package aa14.batch;

import com.google.inject.Guice;
import com.google.inject.Injector;

import aa14b.bootstrap.core.AA14COREServicesBootstrapConfigBuilder;
import aa14f.bootstrap.client.AA14ClientBootstrapConfigBuilder;
import aa14f.common.internal.AA14AppCodes;
import r01f.bootstrap.services.ServicesBootstrapUtil;
import r01f.bootstrap.services.config.ServicesBootstrapConfig;
import r01f.bootstrap.services.config.ServicesBootstrapConfigBuilder;
import r01f.bootstrap.services.config.core.ServicesCoreModuleEventsConfig;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesForApp;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

public abstract class AA14BatchProcessBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  INJECTOR CREATION
/////////////////////////////////////////////////////////////////////////////////////////
	protected static Injector _createInjector() {
		// [0] - Load properties
		XMLPropertiesForApp xmlProps = XMLPropertiesBuilder.createForApp(AA14AppCodes.CORE_APPCODE)
														   .notUsingCache();
		XMLPropertiesForAppComponent servicesProps = xmlProps.forComponent(AppComponent.compose(AA14AppCodes.CORE_APPOINTMENTS_MOD,
																							 	CoreModule.SERVICES));
		
		// [1] - Create the modules bootstrap config
		ServicesBootstrapConfig bootCfg = ServicesBootstrapConfigBuilder
												.forClient(// client config
						 								   AA14ClientBootstrapConfigBuilder.buildClientBootstrapConfig())
				 							   	.ofCoreModules(// main core module
				 									   		   AA14COREServicesBootstrapConfigBuilder.buildCoreBootstrapConfig(xmlProps))
				 							   	.coreEventsHandledAs(ServicesCoreModuleEventsConfig.from(servicesProps))
				 							   	.build();
		// [2] - Create the guice injector
		Injector outInjector = Guice.createInjector(ServicesBootstrapUtil.getBootstrapGuiceModules(bootCfg)
																		 .withoutCommonBindingModules());
		ServicesBootstrapUtil.startServices(outInjector);		//	IMPORTANT!!!
		return outInjector;
	}	
}
