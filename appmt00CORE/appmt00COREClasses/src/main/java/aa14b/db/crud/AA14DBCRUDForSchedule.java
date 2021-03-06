package aa14b.db.crud;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.common.collect.Lists;

import aa14b.db.entities.AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedule;
import aa14b.db.entities.AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedulePrimaryKey;
import aa14b.db.entities.AA14DBEntityForOrgDivisionServiceLocation;
import aa14b.db.entities.AA14DBEntityForSchedule;
import aa14f.api.interfaces.AA14CRUDServicesForSchedule;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14OrganizationalModelObjectRef;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.model.persistence.PersistencePerformedOperation;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.ListensToDBEntityPersistenceEvents;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObjectImpl;
import r01f.securitycontext.SecurityContext;
import r01f.util.types.collections.CollectionUtils;

/**
 * Persistence layer
 */
@Slf4j
public class AA14DBCRUDForSchedule
	 extends AA14DBCRUDBase<AA14ScheduleOID,AA14ScheduleID,AA14Schedule,
	 				        AA14DBEntityForSchedule>
  implements AA14CRUDServicesForSchedule {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBCRUDForSchedule(final DBModuleConfig dbCfg,
								 final EntityManager entityManager,
								 final Marshaller marshaller) {
		super(dbCfg,
			  AA14Schedule.class,AA14DBEntityForSchedule.class,
			  entityManager,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CRUDResult<AA14Schedule> loadById(final SecurityContext securityContext,
											 final AA14ScheduleID id) {
		// Do the query
		TypedQuery<AA14DBEntityForSchedule> query = this.getEntityManager()
														    .createNamedQuery("AA14DBScheduleById",
																  		      _DBEntityType)
															.setParameter("id",id.asString());
		Collection<AA14DBEntityForSchedule> dbEntities = query.getResultList();

		// Return
		CRUDResult<AA14Schedule> outResult = _crudResultForSingleEntity(securityContext,
																		id,
																		dbEntities);
		return outResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setDBEntityFieldsFromModelObject(final SecurityContext securityContext,
												 final AA14Schedule modelObj,final AA14DBEntityForSchedule dbEntity) {
		// Oid
		dbEntity.setOid(modelObj.getOid().asString());
		dbEntity.setId(modelObj.getId().asString());
		
		// business id
		dbEntity.setBusinessId(modelObj.getBusinessId().asString());
		
		// Name
		if (modelObj.getName() != null) {
			dbEntity.setNameSpanish(modelObj.getNameByLanguage().getFor(Language.SPANISH));
			dbEntity.setNameBasque(modelObj.getNameByLanguage().getFor(Language.BASQUE));
		}
	}
	@Override
	public AA14Schedule dbEntityToModelObject(final SecurityContext securityContext,
										      final AA14DBEntityForSchedule dbEntity) {
		// by default the model obj is built from the dbentity using the 
		// descriptor
		AA14Schedule outSchedule = super.dbEntityToModelObject(securityContext,
															   dbEntity);
		return outSchedule;
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//	OVERRIDE THE CREATE & DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected CRUDResult<AA14Schedule> doCreateOrUpdateEntity(final SecurityContext securityContext,
												   			  final AA14Schedule sch,
												   			  final PersistenceRequestedOperation requestedOp,
												   			  final ListensToDBEntityPersistenceEvents<AA14Schedule,AA14DBEntityForSchedule> singleUseDBEntityPersistenceEventListener) {
		// Create the schedule
		CRUDResult<AA14Schedule> outCRUDResult = super.doCreateOrUpdateEntity(securityContext,
															  				  sch,
															  				  requestedOp,
															  				  singleUseDBEntityPersistenceEventListener);
		// manually link the schedule to every location
		if (outCRUDResult.hasSucceeded()
		 && CollectionUtils.hasData(sch.getServiceLocationsRefs())) {
			// get the created schedule
			AA14Schedule createdSch = outCRUDResult.getOrThrow();
			
			log.debug("Linking schedule with oid={} to locations={}",
					  createdSch.getOid(),createdSch.getServiceLocationsOids());
			for (AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,
												  AA14OrgDivisionServiceLocationID> ref : createdSch.getServiceLocationsRefs()) {
				
				AA14DBEntityForOrgDivisionServiceLocation dbLoc = _entityManager.find(AA14DBEntityForOrgDivisionServiceLocation.class,
																				      DBPrimaryKeyForModelObjectImpl.from(ref.getOid()));
				if (dbLoc == null) {
					log.error("Cannot link the schedule with oid={} to the service location with oid={}: the location does NOT exists!!!",
							  createdSch.getOid(),ref.getOid());
					continue;	// skip
				}
				// add the reference to the location
				// (manually managing the many to many relation)
				AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedulePrimaryKey manyToManyPK = AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedulePrimaryKey.from(dbLoc.getOid(),createdSch.getOid().asString());
				AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedule manyToMany = _entityManager.find(AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedule.class,
																													   manyToManyPK);
				if (manyToMany == null) {
					manyToMany = new AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedule(dbLoc.getOid(),createdSch.getOid().asString());
					_entityManager.persist(manyToMany);
				}
				
				// BEWARE!!	schedule <-> location it's a MANY-TO-MANY relation so the location descriptor 
				//			also contains a reference to the schedule that MUST be updated
				AA14OrgDivisionServiceLocation loc = _modelObjectsMarshaller.forReading().fromXml(dbLoc.getDescriptor(),
																								  AA14OrgDivisionServiceLocation.class);
				loc.addScheduleRef(sch.getReference());
				dbLoc.setDescriptor(_modelObjectsMarshaller.forWriting().toXml(loc));
				_entityManager.persist(dbLoc);
				_entityManager.flush();
				
				// setting the schedule dependent service location objs, also modifies the later since it's a BI-DIRECTIONAL relation
				// ... so the entity manager MUST be refreshed in order to avoid an optimistic locking exception
				_entityManager.refresh(dbLoc);
			}
		}
		return outCRUDResult;
	}
	@Override
	public void completeDBEntityBeforeCreateOrUpdate(final SecurityContext securityContext,
													 final PersistencePerformedOperation performedOp, 
													 final AA14Schedule modelOjb, 
													 final AA14DBEntityForSchedule dbEntity) {
		// nothing
	}
	@Override
	protected CRUDResult<AA14Schedule> doDelete(final SecurityContext securityContext, 
												final AA14ScheduleOID oid) {
		// delete the schedule
		CRUDResult<AA14Schedule> outCRUDResult = super.doDelete(securityContext,
																oid);
		// manually link the schedule to every location
		if (outCRUDResult.hasSucceeded()) {
			// Get the deleted schedule
			AA14Schedule sch = outCRUDResult.getOrThrow();
			
			if (CollectionUtils.hasData(sch.getServiceLocationsRefs())) {
				log.debug("un-linking schedule with oid={} from locations={}",
						  sch.getOid(),sch.getServiceLocationsOids());
				for (AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,
													  AA14OrgDivisionServiceLocationID> ref : sch.getServiceLocationsRefs()) {
					AA14DBEntityForOrgDivisionServiceLocation dbLoc = _entityManager.find(AA14DBEntityForOrgDivisionServiceLocation.class,
																					      DBPrimaryKeyForModelObjectImpl.from(ref.getOid()));
					if (dbLoc == null) {
						log.error("Cannot un-link the schedule with oid={} from the service location with oid={}: the location does NOT exists!!!",
								  sch.getOid(),ref.getOid());
						continue;	// skip
					}
					// remove the reference to the location
					// (manually managing the many to many relation)
					AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedulePrimaryKey manyToManyPK = AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedulePrimaryKey.from(dbLoc.getOid(),sch.getOid().asString());
					AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedule manyToMany = _entityManager.find(AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedule.class,
																														   manyToManyPK);
					if (manyToMany != null) {
						_entityManager.remove(manyToMany);
					}
					
					// BEWARE!!	schedule <-> location it's a MANY-TO-MANY relation so the location descriptor 
					//			also contains a reference to the schedule that MUST be updated
					AA14OrgDivisionServiceLocation loc = _modelObjectsMarshaller.forReading().fromXml(dbLoc.getDescriptor(),
																									  AA14OrgDivisionServiceLocation.class);
					loc.removeScheduleRef(sch.getOid());
					dbLoc.setDescriptor(_modelObjectsMarshaller.forWriting().toXml(loc));
					_entityManager.persist(dbLoc);
					_entityManager.flush();
					
					// setting the schedule dependent service location objs, also modifies the later since it's a BI-DIRECTIONAL relation
					// ... so the entity manager MUST be refreshed in order to avoid an optimistic locking exception
					_entityManager.refresh(dbLoc);
				}
			}
		}
		return outCRUDResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDResult<AA14Schedule> linkScheduleToServiceLocations(final SecurityContext securityContext,
																   final AA14ScheduleOID schOid,final Collection<AA14OrgDivisionServiceLocationOID> locOids) {
		// [1] - Load the schedule db entity
		AA14DBEntityForSchedule schDBEntity = _entityManager.find(AA14DBEntityForSchedule.class,
																  DBPrimaryKeyForModelObjectImpl.from(schOid));
		if (schDBEntity == null) {
			return CRUDResultBuilder.using(securityContext)
									.on(AA14Schedule.class)
									.not(PersistenceRequestedOperation.UPDATE)
									.becauseClientRequestedEntityWasNOTFound()
									.about(schOid)
									.build();
		}
		AA14Schedule sch = this.dbEntityToModelObject(securityContext,
												     schDBEntity); 		
				
		// [2] - Link the schedule with every location
		sch.setServiceLocationsRefs(_serviceLocationRefsFor(securityContext,
															   locOids));
		// [3] - Update the schedule (see completeDBEntityBeforeCreateOrUpdate() method)
		CRUDResult<AA14Schedule> outCRUDResult = this.update(securityContext, 
															 sch);
		return outCRUDResult;
	}
	private Collection<AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,
														AA14OrgDivisionServiceLocationID>> _serviceLocationRefsFor(final SecurityContext securityContext,
																												   final Collection<AA14OrgDivisionServiceLocationOID> locOids) {
		Collection<AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,
													AA14OrgDivisionServiceLocationID>> outLocRefs = Lists.newArrayListWithExpectedSize(locOids.size());
		for (AA14OrgDivisionServiceLocationOID locOid : locOids) {
			AA14DBEntityForOrgDivisionServiceLocation locDBEntity = _entityManager.find(AA14DBEntityForOrgDivisionServiceLocation.class,
																						DBPrimaryKeyForModelObjectImpl.from(locOid));
			if (locDBEntity == null) {
				log.error("Cannot find the service location with oid={}: the location does NOT exists!!!",
						  locOid);
				continue;	// skip
			}
			outLocRefs.add(new AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,
																AA14OrgDivisionServiceLocationID>(AA14OrgDivisionServiceLocationOID.forId(locDBEntity.getOid()),
																								  AA14OrgDivisionServiceLocationID.forId(locDBEntity.getId())));
		}
		return outLocRefs;
	}
}
