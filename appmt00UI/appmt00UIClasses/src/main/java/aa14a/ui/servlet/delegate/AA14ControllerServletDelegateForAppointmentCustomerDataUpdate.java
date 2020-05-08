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
import aa14f.model.business.justizia.AA14BusinessDataForJustizia;
import aa14f.model.business.justizia.AA14Profile;
import aa14f.model.oids.AA14IDs.AA14PersonLocatorID;
import aa14f.model.oids.AA14OIDs.AA14AppointmentSubjectID;
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
import r01f.util.types.Strings;

@Slf4j
@RequiredArgsConstructor
public class AA14ControllerServletDelegateForAppointmentCustomerDataUpdate
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
		log.debug("[init]: Update appointment customer personal information -----------------");
				
		Language lang = reqParams.getParameter("lang")
								 .asLanguageFromCountryCode()
								 .orDefault(Language.DEFAULT);
		
		AA14SlotOID slotOid = reqParams.getMandatoryParameter("slotOid")
									   .asOid(AA14SlotOID.class)
			 						   .using(AA14ReqParamToType.transform(AA14SlotOID.class));

		// Oid
		AA14Appointment appointment = _clientAPI.bookedSlotsAPI()
										.getForCRUD()
										.load(slotOid).as(AA14Appointment.class);
		
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
								   .update(appointment).as(AA14Appointment.class);
		
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
		log.debug("[end]: Update appointment date/time-----------------");
	}
}
