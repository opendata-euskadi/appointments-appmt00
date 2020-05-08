package aa14.test.persistence;

import java.util.Collection;

import aa14.test.mockfactories.AA14MockOrganizationFactory;
import aa14f.client.api.AA14ClientAPI;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForOrganizationFindServices;
import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import aa14f.model.summaries.AA14SummarizedOrganization;
import r01f.locale.Language;
import r01f.util.types.collections.CollectionUtils;

public class AA14OrganizationPersistenceTest 
	 extends AA14OrganizationalObjectPersistenceTestBase<AA14OrganizationOID,AA14OrganizationID,AA14Organization> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrganizationPersistenceTest(final AA14ClientAPI api) {
		super(AA14Organization.class,new AA14MockOrganizationFactory(),
			  api.organizationsAPI().getForCRUD(),api.organizationsAPI().getForFind());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void testOtherCRUDMethods() {
		// no other CRUD methods
	}
	@Override
	protected void testOtherFindMethods() {		
		// [0]: Create 5 test orgs
		_managesTestMockObjects.setUpMockObjs(5);
		
		// [1]: Test
		System.out.println("[Organization summaries]");
		Collection<AA14SummarizedOrganization> orgSummaries = this.getClientFindApiAs(AA14ClientAPIDelegateForOrganizationFindServices.class)
																		.findSummaries(Language.SPANISH);
		if (CollectionUtils.hasData(orgSummaries)) {
			for (AA14SummarizedOrganization orgSumm : orgSummaries) {
				System.out.println("\t-" + orgSumm.getOid() + " > " + orgSumm.getId() + " > " + orgSumm.getName());
			}
		}
		
		// [2]: delete created model objs
		_managesTestMockObjects.tearDownCreatedMockObjs();
	}
	@Override
	protected void testOtherMethods() {
		// nothing
	}
}
