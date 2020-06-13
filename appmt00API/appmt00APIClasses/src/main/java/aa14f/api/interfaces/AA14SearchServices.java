package aa14f.api.interfaces;

import aa14f.model.search.AA14SearchFilter;
import aa14f.model.search.AA14SearchResultItemForOrganizationalEntity;
import r01f.services.interfaces.ExposedServiceInterface;
import r01f.services.interfaces.SearchServicesForModelObject;

@ExposedServiceInterface
public interface AA14SearchServices 
	     extends SearchServicesForModelObject<AA14SearchFilter,AA14SearchResultItemForOrganizationalEntity>,
	     		 AA14ServiceInterface {
	// nothing here
}