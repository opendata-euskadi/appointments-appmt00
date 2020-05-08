package aa14.test.mockfactories;

import java.util.Collection;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.util.types.collections.CollectionUtils;

/**
 * Provides MOCK entity ids for the TESTS
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
@Accessors(prefix="_")
public class AA14MockEntityIDProvider {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Random number generator
	 */
	private static final Random RAND = new Random(System.currentTimeMillis());
	
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Map<String,Boolean> _ids;
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) private Collection<Map.Entry<String,Boolean>> _idEntries;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  PROVIDE
/////////////////////////////////////////////////////////////////////////////////////////
	public String getOneUnused() {
		if (CollectionUtils.isNullOrEmpty(_ids)) throw new IllegalStateException("The ids provider has not been initialized!!!");
		
		// Try a max of 100 times to pick an unused element 
		int tryTimes = 0;
		String outId = null;
		do {
			int index = RAND.nextInt(_ids.size());
			Map.Entry<String,Boolean> entry = CollectionUtils.of(_idEntries)
													    	 .pickElementAt(index);
			if (entry.getValue() == false) {
				outId = entry.getKey();
				_ids.put(outId,true);	// now it's used!
				break;
			} 
			tryTimes++;
		} while(tryTimes < 100);
		if (outId == null) throw new IllegalStateException("The ids provider has run out of ids!!");
		return outId;
	}
	public void reset() {
		for(Map.Entry<String,Boolean> me : _ids.entrySet()) {
			me.setValue(false);	
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER & FLUENT-API
/////////////////////////////////////////////////////////////////////////////////////////
	public static X478BMockEntityIDProviderAddStep create() {
		return new X478BMockEntityIDProviderAddStep(new AA14MockEntityIDProvider());
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class X478BMockEntityIDProviderAddStep {
		private final AA14MockEntityIDProvider _provider;
		
		public X478BMockEntityIDProviderAddStep add(final String id) {
			if (_provider.getIds() == null) {
				Map<String,Boolean> ids = Maps.newHashMap();
				_provider.setIds(ids);
			}
			_provider.getIds().put(id,Boolean.FALSE);
			return this;
		}
		public AA14MockEntityIDProvider build() {
			_provider.setIdEntries(CollectionUtils.of(_provider.getIds().entrySet()));
			return _provider;
		}
	}
}
