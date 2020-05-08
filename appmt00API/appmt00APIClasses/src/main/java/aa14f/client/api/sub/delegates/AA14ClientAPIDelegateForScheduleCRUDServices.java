package aa14f.client.api.sub.delegates;

import java.util.Collection;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14CRUDServicesForSchedule;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;

public class AA14ClientAPIDelegateForScheduleCRUDServices
	 extends AA14ClientAPIDelegateForCRUDServicesBase<AA14ScheduleOID,AA14ScheduleID,AA14Schedule> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIDelegateForScheduleCRUDServices(final Provider<SecurityContext> securityContextProvider,
													    final Marshaller modelObjectsMarshaller,
														final AA14CRUDServicesForSchedule crudServicesProxy) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  crudServicesProxy);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Links an schedule to certain locations
	 * Creates the schedule to service location [many to many] relation
	 * Usually there's a [one-to-one relation] between the [schedule] and [location] (service)
	 * ... BUT sometimes the SAME person works for TWO (or more) [services] (location)
	 * so it can exist a [many-to-many] relation between the [schedule] and [location] (service)
	 * 
	 * <pre>
	 * ONE-TO-ONE RELATION						MANY-TO-MANY RELATION
	 * =====================================================================================
	 *                                                  +------------+
	 *    +------------+                                | location A |
	 *    | location A |                                +-----^--^---+
	 *    +-----^------+                                      |  |               +------------+
	 *          |                                             +------------------+ schedule A |
	 *          |           +------------+                    |  |               +------------+
	 *          +-----------+ schedule A |                    |  |
	 *                      +------------+                    |  |               +------------+
	 *                                                        |  +---------------+ schedule A |
	 *    +------------+                                      |  |               +------------+
	 *    | location B |                                +-----v--v---+
	 *    +-----^------+                                | location B |
	 *          |                                       +------------+
	 *          |           +------------+
	 *          +-----------+ schedule B |
	 *                      +------------+
	 * </pre>
	 * 
	 * Users can ONLY be associated with a single [schedule] so if a certain user is working for more 
	 * than a SINGLE [service] (location), a NEW [schedule] must be created and associated with all
	 * [services] (location)
	 * <pre>
	 *          |  SRVC LOC 1   |   SRVC LOC 2    |   SRVC LOC 3
	 *          |===============|=================|================
	 *    SCH 1 |       X       |                 |               
	 *    SCH 2 |               |       X         |      X
	 *    SCH 3 |       X       |       X         |      X 
	 * <pre>
	 * If [sch3] has a single [resource] (user), when an [slot] is occupied either at  [srvc 1], [srvc 2] or [srvc 3],
	 * the [slot] is NOT available 
	 * 
	 * So now when finding an available [slot] for a certain [service] (location) ALL [schedules] related with the
	 * [service] (location) MUST be queried
	 * @param schOid
	 * @param locOids
	 * @return
	 */
	public AA14Schedule linkScheduleToServiceLocations(final AA14ScheduleOID schOid,	
													   final Collection<AA14OrgDivisionServiceLocationOID> locOids) {
		return this.getServiceProxyAs(AA14CRUDServicesForSchedule.class)
						.linkScheduleToServiceLocations(this.getSecurityContext(),
														schOid,
														locOids)
						.getOrThrow();
	}
}
