package aa14a.ui.servlet.delegate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aa14a.ui.servlet.AA14ControllerOperation;
import aa14a.ui.servlet.AA14ReqParamToType;
import aa14f.client.api.AA14ClientAPI;
import aa14f.model.AA14Appointment;
import aa14f.model.AA14AppointmentSubject;
import aa14f.model.AA14NumberOfAdjacentSlots;
import aa14f.model.business.blooddonation.AA14BusinessDataForBloodDonation;
import aa14f.model.business.blooddonation.AA14LastBloodDonation;
import aa14f.model.business.justizia.AA14BusinessDataForJustizia;
import aa14f.model.business.justizia.AA14Profile;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14PersonLocatorID;
import aa14f.model.oids.AA14OIDs.AA14AppointmentSubjectID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.exceptions.Throwables;
import r01f.locale.Language;
import r01f.model.persistence.PersistenceException;
import r01f.model.persistence.PersistenceServiceErrorTypes;
import r01f.servlet.HttpRequestParamsWrapper;
import r01f.types.contact.ContactInfo;
import r01f.types.contact.ContactInfoUsage;
import r01f.types.contact.ContactMail;
import r01f.types.contact.ContactPhone;
import r01f.types.contact.ContactPhoneType;
import r01f.types.contact.EMail;
import r01f.types.contact.NIFPersonID;
import r01f.types.contact.PersonBuilder;
import r01f.types.contact.Phone;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;
import r01f.util.types.Strings;

@Slf4j
@RequiredArgsConstructor
public class AA14ControllerServletDelegateForAppointmentCreate
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
		
		// requestor
		String nif = reqParams.getParameter("nif").asString(FORM_PARAM_SANITIZER_FILTER)
												  .orNull();
		
		String nombre = reqParams.getParameter("nombre").asString(FORM_PARAM_SANITIZER_FILTER)
														.orNull();
		String apellidos = reqParams.getParameter("apellidos").asString(FORM_PARAM_SANITIZER_FILTER)
															  .orNull();
		
		// appointment data
		String numberOfPersons = reqParams.getParameter("numberOfPersons").asString(FORM_PARAM_SANITIZER_FILTER)
																		  .orNull();
		String codExp = reqParams.getParameter("codExp").asString(FORM_PARAM_SANITIZER_FILTER)
														.orDefault("-");
		String subjectDetail = reqParams.getParameter("subjectDetail").asString(FORM_PARAM_SANITIZER_FILTER)
																	  .orNull();
		
		// contact info
		String email = reqParams.getParameter("email").asString(FORM_PARAM_SANITIZER_FILTER)
													  .orNull();
		String movil = reqParams.getParameter("movil").asString(FORM_PARAM_SANITIZER_FILTER)
													  .orNull();
		
		// private details
		String privateDetails = reqParams.getParameter("privateDetails").asString(FORM_PARAM_SANITIZER_FILTER)
																		.orNull();
		
		// Subject
		AA14AppointmentSubject appointmentSubject = new AA14AppointmentSubject();
		if (Strings.isNOTNullOrEmpty(codExp)) appointmentSubject.setId(AA14AppointmentSubjectID.forId(codExp));
		if (Strings.isNOTNullOrEmpty(subjectDetail)) appointmentSubject.setDescription(subjectDetail);		
		appointment.setSubject(appointmentSubject);
		
		// number of persons in the appointment
		if (Strings.isNOTNullOrEmpty(numberOfPersons)) appointment.setNumberOfPersonsInAppointment(numberOfPersons);
		
		// Requestor
		appointment.setPerson(PersonBuilder.createPersonWithId(NIFPersonID.forId(nif,false))	// do not validate cif
				   								.withName(nombre)
				   								.withSurnames(apellidos,null)
				   								.noSalutation()
				   								.preferredLanguage(lang)
				   								.noDetails()
				   								.build());
		
		//FIXME Contact info may be optional in medical service. In that case it will be empty
		String medicalServiceLandline = reqParams.getParameter("fijo").asString(FORM_PARAM_SANITIZER_FILTER).orNull();
	
		appointment.setContactInfo(ContactInfo.create());
		ContactPhone cellphone=ContactPhone.createToBeUsedFor(ContactInfoUsage.OTHER)
														 			    .type(ContactPhoneType.MOBILE)
														 			    .withNumber(Phone.create(movil));
		ContactPhone landline=ContactPhone.createToBeUsedFor(ContactInfoUsage.OTHER)
														 			   .type(ContactPhoneType.NORMAL)
														 			   .withNumber(Phone.create(medicalServiceLandline));
		if (Strings.isNOTNullOrEmpty(medicalServiceLandline)) {
			landline.setDefault(true);
		}
		else {
			cellphone.setDefault(true);
		}
		appointment.getContactInfo().addPhone(cellphone);
		appointment.getContactInfo().addPhone(landline);
			
		appointment.getContactInfo().addMailAddress(ContactMail.createToBeUsedFor(ContactInfoUsage.OTHER)
														 					 .mailTo(EMail.create(email))
														 					 .useAsDefault());
			
		appointment.getContactInfo().setPreferedLanguage(Language.SPANISH);
		
		// private details
		appointment.setPrivateDetails(privateDetails);
		
		// business data
		AA14LastBloodDonation bloodDonationBusinessDataNotDonatedSince = reqParams.getParameter("bloodDonation.notDonatedSince").asEnumElementFromIntCode(AA14LastBloodDonation.class)
																    			.orNull();
		if (bloodDonationBusinessDataNotDonatedSince != null) {
			appointment.setBusinessData(new AA14BusinessDataForBloodDonation(bloodDonationBusinessDataNotDonatedSince));
		}
		
		AA14Profile justiziaRequestorProfile = reqParams.getParameter("justizia.perfil").asEnumElementFromIntCode(AA14Profile.class)
																    			.orNull();
		if (justiziaRequestorProfile != null) {
			appointment.setBusinessData(new AA14BusinessDataForJustizia(justiziaRequestorProfile));
		}
		
		AA14PersonLocatorID locator = reqParams.getParameter("locator")
								   .asType(AA14PersonLocatorID.class)
		 						   .using(AA14ReqParamToType.transform(AA14PersonLocatorID.class))
		 						   .orNull();
		appointment.setPersonLocatorId(locator);
		
		
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
