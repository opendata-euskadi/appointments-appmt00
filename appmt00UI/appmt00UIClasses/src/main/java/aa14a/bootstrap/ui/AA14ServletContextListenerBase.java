package aa14a.bootstrap.ui;

import javax.servlet.ServletContextEvent;

import aa14f.client.api.AA14ClientAPI;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.ServletContextListenerBase;
import r01f.bootstrap.services.config.ServicesBootstrapConfig;

@Slf4j
abstract class AA14ServletContextListenerBase
	   extends ServletContextListenerBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ServletContextListenerBase(final ServicesBootstrapConfig... bootstrapCfgs) {
		super(bootstrapCfgs);
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
