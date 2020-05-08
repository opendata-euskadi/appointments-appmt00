package aa14f.api.interfaces;

import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import r01f.locale.Language;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindSummariesResult;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ExposedServiceInterface;

@ExposedServiceInterface
public interface AA14FindServicesForOrgDivisionServiceLocation
         extends AA14FindServicesForOrganizationalEntityBase<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID,AA14OrgDivisionServiceLocation> {
	/**
	 * Return all locations in a service
	 * @param securityContext
	 * @param serviceOid
	 * @return
	 */
	public FindResult<AA14OrgDivisionServiceLocation> findByOrgDivisionService(final SecurityContext securityContext,
			  								    			 				   final AA14OrgDivisionServiceOID serviceOid);
	/**
	 * Returns summaries for all locations in a service
	 * @param securityContext
	 * @param serviceOid
	 * @param lang
	 * @return
	 */
	public FindSummariesResult<AA14OrgDivisionServiceLocation> findSummariesByOrgDivisionService(final SecurityContext securityContext,
																  			  	  		  		 final AA14OrgDivisionServiceOID serviceOid,
																  			  	  		  		 final Language lang);
}