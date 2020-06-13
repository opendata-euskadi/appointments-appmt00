package aa14b.db.crud;

import java.util.Collection;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;

import aa14b.db.entities.AA14DBEntityForAppointment;
import aa14b.db.entities.AA14DBEntityForBookedSlotBase;
import aa14b.db.entities.AA14DBEntityForNonBookableSlot;
import aa14b.db.entities.AA14DBEntityForOrgDivisionServiceLocation;
import aa14b.db.entities.AA14DBEntityForSchedule;
import aa14b.services.delegates.persistence.AA14CRUDServicesDelegateForBookedSlot;
import aa14f.api.interfaces.AA14CRUDServicesForBookedSlot;
import aa14f.model.AA14Appointment;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14NonBookableSlot;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14PeriodicSlotSerieOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.persistence.CRUDOnMultipleResult;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.PersistencePerformedOperation;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObjectImpl;
import r01f.securitycontext.SecurityContext;
import r01f.types.contact.EMail;
import r01f.util.types.Dates;

/**
 * Persistence layer
 */
public class AA14DBCRUDForBookedSlot
	 extends AA14DBCRUDBase<AA14SlotOID,AA14SlotID,AA14BookedSlot,
	 				   		AA14DBEntityForBookedSlotBase> 
  implements AA14CRUDServicesForBookedSlot {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBCRUDForBookedSlot(final DBModuleConfig dbCfg,
								   final EntityManager entityManager,
								   final Marshaller marshaller) {
		super(dbCfg,
			  AA14BookedSlot.class,AA14DBEntityForBookedSlotBase.class,
			  entityManager,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CRUDOnMultipleResult<AA14BookedSlot> createPeriodicNonBookableSlots(final SecurityContext securityContext,
											  								   final AA14ScheduleOID schOid,
											  								   final Date startDate,final Date endDate,
											  								   final DateTime timeStartNonBookable,final DateTime timeEndNonBookable,
											  								   final boolean sunday,final boolean monday,final boolean tuesday,final boolean wednesday,final boolean thursday,final boolean friday,final boolean saturday,
											  								   final String nonBookableSubject,
											  								   final UserCode userCode) {
		throw new UnsupportedOperationException("Implemented at " + AA14CRUDServicesDelegateForBookedSlot.class);
	}
	@Override
	public CRUDOnMultipleResult<AA14BookedSlot> deletePeriodicNonBookableSlots(final SecurityContext securityContext,
																			   final AA14PeriodicSlotSerieOID serieOid) {
		throw new UnsupportedOperationException("Implemented at " + AA14CRUDServicesDelegateForBookedSlot.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected AA14DBEntityForBookedSlotBase createDBEntityInstanceFor(final AA14BookedSlot modelObj) {
		// since the model object type is an interface whose instances get persisted 
		// as DBAppointment or DBNonBookableSlot depending on the "real" type, 
		// the db entity type instance to be created depends on the model object type
		AA14DBEntityForBookedSlotBase outEntity = null;
		if (modelObj.getClass().equals(AA14Appointment.class)) {
			outEntity = new AA14DBEntityForAppointment();
		} else if (modelObj.getClass().equals(AA14NonBookableSlot.class)) {
			outEntity = new AA14DBEntityForNonBookableSlot();
		} else {
			throw new IllegalArgumentException();
		}
		return outEntity;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setDBEntityFieldsFromModelObject(final SecurityContext securityContext, 
												 final AA14BookedSlot slot,final AA14DBEntityForBookedSlotBase dbSlot) {
		// oid
		dbSlot.setOid(slot.getOid().asString());
		dbSlot.setId(slot.getId().asString());
		
		// type
		dbSlot.setType(slot.getType());
		
		// date
		dbSlot.setYear(slot.getYear().asInteger());
		dbSlot.setMonthOfYear(slot.getMonthOfYear().asInteger());
		dbSlot.setDayOfMonth(slot.getDayOfMonth().asInteger());
		dbSlot.setHourOfDay(slot.getHourOfDay().asInteger());
		dbSlot.setMinuteOfHour(slot.getMinuteOfHour().asInteger());
		
		dbSlot.setDurationInMinutes(slot.getDurationMinutes());
		
		dbSlot.setStartDate(Dates.asCalendar(slot.getStartDate()));
		dbSlot.setEndDate(Dates.asCalendar(slot.getEndDate()));	
		
		// type-specific data transfer
		if (slot instanceof AA14Appointment) {
			AA14Appointment appointment = (AA14Appointment)slot;
			AA14DBEntityForAppointment dbAppointment = (AA14DBEntityForAppointment)dbSlot;
			
			// person
			dbAppointment.setPersonId(appointment.getPerson().getId() != null ? appointment.getPerson().getId().asString() 
																		 	  : null);
			EMail contactEMail = appointment.getPersonLocatorEMail();
			dbAppointment.setContactEMail(contactEMail != null ? contactEMail.asString() : null);
			dbAppointment.setPersonLocatorId(appointment.getPersonLocatorId() != null ? appointment.getPersonLocatorId().asString()
																				  	  : null);
			dbAppointment.setPersonSummary(appointment.getPerson().asSummarizable()
														   	 .getSummary().asString());
			dbAppointment.setSubjectId(appointment.getSubject().getId().asString());			
		} 
		else if (slot instanceof AA14NonBookableSlot) {
			AA14NonBookableSlot nonBookableSlot = (AA14NonBookableSlot)slot;
			AA14DBEntityForNonBookableSlot dbNonBookableSlot = (AA14DBEntityForNonBookableSlot)dbSlot;
			
			// serie oid if periodic
			if (nonBookableSlot.isPeriodic()) dbNonBookableSlot.setPeriodicSlotSerieOid(nonBookableSlot.getPeriodicSlotData()
																									   .getSerieOid().asString());
			
			// user
			dbNonBookableSlot.setUserCode(nonBookableSlot.getUserCode() != null ? nonBookableSlot.getUserCode().asString() : null);
			dbNonBookableSlot.setSubject(nonBookableSlot.getSubject());	
		}
		
		// Descriptor 
		// BEWARE!! The descriptor will NOT contain the fiels completed at completeDBEntityBeforeCreateOrUpdate() method
		dbSlot.setDescriptor(_modelObjectsMarshaller.forWriting()
													.toXml(slot));
	}
	@Override
	public AA14BookedSlot dbEntityToModelObject(final SecurityContext securityContext, 
								   				final AA14DBEntityForBookedSlotBase dbSlot) {
		// a bit tricky: call the default transformer
		AA14BookedSlot outSlot = super.dbEntityToModelObject(securityContext,
														     dbSlot);
		// here the returned model object can be completed with fields data NOT present at the descriptor!
		// the descriptor DOES NOT have some fields values since they're set only at the db entity
		// ... so these values must be set by hand
		if (dbSlot.getOrgDivisionServiceLocationOid() != null) outSlot.setOrgDivisionServiceLocationOid(AA14OrgDivisionServiceLocationOID.forId(dbSlot.getOrgDivisionServiceLocationOid()));
		
		return outSlot;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDResult<AA14BookedSlot> loadById(final SecurityContext securityContext,
								  							  final AA14SlotID id) {
		// Do the query
		String namedQueryName = "AA14DBEntityForSlotById";
		TypedQuery<AA14DBEntityForBookedSlotBase> query = this.getEntityManager()
															  .createNamedQuery(namedQueryName,
																	  		    AA14DBEntityForBookedSlotBase.class)
															  .setParameter("id",id.asString());
		Collection<AA14DBEntityForBookedSlotBase> dbEntities = query.getResultList();

		// Return
		CRUDResult<AA14BookedSlot> outResult = _crudResultForSingleEntity(securityContext,
														     			  id,
														     			  dbEntities);
		return outResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void completeDBEntityBeforeCreateOrUpdate(final SecurityContext securityContext,
													 final PersistencePerformedOperation performedOp,
													 final AA14BookedSlot bookedSlot,final AA14DBEntityForBookedSlotBase dbEntity) {
		// load the location 
		AA14DBEntityForOrgDivisionServiceLocation dbLoc = bookedSlot.getOrgDivisionServiceLocationOid() != null
																?  this.getEntityManager()
																	   .find(AA14DBEntityForOrgDivisionServiceLocation.class,
																			 new DBPrimaryKeyForModelObjectImpl(bookedSlot.getOrgDivisionServiceLocationOid()
																										  					.asString()))
																: null;
		// load the schedule
		AA14DBEntityForSchedule dbSch = this.getEntityManager()
												.find(AA14DBEntityForSchedule.class,
													  new DBPrimaryKeyForModelObjectImpl(bookedSlot.getScheduleOid()
															  									   .asString()));
		
		// the location might be null when the booked slot is a non-bookable slot
		// (remember that the non-bookable slots are linked to an schedule BUT not to any location service)
		if (dbLoc != null) {
			// set the oids
			dbEntity.setOrganizationOid(dbLoc.getOrganizationOid());
			dbEntity.setOrgDivisionOid(dbLoc.getOrgDivisionOid());
			dbEntity.setOrgDivisionServiceOid(dbLoc.getOrgDivisionServiceOid());
			dbEntity.setOrgDivisionServiceLocationOid(dbLoc.getOid());

			// set the ids
			dbEntity.setOrganizationId(dbLoc.getOrganizationId());
			dbEntity.setOrgDivisionId(dbLoc.getOrgDivisionId());
			dbEntity.setOrgDivisionServiceId(dbLoc.getOrgDivisionServiceId());
			dbEntity.setOrgDivisionServiceLocationId(dbLoc.getId());
			
//			dbEntity.setLocation(dbLoc);
		}
		
		// set the schedule (mandatory in all cases)
		dbEntity.setScheduleOid(dbSch.getOid());
		dbEntity.setScheduleId(dbSch.getId());
		
		// set the db entities dependency
//		dbEntity.setSchedule(dbSch);
		
		// setting the appointment's dependent objects (location), COULD also modifies the later if it's a BI-DIRECTIONAL relation
		// ... so the entity manager MUST be refreshed in order to avoid an optimistic locking exception
		//this.getEntityManager().refresh(dbLoc);
	}
}
