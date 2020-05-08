package aa14f.client.api.sub.delegates;

import java.util.Collection;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14FindServicesForOrgDivisionService;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.summaries.AA14SummarizedOrgDivisionService;
import r01f.locale.Language;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindSummariesResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;

public class AA14ClientAPIDelegateForOrgDivisionServiceFindServices
	 extends AA14ClientAPIDelegateForOrganizationalEntityFindServicesBase<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID,AA14OrgDivisionService> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIDelegateForOrgDivisionServiceFindServices(final Provider<SecurityContext> securityContextProvider,
																  final Marshaller modelObjectsMarshaller,
													 			  final AA14FindServicesForOrgDivisionService findServicesProxy) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  findServicesProxy);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return all services in a division
	 * @param divisionOid
	 * @return
	 */
	public Collection<AA14OrgDivisionService> findByOrgDivision(final AA14OrgDivisionOID divisionOid) {
		FindResult<AA14OrgDivisionService> findResult = this.getServiceProxyAs(AA14FindServicesForOrgDivisionService.class)
																	.findByOrgDivision(this.getSecurityContext(),
																					   divisionOid);
		return findResult.getOrThrow();
	}
	/**
	 * Return summaries for all services in a division
	 * @param divisionOid
	 * @param lang
	 * @return
	 */
	public Collection<AA14SummarizedOrgDivisionService> findSummariesByOrgDivision(final AA14OrgDivisionOID divisionOid,
																   				   final Language lang) {
		FindSummariesResult<AA14OrgDivisionService> findResult = this.getServiceProxyAs(AA14FindServicesForOrgDivisionService.class)
																			.findSummariesByOrgDivision(this.getSecurityContext(),
																						   			 divisionOid,
																						   			 lang);
		return findResult.getSummariesOrThrow();
	}
}
