package aa14a.ui.servlet;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import aa14a.model.view.AA14CalendarEvents;
import aa14f.client.api.AA14ClientAPI;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.summaries.AA14SummarizedBookedSlot;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.servlet.HttpRequestParamsWrapper;
import r01f.types.Range;
import r01f.util.types.Dates;

@Slf4j
@Singleton
public class AA14CalendarServlet 
     extends HttpServlet {
       
	private static final long serialVersionUID = -4112043120887421640L;
/////////////////////////////////////////////////////////////////////////////////////////
//  INJECTED FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private AA14ClientAPI _clientAPI;
	private ObjectMapper _jsonObjectMapper;
//	private I18NService _i18nService;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    public AA14CalendarServlet() {
        super();
    } 
    @Inject
    public AA14CalendarServlet(final AA14ClientAPI api,
    						   final ObjectMapper jsonObjectMapper
//    						   @Named("i18n") final I18NService i18nService
    						   ) {
    	_clientAPI = api;
    	_jsonObjectMapper = jsonObjectMapper;
//    	_i18nService = i18nService;
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    @Override
	protected void doPost(final HttpServletRequest request,final HttpServletResponse response) throws ServletException,
																									  IOException {
		this.doGet(request,response);	
	}
    @Override
	protected void doGet(final HttpServletRequest request,final HttpServletResponse response) throws ServletException, 
																									 IOException {
		if (_clientAPI == null) throw new IllegalStateException("Client API was NOT injected!!!");
		
		// get a request params wrapper that provides easier params access
		HttpRequestParamsWrapper reqParams = new HttpRequestParamsWrapper(request);
		
		// load the schedule
		AA14ScheduleID schId = reqParams.getParameter("schId")
								 			.asType(AA14ScheduleID.class)
								 			.using(AA14ReqParamToType.transform(AA14ScheduleID.class))
								 			.orNull();
		if (schId == null) {
			log.warn("Parameter schId was NOT received!!");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		AA14Schedule sch = _clientAPI.schedulesAPI().getForCRUD()
					   		  							 .loadById(schId);
		// get the operation & language
		AA14CalendarOperation op = AA14CalendarOperation.from(request);
		Language lang = reqParams.getParameter("lang")
								 .asLanguageFromCountryCode()
								 .orDefault(Language.SPANISH);
		
		// get the current date to get default values 
		LocalDate thisDay = new LocalDate();
		int year = reqParams.getParameter("year").asInteger()
												 .orDefault(thisDay.getYear());		

		
		Collection<AA14SummarizedBookedSlot> slots = null;
		
		// RANGE appointmentes (to be used at fullcalendar.io - see: http://fullcalendar.io/docs/event_data/events_json_feed/)
		// > Sample: /appmtXXUIWar/AA14CalendarServlet?op=range_appointments&location=Bizkaia&start=2016-02-01&end=2016-02-03)
		if (op.is(AA14CalendarOperation.RANGE_APPOINTMENTS)) {
			// fullcalendar.io will add two query string params (determined by startParam & endParam calendar config vars)
			//		by default startParam=start and endParam=end
			//		the dates will be ISO8601 encoded
			Date startDate = reqParams.getParameter("start").asDate(Dates.ISO8601)
														    .orDefault(new Date());
			Date endDate = reqParams.getParameter("end").asDate(Dates.ISO8601)
														.orDefault(new Date());
			Range<Date> dateRange = Range.closed(startDate,endDate);
			slots = _clientAPI.bookedSlotsAPI().getForFind()
											   .findRangeBookedSlotsSummarizedFor(lang,
													   							  sch.getOid(),
														 						  dateRange);
			log.debug("Range calendar events for {}: {} events",
					  dateRange,(slots != null ? slots.size() : 0));
		}
		// DAY appointments 
		// > Sample: /aa14aaUIWar/AA14CalendarServlet?op=day_appointments&location=Bizkaia&year=2016&monthOfYear=2&dayOfMonth=1)
		else if (op.is(AA14CalendarOperation.DAY_APPOINTMENTS)) {						
			int monthOfYear = reqParams.getParameter("monthOfYear").asInteger()
																   .orDefault(thisDay.getMonthOfYear());
			int dayOfMonth = reqParams.getParameter("dayOfMonth").asInteger()
																 .orDefault(thisDay.getDayOfMonth());
			slots = _clientAPI.bookedSlotsAPI().getForFind()
												  .findDayBookedSlotsSummarizedFor(lang,
														   						   sch.getOid(),
															 					   year,monthOfYear,dayOfMonth);
			log.debug("DAY calendar events for year={} monthOfYear={} dayOfMonth={}: {} events",
					  year,monthOfYear,dayOfMonth,(slots != null ? slots.size() : 0));
		} 
		// WEEK appointments
		else if (op.is(AA14CalendarOperation.WEEK_APPOINTMENTS)) {
			int weekOfYear = reqParams.getParameter("weekOfYear").asInteger()
																 .orDefault(thisDay.getWeekyear());
			slots = _clientAPI.bookedSlotsAPI().getForFind()
												   .findWeekBookedSlotsSummarizedFor(lang,
														   							 sch.getOid(),
														   							 year,weekOfYear);
			log.debug("WEEK calendar events for year={} weekOfYear={}: {} events",
					  year,weekOfYear,(slots != null ? slots.size() : 0));
		}
		// MONTH appointments
		else if (op.is(AA14CalendarOperation.MONTH_APPOINTMENTS)) {
			int monthOfYear = reqParams.getParameter("monthOfYear").asInteger()
																   .orDefault(thisDay.getMonthOfYear());
			slots = _clientAPI.bookedSlotsAPI().getForFind()
												   .findMonthBookedSlotsSummarizedFor(lang,
														   							  sch.getOid(),
														   							  year,monthOfYear);
			log.debug("MONTH calendar events for year={} monthOfYear={}: {} events",
					  year,monthOfYear,(slots != null ? slots.size() : 0));
		}
		// Get an object that wraps the events to be converted to json (the one expected by fullCalendar.js)
		AA14CalendarEvents events = AA14CalendarEvents.from(slots,
//															_i18nService,
															lang);
	
		String json = _jsonObjectMapper.writeValueAsString(events.getEvents());
		_flushJsonToClient(response,json);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    private static void _flushJsonToClient(final HttpServletResponse response,
    							  	   	   final String json) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		response.flushBuffer();
    }
    private static void _flushErrorToClient(final HttpServletResponse response,
    										final String error) throws IOException {
    	log.error(error);
		response.getWriter().write(error);
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.flushBuffer();
    }
}
