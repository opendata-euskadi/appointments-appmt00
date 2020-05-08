package aa14.test.persistence;

import aa14.test.mockfactories.AA14MockScheduleFactory;
import aa14f.client.api.AA14ClientAPI;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import r01f.locale.Language;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.services.LanguageTextsBuilder;
import r01f.patterns.CommandOn;

public class AA14SchedulePersistenceTest 
	 extends AA14TPersistableObjectTestBase<AA14ScheduleOID,AA14ScheduleID,AA14Schedule> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14SchedulePersistenceTest(final AA14ClientAPI api) {
		super(AA14Schedule.class,new AA14MockScheduleFactory(),
			  api.schedulesAPI().getForCRUD(),api.schedulesAPI().getForFind(),
			  0l);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected CommandOn<AA14Schedule> _modelObjectStateUpdateCommand() {
		return new CommandOn<AA14Schedule>() {									
						@Override
						public void executeOn(final AA14Schedule entityToBeUpdated) {
							entityToBeUpdated.setNameByLanguage(LanguageTextsBuilder.createMapBacked()
																		.withMissingLangTextBehavior(LangTextNotFoundBehabior.RETURN_NULL)
																		.addForLang(Language.SPANISH,"A changed name")
																		.addForLang(Language.BASQUE,"[eu] A changed name")
																		.build());
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void testOtherCRUDMethods() {
		// nothing
	}
	@Override
	public void testOtherFindMethods() {
		// nothing
	}
	@Override
	public void testOtherMethods() {
		// nothing
	}
}
