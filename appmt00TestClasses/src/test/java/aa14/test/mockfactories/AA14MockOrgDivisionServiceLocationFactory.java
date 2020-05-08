package aa14.test.mockfactories;

import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14OrganizationalModelObjectRef;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.services.LanguageTextsBuilder;
import r01f.patterns.Factory;
import r01f.util.types.Strings;

@RequiredArgsConstructor
public class AA14MockOrgDivisionServiceLocationFactory 
  implements Factory<AA14OrgDivisionServiceLocation> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14Organization _organization;
	private final AA14OrgDivision _division;
	private final AA14OrgDivisionService _service;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AA14OrgDivisionServiceLocation create() {
		return _buildMockLocation();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private AA14OrgDivisionServiceLocation _buildMockLocation() {
		String randomLocId = _locIds.getOneUnused();
		
		AA14OrgDivisionServiceLocationOID oid = AA14OrgDivisionServiceLocationOID.supply();
		AA14OrgDivisionServiceLocation outLoc = new AA14OrgDivisionServiceLocation();
		outLoc.setOid(oid);
		outLoc.setId(AA14OrgDivisionServiceLocationID.forId(Strings.customized("{}/{}",
																			   _service.getId(),	// organization/division/service
																			   randomLocId)));
		outLoc.setOrgRef(new AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID>(_organization.getOid(),_organization.getId()));
		outLoc.setOrgDivisionRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionOID,AA14OrgDivisionID>(_division.getOid(),_division.getId()));
		outLoc.setOrgDivisionServiceRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID>(_service.getOid(),_service.getId()));
		outLoc.setNameByLanguage(LanguageTextsBuilder.createMapBacked()
										     .withMissingLangTextBehavior(LangTextNotFoundBehabior.RETURN_NULL)
										     .addForLang(Language.SPANISH,"TEST location " + randomLocId)
										     .addForLang(Language.BASQUE,"[eu] TEST location " + randomLocId)
										     .build());
		return outLoc;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14MockEntityIDProvider _locIds = AA14MockEntityIDProvider.create()	
																				.add("BILBAO")
																				.add("DONOSTIA")
																				.add("VITORIA")
																				.add("AJO")
																				.add("SANTANDER")
																				.add("SANTOÑA")
																				.add("GORLIZ")
																				.add("MEÑAKA")
																				.add("PAMPLONA")
																				.add("MADRID")
																				.add("VALENCIA")
																				.add("CULLERA")
																				.add("LOGROÑO")
																				.add("LEGAZPI")
																				.add("AULESTI")
																				.add("SESTAO")
																				.add("PALENCIA")
																				.add("BURGOS")
																						.build();
}
