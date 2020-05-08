package aa14.test.persistence;

import java.util.Collection;

import org.junit.Assert;

import aa14.test.mockfactories.AA14MockOrgDivisionServiceLocationFactory;
import aa14f.client.api.AA14ClientAPI;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForOrgDivisionServiceLocationFindServices;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.summaries.AA14SummarizedOrgDivisionServiceLocation;
import r01f.locale.Language;
import r01f.util.types.collections.CollectionUtils;

public class AA14OrgDivisionServiceLocationPersistenceTest 
	 extends AA14OrganizationalObjectPersistenceTestBase<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID,AA14OrgDivisionServiceLocation> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14Organization _organization;
	private final AA14OrgDivision _division;
	private final AA14OrgDivisionService _service;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrgDivisionServiceLocationPersistenceTest(final AA14ClientAPI api,
						 			  		  		   final AA14Organization organization,
						 			  		  		   final AA14OrgDivision division,
						 			  		  		   final AA14OrgDivisionService service) {
		super(AA14OrgDivisionServiceLocation.class,new AA14MockOrgDivisionServiceLocationFactory(organization,division,service),
			  api.orgDivisionServiceLocationsAPI().getForCRUD(),api.orgDivisionServiceLocationsAPI().getForFind());
		_organization = organization;
		_division = division;
		_service = service;
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
		AA14ClientAPIDelegateForOrgDivisionServiceLocationFindServices findApi = getClientFindApiAs(AA14ClientAPIDelegateForOrgDivisionServiceLocationFindServices.class);
		
		// Find locations by service
		System.out.println("\n\n\n\n--------------------------------------------------------------------");
		System.out.println("[" + _service.getId() + " service locations]");
		Collection<AA14OrgDivisionServiceLocation> locs = findApi.findByOrgDivisionService(_service.getOid());
		
		Assert.assertTrue(CollectionUtils.hasData(locs));		
		for(AA14OrgDivisionServiceLocation loc : locs) {
			System.out.println("\t-" + loc.getOid() + " > " + loc.getNameByLanguage().get(Language.SPANISH));
		}
		
		// Find location summaries by service
		System.out.println("\n\n\n\n--------------------------------------------------------------------");
		System.out.println("[" + _service.getId() + " service locations summaries]");
		Collection<AA14SummarizedOrgDivisionServiceLocation> locationsSummaries = findApi.findSummariesByOrgDivisionService(_service.getOid(),Language.SPANISH);
		Assert.assertTrue(CollectionUtils.hasData(locationsSummaries));
		
		if (CollectionUtils.hasData(locationsSummaries)) {
			for (AA14SummarizedOrgDivisionServiceLocation locSummary : locationsSummaries) {
				System.out.println("\t-" + locSummary.getOid() + " > " + locSummary.getId() + " > " + locSummary.getName());
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
