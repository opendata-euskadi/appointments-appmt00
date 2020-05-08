package aa14f.common.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.AppCode;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsMapBacked;
import r01f.services.ids.ServiceIDs.ClientApiAppCode;
import r01f.services.ids.ServiceIDs.CoreAppCode;
import r01f.services.ids.ServiceIDs.CoreModule;

public abstract class AA14AppCodes {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String API_APPCODE_STR = "aa14f";
	public static final String CORE_APPCODE_STR = "aa14b";
	public static final String WEB_FRONT_END_APPCODE_STR = "aa14a";	
	public static final String APPOINTMENTS_MODULE_STR = "appointments";	
	
	public static final ClientApiAppCode API_APPCODE = ClientApiAppCode.forId(AA14AppCodes.API_APPCODE_STR);
	
	public static final CoreAppCode CORE_APPCODE = CoreAppCode.forId(AA14AppCodes.CORE_APPCODE_STR);
	public static final CoreAppCode WEB_FRONT_END_APPCODE = CoreAppCode.forId(AA14AppCodes.WEB_FRONT_END_APPCODE_STR);
	
	public static final CoreModule CORE_APPOINTMENTS_MOD = CoreModule.forId(APPOINTMENTS_MODULE_STR);
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public enum AA14AppCode {	
		FRAMEWORK						(AppCode.forId("r01f"),				new LanguageTextsMapBacked().addForAll("Framework")),
		API								(API_APPCODE.asAppCode(),			new LanguageTextsMapBacked().addForAll("Appointments API")),
		CORE		  					(CORE_APPCODE.asAppCode(),			new LanguageTextsMapBacked().addForAll("Appointments core")),
		FRONT_END	  					(WEB_FRONT_END_APPCODE.asAppCode(),	new LanguageTextsMapBacked().addForAll("Appointments web front-end"));
	
																	
				private final AppCode _code;
		@Getter private final LanguageTexts _appNames;
		
		public AppCode code() {
			return _code;
		}
	}
}
