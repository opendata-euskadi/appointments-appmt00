package aa14b.services.delegates.persistence;

import java.util.Collection;
import java.util.Date;

import javax.persistence.EntityManager;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;

import aa14b.calendar.AA14CalendarService;
import aa14b.calendar.AA14CalendarServiceException;
import aa14b.db.crud.AA14DBCRUDForBookedSlot;
import aa14b.db.find.AA14DBFindForBookedSlot;
import aa14b.services.internal.AA14SlotOverlappingValidatorService;
import aa14f.api.interfaces.AA14CRUDServicesForBookedSlot;
import aa14f.api.interfaces.AA14CRUDServicesForOrgDivisionServiceLocation;
import aa14f.api.interfaces.AA14CRUDServicesForSchedule;
import aa14f.api.interfaces.AA14FindServicesForBookedSlot;
import aa14f.api.interfaces.AA14PersonLocatorServices;
import aa14f.model.AA14Appointment;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14BookedSlotValidators;
import aa14f.model.AA14NonBookableSlot;
import aa14f.model.AA14PeriodicSlotData;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14PersonLocatorID;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.oids.AA14OIDs.AA14PeriodicSlotSerieOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.exceptions.ExceptionSeverity;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.persistence.CRUDError;
import r01f.model.persistence.CRUDOnMultipleResult;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.model.persistence.PersistenceOperationExecError;
import r01f.model.persistence.PersistenceOperationExecResultBuilder;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.model.services.COREServiceErrorOrigin;
import r01f.model.services.COREServiceErrorType;
import r01f.model.services.COREServiceMethod;
import r01f.model.services.COREServiceMethodExecError;
import r01f.model.services.COREServiceMethodExecResult;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfigBuilder;
import r01f.securitycontext.SecurityContext;
import r01f.services.callback.spec.COREServiceMethodCallbackSpec;
import r01f.types.Range;
import r01f.types.contact.EMail;
import r01f.types.contact.PersonID;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;
import r01f.util.types.collections.CollectionUtils;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;

/**
 * Service layer delegated type for CRUD (Create/Read/Update/Delete) operations
 */
@Slf4j
public class AA14CRUDServicesDelegateForBookedSlot
	 extends AA14CRUDServicesDelegateBase<AA14SlotOID,AA14SlotID,AA14BookedSlot>
  implements AA14CRUDServicesForBookedSlot {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14PersonLocatorServices _personLocatorServices;
	private final AA14CalendarService _calendarService;
	private final AA14CRUDServicesForOrgDivisionServiceLocation _locCRUD;
	private final AA14CRUDServicesForSchedule _schCRUD;
	private final AA14FindServicesForBookedSlot _slotFind;
	private final AA14SlotOverlappingValidatorService _slotOverlappingValidator;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14CRUDServicesDelegateForBookedSlot(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
												 final EntityManager entityManager,
										     	 final Marshaller marshaller,
			  			   		   	   	     	 final EventBus eventBus,
			  			   		   	   	     	 final AA14PersonLocatorServices personLocatorServices,
												 final AA14CalendarService calendarService,
												 final AA14SlotOverlappingValidatorService slotOverlappintValidatorService) {
		super(coreCfg,
			  AA14BookedSlot.class,
			  new AA14DBCRUDForBookedSlot(DBModuleConfigBuilder.dbModuleConfigFrom(coreCfg),
					  					  entityManager,
					  					  marshaller),
			  eventBus);
		_personLocatorServices = personLocatorServices;
		_calendarService = calendarService;
		_locCRUD = new AA14CRUDServicesDelegateForOrgDivisionServiceLocation(coreCfg,
																			 entityManager,
																			 marshaller,
																			 eventBus);
		_schCRUD = new AA14CRUDServicesDelegateForSchedule(coreCfg,
														   entityManager,
														   marshaller,
														   eventBus);
		_slotFind = new AA14DBFindForBookedSlot(DBModuleConfigBuilder.dbModuleConfigFrom(coreCfg),
												entityManager,
												marshaller);
		_slotOverlappingValidator = slotOverlappintValidatorService;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CREATE OVERRIDE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDOnMultipleResult<AA14BookedSlot> createPeriodicNonBookableSlots(final SecurityContext securityContext,
											  								   final AA14ScheduleOID schOid,
											  								   final Date startDate,final Date endDate,
											  								   final DateTime timeStartNonBookable,final DateTime timeEndNonBookable,
											  								   final boolean sunday,final boolean monday,final boolean tuesday,final boolean wednesday,final boolean thursday,final boolean friday,final boolean saturday,
											  								   final String nonBookableSubject,
											  								   final UserCode userCode) {
		// a bit of logging
		StringBuilder weekDayDbg = new StringBuilder();
		if (monday) weekDayDbg.append("[Mon]");
		if (tuesday) weekDayDbg.append("[Tu]");
		if (wednesday) weekDayDbg.append("[Wed]");
		if (thursday) weekDayDbg.append("[Th]");
		if (friday) weekDayDbg.append("[Fri]");
		log.info("... creating a periodic appointment in range={} - {} every {} starting at {} and ending at {}",
				 timeStartNonBookable,timeEndNonBookable,
				 weekDayDbg,
				 startDate,endDate);
		
		// Out result
		CRUDOnMultipleResult<AA14BookedSlot> outMultiResult = new CRUDOnMultipleResult<AA14BookedSlot>(AA14BookedSlot.class,
																									   PersistenceRequestedOperation.CREATE);
		
		// Iterate over the days creating the non-bookable slots		
		int numCreatedSlots = 0;
		AA14PeriodicSlotData periodicData = new AA14PeriodicSlotData(AA14PeriodicSlotSerieOID.supply(),
																     Range.closed(startDate,endDate), 
																     sunday,monday,tuesday,wednesday,thursday,friday,saturday);
		LocalDate start = new LocalDate(startDate);
		LocalDate end = new LocalDate(endDate);
		for (LocalDate date = start; date.isBefore(end) || date.isEqual(end); date = date.plusDays(1)) {
			if ((date.getDayOfWeek() == DateTimeConstants.SUNDAY && sunday)
			 ||	(date.getDayOfWeek() == DateTimeConstants.MONDAY && monday)
			 || (date.getDayOfWeek() == DateTimeConstants.TUESDAY && tuesday)
			 || (date.getDayOfWeek() == DateTimeConstants.WEDNESDAY && wednesday)
			 || (date.getDayOfWeek() == DateTimeConstants.THURSDAY && thursday)
			 || (date.getDayOfWeek() == DateTimeConstants.FRIDAY && friday)
			 || (date.getDayOfWeek() == DateTimeConstants.SATURDAY && saturday)) {
				// Create the new slot with a serie number
				AA14NonBookableSlot slot = new AA14NonBookableSlot();
				slot.setOid(AA14SlotOID.supply());
				slot.setPeriodicSlotData(periodicData);		// periodic data
				
				// schedule & location (mandatory)
				slot.setScheduleOid(schOid);
				slot.setOrgDivisionServiceLocationOid(null);	// any location!!	AA14OrgDivisionServiceLocationOID.ANY
				
				// Date
				slot.setYear(Year.of(date));
				slot.setMonthOfYear(MonthOfYear.of(date));		// 2016/Dec/26 error in MonthOfYear (do not have to add +1)
				slot.setDayOfMonth(DayOfMonth.of(date));
				slot.setHourOfDay(HourOfDay.of(timeStartNonBookable));
				slot.setMinuteOfHour(MinuteOfHour.of(timeStartNonBookable));
				slot.setDurationMinutes(Minutes.minutesBetween(timeStartNonBookable,
															   timeEndNonBookable)
											   .getMinutes());
				// Subject
				slot.setSubject(nonBookableSubject);
				slot.setUserCode(userCode);
								
				// save
				log.debug("\t\t-creating slot for {}",slot.getDateRange());
				CRUDResult<AA14BookedSlot> slotCreateResult = this.create(securityContext,
								   										  slot);
				numCreatedSlots++;
				
				outMultiResult.addOperationResult(slotCreateResult);
			}
		}
		log.debug("...{} created non-bookable slots",numCreatedSlots);
		return outMultiResult;
	}
	@Override
	public CRUDResult<AA14BookedSlot> create(final SecurityContext securityContext,
							    			 final AA14BookedSlot slot,
							    			 final COREServiceMethodCallbackSpec callbackSpec) {
		return _createOrUpdate(securityContext,
							   slot,
							   PersistenceRequestedOperation.CREATE,
							   callbackSpec);
	}
	@Override
	public CRUDResult<AA14BookedSlot> update(final SecurityContext securityContext,
											 final AA14BookedSlot slot,
											 final COREServiceMethodCallbackSpec callbackSpec) {
		return _createOrUpdate(securityContext,
							   slot,
							   PersistenceRequestedOperation.UPDATE,
							   callbackSpec);
	}
	@SuppressWarnings("null")
	private CRUDResult<AA14BookedSlot> _createOrUpdate(final SecurityContext securityContext,
							    			 		   final AA14BookedSlot slot,
							    			 		   final PersistenceRequestedOperation reqOp,
							    			 		   final COREServiceMethodCallbackSpec callbackSpec) {
		// BEWARE!!! This method is NOT completely transactional since the underlying calendar is NOT transactional
		
		// [-1]: Validate the data
		//			- is the received slot data valid?
		//			- is the slot available?
		ObjectValidationResult<AA14BookedSlot> valid = this.validateModelObjBeforeCreateOrUpdate(securityContext, 
																								 reqOp,
																								 slot);
		if (valid.isNOTValid()) return CRUDResultBuilder.using(securityContext)
														.on(AA14BookedSlot.class)
														.not(reqOp)
														.becauseClientBadRequest(valid.asNOKValidationResult())
														.build();
		if (!_slotOverlappingValidator.isSlotAvailable(securityContext,
													   slot)) {
			return CRUDResultBuilder.using(securityContext)
									.on(AA14BookedSlot.class)
									.not(reqOp)
									.becauseClientError(COREServiceErrorType.originatedAt(COREServiceErrorOrigin.SERVER)
																			.withName("entity already exists")
																			.noCodes()
																			.severity(ExceptionSeverity.RECOVERABLE)
																			.build(),
														"The requested slot range {}-{} in schedule oid={} is NOT available at {}",
														slot.getStartTime(),slot.getEndTime(),
														slot.getScheduleOid(),
														slot.getStartDate())
									.about(slot)
									.buildWithExtendedErrorCode(1);
		}
		
		// [0]: Load the location & schedule data
		// Create a new AA14OrgDivisionServiceLocation delegate to load the full AA14OrgDivisionServiceLocation
		AA14OrgDivisionServiceLocation loc = slot.getOrgDivisionServiceLocationOid() != null
												?  _locCRUD.load(securityContext,
																 slot.getOrgDivisionServiceLocationOid())
														   .getOrThrow()
												: null;
		AA14Schedule sch = _schCRUD.load(securityContext,
										 slot.getScheduleOid())
								   .getOrThrow();
		
		
		// [1]: If it's an appointment (not a booked slot), generate a [person locator] 
		if (slot instanceof AA14Appointment) {
			AA14Appointment appointment = (AA14Appointment)slot;
			
			AA14PersonLocatorID personLocatorId = _personLocatorIdFor(securityContext,
																	  appointment);	
			appointment.setPersonLocatorId(personLocatorId);
		}
		
		
		// [2]: Use the calendar service to create the slot at the underlying calendar (ie: qmatic orchestra calendar)
		AA14SlotID slotId = null;
		if (sch.getOrchestraConfig() != null
		 && sch.getOrchestraConfig().isEnabled()) {
			// [1.1]: QMATIC Orchestra
			COREServiceMethodExecResult<AA14SlotID> orchestraCreateOrUpdateResult = _createOrUpdateQMaticOrchestraAppointment(securityContext,
																												 			  loc,sch,
																												 			  slot,
																												 			  reqOp);
			if (orchestraCreateOrUpdateResult.hasFailed()) {			
				// return an error
				COREServiceMethodExecError<AA14SlotID> qmaticPersistErr = orchestraCreateOrUpdateResult.asCOREServiceMethodExecError();
				if (qmaticPersistErr.getErrorCode() == AA14CalendarServiceException.SLOT_OCCUPIED_EXT_ERROR_CODE) {
					return CRUDResultBuilder.using(securityContext)
											.on(_modelObjectType)
											.notCreated()
												.becauseClientRequestedEntityAlreadyExists()
											    .about(slot)
											    .buildWithExtendedErrorCode(AA14CalendarServiceException.SLOT_OCCUPIED_EXT_ERROR_CODE);
				}
				return CRUDResultBuilder.using(securityContext)
										.on(_modelObjectType)
										.not(reqOp)
										.because(qmaticPersistErr.getError())
										.build();
			}
			// get the calendar-returned id
			slotId = orchestraCreateOrUpdateResult.getOrThrow();
		} else {
			// [1.2]: NOT using QMATIC Orchestra: generate an id with the same id as the slot oid
			slotId = AA14SlotID.forId(slot.getOid().asString());
		}
		
		
		// [3]: Set the slot id returned by the calendar
		slot.setId(slotId);
		
		
		// [4]: Persist the slot at the DB
		CRUDResult<AA14BookedSlot> outResult = null;
		if (reqOp.is(PersistenceRequestedOperation.CREATE)) {
			outResult = super.create(securityContext,
					 				 slot,				// creates the slot at the DB
					 				 callbackSpec);
		} else if (reqOp.is(PersistenceRequestedOperation.UPDATE)) {
			outResult = super.update(securityContext,
									 slot,				// updates the slot at the DB
									 callbackSpec);
		}
		
		if (outResult.hasFailed() 
		 && sch.getOrchestraConfig() != null		
		 && sch.getOrchestraConfig().isEnabled()) {
			// try to restore the state removing the slot from the calendar
			CRUDError<AA14BookedSlot> error = outResult.asCRUDError();
			log.error("An error was raised while trying to persist the slot in the DB: {}",error.getErrorMessage(),
																						   error.getError());

			COREServiceMethodExecResult<Boolean> calendarSlotReleaseResult = _calendarService.releaseSlot(securityContext,
																										  loc,sch,
																										  slotId);
			boolean calendarStateRestored = calendarSlotReleaseResult.hasSucceeded();
			if (!calendarStateRestored) {
				log.error("The slot with id={} (oid={}) could NOT be persisted to the database and the previously created calendar slot reservation could NOT be removed: The calendar/DB are unsynchronized since the slot reservation is NOT at the DB while remains at the calendar", 
						   slot.getId(),slot.getOid(),
						   calendarSlotReleaseResult.as(PersistenceOperationExecError.class)
						   								  .getError());
			}
		}
		return outResult;
	}
	private AA14PersonLocatorID _personLocatorIdFor(final SecurityContext securityContext,
													final AA14Appointment appointment) {
		// get the person id & contact mean (email)
		PersonID personId = appointment.getPerson() != null
						 		? appointment.getPerson().getId()
						 		: null;
		EMail email = appointment.getPersonLocatorEMail();
						 		
		if (personId == null || email == null) {
			log.info("...NOT creating a [person locator] since the [appointment]'s [person] info or [contact] info is NOT enough");
			return null;
			
		}
		AA14PersonLocatorID outLocator = null;
		
		// see if there exists a [person locator] for the received [person]
		COREServiceMethodExecResult<AA14PersonLocatorID> personLocatorIdFind = _personLocatorServices.findPersonLocatorFor(securityContext,
																														   personId,email);
		if (personLocatorIdFind.hasFailed()) {
			Throwable th = personLocatorIdFind.asCOREServiceMethodExecError()
											  .getError();
			log.error("... something was wrong while trying to set the [appointment]'s [person locator]: {}",
					  th.getMessage(),th);
			return appointment.getPersonLocatorId();	// try to return something
		}
		
		// compute the locator
		AA14PersonLocatorID prevLocatorId = personLocatorIdFind.getOrThrow();
		log.info("... previous [person locator] for personId={} > {}",
				 appointment.getPerson().getId(),prevLocatorId);
		
		if (prevLocatorId != null) {
			// use the prev locator (ignore any received locator)
			outLocator = prevLocatorId;
			log.info("...previous [preson locator] for personId={} is {}: re-use that one",
					 appointment.getPerson().getId(),outLocator);
		}
		else if (appointment.getPersonLocatorId() == null) {
			// create a new locator
			outLocator = AA14PersonLocatorID.supplyFor(appointment.getPerson().getId());
			log.info("... previous [preson locator] for personId={} is null and also is the received one: create a NEW one: {}",
					 appointment.getPerson().getId(),outLocator);
		}
		else if (appointment.getPersonLocatorId() != null) {			
			// use the received locator
			outLocator = appointment.getPersonLocatorId();
			log.info("...previous [preson locator] for personId={} is null: use the received one: {}",
					 appointment.getPerson().getId(),outLocator);
		}
		return outLocator;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DELETE OVERRIDE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDOnMultipleResult<AA14BookedSlot> deletePeriodicNonBookableSlots(final SecurityContext securityContext,
																			   final AA14PeriodicSlotSerieOID serieOid) {
		// [1] - Find all serie-related slots
		Collection<AA14SlotOID> serieSlotsOids = _slotFind.findNonBookablePeriodicSlotsOids(securityContext,
																					   		serieOid)
																.getOrThrow();
		log.info("...{} slots belonging to serie with oid={} will be deleted: {}",
				serieSlotsOids.size(),serieOid,serieSlotsOids);
		Collection<CRUDResult<AA14BookedSlot>> serieSlotsDelResults = Lists.newArrayListWithExpectedSize(serieSlotsOids.size());
		if (CollectionUtils.hasData(serieSlotsOids)) {
			for (AA14SlotOID serieSlotOid : serieSlotsOids) {
				CRUDResult<AA14BookedSlot> serieSlotDelResult = this.delete(securityContext,
																			serieSlotOid);
				serieSlotsDelResults.add(serieSlotDelResult);
			}
		}
		CRUDOnMultipleResult<AA14BookedSlot> outMultiResult = CRUDResultBuilder.using(securityContext)
																			   .onMultiple(AA14BookedSlot.class)
																			   .executed(PersistenceRequestedOperation.DELETE,
																					   	 serieSlotsDelResults);
		return outMultiResult;
	}
	@Override
	public CRUDResult<AA14BookedSlot> delete(final SecurityContext securityContext,
							    			 final AA14SlotOID oid,
							    			 final COREServiceMethodCallbackSpec callbackSpec) {
		// BEWARE!!! This method is NOT transactional since the underlying calendar is NOT transactional
		
		// [0]
		// [0.1]: Load the slot data just to get the slot id
		AA14BookedSlot slot = this.load(securityContext,
						   				oid)
					 			  .getOrThrow();
		
		
		// [0.2]: Load the location & schedule data
		// Create a new AA14OrgDivisionServiceLocation delegate to load the full AA14OrgDivisionServiceLocation
		AA14OrgDivisionServiceLocation loc = slot.getOrgDivisionServiceLocationOid() != null ? _locCRUD.load(securityContext,
														   													 slot.getOrgDivisionServiceLocationOid())
												    													.getOrThrow()
												    										 : null;
		AA14Schedule sch = _schCRUD.load(securityContext,
										 slot.getScheduleOid())
								   .getOrThrow();
		
		// [1]: Use the calendar service to delete the slot at the underlying calendar (ie: qmatic orchestra calendar)
		if (sch.getOrchestraConfig() != null
		 && sch.getOrchestraConfig().isEnabled()) {
			COREServiceMethodExecResult<Boolean> calendarSlotCancelResult = _calendarService.releaseSlot(securityContext,
														     										     loc,sch,
														     											 slot.getId());
			if (calendarSlotCancelResult.hasFailed()) {
				AA14CalendarServiceException calendarEx = (AA14CalendarServiceException)calendarSlotCancelResult.as(PersistenceOperationExecError.class)
																																			 .getError();
				log.error("An error was raised while trying to release the slot reservation with oid={} and id={} in the calendar: {}",
						  slot.getOid(),slot.getId(),calendarEx.getMessage(),
						  calendarEx);
				return CRUDResultBuilder.using(securityContext)
										.on(_modelObjectType)
										.notDeleted()
											.because(calendarEx)
											.about(slot.getOid())
											.build();
			}
			// OK
			boolean deleted = calendarSlotCancelResult.getOrThrow();
			if (!deleted) {
				log.error("Could NOT cancel slot with oid={} and id={} in the calendar",
						  slot.getOid(),slot.getId());	
				return CRUDResultBuilder.using(securityContext)
										.on(_modelObjectType)
										.notDeleted()
											.becauseServerError("unknown")
											.about(slot.getOid())
											.build();
			}
		}
		// [2]: Remove the slot reservation at the DB
		CRUDResult<AA14BookedSlot> outResult = super.delete(securityContext,
					 			 			   				slot.getOid(),		// removes the slot reservation from the DB
					 			 			   				callbackSpec);
		if (outResult.hasFailed()) {
			CRUDError<AA14BookedSlot> error = outResult.asCRUDError();
			log.error("An error was raised while trying to delete an slot with oid={} (id={}) from the DB: The calendar/DB are unsynchronized since the slot reservation is NOT in the calendar while remains at the DB",
					  slot.getOid(),slot.getId(),
					  error.getError());
		}
		return outResult;
		
	}
	private COREServiceMethodExecResult<AA14SlotID> _createOrUpdateQMaticOrchestraAppointment(final SecurityContext securityContext,
																							  final AA14OrgDivisionServiceLocation loc,final AA14Schedule sch,
																							  final AA14BookedSlot slot,
																							  final PersistenceRequestedOperation reqOp) {	
		COREServiceMethodExecResult<AA14SlotID> calendarSlotPersistResult = null;
		if (reqOp.is(PersistenceRequestedOperation.CREATE)) {
			calendarSlotPersistResult = _calendarService.reserveSlot(securityContext,
														 			 loc,sch,
														 			 slot);
			if (calendarSlotPersistResult.hasFailed() 
			 && calendarSlotPersistResult.asCOREServiceMethodExecError().getErrorCode() == AA14CalendarServiceException.SLOT_OCCUPIED_EXT_ERROR_CODE
			 && calendarSlotPersistResult.asCOREServiceMethodExecError().getErrorAs(AA14CalendarServiceException.class)
			 											 				.wasBecauseSlotIsOccupied()) {
				// the slot is occupied...
				log.warn("The calendar slot at {}/{}/{}-{}:{} during {} is occupied; the user should retry another slot",
						 slot.getYear(),slot.getMonthOfYear(),slot.getDayOfMonth(),
						 slot.getHourOfDay(),slot.getMinuteOfHour(),
						 slot.getDurationMinutes());
				
				// return an error
				PersistenceOperationExecError<AA14SlotID> outPersistExResult = PersistenceOperationExecResultBuilder.using(securityContext)
																							.notExecuted(COREServiceMethod.named("createOrUpdateQMaticOrchestraAppointment"))
																							.becauseClientBadRequest("entity already exists");
				outPersistExResult.setErrorCode(AA14CalendarServiceException.SLOT_OCCUPIED_EXT_ERROR_CODE);
				return outPersistExResult;
			}
		}		
		else if (reqOp.is(PersistenceRequestedOperation.UPDATE))  {
			// update
			calendarSlotPersistResult = _calendarService.updateSlot(securityContext,
																	slot);
		}
		return calendarSlotPersistResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PARAMS VALIDATION ON CREATION / UPDATE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override 
	public ObjectValidationResult<AA14BookedSlot> validateModelObjBeforeCreateOrUpdate(final SecurityContext securityContext,
																	 	  			   final PersistenceRequestedOperation requestedOp,
																	 	  			   final AA14BookedSlot slot) {
		// [1]: Do base validations
		ObjectValidationResult<AA14BookedSlot> outValid = AA14BookedSlotValidators.createSlotBaseValidator()
																				  .validate(slot);
		
		// [2]: Ensure the location exists
		if (outValid.isValid()
		 && slot.getOrgDivisionServiceLocationOid() != null) {
			// try to load the location by it's id
			CRUDResult<AA14OrgDivisionServiceLocation> existingLocByIdLoadResult = _locCRUD.load(securityContext,
																			 		  			 slot.getOrgDivisionServiceLocationOid());
			if (existingLocByIdLoadResult.hasFailed()) {
				outValid = ObjectValidationResultBuilder.on(slot)
														 .isNotValidBecause("The {} with oid={} sets an INVALID (not-existing) location oid={}",
																 			slot.getClass().getSimpleName(),slot.getOid(),slot.getOrgDivisionServiceLocationOid());
			}
		}
		
		// [3]: Return
		return outValid;
	}

}
