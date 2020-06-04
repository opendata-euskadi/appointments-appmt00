package aa14a.ui.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import aa14f.client.api.AA14ClientAPI;
import aa14f.model.AA14Appointment;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14ScheduleBookingConfig;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import aa14f.model.summaries.AA14SummarizedOrgHierarchy;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.servlet.HttpRequestParamsWrapper;
import r01f.util.types.Dates;

@Slf4j
@Singleton
public class AA14ModelObjectsLoadServlet 
     extends HttpServlet {
       
	private static final long serialVersionUID = -4112043120887421640L;
/////////////////////////////////////////////////////////////////////////////////////////
//  INJECTED FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private AA14ClientAPI _clientAPI;
	/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    public AA14ModelObjectsLoadServlet() {
        super();
    } 
    @Inject
    public AA14ModelObjectsLoadServlet(final AA14ClientAPI api) {
    	_clientAPI = api;
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
		
		// get the operation
		AA14ModelObjectsLoadOperation op = AA14ModelObjectsLoadOperation.from(request);
		
		// ::: Load appointment by oid
		if (op.is(AA14ModelObjectsLoadOperation.APPOINTMENT_BY_OID)) {
			AA14SlotOID	slotOid = reqParams.getMandatoryParameter("appointmentOid")
												.asOid(AA14SlotOID.class)
												.using(AA14ReqParamToType.transform(AA14SlotOID.class));
			log.debug("[init]: Load appointment with oid: {}-----------------",slotOid);
			AA14Appointment cita = _clientAPI.bookedSlotsAPI()
											 .getForCRUD()
											 .load(slotOid)
											 .as(AA14Appointment.class);
			_returnJsonResponse(response,
								cita); 
			log.debug(" [end]: Load appointment with oid: {}-----------------",slotOid);
		} 
		// ::: Load a location by it's oid
		else if (op.is(AA14ModelObjectsLoadOperation.LOCATION_BY_OID)) {
			AA14OrgDivisionServiceLocationOID locOid = reqParams.getParameter("locOid")
																.asType(AA14OrgDivisionServiceLocationOID.class)
																.using(AA14ReqParamToType.transform(AA14OrgDivisionServiceLocationOID.class))
																.orNull();
			log.debug("[init]: Load location with oid: {}-----------------",locOid);
			AA14OrgDivisionServiceLocation loc = _clientAPI.configAPI()
														   .getLocationFor(locOid);
			_returnJsonResponse(response,loc);
			log.debug(" [end]: Load location with oid: {}-----------------",locOid);
		}
		// ::: Load a location by it's id
		else if (op.is(AA14ModelObjectsLoadOperation.LOCATION_BY_ID)) {
			AA14OrgDivisionServiceLocationID locId = reqParams.getParameter("locId")
															  .asType(AA14OrgDivisionServiceLocationID.class)
															  .using(AA14ReqParamToType.transform(AA14OrgDivisionServiceLocationID.class))
															  .orNull();
			log.debug("[init]: Load location with id: {}-----------------",locId);
			AA14OrgDivisionServiceLocation loc = _clientAPI.configAPI()
														   .getLocationFor(locId);
			if (loc == null) {
				_flushErrorToClient(response,
									"Location with id=" + locId + " was NOT found");
			} else {
				_returnJsonResponse(response,
									loc);
			}			
			log.debug(" [end]: Load location with id: {}-----------------",locId);
		}
		// ::: Load the org hierarchy where a certain location belongs
		else if (op.is(AA14ModelObjectsLoadOperation.lOCATION_ORG_HIERARCHY_BY_OID)) {
			AA14OrgDivisionServiceLocationOID locOid = reqParams.getParameter("locOid")
																.asType(AA14OrgDivisionServiceLocationOID.class)
																.using(AA14ReqParamToType.transform(AA14OrgDivisionServiceLocationOID.class))
																.orNull();
			Language lang = reqParams.getParameter("lang")
									 .asType(Language.class)
									 .using(AA14ReqParamToType.transform(Language.class))
									 .orNull();
			log.debug("[init]: Load the org hierarchy where a location oid={} belongs-----------------",locOid);
			AA14SummarizedOrgHierarchy hierarchy = _clientAPI.configAPI()
															 .getOrgHierarchyFor(locOid,
																			     lang);
			_returnJsonResponse(response,hierarchy);
			log.debug(" [end]: Load the org hierarchy where a location oid={} belongs-----------------",locOid);
		}
		// ::: Load the org hierarchy where a certain location belongs
		else if (op.is(AA14ModelObjectsLoadOperation.lOCATION_ORG_HIERARCHY_BY_ID)) {
			AA14OrgDivisionServiceLocationID locId = reqParams.getParameter("locId")
															  .asType(AA14OrgDivisionServiceLocationID.class)
															  .using(AA14ReqParamToType.transform(AA14OrgDivisionServiceLocationID.class))
															  .orNull();
			Language lang = reqParams.getParameter("lang")
									 .asType(Language.class)
									 .using(AA14ReqParamToType.transform(Language.class))
									 .orNull();
			log.debug("[init]: Load the org hierarchy where a location id={} belongs-----------------",locId);
			AA14SummarizedOrgHierarchy hierarchy = _clientAPI.configAPI()
															 .getOrgHierarchyFor(locId,
																			     lang);
			if (hierarchy == null) {
				_flushErrorToClient(response,
									"Location with id=" + locId + " was NOT found");
			} else {
				_returnJsonResponse(response,
									hierarchy);
			}			
			log.debug(" [end]: Load the org hierarchy where a location id={} belongs-----------------",locId);
		}
		// ::: Load booking config for a certain location
		//	   BEWARE!	The calendar / booking config is associated with the SCHEDULE, not with the LOCATION or SERVICE,
		//				any location can be associated with multiple schedules BUT all of them MUST have
		//				the same booking config
		else if (op.is(AA14ModelObjectsLoadOperation.BOOKING_CONFIG_BY_LOCATION_OID)) {
			AA14OrgDivisionServiceLocationOID locOid = reqParams.getParameter("locOid")
															  .asType(AA14OrgDivisionServiceLocationOID.class)
															  .using(AA14ReqParamToType.transform(AA14OrgDivisionServiceLocationOID.class))
															  .orNull();
			log.debug("[init]: Get the booking config by location oid: {}-----------------",locOid);
			AA14ScheduleBookingConfig schBookingConfig = _clientAPI.configAPI()
																   .getScheduleBookingConfigFor(locOid);
			
			if (schBookingConfig == null) {
				_flushErrorToClient(response,
									"Location with oid=" + locOid + " was NOT found");
			} else {
				_returnJsonResponse(response,
									schBookingConfig);
			}
			log.debug("[end]: Get the booking config by location id: {}-----------------",locOid);
		}
		// ::: Load booking config for a certain location
		//	   BEWARE!	The calendar / booking config is associated with the SCHEDULE, not with the LOCATION or SERVICE,
		//				any location can be associated with multiple schedules BUT all of them MUST have
		//				the same booking config
		else if (op.is(AA14ModelObjectsLoadOperation.BOOKING_CONFIG_BY_LOCATION_ID)) {
			AA14OrgDivisionServiceLocationID locId = reqParams.getParameter("locId")
															  .asType(AA14OrgDivisionServiceLocationID.class)
															  .using(AA14ReqParamToType.transform(AA14OrgDivisionServiceLocationID.class))
															  .orNull();
			log.debug("[init]: Get the booking config by location id: {}-----------------",locId);
			AA14ScheduleBookingConfig schBookingConfig = _clientAPI.configAPI()
																   .getScheduleBookingConfigFor(locId);
			
			if (schBookingConfig == null) {
				_flushErrorToClient(response,
									"Location with id=" + locId + " was NOT found");
			} else {
				_returnJsonResponse(response,
									schBookingConfig);
			}
			log.debug("[end]: Get the booking config by location id: {}-----------------",locId);
		}
		// ::: Load booking config for a certain schedule
		else if (op.is(AA14ModelObjectsLoadOperation.BOOKING_CONFIG_BY_SCHEDULE_OID)) {
			AA14ScheduleOID schOid = reqParams.getParameter("schOid")
											  .asType(AA14ScheduleOID.class)
											  .using(AA14ReqParamToType.transform(AA14ScheduleOID.class))
											  .orNull();
			log.debug("[init]: Get the booking config by schedule oid: {}-----------------",schOid);
			AA14ScheduleBookingConfig schBookingConfig = _clientAPI.configAPI()
																   .getScheduleBookingConfigFor(schOid);
			
			if (schBookingConfig == null) {
				_flushErrorToClient(response,
									"Schedule with oid=" + schOid + " was NOT found");
			} else {
				_returnJsonResponse(response,
									schBookingConfig);
			}
			log.debug("[end]: Get the booking config by schedule oid: {}-----------------",schOid);
		}
		// ::: Load booking config for a certain schedule
		else if (op.is(AA14ModelObjectsLoadOperation.BOOKING_CONFIG_BY_SCHEDULE_ID)) {
			AA14ScheduleID schId = reqParams.getParameter("schId")
											.asType(AA14ScheduleID.class)
										    .using(AA14ReqParamToType.transform(AA14ScheduleID.class))
										    .orNull();
			log.debug("[init]: Get the booking config by schedule id: {}-----------------",schId);
			AA14ScheduleBookingConfig schBookingConfig = _clientAPI.configAPI()
																   .getScheduleBookingConfigFor(schId);
			if (schBookingConfig == null) {
				_flushErrorToClient(response,
									"Schedule with oid=" + schId + " was NOT found");
			} else {
				_returnJsonResponse(response,
									schBookingConfig);
			}
			log.debug("[end]: Get the booking config by schedule id: {}-----------------",schId);
		}
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _returnJsonResponse(final HttpServletResponse response,
							   		 		final Object obj) throws IOException {
		Gson gson = new GsonBuilder().setDateFormat(Dates.ISO8601) //avoid deprecation warning with moment.js
									 .create();
		String json =gson.toJson(obj);
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
