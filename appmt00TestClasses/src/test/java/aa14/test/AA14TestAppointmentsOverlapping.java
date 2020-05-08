package aa14.test;

import java.util.Collection;
import java.util.Date;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Test;

import aa14f.model.AA14Appointment;
import aa14f.model.AA14AppointmentSubject;
import aa14f.model.AA14BookedSlot;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.business.AA14BusinessConfigForBizilagun;
import aa14f.model.oids.AA14OIDs.AA14AppointmentSubjectID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.extern.slf4j.Slf4j;
import r01f.concurrent.Threads;
import r01f.locale.Language;
import r01f.model.persistence.PersistenceException;
import r01f.types.Range;
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



@Slf4j
public class AA14TestAppointmentsOverlapping
	 extends AA14TestBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testAppointmentOverlapping() {
		AA14OrgDivisionServiceLocation loc = AA14TestBase.getClientApi()
														 .configAPI()
														 .getConfig()
														 	.getForBizilagun()
												 		    .getLocationFor(AA14BusinessConfigForBizilagun.LOC_COMUNIDADES_ALQUILERES_AR_ID);
		AA14Schedule sch = AA14TestBase.getClientApi()
							 .configAPI()
							 .getConfig()
							 	.getForBizilagun()
							 	.getScheduleFor(AA14BusinessConfigForBizilagun.SCH_COMUNIDADES_ALQUILERES_AR_ID);
		log.warn("The max appointments in slot for sch with id={} is {}",
				 sch.getId(),sch.getBookingConfig().getMaxAppointmentsInSlot());
		
		LocalDate today = new LocalDate();
		LocalTime rangeStartTime = new LocalTime(sch.getBookingConfig().getMinBookableHour().getHourOfDay(),
												 sch.getBookingConfig().getMinBookableHour().getMinuteOfHour());
		LocalTime rangeEndTime = rangeStartTime.plusMinutes(sch.getBookingConfig().getSlotDefaultLengthMinutes());

		Range<Date> dateRange = Range.closed(rangeStartTime.toDateTimeToday().toDate(),
											 rangeEndTime.toDateTimeToday().toDate());
		
		AA14Appointment app = null;
		Collection<AA14BookedSlot> bookedSlots = null;
		
		// [1] - Create the max number of appointments
		for (int i=0; i < sch.getBookingConfig().getMaxAppointmentsInSlot(); i++) {
			// create an appointment				
			app = _buildMockAppointment(loc.getOid(),sch.getOid(),
										Year.of(today),MonthOfYear.of(today),DayOfMonth.of(today),
										HourOfDay.of(rangeStartTime),MinuteOfHour.of(rangeStartTime));
			AA14TestBase.getClientApi()
						   .bookedSlotsAPI()
						   		.getForCRUD()
						   			.create(app);
			// Check that there number of appointments in the range is correct
			bookedSlots = AA14TestBase.getClientApi()
								   .bookedSlotsAPI()
								   		.getForFind()
								   			.findRangeBookedSlotsFor(sch.getOid(),
								   									 dateRange);
			Assert.assertEquals(i+1,bookedSlots.size());
		}
		
		// [2] - try to create another appointment: this MUST FAIL
		try {
			app = _buildMockAppointment(loc.getOid(),sch.getOid(),
									    Year.of(today),MonthOfYear.of(today),DayOfMonth.of(today),
									    HourOfDay.of(rangeStartTime),MinuteOfHour.of(rangeStartTime));
			AA14TestBase.getClientApi()
						   .bookedSlotsAPI()
						   		.getForCRUD()
						   			.create(app);
			bookedSlots = AA14TestBase.getClientApi()
								   .bookedSlotsAPI()
								   		.getForFind()
								   			.findRangeBookedSlotsFor(sch.getOid(),
								   									 dateRange);
		} catch(PersistenceException persistEx) {
			log.error("The appointment cannot be created: {) (error code={})",
					  persistEx.getMessage(),persistEx.getExtendedCode(),
					  persistEx);
			Assert.assertTrue(persistEx.getExtendedCode() == 201);
		}
		
		// wait for background processes to finish
		Threads.safeSleep(5000);
	}
	private static AA14Appointment _buildMockAppointment(final AA14OrgDivisionServiceLocationOID locOid,final AA14ScheduleOID schOid,
												  		 final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												  		 final HourOfDay hourOfDay,final MinuteOfHour minuteOfHour) {		
		// Oid
		AA14Appointment outAppointment = new AA14Appointment();
		outAppointment.setOid(AA14SlotOID.supply());
		
		// Org
		outAppointment.setOrgDivisionServiceLocationOid(locOid);	// mandatory
		outAppointment.setScheduleOid(schOid);
		
		// Date
		outAppointment.setYear(year);
		outAppointment.setMonthOfYear(monthOfYear);
		outAppointment.setDayOfMonth(dayOfMonth);
		outAppointment.setHourOfDay(hourOfDay);
		outAppointment.setMinuteOfHour(minuteOfHour);
		
		// Subject
		outAppointment.setSubject(new AA14AppointmentSubject(AA14AppointmentSubjectID.forId("EX234123282"),
															 "Revisión del expediente"));
		
		// Requestor
		outAppointment.setPerson(PersonBuilder.createPersonWithId(NIFPersonID.forId("30639781D"))
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
}
