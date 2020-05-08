package aa14f.api.context;


import com.google.inject.Provider;

/**
 * Mock provider for user contexts
 */
public class AA14MockSecurityContextProvider
  implements Provider<AA14SecurityContext> {
/////////////////////////////////////////////////////////////////////////////////////////
//  Provider
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AA14SecurityContext get() {
		return new AA14SecurityContext();
	}

}
