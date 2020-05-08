package aa14.test;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import aa14f.model.AA14NumberOfAdjacentSlots;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.timeslots.AA14DayTimeSlots;
import aa14f.model.timeslots.AA14TimeSlot;
import aa14f.util.AA14AdjacentSlotsFilter;
import lombok.extern.slf4j.Slf4j;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;
import r01f.util.types.collections.CollectionUtils;

/**
 * jvm arguments: -javaagent:c:/develop/local-libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true -DR01_Home=c:/develop
 */
@Slf4j
public class AA14AdjacentTimeSlotsTest {
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////	
	@Test
	public void testTimeSlotsAdjacencyForTwoSlots() {
		AA14DayTimeSlots dayTimeSlots = _createDayTimeSlots(AA14ScheduleOID.forId("schOid"));
		log.info("Before keeping only two adjacent time slots:\n{}",
				 _timeSlotsDebugInfo(dayTimeSlots.getTimeSlots()));
		
		AA14DayTimeSlots computedDayTimeSlots = AA14AdjacentSlotsFilter.reComputeSlotAvailability(dayTimeSlots,
																	   							  AA14NumberOfAdjacentSlots.ONE);
		log.info("After keeping only two adjacent time slots:\n{}",
				 _timeSlotsDebugInfo(computedDayTimeSlots.getTimeSlots()));
	}
	
	@Test
	public void testTimeSlotsAdjacencyForThreeSlots() {
		AA14DayTimeSlots dayTimeSlots = _createDayTimeSlots(AA14ScheduleOID.forId("schOid"));
		log.info("Before keeping three adjacent time slots:\n{}",
				 _timeSlotsDebugInfo(dayTimeSlots.getTimeSlots()));
		
		AA14DayTimeSlots computedDayTimeSlots = AA14AdjacentSlotsFilter.reComputeSlotAvailability(dayTimeSlots,
																	   			    			  AA14NumberOfAdjacentSlots.TWO);
		log.info("After keeping three adjacent time slots:\n{}",
				 _timeSlotsDebugInfo(computedDayTimeSlots.getTimeSlots()));
	}
	private static CharSequence _timeSlotsDebugInfo(final Collection<AA14TimeSlot> timeSlots) {
		if (CollectionUtils.isNullOrEmpty(timeSlots)) return "";
		StringBuilder sb = new StringBuilder();
		for (Iterator<AA14TimeSlot> slotIt = timeSlots.iterator(); slotIt.hasNext();) {
			AA14TimeSlot slot = slotIt.next();
			sb.append(slot.debugTimeRange());
			sb.append(slot.isAvailable() ? " available " : "");
			
			if (slotIt.hasNext()) sb.append("\n");
		}
		return sb;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a {@link AA14DayTimeSlots} object with
	 * <pre>
	 * numberOfAdjacentSlots     [2]  [2]
	 *       					  \    \
	 *       		08:00 - 08:30 |    | 
	 *       		08:30 - 09:00 |    |
	 *       		09:00 - 09:30 |    /
	 *                            /
	 *       		10:00 - 10:30
	 *       					  \    \
	 *       		11:00 - 11:30 |    |
	 *       		11:30 - 12:00 |    |
	 *       					  /    /
	 *       		12:30 - 13:00
	 * </pre>
	 * @return
	 */
	private static AA14DayTimeSlots _createDayTimeSlots(final AA14ScheduleOID schOid) {
		AA14DayTimeSlots outDayTimeSlots = new AA14DayTimeSlots(Year.of(2018),MonthOfYear.of(1),DayOfMonth.of(1));
		
		outDayTimeSlots.add(_timeSlotFor(schOid,8,0 , true));
		outDayTimeSlots.add(_timeSlotFor(schOid,8,30, true));
		outDayTimeSlots.add(_timeSlotFor(schOid,9,0,  true));
		outDayTimeSlots.add(_timeSlotFor(schOid,9,30, false));
		outDayTimeSlots.add(_timeSlotFor(schOid,10,0, true));
		outDayTimeSlots.add(_timeSlotFor(schOid,10,30,false));
		outDayTimeSlots.add(_timeSlotFor(schOid,11,0, true));
		outDayTimeSlots.add(_timeSlotFor(schOid,11,30,true));
		outDayTimeSlots.add(_timeSlotFor(schOid,12,0, false));
		outDayTimeSlots.add(_timeSlotFor(schOid,12,30,true));
		
		return outDayTimeSlots;
	}
	private static AA14TimeSlot _timeSlotFor(final AA14ScheduleOID schOid,
											 final int hourOfDay,final int minuteOfHour,
											 final boolean available) {
		return new AA14TimeSlot(schOid,
								HourOfDay.of(hourOfDay),MinuteOfHour.of(minuteOfHour),
								30,
								available);
	}
}
