package aa14b.services.internal;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import aa14f.api.interfaces.AA14CRUDServicesForSchedule;
import aa14f.api.interfaces.AA14FindServicesForBookedSlot;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14BookedSlotType;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import lombok.extern.slf4j.Slf4j;
import r01f.model.persistence.FindResult;
import r01f.securitycontext.SecurityContext;
import r01f.types.Range;
import r01f.util.types.Dates;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * An INTERNAL service that checks if an slot is really available to put in a new appointment
 */
@Singleton
@Slf4j
public class AA14SlotOverlappingValidatorService {
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14FindServicesForBookedSlot _slotFind;
	private final AA14CRUDServicesForSchedule _scheduleCRUD;
	
	private final ConcurrentMap<AA14ScheduleOID,Integer> _configuredMaxAppointmentsInSlotBySchedule = Maps.newConcurrentMap();
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14SlotOverlappingValidatorService(final AA14CRUDServicesForSchedule schCRUD,
											   final AA14FindServicesForBookedSlot slotFind) {
		_slotFind = slotFind;
		_scheduleCRUD = schCRUD;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Checks if an appointment can be created in a slot
	 * (the configured maximum number of appointments -or non bookable slots- has NOT been reached)
	 * @param securityContext
	 * @param slot
	 * @return
	 */
	public boolean isSlotAvailable(final SecurityContext securityContext,
								   final AA14BookedSlot slot) {
		if (slot.getType() == AA14BookedSlotType.NON_BOOKABLE) return true;		// non bookable slots can always be created
		
		// [0] - Find all booked slots that could potentially overlap this one
		log.debug("Checking the slot within {} and {} can be created (there's enougth space for it",
				  slot.getStartDate(),slot.getEndDate());
		FindResult<AA14BookedSlot> existingSlotFind = _slotFind.findBookedSlotsOverlappingRange(securityContext,
										  								   						slot.getScheduleOid(),
										  								   						new Range<Date>(slot.getDateRange()));
		Collection<AA14BookedSlot> existingSlots = existingSlotFind.getOrThrow();
		
		// [1] - Get the real overlapping slots
		//			a) when updating remove the updated slot
		//			b) join adjacent (consecutive) slots (consecutive slots count as a SINGLE slot
		//												  when computing the max appointments in slot)
		int realOverlappingSlots = _realOverlappingSlots(slot,
														 existingSlots);
		
		// [2] - Check that the configured maximum number of appointments has NOT been reached
		int configuredMaxAppointments = _configuredMaxAppointmentsInSlotFor(securityContext,
																			slot.getScheduleOid());
		log.debug("\t... configured max appointments in slot: {} - actual appointments in slot: {} > the slot is available: {}",
				  configuredMaxAppointments,realOverlappingSlots,
				  realOverlappingSlots < configuredMaxAppointments);
		
		return realOverlappingSlots < configuredMaxAppointments;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Avoids loading the schedule config again and again
	 * @param securityContext
	 * @param schOid
	 * @return
	 */
	private int _configuredMaxAppointmentsInSlotFor(final SecurityContext securityContext,
													final AA14ScheduleOID schOid) {
		if (!_configuredMaxAppointmentsInSlotBySchedule.containsKey(schOid)) {
			AA14Schedule sch = _scheduleCRUD.load(securityContext,
												  schOid)
											.getOrThrow();
			_configuredMaxAppointmentsInSlotBySchedule.putIfAbsent(schOid,
																   sch.getBookingConfig().getMaxAppointmentsInSlot());
		}
		return _configuredMaxAppointmentsInSlotBySchedule.get(schOid);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * To get the real existing slots some things MUST be have into account
	 * a) when UPDATING the UPDATED slot must be removed
	 * b) when consecutive slots exists, they MUST be consolidated
	 *    For example this situation:
	 * 	  <pre>
	 * 	    [reserve] [appmt1] [appmt2]
	 * 			|        |  
	 * 			|		 | 
	 * 			|        |
	 * 			|                 |
	 * 			|				  |
	 * 			|                 |
	 * 			|
	 * 			|
	 * 	  </pre>
	 * 	  When querying the [booked slots] within the range 3 records are returned
	 *    ... BUT in computing the [number of occuppied resources], JUST 2 resources
	 *    	  must be counted since [appmt1] and [appmt2] are consecutive
	 *    Just TWO [resources] are involved:
	 *    <pre> 
	 *    this situation ---- will be translated to --- this resource utilization:   
	 *      [reserve] [appmt1] [appmt2]               [resource1] [resource2]        
	 *    	    |        |                                 |           | \           
	 *          |        |                                 |           |  - Appmt1   
	 *    	    |        |                                 |           | /           
	 *    	    |                 |                        |           | \           
	 *          |                 |                        |           |  - Appmt2   
	 *    	    |                 |                        |           | /           
	 *    	    |                                          |                         
	 *    	    |                                          |
	 *     </pre>                         
	 * @param existingSlots
	 * @return
	 */
	private static int _realOverlappingSlots(final AA14BookedSlot slot,
									  		 final Collection<AA14BookedSlot> existingSlots) {
		if (CollectionUtils.isNullOrEmpty(existingSlots)) return 0;
		
		// a) convert to a collection of ranges ordered by start date
		List<Range<Date>> allRanges = FluentIterable.from(existingSlots)
											// remove the given updated slot (do not take it into account when updating)
											.filter(new Predicate<AA14BookedSlot>() {
															@Override
															public boolean apply(final AA14BookedSlot otherSlot) {
																return otherSlot.getOid() != null ? otherSlot.getOid().isNOT(slot.getOid())
																								  : true;
															}
													})
											// transform to a collection for [date ranges]
										   .transform(new Function<AA14BookedSlot,Range<Date>>() {
																@Override
																public Range<Date> apply(final AA14BookedSlot s) {
																	return new Range<Date>(s.getDateRange());
																}
													   })
										   // sort by [start date]
										   .toSortedList(RANGE_BY_START_DATE_COMPARATOR);
		
		// b) split [date ranges] by resource
		Collection<Collection<Range<Date>>> rangesByResource = Lists.newArrayList();
		
		for (Range<Date> range : allRanges) {
			if (log.isDebugEnabled()) log.debug("> {}",_dateRangeToString(range,"HH:mm"));
			
			// try to add the range to any of the collections
			// ... or to a new one if no suitable one is found
			boolean added = false;
			for (Collection<Range<Date>> resourceRanges : rangesByResource) {
				boolean fits = _fitsInResourceRanges(range,
										  			 resourceRanges);
				if (log.isDebugEnabled()) log.debug("\t-{} can {} fit in {}",
													_dateRangeToString(range,"HH:mm"),
													fits ? "" : "NOT",
													_dateRangesToString(resourceRanges,"HH:mm"));
				if (fits) {
					// fits in an existing resource ranges
					resourceRanges.add(range);	// remember that the [resource ranges] collection is SORTED
					added = true;
					break;
				} 
			}
			if (!added) {
				if (log.isDebugEnabled()) log.debug("\t...created resource collection");
				
				Collection<Range<Date>> resourceRanges = Sets.newTreeSet(RANGE_BY_START_DATE_COMPARATOR);	// beware! ordered by start date
				resourceRanges.add(range);
				rangesByResource.add(resourceRanges);
			}
		}
		if (log.isDebugEnabled()) {
			for (Collection<Range<Date>> ranges : rangesByResource) {
				log.debug("{}",_dateRangesToString(ranges,"HH:mm"));
			}
		}
		return rangesByResource.size();
	}
	/**
	 * Checks if the given range can be "inserted" in the given ranges collection
	 * @param range
	 * @param ranges
	 * @return
	 */
	private static boolean _fitsInResourceRanges(final Range<Date> range,
										   		 final Collection<Range<Date>> ranges) {
		boolean outFits = false;
		if (CollectionUtils.isNullOrEmpty(ranges)) {
			outFits = true;
		} else if (ranges.size() == 1) {
			Range<Date> otherRange = Iterables.getFirst(ranges,null);
			outFits = otherRange.upperEndpoint().before(range.lowerEndpoint()) || otherRange.upperEndpoint().equals(range.lowerEndpoint());
		} else {
			for (int i=0; i < ranges.size(); i++) {
				if (i < (ranges.size() - 1)) {
					Range<Date> otherRange1 = Iterables.get(ranges,i);
					Range<Date> otherRange2 = Iterables.get(ranges,i+1);
					outFits = (otherRange1.upperEndpoint().before(range.lowerEndpoint()) || otherRange1.upperEndpoint().equals(range.lowerEndpoint()))
						   && (otherRange2.lowerEndpoint().after(range.upperEndpoint()) || otherRange2.lowerEndpoint().equals(range.upperEndpoint()));
				} else {
					Range<Date> otherRange = Iterables.get(ranges,i);
					outFits = otherRange.upperEndpoint().before(range.lowerEndpoint()) || otherRange.upperEndpoint().equals(range.lowerEndpoint());
				}
				if (outFits) break;
			}
		}
		return outFits; 
	}
	private static final Comparator<Range<Date>> RANGE_BY_START_DATE_COMPARATOR = new Comparator<Range<Date>>() {
																						@Override
																						public int compare(final Range<Date> r1,final Range<Date> r2) {
																							return r1.lowerEndpoint().equals(r2.lowerEndpoint()) ? 0
																																		   		 : r1.lowerEndpoint().before(r2.lowerEndpoint()) ? -1
																																		   				 										 : 1;
																						}
																				  };
	private static String _dateRangesToString(final Collection<Range<Date>> ranges,final String dateFormat) {
		StringBuilder sb = new StringBuilder();
		for (Iterator<Range<Date>> rIt = ranges.iterator(); rIt.hasNext(); ) {
			Range<Date> r = rIt.next();
			sb.append(_dateRangeToString(r,dateFormat));
			if (rIt.hasNext()) sb.append(",");
		}
		return sb.toString();
	}
	private static String _dateRangeToString(final Range<Date> range,final String dateFormat) {
		return Strings.customized("{}..{}",
								  range.lowerEndpoint() != null ? Dates.format(range.lowerEndpoint(),dateFormat) : "",
								  range.upperEndpoint() != null ? Dates.format(range.getUpperBound(),dateFormat) : "");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
/*
	public static void main(final String[] args) {
		Range<Date> r0 = Range.closed(Dates.fromFormatedString("2020/06/01 08:00","yyyy/MM/dd HH:mm"),
									  Dates.fromFormatedString("2020/06/01 14:00","yyyy/MM/dd HH:mm"));
		
		Range<Date> r1 = Range.closed(Dates.fromFormatedString("2020/06/01 08:00","yyyy/MM/dd HH:mm"),
									  Dates.fromFormatedString("2020/06/01 08:10","yyyy/MM/dd HH:mm"));
		Range<Date> r2 = Range.closed(Dates.fromFormatedString("2020/06/01 08:10","yyyy/MM/dd HH:mm"),
									  Dates.fromFormatedString("2020/06/01 08:20","yyyy/MM/dd HH:mm"));
		Range<Date> r3 = Range.closed(Dates.fromFormatedString("2020/06/01 08:20","yyyy/MM/dd HH:mm"),
									  Dates.fromFormatedString("2020/06/01 08:30","yyyy/MM/dd HH:mm"));
		Range<Date> r4 = Range.closed(Dates.fromFormatedString("2020/06/01 08:30","yyyy/MM/dd HH:mm"),
									  Dates.fromFormatedString("2020/06/01 08:40","yyyy/MM/dd HH:mm"));
		
		AA14BookedSlot s0 = new AA14Appointment(r0.asGuavaRange());
		AA14BookedSlot s1 = new AA14Appointment(r1.asGuavaRange());
		AA14BookedSlot s2 = new AA14Appointment(r2.asGuavaRange());
		AA14BookedSlot s3 = new AA14Appointment(r3.asGuavaRange());
		AA14BookedSlot s4 = new AA14Appointment(r4.asGuavaRange());
		
		System.out.println("_______________________________________________________");
		System.out.println("==true >" + _fitsInResourceRanges(r0,Lists.<Range<Date>>newArrayList()));			// true
		System.out.println("=false >" + _fitsInResourceRanges(r0,Lists.<Range<Date>>newArrayList(r1)));			// false
		System.out.println("=false >" + _fitsInResourceRanges(r1,Lists.<Range<Date>>newArrayList(r0)));			// false
		System.out.println("=false >" + _fitsInResourceRanges(r2,Lists.<Range<Date>>newArrayList(r1)));			// false
		System.out.println("==true >" + _fitsInResourceRanges(r2,Lists.<Range<Date>>newArrayList(r1,r3)));		// true
		System.out.println("==true >" + _fitsInResourceRanges(r4,Lists.<Range<Date>>newArrayList(r1,r2,r3)));	// true
		System.out.println("=false >" + _fitsInResourceRanges(r0,Lists.<Range<Date>>newArrayList(r1,r2,r3)));	// false
		System.out.println("==true >" + _fitsInResourceRanges(r4,Lists.<Range<Date>>newArrayList(r1,r2)));		// true
		System.out.println("==true >" + _fitsInResourceRanges(r3,Lists.<Range<Date>>newArrayList(r1,r4)));		// true
		
		System.out.println("_______________________________________________________");
		AA14BookedSlot slot = new AA14Appointment(Range.closed(Dates.fromFormatedString("2020/06/01 08:00","yyyy/MM/dd HH:mm"),
									  						   Dates.fromFormatedString("2020/06/01 08:20","yyyy/MM/dd HH:mm"))
													   .asGuavaRange());
		System.out.println("==2 >" + _realOverlappingSlots(slot,
														   Lists.<AA14BookedSlot>newArrayList(s0,
																   							  s1,s2)));		// 2 = [s0] / [s1,s2]
		System.out.println("==2 >" + _realOverlappingSlots(slot,
														   Lists.<AA14BookedSlot>newArrayList(s0,
																   							  s1,s2,s3)));	// 2 = [s0] / [s1,s2,s3]
		System.out.println("==3 >" + _realOverlappingSlots(slot,
														   Lists.<AA14BookedSlot>newArrayList(s0,
																   							  s1,s2,s3,s4,
																   							  s2)));		// 3 = [s0] / [s1,s2,s3,s4] / [s2]
		System.out.println("==2 >" + _realOverlappingSlots(slot,
														   Lists.<AA14BookedSlot>newArrayList(s0,
																   							  s1,s2,
																   							  s4,
																   							  s3)));		// 2 = [s0] / [s1,s2,s4] / [s3]
		System.out.println("==3 >" + _realOverlappingSlots(slot,
														   Lists.<AA14BookedSlot>newArrayList(s0,
																   							  s1,s2,
																   							  s4,
																   							  s2,
																   							  s3)));		// 3 = [s0] / [s1,s2,s4] / [s2,s3]
		System.out.println("==2 >" + _realOverlappingSlots(slot,
														   Lists.<AA14BookedSlot>newArrayList(s0,
																   							  s1)));		// 2 = [s0] / [s1]
		System.out.println("==3 >" + _realOverlappingSlots(slot,
														   Lists.<AA14BookedSlot>newArrayList(s0,
																   							  s1,s2,
																   							  s2,s3,
																   							  s3,s4)));		// 3 = [s0] / [s1,s2,s3] / [s2,s3,s4]
		System.out.println("==3 >" + _realOverlappingSlots(slot,
														   Lists.<AA14BookedSlot>newArrayList(s1,s4,
																   							  s3,
																   							  s2,s4,
																   							  s0)));		// 3 = [s1,s2,s3,s4] / [s4] / [s0]
		System.out.println("==3 >" + _realOverlappingSlots(slot,
														   Lists.<AA14BookedSlot>newArrayList(s1,s2,s3,
																   							  s3,
																   							  s2,s4,
																   							  s0)));		// 3 = [s1,s2,s3,s4] / [s2,s3] / [s0]
		System.out.println("==1 >" + _realOverlappingSlots(slot,
														   Lists.<AA14BookedSlot>newArrayList(s2,s3,
																   							  s1)));		// 1 = [s1,s2,s3]
	}
*/
}
