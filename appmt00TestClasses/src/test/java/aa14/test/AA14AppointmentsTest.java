package aa14.test;

import java.util.Date;

import org.joda.time.LocalDate;
import org.junit.Test;

import com.google.common.collect.Iterables;

import aa14f.model.AA14Appointment;
import aa14f.model.AA14AppointmentSubject;
import aa14f.model.AA14NumberOfAdjacentSlots;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.business.AA14BusinessConfigForTrafikoa;
import aa14f.model.oids.AA14OIDs.AA14AppointmentSubjectID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import aa14f.model.timeslots.AA14DayRangeTimeSlots;
import aa14f.model.timeslots.AA14DayTimeSlots;
import aa14f.model.timeslots.AA14TimeSlot;
import r01f.locale.Language;
import r01f.model.persistence.PersistenceException;
import r01f.model.persistence.PersistenceServiceErrorTypes;
import r01f.patterns.Memoized;
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
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;


/**
 * jvm arguments: -javaagent:c:/develop/local-libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true -DR01_Home=c:/develop
 */
public class AA14AppointmentsTest
	 extends AA14TestBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final Memoized<AA14Organization> _organization = new Memoized<AA14Organization>() {
																@Override
																public AA14Organization supply() {
																	return AA14TestBase.getClientApi()
																		 		.organizationsAPI().getForCRUD()
																		 		.loadById(AA14BusinessConfigForTrafikoa.ORG_ID);
																	
																}
													   };
	private final Memoized<AA14OrgDivision> _division = new Memoized<AA14OrgDivision>() {
															@Override
															public AA14OrgDivision supply() {
																return AA14TestBase.getClientApi()
																	 		.orgDivisionsAPI().getForCRUD()
																	 		.loadById(AA14BusinessConfigForTrafikoa.DIVISION_ID);
															}
													   };
	private final Memoized<AA14OrgDivisionService> _service = new Memoized<AA14OrgDivisionService>() {
																@Override
																public AA14OrgDivisionService supply() {
																return AA14TestBase.getClientApi()
																	 		.orgDivisionServicesAPI().getForCRUD()
																	 		.loadById(AA14BusinessConfigForTrafikoa.SERVICE_ID);
																}
													    };
	private final Memoized<AA14OrgDivisionServiceLocation> _location = new Memoized<AA14OrgDivisionServiceLocation>() {
																		@Override
																		public AA14OrgDivisionServiceLocation supply() {
																return AA14TestBase.getClientApi()
																	 		.orgDivisionServiceLocationsAPI().getForCRUD()
																	 		.loadById(AA14BusinessConfigForTrafikoa.LOC_BILBAO_ID);
																		}
										   						 };
	private final Memoized<AA14Schedule> _schedule = new Memoized<AA14Schedule>() {
														@Override
														public AA14Schedule supply() {
															return AA14TestBase.getClientApi()
																 		.schedulesAPI().getForCRUD()
																 		.loadById(AA14BusinessConfigForTrafikoa.SCH_BILBAO_ID);
																	}
										   	  };
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testAppointment() {
		// Get the time slots
		_testAppointment(_organization.get(),
						 _division.get(), 
						 _service.get(),
						 _location.get(),
						 _schedule.get());
	}
	private static void _testAppointment(final AA14Organization org,
								  		 final AA14OrgDivision division,
								  		 final AA14OrgDivisionService service,
								  		 final AA14OrgDivisionServiceLocation location,
								  		 final AA14Schedule sch) {
		
		LocalDate startDate = new LocalDate(2015,10,6);
		LocalDate endDate = startDate.plusDays(2);		
		Range<Date> range = Range.closed(startDate.toDate(),endDate.toDate());
		
		// [1]: Get the available time-slots
		AA14DayRangeTimeSlots dayRangeTimeSlots = AA14TestBase.getClientApi()
																	 .bookedSlotsAPI()
																	  		 .getForCalendar()
																	  		 .timeSlotsForRange(location.getOid(),
																	  				 			AA14NumberOfAdjacentSlots.ONE,			// a single slot
																	  				   			range,
																	  				   			true);		// slip date range to find first available slot
		System.out.println("[Available slots]");
		System.out.println(dayRangeTimeSlots.debugInfo());
		
		// [2]: find the first available slot
		AA14DayTimeSlots daySlots = _findFirstDayWithAvailableSlots(dayRangeTimeSlots);
		if (daySlots == null) throw new IllegalStateException("No day with available slots within the range: " + range);
		AA14TimeSlot slot = Iterables.get(daySlots.getTimeSlots(),0);
		System.out.println("[Create appointment at: " + Strings.customized("{}-{}-{} at {}:{}]",
																		   daySlots.getYear(),daySlots.getMonthOfYear(),daySlots.getDayOfMonth(),
																		   slot.getHourOfDay(),slot.getMinuteOfHour()));
		
		// [3]: Create the appointment
		AA14Appointment appointment = _buildMockAppointment(location.getOid(),sch.getOid(),
															daySlots.getYear(),daySlots.getMonthOfYear(),daySlots.getDayOfMonth(),
															slot.getHourOfDay(),slot.getMinuteOfHour());
		try {
			appointment = AA14TestBase.getClientApi()
											 .bookedSlotsAPI()
										   	     .getForCRUD()
											    	.create(appointment)
											    	.as(AA14Appointment.class);
			System.out.println("==============>" + appointment.getId());
		} catch(PersistenceException persistEx) {
			if (persistEx.is(PersistenceServiceErrorTypes.ENTITY_ALREADY_EXISTS)
			 && persistEx.getExtendedCode() == 1) {
				// the slot was occupied when it was tried to be booked
				System.out.println("==============> The user MUST select another slot since the selected one was occupied when it was tried to be booked");
			}
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static AA14DayTimeSlots _findFirstDayWithAvailableSlots(final AA14DayRangeTimeSlots range) {
		AA14DayTimeSlots outDay = null;
		for (AA14DayTimeSlots day : range) {
			if (CollectionUtils.hasData(day.getTimeSlots())) {
				outDay = day;
				break;
			}
		}
		return outDay;
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
															 "Revisi�n del expediente"));
		
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
