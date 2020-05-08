package aa14f.api.interfaces;

import java.util.Collection;

import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import r01f.model.persistence.CRUDResult;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ExposedServiceInterface;

@ExposedServiceInterface
public interface AA14CRUDServicesForOrgDivisionServiceLocation
         extends AA14CRUDServicesForOrganizationalEntityBase<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID,AA14OrgDivisionServiceLocation> {
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
	 * @param securityContext
	 * @param locOid
	 * @param schOids
	 * @return
	 */
	public CRUDResult<AA14OrgDivisionServiceLocation> linkLocationToSchedules(SecurityContext securityContext,
																			  AA14OrgDivisionServiceLocationOID locOid,
																			  Collection<AA14ScheduleOID> schOids);
}