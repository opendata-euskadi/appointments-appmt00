package aa14.test.mockfactories;

import org.joda.time.LocalDate;

import aa14f.model.AA14Appointment;
import aa14f.model.AA14AppointmentSubject;
import aa14f.model.AA14BookedSlot;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14OIDs.AA14AppointmentSubjectID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.patterns.Factory;
import r01f.types.contact.ContactInfo;
import r01f.types.contact.ContactInfoUsage;
import r01f.types.contact.ContactMail;
import r01f.types.contact.ContactPhone;
import r01f.types.contact.ContactPhoneType;
import r01f.types.contact.EMail;
import r01f.types.contact.NIFPersonID;
import r01f.types.contact.PersonBuilder;
import r01f.types.contact.PersonSalutation;
import r01f.types.contact.Phone;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;

@RequiredArgsConstructor
public class AA14MockAppointmentFactory 
  implements Factory<AA14BookedSlot> {
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////	
	public static final NIFPersonID MOCK_PERSON_ID = NIFPersonID.forId("30639781D");
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14Organization _organization;
	private final AA14OrgDivision _division;
	private final AA14OrgDivisionService _service;
	private final AA14OrgDivisionServiceLocation _location;
	private final AA14Schedule _schedule;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AA14BookedSlot create() {
		return _buildMockBookedSlot();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private AA14BookedSlot _buildMockBookedSlot() {		
		// Oid
		AA14Appointment outAppointment = new AA14Appointment();
		outAppointment.setOid(AA14SlotOID.supply());
//		outAppointment.setId(AA14AppointmentID.forId(Strings.customized("{}/{}",
//																		_location.getId(),	// organization/division/service/location
//																		_aptmtIds.getOneUnused())));
		
		// Org
		outAppointment.setOrgDivisionServiceLocationOid(_location.getOid());	// mandatory
		outAppointment.setScheduleOid(_schedule.getOid());						// mandatory
		
		// Date
		LocalDate date = LocalDate.now();
		outAppointment.setYear(Year.of(date));
		outAppointment.setMonthOfYear(MonthOfYear.of(date));
		outAppointment.setDayOfMonth(DayOfMonth.of(date));
		outAppointment.setHourOfDay(HourOfDay.of(10));
		outAppointment.setMinuteOfHour(MinuteOfHour.of(30));
		
		// Subject
		outAppointment.setSubject(new AA14AppointmentSubject(AA14AppointmentSubjectID.forId("EX234123282"),
															 "Revisión del expediente"));
		
		// Requestor
		outAppointment.setPerson(PersonBuilder.createPersonWithId(MOCK_PERSON_ID)
											  .withName("Alex")
											  .withSurnames("Lara","Garachana")
											  .useSalutation(PersonSalutation.DR)
											  .preferredLanguage(Language.DEFAULT)
											  .withDetails("The master blaster")
											  .build());
		outAppointment.setContactInfo(ContactInfo.create()
												 .addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.OTHER)
														 			   .type(ContactPhoneType.MOBILE)
														 			   .withNumber(Phone.create("688671967"))
														 			   .useAsDefault())
												 .addMailAddress(ContactMail.createToBeUsedFor(ContactInfoUsage.OTHER)
														 					.mailTo(EMail.create("futuretelematics@gmail.com"))
														 					.useAsDefault()));
		return outAppointment;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14MockEntityIDProvider _aptmtIds = AA14MockEntityIDProvider.create()	
																				.add("1")
																				.add("2")
																				.add("3")
																				.add("4")
																				.add("5")
																				.add("6")
																				.add("7")
																				.add("8")
																				.add("9")
																				.add("10")
																				.add("11")
																				.add("12")
																				.add("13")
																				.add("14")
																				.add("15")
																				.add("16")
																				.add("17")
																				.add("18")
																						.build();
}
