package aa14.test.persistence;

import java.util.Collection;

import org.junit.Assert;

import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForOrganizationalEntityCRUDServicesBase;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForOrganizationalEntityFindServicesBase;
import aa14f.model.config.AA14OrganizationalModelObject;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.locale.Language;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.services.LanguageTextsBuilder;
import r01f.patterns.CommandOn;
import r01f.patterns.Factory;
import r01f.util.types.collections.CollectionUtils;

/**
 * JVM arguments:
 * -javaagent:D:/tools_workspaces/eclipse/local_libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true
 */
abstract class AA14OrganizationalObjectPersistenceTestBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,
								  						 	  	   M extends AA14OrganizationalModelObject<O,ID>> 
	   extends AA14TPersistableObjectTestBase<O,ID,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	protected AA14OrganizationalObjectPersistenceTestBase(final Class<M> entityType,final Factory<M> mockObjsFactory,
								 			   		    	 	  final AA14ClientAPIDelegateForOrganizationalEntityCRUDServicesBase<O,ID,M> crudApi,final AA14ClientAPIDelegateForOrganizationalEntityFindServicesBase<O,ID,M> findApi) {
		super(entityType,mockObjsFactory,
			  crudApi,findApi,
			  0l);					// org entities have no crud operation associated background jobs
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CRUD
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected CommandOn<M> _modelObjectStateUpdateCommand() {		
		return new CommandOn<M>() {									
						@Override
						public void executeOn(final M entityToBeUpdated) {
							entityToBeUpdated.setNameByLanguage(LanguageTextsBuilder.createMapBacked()
																		.withMissingLangTextBehavior(LangTextNotFoundBehabior.RETURN_NULL)
																		.addForLang(Language.SPANISH,"A changed name")
																		.addForLang(Language.BASQUE,"[eu] A changed name")
																		.build());
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FIND
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public void doFindTest() {
		// [1]: Basic find tests
		super.doFindTest();
		
		// [2]: Test find by name
		System.out.println("[Test find by name]");
		_managesTestMockObjects.setUpMockObjs(5);
		AA14ClientAPIDelegateForOrganizationalEntityFindServicesBase<O,ID,M> findApi = (AA14ClientAPIDelegateForOrganizationalEntityFindServicesBase<O,ID,M>)this.getClientFindApiAs(AA14ClientAPIDelegateForOrganizationalEntityFindServicesBase.class);
		Collection<M> entitiesWithName = findApi.findByNameIn(Language.SPANISH,
															  "TEST");
		Assert.assertTrue(CollectionUtils.hasData(entitiesWithName));
		System.out.println("\t[Entities by name]: TEST");
		for (M entityWithName : entitiesWithName) {
			System.out.println("\t\t-" + entityWithName.getOid() + " > " + entityWithName.getNameByLanguage().get(Language.SPANISH));
		}
		_managesTestMockObjects.tearDownCreatedMockObjs();
	}
}
