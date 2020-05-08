package aa14b.services.delegates.persistence;

import java.util.Collection;
import java.util.Date;

import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14b.db.crud.AA14DBCRUDForSchedule;
import aa14f.api.interfaces.AA14BusinessConfigServices;
import aa14f.api.interfaces.AA14CRUDServicesForSchedule;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfigBuilder;
import r01f.securitycontext.SecurityContext;
import r01f.services.callback.spec.COREServiceMethodCallbackSpec;
import r01f.util.types.collections.CollectionUtils;

/**
 * Service layer delegated type for CRUD (Create/Read/Update/Delete) operations
 */
public class AA14CRUDServicesDelegateForSchedule
	 extends AA14CRUDServicesDelegateBase<AA14ScheduleOID,AA14ScheduleID,AA14Schedule>
  implements AA14CRUDServicesForSchedule {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final EntityManager _entityManager;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14CRUDServicesDelegateForSchedule(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
											   final EntityManager entityManager,
											   final Marshaller marshaller,
				  			   		   	   	   final EventBus eventBus) {
		super(coreCfg,
			  AA14Schedule.class,
			  new AA14DBCRUDForSchedule(DBModuleConfigBuilder.dbModuleConfigFrom(coreCfg),
					  					entityManager,
					  					marshaller),
			  eventBus);
		_entityManager = entityManager;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDResult<AA14Schedule> linkScheduleToServiceLocations(final SecurityContext securityContext,
																   final AA14ScheduleOID schOid,final Collection<AA14OrgDivisionServiceLocationOID> locOids) {
		// [0] - Check params
		if (schOid == null || CollectionUtils.isNullOrEmpty(locOids)) return CRUDResultBuilder.using(securityContext)
																							  .on(AA14Schedule.class)
																							  .not(PersistenceRequestedOperation.UPDATE)
																							  .becauseClientBadRequest("schedule oid and location oids MUST NOT be null")
																							  .build();
		
		// [1] - Delegate
		return this.getServiceImplAs(AA14CRUDServicesForSchedule.class)
						.linkScheduleToServiceLocations(securityContext,
														schOid,locOids);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OVERRIDE DB MUTATOR METHDOS TO UPDATE THE CONFIG LAST-UPDATE DATE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected CRUDResult<AA14Schedule> doUpdateOrCreate(final SecurityContext securityContext,
											 			final AA14Schedule modelObj,
											 			final PersistenceRequestedOperation requestedOperation, 
											 			final COREServiceMethodCallbackSpec callbackSpec) {
		CRUDResult<AA14Schedule> outResult = super.doUpdateOrCreate(securityContext,
														 			modelObj, 
														 			requestedOperation, 
														 			callbackSpec);
		if (outResult.hasSucceeded()) {
			// update the last-update timestamp
			AA14BusinessConfigServices cfgServices = new AA14BusinessConfigServicesDelegate(_entityManager);
			cfgServices.updateLastUpdateDate(securityContext,
											 new Date());
		}
		return outResult;
	}

	@Override
	public CRUDResult<AA14Schedule> delete(final SecurityContext securityContext, 
									 	   final AA14ScheduleOID oid,
									 	   final COREServiceMethodCallbackSpec callbackSpec) {
		// TODO Auto-generated method stub
		CRUDResult<AA14Schedule> outResult = super.delete(securityContext, 
														  oid, 
														  callbackSpec);
		if (outResult.hasSucceeded()) {
			// update the last-update timestamp
			AA14BusinessConfigServices cfgServices = new AA14BusinessConfigServicesDelegate(_entityManager);
			cfgServices.updateLastUpdateDate(securityContext,
											 new Date());
		}
		return outResult;
	}
}
