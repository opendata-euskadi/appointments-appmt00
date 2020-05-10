package aa14a.ui.servlet;

import aa14a.ui.servlet.AA14ReqParamToType;
import aa14f.model.AA14Appointment;
import aa14f.model.AA14AppointmentSubject;
import aa14f.model.oids.AA14IDs.AA14PersonLocatorID;
import aa14f.model.oids.AA14OIDs.AA14AppointmentSubjectID;
import r01f.locale.Language;
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

/**
 * Sets appointment data from the request params
 */
abstract class AA14AppointmentFromRequestBuilderBase 
	implements AA14AppointmentFromRequestBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void setAppointmentDataFromRequest(final HttpRequestParamsWrapper reqParams,final AA14Appointment appointment) {
		Language lang = reqParams.getParameter("lang")
								 .asLanguageFromCountryCode()
								 .orDefault(Language.DEFAULT);
		
		// requestor
		String nif = reqParams.getParameter("nif").asString(AA14RequestParamsSanitizer.FILTER)
												  .orNull();
		
		String nombre = reqParams.getParameter("nombre").asString(AA14RequestParamsSanitizer.FILTER)
														.orNull();
		String apellidos = reqParams.getParameter("apellidos").asString(AA14RequestParamsSanitizer.FILTER)
															  .orNull();
		
		// appointment data
		String numberOfPersons = reqParams.getParameter("numberOfPersons").asString(AA14RequestParamsSanitizer.FILTER)
																		  .orNull();
		String codExp = reqParams.getParameter("codExp").asString(AA14RequestParamsSanitizer.FILTER)
														.orDefault("-");
		String subjectDetail = reqParams.getParameter("subjectDetail").asString(AA14RequestParamsSanitizer.FILTER)
																	  .orNull();
		
		// contact info
		String email = reqParams.getParameter("email").asString(AA14RequestParamsSanitizer.FILTER)
													  .orNull();
		String movil = reqParams.getParameter("movil").asString(AA14RequestParamsSanitizer.FILTER)
													  .orNull();
		
		// private details
		String privateDetails = reqParams.getParameter("privateDetails").asString(AA14RequestParamsSanitizer.FILTER)
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
		String medicalServiceLandline = reqParams.getParameter("fijo").asString(AA14RequestParamsSanitizer.FILTER).orNull();
	
		appointment.setContactInfo(ContactInfo.create());
		ContactPhone cellphone = ContactPhone.createToBeUsedFor(ContactInfoUsage.OTHER)
							 			     .type(ContactPhoneType.MOBILE)
							 			     .withNumber(Phone.create(movil));
		ContactPhone landline = ContactPhone.createToBeUsedFor(ContactInfoUsage.OTHER)
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
		_setAppointmentBusinessDataFromRequest(reqParams,appointment);
		
		// Preson locator
		AA14PersonLocatorID locator = reqParams.getParameter("locator")
											   .asType(AA14PersonLocatorID.class)
					 						   .using(AA14ReqParamToType.transform(AA14PersonLocatorID.class))
					 						   .orNull();
		appointment.setPersonLocatorId(locator);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets business-specific data
	 * @param reqParams
	 * @param appointment
	 */
	protected abstract void _setAppointmentBusinessDataFromRequest(final HttpRequestParamsWrapper reqParams,final AA14Appointment appointment);
}
