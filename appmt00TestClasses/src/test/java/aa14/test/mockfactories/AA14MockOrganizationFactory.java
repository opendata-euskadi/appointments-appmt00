package aa14.test.mockfactories;

import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import r01f.locale.Language;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.services.LanguageTextsBuilder;
import r01f.patterns.Factory;
import r01f.util.types.Strings;

public class AA14MockOrganizationFactory 
  implements Factory<AA14Organization> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AA14Organization create() {
		return _buildMockOrganization();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ENTITY PROVIDER
/////////////////////////////////////////////////////////////////////////////////////////
	private AA14Organization _buildMockOrganization() {
		String randomOrg = _orgIds.getOneUnused();
		
		AA14OrganizationOID oid = AA14OrganizationOID.supply();
		AA14Organization outOrg = new AA14Organization();
		outOrg.setOid(oid);
		outOrg.setId(AA14OrganizationID.forId(Strings.customized("{}",
																 randomOrg)));
		outOrg.setNameByLanguage(LanguageTextsBuilder.createMapBacked()
										   .withMissingLangTextBehavior(LangTextNotFoundBehabior.RETURN_NULL)
										   .addForLang(Language.SPANISH,"TEST organization " + randomOrg)
										   .addForLang(Language.BASQUE,"[eu] TEST organization " + randomOrg)
										   .build());
		
		return outOrg;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14MockEntityIDProvider _orgIds = AA14MockEntityIDProvider.create()	
																				.add("IBERDROLA")
																				.add("LANBIDE")
																				.add("SPRI")
																				.add("EJIE")
																				.add("IZENPE")
																				.add("IVAP")
																				.add("AVPD")
																				.add("KZGUNEA")
																				.add("LANTIK")
																				.add("IFZE")
																				.add("CIMUBISA")
																				.add("CCASA")
																				.add("BBVA")
																				.add("BANCO_SANTANDER")
																				.add("BANCO_POPULAR")
																				.add("TELEFONICA")
																				.add("HP")
																				.add("IBM")
																				.add("GOOGLE")
																				.add("AMAZON")
																				.add("SAMSUNG")
																				.add("DELL")
																				.add("LENOVO")
																				.build();
}
