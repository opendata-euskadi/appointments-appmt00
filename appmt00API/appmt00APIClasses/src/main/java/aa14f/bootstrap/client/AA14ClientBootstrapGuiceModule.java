package aa14f.bootstrap.client;

import com.google.inject.Binder;
import com.google.inject.Provides;

import aa14f.api.context.AA14MockSecurityContextProvider;
import aa14f.common.internal.AA14AppCodes;
import lombok.EqualsAndHashCode;
import r01f.bootstrap.services.client.ServicesClientAPIBootstrapGuiceModuleBase;
import r01f.bootstrap.services.config.client.ServicesClientGuiceBootstrapConfig;
import r01f.inject.HasMoreBindings;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;
import r01f.securitycontext.SecurityContext;

/**
 * Client-API bindings
 */
@EqualsAndHashCode(callSuper=true) // This is important for guice modules
public class AA14ClientBootstrapGuiceModule 
  	 extends ServicesClientAPIBootstrapGuiceModuleBase 	// this is a client guice bindings module
  implements HasMoreBindings {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientBootstrapGuiceModule(final ServicesClientGuiceBootstrapConfig servicesClientBootstrapCfg) {
		super(servicesClientBootstrapCfg);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GUICE MODULE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void configureMoreBindings(final Binder binder) {
		_bindModelObjectsMarshaller(binder);
		_bindModelObjectExtensionsModule(binder);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  USER CONTEXT PROVIDERS
/////////////////////////////////////////////////////////////////////////////////////////
	@Provides @SuppressWarnings("static-method")
	SecurityContext provideUserContext() {
		AA14MockSecurityContextProvider provider = new AA14MockSecurityContextProvider();
		return provider.get();
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	COMMON BINDINGS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * bindings for the marshaller
	 */
	private static void _bindModelObjectsMarshaller(final Binder binder) {
		// Create the model objects marshaller
		Marshaller marshaller = MarshallerBuilder.findTypesToMarshallAt(AA14AppCodes.API_APPCODE)
												 .build();
		// Bind this instance to the model object's marshaller
		binder.bind(Marshaller.class).annotatedWith(ModelObjectsMarshaller.class)
									 .toInstance(marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MODEL EXTENSIONS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @param binder 
	 * @return bindings for the model extensions
	 */
	private static void _bindModelObjectExtensionsModule(final Binder binder) {
		// nothing
	}
}
