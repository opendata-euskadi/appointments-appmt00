package aa14f.api.interfaces;

import aa14f.model.search.AA14SearchFilter;
import aa14f.model.search.AA14SearchResultItem;
import r01f.services.interfaces.ExposedServiceInterface;
import r01f.services.interfaces.SearchServicesForModelObject;

@ExposedServiceInterface
public interface AA14SearchServices 
	     extends SearchServicesForModelObject<AA14SearchFilter,AA14SearchResultItem>,
	     		 AA14ServiceInterface {
	// nothing here
}