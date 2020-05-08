package aa14f.bootstrap.client;

import aa14f.api.interfaces.AA14ServiceInterface;
import aa14f.client.api.AA14ClientAPI;
import aa14f.common.internal.AA14AppCodes;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.bootstrap.services.config.client.ServicesClientBootstrapConfig;
import r01f.bootstrap.services.config.client.ServicesClientBootstrapConfigBuilder;
import r01f.patterns.IsBuilder;

/**
 * Builds bootstrap confif
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class AA14ClientBootstrapConfigBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static ServicesClientBootstrapConfig buildClientBootstrapConfig() {
		return ServicesClientBootstrapConfigBuilder.forClientApiAppCode(AA14AppCodes.API_APPCODE)
																  .exposingApi(AA14ClientAPI.class)
																  .ofServiceInterfacesExtending(AA14ServiceInterface.class)
																  .bootstrappedWith(AA14ClientBootstrapGuiceModule.class)
																  .build();
	}
}
