package aa14f.api.interfaces;

import aa14f.model.search.AA14SearchFilterForOrganizationalEntity;
import aa14f.model.search.AA14SearchResultItemForOrganizationalEntity;
import r01f.services.interfaces.ExposedServiceInterface;
import r01f.services.interfaces.SearchServicesForModelObject;

@ExposedServiceInterface
public interface AA14SearchServices 
	     extends SearchServicesForModelObject<AA14SearchFilterForOrganizationalEntity,AA14SearchResultItemForOrganizationalEntity>,
	     		 AA14ServiceInterface {
	// nothing here
}