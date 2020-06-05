package aa14b.services.delegates.persistence;

import java.util.Collection;
import java.util.Comparator;

import javax.persistence.EntityManager;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.EventBus;

import aa14b.calendar.AA14CalendarAvailableTimeSlotsDBFindDelegate;
import aa14b.calendar.AA14CalendarAvailableTimeSlotsFindDelegate;
import aa14b.calendar.AA14CalendarAvailableTimeSlotsOrchestraFindDelegate;
import aa14b.calendar.AA14CalendarService;
import aa14b.services.internal.AA14BookedSlotSummarizerService;
import aa14f.api.interfaces.AA14BookedSlotsCalendarServices;
import aa14f.model.AA14ModelObjectRef;
import aa14f.model.AA14NumberOfAdjacentSlots;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.timeslots.AA14DayRangeTimeSlots;
import aa14f.model.timeslots.AA14DayTimeSlots;
import aa14f.model.timeslots.AA14TimeSlot;
import aa14f.util.AA14AdjacentSlotsFilter;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.exceptions.Throwables;
import r01f.objectstreamer.HasMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.delegates.persistence.PersistenceServicesDelegateBase;
import r01f.types.Range;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;

@Slf4j
@Accessors(prefix="_")
public class AA14AppointmentsCalendarServicesDelegate
	 extends PersistenceServicesDelegateBase 
  implements AA14BookedSlotsCalendarServices,
  			 HasMarshaller {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
			private final AA14CRUDServicesDelegateForOrgDivisionServiceLocation _locCRUD;
			private final AA14CRUDServicesDelegateForSchedule _schCRUD;
			private final AA14CalendarAvailableTimeSlotsFindDelegate _availableTimeSlotsDBFindDelegate;
			private final AA14CalendarAvailableTimeSlotsFindDelegate _availableTimeSlotsOrchestraFindDelegate;
	@Getter private final Marshaller _modelObjectsMarshaller; 
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14AppointmentsCalendarServicesDelegate(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
													final EntityManager entityManager,
											        final Marshaller marshaller,
				  			   		   	   	        final EventBus eventBus,
				  			   		   	   	        final AA14BookedSlotSummarizerService slotSummarizerService,
													final AA14CalendarService calendarService) {
		super(coreCfg,
			  null,		// no service impl
			  null);	// no event bus
		_locCRUD = new AA14CRUDServicesDelegateForOrgDivisionServiceLocation(coreCfg,
																			 entityManager,
																			 marshaller,
																			 eventBus);
		_schCRUD = new AA14CRUDServicesDelegateForSchedule(coreCfg,
														   entityManager,
														   marshaller,
														   eventBus);
		_availableTimeSlotsDBFindDelegate = new AA14CalendarAvailableTimeSlotsDBFindDelegate(new AA14FindServicesDelegateForBookedSlot(coreCfg,
																																	   entityManager,
																																	   marshaller,
																																	   eventBus,
																																	   slotSummarizerService));
		_availableTimeSlotsOrchestraFindDelegate = new AA14CalendarAvailableTimeSlotsOrchestraFindDelegate(calendarService);
		_modelObjectsMarshaller = marshaller;
	}

/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AA14DayRangeTimeSlots availableTimeSlotsForRange(final SecurityContext securityContext,
												   			final AA14OrgDivisionServiceLocationOID locOid,final AA14ScheduleOID prefSchOid,
												   			final AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   			final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   			final int numberOfDays,
												   			final boolean slipDateRangeToFindFirstAvailableSlot) {
		// Create a new AA14OrgDivisionServiceLocation delegate to load the full AA14OrgDivisionServiceLocation
		AA14OrgDivisionServiceLocation loc = _locCRUD.load(securityContext,
														   locOid)
												     .getOrThrow();
		Collection<AA14Schedule> schs = FluentIterable.from(loc.getSchedulesRefs())
											.transform(new Function<AA14ModelObjectRef<AA14ScheduleOID,AA14ScheduleID>,AA14Schedule>() {
																@Override
																public AA14Schedule apply(final AA14ModelObjectRef<AA14ScheduleOID,AA14ScheduleID> ref) {
																	return _schCRUD.load(securityContext,
																						 ref.getOid())
																				   .getOrThrow();
																}
													   })
											.toList();
		return _availableTimeSlotsForRange(securityContext,
										   loc,schs,prefSchOid,
										   numberOfAdjacentSlots,
										   year,monthOfYear,dayOfMonth,
										   numberOfDays,
										   slipDateRangeToFindFirstAvailableSlot);
	}
	@Override
	public AA14DayRangeTimeSlots availableTimeSlotsForRange(final SecurityContext securityContext,
												   			final AA14ScheduleOID schOid,
												   			final AA14OrgDivisionServiceLocationOID prefLocOid,
												   			final AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   			final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   			final int numberOfDays,
												   			final boolean slipDateRangeToFindFirstAvailableSlot) {
		// load the full service locations at the given schedule
		AA14Schedule sch = _schCRUD.load(securityContext,
										 schOid)
									.getOrThrow();
		Collection<AA14OrgDivisionServiceLocation> locs = FluentIterable.from(sch.getServiceLocationsRefs())
																.transform(new Function<AA14ModelObjectRef<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID>,AA14OrgDivisionServiceLocation>() {
																					@Override
																					public AA14OrgDivisionServiceLocation apply(final AA14ModelObjectRef<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID> ref) {
																						return _locCRUD.load(securityContext,
																											 ref.getOid())
																									   .getOrThrow();
																					}
																		   })
																.toList();
		return _availableTimeSlotsForRange(securityContext,
										   sch,locs,prefLocOid,
										   numberOfAdjacentSlots,
										   year,monthOfYear,dayOfMonth,
										   numberOfDays,
										   slipDateRangeToFindFirstAvailableSlot);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the day by day time slots available for appointments in the provided date range
	 * <pre>
	 * 						  | LOC 1 | LOC 2 | LOC 3
	 *                        |-------|-------|------
	 *                   SHC1 |   X   |       |   X
	 *                   -----|-------|-------|------
	 *                   SCH2 |       |   X   |   X
	 *                   -----|-------|-------|------
	 *                   SCH3 |   X   |   X   | 
	 *                            ^
	 *                            |
	 *                       find by col
	 * </pre>
	 * In the example above, this method returns all available slots at LOC 1
	 * whether they're in SCH1 or SCH3
	 *                           
	 * @param securityContext
	 * @param loc the location for which the calendar is requested
	 * @param shcs the schedules the location is related with
	 * @param numberOfAdjacentSlots the number of adjacent slots to consider an slot as free
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @param numberOfDays
	 * @param slip the range to find the first available slot if the given range does NOT contains any available slot
	 * @return
	 */
	private AA14DayRangeTimeSlots _availableTimeSlotsForRange(final SecurityContext securityContext,
												   			  final AA14OrgDivisionServiceLocation loc,final Collection<AA14Schedule> schs,final AA14ScheduleOID prefSchOid,
												   			  final AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   			  final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   			  final int numberOfDays,
												   			  final boolean slipDateRangeToFindFirstAvailableSlot) {
		log.debug("Available time slots at LOCATION={} for SCHEDULES={} from {}/{}/{} within {} days (slip range to find first available slot={})",
				  loc.getId(),
				  FluentIterable.from(schs)
	 						    .transform(new Function<AA14Schedule,AA14ScheduleID>() {
													@Override
													public AA14ScheduleID apply(final AA14Schedule sch) {
														return sch.getId();
													}
	 									  }),
				 year,monthOfYear,dayOfMonth,
				 numberOfDays,
				 slipDateRangeToFindFirstAvailableSlot);
		// validate params
		AA14NumberOfAdjacentSlots theNumberOfAdjacentSlots = numberOfAdjacentSlots == null ? AA14NumberOfAdjacentSlots.ONE : numberOfAdjacentSlots;
		int theNumberOfDays = numberOfDays <= 0 ? 1 : numberOfDays;
		if (numberOfDays <= 0) log.warn("The provided day period for returning the time slots is not valid: defaulting to 1 day");
		if (year.getYear() < 2015 
		 || !Range.closed(1,12).contains(monthOfYear.getMonthOfYear()) 
		 || !Range.closed(1,31).contains(dayOfMonth.getDayOfMonth())) 
			throw new IllegalArgumentException(Throwables.message("The provided year/month/day={}/{}/{} is NOT valid",
																  year,monthOfYear,dayOfMonth));
		
		// order the schedules so the preferred one is always the first
		Predicate<AA14Schedule> prefSchPredicate = new Predicate<AA14Schedule>() {
															@Override
															public boolean apply(final AA14Schedule sch) {
																return sch.getOid().is(prefSchOid);
															}
										   		   };
		Collection<AA14Schedule> orderedSchedules = Lists.newArrayListWithExpectedSize(schs.size());
		AA14Schedule prefSch = FluentIterable.from(schs)
										     .firstMatch(prefSchPredicate)
										     .orNull();
		if (prefSch != null) { 
			orderedSchedules.add(prefSch);
			orderedSchedules.addAll(FluentIterable.from(schs)
												  .filter(Predicates.not(prefSchPredicate))
												  .toList());
		} else {
			orderedSchedules = schs;
		}
		
		// Get the day range time slots for every schedule
		Collection<AA14DayRangeTimeSlots> schDayRangeTimeSlots = Lists.newArrayListWithExpectedSize(schs.size());
		for (AA14Schedule sch : orderedSchedules) {
			AA14DayRangeTimeSlots schLocDayRangeTimeSlots =  _availableTimeSlotsForRange(securityContext,
																						 loc,sch,
																						 theNumberOfAdjacentSlots,
																						 year,monthOfYear,dayOfMonth,
																						 theNumberOfDays,
																						 slipDateRangeToFindFirstAvailableSlot);
			schDayRangeTimeSlots.add(schLocDayRangeTimeSlots);
		}
		// mix all the day range time slots (the collection will contain the preferred schedule in the first place)		
		return _mixDayRangeTimeSlotsCol(year,monthOfYear,dayOfMonth,
									    numberOfDays,
										schDayRangeTimeSlots);
	}
	/**
	 * Returns the day by day time slots available for appointments in the provided date range
	 * <pre>
	 * 						  | LOC 1 | LOC 2 | LOC 3
	 *                        |-------|-------|------
	 *                   SHC1 |   X   |       |   X    <-- find by row
	 *                   -----|-------|-------|------
	 *                   SCH2 |       |   X   |   X
	 *                   -----|-------|-------|------
	 *                   SCH3 |   X   |   X   | 
	 * </pre>
	 * In the example above, this method returns all available slots at SCH 1
	 * whether they're for LOC1 or LOC 3
	 * @param securityContext
	 * @param sch the schedule for which the calendar is requested
	 * @param locs the locations the schedule is related with
	 * @param numberOfAdjacentSlots the number of adjacent free slots to consider an slot as free
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @param numberOfDays
	 * @param slip the range to find the first available slot if the given range does NOT contains any available slot
	 * @return
	 */
	private AA14DayRangeTimeSlots _availableTimeSlotsForRange(final SecurityContext securityContext,
												   			  final AA14Schedule sch,final Collection<AA14OrgDivisionServiceLocation> locs,final AA14OrgDivisionServiceLocationOID prefLocOid,
												   			  final AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   			  final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   			  final int numberOfDays,
												   			  final boolean slipDateRangeToFindFirstAvailableSlot) {
		log.warn("Available time slots at SCHEDULE={} for LOCATION={} from {}/{}/{} within {} days (slip range to find first available slot={})",
				 sch.getId(),
				 FluentIterable.from(locs).transform(new Function<AA14OrgDivisionServiceLocation,AA14OrgDivisionServiceLocationID>() {
																@Override
																public AA14OrgDivisionServiceLocationID apply(final AA14OrgDivisionServiceLocation loc) {
																	return loc.getId();
																}
				 									 }),
				 year,monthOfYear,dayOfMonth,
				 numberOfDays,
				 slipDateRangeToFindFirstAvailableSlot);
		// validate params
		AA14NumberOfAdjacentSlots theNumberOfAdjacentSlots = numberOfAdjacentSlots == null ? AA14NumberOfAdjacentSlots.ONE : numberOfAdjacentSlots;
		int theNumberOfDays = numberOfDays <= 0 ? 1 : numberOfDays;
		if (numberOfDays <= 0) log.warn("The provided day period for returning the time slots is not valid: defaulting to 1 day");
		if (year.getYear() < 2015 
		 || !Range.closed(1,12).contains(monthOfYear.getMonthOfYear()) 
		 || !Range.closed(1,31).contains(dayOfMonth.getDayOfMonth())) 
			throw new IllegalArgumentException(Throwables.message("The provided year/month/day={}/{}/{} is NOT valid",
																  year,monthOfYear,dayOfMonth));
		
		// order the locs so the preferred one is always the first
		Collection<AA14OrgDivisionServiceLocation> orderedLocs = Ordering.from(new Comparator<AA14OrgDivisionServiceLocation>() {
																						@Override
																						public int compare(final AA14OrgDivisionServiceLocation l1,final AA14OrgDivisionServiceLocation l2) {
																							if (l1.getOid().is(prefLocOid)) return 1;
																							return -1;
																						}
																			   })
										 								 .immutableSortedCopy(locs);
		// Get the day range time slots for every schedule
		Collection<AA14DayRangeTimeSlots> locDayRangeTimeSlots = Lists.newArrayListWithExpectedSize(locs.size());
		for (AA14OrgDivisionServiceLocation loc : orderedLocs) {
			AA14DayRangeTimeSlots schLocDayRangeTimeSlots =  _availableTimeSlotsForRange(securityContext,
																						 loc,sch,
																						 theNumberOfAdjacentSlots,
																						 year,monthOfYear,dayOfMonth,
																						 theNumberOfDays,
																						 slipDateRangeToFindFirstAvailableSlot);
			locDayRangeTimeSlots.add(schLocDayRangeTimeSlots);
		}
		// mix all the day range time slots
		AA14DayRangeTimeSlots outRangeAvailableSlots = _mixDayRangeTimeSlotsCol(year,monthOfYear,dayOfMonth,
																			    numberOfDays,
																			    locDayRangeTimeSlots);
		return outRangeAvailableSlots;
	}
	private AA14DayRangeTimeSlots _availableTimeSlotsForRange(final SecurityContext securityContext,
															  final AA14OrgDivisionServiceLocation loc,final AA14Schedule sch,
															  final AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   			  final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   			  final int numberOfDays,
												   			  final boolean slipDateRangeToFindFirstAvailableSlot) {
		// [1] - use the db or qmatic orchestra to get the available slots
		AA14DayRangeTimeSlots outRangeAvailableSlots;
		if (sch.getOrchestraConfig() != null
		 && sch.getOrchestraConfig().isEnabled()) {
			// use qmatic orchestra
			if (slipDateRangeToFindFirstAvailableSlot) {
				outRangeAvailableSlots = _availableTimeSlotsOrchestraFindDelegate.nearestRangeWithAvailableTimeSlots(securityContext,
																					   						 		 loc,sch,
																					   						 		 year,monthOfYear,dayOfMonth,
																					   						 		 numberOfDays);
			} else {
				outRangeAvailableSlots = _availableTimeSlotsOrchestraFindDelegate.availableTimeSlotsForRange(securityContext, 
																											 loc,sch,
																											 year,monthOfYear,dayOfMonth,
																											 numberOfDays);
			}
		} else {
			// use db
			if (slipDateRangeToFindFirstAvailableSlot) {
				outRangeAvailableSlots = _availableTimeSlotsDBFindDelegate.nearestRangeWithAvailableTimeSlots(securityContext,
																					   				  		  loc,sch,
																					   				  		  year,monthOfYear,dayOfMonth,
																					   				  		  numberOfDays);
			} else {
				outRangeAvailableSlots = _availableTimeSlotsDBFindDelegate.availableTimeSlotsForRange(securityContext,
																					   				  loc,sch,
																					   				  year,monthOfYear,dayOfMonth,
																					   				  numberOfDays);
			}
		}
		// [2] - If number of adjacent slots > 1 remove slots that do NOT match the restriction
		if (numberOfAdjacentSlots.getValue() > 1) {
			log.info("{} adjacent slots requested", numberOfAdjacentSlots.getValue());
			outRangeAvailableSlots = AA14AdjacentSlotsFilter.reComputeSlotAvailability(outRangeAvailableSlots,
																			   		   numberOfAdjacentSlots);
		}
		// [3] - Return
		return outRangeAvailableSlots;
	}
	/**
	 * mixes a collection of day range time slots into a SINGLE day range time-slots 
	 * The process just overlaps all the individual day range time-slots
	 *  - if a certain slot is only available in a collection item just put it at the output day range time slots
	 * 	- if a certain slot is available in multiple collection items pick a random one
	 * @param year year of the starting date
	 * @param monthOfYear month of the starting date
	 * @param dayOfMonth day of the starting date
	 * @param numberOfDays number of days in the range
	 * @param dayRangeTimeSlotsCol collection of available slots
	 */
	private static AA14DayRangeTimeSlots _mixDayRangeTimeSlotsCol(final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   		   		  final int numberOfDays,
												   		   		  final Collection<AA14DayRangeTimeSlots> dayRangeTimeSlotsCol) {
		// ensure all day range time slots have the SAME slot size
		boolean allSameSlotSize = _sameSlotsSizeInMinutes(dayRangeTimeSlotsCol);
		if (!allSameSlotSize) throw new IllegalStateException("The provided day range time slots do NOT have the SAME slot size in minutes!!!");
		
		// every dayRangeTimeSlots MUST have the same slot duration in minutes... so pick the slot size from any 
		int slotsSizeInMinutes = FluentIterable.from(dayRangeTimeSlotsCol)
											   .first().orNull().getTimeSlotsSizeInMinutes();	
		
		AA14DayRangeTimeSlots outSlots = new AA14DayRangeTimeSlots(slotsSizeInMinutes,
																   year,monthOfYear,dayOfMonth,
																   numberOfDays);
			
		// day range
		for (AA14DayRangeTimeSlots item : dayRangeTimeSlotsCol) {
			if (!item.hasDayTimeSlots()) continue;		// skip empty day-range time-slots
			
			// day
			for (AA14DayTimeSlots dayTimeSlots : item.getDayTimeSlots()) {
				// check if there's another day time slots object
				AA14DayTimeSlots existingDayTimeSlots = outSlots.dayTimeSlotsFor(dayTimeSlots.getYear(), 
																				 dayTimeSlots.getMonthOfYear(),
																				 dayTimeSlots.getDayOfMonth());
				// day slots
				if (existingDayTimeSlots != null) {	
					// mix day slots
					for (AA14TimeSlot slot : dayTimeSlots.getTimeSlots()) {
						AA14TimeSlot existingSlot = existingDayTimeSlots.getSlotAt(slot.getHourOfDay(),
																				   slot.getMinuteOfHour());
						if (existingSlot != null) {
							// just ignore (the preferred [schedule] should have been the FIRST to be processed
							//				... so the slots at this [schedule] will be the selected ones)
						} else {
							// add
							existingDayTimeSlots.add(slot);
						}
					}
				} else {
					// beware! clone
					AA14DayTimeSlots clonedDayTimeSlots = new AA14DayTimeSlots(dayTimeSlots.getYear(),dayTimeSlots.getMonthOfYear(),dayTimeSlots.getDayOfMonth(),
																			   Lists.newArrayList(dayTimeSlots.getTimeSlots()));
					outSlots.add(clonedDayTimeSlots);
				}
			}
			outSlots.setMoreAvailable(outSlots.isMoreAvailable() || item.isMoreAvailable());
			
		}
		return outSlots;
	}
	/**
	 * Checks if all {@link AA14DayRangeTimeSlots} have the same slot size
	 * @param dayRangeTimeSlots
	 * @return
	 */
	private static boolean _sameSlotsSizeInMinutes(final Collection<AA14DayRangeTimeSlots> dayRangeTimeSlots) {
		boolean outSame = true;
		int currSlotsSizeInMinutes = -1;
		for (AA14DayRangeTimeSlots s : dayRangeTimeSlots) {
			if (currSlotsSizeInMinutes < 0) currSlotsSizeInMinutes = s.getTimeSlotsSizeInMinutes();		// only the first
			if (currSlotsSizeInMinutes != s.getTimeSlotsSizeInMinutes()) {
				outSame = false;
				break;
			}
			currSlotsSizeInMinutes = s.getTimeSlotsSizeInMinutes();		// next
		}
		return outSame;
	}
}
