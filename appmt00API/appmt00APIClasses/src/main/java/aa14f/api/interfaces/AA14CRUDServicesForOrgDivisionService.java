package aa14f.api.interfaces;

import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import r01f.services.interfaces.ExposedServiceInterface;

@ExposedServiceInterface
public interface AA14CRUDServicesForOrgDivisionService
         extends AA14CRUDServicesForOrganizationalEntityBase<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID,AA14OrgDivisionService> {
	// nothing specific
}