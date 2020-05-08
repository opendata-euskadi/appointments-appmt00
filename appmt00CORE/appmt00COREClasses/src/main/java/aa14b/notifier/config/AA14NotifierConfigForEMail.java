package aa14b.notifier.config;

import aa14f.common.internal.AA14AppCodes;
import lombok.experimental.Accessors;
import r01f.core.services.notifier.config.NotifierAppDependentConfigBase;
import r01f.core.services.notifier.config.NotifierEnums.NotifierType;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Accessors(prefix="_")
public class AA14NotifierConfigForEMail 
	 extends NotifierAppDependentConfigBase  {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14NotifierConfigForEMail() {
		super(NotifierType.EMAIL,
			  AA14AppCodes.CORE_APPCODE);

	}
	public AA14NotifierConfigForEMail(final XMLPropertiesForAppComponent props) {
		super(NotifierType.EMAIL,
			  props.getAppCode());
	}
	public static AA14NotifierConfigForEMail createFrom(final XMLPropertiesForAppComponent xmlProps) {
		return new AA14NotifierConfigForEMail(xmlProps);
	}	
}
