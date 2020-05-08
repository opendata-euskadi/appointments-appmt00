package aa14f.api.interfaces;

import aa14f.model.config.AA14OrgDivision;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import r01f.locale.Language;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindSummariesResult;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ExposedServiceInterface;

@ExposedServiceInterface
public interface AA14FindServicesForOrgDivision
         extends AA14FindServicesForOrganizationalEntityBase<AA14OrgDivisionOID,AA14OrgDivisionID,AA14OrgDivision> {
	/**
	 * Return all divisions belonging to an organization
	 * @param securityContext
	 * @param orgOid
	 * @return
	 */
	public FindResult<AA14OrgDivision> findByOrganization(final SecurityContext securityContext,
			  								  		   	  final AA14OrganizationOID orgOid);
	
	/**
	 * Returns summaries for all divisions belonging to an organization
	 * @param securityContext
	 * @param orgOid
	 * @param lang
	 * @return
	 */
	public FindSummariesResult<AA14OrgDivision> findSummariesByOrganization(final SecurityContext securityContext,
																		 	final AA14OrganizationOID orgOid,
																		 	final Language lang);	
}