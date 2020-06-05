package aa14a.ui.servlet;

import com.google.common.base.Function;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.reflection.ReflectionUtils;
import r01f.util.types.Strings;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class AA14ReqParamToType {
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////	
	public static <T> Function<CharSequence,T> transform(final Class<T> type) {
		return new Function<CharSequence,T>() {
						@Override
						public T apply(final CharSequence str) {
							String s = str != null ? str.toString().trim() : null;
							return Strings.isNOTNullOrEmpty(s) 
										? ReflectionUtils.<T>createInstanceFromString(type,
	 																		   		  AA14RequestParamsSanitizer.FILTER.filter(s))	// sanitize
									    : null;
						}
			
			   };
	}
}
