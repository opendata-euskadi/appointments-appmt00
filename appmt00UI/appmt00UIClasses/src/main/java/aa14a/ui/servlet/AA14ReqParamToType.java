package aa14a.ui.servlet;

import com.google.common.base.Function;

import r01f.reflection.ReflectionUtils;

public class AA14ReqParamToType {
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////	
	public static <T> Function<CharSequence,T> transform(final Class<T> type) {
		return new Function<CharSequence,T>() {
						@Override
						public T apply(final CharSequence str) {
							return ReflectionUtils.<T>createInstanceFromString(type,
	 																		   str.toString().trim());
						}
			
			   };
	}
}