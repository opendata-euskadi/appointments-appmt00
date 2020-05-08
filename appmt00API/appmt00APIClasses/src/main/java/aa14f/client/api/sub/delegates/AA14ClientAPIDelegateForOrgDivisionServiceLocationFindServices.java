package aa14f.client.api.sub.delegates;

import java.util.Collection;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14FindServicesForOrgDivisionServiceLocation;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.summaries.AA14SummarizedOrgDivisionServiceLocation;
import r01f.locale.Language;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindSummariesResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;

public class AA14ClientAPIDelegateForOrgDivisionServiceLocationFindServices
	 extends AA14ClientAPIDelegateForOrganizationalEntityFindServicesBase<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID,AA14OrgDivisionServiceLocation> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIDelegateForOrgDivisionServiceLocationFindServices(final Provider<SecurityContext> securityContextProvider,
																		  final Marshaller modelObjectsMarshaller,
													 			  		  final AA14FindServicesForOrgDivisionServiceLocation findServicesProxy) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  findServicesProxy);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return all locations in a service
	 * @param serviceOid
	 * @return
	 */
	public Collection<AA14OrgDivisionServiceLocation> findByOrgDivisionService(final AA14OrgDivisionServiceOID serviceOid) {
		FindResult<AA14OrgDivisionServiceLocation> findResult = this.getServiceProxyAs(AA14FindServicesForOrgDivisionServiceLocation.class)
																		.findByOrgDivisionService(this.getSecurityContext(),
																					   		  	  serviceOid);
		return findResult.getOrThrow();
	}
	/**
	 * Return summaries for all locations in a service
	 * @param serviceOid
	 * @param lang
	 * @return
	 */
	public Collection<AA14SummarizedOrgDivisionServiceLocation> findSummariesByOrgDivisionService(final AA14OrgDivisionServiceOID serviceOid,
																   				   				  final Language lang) {
		FindSummariesResult<AA14OrgDivisionServiceLocation> findResult = this.getServiceProxyAs(AA14FindServicesForOrgDivisionServiceLocation.class)
																				.findSummariesByOrgDivisionService(this.getSecurityContext(),
																							   			 		   serviceOid,
																							   			 		   lang);
		return findResult.getSummariesOrThrow();
	}
}
