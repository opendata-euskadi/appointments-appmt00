package aa14.test.persistence;

import java.util.Collection;

import org.junit.Assert;

import aa14.test.mockfactories.AA14MockOrgDivisionFactory;
import aa14f.client.api.AA14ClientAPI;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForOrgDivisionFindServices;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.summaries.AA14SummarizedOrgDivision;
import r01f.locale.Language;
import r01f.util.types.collections.CollectionUtils;

public class AA14OrgDivisionPersistenceTest 
	extends AA14OrganizationalObjectPersistenceTestBase<AA14OrgDivisionOID,AA14OrgDivisionID,AA14OrgDivision> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14Organization _organization;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrgDivisionPersistenceTest(final AA14ClientAPI api,
							   			final AA14Organization organization) {
		super(AA14OrgDivision.class,new AA14MockOrgDivisionFactory(organization),
			  api.orgDivisionsAPI().getForCRUD(),api.orgDivisionsAPI().getForFind());
		_organization = organization;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void testOtherCRUDMethods() {
		// no other CRUD methods
	}
	@Override
	public void testOtherFindMethods() {
		// [0]: Create test objects
		_managesTestMockObjects.setUpMockObjs(5);
		
		
		// [1]: Test
		AA14ClientAPIDelegateForOrgDivisionFindServices findApi = getClientFindApiAs(AA14ClientAPIDelegateForOrgDivisionFindServices.class);
		
		// Find by organization
		System.out.println("\n\n\n\n--------------------------------------------------------------------");
		System.out.println("[" + _organization.getId() + " OrgDivisions]");
		Collection<AA14OrgDivision> divisions = findApi.findByOrganization(_organization.getOid());
		Assert.assertTrue(CollectionUtils.hasData(divisions));
		
		System.out.println("[OrgDivisions for organization]: " + divisions + "_____________________");
		for(AA14OrgDivision div : divisions) {
			System.out.println("\t-" + div.getOid() + " > " + div.getNameByLanguage().get(Language.SPANISH));
		}
		
		// Find summaries by organization
		System.out.println("\n\n\n\n--------------------------------------------------------------------");
		System.out.println("[" + _organization.getId() + " OrgDivisions summaries]");
		Collection<AA14SummarizedOrgDivision> divisionsSummaries = findApi.findSummariesByOrganization(_organization.getOid(),Language.SPANISH);
		Assert.assertTrue(CollectionUtils.hasData(divisionsSummaries));
		
		if (CollectionUtils.hasData(divisionsSummaries)) {
			for (AA14SummarizedOrgDivision divSummary : divisionsSummaries) {
				System.out.println("\t-" + divSummary.getOid() + " > " + divSummary.getId() + " > " + divSummary.getName());
			}
		}
		
		// [2]: Teardown created test objects
		_managesTestMockObjects.tearDownCreatedMockObjs();
	}
	@Override
	public void testOtherMethods() {
		// nothing
	}


}
