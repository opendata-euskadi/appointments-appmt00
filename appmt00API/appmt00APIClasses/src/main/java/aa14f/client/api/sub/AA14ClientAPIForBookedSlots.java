package aa14f.client.api.sub;

import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14BookedSlotsCalendarServices;
import aa14f.api.interfaces.AA14CRUDServicesForBookedSlot;
import aa14f.api.interfaces.AA14CRUDServicesForSchedule;
import aa14f.api.interfaces.AA14FindServicesForBookedSlot;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForAppointmentsCalendar;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForBookedSlotCRUDServices;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForBookedSlotFindServices;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14NonBookableSlot;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.UserCode;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.ClientSubAPIBase;
import r01f.services.interfaces.ServiceInterface;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;

/**
 * Client implementation of services maintenance.
 */
@Accessors(prefix="_")
@Slf4j
public class AA14ClientAPIForBookedSlots
     extends ClientSubAPIBase {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final AA14ClientAPIDelegateForBookedSlotCRUDServices _forCRUD;
	@Getter private final AA14ClientAPIDelegateForBookedSlotFindServices _forFind;
	@Getter private final AA14ClientAPIDelegateForAppointmentsCalendar _forCalendar;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("rawtypes")
	public AA14ClientAPIForBookedSlots(final Provider<SecurityContext> securityContextProvider,
										final Marshaller modelObjectsMarshaller,
								  		final Map<Class,ServiceInterface> srvcIfaceMappings) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  srvcIfaceMappings);	// reference to other client apis

		_forCRUD = new AA14ClientAPIDelegateForBookedSlotCRUDServices(securityContextProvider,
																	  modelObjectsMarshaller,
													 			 	  this.getServiceInterfaceCoreImplOrProxy(AA14CRUDServicesForBookedSlot.class));
		_forFind = new AA14ClientAPIDelegateForBookedSlotFindServices(securityContextProvider,
																	  modelObjectsMarshaller,
															  		  this.getServiceInterfaceCoreImplOrProxy(AA14FindServicesForBookedSlot.class));
		_forCalendar = new AA14ClientAPIDelegateForAppointmentsCalendar(securityContextProvider,
																	    modelObjectsMarshaller,
																		this.getServiceInterfaceCoreImplOrProxy(AA14BookedSlotsCalendarServices.class));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a new non-bookable slot or updates an existing one
	 * @param slotOid
	 * @param schId
	 * @param date
	 * @param timeStartNonBookable
	 * @param timeEndNonBookable
	 * @param nonBookableSubject
	 * @param userCode
	 * @return
	 */
	public AA14NonBookableSlot createOrUpdateNonBookableSlot(final AA14SlotOID slotOid,
															 final AA14ScheduleID schId,
															 final Date date,final DateTime timeStartNonBookable,final DateTime timeEndNonBookable,
															 final String nonBookableSubject,
															 final UserCode userCode) {
		// load the schedule
		AA14Schedule schedule = this.getServiceInterfaceCoreImplOrProxy(AA14CRUDServicesForSchedule.class)
								    .loadById(this.getSecurityContext(),
								    		  schId)
								    .getOrThrow();
		
		log.info("Reserve slot at schedule={} at {} from {} to {} for {}",
				 schedule.getId(),date,timeStartNonBookable,timeEndNonBookable,nonBookableSubject);
		
		// Create or update the slot
		int durationInMinutes = Minutes.minutesBetween(timeStartNonBookable,
													   timeEndNonBookable)
									   .getMinutes();
		if (durationInMinutes > 0) {
			// Oid
			AA14NonBookableSlot slot = null;
			if (slotOid == null) {	
				// create a new slot
				slot = new AA14NonBookableSlot();
				slot.setOid(AA14SlotOID.supply());
			} else {
				// try to load the existing slot
				AA14BookedSlot existingSlot = this.getForCRUD()
														.loadOrNull(slotOid);
				if (existingSlot == null) throw new IllegalArgumentException("The slot with oid=" + slotOid + " does NOT exists!!");
				
				slot = existingSlot.as(AA14NonBookableSlot.class);
				slot.setOid(slotOid);
			}
			
			// schedule & location (mandatory)
			slot.setScheduleOid(schedule.getOid());
			slot.setOrgDivisionServiceLocationOid(null);	// any location!!	AA14OrgDivisionServiceLocationOID.ANY
			
			// Date
			slot.setYear(Year.of(date));
			slot.setMonthOfYear(MonthOfYear.of(date));		// 2016/Dec/26 error in MonthOfYear (do not have to add +1)
			slot.setDayOfMonth(DayOfMonth.of(date));
			slot.setHourOfDay(HourOfDay.of(timeStartNonBookable.getHourOfDay()));
			slot.setMinuteOfHour(MinuteOfHour.of(timeStartNonBookable.getMinuteOfHour()));
			slot.setDurationMinutes(Minutes.minutesBetween(timeStartNonBookable,
														   timeEndNonBookable)
										   .getMinutes());
			
			// Subject
			slot.setSubject(nonBookableSubject);
			slot.setUserCode(userCode);
			
			// save
			slot = this.getForCRUD()
						   .save(slot)		// create or update if it previously exists
						   .as(AA14NonBookableSlot.class);
			return slot;
		} else {
			log.warn("BEWARE!!!! An attempt to create a non bookable slot with ZERO duration was done; ignoring...");
		}
		return null;
	}
	/**
	 * Creates a periodic non-bookable slot
	 * @param schId
	 * @param startDate
	 * @param endDate
	 * @param timeStartNonBookable
	 * @param timeEndNonBookable
	 * @param sunday
	 * @param monday
	 * @param tuesday
	 * @param wednesday
	 * @param thursday
	 * @param friday
	 * @param saturday
	 * @param nonBookableSubject
	 * @param userCode
	 * @return
	 */
	public int createPeriodicNonBookableSlots(final AA14ScheduleID schId,
											  final Date startDate,final Date endDate,
											  final DateTime timeStartNonBookable,final DateTime timeEndNonBookable,
											  final boolean sunday,final boolean monday,final boolean tuesday,final boolean wednesday,final boolean thursday,final boolean friday,final boolean saturday,
											  final String nonBookableSubject,
											  final UserCode userCode) {
		// load the schedule
		AA14Schedule schedule = this.getServiceInterfaceCoreImplOrProxy(AA14CRUDServicesForSchedule.class)
								    .loadById(this.getSecurityContext(),
								    		  schId)
								    .getOrThrow();
		// 
		int created = this.getForCRUD()
								.createPeriodicNonBookableSlots(schedule.getOid(),
																startDate,endDate,
																timeStartNonBookable,timeEndNonBookable,
																sunday,monday,tuesday,wednesday,thursday,friday,saturday, 
																nonBookableSubject, 
																userCode);
		return created;
	}
}
