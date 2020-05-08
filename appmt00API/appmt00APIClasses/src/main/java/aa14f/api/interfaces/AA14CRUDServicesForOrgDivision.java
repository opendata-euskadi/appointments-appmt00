package aa14f.api.interfaces;

import aa14f.model.config.AA14OrgDivision;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import r01f.services.interfaces.ExposedServiceInterface;

@ExposedServiceInterface
public interface AA14CRUDServicesForOrgDivision
         extends AA14CRUDServicesForOrganizationalEntityBase<AA14OrgDivisionOID,AA14OrgDivisionID,AA14OrgDivision> {
	// nothing specific
}