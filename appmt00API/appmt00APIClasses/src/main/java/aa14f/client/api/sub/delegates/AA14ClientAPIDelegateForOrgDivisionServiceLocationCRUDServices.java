package aa14f.client.api.sub.delegates;

import java.util.Collection;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14CRUDServicesForOrgDivisionServiceLocation;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;

public class AA14ClientAPIDelegateForOrgDivisionServiceLocationCRUDServices
	 extends AA14ClientAPIDelegateForOrganizationalEntityCRUDServicesBase<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID,AA14OrgDivisionServiceLocation> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIDelegateForOrgDivisionServiceLocationCRUDServices(final Provider<SecurityContext> securityContextProvider,
																		  final Marshaller modelObjectsMarshaller,
															 			  final AA14CRUDServicesForOrgDivisionServiceLocation crudServicesProxy) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  crudServicesProxy);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Links a location to certain schedules
	 * Creates the schedule to service location [many to many] relation
	 * <pre>
	 *          |  SRVC LOC 1   |   SRVC LOC 2    |   SRVC LOC 3
	 *          |===============|=================|================
	 *    SCH 1 |       X       |                 |               
	 *    SCH 2 |               |       X         |      X
	 *    SCH 3 |       X       |       X         |      X
	 * </pre>
	 * @param locOid
	 * @param schOids
	 * @return
	 */
	public AA14OrgDivisionServiceLocation linkLocationToSchedules(final AA14OrgDivisionServiceLocationOID locOid,
																  final Collection<AA14ScheduleOID> schOids) {
		return this.getServiceProxyAs(AA14CRUDServicesForOrgDivisionServiceLocation.class)
						.linkLocationToSchedules(this.getSecurityContext(),
												 locOid,
												 schOids)
						.getOrThrow();
	}
}
