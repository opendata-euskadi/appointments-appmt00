package aa14b.db.find;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import aa14b.db.entities.AA14DBEntityForSchedule;
import aa14f.api.interfaces.AA14FindServicesForSchedule;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import lombok.extern.slf4j.Slf4j;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindResultBuilder;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.securitycontext.SecurityContext;

/**
 * Persistence layer
 */
@Slf4j
public class AA14DBFindForSchedule
	 extends AA14DBFindBase<AA14ScheduleOID,AA14ScheduleID,AA14Schedule,
	 						AA14DBEntityForSchedule>
  implements AA14FindServicesForSchedule {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBFindForSchedule(final DBModuleConfig dbCfg,
								 final EntityManager entityManager,
								 final Marshaller marshaller) {
		super(dbCfg,
			  AA14Schedule.class,AA14DBEntityForSchedule.class,
			  entityManager,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14Schedule> findByBusinessId(final SecurityContext securityContext,
										  			 final AA14BusinessID businessId) {
		log.debug("> loading schedules for businessId={}",businessId);

		TypedQuery<AA14DBEntityForSchedule> query = this.getEntityManager()
												        .createNamedQuery("AA14DBScheduleByBusinessId",
														  		          AA14DBEntityForSchedule.class)
														.setParameter("businessId",businessId.asString());
		query.setHint(QueryHints.READ_ONLY,HintValues.TRUE);
		Collection<AA14DBEntityForSchedule> entities = query.getResultList();

		FindResult<AA14Schedule> outEntities = FindResultBuilder.using(securityContext)
										          	    .on(_modelObjectType)
										          	    .foundDBEntities(entities)
										          	    .transformedToModelObjectsUsing(this);
		return outEntities;
	}
}