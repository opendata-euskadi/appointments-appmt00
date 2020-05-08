package aa14f.client.api.sub.delegates;

import java.util.Collection;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14FindServicesForOrgDivision;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import aa14f.model.summaries.AA14SummarizedOrgDivision;
import r01f.locale.Language;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindSummariesResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;

public class AA14ClientAPIDelegateForOrgDivisionFindServices
	 extends AA14ClientAPIDelegateForOrganizationalEntityFindServicesBase<AA14OrgDivisionOID,AA14OrgDivisionID,AA14OrgDivision> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIDelegateForOrgDivisionFindServices(final Provider<SecurityContext> securityContextProvider,
														   final Marshaller modelObjectsMarshaller,
														   final AA14FindServicesForOrgDivision findServicesProxy) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  findServicesProxy);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return all OrgDivisions of one organization.
	 * @param orgOid
	 * @return
	 */
	public Collection<AA14OrgDivision> findByOrganization(final AA14OrganizationOID orgOid) {
		FindResult<AA14OrgDivision> findResult = this.getServiceProxyAs(AA14FindServicesForOrgDivision.class)
														.findByOrganization(this.getSecurityContext(),
																			orgOid);
		return findResult.getOrThrow();
	}
	/**
	 * Return summaries for all OrgDivisions in an organization
	 * @param orgOid
	 * @param lang
	 * @return
	 */
	public Collection<AA14SummarizedOrgDivision> findSummariesByOrganization(final AA14OrganizationOID orgOid,
																		     final Language lang) {
		FindSummariesResult<AA14OrgDivision> findResult = this.getServiceProxyAs(AA14FindServicesForOrgDivision.class)
																.findSummariesByOrganization(this.getSecurityContext(),
																							 orgOid,
																							 lang);
		return findResult.getSummariesOrThrow();
	}
}
