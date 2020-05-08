package aa14a.ui.servlet.delegate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aa14a.ui.servlet.AA14ControllerOperation;
import aa14a.ui.servlet.AA14ReqParamToType;
import aa14f.client.api.AA14ClientAPI;
import aa14f.model.AA14BookedSlot;
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
public class AA14ControllerServletDelegateForAppointmentDateTimeUpdate
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
		log.debug("[init]: Update appointment date/time-----------------");
				
		Language lang = reqParams.getParameter("lang")
								 .asLanguageFromCountryCode()
								 .orDefault(Language.DEFAULT);
		
		AA14SlotOID slotOid = reqParams.getMandatoryParameter("slotOid")
									   .asOid(AA14SlotOID.class)
			 						   .using(AA14ReqParamToType.transform(AA14SlotOID.class));
		String fecha = reqParams.getMandatoryParameter("appointmentDateTime")
								.asString();
		fecha = fecha.replace("SLTID_","");
		String[] cita = fecha.split("_");		
		Year year = Year.of(Integer.parseInt(cita[2])); 
		MonthOfYear monthOfYear = MonthOfYear.of(Integer.parseInt(cita[1])); 
		DayOfMonth dayOfMonth = DayOfMonth.of(Integer.parseInt(cita[0])); 
		HourOfDay hourOfDay = HourOfDay.of(Integer.parseInt(cita[3]));
		MinuteOfHour minutesOfHour = MinuteOfHour.of(Integer.parseInt(cita[4]));
		AA14ScheduleOID schOid = AA14ScheduleOID.forId(cita[5]);

		// Oid
		AA14BookedSlot slot = _clientAPI.bookedSlotsAPI()
										.getForCRUD()
										.load(slotOid);
		// schedule
		if (schOid.isNOT(slot.getScheduleOid())) {
			log.debug("...schedule changed from {} to {}",
					  slot.getScheduleOid(),schOid);
			slot.setScheduleOid(schOid);
		}
		// Date
		slot.setYear(year);
		slot.setMonthOfYear(monthOfYear);
		slot.setDayOfMonth(dayOfMonth);
		slot.setHourOfDay(hourOfDay);
		slot.setMinuteOfHour(minutesOfHour);
		try {
			slot = _clientAPI.bookedSlotsAPI()
							   	   .getForCRUD()
								   .update(slot);
		
			_returnJsonResponse(response,
								slot); 

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
		log.debug("[end]: Update appointment date/time-----------------");
	}
}
