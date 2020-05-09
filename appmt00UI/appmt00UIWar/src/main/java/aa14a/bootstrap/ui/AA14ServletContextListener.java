package aa14a.bootstrap.ui;

import javax.servlet.ServletContextEvent;

import aa14b.bootstrap.core.AA14COREServicesBootstrapConfigBuilder;
import aa14f.bootstrap.client.AA14ClientBootstrapConfigBuilder;
import aa14f.client.api.AA14ClientAPI;
import aa14f.common.internal.AA14AppCodes;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.ServletContextListenerBase;
import r01f.bootstrap.services.config.ServicesBootstrapConfig;
import r01f.bootstrap.services.config.ServicesBootstrapConfigBuilder;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfig;
import r01f.bootstrap.services.config.core.ServicesCoreModuleEventsConfig;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesForApp;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Slf4j
public class AA14ServletContextListener
	 extends ServletContextListenerBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ServletContextListener() {
		super(_buildServicesBootstrapConfig());
	}
	private static final ServicesBootstrapConfig[] _buildServicesBootstrapConfig() {
		// [0] - Load properties
		XMLPropertiesForApp xmlProps = XMLPropertiesBuilder.createForApp(AA14AppCodes.CORE_APPCODE)
														   .notUsingCache();
		XMLPropertiesForAppComponent servicesProps = xmlProps.forComponent(AppComponent.compose(AA14AppCodes.CORE_APPOINTMENTS_MOD,
																							 	CoreModule.SERVICES));
		
		// [1] - Create the modules bootstrap config
		ServicesBootstrapConfig bootCfg = ServicesBootstrapConfigBuilder
												.forClient(AA14ClientBootstrapConfigBuilder.buildClientBootstrapConfig())
				 							   	.ofCoreModules(_buildCoreBootstrapConfigs(xmlProps))
				 							   	.coreEventsHandledAs(ServicesCoreModuleEventsConfig.from(servicesProps))
				 							   	.build();
		return new ServicesBootstrapConfig[] { 
						bootCfg
				   };
	}
	private static final ServicesCoreBootstrapConfig[] _buildCoreBootstrapConfigs(final XMLPropertiesForApp xmlProps) {
		return new ServicesCoreBootstrapConfig[] {
						   AA14COREServicesBootstrapConfigBuilder.buildCoreBootstrapConfig(xmlProps),
						   // ui servlet services
						   AA14ServletServicesBootstrapConfigBuilder.buildCoreBootstrapConfig(xmlProps)
				   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ServletContextListenerBase
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void contextInitialized(final ServletContextEvent servletContextEvent) {
		super.contextInitialized(servletContextEvent);
		
		// ensure the config is loaded
		try {
			AA14ClientAPI clientApi = this.getInjector().getInstance(AA14ClientAPI.class);
			clientApi.configAPI()
					 .forceReloadConfig();
		} catch (Throwable th) {
			log.error("\n\n\n\n");
			log.error("///////// Error loading the config: " + th.getMessage());
			log.error("///////// Maybe there's NO config: use '/appmtXXUIWar/AA14ControllerServlet?op=INIT_DB' to create the initial DB data");
			log.error("\n\n\n\n");
		}
	}
}
