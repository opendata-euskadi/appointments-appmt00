package aa14b.calendar;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import aa14b.calendar.orchestra.model.AA14OrchestraAvailableSlotDates;
import aa14b.calendar.orchestra.model.AA14OrchestraAvailableSlots;
import aa14b.calendar.orchestra.model.AA14OrchestraBookedSlot;
import aa14b.calendar.orchestra.model.AA14OrchestraBranchList;
import aa14b.calendar.orchestra.model.AA14OrchestraBranchServicesList;
import aa14b.calendar.orchestra.model.AA14OrchestraIDs.AA14OrchestraBranchID;
import aa14b.calendar.orchestra.model.AA14OrchestraIDs.AA14OrchestraBranchServiceID;
import aa14b.calendar.orchestra.model.AA14OrchestraModelObject;
import aa14b.calendar.orchestra.model.AA14OrchestraNewSlotReservation;
import aa14f.model.AA14Appointment;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14NonBookableSlot;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import lombok.Cleanup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.Password;
import r01f.guids.CommonOIDs.UserCode;
import r01f.httpclient.HttpClient;
import r01f.httpclient.HttpRequestPayload;
import r01f.httpclient.HttpResponseCode;
import r01f.internal.R01F;
import r01f.io.util.StringPersistenceUtils;
import r01f.locale.Language;
import r01f.mime.MimeTypes;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.model.persistence.PersistenceServiceErrorTypes;
import r01f.types.IsPath;
import r01f.types.Path;
import r01f.types.contact.ContactInfo;
import r01f.types.contact.NIFPersonID;
import r01f.types.contact.Person;
import r01f.types.url.Host;
import r01f.types.url.Url;
import r01f.types.url.UrlPath;
import r01f.util.types.Dates;
import r01f.util.types.Strings;


/**
 * QMatic REST serivces TEST page: http://txandak.jakina.ejgvdns/test/
 * 		 [Connector]: /calendar-backend/public/api/v1/
 * 		 [REST]     : the remaining of the REST url (see examples below)
 *       [Type]		: see examples below 
 *       [JSON]		: the json to be sent
 *      
 * 
 * 
 * Logic model:
 * 		Branch -location- (ie Bilbao)
 *         |
 * 	 	   |-- Profile ------------->Service (ie Zuzenean)
 * 		   |				           |  |				|--Available dates
 * 		   |			       /       |  |						|--Available appointable slots
 *         |-- Resources Group --------   |
 *                  |          \          |
 *                  |-- Members           |
 *                                       \|/
 *      Schedules (ie Zuzenean Bilbao schedule)
 * 
 * Branches
 * ======== 
 * GET: http://txandak.jakina.ejgvdns/calendar-backend/public/api/v1/branches
 * Response:   
 *	    "branchList": [
 *	    	{
 *	    		"publicId": "46",
 *	    		"branchPrefix": null,
 *	    		"updated": 1434536886155,
 *	    		"created": 1434530694520,
 *	    		"name": "Tráfico Bilbao",
 *	    		"addressCountry": "España",
 *	    		"addressState": null,
 *	    		"addressCity": "Bilbao",
 *	    		"addressZip": null,
 *	    		"addressLine1": null,
 *	    		"addressLine2": null,
 *	    		"timeZone": "Europe/Madrid",
 *	    		"phone": null,
 *	    		"email": null,
 *	    		"longitude": null,
 *	    		"latitude": null,
 *	    		"custom": null
 *	    	},
 *	    	...
 *	    ],
 *	    "notifications": [],
 *      "meta": {
 *         	"totalResults": 46,
 *         	"start": "",
 *         	"end": "",
 *         	"offset": null,
 *         	"limit": null,
 *         	"fields": "",
 *         	"arguments": { }
 *       }
 *          
 * NOTE: Zuzenean trafikoa:
 * 		Branch  		 BranchPublicID
 *      -------------------------------
 * 		Tráfico bilbao         46
 *      Tráfico Donosti		   47
 *      Tráfico Vitoria		   48
 *      
 * Branch services
 * ===============
 * GET: http://txandak.jakina.ejgvdns/calendar-backend/public/api/v1/branches/{BranchPublicID}/services
 * Response: Bilbao branch (id=46) services
 *	    "serviceList": [
 *	    	{
 *	    	"publicId": "23",
 *	    	"updated": 1434610613102,
 *	    	"created": 1434530657641,
 *	    	"active": true,
 *	    	"publicEnabled": true,
 *	    	"name": "Trafiko",
 *	    	"duration": 1,
 *	    	"additionalCustomerDuration": 0,
 *	    	"custom": null
 *	    	},
 *			...
 *      ],
 *	    "notifications": [],
 *	    "meta": {
 *	    	"totalResults": 1,
 *	    	"start": "",
 *	    	"end": "",
 *	    	"offset": null,
 *	    	"limit": null,
 *	    	"fields": "",
 *	    	"arguments": { }
 *	    }
 *
 * Branch service available dates
 * ============================== 
 * GET: http://txandak.jakina.ejgvdns/calendar-backend/public/api/v1/branches/{BranchPublicID}/services/{ServicePublicID}/dates
 * Response: Bilbao (id=46) Trafiko service (id=23) dates
 *    	"dates": [
 *          "2015-08-21T00:00:00",
 *    	    "2015-08-24T00:00:00",
 *    	    "2015-08-25T00:00:00",
 *    	    "2015-08-26T00:00:00",
 *    	    "2015-08-27T00:00:00",
 *    	    "2015-08-28T00:00:00",
 *    	    "2015-08-31T00:00:00",
 *    	    "2015-09-01T00:00:00",
 *    	    "2015-09-02T00:00:00",
 *    	    "2015-09-03T00:00:00",
 *    	    "2015-09-04T00:00:00",
 *    	    "2015-09-07T00:00:00",
 *    	    "2015-09-08T00:00:00",
 *    	    "2015-09-09T00:00:00",
 *    	    "2015-09-10T00:00:00",
 *    	    "2015-09-11T00:00:00",
 *    	    "2015-09-14T00:00:00",
 *    	    "2015-09-15T00:00:00",
 *    	    "2015-09-16T00:00:00",
 *    	    "2015-09-17T00:00:00",
 *    	    "2015-09-18T00:00:00"
 *      ],
 *    	"notifications": [],
 *    	"meta": {
 *    		"totalResults": 21,
 *    		"start": "",
 *    		"end": "",
 *    		"offset": null,
 *    		"limit": null,
 *    		"fields": "",
 *    		"arguments": { }
 *    	}
 *    
 * Branch service available slots at a certain date
 * ================================================  
 * GET: http://txandak.jakina.ejgvdns/calendar-backend/public/api/v1/branches/{BranchPublicID}/services/{ServicePublicID}/dates/yyyy-MM-dd/times
 * Response: Bilbao (id=46) Trafiko service (id=23) slots at 2015-08-21
 *    	"times": [
 *          "09:00",
 *    	    "09:15",
 *    	    "09:30",
 *    	    "09:45",
 *    	    "10:00",
 *    	    "10:15",
 *    	    "10:30",
 *    	    "10:45",
 *    	    "11:00",
 *    	    "11:15",
 *    	    "11:30",
 *    	    "11:45",
 *    	    "12:00",
 *    	    "12:15",
 *    	    "12:30",
 *    	    "12:45",
 *    	    "13:00",
 *    	    "13:15",
 *    	    "13:30",
 *    	    "13:45"
 *      ],
 *    	"notifications": [],
 *    	"meta": {
 *    		"start": "",
 *    		"end": "",
 *    		"totalResults": 20,
 *    		"offset": null,
 *    		"limit": null,
 *    		"fields": "",
 *    		"arguments": { }
 *    	}
 *    
 * Create an appointment
 * =====================
 * POST: http://txandak.jakina.ejgvdns/calendar-backend/public/api/v1/branches/{BranchPublicID}/services/{ServicePublicID}/dates/yyyy-MM-dd/times/HH:mm/book
 *    	"title": "Test Appointment",
 *    	"notes": "This is a test appointment",
 *    	"customer": {
 *    		"firstName": "My name",
 *    		"lastName": "is god",
 *    		"email": "a-lara@ejie.eus",
 *    		"externalId": "30639781D",
 *    		"phone": "688671967"
 *    	}
 * Response: contains info about 
 * 						- the created appointment
 * 						- the customer
 * 						- branch / service
 *    	"publicId": "f495b7e57738062f4995dabc219a25a56ce1667a07575d0fa7f3e0ad4fe15ef2",
 *    	"created": 1440140869688,
 *    	"updated": 1440140869708,
 *    	"title": "Test Alex Appointment Fri Aug 21 09:04:44 CEST 2015",
 *    	"notes": "A test appointment",
 *    	"start": "2015-08-24T07:15:00.000+0000",
 *    	"end": "2015-08-24T07:16:00.000+0000",
 *    	"allDay": false,
 *    	"status": 20,
 *    	"custom": null,
 *    	"resource": {
 *    		"appointments": null,
 *    		"name": "R_Trafico_1",
 *    		"custom": "{\"color\":\"#1b9e4f\"}"
 *    	},
 *    	"customers": [
 *    		{
 *    			"publicId": "44ee8fcefb0cb4a47abd3a8030d1a95b328dc9720ffa4e11c96d6d8b54da903d",
 *    			"created": 1440140869536,
 *    			"updated": null,
 *    			"identificationNumber": null,
 *    			"externalId": "30639781D",
 *    			"firstName": "Alex",
 *    			"lastName": "Lara Garachana",
 *    			"addressCountry": null,
 *    			"addressState": null,
 *    			"addressCity": null,
 *    			"addressZip": null,
 *    			"addressLine2": null,
 *    			"addressLine1": null,
 *    			"phone": "688671967",
 *    			"email": "a-lara@ejie.eus",
 *    			"dateOfBirth": null,
 *    			"custom": null
 *    		}
 *    	],
 *    	"branch": {
 *    		"publicId": "46",
 *    		"updated": 1434536886155,
 *    		"created": 1434530694520,
 *    		"branchPrefix": null,
 *    		"name": "Tráfico Bilbao",
 *    		"timeZone": "Europe/Madrid",
 *    		"addressCountry": "España",
 *    		"addressState": null,
 *    		"addressCity": "Bilbao",
 *    		"addressZip": null,
 *    		"addressLine2": null,
 *    		"addressLine1": null,
 *    		"phone": null,
 *    		"email": null,
 *    		"longitude": null,
 *    		"latitude": null,
 *    		"custom": null
 *    	},
 *    	"services": [
 *    		{
 *    			"publicId": "23",
 *    			"name": "Trafiko",
 *    			"updated": 1434610613102,
 *    			"created": 1434530657641,
 *    			"active": true,
 *    			"publicEnabled": true,
 *    			"duration": 1,
 *    			"additionalCustomerDuration": 0,
 *    			"custom": null
 *    		}
 *    	]
 *    
 * Delete an appointment
 * =====================
 * DELETE: http://txandak.jakina.ejgvdns/calendar-backend/public/api/v1/appointments/{appointmentId}
 * Response: code 204 -no content- (does NOT returns anything)
 */
@Slf4j
@Accessors(prefix="_")
@RequiredArgsConstructor
public class AA14QMaticOrchestraMediator {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final Host _host;
	@Getter private final int _port;
	@Getter private final Charset _charset;
	
	@Getter private final UserCode _userCode;
	@Getter private final Password _password;
	
	@Getter private final Path _endpointBaseUrl;
	
	@Getter private final ObjectMapper _jsonObjectsMapper;
	
//	private final VelocityEngine _velocityEngine;
//	private final Path _appointmentBookJsonMessageTemplatePath;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  LIST METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a list of all branches
	 * @return 
	 */
	@SuppressWarnings("cast")
	public AA14OrchestraBranchList listBranches() {
		// http://txandak.jakina.ejgvdns/calendar-backend/public/api/v1/branches
		Url branchesUrl = Url.from(_host,_port,
			   				        UrlPath.from((IsPath)_endpointBaseUrl.joinedWith("branches")));
		String branchesJson = _get(branchesUrl,	
						   		   _userCode,_password);
		return _jsonToOrchestraModelObject(_jsonObjectsMapper,
				  					       branchesJson,
										   AA14OrchestraBranchList.class);
	}
	/**
	 * Returns a list of all services at a branch
	 * @param branchId
	 * @return
	 */
	@SuppressWarnings("cast")
	public AA14OrchestraBranchServicesList listBranchServices(final AA14OrchestraBranchID branchId) {
		//  http://txandak.jakina.ejgvdns/calendar-backend/public/api/v1/branches/{BranchPublicID}/services
		Url branchServicesUrl = Url.from(_host,_port,
					 				      UrlPath.from((IsPath)_endpointBaseUrl.joinedWith("branches",branchId,				
					 						   					   	  			           "services")));
		String branchServicesJson = _get(branchServicesUrl,
										 _userCode,_password);
		return _jsonToOrchestraModelObject(_jsonObjectsMapper,
										   branchServicesJson,
										   AA14OrchestraBranchServicesList.class);
	}
	/**
	 * Returns a list all dates available to book appointments in a branch's service
	 * @param branchId
	 * @param serviceId
	 * @return
	 */
	@SuppressWarnings("cast")
	public AA14OrchestraAvailableSlotDates listBranchServiceAvailableDates(final AA14OrchestraBranchID branchId,final AA14OrchestraBranchServiceID serviceId) {
		//  http://txandak.jakina.ejgvdns/calendar-backend/public/api/v1/branches/{BranchPublicID}/services/{ServicePublicID}/dates
		Url branchServiceDatesUrl = Url.from(_host,_port,
									   	     UrlPath.from((IsPath)_endpointBaseUrl.joinedWith("branches",branchId,				
									   			   					     			  		  "services",serviceId,
									   	    		 										  "dates")));
		String branchServiceDatesJson = _get(branchServiceDatesUrl,
										 	 _userCode,_password);
		return _jsonToOrchestraModelObject(_jsonObjectsMapper,
										   branchServiceDatesJson,
										   AA14OrchestraAvailableSlotDates.class);
	}
	/**
	 * Returns a list of all slots available to book appointments at a certain date in a branch's service
	 * @param branchId
	 * @param serviceId
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	@SuppressWarnings("cast")
	public AA14OrchestraAvailableSlots listBranchServiceAvailableSlotsAtDate(final AA14OrchestraBranchID branchId,final AA14OrchestraBranchServiceID serviceId,
																			 final int year,final int monthOfYear,final int dayOfMonth) {
		//  http://txandak.jakina.ejgvdns/calendar-backend/public/api/v1/branches/{BranchPublicID}/services/{ServicePublicID}/dates/yyyy-MM-dd/times
		Url branchServiceDateSlotsUrl = Url.from(_host,_port,
							 				      UrlPath.from((IsPath)_endpointBaseUrl.joinedWith("branches",branchId,			
							 						   					   	  			   		   "services",serviceId,	
							 						   					   	  			   		   "dates",Dates.format(new LocalDate(year,monthOfYear,dayOfMonth).toDate(),
							 						   					   	  					   						"yyyy-MM-dd"),	// (ensure this date is within the previously returned available dates)
							 						   				       	  			   		   "times")));
		String branchServiceDateSlotsJson = _get(branchServiceDateSlotsUrl,
										 	 	 _userCode,_password);
		return _jsonToOrchestraModelObject(_jsonObjectsMapper,
										   branchServiceDateSlotsJson,
										   AA14OrchestraAvailableSlots.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  APPOINTMENTS
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Books an appointment to a service in a branch
	 * @param branchId
	 * @param serviceId
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @param hourOfDay
	 * @param minutesOfHour
	 * @param appointmentTitle
	 * @param appointmentNotes
	 * @param customer
	 * @param customerContact
	 * @return
	 */
	@SuppressWarnings("cast")
	public AA14OrchestraBookedSlot reserveSlot(final AA14OrchestraBranchID branchId,final AA14OrchestraBranchServiceID serviceId,
								  			   final AA14BookedSlot slot) {
		//  http://txandak.jakina.ejgvdns/calendar-backend/public/api/v1/branches/{BranchPublicID}/services/{ServicePublicID}/dates/yyyy-MM-dd/times/09:00/book
		Url branchServiceDateSlotsBookUrl = Url.from(_host,_port,
												      UrlPath.from((IsPath)_endpointBaseUrl.joinedWith("branches",branchId,			
																	   					   	   		   "services",serviceId,	
																	   					   	   		   "dates",Dates.format(new LocalDate(slot.getYear().asInteger(),
																	   					   			   							  		  slot.getMonthOfYear().asInteger(),
																	   					   			   							  		  slot.getDayOfMonth().asInteger())
																	   					   			   								.toDate(),
																	   					   	   				   				"yyyy-MM-dd"),		// (ensure this date is within the previously returned available dates)
																			   					   	   "times",Strings.customized("{}:{}",
																	   													          String.format("%02d",slot.getHourOfDay()),
																	   													          String.format("%02d",slot.getMinuteOfHour())),// (ensure this time is within the previously returned available slots)
										 	 											       			"book")));
//		String slotBookJsonMessage = _composeNewAppointmentJson(_velocityEngine,_appointmentBookJsonMessageTemplatePath, 
//																appointmentTitle,appointmentNotes,
//																customer,customerContact);
		
		String newSlotJson =	_composeSlotJson(_jsonObjectsMapper,
												 slot);
		String bookedSlotJsonResponse = _post(_charset,
											  branchServiceDateSlotsBookUrl,
											  _userCode,_password,
											  newSlotJson);
		return _jsonToOrchestraModelObject(_jsonObjectsMapper,
										   bookedSlotJsonResponse,
										   AA14OrchestraBookedSlot.class);
	}
	@SuppressWarnings("cast")
	public AA14OrchestraBookedSlot updateSlot(final AA14BookedSlot slot) {
		Url slotUrl = Url.from(_host,_port,
							   UrlPath.from((IsPath)_endpointBaseUrl.joinedWith("appointments",slot.getId())));
		String slotJson =	_composeSlotJson(_jsonObjectsMapper,
											 slot);
		String updatedSlotJsonResponse = _put(_charset,
											  slotUrl,
											  _userCode,_password,
											  slotJson);
		return _jsonToOrchestraModelObject(_jsonObjectsMapper,
										   updatedSlotJsonResponse,
										   AA14OrchestraBookedSlot.class);
	}
	/**
	 * Releases an slot
	 * @param slotId
	 * @return
	 */
	@SuppressWarnings("cast")
	public boolean releaseSlot(final AA14SlotID slotId) {
		Url slotUrl = Url.from(_host,_port,
							   UrlPath.from((IsPath)_endpointBaseUrl.joinedWith("appointments",slotId)));
		boolean appointmentCancelResult = _delete(slotUrl,
												  _userCode,_password);
		return appointmentCancelResult;		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  RAW GET / PUT / POST / DELETE
/////////////////////////////////////////////////////////////////////////////////////////
	private static String _get(final Url url,
					    	   final UserCode userCode,Password password) {
		log.debug("GET: {}",url);
		String outJson = null;
		try {
			HttpURLConnection conx = HttpClient.forUrl(url)
						  				 	   .GET()						  				 	   
						  				 	   .getConnection()
						  				 	   		.notUsingProxy()
						  				 	   		.withoutTimeOut()
						  				 	   		.usingBasicAuthCredentials(userCode,password);
			@Cleanup
			InputStream is = conx.getInputStream();
			outJson = StringPersistenceUtils.load(is);
			conx.disconnect();
		} catch(IOException ioEx) {
			throw new AA14CalendarServiceException(PersistenceRequestedOperation.LOAD,
															  ioEx.getMessage(),
															  PersistenceServiceErrorTypes.CLIENT_CANNOT_CONNECT_SERVER,99);
		}
		return outJson;
	}
	private static String _post(final Charset charset,
								final Url url,
						 		final UserCode userCode,Password password,
						 		final String jsonToBePosted) { 
		log.debug("POST: {}",url);
		log.debug("Content: {}",Strings.removeNewlinesOrCarriageRetuns(jsonToBePosted));
		String outJson = null;
		try {
			HttpURLConnection conx = HttpClient.forUrl(url)
							  			   	   .withHeader("Accept","application/json")
							  				   .POST().withPayload(HttpRequestPayload.wrap(jsonToBePosted,
							  						   									   charset)
							  						 							     .mimeType(MimeTypes.APPLICATION_JSON))
							  				   .getConnection()
						  				 	   		.notUsingProxy()
						  				 	   		.withoutTimeOut()
						  				 	   		.usingBasicAuthCredentials(userCode,password);
			HttpResponseCode responseCode = HttpResponseCode.of(conx.getResponseCode());
			if (responseCode == HttpResponseCode.NOT_FOUND) {
				throw new AA14CalendarServiceException(PersistenceRequestedOperation.CREATE,
													   "Slot occupied!",
													   PersistenceServiceErrorTypes.ENTITY_ALREADY_EXISTS,1);
			}
			@Cleanup
			InputStream is = conx.getInputStream();
			outJson = StringPersistenceUtils.load(is);
			conx.disconnect();
		} catch(IOException ioEx) {
			ioEx.printStackTrace();
			throw new AA14CalendarServiceException(PersistenceRequestedOperation.CREATE,
															  ioEx.getMessage(),
															  PersistenceServiceErrorTypes.CLIENT_CANNOT_CONNECT_SERVER,99);
		}
		return outJson;
	}
	private static String _put(final Charset charset,
							   final Url url,
						 	   final UserCode userCode,Password password,
						 	   final String jsonToBePosted) { 
		log.debug("PUT: {}",url);
		log.debug("Content: {}",Strings.removeNewlinesOrCarriageRetuns(jsonToBePosted));
		String outJson = null;
		try {
			HttpURLConnection conx = HttpClient.forUrl(url)
							  			   	   .withHeader("Accept","application/json")
							  				   .PUT().withPayload(HttpRequestPayload.wrap(jsonToBePosted,
							  						   									  charset)
							  						 							    .mimeType(MimeTypes.APPLICATION_JSON))
							  				   .getConnection()
						  				 	   		.notUsingProxy()
						  				 	   		.withoutTimeOut()
						  				 	   		.usingBasicAuthCredentials(userCode,password);
			HttpResponseCode responseCode = HttpResponseCode.of(conx.getResponseCode());
			if (responseCode == HttpResponseCode.NOT_FOUND) {
				throw new AA14CalendarServiceException(PersistenceRequestedOperation.UPDATE,
													   "Error!!",
													   PersistenceServiceErrorTypes.SERVER_ERROR,responseCode.ordinal());
			}
			@Cleanup
			InputStream is = conx.getInputStream();
			outJson = StringPersistenceUtils.load(is);
			conx.disconnect();
		} catch(IOException ioEx) {
			ioEx.printStackTrace();
			throw new AA14CalendarServiceException(PersistenceRequestedOperation.UPDATE,
															  ioEx.getMessage(),
															  PersistenceServiceErrorTypes.CLIENT_CANNOT_CONNECT_SERVER,99);
		}
		return outJson;
	}
	private static boolean _delete(final Url url,
					    	   	   final UserCode userCode,Password password) {
		log.debug("DELETE: {}",url);
		boolean outDeleted = false;
		try {
			HttpURLConnection conx = HttpClient.forUrl(url)
						  				 	   .DELETE()
						  				 	   .getConnection()
						  				 	   		.notUsingProxy()
						  				 	   		.withoutTimeOut()
						  				 	   		.usingBasicAuthCredentials(userCode,password);
			HttpResponseCode responseCode = HttpResponseCode.of(conx.getResponseCode());
			if (responseCode == HttpResponseCode.NOT_FOUND) {
				throw new AA14CalendarServiceException(PersistenceRequestedOperation.DELETE,
																  Strings.customized("Appointment does NOT exists: it cannot be deleted"),
																  PersistenceServiceErrorTypes.ENTITY_NOT_FOUND,1);
			}
			if (responseCode.isIn(HttpResponseCode.OK,
								  HttpResponseCode.ACCEPTED,
								  HttpResponseCode.NO_CONTENT)) {
				// OK
				outDeleted = true;
				conx.disconnect();
			}
		} catch(IOException ioEx) {
			throw new AA14CalendarServiceException(PersistenceRequestedOperation.DELETE,
															  ioEx.getMessage(),
															  PersistenceServiceErrorTypes.CLIENT_CANNOT_CONNECT_SERVER,99);
		}
		return outDeleted;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Uses a Jackson JSON Mapper to convert the Orchestra's REST service-returned json into a {@link AA14OrchestraModelObject}
	 * @param jsonMapper
	 * @param json
	 * @param orchestraModelObjType
	 * @return
	 */
	private static <O extends AA14OrchestraModelObject> O _jsonToOrchestraModelObject(final ObjectMapper jsonMapper,
																			 		  final String json,
																			 		  final Class<O> orchestraModelObjType) {
		O outObj = null;
		try {
			outObj = jsonMapper.readValue(json,
									      orchestraModelObjType);
		} catch(JsonMappingException jsonEx) {
			log.error("{}",Strings.removeNewlinesOrCarriageRetuns(json),
					  jsonEx);
			throw new IllegalArgumentException("Could NOT convert the orchestra returned json to a model object: " + jsonEx.getMessage());
		} catch (JsonGenerationException jsonGenEx) {
			log.error("{}",Strings.removeNewlinesOrCarriageRetuns(json),
					  jsonGenEx);
			throw new IllegalArgumentException("Could NOT convert the orchestra returned json to a model object: " + jsonGenEx.getMessage());
		} catch (IOException ioEx) {
			/* ignore */
		} 
		return outObj;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
//												final int year,final int monthOfYear,final int dayOfMonth,
//								  				final int hourOfDay,final int minutesOfHour,
//								  				final String appointmentTitle,final String appointmentNotes,
//								  				final Person<NIFPersonID> customer,final ContactInfo customerContact
	/**
	 * Uses a Jackson JSON Mapper to create the appointment create json message
	 * @param jsonMapper
	 * @param slot
	 * @return
	 */
	private static String _composeSlotJson(final ObjectMapper jsonMapper,
										   final AA14BookedSlot slot) {
		String outJson = null;
		if (slot instanceof AA14Appointment) {
			AA14Appointment appointment = (AA14Appointment)slot;
			outJson = _composeNewAppointmentJson(jsonMapper,
												 appointment.getSubjectIn(Language.DEFAULT),appointment.getSummaryIn(Language.DEFAULT),
												 appointment.getPerson(),appointment.getContactInfo());
		} else if (slot instanceof AA14NonBookableSlot) {
			AA14NonBookableSlot nonBookableSlot = (AA14NonBookableSlot)slot;
			outJson = _composeNewNonBookableSlotJson(jsonMapper,
													 nonBookableSlot.getSubjectIn(Language.DEFAULT),nonBookableSlot.getSummaryIn(Language.DEFAULT),
													 nonBookableSlot.getUserCode());
		}
		return outJson;
	}
	/**
	 * Uses a Jackson JSON Mapper to create the appointment create json message
	 * @param jsonMapper
	 * @param appointmentTitle
	 * @param appointmentNotes
	 * @param customer
	 * @param customerContact
	 * @return
	 */
	private static String _composeNewNonBookableSlotJson(final ObjectMapper jsonMapper,
													 	 final String subject,final String notes,
													 	 final UserCode userCode) {
		// Create an orchestra appointment
		AA14OrchestraNewSlotReservation orchestraNewSlotReservation = AA14OrchestraNewSlotReservation.createFor(subject,notes,
																												userCode);
		// Serialize it as Json
		String outJson = null;
		try {
			outJson = jsonMapper.writeValueAsString(orchestraNewSlotReservation);
		} catch(JsonMappingException jsonEx) {
			throw new IllegalArgumentException("Could NOT convert the new appointment to JSon: " + jsonEx.getMessage());
		} catch (JsonGenerationException jsonGenEx) {
			throw new IllegalArgumentException("Could NOT convert the new appointment to JSon: " + jsonGenEx.getMessage());
		} catch (IOException ioEx) {
			/* ignore */
		} 
		return outJson;
	}
	/**
	 * Uses a Jackson JSON Mapper to create the appointment create json message
	 * @param jsonMapper
	 * @param appointmentTitle
	 * @param appointmentNotes
	 * @param customer
	 * @param customerContact
	 * @return
	 */
	private static String _composeNewAppointmentJson(final ObjectMapper jsonMapper,
													 final String appointmentTitle,final String appointmentNotes,
													 final Person<NIFPersonID> customer,final ContactInfo customerContact) {
		// Create an orchestra appointment
		AA14OrchestraNewSlotReservation orchestraNewSlotReservation = AA14OrchestraNewSlotReservation.createFor(appointmentTitle,appointmentNotes,
																						   						customer,customerContact);
		// Serialize it as Json
		String outJson = null;
		try {
			outJson = jsonMapper.writeValueAsString(orchestraNewSlotReservation);
		} catch(JsonMappingException jsonEx) {
			throw new IllegalArgumentException("Could NOT convert the new appointment to JSon: " + jsonEx.getMessage());
		} catch (JsonGenerationException jsonGenEx) {
			throw new IllegalArgumentException("Could NOT convert the new appointment to JSon: " + jsonGenEx.getMessage());
		} catch (IOException ioEx) {
			/* ignore */
		} 
		return outJson;
	}
	/**
	 * Uses a velocity template to create the appointment create json message
	 * Template file:
	* 		{
	* 			"title": "${appointment_title}",
	* 			"notes": "${appointment_notes}",
	* 			"customer": {
	* 				"externalId": "${customer_id}",
	* 				"firstName": "${customer_firstName}",
	* 				"lastName": "${customer_surname}",
	* 				"email": "${customer_email}",
	* 				"phone": "${customer_phone}"
	* 			}
	* 		}
	 * @param velocityEngine
	 * @param appointmentBookJsonMessageTemplatePath
	 * @param appointmentTitle
	 * @param appointmentNotes
	 * @param customer
	 * @param customerContact
	 * @return
	 */
	private static String _composeNewAppointmentJson(final VelocityEngine velocityEngine,
													 final Path appointmentBookJsonMessageTemplatePath,
													 final String appointmentTitle,final String appointmentNotes,
													 final Person<NIFPersonID> customer,final ContactInfo customerContact) {
		Map<String,Object> model = Maps.newHashMapWithExpectedSize(7);
		model.put("appointment_title",appointmentTitle);
		model.put("appointment_notes",appointmentNotes);
		
		model.put("customer_id",customer.getId().asString());
		model.put("customer_firstName",customer.getName());
		model.put("customer_surname",customer.getSurname());
		model.put("customer_email",customerContact.getDefaultMailAddressOrAny().asString());
		model.put("customer_phone",customerContact.getDefaultPhoneOrAny().asString());
		
		
	    // Text... using velocity
	    VelocityContext context = new VelocityContext(model);
		StringWriter sw = new StringWriter();
		velocityEngine.mergeTemplate(appointmentBookJsonMessageTemplatePath.asRelativeString(),
			  						 R01F.DEFAULT_CHARSET.name(),
			  						 context,
			  						 sw);
		sw.flush();
	    String appointmentBookJsonMessage = sw.toString();
		return appointmentBookJsonMessage;
	}
}
