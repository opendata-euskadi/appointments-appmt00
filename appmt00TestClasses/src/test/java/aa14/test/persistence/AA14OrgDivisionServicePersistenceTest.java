package aa14.test.persistence;

import java.util.Collection;

import org.junit.Assert;

import aa14.test.mockfactories.AA14MockOrgDivisionServiceFactory;
import aa14f.client.api.AA14ClientAPI;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForOrgDivisionServiceFindServices;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.summaries.AA14SummarizedOrgDivisionService;
import r01f.locale.Language;
import r01f.util.types.collections.CollectionUtils;

public class AA14OrgDivisionServicePersistenceTest 
	 extends AA14OrganizationalObjectPersistenceTestBase<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID,AA14OrgDivisionService> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14Organization _organization;
	private final AA14OrgDivision _division;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrgDivisionServicePersistenceTest(final AA14ClientAPI api,
						 			  		   final AA14Organization organization,
						 			  		   final AA14OrgDivision division) {
		super(AA14OrgDivisionService.class,new AA14MockOrgDivisionServiceFactory(organization,division),
			  api.orgDivisionServicesAPI().getForCRUD(),api.orgDivisionServicesAPI().getForFind());
		_organization = organization;
		_division = division;
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
		// [0]: Create 5 divisions
		_managesTestMockObjects.setUpMockObjs(5);
		
		// [1]: Test
		AA14ClientAPIDelegateForOrgDivisionServiceFindServices findApi = getClientFindApiAs(AA14ClientAPIDelegateForOrgDivisionServiceFindServices.class);
		
		// Find services by divisions
		System.out.println("\n\n\n\n--------------------------------------------------------------------");
		System.out.println("[" + _division.getId() + " division services]");
		Collection<AA14OrgDivisionService> services = findApi.findByOrgDivision(_division.getOid());
		
		Assert.assertTrue(CollectionUtils.hasData(services));		
		for(AA14OrgDivisionService service : services) {
			System.out.println("\t-" + service.getOid() + " > " + service.getNameByLanguage().get(Language.SPANISH));
		}
		
		// Find service summaries by division
		System.out.println("\n\n\n\n--------------------------------------------------------------------");
		System.out.println("[" + _division.getId() + " division services summaries]");
		Collection<AA14SummarizedOrgDivisionService> servicesSummaries = findApi.findSummariesByOrgDivision(_division.getOid(),Language.SPANISH);
		Assert.assertTrue(CollectionUtils.hasData(servicesSummaries));
		
		if (CollectionUtils.hasData(servicesSummaries)) {
			for (AA14SummarizedOrgDivisionService serviceSummary : servicesSummaries) {
				System.out.println("\t-" + serviceSummary.getOid() + " > " + serviceSummary.getId() + " > " + serviceSummary.getName());
			}
		}
		
		// [2]: Tear down created model objects
		_managesTestMockObjects.tearDownCreatedMockObjs();
	}
	@Override
	public void testOtherMethods() {
		// nothing
	}
}
