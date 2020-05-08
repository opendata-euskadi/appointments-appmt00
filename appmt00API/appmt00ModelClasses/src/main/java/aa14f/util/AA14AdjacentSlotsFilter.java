package aa14f.util;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import aa14f.model.AA14NumberOfAdjacentSlots;
import aa14f.model.timeslots.AA14DayRangeTimeSlots;
import aa14f.model.timeslots.AA14DayTimeSlots;
import aa14f.model.timeslots.AA14TimeSlot;
import r01f.util.types.collections.CollectionUtils;

public class AA14AdjacentSlotsFilter {
	/**
	 * Re-computes the slots availability so an slot is available ONLY if there's followed by the 
	 * given number of adjacent slots
	 * @param dayRangeTimeSlots
	 * @param numberOfAdjacentSlots
	 * @return
	 */
	public static AA14DayRangeTimeSlots reComputeSlotAvailability(final AA14DayRangeTimeSlots dayRangeTimeSlots,
														  		  final AA14NumberOfAdjacentSlots numberOfAdjacentSlots) {
		return new AA14DayRangeTimeSlots(dayRangeTimeSlots.getTimeSlotsSizeInMinutes(),
										 dayRangeTimeSlots.getRequestedDateRange(),
										 AA14AdjacentSlotsFilter.reComputeSlotAvailability(dayRangeTimeSlots.getDayTimeSlots(),
												 								   		   numberOfAdjacentSlots),
										 dayRangeTimeSlots.isMoreAvailable());
	}
	/**
	 * Re-computes the slots availability so an slot is available ONLY if there's followed by the 
	 * given number of adjacent slots
	 * @param dayTimeSlots
	 * @param numberOfAdjacentSlots
	 * @return
	 */
	public static Collection<AA14DayTimeSlots> reComputeSlotAvailability(final Collection<AA14DayTimeSlots> dayTimeSlots,
																 		 final AA14NumberOfAdjacentSlots numberOfAdjacentSlots) {
		return FluentIterable.from(dayTimeSlots)
					 .transform(new Function<AA14DayTimeSlots,AA14DayTimeSlots>() {
										@Override
										public AA14DayTimeSlots apply(final AA14DayTimeSlots daySlots) {
											return AA14AdjacentSlotsFilter.reComputeSlotAvailability(daySlots,
																									 numberOfAdjacentSlots);
										}
					 			})
					 .toList();
	}
	/**
	 * Re-computes the slots availability so an slot is available ONLY if there's followed by the 
	 * given number of adjacent slots
	 * @param dayTimeSlots
	 * @param numberOfAdjacentSlots
	 * @return
	 */
	public static AA14DayTimeSlots reComputeSlotAvailability(final AA14DayTimeSlots daySlots,
															 final AA14NumberOfAdjacentSlots numberOfAdjacentSlots) {
		AA14DayTimeSlots outDaySlots = null;
		if (CollectionUtils.hasData(daySlots.getTimeSlots())) {
			// clone
			List<AA14TimeSlot> theSlots = (List<AA14TimeSlot>)daySlots.getTimeSlots();
			Collection<AA14TimeSlot> slots = AA14AdjacentSlotsFilter.reComputeSlotAvailability(theSlots,
																					   		   numberOfAdjacentSlots);											
			outDaySlots = new AA14DayTimeSlots(daySlots.getYear(),daySlots.getMonthOfYear(),daySlots.getDayOfMonth(),
											   slots);
		} else {
			// no change
			outDaySlots = daySlots;
		}
		return outDaySlots;
	}
	/**
	 * Re-computes the slots availability so an slot is available ONLY if there's followed by the 
	 * given number of adjacent slots
	 * <pre>
	 * 								  numberOfAdjacentSlots 
	 *      							    [3]  [2]
	 *       					  available
	 *       		08:00 - 08:30     Y      |    | 
	 *       		08:30 - 09:00     Y      |    |
	 *       		09:00 - 09:30     Y      |    
	 *       		09:30 - 10:00     X
	 *       		10:00 - 10:30     Y
	 *       		10:30 - 11:00     X
	 *       		11:00 - 11:30     Y       |    |
	 *       		11:30 - 12:00     Y       |    |
	 *       		12:30 - 13:00     Y       |
	 * </pre>
	 * @param timeSlots
	 * @param numberOfAdjacentSlots
	 * @return
	 */
	public static Collection<AA14TimeSlot> reComputeSlotAvailability(final List<AA14TimeSlot> inTimeSlots,
								  							   		 final AA14NumberOfAdjacentSlots numberOfAdjacentSlots) {
		
		
		Collection<AA14TimeSlot> outTimeSlots = Lists.newArrayListWithExpectedSize(inTimeSlots.size());
		if (inTimeSlots.size()==1) { //a single available slot is never adjacent
			AA14TimeSlot prevAvailableSlot = inTimeSlots.get(0);
			AA14TimeSlot notAvailableSlot = new AA14TimeSlot(prevAvailableSlot.getScheduleOid(),
																	 prevAvailableSlot.getHourOfDay(),prevAvailableSlot.getMinuteOfHour(),
																	 false);		// now it's NOT available
			outTimeSlots.add(notAvailableSlot);
		}
		else {
		
			int gapInitPosition = 0;
			int gapEndPosition = 1;
			int slotGap = 0;
			do {
				boolean isAdjacent = false;
				boolean isElegible = false;
				AA14TimeSlot prevTimeSlot = inTimeSlots.get(gapInitPosition);
				do {
					AA14TimeSlot currTimeSlot = inTimeSlots.get(gapEndPosition);
					isAdjacent = prevTimeSlot.isAdjacent(currTimeSlot);
//					System.out.println("===>" + prevTimeSlot.debugTimeRange() 
//											  + " ( " + (prevTimeSlot.isAvailable()?"available": "NOT available") + ")"
//											  + (isAdjacent ? " is adjacent to " : " is NOT adjacent to ")
//											  + currTimeSlot.debugTimeRange() + "("+ (currTimeSlot.isAvailable()?"available": "NOT available") +")");
					isElegible = isAdjacent 
						&& prevTimeSlot.isAvailable() 
						&& currTimeSlot.isAvailable();
					if (isElegible) 
						slotGap++;
					
					// next slot
					prevTimeSlot = currTimeSlot;
					gapEndPosition = gapEndPosition+1;
				} while(isElegible
					 && slotGap < numberOfAdjacentSlots.getValue()-1
					 && gapEndPosition < inTimeSlots.size());
				
				if (slotGap == numberOfAdjacentSlots.getValue()-1) {
					// there's enough number of adjacent free slots
					for (int k=gapInitPosition; k < gapEndPosition; k++) {
						if (!outTimeSlots.contains(inTimeSlots.get(k))){
							outTimeSlots.add(inTimeSlots.get(k));
						}	
					}
				}
				else {
					// there's NO enough number of adjacent free slots: change all them to unavailable
					int unavailibilityInitIndex=!outTimeSlots.contains(inTimeSlots.get(gapInitPosition))?gapInitPosition:gapInitPosition+1;
					for (int k= unavailibilityInitIndex; k < gapEndPosition-1; k++) {
						//System.out.println("Changing to unavailable: "+inTimeSlots.get(k).debugTimeRange()+" k: "+k);
						AA14TimeSlot prevAvailableSlot = inTimeSlots.get(k);
						AA14TimeSlot notAvailableSlot = AA14TimeSlot.createNotAvailableAt(prevAvailableSlot.getScheduleOid(),
																		 prevAvailableSlot.getHourOfDay(),prevAvailableSlot.getMinuteOfHour(),
																		 prevAvailableSlot.getSizeInMinutes());		// now it's NOT available
						outTimeSlots.add(notAvailableSlot);
					}
				}
				// new start
				gapInitPosition = gapEndPosition-1;
				gapEndPosition = gapInitPosition+1;		
				slotGap=0;
				
			} while(gapInitPosition < inTimeSlots.size()
				 && gapEndPosition < inTimeSlots.size());
				if (gapInitPosition==inTimeSlots.size()-1) {
					//last element is never adjacent
					AA14TimeSlot prevAvailableSlot = inTimeSlots.get(gapInitPosition);
					AA14TimeSlot notAvailableSlot = AA14TimeSlot.createNotAvailableAt(prevAvailableSlot.getScheduleOid(),
																		 prevAvailableSlot.getHourOfDay(),prevAvailableSlot.getMinuteOfHour(),
																		 prevAvailableSlot.getSizeInMinutes());		// now it's NOT available
					outTimeSlots.add(notAvailableSlot);
					
				}
		}
		return outTimeSlots;
	}
}
