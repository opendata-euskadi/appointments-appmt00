package aa14.test.mockfactories;

import aa14f.model.config.AA14Schedule;
import aa14f.model.config.AA14ScheduleBookingConfig;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.services.LanguageTextsBuilder;
import r01f.patterns.Factory;
import r01f.util.types.Strings;

@RequiredArgsConstructor
public class AA14MockScheduleFactory 
  implements Factory<AA14Schedule> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AA14Schedule create() {
		return _buildMockSchedule();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private AA14Schedule _buildMockSchedule() {
		String randomSchId = _schIds.getOneUnused();
		
		AA14ScheduleOID oid = AA14ScheduleOID.supply();
		AA14ScheduleID id = AA14ScheduleID.forId(Strings.customized("{}_id",
											      					oid));
		AA14Schedule outSch = new AA14Schedule();
		outSch.setOid(oid);
		outSch.setId(id);
		outSch.setNameByLanguage(LanguageTextsBuilder.createMapBacked()
										     .withMissingLangTextBehavior(LangTextNotFoundBehabior.RETURN_NULL)
										     .addForLang(Language.SPANISH,"TEST schedule " + randomSchId)
										     .addForLang(Language.BASQUE,"[eu] TEST schedule " + randomSchId)
										     .build());
		outSch.setBookingConfig(AA14ScheduleBookingConfig.DEF_BOOKING_CONFIG);
		return outSch;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14MockEntityIDProvider _schIds = AA14MockEntityIDProvider.create()	
																				.add("SCH1")
																				.add("SCH2")
																				.add("SCH3")
																				.add("SCH4")
																				.add("SCH5")
																				.add("SCH6")
																				.add("SCH7")
																				.add("SCH8")
																				.add("SCH9")
																				.add("SCH10")
																				.add("SCH11")
																				.add("SCH12")
																				.add("SCH13")
																				.add("SCH14")
																				.add("SCH15")
																				.add("SCH16")
																				.add("SCH17")
																				.add("SCH18")
																						.build();
}
