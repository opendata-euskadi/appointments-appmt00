package aa14f.api.interfaces;

import java.util.Date;

import aa14f.model.config.business.AA14BusinessConfigs;
import r01f.model.persistence.PersistenceOperationResult;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ExposedServiceInterface;

@ExposedServiceInterface
public interface AA14BusinessConfigServices
         extends AA14ServiceInterface {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Load all the business configs
	 * @param securityContext
	 * @return
	 */
	public PersistenceOperationResult<AA14BusinessConfigs> loadConfig(final SecurityContext securityContext);
	/**
	 * Returns the last update date
	 * @param securityContext
	 * @return
	 */
	public PersistenceOperationResult<Date> getLastUpdateDate(final SecurityContext securityContext);
	/**
	 * Updates the last update date
	 * @param securityContext
	 * @param date
	 * @return
	 */
	public PersistenceOperationResult<Date> updateLastUpdateDate(final SecurityContext securityContext,
																 final Date date);

}
