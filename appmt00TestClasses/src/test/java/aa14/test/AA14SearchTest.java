package aa14.test;

import org.junit.Assert;

import aa14f.client.api.AA14ClientAPI;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14Organization;
import aa14f.model.search.AA14SearchFilter;
import aa14f.model.search.AA14SearchResultItem;
import r01f.locale.Language;
import r01f.model.search.SearchResults;

public class AA14SearchTest {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14ClientAPI _api;
	private final AA14Organization _org;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14SearchTest(final AA14ClientAPI api,
					      final AA14Organization org) {
		_api = api;
		_org = org;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public void doTest() {
		// create a search filter for every entity
		try {
			@SuppressWarnings("unchecked")
			AA14SearchFilter filter = new AA14SearchFilter(AA14Organization.class,
														   AA14OrgDivision.class,
														   AA14OrgDivisionService.class);
			filter.belongingTo(_org.getOid());
			filter.withText("Agent").in(Language.SPANISH);
			SearchResults<AA14SearchFilter,
						  AA14SearchResultItem> searchResults = _api.searchAPI()
						  											.search(filter)
						  											.firstPage();
				Assert.assertTrue(searchResults != null && searchResults.getTotalItemsCount() > 0);
				System.out.println("--->Found: " + searchResults.getTotalItemsCount() + " items searching by " + filter.toCriteriaString());

				for (AA14SearchResultItem item : searchResults.getPageItems()) {
					// ...
				}
		} catch(Throwable th) {
			th.printStackTrace(System.out);
		}
	}
}
