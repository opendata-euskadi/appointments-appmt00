package aa14.test.mockfactories;

import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14OrganizationalModelObjectRef;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.services.LanguageTextsBuilder;
import r01f.patterns.Factory;
import r01f.util.types.Strings;

@RequiredArgsConstructor
public class AA14MockOrgDivisionFactory 
  implements Factory<AA14OrgDivision> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14Organization _organization;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
		@Override
		public AA14OrgDivision create() {
			return _buildMockOrgDivision();
		} 
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private AA14OrgDivision _buildMockOrgDivision() {
		String randomDiv = _divIds.getOneUnused();
		
		AA14OrgDivisionOID oid = AA14OrgDivisionOID.supply();
		AA14OrgDivision outDiv = new AA14OrgDivision();
		outDiv.setOid(oid);
		outDiv.setId(AA14OrgDivisionID.forId(Strings.customized("{}/{}",
															    _organization.getId(),
															    randomDiv)));
		outDiv.setOrgRef(new AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID>(_organization.getOid(),_organization.getId()));
		outDiv.setNameByLanguage(LanguageTextsBuilder.createMapBacked()
										   .withMissingLangTextBehavior(LangTextNotFoundBehabior.RETURN_NULL)
										   .addForLang(Language.SPANISH,"TEST division " + randomDiv)
										   .addForLang(Language.BASQUE,"[eu] TEST division " + randomDiv)
										   .build());
		return outDiv;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14MockEntityIDProvider _divIds = AA14MockEntityIDProvider.create()	
																				.add("DIVISION_1")
																				.add("DIVISION_2")
																				.add("DIVISION_3")
																				.add("DIVISION_4")
																				.add("DIVISION_5")
																				.add("DIVISION_6")
																				.add("DIVISION_7")
																				.add("DIVISION_8")
																				.add("DIVISION_9")
																				.add("DIVISION_10")
																				.add("DIVISION_11")
																				.add("DIVISION_12")
																				.add("DIVISION_13")
																				.add("DIVISION_14")
																				.add("DIVISION_15")
																				.add("DIVISION_16")
																				.add("DIVISION_17")
																				.add("DIVISION_18")
																				.build();
}
