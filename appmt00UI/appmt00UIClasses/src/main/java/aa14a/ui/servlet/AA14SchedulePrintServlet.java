package aa14a.ui.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.api.client.util.Lists;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import aa14f.client.api.AA14ClientAPI;
import aa14f.model.AA14Appointment;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14BookedSlotType;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.servlet.HttpRequestParamsWrapper;
import r01f.types.Range;
import r01f.util.types.Dates;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
@Singleton
public class AA14SchedulePrintServlet 
     extends HttpServlet {
       
	private static final long serialVersionUID = -4112043120887421640L;
/////////////////////////////////////////////////////////////////////////////////////////
//  INJECTED FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private AA14ClientAPI _clientAPI;
	private AA14ScheduleBusinessDataPrinter _scheduleBusinessDataPrinter;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    public AA14SchedulePrintServlet() {
        super();
    } 
    @Inject
    public AA14SchedulePrintServlet(final AA14ClientAPI api,
    								final AA14ScheduleBusinessDataPrinter scheduleBusinessDataPrinter) {
    	_clientAPI = api;
    	_scheduleBusinessDataPrinter = scheduleBusinessDataPrinter;
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
		
		// get the language
		Language lang = reqParams.getParameter("lang")
								 .asLanguageFromCountryCode()
								 .orDefault(Language.SPANISH);
		
		// load the location
		AA14OrgDivisionServiceLocationID locId = reqParams.getMandatoryParameter("serviceLocId")
								 							.asType(AA14OrgDivisionServiceLocationID.class)
								 							.using(AA14ReqParamToType.transform(AA14OrgDivisionServiceLocationID.class));
		AA14OrgDivisionServiceLocation loc = _clientAPI.configAPI()
													   .getLocationFor(locId);
		
		// get the date range
		String dateFormat = lang.is(Language.SPANISH) ? Dates.ES_DEFAULT_FORMAT
													  : Dates.EU_DEFAULT_FORMAT;
		Date startDate = reqParams.getParameter("print_start_date").asDate(dateFormat)
																   .orDefault(new Date());
		Date endDate = reqParams.getParameter("print_end_date").asDate(dateFormat)
															   .orDefault(new Date());
		
		// ensure the first moment of the start date and the last moment of the end date
		DateTime startDateFirstInstant = new DateTime(startDate)
											.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
		DateTime endDateLastInstant = new DateTime(endDate)
											.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
		
		
		Range<Date> dateRange = Range.closed(startDateFirstInstant.toDate(),
											 endDateLastInstant.toDate());
		
		// load the booked slots
		log.debug("[CalendarPRINT]: Retrieve slots for location oid/id={}/{} in date range={}",
				  loc.getOid(),loc.getId(),
				  dateRange);
		Collection<AA14BookedSlot> slots = _clientAPI.bookedSlotsAPI()
														.getForFind()
								   							.findRangeBookedSlotsFor(loc.getOid(),
								   													 dateRange);
		log.debug("Range calendar events for {}: {} events",
				  dateRange,(slots != null ? slots.size() : 0));
		// Print
		response.setContentType("text/html");
		PrintWriter w = response.getWriter();
		_printHtml(loc,
				   slots,
				   dateRange,
				   lang,
				   w);
		w.flush();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	PRINT 
/////////////////////////////////////////////////////////////////////////////////////////
    private void _printHtml(final AA14OrgDivisionServiceLocation loc,
    						final Collection<AA14BookedSlot> slots,
    						final Range<Date> dateRange,
    						final Language lang,
    						final PrintWriter w) {
    	// [1] - Filter booked slots to get appointments only (filter non-bookable slots)
    	Collection<AA14Appointment> apps = FluentIterable.from(slots)
    													 // filter appointments (discard non bookable slots)
		    											 .filter(new Predicate<AA14BookedSlot>() {
																		@Override
																		public boolean apply(final AA14BookedSlot slot) {
																			return slot.getType() == AA14BookedSlotType.APPOINTMENT;	// same as slot instanceof AA14Appointment
																		}
		    											 		 })
		    											 // transform to appointment
		    											 .transform(new Function<AA14BookedSlot,AA14Appointment>() {
																			@Override
																			public AA14Appointment apply(final AA14BookedSlot slot) {
																				return slot.as(AA14Appointment.class);
																			}
		    													   })
		    											 .toList();
    	// [2] - Map by date
    	Map<LocalDate,Collection<AA14Appointment>> appsByDate = Maps.newHashMap();
    	for (AA14Appointment app : apps) {
    		LocalDate date = new LocalDate(app.getStartDate());
    		Collection<AA14Appointment> appCol = null;
    		if (appsByDate.containsKey(date)) {
    			appCol = appsByDate.get(date);
    		} else {
    			appCol = Lists.newArrayList();
    			appsByDate.put(date,appCol);
    		}
    		appCol.add(app);
    	}
    	
    	
    	// [3] - Print
		w.println("<html>");
		
		w.println("<head>");
		_printHtmlHead(w);
		w.println("</head>");
		
		w.println("<body>");
		_printListingHeader(loc,
					 		apps,
					 		dateRange,
					 		lang,
					 		w);
		_printAppointments(loc,
						   appsByDate,
						   dateRange,
						   lang,
						   w);
		w.println("</body>");
		
		w.println("</html>");
    }
    private static void _printHtmlHead(final PrintWriter w) {
    	w.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"/appcont/aa14aAppointments/styles/aa14a-appointment_print_styles.css\" />");
    	
    	
    	w.println("<!-- jquery -->");
    	w.println("<!--[if lt IE 9]>");
    	w.println("<script type=\"text/javascript\" src=\"//ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js\"></script>");
    	w.println("<![endif]-->");
    	w.println("<!--[if gte IE 9]><!-->");
    	w.println("<script src=\"/appcont/aa14aAppointments/jquery/jquery-2.1.4.min.js\"></script>");
    	w.println("<!--<![endif]-->");
    	
    	w.println("<script src=\"/appcont/aa14aAppointments/scripts/aa14a-printAppointments.js\"></script>");
    }
    private void _printListingHeader(final AA14OrgDivisionServiceLocation loc,
    						  		 final Collection<AA14Appointment> apps,
    						  		 final Range<Date> dateRange,
    						  		 final Language lang,
    						  		 final PrintWriter w) {
    	final String dateFormat = _dateFormat(lang);
    	
    	// Header
    	w.println("<h1>");
		w.println(Strings.customized("{} ({})",
    							     loc.getNameByLanguage().get(lang),
    							     _formatDateRange(dateRange,
    							    		 		  dateFormat)));
    	w.println("</h1>");
    	w.println("<span>");
    	w.println(apps.size());
    	w.println(lang == Language.SPANISH ? "Citas" : "Hitzorduak");
    	w.println("</span>");
		w.println(Strings.customized("<input type='button' id='aa14_appointment_list_print' value='{}' />", lang == Language.SPANISH ? "Imprimir": "Inprimatu"));
		w.println("<hr />");
    	
    }
    private void _printAppointments(final AA14OrgDivisionServiceLocation loc,
    								final Map<LocalDate,Collection<AA14Appointment>> appsByDate,
    								final Range<Date> dateRange,
    								final Language lang,
    								final PrintWriter w) {
    	final String dateFormat = _dateFormat(lang);

    	
    	w.println("<div class='aa14SlotListing'>");
    	
    	
    	// Appointments table
    	if (CollectionUtils.isNullOrEmpty(appsByDate)) {
    		w.println("<table>");
    		w.println("<tr>");
    		w.println("<td>");
    		w.print(Strings.customized(lang == Language.SPANISH ? " -- No hay citas para el d&iacute;a {} -- " : "-- Ez dago eguneko {} hitzordurik -- ",
    								   _formatDateRange(dateRange,
    										   			dateFormat)));
    		w.println("</td>");
    		w.println("</td>");
    		w.println("</table>");
    	} 
    	else { 
    		for (LocalDate date : Ordering.natural().sortedCopy(appsByDate.keySet())) {
				w.println(Strings.customized("<h2>{}</h2>",
											 Dates.format(date.toDate(),dateFormat)));
				w.println("<table>");
				Collection<AA14Appointment> dayAppsOrderedByHour = Ordering.from(new Comparator<AA14Appointment>() {
																						@Override
																						public int compare(final AA14Appointment a1,final AA14Appointment a2) {
																							return a1.getStartTime().compareTo(a2.getStartTime());
																						}
																				 })
																			.sortedCopy(appsByDate.get(date));
				for (AA14Appointment app : dayAppsOrderedByHour) {
					_printAppointment(loc, 
									  app,
									  lang,dateFormat,
									  w);
				}
				w.println("</table>");
				
					
				
    		}
    	}
    	w.println("</div>");
    }
    private void _printAppointment(final AA14OrgDivisionServiceLocation loc,
    							   final AA14Appointment app,
    							   final Language lang,final String dateFormat,
    							   final PrintWriter w) {
		w.println("<tr>");
		
		// date
		w.print("<td class='aa14_date'>");
		w.print(Dates.format(app.getStartDate(),dateFormat));
		w.print("</td>");
		
		// start time 
		w.print("<td class='aa14_time'>");
		
		w.println(Strings.customized("{}:{}",
						StringUtils.leftPad(Integer.toString(app.getStartTime().getHourOfDay()),2,'0'),
						StringUtils.leftPad(Integer.toString(app.getStartTime().getMinuteOfHour()),2,'0'),
						app.getNumberOfAdjacentSlots().asInteger()>1?"*":""));
		w.print("</td>");
		
		// duration
		if (_scheduleBusinessDataPrinter.shouldPrintAppointmentDuration(loc)) {
			w.print("<td class='aa14_time'>");
			w.println(Strings.customized("{} min",
					  StringUtils.leftPad(Integer.toString(app.getDurationMinutes()*app.getNumberOfAdjacentSlots().getValue()),2,'0')));
			w.print("</td>");
		}
		
		// DNI
		w.print("<td class='aa14_person_id'>");
		w.println(app.getPerson().getId());
		w.print("</td>");
		
		// Name & surname
		w.print("<td class='aa14_person_name'>");
		w.println(app.getPerson().asSummarizable().getSummary());
		w.print("</td>");
		
		// Business data
		_scheduleBusinessDataPrinter.printBusinessData(loc,
													   app, 
													   w, 
													   lang);
		w.println("</tr>");
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    private static void _flushErrorToClient(final HttpServletResponse response,
    										final String error) throws IOException {
    	log.error(error);
		response.getWriter().write(error);
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.flushBuffer();
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the date format depending upon the language
     * @param lang
     * @return
     */
    private static String _dateFormat(final Language lang) {
    	return lang == Language.SPANISH || lang == null ? Dates.ES_DEFAULT_FORMAT 
    							 				 	    : Dates.EU_DEFAULT_FORMAT;
    }
    /**
     * Formats a date range
     * @param dateRange
     * @param dateFormat
     * @return
     */
    private static String _formatDateRange(final Range<Date> dateRange,
    									   final String dateFormat) {
    	String dateRangeFormatted = null;
    	LocalDate startLocalDate = new LocalDate(dateRange.getLowerBound());
    	LocalDate endLocalDate = new LocalDate(dateRange.getUpperBound());
    	if (startLocalDate.isEqual(endLocalDate)) {
    		dateRangeFormatted = Strings.customized("{}",
	    							   				Dates.format(dateRange.getLowerBound(),dateFormat));
    	} else {
	    	dateRangeFormatted = Strings.customized("{} - {}",
	    							   				Dates.format(dateRange.getLowerBound(),dateFormat),Dates.format(dateRange.getUpperBound(),dateFormat));
    	}
    	return dateRangeFormatted;
    }
}
