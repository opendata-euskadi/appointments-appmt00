package aa14a.ui.servlet.delegate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aa14a.ui.servlet.AA14AppointmentFromRequestBuilder;
import aa14a.ui.servlet.AA14ControllerOperation;
import aa14a.ui.servlet.AA14ReqParamToType;
import aa14f.client.api.AA14ClientAPI;
import aa14f.model.AA14Appointment;
import aa14f.model.AA14NumberOfAdjacentSlots;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.locale.Language;
import r01f.model.persistence.PersistenceException;
import r01f.model.persistence.PersistenceServiceErrorTypes;
import r01f.servlet.HttpRequestParamsWrapper;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;

@Slf4j
@RequiredArgsConstructor
public class AA14ControllerServletDelegateForAppointmentCreate
	 extends AA14ControllerServletDelegateBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14ClientAPI _clientAPI;
	private final AA14AppointmentFromRequestBuilder _appointmentFromRequestBuilder;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void executeOp(final HttpServletRequest request,final HttpServletResponse response,
						  final AA14ControllerOperation op,final HttpRequestParamsWrapper reqParams) throws ServletException, 
																 											IOException {
		log.debug("[init]: Appointment create-----------------");
				
		Language lang = reqParams.getParameter("lang")
								 .asLanguageFromCountryCode()
								 .orDefault(Language.DEFAULT);
		
		// location
		AA14OrgDivisionServiceLocationID locId = reqParams.getMandatoryParameter("serviceLocId")
									 						  .asType(AA14OrgDivisionServiceLocationID.class)
									 						  .using(AA14ReqParamToType.transform(AA14OrgDivisionServiceLocationID.class));
		AA14OrgDivisionServiceLocation location = _clientAPI.orgDivisionServiceLocationsAPI().getForCRUD()
														   		  .loadById(locId);
		
		
		// slot (see aa14a-appointmentDateTimeSelectionCalendar)
		//  id=SLTID_{dd}_{MM}_{yyyy}_{HH}_{mm}_{schOid}
		//		[0]   [1] [2]   [3]   [4]  [5]   [6]
		//	Ej: id=SLTID_25_4_2019_11_0_6CD9FB63-B535-4819-BC58-4CAD429D4B45
		String slotId = reqParams.getMandatoryParameter("appointmentDateTime")
								.asString();
		String[] slotIdSplitted = slotId.split("_");
		
		DayOfMonth dayOfMonth = DayOfMonth.of(Integer.parseInt(slotIdSplitted[1]));
		MonthOfYear monthOfYear = MonthOfYear.of(Integer.parseInt(slotIdSplitted[2]));  
		Year year = Year.of(Integer.parseInt(slotIdSplitted[3])); 
		HourOfDay hourOfDay = HourOfDay.of(Integer.parseInt(slotIdSplitted[4]));
		MinuteOfHour minuteOfHour = MinuteOfHour.of(Integer.parseInt(slotIdSplitted[5]));
		AA14ScheduleOID schOid = AA14ScheduleOID.forId(slotIdSplitted[6]);
		AA14NumberOfAdjacentSlots numberOfAdjacentSlots = reqParams.getParameter("numberOfAdjacentSlots")
																		.asEnumElementFromIntCode(AA14NumberOfAdjacentSlots.class)
																		.orDefault(AA14NumberOfAdjacentSlots.ONE);		// one of slots 

		// Oid
		AA14Appointment appointment = new AA14Appointment();
		appointment.setOid(AA14SlotOID.supply());
		
		// location & schedule (mandatory!!!)
		appointment.setOrgDivisionServiceLocationOid(location.getOid());	// mandatory
		appointment.setScheduleOid(schOid);
		
		// Date
		appointment.setYear(year);
		appointment.setMonthOfYear(monthOfYear);
		appointment.setDayOfMonth(dayOfMonth);
		appointment.setHourOfDay(hourOfDay);
		appointment.setMinuteOfHour(minuteOfHour);
		appointment.setDurationMinutes(_clientAPI.configAPI()
												 .getScheduleBookingConfigFor(schOid)
												 .getSlotDefaultLengthMinutes());	// get the appointment duration from the schedule config
		appointment.setNumberOfAdjacentSlots(numberOfAdjacentSlots);
		
		// set appointment data
		_appointmentFromRequestBuilder.setAppointmentDataFromRequest(reqParams,appointment);
		
		// Update!
		try {
			appointment = _clientAPI.bookedSlotsAPI()
								   	   .getForCRUD()
									   .create(appointment)
									   .as(AA14Appointment.class);
		
			_returnJsonResponse(response,
								appointment); 
		
	     
		} catch (PersistenceException persistEx) {
			log.error("Persistence error code={} / ext code={}: {}",
					  persistEx.getCode(),persistEx.getExtendedCode(),
					  persistEx.getMessage(),
					  persistEx);
			if (persistEx.is(PersistenceServiceErrorTypes.ENTITY_ALREADY_EXISTS)
			 && persistEx.getExtendedCode() == 1) {
				// the slot was occupied when it was tried to be booked
				response.getWriter().write("The user MUST select another slot since the selected one was occupied when it was tried to be booked: ERROR_CODE=" + persistEx.getCode()); 
			}
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.flushBuffer();

		} catch (Exception e) {
			e.printStackTrace(System.out);
			response.getWriter().print(Throwables.getStackTraceAsString(e));
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.flushBuffer();
		}
		log.debug("[end]:  Appointment create-----------------");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	
}
