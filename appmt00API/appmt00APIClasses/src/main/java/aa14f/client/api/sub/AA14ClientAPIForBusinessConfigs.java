package aa14f.client.api.sub;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;

import com.google.inject.Provider;

import aa14f.api.cache.AA14BusinessConfigCache;
import aa14f.api.cache.AA14CacheEntry;
import aa14f.api.interfaces.AA14BusinessConfigServices;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.AA14ScheduleBookingConfig;
import aa14f.model.config.business.A14BusinessConfigWrappedBase;
import aa14f.model.config.business.AA14BusinessConfig;
import aa14f.model.config.business.AA14BusinessConfigs;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.summaries.AA14SummarizedOrgHierarchy;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.objectstreamer.Marshaller;
import r01f.patterns.FactoryFrom;
import r01f.reflection.ReflectionUtils;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.api.delegates.ClientAPIServiceDelegateBase;

/**
 * Client implementation of [person locator] services
 */
@Slf4j
@Accessors(prefix="_")
public class AA14ClientAPIForBusinessConfigs
     extends ClientAPIServiceDelegateBase<AA14BusinessConfigServices> {		
/////////////////////////////////////////////////////////////////////////////////////////
//	CACHE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A cache for the config
	 */
	private final AA14BusinessConfigCache _configCache;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIForBusinessConfigs(final Provider<SecurityContext> securityContextProvider,
								    	   final Marshaller modelObjectsMarshaller,
								    	   final AA14BusinessConfigServices businessConfigServices,
								    	   final AA14BusinessConfigCache configCache) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  businessConfigServices);
		// Config cache
		_configCache = configCache;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Load all the business configs
	 * @param securityContext
	 * @return
	 */
	public AA14BusinessConfigs getConfig() {
		try {
			AA14CacheEntry cachedEntry = _configCache.get();		// use the cache
			log.debug("[config]: use a config cached with time-stamp={}",cachedEntry.getCachedAt());
			return cachedEntry.getConfig();
		} catch (Throwable th) {
			log.error("[config]: Error loading config cache: {}",th.getMessage(),th);
			return null;
		}
	}
	/**
	 * Load the business config for the given id
	 * @param securityContext
	 * @param businessId
	 * @return
	 */
	public AA14BusinessConfig getConfigFor(final AA14BusinessID businessId) {
		return this.getConfig()
				   .getFor(businessId);
	}
	public <C extends A14BusinessConfigWrappedBase> C getConfigFor(final AA14BusinessID businessId,
														 		   final FactoryFrom<AA14BusinessConfig,C> wrapperFactory) {
		// get the config
		AA14BusinessConfig businessConfig = this.getConfigFor(businessId);
		
		// wrap
		return wrapperFactory.from(businessConfig);
	}
	/**
	 * Loads the business config typed for the given id
	 * @param <C>
	 * @param businessId
	 * @param businessType
	 * @return
	 */
	public <C extends A14BusinessConfigWrappedBase> C getConfigFor(final AA14BusinessID businessId,
														 		   final Class<C> businessType) {
		return this.getConfigFor(businessId,
								 new FactoryFrom<AA14BusinessConfig,C>() {
										@Override @SneakyThrows @SuppressWarnings("unchecked")
										public C from(final AA14BusinessConfig businessConfig) {
											Method wrapStaticMethod = ReflectionUtils.staticMethod(businessType,
																							  	   "wrap",
																							  	   new Class<?>[] {AA14BusinessConfig.class});
											return (C)wrapStaticMethod.invoke(null,	// it's an static method
																			  businessConfig);
										}
								 });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BUSINESS
/////////////////////////////////////////////////////////////////////////////////////////	
//	public AA14BusinessConfigForTrafikoa getForTrafikoa() {
//		return _wrapConfig(AA14BusinessID.TRAFIKOA,
//						   new FactoryFrom<AA14BusinessConfig,AA14BusinessConfigForTrafikoa>() {
//								@Override
//								public AA14BusinessConfigForTrafikoa from(final AA14BusinessConfig cfg) {
//									return AA14BusinessConfigForTrafikoa.wrap(cfg);
//								}
//						   });
//	}
//	public AA14BusinessConfigForBizilagun getForBizilagun() {
//		return _wrapConfig(AA14BusinessID.BIZILAGUN,
//						   new FactoryFrom<AA14BusinessConfig,AA14BusinessConfigForBizilagun>() {
//								@Override
//								public AA14BusinessConfigForBizilagun from(final AA14BusinessConfig cfg) {
//									return AA14BusinessConfigForBizilagun.wrap(cfg);
//								}
//						   });
//	}
//	public AA14BusinessConfigForBloodDonation getForBloodDonation() {
//		return _wrapConfig(AA14BusinessID.BLOOD_DONATION,
//						   new FactoryFrom<AA14BusinessConfig,AA14BusinessConfigForBloodDonation>() {
//								@Override
//								public AA14BusinessConfigForBloodDonation from(final AA14BusinessConfig cfg) {
//									return AA14BusinessConfigForBloodDonation.wrap(cfg);
//								}
//						   });
//	}
//	public AA14BusinessConfigForMedicalService getForMedicalService() {
//		return _wrapConfig(AA14BusinessID.MEDICAL_SERVICE,
//						   new FactoryFrom<AA14BusinessConfig,AA14BusinessConfigForMedicalService>() {
//								@Override
//								public AA14BusinessConfigForMedicalService from(final AA14BusinessConfig cfg) {
//									return AA14BusinessConfigForMedicalService.wrap(cfg);
//								}
//						   });
//	}
//	public AA14BusinessConfigForZuzenean getForZuzenean() {
//		return _wrapConfig(AA14BusinessID.ZUZENEAN,
//						   new FactoryFrom<AA14BusinessConfig,AA14BusinessConfigForZuzenean>() {
//								@Override
//								public AA14BusinessConfigForZuzenean from(final AA14BusinessConfig cfg) {
//									return AA14BusinessConfigForZuzenean.wrap(cfg);
//								}
//						   });
//	}
//	public AA14BusinessConfigForJustizia getForJustizia() {
//		return _wrapConfig(AA14BusinessID.JUSTIZIA,
//						   new FactoryFrom<AA14BusinessConfig,AA14BusinessConfigForJustizia>() {
//								@Override
//								public AA14BusinessConfigForJustizia from(final AA14BusinessConfig cfg) {
//									return AA14BusinessConfigForJustizia.wrap(cfg);
//								}
//						   });
//	}
//	private <W extends A14BusinessConfigWrappedBase> W _wrapConfig(final AA14BusinessID businessId,
//															       final FactoryFrom<AA14BusinessConfig,W> wrapperFactory) {
//		return wrapperFactory.from(this.getConfig()
//				   					   .getFor(businessId));
//	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LOCATION ACCESS
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14OrgDivisionServiceLocation getLocationFor(final AA14OrgDivisionServiceLocationOID oid) {
		return this.getConfig()
				   .getLocationFor(oid);
	}
	public AA14OrgDivisionServiceLocation getLocationFor(final AA14OrgDivisionServiceLocationID id) {
		return this.getConfig()
				   .getLocationFor(id);
	}
	public Collection<AA14OrgDivisionServiceLocation> getAllLocations() {
		return this.getConfig()
				   .getAllLocations();
	}
	/**
	 * Returns the locations for a given schedule
	 * @param schId
	 * @return
	 */
	public Collection<AA14OrgDivisionServiceLocationOID> getLocationsOidsFor(final AA14ScheduleID schId) {
		return this.getConfig()
				   .getLocationsOidsFor(schId);
	}
	/**
	 * Returns the locations for a given schedule
	 * @param schId
	 * @return
	 */
	public Collection<AA14OrgDivisionServiceLocationID> getLocationsIdsFor(final AA14ScheduleID schId) {
		return this.getConfig()
				   .getLocationsIdsFor(schId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ORG HIERARCH ACCESS
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14SummarizedOrgHierarchy getOrgHierarchyFor(final AA14OrgDivisionServiceLocationOID locOid,
														 final Language lang) {
		return this.getConfig()
				   .getSummarizedOrgHierarchyFor(locOid,lang);
	}
	public AA14SummarizedOrgHierarchy getOrgHierarchyFor(final AA14OrgDivisionServiceLocationID locId,
														 final Language lang) {
		return this.getConfig()
				   .getSummarizedOrgHierarchyFor(locId,lang);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SCHEDULE ACCESS
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14Schedule getScheduleFor(final AA14ScheduleOID schOid) {
		return this.getConfig()
				   .getScheduleFor(schOid);
	}
	public AA14Schedule getScheduleFor(final AA14ScheduleID schId) {
		return this.getConfig()
				   .getScheduleFor(schId);
	}
	public AA14ScheduleBookingConfig getScheduleBookingConfigFor(final AA14ScheduleOID schOid) {
		AA14Schedule sch = this.getScheduleFor(schOid);
		return sch.getBookingConfig();
	}
	public AA14ScheduleBookingConfig getScheduleBookingConfigFor(final AA14ScheduleID schId) {
		AA14Schedule sch = this.getScheduleFor(schId);
		return sch.getBookingConfig();
	}
	/**
	 * Loads the booking config for a certain location
	 *	   BEWARE!	The calendar / booking config is associated with the SCHEDULE, not with the LOCATION or SERVICE,
	 *				any location can be associated with multiple schedules BUT all of them MUST have
	 *				the same booking config
	 * @param locOid locationOid
	 * @return the booking config or null
	 */
	public AA14ScheduleBookingConfig getScheduleBookingConfigFor(final AA14OrgDivisionServiceLocationOID locOid) {
		return this.getConfig()
				   .getScheduleBookingConfigFor(locOid);
	}
	/**
	 * Loads the booking config for a certain location
	 *	   BEWARE!	The calendar / booking config is associated with the SCHEDULE, not with the LOCATION or SERVICE,
	 *				any location can be associated with multiple schedules BUT all of them MUST have
	 *				the same booking config
	 * @param locOid locationOid
	 * @return the booking config or null
	 */
	public AA14ScheduleBookingConfig getScheduleBookingConfigFor(final AA14OrgDivisionServiceLocationID locId) {
		return this.getConfig()
				   .getScheduleBookingConfigFor(locId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Load all the business configs
	 * @param securityContext
	 * @return
	 */
	public AA14BusinessConfigs loadConfig() {
		return this.getServiceProxyAs(AA14BusinessConfigServices.class)
						.loadConfig(this.getSecurityContext())
						.getOrThrow();
	}
	/**
	 * Forces the config cache refresh 
	 * @return
	 */
	public void forceReloadConfig() {
		_configCache.forceReloadConfig();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LAST UPDATE DATE
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the last update date
	 * @param securityContext
	 * @return
	 */
	public Date getLastUpdateDate() {
		return this.getServiceProxyAs(AA14BusinessConfigServices.class)
						.getLastUpdateDate(this.getSecurityContext())
						.getOrThrow();
	}
	/**
	 * Updates the last update date
	 * @param securityContext
	 * @param date
	 * @return
	 */
	public Date updateLastUpdateDate(final Date date) {
		return this.getServiceProxyAs(AA14BusinessConfigServices.class)
						.updateLastUpdateDate(this.getSecurityContext(),
											  date)
						.getOrThrow();
	}
}
