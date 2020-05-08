package aa14.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.google.common.collect.Lists;

import aa14b.bootstrap.core.AA14COREServicesBootstrapConfigBuilder;
import aa14f.bootstrap.client.AA14ClientBootstrapConfigBuilder;
import aa14f.client.api.AA14ClientAPI;
import aa14f.common.internal.AA14AppCodes;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.ServicesBootstrapConfig;
import r01f.bootstrap.services.config.ServicesBootstrapConfigBuilder;
import r01f.bootstrap.services.config.core.ServicesCoreModuleEventsConfig;
import r01f.exceptions.Throwables;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.test.api.TestAPIBase;
import r01f.xmlproperties.XMLPropertiesBuilder;
import r01f.xmlproperties.XMLPropertiesForApp;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * JVM arguments:
 * -javaagent:d:/eclipse/local_libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true
 */
@Accessors(prefix="_")
@RequiredArgsConstructor
public abstract class AA14TestBase 
	          extends TestAPIBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static AA14ClientAPI getClientApi() {
		AA14ClientAPI clientApi = TestAPIBase.getClientApi(AA14ClientAPI.class);
		return clientApi;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  JUnit
/////////////////////////////////////////////////////////////////////////////////////////
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			// [0] - Load properties
			XMLPropertiesForApp xmlProps = XMLPropertiesBuilder.createForApp(AA14AppCodes.CORE_APPCODE)
															   .notUsingCache();
			XMLPropertiesForAppComponent servicesProps = xmlProps.forComponent(AppComponent.compose(AA14AppCodes.CORE_APPOINTMENTS_MOD,
																								 	CoreModule.SERVICES));
			
			// [1] - Create the modules bootstrap config
			ServicesBootstrapConfig bootCfg = ServicesBootstrapConfigBuilder
													.forClient(AA14ClientBootstrapConfigBuilder.buildClientBootstrapConfig())
					 							   	.ofCoreModules(AA14COREServicesBootstrapConfigBuilder.buildCoreBootstrapConfig(xmlProps))
					 							   	.coreEventsHandledAs(ServicesCoreModuleEventsConfig.from(servicesProps))
					 							   	.build();
			// [2] - Setup
			 _setUpBeforeClass(Lists.newArrayList(bootCfg));
		} catch(Exception ex) {
			ex.printStackTrace(System.out);
			Throwables.throwUnchecked(ex);
		}
	}
	@AfterClass
	public static void tearDownAfterClass()  {
		// [99]-Tear things down
		try {
			_tearDownAfterClass();
		} catch(Exception ex) {
			ex.printStackTrace(System.out);
			Throwables.throwUnchecked(ex);
		}
	}
}
