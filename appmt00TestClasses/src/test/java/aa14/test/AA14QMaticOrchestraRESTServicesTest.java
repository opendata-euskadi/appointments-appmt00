package aa14.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Assert;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;

import aa14b.bootstrap.core.AA14CalendarGuiceModule;
import aa14b.calendar.AA14QMaticOrchestraMediator;
import aa14b.calendar.orchestra.model.AA14OrchestraAvailableSlotDates;
import aa14b.calendar.orchestra.model.AA14OrchestraAvailableSlots;
import aa14b.calendar.orchestra.model.AA14OrchestraBookedSlot;
import aa14b.calendar.orchestra.model.AA14OrchestraBranch;
import aa14b.calendar.orchestra.model.AA14OrchestraBranchList;
import aa14b.calendar.orchestra.model.AA14OrchestraBranchService;
import aa14b.calendar.orchestra.model.AA14OrchestraBranchServicesList;
import aa14b.calendar.orchestra.model.AA14OrchestraCustomer;
import aa14b.calendar.orchestra.model.AA14OrchestraIDs.AA14OrchestraBranchID;
import aa14b.calendar.orchestra.model.AA14OrchestraIDs.AA14OrchestraBranchServiceID;
import aa14b.calendar.orchestra.model.AA14OrchestraMeta;
import aa14b.calendar.orchestra.model.AA14OrchestraModelObject;
import aa14b.calendar.orchestra.model.AA14OrchestraNewSlotReservation;
import aa14f.model.AA14Appointment;
import aa14f.model.AA14AppointmentSubject;
import aa14f.model.AA14BookedSlot;
import aa14f.model.oids.AA14OIDs.AA14AppointmentSubjectID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.Cleanup;
import r01f.io.util.StringPersistenceUtils;
import r01f.locale.Language;
import r01f.resources.ResourcesLoader;
import r01f.resources.ResourcesLoaderBuilder;
import r01f.types.Path;
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
import r01f.util.types.collections.CollectionUtils;
import r01f.xmlproperties.XMLPropertiesGuiceModule;

/**
 * jvm arguments: -javaagent:c:/develop/local-libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true -DR01_Home=c:/develop
 */
public class AA14QMaticOrchestraRESTServicesTest {
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIN
/////////////////////////////////////////////////////////////////////////////////////////
	public static void main(final String[] args) {
		Injector injector = Guice.createInjector(new XMLPropertiesGuiceModule(),
												 new AA14CalendarGuiceModule());
		AA14QMaticOrchestraMediator qMaticMediator = injector.getInstance(AA14QMaticOrchestraMediator.class);
		
		_testOrchestraJSONObjects(qMaticMediator);
		_testOrchestraRESTServices(qMaticMediator);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  TEST CALLS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Test all rest services
	 */
	private static void _testOrchestraRESTServices(final AA14QMaticOrchestraMediator qMaticMediator) {
		// Test branch & service
		AA14OrchestraBranchID bilbaoBranch = AA14OrchestraBranchID.forId("46");
		AA14OrchestraBranchServiceID trafikoaService = AA14OrchestraBranchServiceID.forId("23");
		
		System.out.println("\n\n\n\n");
		System.out.println("========================================================================================");
		System.out.println("Branches");
		System.out.println("========================================================================================");
		AA14OrchestraBranchList branchList = qMaticMediator.listBranches();
		String branchesJson = _orchestraModelObjectToJSON(qMaticMediator.getJsonObjectsMapper(),
														  branchList);	
		System.out.println(branchesJson);
		
		
		System.out.println("\n\n\n\n");
		System.out.println("========================================================================================");
		System.out.println("Services for branch with public id=46 (Tráfico Bilbao)");
		System.out.println("========================================================================================");
		AA14OrchestraBranchServicesList servicesList = qMaticMediator.listBranchServices(bilbaoBranch);
		String branchServicesJson = _orchestraModelObjectToJSON(qMaticMediator.getJsonObjectsMapper(),
																servicesList);
		System.out.println(branchServicesJson);
		
		
		System.out.println("\n\n\n\n");
		System.out.println("========================================================================================");
		System.out.println("Available dates at Trafiko service (id=23) at Bilbao branch (id=46)");
		System.out.println("========================================================================================");
		AA14OrchestraAvailableSlotDates availableDates = qMaticMediator.listBranchServiceAvailableDates(bilbaoBranch,		// Bilbao
																					   						   trafikoaService);	// Trafiko
		Assert.assertTrue(CollectionUtils.hasData(availableDates.getDateList()));
		String branchServiceDatesJson = _orchestraModelObjectToJSON(qMaticMediator.getJsonObjectsMapper(),
																	availableDates);
		System.out.println(branchServiceDatesJson);
		
		// Pick an available date
		LocalDate availableDate = new LocalDate(CollectionUtils.<Date>pickOneElement(availableDates.getDateList()));
		
		
		System.out.println("\n\n\n\n");
		System.out.println("========================================================================================");
		System.out.println("Available slots at " + availableDate + " at Trafiko service (id=23) at Bilbao branch (id=46)");
		System.out.println("========================================================================================");
		AA14OrchestraAvailableSlots availableSlots = qMaticMediator.listBranchServiceAvailableSlotsAtDate(bilbaoBranch,	// Bilbao
																			   			 							 trafikoaService,	// Trafiko
																			   			 							 availableDate.getYear(),availableDate.getMonthOfYear(),availableDate.getDayOfMonth());
		Assert.assertTrue(CollectionUtils.hasData(availableSlots.getSlots()));
		String branchServiceDateSlotsJson = _orchestraModelObjectToJSON(qMaticMediator.getJsonObjectsMapper(),
																		availableSlots);
		System.out.println(branchServiceDateSlotsJson);
		
		// Pick an available stot at the previously obtained available date
		LocalTime availableTime = CollectionUtils.pickOneElement(availableSlots.getSlots());
		
		
		System.out.println("\n\n\n\n");
		System.out.println("========================================================================================");
		System.out.println("Create an appointment at " + availableDate + " - " + availableTime + " at Trafiko service (id=23) at Bilbao branch (id=46)");
		System.out.println("========================================================================================");
		AA14BookedSlot slot = _createMockAppointment(availableDate);
		AA14OrchestraBookedSlot appointment = qMaticMediator.reserveSlot(bilbaoBranch,		// Bilbao
															   			  trafikoaService,	// Trafiko
															   			  slot);
		String appointmentBookJsonResponse = _orchestraModelObjectToJSON(qMaticMediator.getJsonObjectsMapper(),
																		 appointment);
		System.out.println(appointmentBookJsonResponse);
	}
	private static <A extends AA14OrchestraModelObject> String _orchestraModelObjectToJSON(final ObjectMapper jsonMapper,
													  									   final A modelObj) {
		String outJson = null;
		try {
			outJson = jsonMapper.writeValueAsString(modelObj);
		} catch(JsonMappingException jsonEx) {
			throw new IllegalArgumentException("Could NOT convert the orcherstra model object of type " + modelObj.getClass() + " to JSon: " + jsonEx.getMessage());
		} catch (JsonGenerationException jsonGenEx) {
			throw new IllegalArgumentException("Could NOT convert theorcherstra model object of type " + modelObj.getClass() + " to JSon: " + jsonGenEx.getMessage());
		} catch (IOException ioEx) {
			/* ignore */
		} 
		return outJson;
	}
	private static AA14Appointment _createMockAppointment(final LocalDate availableDate) {
		AA14Appointment outAppointment = new AA14Appointment();
		outAppointment.setOid(AA14SlotOID.supply());
		
		// Date
		outAppointment.setYear(Year.of(availableDate));
		outAppointment.setMonthOfYear(MonthOfYear.of(availableDate));
		outAppointment.setDayOfMonth(DayOfMonth.of(availableDate));
		outAppointment.setHourOfDay(HourOfDay.of(10));
		outAppointment.setMinuteOfHour(MinuteOfHour.of(30));
		
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
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static void _testOrchestraJSONObjects(final AA14QMaticOrchestraMediator qMaticMediator) {
		try {
			// Create a json object mapper 
			ObjectMapper jsonMapper = qMaticMediator.getJsonObjectsMapper();
			ResourcesLoader resLoader = ResourcesLoaderBuilder.createDefaultResourcesLoader();
			
			// ====== Meta object
			@Cleanup
			InputStream metaJsonIs = resLoader.getInputStream(Path.from("aa14/test/AA14OrchestraMetaObject.json"));
			String metaJson = StringPersistenceUtils.load(metaJsonIs);
			System.out.println("[META]===============================");
			System.out.println(metaJson);
			AA14OrchestraMeta meta = jsonMapper.readValue(metaJson,
													      AA14OrchestraMeta.class);
			Assert.assertTrue(meta.getTotalresults() > 0);
			
			
			// ======= Branch object
			@Cleanup
			InputStream branchJsonIs = resLoader.getInputStream(Path.from("aa14/test/AA14OrchestraBranchObject.json"));
			String branchJson = StringPersistenceUtils.load(branchJsonIs);
			System.out.println("[BRANCH]===============================");
			System.out.println(branchJson);
			AA14OrchestraBranch branch = jsonMapper.readValue(branchJson,
													          AA14OrchestraBranch.class);
			Assert.assertTrue(branch.getName().equals("Tráfico Bilbao"));
			
			
			// ======= service object
			@Cleanup
			InputStream serviceJsonIs = resLoader.getInputStream(Path.from("aa14/test/AA14OrchestraServiceObject.json"));
			String serviceJson = StringPersistenceUtils.load(serviceJsonIs);
			System.out.println("[SERVICE]===============================");
			System.out.println(serviceJson);
			AA14OrchestraBranchService service = jsonMapper.readValue(serviceJson,
													        	      AA14OrchestraBranchService.class);
			Assert.assertTrue(service.getName().equals("Trafiko"));
			
			// ======= Customer object
			@Cleanup
			InputStream customerJsonIs = resLoader.getInputStream(Path.from("aa14/test/AA14OrchestraCustomerObject.json"));
			String customerJson = StringPersistenceUtils.load(customerJsonIs);
			System.out.println("[CUSTOMER]===============================");
			System.out.println(customerJson);
			AA14OrchestraCustomer customer = jsonMapper.readValue(customerJson,
													              AA14OrchestraCustomer.class);
			Assert.assertTrue(customer.getFirstName().equals("Alex"));
			
			// ======= Appointment object
			@Cleanup
			InputStream newAppointmentJsonIs = resLoader.getInputStream(Path.from("aa14/test/AA14OrchestraNewAppointmentObject.json"));
			String newAppointmentJson = StringPersistenceUtils.load(newAppointmentJsonIs);
			System.out.println("[NEW APPOINTMENT]===============================");
			System.out.println(newAppointmentJson);
			AA14OrchestraNewSlotReservation newAppointment = jsonMapper.readValue(newAppointmentJson,
													          		          AA14OrchestraNewSlotReservation.class);
			
			// ======= Appointment object
			@Cleanup
			InputStream appointmentJsonIs = resLoader.getInputStream(Path.from("aa14/test/AA14OrchestraAppointmentObject.json"));
			String appointmentJson = StringPersistenceUtils.load(appointmentJsonIs);
			System.out.println("[APPOINTMENT]===============================");
			System.out.println(appointmentJson);
			AA14OrchestraBookedSlot appointment = jsonMapper.readValue(appointmentJson,
													          		    AA14OrchestraBookedSlot.class);
			Assert.assertTrue(appointment.getId().asString().equals("f495b7e57738062f4995dabc219a25a56ce1667a07575d0fa7f3e0ad4fe15ef2"));

			// ======= BranchList object
			@Cleanup
			InputStream branchListJsonIs = resLoader.getInputStream(Path.from("aa14/test/AA14OrchestraBranchListObject.json"));
			String branchListJson = StringPersistenceUtils.load(branchListJsonIs);
			System.out.println("[BranchList]===============================");
			System.out.println(branchListJson);
			AA14OrchestraBranchList branchList = jsonMapper.readValue(branchListJson,
													          	      AA14OrchestraBranchList.class);
			Assert.assertTrue(CollectionUtils.hasData(branchList.getBranches()));
			
			
			// ======= BranchServicesList object
			@Cleanup
			InputStream branchServicesListJsonIs = resLoader.getInputStream(Path.from("aa14/test/AA14OrchestraBranchServicesListObject.json"));
			String branchServicesListJson = StringPersistenceUtils.load(branchServicesListJsonIs);
			System.out.println("[branchServicesList]===============================");
			System.out.println(branchServicesListJson);
			AA14OrchestraBranchServicesList branchServicesList = jsonMapper.readValue(branchServicesListJson,
													          	  				      AA14OrchestraBranchServicesList.class);
			Assert.assertTrue(CollectionUtils.hasData(branchServicesList.getServices()));
			
			
			// ======= Appointment available dates object
			@Cleanup
			InputStream availableDatesJsonIs = resLoader.getInputStream(Path.from("aa14/test/AA14OrchestraAppointmentAvailableDatesObject.json"));
			String availableDatesJson = StringPersistenceUtils.load(availableDatesJsonIs);
			System.out.println("[AvailableDates]===============================");
			System.out.println(availableDatesJson);
			AA14OrchestraAvailableSlotDates availableDates = jsonMapper.readValue(availableDatesJson,
													          		   				     AA14OrchestraAvailableSlotDates.class);
			Assert.assertTrue(CollectionUtils.hasData(availableDates.getDateList()));
			
			// ======= Appointment available slots object
			@Cleanup
			InputStream availableSlotsJsonIs = resLoader.getInputStream(Path.from("aa14/test/AA14OrchestraAppointmentAvailableSlotsObject.json"));
			String availableSlotsJson = StringPersistenceUtils.load(availableSlotsJsonIs);
			System.out.println("[AvailableSlots]===============================");
			System.out.println(availableSlotsJson);
			AA14OrchestraAvailableSlots availableSlots = jsonMapper.readValue(availableSlotsJson,
													          		   				     AA14OrchestraAvailableSlots.class);
			Assert.assertTrue(CollectionUtils.hasData(availableSlots.getSlots()));			
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
