package aa14a.bootstrap.ui;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

import aa14a.ui.portal.AA14AppEmbedServletFilter;
import aa14a.ui.servlet.AA14CalendarPrintServlet;
import aa14a.ui.servlet.AA14CalendarServlet;
import aa14a.ui.servlet.AA14ControllerServlet;
import aa14a.ui.servlet.AA14ModelObjectsLoadServlet;
import aa14f.common.internal.AA14AppCodes;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenServletExposed;
import r01f.bootstrap.services.core.ServletImplementedServicesCoreBootstrapGuiceModuleBase;
import r01f.ejie.xlnets.servlet.XLNetsAuthServletFilter;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.xmlproperties.XMLProperties;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import r01f.xmlproperties.annotations.XMLPropertiesComponent;

@Slf4j
public class AA14ServletServicesBootstrapGuiceModule
     extends ServletImplementedServicesCoreBootstrapGuiceModuleBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ServletServicesBootstrapGuiceModule(final ServicesCoreBootstrapConfigWhenServletExposed coreBootstrapCfg) {
		super(coreBootstrapCfg);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BIND
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(final Binder binder) {
		log.warn("START____________ AA14 ServiceRequests REST Bootstraping _____________________________");

		final boolean enableXLNetsFilter = true;
//		final boolean enableXLNetsFilter = _servicesCoreProps.propertyAt("services/xlNetsServletFilterEnabled")
//													   		 .asBoolean(true);
//		log.warn("XLNetsServletFilter is {}",enableXLNetsFilter ? "enabled" : "disabled");

		
		// the servlet module is a kind of replacement for web.xml file
		binder.install(new ServletModule() {
								@Override
								protected void configureServlets() {
									// Security filter
									// Bind the filter as singleton (guice requires it)
									this.bind(XLNetsAuthServletFilter.class)
										.in(Singleton.class);
									if (enableXLNetsFilter) {
										this.filterRegex(".*")
											.through(XLNetsAuthServletFilter.class);	// uses the xlnets properties provided below (see _provideXLNetsProperties)
									}
									
									// Portal page embed filter
									// Bind the filter as singleton (guice requires it)
									this.bind(AA14AppEmbedServletFilter.class)
										.in(Singleton.class);
									this.filterRegex(".*")
										.through(AA14AppEmbedServletFilter.class);
									
									// controller
									this.serve("/" + AA14ControllerServlet.class.getSimpleName())
										.with(AA14ControllerServlet.class);
									// calendar
									this.serve("/" + AA14CalendarServlet.class.getSimpleName())
										.with(AA14CalendarServlet.class);
									this.serve("/" + AA14CalendarPrintServlet.class.getSimpleName())
										.with(AA14CalendarPrintServlet.class);
									// view
									this.serve("/" + AA14ModelObjectsLoadServlet.class.getSimpleName())
										.with(AA14ModelObjectsLoadServlet.class);
							}
					   });
		// json mapper used at the calendar
		binder.bind(ObjectMapper.class)
			  .toInstance(new ObjectMapper());
		
		log.warn("END_______________ AA14: ServiceRequests REST Bootstraping _____________________________");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Alternative to using a provider
     * binder.bind(XMLPropertiesForAppComponent.class)
     * 	  .annotatedWith(new XMLPropertiesComponent() {		// see [Binding annotations with attributes] at https://github.com/google/guice/wiki/BindingAnnotations
     * 								@Override
     * 								public Class<? extends Annotation> annotationType() {
     * 									return XMLPropertiesComponent.class;
     * 								}
     * 								@Override
     * 								public String value() {
     * 									return "xlnets";
     * 								}
     * 	  				 })
     * 	  .toProvider(new Provider<XMLPropertiesForAppComponent>() {
     * 						@Override
     * 						public XMLPropertiesForAppComponent get() {
     * 							return XXXServicesBootstrapGuiceModule.this.servicesProperties();
     * 						}
     * 	  			  });
	 */
	
	@Provides @XMLPropertiesComponent("xlNets") @Singleton
	@SuppressWarnings("static-method")
	private XMLPropertiesForAppComponent _provideXLNetsProperties(final XMLProperties props) {
		XMLPropertiesForAppComponent outPropsForComponent = new XMLPropertiesForAppComponent(props.forApp(AA14AppCodes.CORE_APPCODE),	// BEWARE!!! aa14b!!!
																							 AppComponent.forId("xlnets"));
		return outPropsForComponent;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
// 	@Provides @Named("i18n") @Singleton
// 	public I18NService provideI18NBundle(final XMLProperties props) {
// 		// The XMLProperties is injected to the provider
// 		return I18NServiceBuilder.createUsing(ResourceBundleControlBuilder.defaultResourceBundleControl())
// 								 .forBundleChain("aa14a")			// see resources at /resources/aa14a_{lang}.properties
// 								 .usingThreadContextClassLoader()	// BEWARE!!! WTF 
// 								 .withMissingKeyBehaviour(ResourceBundleMissingKeyBehaviour.RETURN_KEY);
// 	}
}
