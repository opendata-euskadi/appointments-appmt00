package aa14a.ui.servlet.delegate;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;

import com.google.common.collect.Lists;

import aa14a.ui.servlet.AA14ControllerOperation;
import aa14a.ui.servlet.AA14ReqParamToType;
import aa14f.client.api.AA14ClientAPI;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14PersonLocatorID;
import aa14f.model.oids.AA14OIDs.AA14AppointmentSubjectID;
import aa14f.model.search.AA14AppointmentFilter;
import aa14f.model.summaries.AA14SummarizedAppointment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.servlet.HttpRequestParamsWrapper;
import r01f.types.Range;
import r01f.types.contact.NIFPersonID;
import r01f.util.types.Dates;

@Slf4j
@RequiredArgsConstructor
public class AA14ControllerServletDelegateForAppointmentFind
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
		log.debug("[init]: Find Appointments-----------------");
		
		Language lang = reqParams.getParameter("lang")
								 .asLanguageFromCountryCode()
								 .orDefault(Language.SPANISH);

		// service & location
		AA14OrgDivisionServiceID serviceId = reqParams.getParameter("serviceId")
													  .asType(AA14OrgDivisionServiceID.class)
							 						  .using(AA14ReqParamToType.transform(AA14OrgDivisionServiceID.class))
							 						  .orNull();
		AA14OrgDivisionServiceLocationID serviceLocId = reqParams.getParameter("serviceLocId")
													  		     .asType(AA14OrgDivisionServiceLocationID.class)
										 						 .using(AA14ReqParamToType.transform(AA14OrgDivisionServiceLocationID.class))
										 						 .orNull();
	

		// get the date range
		String dateFormat = lang.is(Language.SPANISH) ? Dates.ES_DEFAULT_FORMAT
													  : Dates.EU_DEFAULT_FORMAT;
		Date startDate = reqParams.getParameter("search_start_date").asDate(dateFormat)
																    .orNull();
		Date endDate = reqParams.getParameter("search_end_date").asDate(dateFormat)
															    .orNull();
		
		Range<Date> dateRange = null;
		if (startDate != null || endDate != null) {
			// ensure the first moment of the start date and the last moment of the end date
			DateTime startDateFirstInstant = null;
			DateTime endDateLastInstant = null;
			if (startDate != null) {
				startDateFirstInstant = new DateTime(startDate)
											.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
			}
			if (endDate != null) {
				endDateLastInstant = new DateTime(endDate)
											.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
			}
			if( startDate != null && endDate!= null) {	
				dateRange= Range.closed(startDateFirstInstant.toDate(),
										endDateLastInstant.toDate());
			} else if (startDateFirstInstant != null) {
				dateRange = Range.atLeast(startDateFirstInstant.toDate());
			} else {
				dateRange = Range.atMost(endDateLastInstant.toDate());
			}
		}
		
		// person details
		NIFPersonID nif = reqParams.getParameter("nif")
								   .asType(NIFPersonID.class)
		 						   .using(AA14ReqParamToType.transform(NIFPersonID.class))
		 						   .orNull();
		AA14AppointmentSubjectID codExp = reqParams.getParameter("codExp")
												   .asType(AA14AppointmentSubjectID.class)
						 						   .using(AA14ReqParamToType.transform(AA14AppointmentSubjectID.class))
						 						   .orNull();
		AA14PersonLocatorID locator = reqParams.getParameter("locator")
											   .asType(AA14PersonLocatorID.class)
					 						   .using(AA14ReqParamToType.transform(AA14PersonLocatorID.class))
					 						   .orNull();
		
		// ensure that at least the nif / codExp or locator are present
		if (nif == null && codExp == null && locator == null) {
			log.warn("some of nif, codExp or locator are needed to find an appointment: NO appointments will be returned");
			_returnJsonResponse(response,
							    Lists.newArrayList());
		} else {
			//if (serviceId == null && serviceLocId == null) 
			//	throw new IllegalArgumentException("Either the service or the location is needed to search for an appointment!");
			
			// create the filter
			AA14AppointmentFilter filter = new AA14AppointmentFilter();
			filter.setPersonId(nif);
			filter.setSubjectId(codExp);
			filter.setServiceId(serviceId);
			filter.setServiceLocationId(serviceLocId);
			filter.setDateRange(dateRange);
			filter.setPersonLocatorId(locator);
			
			// find
			Collection <AA14SummarizedAppointment> appointments = _clientAPI.bookedSlotsAPI()
																			.getForFind()
																				.findAppointmentsBy(filter, 
																									lang);		
			_returnJsonResponse(response,
							    appointments);
		}
		log.debug("[end]: Find Appointments-----------------");
	}
}
