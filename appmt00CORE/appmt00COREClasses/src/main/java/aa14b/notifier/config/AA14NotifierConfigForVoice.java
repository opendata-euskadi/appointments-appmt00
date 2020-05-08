package aa14b.notifier.config;

import aa14f.common.internal.AA14AppCodes;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.core.services.notifier.config.NotifierAppDependentConfigBase;
import r01f.core.services.notifier.config.NotifierEnums.NotifierType;
import r01f.types.url.Url;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

@Accessors(prefix="_")
public class AA14NotifierConfigForVoice 
	 extends NotifierAppDependentConfigBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Url _twmlUrl;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14NotifierConfigForVoice() {
		super(NotifierType.VOICE,
			  AA14AppCodes.CORE_APPCODE);
		_twmlUrl = null;	// TODO review!

	}
	public AA14NotifierConfigForVoice(final XMLPropertiesForAppComponent props) {
		super(NotifierType.VOICE,
			  props.getAppCode());
		_twmlUrl = props.propertyAt("/notifier/notifiers/" + _type.asStringLowerCase() + "/twmlUrl")
					   .asUrl(Url.from("http://demo.twilio.com/docs/voice.xml"));

	}
	public static AA14NotifierConfigForVoice createFrom(final XMLPropertiesForAppComponent orios) {
		return new AA14NotifierConfigForVoice(orios);
	}	
}
