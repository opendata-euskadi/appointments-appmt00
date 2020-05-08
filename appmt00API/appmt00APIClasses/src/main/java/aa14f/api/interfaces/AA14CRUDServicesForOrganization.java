package aa14f.api.interfaces;

import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import r01f.services.interfaces.ExposedServiceInterface;

@ExposedServiceInterface
public interface AA14CRUDServicesForOrganization
         extends AA14CRUDServicesForOrganizationalEntityBase<AA14OrganizationOID,AA14OrganizationID,AA14Organization> {
	// nothing specific
}