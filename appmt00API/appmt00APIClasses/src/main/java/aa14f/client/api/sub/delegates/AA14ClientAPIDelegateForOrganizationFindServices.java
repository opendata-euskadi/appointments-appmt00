package aa14f.client.api.sub.delegates;

import java.util.Collection;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14FindServicesForOrganization;
import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import aa14f.model.summaries.AA14SummarizedOrganization;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.model.persistence.FindSummariesResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;

public class AA14ClientAPIDelegateForOrganizationFindServices
	 extends AA14ClientAPIDelegateForOrganizationalEntityFindServicesBase<AA14OrganizationOID,AA14OrganizationID,AA14Organization> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIDelegateForOrganizationFindServices(final Provider<SecurityContext> securityContextProvider,
															final Marshaller modelObjectsMarshaller,
															final AA14FindServicesForOrganization findServicesProxy) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  findServicesProxy);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns all organizations summary that contains their {@link OID} and ID
	 * alongside their name in the provided language
	 * @param lang
	 * @return
	 */
	public Collection<AA14SummarizedOrganization> findSummaries(final Language lang) {
		FindSummariesResult<AA14Organization> findSummaryResult = this.getServiceProxyAs(AA14FindServicesForOrganization.class)
																			.findSummaries(this.getSecurityContext(),
																						   lang);
		Collection<AA14SummarizedOrganization> orgSummaries = findSummaryResult.getSummariesOrThrow();
		return orgSummaries;
	}
}
