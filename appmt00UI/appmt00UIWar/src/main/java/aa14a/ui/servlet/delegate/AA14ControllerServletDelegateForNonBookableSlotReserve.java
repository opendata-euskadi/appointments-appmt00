package aa14a.ui.servlet.delegate;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import aa14a.ui.servlet.AA14ControllerOperation;
import aa14a.ui.servlet.AA14ReqParamToType;
import aa14f.client.api.AA14ClientAPI;
import aa14f.model.AA14NonBookableSlot;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.UserCode;
import r01f.locale.Language;
import r01f.model.persistence.PersistenceException;
import r01f.model.persistence.PersistenceServiceErrorTypes;
import r01f.servlet.HttpRequestParamsWrapper;

@Slf4j
@RequiredArgsConstructor
public class AA14ControllerServletDelegateForNonBookableSlotReserve
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
			log.warn("[init]: NON-Bookable slot reserve-----------------");
			
			// common data
			AA14ScheduleID schId = reqParams.getMandatoryParameter("schId")
									 	    .asType(AA14ScheduleID.class)
					 						.using(AA14ReqParamToType.transform(AA14ScheduleID.class));
			AA14Schedule sch = _clientAPI.configAPI()
										 .getScheduleFor(schId);
			if (sch == null) throw new IllegalArgumentException(schId + " is NOT a valid schedule!");
			
			int numResources = Integer.parseInt(reqParams.getMandatoryParameter("numResources")
															.asString()
															.substring(1));	
			if (numResources <= 0 
			 || numResources > sch.getBookingConfig().getMaxAppointmentsInSlot()) throw new IllegalArgumentException("The max appointments for schedule " + sch.getId() + " is " + sch.getBookingConfig().getMaxAppointmentsInSlot() + " " + numResources + " is NOT a valid value!");

			AA14SlotOID oid = reqParams.getParameter("slotOid")
									   .asOid(AA14SlotOID.class)
				 					   .using(AA14ReqParamToType.transform(AA14SlotOID.class))
				 					   .orNull();
			Language lang = reqParams.getMandatoryParameter("lang")
									 .asLanguageFromCountryCode();
			Date date = reqParams.getMandatoryParameter("dateNonBookable")
								  	.asDate(lang == Language.SPANISH ? "dd/MM/yyyy" : "yyyy/MM/dd");
			DateTime timeStartNonBookable = _parseDateTime(reqParams.getMandatoryParameter("timeStartNonBookable").asString());
			DateTime timeEndNonBookable = _parseDateTime(reqParams.getMandatoryParameter("timeEndNonBookable").asString());
			String nonBookableSubject = reqParams.getParameter("subject").asString().orDefault("----");
			UserCode userCode = reqParams.getParameter("userCode")
										 .asType(UserCode.class)
					 					 .using(AA14ReqParamToType.transform(UserCode.class))
					 					 .orDefault(UserCode.forId("unknown"));
			
			// periodic
			boolean nonBookablePeriodic = reqParams.getParameter("periodicNonBookable").asString().orNull() != null;
			if (nonBookablePeriodic) {
				Date endDate = reqParams.getMandatoryParameter("dateNonBookablePeriodicEnd")
									  		.asDate(lang == Language.SPANISH ? "dd/MM/yyyy" : "yyyy/MM/dd");
				if (endDate == null) {
					log.error("The user MUST select an end date in order to create a periodic non-bookable slot");
					response.getWriter().write("The user MUST select an end date in order to create a periodic non-bookable slot");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.flushBuffer();
				} else {
						
					boolean sunday = reqParams.getParameter("sundayNonBookablePeriodic").asString().orNull() != null;
					boolean monday = reqParams.getParameter("mondayNonBookablePeriodic").asString().orNull() != null;
					boolean tuesday = reqParams.getParameter("tuesdayNonBookablePeriodic").asString().orNull() != null;
					boolean wednesday = reqParams.getParameter("wednesdayNonBookablePeriodic").asString().orNull() != null;
					boolean thursday = reqParams.getParameter("thursdayNonBookablePeriodic").asString().orNull() != null;
					boolean friday = reqParams.getParameter("fridayNonBookablePeriodic").asString().orNull() != null;
					boolean saturday = reqParams.getParameter("saturdayNonBookablePeriodic").asString().orNull() != null;
					
					int numCreatedSlots = 0;
					for (int i=0; i < numResources; i++) {
						int n =	_clientAPI.bookedSlotsAPI()
												.createPeriodicNonBookableSlots(schId,
																			    date,endDate,
																			    timeStartNonBookable,timeEndNonBookable, 
																			    sunday,monday,tuesday,wednesday,thursday,friday,saturday,
																			    nonBookableSubject,
																			    userCode);
						log.debug("\t...created {} slots",numCreatedSlots);
						numCreatedSlots += n;
					}
					_returnJsonResponse(response,
										numCreatedSlots);
				}
				
			} else {
				try {
					// create or update the non-bookable slot
					int numCreatedSlots = 0;
					for (int i=0; i < numResources; i++) {
						AA14NonBookableSlot slot = _clientAPI.bookedSlotsAPI()
															 .createOrUpdateNonBookableSlot(oid,
																						    schId,
																						    date,timeStartNonBookable,timeEndNonBookable, 
																						    nonBookableSubject,
																						    userCode);
						if (slot != null) log.info("\t...created 1 slot with oid={}",
								 				   slot.getOid());
						numCreatedSlots++;
					}
					_returnJsonResponse(response,
										numCreatedSlots);		// 1 slot created or updated
				} catch (PersistenceException thrownPersistEx) {
					log.error("Error persisting the non-bookable slot: {}",thrownPersistEx.getMessage(),
							  thrownPersistEx);
					
					if (thrownPersistEx.is(PersistenceServiceErrorTypes.ENTITY_ALREADY_EXISTS)
					 && thrownPersistEx.getExtendedCode() == 1) {
						// the slot was occupied when it was tried to be booked
						response.getWriter().write("The user MUST select another slot since the selected one was occupied when it was tried to be booked: " + thrownPersistEx.getCode()); 
					}
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.flushBuffer();
				} catch(Throwable thrownExWhenPersistingSlot) {
					log.error("Error persisting the non-bookable slot: {}",thrownExWhenPersistingSlot.getMessage(),
							  thrownExWhenPersistingSlot);
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.flushBuffer();
				}
			}
			
			log.warn("[end]: NON-Bookable slot reserve-----------------");
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static DateTime _parseDateTime(final String dateTimeStr) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");	// if 12:10AM format= hh:mma (a=am/pm)
		DateTime dt = formatter.parseDateTime(dateTimeStr);
		return dt;
	}
}
