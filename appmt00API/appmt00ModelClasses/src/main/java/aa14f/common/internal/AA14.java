package aa14f.common.internal;

import java.util.UUID;

/**
 * Encapsulates some usually used functions as:
 * <ul>
 * 		<li>Property access</li>
 * 		<li>OIDs generation</li>
 * </ul>
 */
public class AA14 {	
/////////////////////////////////////////////////////////////////////////////////////////
// 	GUID GENERATION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Generates a GUID
	 * @return
	 */
	public static String generateGUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().toUpperCase();
	}
}
