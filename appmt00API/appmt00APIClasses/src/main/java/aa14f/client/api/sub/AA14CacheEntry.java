package aa14f.client.api.sub;

import java.io.Serializable;
import java.util.Date;

import aa14f.model.config.business.AA14BusinessConfigs;
import lombok.Getter;
import lombok.experimental.Accessors;

	@Accessors(prefix="_") 
    class AA14CacheEntry
implements Serializable {
	private static final long serialVersionUID = -3077860130764155886L;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter private final AA14BusinessConfigs _config;
	@Getter private final Date _cachedAt;
	@Getter private final Date _lastUpdatedAt;

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14CacheEntry(final AA14BusinessConfigs config,
					  	  final Date lastUpdatedAt) {
		_config = config;
		_cachedAt = new Date();
		_lastUpdatedAt = lastUpdatedAt;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public boolean needsReloadIfLastUpdatedAt(final Date lastUpdatedAt) {
		return _lastUpdatedAt != null ? lastUpdatedAt.after(_lastUpdatedAt)
									  : true;
	}
}