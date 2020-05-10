package aa14f.model.config;

import aa14f.model.AA14ModelObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.locale.HasLanguageTexts;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.LanguageTextsMapBacked;
import r01f.locale.LanguageTextsWrapper;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="notificationMessageData")
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class AA14NotificationMessageData 
  implements AA14ModelObject {

	private static final long serialVersionUID = -4427287602617366183L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="key",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _key;
	
	@MarshallField(as="byLanguage")
	@Getter @Setter private LanguageTexts _dataByLanguage;
	
	@Getter private final transient LanguageTextsWrapper<AA14NotificationMessageData> _data = LanguageTextsWrapper.at(this)
																												  .wrap(new HasLanguageTexts() {
																															@Override
																															public void set(final LanguageTexts langText) {
																																_dataByLanguage = langText;
																															}
																															@Override
																															public LanguageTexts get() {
																																return _dataByLanguage;
																															}
																														});
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static AA14NotificationMessageData create(final String key,
													 final LanguageTexts dataByLang) {
		return new AA14NotificationMessageData(key,
											   dataByLang);
	}
	public static AA14NotificationMessageData create(final String key,
													 final String dataES,final String dataEU) {
		return new AA14NotificationMessageData(key,
											   new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
											   			.add(Language.SPANISH,dataES)
											   			.add(Language.BASQUE,dataEU));
	}
}

