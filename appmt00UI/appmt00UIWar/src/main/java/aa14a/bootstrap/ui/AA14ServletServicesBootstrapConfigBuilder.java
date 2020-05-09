package aa14a.bootstrap.ui;

import aa14f.common.internal.AA14AppCodes;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigBuilder;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenServletExposed;
import r01f.patterns.IsBuilder;
import r01f.xmlproperties.XMLPropertiesForApp;

/**
 * Builds bootstrap confif
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class AA14ServletServicesBootstrapConfigBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static ServicesCoreBootstrapConfigWhenServletExposed buildCoreBootstrapConfig(final XMLPropertiesForApp xmlProps) {
		return ServicesCoreBootstrapConfigBuilder.forCoreAppAndModule(AA14AppCodes.WEB_FRONT_END_APPCODE,AA14AppCodes.CORE_APPOINTMENTS_MOD)
	   				.servletImplemented()
	   					.bootstrappedBy(AA14ServletServicesBootstrapGuiceModule.class)
	   					.build();
	}
}
