package aa14b.services.delegates.persistence;

import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14b.db.find.AA14DBFindForSchedule;
import aa14f.api.interfaces.AA14FindServicesForSchedule;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.persistence.FindResult;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfigBuilder;
import r01f.securitycontext.SecurityContext;

/**
 * Service layer delegated type for CRUD (Create/Read/Update/Delete) operations
 */
public class AA14FindServicesDelegateForSchedule
	 extends AA14FindServicesDelegateBase<AA14ScheduleOID,AA14ScheduleID,AA14Schedule>
  implements AA14FindServicesForSchedule {

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14FindServicesDelegateForSchedule(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
											   final EntityManager entityManager,
											   final Marshaller marshaller,
				  			   		   	   	   final EventBus eventBus) {
		super(coreCfg,
			  AA14Schedule.class,
			  new AA14DBFindForSchedule(DBModuleConfigBuilder.dbModuleConfigFrom(coreCfg),
					  					entityManager,
					  			  		marshaller));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14Schedule> findByBusinessId(final SecurityContext securityContext,
										  			 final AA14BusinessID businessId) {
		if (businessId == null) throw new IllegalArgumentException("businessId is mandatory!");
		return this.getServiceImplAs(AA14FindServicesForSchedule.class)
				   .findByBusinessId(securityContext,
						   			 businessId);
	}
}
