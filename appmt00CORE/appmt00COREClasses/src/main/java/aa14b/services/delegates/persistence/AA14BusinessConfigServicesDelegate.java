package aa14b.services.delegates.persistence;

import java.util.Collection;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.eventbus.EventBus;

import aa14b.db.entities.AA14DBEntityForBusinessConfig;
import aa14f.api.interfaces.AA14BusinessConfigServices;
import aa14f.api.interfaces.AA14FindServicesForOrgDivision;
import aa14f.api.interfaces.AA14FindServicesForOrgDivisionService;
import aa14f.api.interfaces.AA14FindServicesForOrgDivisionServiceLocation;
import aa14f.api.interfaces.AA14FindServicesForOrganization;
import aa14f.api.interfaces.AA14FindServicesForSchedule;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.business.AA14BusinessConfig;
import aa14f.model.config.business.AA14BusinessConfigs;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.persistence.PersistenceOperationExecResultBuilder;
import r01f.model.persistence.PersistenceOperationResult;
import r01f.model.services.COREServiceMethod;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObjectImpl;
import r01f.securitycontext.SecurityContext;
import r01f.services.delegates.persistence.PersistenceServicesDelegateBase;
import r01f.util.types.Dates;
import r01f.util.types.collections.CollectionUtils;

@Accessors(prefix="_")
public class AA14BusinessConfigServicesDelegate
	 extends PersistenceServicesDelegateBase 
  implements AA14BusinessConfigServices {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final EntityManager _entityManager;
	
	private final AA14FindServicesForOrganization _orgFind;
	private final AA14FindServicesForOrgDivision _orgDivFind;
	private final AA14FindServicesForOrgDivisionService _orgDivSrvcFind;
	private final AA14FindServicesForOrgDivisionServiceLocation _orgDivSrvcLocFind;
	private final AA14FindServicesForSchedule _schFind;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14BusinessConfigServicesDelegate(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
											  final EntityManager entityManager,
											  final Marshaller marshaller,
				  			   		   	   	  final EventBus eventBus,
				  			   		   	   	  // crud services
				  			   		   	   	  final AA14FindServicesForOrganization orgFind,
											  final AA14FindServicesForOrgDivision orgDivFind,
											  final AA14FindServicesForOrgDivisionService orgDivSrvcFind,
											  final AA14FindServicesForOrgDivisionServiceLocation orgDivSrvcLocFind,
											  final AA14FindServicesForSchedule schFind) {
		super(coreCfg,
			  null,			// no service impl
			  eventBus);	// no event bus
		_entityManager = entityManager;
		
		_orgFind = orgFind;
		_orgDivFind = orgDivFind;
		_orgDivSrvcFind = orgDivSrvcFind;
		_orgDivSrvcLocFind = orgDivSrvcLocFind;
		_schFind = schFind;
	}
	// this constructor is ONLY used when updating the [last-update] date
	AA14BusinessConfigServicesDelegate(final EntityManager entityManager) {
		this(null,
			 entityManager,
			 null,				// marshaller
			 null,				// event bus
			 // crud services
			 null,
			 null,
			 null,
			 null,
			 null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public PersistenceOperationResult<AA14BusinessConfigs> loadConfig(final SecurityContext securityContext) {
		AA14BusinessConfigs outCfgs = _doLoadConfig(securityContext);
		return PersistenceOperationExecResultBuilder.using(securityContext)										
													.executed(COREServiceMethod.named("loadConfig"))
													.returning(outCfgs);
	}
	private AA14BusinessConfigs _doLoadConfig(final SecurityContext securityContext) {
		// Find all different business ids
		TypedQuery<AA14BusinessID> qry = _entityManager.createNamedQuery("AA14DBOrganizationalEntityDifferentBusinessId",
																		 AA14BusinessID.class);
		qry.setHint(QueryHints.READ_ONLY,HintValues.TRUE);
		Collection<AA14BusinessID> differentBusinessId = qry.getResultList();
		
		// Load the config for everty businessId
		AA14BusinessConfigs outCfgs = new AA14BusinessConfigs();
		if (CollectionUtils.hasData(differentBusinessId)) {
			Collection<AA14BusinessConfig> businessCfgs = FluentIterable.from(differentBusinessId)
																.transform(new Function<AA14BusinessID,AA14BusinessConfig>() {
																				@Override
																				public AA14BusinessConfig apply(final AA14BusinessID businessId) {
																					return _loadConfigFor(securityContext,
																										  businessId);
																				}
																		   })
																.toList();
			outCfgs.setBusiness(businessCfgs);
		}
		return outCfgs;
	}
	private AA14BusinessConfig _loadConfigFor(final SecurityContext securityContext,
											  final AA14BusinessID businessId) {
		// a) load orgs
		Collection<AA14Organization> orgs = _orgFind.findByBusinessId(securityContext, 
																	  businessId)
													.getOrThrow();
		
		// b) load org divisions
		Collection<AA14OrgDivision> divs = CollectionUtils.hasData(orgs)
												? _orgDivFind.findByBusinessId(securityContext,
																			   businessId)
													  		  .getOrThrow()
												: null;
		// c) load org division services
		Collection<AA14OrgDivisionService> srvcs = CollectionUtils.hasData(divs)
														? _orgDivSrvcFind.findByBusinessId(securityContext,
																					businessId)
																  		 .getOrThrow()
														: null;
		// d) load org division services locations
		Collection<AA14OrgDivisionServiceLocation> locs = CollectionUtils.hasData(srvcs)
																? _orgDivSrvcLocFind.findByBusinessId(securityContext,
																						  			  businessId)
																					.getOrThrow()
																: null;
		// d) load schedules
		Collection<AA14Schedule> schs = _schFind.findByBusinessId(securityContext,
																  businessId)
												.getOrThrow();
		
		// return
		AA14BusinessConfig outConfig = new AA14BusinessConfig();
		outConfig.setId(businessId);
		outConfig.setOrganization(CollectionUtils.<AA14Organization>pickOneElement(orgs));	// TODO maybe sometime...
		outConfig.setDivisions(divs);
		outConfig.setServices(srvcs);
		outConfig.setLocations(locs);
		outConfig.setSchedules(schs);
		return outConfig;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LAST UPDATE DATE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public PersistenceOperationResult<Date> getLastUpdateDate(final SecurityContext securityContext) {
		AA14DBEntityForBusinessConfig dbEntity = _entityManager.find(AA14DBEntityForBusinessConfig.class,
																	 new DBPrimaryKeyForModelObjectImpl("last.update"));
		// get the date
		Date outDate = dbEntity != null ? Dates.fromCalendar(dbEntity.getLastUpdateDate())
										: new Date();	// just now
		// return
		return PersistenceOperationExecResultBuilder.using(securityContext)
													.executed(COREServiceMethod.named("getLastUpdateDate"))
													.returning(outDate);
	}
	@Override
	public PersistenceOperationResult<Date> updateLastUpdateDate(final SecurityContext securityContext, 
																 final Date date) {
		AA14DBEntityForBusinessConfig dbEntity = _entityManager.find(AA14DBEntityForBusinessConfig.class,
																	 new DBPrimaryKeyForModelObjectImpl("last.update"));
		if (dbEntity == null) dbEntity = new AA14DBEntityForBusinessConfig();
		
		// set the date & update the db
		dbEntity.setLastUpdateDate(Dates.asCalendar(date));
		_entityManager.persist(dbEntity);
		
		// return
		return PersistenceOperationExecResultBuilder.using(securityContext)
													.executed(COREServiceMethod.named("updateLastUpdateDate"))
													.returning(date);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
}
