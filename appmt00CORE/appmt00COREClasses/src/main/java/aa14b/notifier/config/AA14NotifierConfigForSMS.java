package aa14b.notifier.config;

import aa14f.common.internal.AA14AppCodes;
import lombok.experimental.Accessors;
import r01f.core.services.notifier.config.NotifierAppDependentConfigBase;
import r01f.core.services.notifier.config.NotifierEnums.NotifierType;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Accessors(prefix="_")
public class AA14NotifierConfigForSMS 
	 extends NotifierAppDependentConfigBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14NotifierConfigForSMS() {
		super(NotifierType.SMS,
			  AA14AppCodes.CORE_APPCODE);
	}
	public AA14NotifierConfigForSMS(final XMLPropertiesForAppComponent props) {
		super(NotifierType.SMS,
			  props.getAppCode());
	}
	public static AA14NotifierConfigForSMS createFrom(final XMLPropertiesForAppComponent xmlProps) {
		return new AA14NotifierConfigForSMS(xmlProps);
	}	
}
