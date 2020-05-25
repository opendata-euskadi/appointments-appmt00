package aa14a.ui.servlet.delegate;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDate;

import aa14a.ui.servlet.AA14ControllerOperation;
import aa14a.ui.servlet.AA14ReqParamToType;
import aa14f.client.api.AA14ClientAPI;
import aa14f.model.AA14NumberOfAdjacentSlots;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.timeslots.AA14DayRangeTimeSlots;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.servlet.HttpRequestParamsWrapper;
import r01f.types.Range;

@Slf4j
@RequiredArgsConstructor
public class AA14ControllerServletDelegateForSlotListing
	 extends AA14ControllerServletDelegateBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14ClientAPI _clientAPI;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void executeOp(final HttpServletRequest request,final HttpServletResponse response,
						  final AA14ControllerOperation op,final HttpRequestParamsWrapper reqParams) throws ServletException, 
																 											IOException {
		log.debug("[init]: Slot listing -----------------");
		
		// get the service location
		// get the service location
		AA14OrgDivisionServiceLocationID locId = reqParams.getParameter("serviceLocId")		// not mandatory!!
									 					  .asType(AA14OrgDivisionServiceLocationID.class)
									 					  .using(AA14ReqParamToType.transform(AA14OrgDivisionServiceLocationID.class))
									 					  .orNull();
		AA14ScheduleID prefSchId = reqParams.getParameter("prefSchId")		// not mandatory
										    .asType(AA14ScheduleID.class)
					 					    .using(AA14ReqParamToType.transform(AA14ScheduleID.class))
					 					    .orNull();
		AA14ScheduleID schId = reqParams.getParameter("schId")			// not mandatory
										    .asType(AA14ScheduleID.class)
					 					    .using(AA14ReqParamToType.transform(AA14ScheduleID.class))
					 					    .orNull();
		
		AA14NumberOfAdjacentSlots numberOfAdjacentSlots = reqParams.getParameter("numberOfAdjacentSlots")
																		.asEnumElementFromIntCode(AA14NumberOfAdjacentSlots.class)
																		.orDefault(AA14NumberOfAdjacentSlots.ONE);		// one of slots 
		// get the current date to get default values 
		LocalDate thisDay = new LocalDate();
		
		int year = reqParams.getParameter("year").asInteger().orDefault(thisDay.getYear());
		int month = reqParams.getParameter("month").asInteger().orDefault(thisDay.getMonthOfYear()); 
		int day = reqParams.getParameter("day").asInteger().orDefault(thisDay.getDayOfMonth()); 
		
		LocalDate startDate = new LocalDate(year,month,day);		// start date (monday)
		LocalDate endDate = startDate.plusDays(5);			// period (5) 
		Range<Date> range = Range.closed(startDate.toDate(),endDate.toDate());
		
		// BEWARE!!
		// 		When painting the calendar of free slots for the FIRST TIME, the date range is slipped to find 
		//		the first range with an available time slot 
		//		... BUT when paging the calendar, DO NOT slip the date range to find the first available timeslot
		//			just return the required slots for the given timeslot
		boolean slipDateRangeToFindFirstAvailableSlot = reqParams.getParameter("slipDateRangeToFindFirstAvailableSlot")
																 .asBoolean()
																 .orDefault(false);		// DO NOT slip by default		
		
		log.debug("\t... at service location with id={} (preferred schedule with id={}) > dateRange={} (slip range to find first available slot={})",
				  locId,prefSchId,
				  range.asString(),slipDateRangeToFindFirstAvailableSlot);
		AA14DayRangeTimeSlots dayRangeTimeSlots = null;
		if (locId != null) {
			AA14OrgDivisionServiceLocation location = _clientAPI.orgDivisionServiceLocationsAPI().getForCRUD()
														   		  .loadById(locId);
			if (prefSchId != null) {
				AA14Schedule prefSch = _clientAPI.schedulesAPI().getForCRUD()
															   	.loadById(prefSchId);
				dayRangeTimeSlots = _clientAPI.bookedSlotsAPI()
											  .getForCalendar()
											  .timeSlotsForRange(location.getOid(),
													  			 numberOfAdjacentSlots,
																 range,
																 prefSch.getOid(),
																 slipDateRangeToFindFirstAvailableSlot);
			}
			else {
				dayRangeTimeSlots = _clientAPI.bookedSlotsAPI()
											  .getForCalendar()
											  .timeSlotsForRange(location.getOid(),
													  			 numberOfAdjacentSlots,
																 range,
																 slipDateRangeToFindFirstAvailableSlot);	
			}
		}
		else {
			AA14Schedule sch = _clientAPI.schedulesAPI().getForCRUD()
														.loadById(schId);
			dayRangeTimeSlots = _clientAPI.bookedSlotsAPI()
										  .getForCalendar()
										  .timeSlotsForRange(sch.getOid(),
												  			 numberOfAdjacentSlots,
															 range,
															 slipDateRangeToFindFirstAvailableSlot);
		}
		
		_returnJsonResponse(response, 
							dayRangeTimeSlots); 
		
		log.debug(" [End]: Slot listing-----------------");
	}
}
