package aa14f.api.interfaces;

import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.model.persistence.FindSummariesResult;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ExposedServiceInterface;

@ExposedServiceInterface
public interface AA14FindServicesForOrganization
         extends AA14FindServicesForOrganizationalEntityBase<AA14OrganizationOID,AA14OrganizationID,AA14Organization> {
	
	/**
	 * Returns all organizations summaries that contains their {@link OID} and ID
	 * alongside their name in the provided language
	 * @param securityContext
	 * @param lang
	 * @return
	 */
	public FindSummariesResult<AA14Organization> findSummaries(final SecurityContext securityContext,
															   final Language lang);
}