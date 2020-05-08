package aa14f.api.interfaces;

import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import r01f.locale.Language;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindSummariesResult;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ExposedServiceInterface;

@ExposedServiceInterface
public interface AA14FindServicesForOrgDivisionService
         extends AA14FindServicesForOrganizationalEntityBase<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID,AA14OrgDivisionService> {
	/**
	 * Return all services in a division.
	 * @param securityContext
	 * @param divisionOid
	 * @return
	 */
	public FindResult<AA14OrgDivisionService> findByOrgDivision(final SecurityContext securityContext,
			  								    			 	final AA14OrgDivisionOID divisionOid);
	/**
	 * Returns summaries for all services in a division
	 * @param securityContext
	 * @param divisionOid
	 * @param lang
	 * @return
	 */
	public FindSummariesResult<AA14OrgDivisionService> findSummariesByOrgDivision(final SecurityContext securityContext,
																  			  	  final AA14OrgDivisionOID divisionOid,
																  			  	  final Language lang);
}