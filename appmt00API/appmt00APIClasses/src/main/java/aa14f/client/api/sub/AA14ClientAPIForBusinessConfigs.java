package aa14f.client.api.sub;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.inject.Provider;

import aa14f.api.context.AA14SecurityContext;
import aa14f.api.interfaces.AA14BusinessConfigServices;
import aa14f.common.internal.AA14AppCodes;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.AA14ScheduleBookingConfig;
import aa14f.model.config.business.AA14BusinessConfig;
import aa14f.model.config.business.AA14BusinessConfigForBizilagun;
import aa14f.model.config.business.AA14BusinessConfigForBloodDonation;
import aa14f.model.config.business.AA14BusinessConfigForJustizia;
import aa14f.model.config.business.AA14BusinessConfigForMedicalService;
import aa14f.model.config.business.AA14BusinessConfigForTrafikoa;
import aa14f.model.config.business.AA14BusinessConfigForZuzenean;
import aa14f.model.config.business.AA14BusinessConfigs;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.summaries.AA14SummarizedOrgHierarchy;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.objectstreamer.Marshaller;
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
	private final LoadingCache<String,AA14CacheEntry> _configCache;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIForBusinessConfigs(final Provider<SecurityContext> securityContextProvider,
								    	   final Marshaller modelObjectsMarshaller,
								    	   final AA14BusinessConfigServices businessConfigServices) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  businessConfigServices);
		
		// Config cache
		// create the cache
		// see: https://github.com/google/guava/wiki/CachesExplained
		RemovalListener<String,AA14CacheEntry> cacheRemovalListener = new RemovalListener<String,AA14CacheEntry>() {
																			@Override
																			public void onRemoval(final RemovalNotification<String,AA14CacheEntry> notif) {
																		    	log.warn("[config] > remove config cached at {} because {}",
																		    			 notif.getValue().getCachedAt(),notif.getCause());
																			}
																	  };
		_configCache = CacheBuilder.newBuilder()
							.maximumSize(10)							
							//	.expireAfterWrite(60,TimeUnit.MINUTES)	// for 60 minutes		BEWARE!!! do NOT use both expireAfterWrite and refreshAfterWrite
							.refreshAfterWrite(10,TimeUnit.SECONDS)		// refresh after 2 minutes
							.removalListener(cacheRemovalListener)
							.build(new CacheLoader<String,AA14CacheEntry>() {
											@Override
											public AA14CacheEntry load(final String key) { // no checked exception
												return _loadConfig();
											}
											@Override
											public ListenableFuture<AA14CacheEntry> reload(final String key,
																						   final AA14CacheEntry prevConfig) {
												// [1] - Get the time when the [config] was last updated 
												Date lastUpdateDate = AA14ClientAPIForBusinessConfigs.this.getLastUpdateDate(new AA14SecurityContext(AA14AppCodes.CORE_APPCODE));
												
												log.warn("[config]: reload / refresh config cache (stored config time-stamp={} actual config time-stamp={}...................................",
														 prevConfig.getLastUpdatedAt(),lastUpdateDate);
												
												// [2] - Return a future
												if (prevConfig.needsReloadIfLastUpdatedAt(lastUpdateDate)) {
													log.warn("[config] > Reload cached config because cached config time-stamp={} and config was updated at {}",
															 prevConfig.getLastUpdatedAt(),lastUpdateDate);
													ListenableFutureTask<AA14CacheEntry> task = ListenableFutureTask.create(new Callable<AA14CacheEntry>() {
																										@Override
																										public AA14CacheEntry call() throws Exception {
																											return _loadConfig();
																										}
																								});													
													// load in a new thread > ASYNC
													// (the data will be available next time the config is required)
													Executors.newSingleThreadExecutor()
															 .execute(task);
													return task;
												} 
												// return a "fake" future
												log.warn("[config]: cached config does NOT needs to be reloaded");
												return Futures.immediateFuture(prevConfig);
											}
											private AA14CacheEntry _loadConfig() {
												log.warn("*******************************************************************************************");
												log.warn("CONFIG: start load config");
												log.warn("*******************************************************************************************");
												SecurityContext securityContext = securityContextProvider.get();
												
												// [1] - Load the config
												AA14BusinessConfigs config = AA14ClientAPIForBusinessConfigs.this.loadConfig();
												
												// [2] - Load the last time the config was updated
												Date lastUpdateDate = AA14ClientAPIForBusinessConfigs.this.getLastUpdateDate(securityContext);
												
												log.warn("*******************************************************************************************");
												log.warn("CONFIG: end load config (last update date={})",lastUpdateDate);
												log.warn("*******************************************************************************************");

												return new AA14CacheEntry(config,
																		  lastUpdateDate);	// the last time the [structure outline] was updated
											}
								   });
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
			AA14CacheEntry cachedEntry = _configCache.get("config");		// use the cache
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
/////////////////////////////////////////////////////////////////////////////////////////
//	BUSINESS
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14BusinessConfigForTrafikoa getForTrafikoa() {
		return this.getConfig()
				   .getForTrafikoa();
	}
	public AA14BusinessConfigForBizilagun getForBizilagun() {
		return this.getConfig()
				   .getForBizilagun();
	}
	public AA14BusinessConfigForBloodDonation getForBloodDonation() {
		return this.getConfig()
				   .getForBloodDonation();
	}
	public AA14BusinessConfigForMedicalService getForMedicalService() {
		return this.getConfig()
				   .getForMedicalService();
	}
	public AA14BusinessConfigForZuzenean getForZuzenean() {
		return this.getConfig()
				   .getForZuzenean();
	}
	public AA14BusinessConfigForJustizia getForJustizia() {
		return this.getConfig()
				   .getForJustizia();
	}
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
		_configCache.refresh("config");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LAST UPDATE DATE
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the last update date
	 * @param securityContext
	 * @return
	 */
	public Date getLastUpdateDate(final SecurityContext securityContext) {
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
	public Date updateLastUpdateDate(final SecurityContext securityContext,
									 final Date date) {
		return this.getServiceProxyAs(AA14BusinessConfigServices.class)
						.updateLastUpdateDate(this.getSecurityContext(),
											  date)
						.getOrThrow();
	}
}
