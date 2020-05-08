package aa14f.api.context;

import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.UserCode;
import r01f.patterns.IsBuilder;

public class AA14SecurityContextBuilder
  implements IsBuilder {
	
	/**
	 * Creates a {@link UserContext} for a physical user
	 * @param userCode
	 * @return
	 */
	public static AA14SecurityContext createFor(final UserCode userCode) {
		return new AA14SecurityContext(userCode);
	}
	/**
	 * Creates a {@link UserContext} for an app
	 * @param appCode
	 * @return
	 */
	public static AA14SecurityContext createForApp(final AppCode appCode) {
		return new AA14SecurityContext(appCode);
	}
}
