package aa14b.db.crud;

import java.util.Collection;

import javax.persistence.EntityManager;

import com.google.common.collect.Lists;

import aa14b.db.entities.AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedule;
import aa14b.db.entities.AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedulePrimaryKey;
import aa14b.db.entities.AA14DBEntityForOrgDivision;
import aa14b.db.entities.AA14DBEntityForOrgDivisionService;
import aa14b.db.entities.AA14DBEntityForOrgDivisionServiceLocation;
import aa14b.db.entities.AA14DBEntityForOrganization;
import aa14b.db.entities.AA14DBEntityForSchedule;
import aa14f.api.interfaces.AA14CRUDServicesForOrgDivisionServiceLocation;
import aa14f.model.AA14ModelObjectRef;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import lombok.extern.slf4j.Slf4j;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.model.persistence.PersistencePerformedOperation;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObjectImpl;
import r01f.securitycontext.SecurityContext;
import r01f.util.types.collections.CollectionUtils;

/**
 * Persistence layer
 */
@Slf4j
public class AA14DBCRUDForOrgDivisionServiceLocation
	 extends AA14DBCRUDForOrganizationalEntityBase<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID,AA14OrgDivisionServiceLocation,
	 				        					   AA14DBEntityForOrgDivisionServiceLocation>
  implements AA14CRUDServicesForOrgDivisionServiceLocation {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBCRUDForOrgDivisionServiceLocation(final DBModuleConfig dbCfg,
												   final EntityManager entityManager,
												   final Marshaller marshaller) {
		super(dbCfg,
			  AA14OrgDivisionServiceLocation.class,AA14DBEntityForOrgDivisionServiceLocation.class,
			  entityManager,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setDBEntityFieldsFromModelObject(final SecurityContext securityContext,
												 final AA14OrgDivisionServiceLocation loc,final AA14DBEntityForOrgDivisionServiceLocation dbEntity) {
		super.setDBEntityFieldsFromModelObject(securityContext, 
											   loc,dbEntity);
		// org reference
		dbEntity.setOrganizationOid(loc.getOrgRef().getOid().asString());
		dbEntity.setOrganizationId(loc.getOrgRef().getId().asString());
		
		// division reference
		dbEntity.setOrgDivisionOid(loc.getOrgDivisionRef().getOid().asString());		
		dbEntity.setOrgDivisionId(loc.getOrgDivisionRef().getId().asString());
		
		// service reference
		dbEntity.setOrgDivisionServiceOid(loc.getOrgDivisionServiceRef().getOid().asString());		
		dbEntity.setOrgDivisionServiceId(loc.getOrgDivisionServiceRef().getId().asString());
		
		// hierarchy level
		dbEntity.setHierarchyLevel(4);	// used to return ordered results when searching (see AA14DBSearcherForEntityModelObject)
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void completeDBEntityBeforeCreateOrUpdate(final SecurityContext securityContext,
													 final PersistencePerformedOperation performedOp, 
													 final AA14OrgDivisionServiceLocation loc,final AA14DBEntityForOrgDivisionServiceLocation dbLoc) {
		// load the organization, division & service entities
		AA14DBEntityForOrganization dbOrg = this.getEntityManager().find(AA14DBEntityForOrganization.class,
																		 new DBPrimaryKeyForModelObjectImpl(dbLoc.getOrganizationOid()));
		AA14DBEntityForOrgDivision dbDivision = this.getEntityManager().find(AA14DBEntityForOrgDivision.class,
																	 		 new DBPrimaryKeyForModelObjectImpl(dbLoc.getOrgDivisionOid()));
		AA14DBEntityForOrgDivisionService dbService = this.getEntityManager().find(AA14DBEntityForOrgDivisionService.class,
																	 		 	   new DBPrimaryKeyForModelObjectImpl(dbLoc.getOrgDivisionServiceOid()));
		// set the dependencies
		dbLoc.setOrganizationOid(dbOrg.getOrganizationOid());
		dbLoc.setOrgDivisionOid(dbDivision.getOid());
		dbLoc.setOrgDivisionServiceOid(dbService.getOid());
		
		dbLoc.setOrganizationId(dbOrg.getId());
		dbLoc.setOrgDivisionId(dbDivision.getId());
		dbLoc.setOrgDivisionServiceId(dbService.getId());
		
//		locDBEntity.setOrganization(dbOrg);
//		locDBEntity.setOrgDivision(dbDivision);
//		locDBEntity.setOrgDivisionService(dbService);
		
		// setting the location's dependent objects (org /division / service), also modifies the later since it's a BI-DIRECTIONAL relation
		// ... so the entity manager MUST be refreshed in order to avoid an optimistic locking exception
		this.getEntityManager().refresh(dbOrg);
		this.getEntityManager().refresh(dbDivision);
		this.getEntityManager().refresh(dbService);
		
		if (CollectionUtils.hasData(loc.getSchedulesRefs())) {
			log.debug("Linking service location with oid={} to schedules={}",
					  loc.getOid(),loc.getSchedulesOids());
			for (AA14ModelObjectRef<AA14ScheduleOID,
									AA14ScheduleID> ref : loc.getSchedulesRefs()) {			
				AA14DBEntityForSchedule dbSch = _entityManager.find(AA14DBEntityForSchedule.class,
																    DBPrimaryKeyForModelObjectImpl.from(ref.getOid()));
				if (dbSch == null) {
					log.error("Cannot link the service location with oid={} to the schedule with oid={}: the schedule does NOT exists!!!",
							  loc.getOid(),ref.getOid());
					continue;	// skip
				}
				// add the reference to the location
				// (manually managing the many to many relation)
				AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedulePrimaryKey manyToManyPK = AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedulePrimaryKey.from(dbLoc.getOid(),dbSch.getOid());
				AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedule manyToMany = _entityManager.find(AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedule.class,
																													   manyToManyPK);
				if (manyToMany == null) {
					manyToMany = new AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedule(dbLoc.getOid(),dbSch.getOid());
					_entityManager.persist(manyToMany);
				}
				
				// BEWARE!!	the location <-> schedule it's a MANY-TO-MANY relation so the schedule descriptor 
				//			also contains a reference to the schedule that MUST be updated
				AA14Schedule sch = _modelObjectsMarshaller.forReading().fromXml(dbSch.getDescriptor(),
																				AA14Schedule.class);
				sch.addServiceLocationRef(loc.getReference());
				dbSch.setDescriptor(_modelObjectsMarshaller.forWriting().toXml(sch));
				_entityManager.persist(dbSch);
				_entityManager.flush();
				
				// setting the service location dependent schedule objs, also modifies the later since it's a BI-DIRECTIONAL relation
				// ... so the entity manager MUST be refreshed in order to avoid an optimistic locking exception
				_entityManager.refresh(dbSch);
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CRUDResult<AA14OrgDivisionServiceLocation> linkLocationToSchedules(final SecurityContext securityContext,
																			  final AA14OrgDivisionServiceLocationOID locOid,final Collection<AA14ScheduleOID> schOids) {
		// [1] - Load the schedule db entity
		AA14DBEntityForOrgDivisionServiceLocation locDBEntity = _entityManager.find(AA14DBEntityForOrgDivisionServiceLocation.class,
																  				    DBPrimaryKeyForModelObjectImpl.from(locOid));
		if (locDBEntity == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(AA14OrgDivisionServiceLocation.class)
									.not(PersistenceRequestedOperation.UPDATE)
									.becauseClientRequestedEntityWasNOTFound()
									.about(locOid)
									.build();
		}
		AA14OrgDivisionServiceLocation loc = this.dbEntityToModelObject(securityContext,
														 				locDBEntity); 		
				
		// [2] - Link the location with every schedule
		loc.setSchedulesRefs(_scheduleRefsFor(securityContext,
											  schOids));
		// [3] - return the updated schedule (see completeDBEntityBeforeCreateOrUpdate() method)
		CRUDResult<AA14OrgDivisionServiceLocation> outCRUDResult = super.update(securityContext,
																				loc);
		return outCRUDResult;
	}
	private Collection<AA14ModelObjectRef<AA14ScheduleOID,
										  AA14ScheduleID>> _scheduleRefsFor(final SecurityContext securityContext,
												  							final Collection<AA14ScheduleOID> schOids) {
		Collection<AA14ModelObjectRef<AA14ScheduleOID,AA14ScheduleID>> outRefs = Lists.newArrayListWithExpectedSize(schOids.size());
		for (AA14ScheduleOID schOid : schOids) {
			AA14DBEntityForSchedule schDBEntity = _entityManager.find(AA14DBEntityForSchedule.class,
																	  DBPrimaryKeyForModelObjectImpl.from(schOid));
			if (schDBEntity == null) {
				log.error("Cannot find the schedule with oid={}: the schedule does NOT exists!!!",
						  schOid);
				continue;	// skip
			}
			outRefs.add(new AA14ModelObjectRef<AA14ScheduleOID,
										  	   AA14ScheduleID>(AA14ScheduleOID.forId(schDBEntity.getOid()),
										  			   		   AA14ScheduleID.forId(schDBEntity.getId())));
		}
		return outRefs;
	}
}
