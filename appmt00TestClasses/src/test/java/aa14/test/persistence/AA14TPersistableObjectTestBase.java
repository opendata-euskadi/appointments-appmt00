package aa14.test.persistence;

import org.junit.Assert;

import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForCRUDServicesBase;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForFindServicesBase;
import aa14f.model.AA14EntityModelObject;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.patterns.Factory;
import r01f.test.persistence.TestPersistableModelObjectBase;
import r01f.test.persistence.TestPersistableModelObjectManager;

/**
 * JVM arguments:
 * -javaagent:D:/tools_workspaces/eclipse/local_libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true
 */
public abstract class AA14TPersistableObjectTestBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,
								         			 M extends AA14EntityModelObject<O,ID>> 
			  extends TestPersistableModelObjectBase<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	protected AA14TPersistableObjectTestBase(final Class<M> entityType,final Factory<M> mockObjsFactory,
											 final AA14ClientAPIDelegateForCRUDServicesBase<O,ID,M> crudApi,final AA14ClientAPIDelegateForFindServicesBase<O,ID,M> findApi,
											 final long milisToWaitForBackgroundJobs) {
		super(TestPersistableModelObjectManager.create(entityType,mockObjsFactory,
													   crudApi,
													   milisToWaitForBackgroundJobs),
			  crudApi,findApi);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CRUD
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	protected void testOtherCRUDMethods() {
		System.out.println("[Test load by id]");
		_managesTestMockObjects.setUpMockObjs(1);
		
		M entity = _managesTestMockObjects.getAnyCreatedMockObj();	// get the recently created model object
		M entityById = (M)this.getClientCRUDApiAs(AA14ClientAPIDelegateForCRUDServicesBase.class)
									.loadById(entity.getId());
		Assert.assertEquals(entity.getId(),entityById.getId());
		Assert.assertNotNull(entityById);
		System.out.println("[Entity by id=" + entity.getId() + "]: " + entityById.getOid() + " > " + entityById.asSummarizable()
																											   .getSummary().asString());
		
		_managesTestMockObjects.tearDownCreatedMockObjs();
	}
}
