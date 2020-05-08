package aa14.test.mockfactories;

import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14OrganizationalModelObjectRef;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.services.LanguageTextsBuilder;
import r01f.patterns.Factory;
import r01f.util.types.Strings;

@RequiredArgsConstructor
public class AA14MockOrgDivisionServiceFactory 
  implements Factory<AA14OrgDivisionService> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14Organization _organization;
	private final AA14OrgDivision _division;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AA14OrgDivisionService create() {
		return _buildMockService();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private AA14OrgDivisionService _buildMockService() {
		String randomServiceId = _srvIds.getOneUnused();
		
		AA14OrgDivisionServiceOID oid = AA14OrgDivisionServiceOID.supply();
		AA14OrgDivisionService outService = new AA14OrgDivisionService();
		outService.setOid(oid);
		outService.setId(AA14OrgDivisionServiceID.forId(Strings.customized("{}/{}",
																		   _division.getId(),	// organization/division
																		   randomServiceId)));
		outService.setOrgRef(new AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID>(_organization.getOid(),_organization.getId()));
		outService.setOrgDivisionRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionOID,AA14OrgDivisionID>(_division.getOid(),_division.getId()));
		outService.setNameByLanguage(LanguageTextsBuilder.createMapBacked()
										     .withMissingLangTextBehavior(LangTextNotFoundBehabior.RETURN_NULL)
										     .addForLang(Language.SPANISH,"TEST service " + randomServiceId)
										     .addForLang(Language.BASQUE,"[eu] TEST service " + randomServiceId)
										     .build());
		return outService;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14MockEntityIDProvider _srvIds = AA14MockEntityIDProvider.create()	
																				.add("SERVICIO_1")
																				.add("SERVICIO_2")
																				.add("SERVICIO_3")
																				.add("SERVICIO_4")
																				.add("SERVICIO_5")
																				.add("SERVICIO_6")
																				.add("SERVICIO_7")
																				.add("SERVICIO_8")
																				.add("SERVICIO_9")
																				.add("SERVICIO_10")
																				.add("SERVICIO_11")
																				.add("SERVICIO_12")
																				.add("SERVICIO_13")
																				.add("SERVICIO_14")
																				.add("SERVICIO_15")
																				.add("SERVICIO_16")
																				.add("SERVICIO_17")
																				.add("SERVICIO_18")
																						.build();
}
