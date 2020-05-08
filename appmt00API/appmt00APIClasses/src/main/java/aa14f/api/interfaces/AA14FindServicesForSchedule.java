package aa14f.api.interfaces;

import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import r01f.model.persistence.FindResult;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.ExposedServiceInterface;

@ExposedServiceInterface
public interface AA14FindServicesForSchedule
         extends AA14FindServicesBase<AA14ScheduleOID,AA14ScheduleID,AA14Schedule> {
	/**
	 * Finds schedules of the given type for the given business id
	 * @param securityContext
	 * @param orgEntityType
	 * @param businessId
	 * @return
	 */
	public FindResult<AA14Schedule> findByBusinessId(final SecurityContext securityContext,
										  			 final AA14BusinessID businessId);
}