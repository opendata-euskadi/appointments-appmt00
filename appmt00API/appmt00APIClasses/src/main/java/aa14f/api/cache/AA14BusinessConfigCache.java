package aa14f.api.cache;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
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
import aa14f.model.config.business.AA14BusinessConfigs;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import r01f.securitycontext.SecurityContext;

@Slf4j
public class AA14BusinessConfigCache {
/////////////////////////////////////////////////////////////////////////////////////////
//	CACHE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A cache for the config
	 */
	private final LoadingCache<String,AA14CacheEntry> _configCache;
	/**
	 * Business config services
	 */
	private final AA14BusinessConfigServices _businessConfigServices;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14BusinessConfigCache(final Provider<SecurityContext> securityContextProvider,
								   final AA14BusinessConfigServices businessConfigServices) {
		_businessConfigServices = businessConfigServices;
		
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
							.refreshAfterWrite(2,TimeUnit.MINUTES)		// refresh after 2 minutes
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
												Date lastUpdateDate = AA14BusinessConfigCache.this.getLastUpdateDate(new AA14SecurityContext(AA14AppCodes.CORE_APPCODE));
												
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
												AA14BusinessConfigs config = AA14BusinessConfigCache.this.loadConfig(securityContext);
												
												// [2] - Load the last time the config was updated
												Date lastUpdateDate = AA14BusinessConfigCache.this.getLastUpdateDate(securityContext);
												
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
	public AA14CacheEntry get() throws ExecutionException {
		return _configCache.get("config");
	}
	@SneakyThrows
	public AA14BusinessConfigs getBusinessConfig() {
		return this.get()
				   .getConfig();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Load all the business configs
	 * @param securityContext
	 * @return
	 */
	public AA14BusinessConfigs loadConfig(final SecurityContext securityContext) {
		return _businessConfigServices.loadConfig(securityContext)
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
		return _businessConfigServices.getLastUpdateDate(securityContext)
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
		return _businessConfigServices.updateLastUpdateDate(securityContext,
											  				date)
						.getOrThrow();
	}
}
