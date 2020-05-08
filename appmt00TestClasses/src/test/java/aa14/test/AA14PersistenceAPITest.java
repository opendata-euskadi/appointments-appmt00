package aa14.test;

import java.util.Collection;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import aa14.test.mockfactories.AA14MockAppointmentFactory;
import aa14.test.mockfactories.AA14MockOrgDivisionFactory;
import aa14.test.mockfactories.AA14MockOrgDivisionServiceFactory;
import aa14.test.mockfactories.AA14MockOrgDivisionServiceLocationFactory;
import aa14.test.mockfactories.AA14MockOrganizationFactory;
import aa14.test.mockfactories.AA14MockScheduleFactory;
import aa14.test.persistence.AA14AppointmentPersistenceTest;
import aa14.test.persistence.AA14OrgDivisionPersistenceTest;
import aa14.test.persistence.AA14OrgDivisionServiceLocationPersistenceTest;
import aa14.test.persistence.AA14OrgDivisionServicePersistenceTest;
import aa14.test.persistence.AA14OrganizationPersistenceTest;
import aa14.test.persistence.AA14SchedulePersistenceTest;
import aa14f.client.api.AA14ClientAPI;
import aa14f.common.internal.AA14AppCodes;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14NumberOfAdjacentSlots;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.timeslots.AA14DayRangeTimeSlots;
import r01f.model.persistence.PersistenceException;
import r01f.model.persistence.PersistenceServiceErrorTypes;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;
import r01f.test.persistence.TestPersistableModelObjectManager;
import r01f.types.Range;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;
import r01f.xml.XMLStringSerializer;


/**
 * jvm arguments: -javaagent:c:/develop/local-libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true -DR01_Home=c:/develop
 */
public class AA14PersistenceAPITest
	 extends AA14TestBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testOrganizations() {
		AA14ClientAPI clientApi = AA14TestBase.getClientApi(); 
		final AA14OrganizationPersistenceTest testOrg = new AA14OrganizationPersistenceTest(clientApi);
		testOrg.doTest();
	}
	@Test
	public void testDivisions() {
		AA14ClientAPI clientApi = AA14TestBase.getClientApi(); 
		
		// [0]: create an organization to act as parent of divisions
		// -- organization
		TestPersistableModelObjectManager<AA14OrganizationOID,AA14Organization> orgFactory = TestPersistableModelObjectManager.create(AA14Organization.class,
																																	  new AA14MockOrganizationFactory(),
																																	  clientApi.organizationsAPI()
																																	 		     .getForCRUD());
		orgFactory.setUpMockObjs(1);
		AA14Organization divParentOrg = orgFactory.getAnyCreatedMockObj();
		
		// [1]: testing...
		final AA14OrgDivisionPersistenceTest testDivision = new AA14OrgDivisionPersistenceTest(clientApi,
															  		     				    divParentOrg);
		testDivision.doTest();
		
		// [2]: tear down		
		orgFactory.tearDownCreatedMockObjs();
	}
	@Test
	public void testServices() {
		AA14ClientAPI clientApi = AA14TestBase.getClientApi(); 
		
		// [0]: create an organization to act as parent of division and a division to act as parent of services
		// -- organization
		TestPersistableModelObjectManager<AA14OrganizationOID,AA14Organization> orgFactory = TestPersistableModelObjectManager.create(AA14Organization.class,
																																	  new AA14MockOrganizationFactory(),
																																	  clientApi.organizationsAPI()
																																	 			 .getForCRUD());
		orgFactory.setUpMockObjs(1);
		AA14Organization divParentOrg = orgFactory.getAnyCreatedMockObj();
		// -- division
		TestPersistableModelObjectManager<AA14OrgDivisionOID,AA14OrgDivision> divFactory = TestPersistableModelObjectManager.create(AA14OrgDivision.class,
																																    new AA14MockOrgDivisionFactory(divParentOrg),
																																    clientApi.orgDivisionsAPI()
																																	 		   	   .getForCRUD());
		divFactory.setUpMockObjs(1);
		AA14OrgDivision serviceParentDivision = divFactory.getAnyCreatedMockObj();		
		
		
		// [1]: Testing...
		final AA14OrgDivisionServicePersistenceTest testService = new AA14OrgDivisionServicePersistenceTest(clientApi,
											  										  					divParentOrg,
											  										  					serviceParentDivision);
		testService.doTest();
		
		// [2]: Tear down (reverse order)
		divFactory.tearDownCreatedMockObjs();
		orgFactory.tearDownCreatedMockObjs();
	}
	@Test
	public void testLocations() {
		AA14ClientAPI clientApi = AA14TestBase.getClientApi(); 
		
		// [0]: create an organization to act as parent of division, a division to act as parent of a service and a service to act as perent of locations
		// -- organization
		TestPersistableModelObjectManager<AA14OrganizationOID,AA14Organization> orgFactory = TestPersistableModelObjectManager.create(AA14Organization.class,
																																	  new AA14MockOrganizationFactory(),
																																	  clientApi.organizationsAPI()
																																	 				 .getForCRUD());
		orgFactory.setUpMockObjs(1);
		AA14Organization divParentOrg = orgFactory.getAnyCreatedMockObj();
		// -- division
		TestPersistableModelObjectManager<AA14OrgDivisionOID,AA14OrgDivision> divFactory = TestPersistableModelObjectManager.create(AA14OrgDivision.class,
																																    new AA14MockOrgDivisionFactory(divParentOrg),
																																    clientApi.orgDivisionsAPI()
																																	 		    	   .getForCRUD());
		divFactory.setUpMockObjs(1);
		AA14OrgDivision serviceParentDivision = divFactory.getAnyCreatedMockObj();		
		// -- service
		TestPersistableModelObjectManager<AA14OrgDivisionServiceOID,AA14OrgDivisionService> serviceFactory = TestPersistableModelObjectManager.create(AA14OrgDivisionService.class,
																																					  new AA14MockOrgDivisionServiceFactory(divParentOrg,serviceParentDivision),
																																				      clientApi.orgDivisionServicesAPI()
																																					 		  		 .getForCRUD());
		serviceFactory.setUpMockObjs(1);
		AA14OrgDivisionService locationParentService = serviceFactory.getAnyCreatedMockObj();	
		
		// [1]: Testing...
		final AA14OrgDivisionServiceLocationPersistenceTest testLocation = new AA14OrgDivisionServiceLocationPersistenceTest(clientApi,
																		  										 	     divParentOrg,
																		  										 	     serviceParentDivision,
																		  										 	     locationParentService);
		testLocation.doTest();
		
		// [2]: Tear down (reverse order)
		serviceFactory.tearDownCreatedMockObjs();
		divFactory.tearDownCreatedMockObjs();
		orgFactory.tearDownCreatedMockObjs();
	}
	@Test
	public void testSchedules() {
		AA14ClientAPI clientApi = AA14TestBase.getClientApi(); 
		
		final AA14SchedulePersistenceTest testLocation = new AA14SchedulePersistenceTest(clientApi);
		testLocation.doTest();
	}
	@Test
	public void testSchedulesLocationsRelation() {
		AA14ClientAPI clientApi = AA14TestBase.getClientApi(); 

		// [0]: create an organization to act as parent of division, a division to act as parent of a service and a service to act as perent of locations
		// -- organization
		TestPersistableModelObjectManager<AA14OrganizationOID,AA14Organization> orgFactory = TestPersistableModelObjectManager.create(AA14Organization.class,
																																	  new AA14MockOrganizationFactory(),
																																	  clientApi.organizationsAPI()
																																	 			 .getForCRUD());
		orgFactory.setUpMockObjs(1);
		AA14Organization divParentOrg = orgFactory.getAnyCreatedMockObj();
		// -- division
		TestPersistableModelObjectManager<AA14OrgDivisionOID,AA14OrgDivision> divFactory = TestPersistableModelObjectManager.create(AA14OrgDivision.class,
																																    new AA14MockOrgDivisionFactory(divParentOrg),
																																    clientApi.orgDivisionsAPI()
																																	 		   .getForCRUD());
		divFactory.setUpMockObjs(1);
		AA14OrgDivision serviceParentDivision = divFactory.getAnyCreatedMockObj();		
		// -- service
		TestPersistableModelObjectManager<AA14OrgDivisionServiceOID,AA14OrgDivisionService> serviceFactory = TestPersistableModelObjectManager.create(AA14OrgDivisionService.class,
																																					  new AA14MockOrgDivisionServiceFactory(divParentOrg,serviceParentDivision),
																																				      clientApi.orgDivisionServicesAPI()
																																					 			 .getForCRUD());
		serviceFactory.setUpMockObjs(1);
		AA14OrgDivisionService locationParentService = serviceFactory.getAnyCreatedMockObj();	
		// -- location
		//    (create TWO location: every appointments created at one of them will allways succeed while the appointments
		//	   created at the other one will allways fail)
		TestPersistableModelObjectManager<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocation> locationFactory = TestPersistableModelObjectManager.create(AA14OrgDivisionServiceLocation.class,
																																					 				   new AA14MockOrgDivisionServiceLocationFactory(divParentOrg,serviceParentDivision,locationParentService),
																																					 				   clientApi.orgDivisionServiceLocationsAPI()
																																					 				   					.getForCRUD());
		locationFactory.setUpMockObjs(2);
		
		// [1]: Create a schedule
		TestPersistableModelObjectManager<AA14ScheduleOID,AA14Schedule> schFactory = TestPersistableModelObjectManager.create(AA14Schedule.class,
																															  new AA14MockScheduleFactory(),
																															  clientApi.schedulesAPI()
																															 			 .getForCRUD());
		schFactory.setUpMockObjs(1);
		
		// [2]: Link the location and schedule
		AA14Schedule sch = schFactory.getAnyCreatedMockObj();
		Collection<AA14OrgDivisionServiceLocation> locs = locationFactory.getCreatedMockObjs();
		
		AA14Schedule updatedSch = clientApi.schedulesAPI()
												.getForCRUD()
													.linkScheduleToServiceLocations(sch.getOid(),
																					FluentIterable.from(locs)
																								  .transform(new Function<AA14OrgDivisionServiceLocation,AA14OrgDivisionServiceLocationOID>() {
																														@Override
																														public AA14OrgDivisionServiceLocationOID apply(final AA14OrgDivisionServiceLocation loc) {
																															return loc.getOid();
																														}
																								  			 })
																								  .toList());
		Assert.assertTrue(updatedSch.getServiceLocationsRefs() != null 
					   && updatedSch.getServiceLocationsRefs().size() == locs.size());
		
		for (AA14OrgDivisionServiceLocation loc : locs) {
			AA14OrgDivisionServiceLocation storedLoc = clientApi.orgDivisionServiceLocationsAPI()
																	.getForCRUD()
																		.load(loc.getOid());
			Assert.assertTrue(storedLoc.getSchedulesRefs() != null
						   && FluentIterable.from(storedLoc.getSchedulesRefs())
						   					.first().orNull().getOid()
						   									 .equals(updatedSch.getOid()));
		}
													
		
		// [99]: Tear down
		schFactory.tearDownCreatedMockObjs();
		locationFactory.tearDownCreatedMockObjs();
		serviceFactory.tearDownCreatedMockObjs();
		divFactory.tearDownCreatedMockObjs();
		orgFactory.tearDownCreatedMockObjs();
	}
//	@Test
	public void testAppointments() {
		AA14ClientAPI clientApi = AA14TestBase.getClientApi(); 
		
		// [0]: create an organization to act as parent of division, a division to act as parent of a service and a service to act as perent of locations
		// -- organization
		TestPersistableModelObjectManager<AA14OrganizationOID,AA14Organization> orgFactory = TestPersistableModelObjectManager.create(AA14Organization.class,
																																	  new AA14MockOrganizationFactory(),
																																	  clientApi.organizationsAPI()
																																	 			 .getForCRUD());
		orgFactory.setUpMockObjs(1);
		AA14Organization divParentOrg = orgFactory.getAnyCreatedMockObj();
		// -- division
		TestPersistableModelObjectManager<AA14OrgDivisionOID,AA14OrgDivision> divFactory = TestPersistableModelObjectManager.create(AA14OrgDivision.class,
																																    new AA14MockOrgDivisionFactory(divParentOrg),
																																    clientApi.orgDivisionsAPI()
																																	 		   .getForCRUD());
		divFactory.setUpMockObjs(1);
		AA14OrgDivision serviceParentDivision = divFactory.getAnyCreatedMockObj();		
		// -- service
		TestPersistableModelObjectManager<AA14OrgDivisionServiceOID,AA14OrgDivisionService> serviceFactory = TestPersistableModelObjectManager.create(AA14OrgDivisionService.class,
																																					  new AA14MockOrgDivisionServiceFactory(divParentOrg,serviceParentDivision),
																																				      clientApi.orgDivisionServicesAPI()
																																					 			 .getForCRUD());
		serviceFactory.setUpMockObjs(1);
		AA14OrgDivisionService locationParentService = serviceFactory.getAnyCreatedMockObj();	
		// -- location
		//    (create TWO location: every appointments created at one of them will allways succeed while the appointments
		//	   created at the other one will allways fail)
		TestPersistableModelObjectManager<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocation> locationFactory = TestPersistableModelObjectManager.create(AA14OrgDivisionServiceLocation.class,
																																					 				   new AA14MockOrgDivisionServiceLocationFactory(divParentOrg,serviceParentDivision,locationParentService),
																																					 				   clientApi.orgDivisionServiceLocationsAPI()
																																					 				   					.getForCRUD());
		locationFactory.setUpMockObjs(1);
		AA14OrgDivisionServiceLocation location = locationFactory.getAnyCreatedMockObj();
		
		// -- create an schedule
		TestPersistableModelObjectManager<AA14ScheduleOID,AA14Schedule> schFactory = TestPersistableModelObjectManager.create(AA14Schedule.class,
																															  new AA14MockScheduleFactory(),
																															  clientApi.schedulesAPI()
																															 			 .getForCRUD());
		schFactory.setUpMockObjs(1);
		AA14Schedule sch = schFactory.getAnyCreatedMockObj();
		
		// -- Link the location and schedule
		sch = clientApi.schedulesAPI()
					.getForCRUD()
						.linkScheduleToServiceLocations(sch.getOid(),
														Lists.newArrayList(location.getOid()));
		location.setSchedulesRefs(Lists.newArrayList(sch.getReference()));		// update the location
		
		
		
		// [1]: Testing CRUD & FIND
		final AA14AppointmentPersistenceTest testAppointment = new AA14AppointmentPersistenceTest(clientApi,
							  										 	    				  	  divParentOrg,
							  										 	    				  	  serviceParentDivision,
							  										 	    				  	  locationParentService,
							  										 	    				  	  location,
							  										 	    				  	  sch);		
		testAppointment.doTest();
		
		
		// [2]: Test occupied calendar slot
		System.out.println("\n\n\n\n\n\n_______________________________________________________________________");
		System.out.println("[Appointment's calendar slot occupied Test]");
		// Create a location with a certain id that clues AA14AppointmentCalendarServiceMockImpl that the 
		// appointment creation operation must fail
		locationFactory.setUpMockObjs(1);
		AA14OrgDivisionServiceLocation appointmentParentLocationNOK = locationFactory.getAnyCreatedMockObj();
		appointmentParentLocationNOK.setId(AA14OrgDivisionServiceLocationID.forId("FAIL"));	// see AA14AppointmentCalendarServiceMockImpl
		clientApi.orgDivisionServiceLocationsAPI()
				   .getForCRUD()
				 		.save(appointmentParentLocationNOK);
		
		// try to save an appointment
		AA14BookedSlot occupiedAppointment = new AA14MockAppointmentFactory(divParentOrg,
							  										 	    serviceParentDivision,
							  										 	    locationParentService,
							  										 	    appointmentParentLocationNOK,		// must fail
							  										 	    sch)
														.create();
		try {
			clientApi.bookedSlotsAPI()
						   .getForCRUD()
						 		.save(occupiedAppointment);
		} catch(PersistenceException persistEx) {
			persistEx.printStackTrace();
			Assert.assertTrue(persistEx.is(PersistenceServiceErrorTypes.ENTITY_ALREADY_EXISTS) && persistEx.getExtendedCode() == 1);
			
			// retry the appointment creation
			System.out.println("... the requested calendar slot is occupied: try again");			
			occupiedAppointment.setOrgDivisionServiceLocationOid(location.getOid());			// should succeed
			clientApi.bookedSlotsAPI()
					   .getForCRUD()
							.save(occupiedAppointment);
			
			// remove the created appointment
			clientApi.bookedSlotsAPI()
					   .getForCRUD()
							.delete(occupiedAppointment.getOid());
		}
		
		// [3]: Testing calendar
		System.out.println("\n\n\n\n\n\n_______________________________________________________________________");
		System.out.println("[Appointments Calendar Test]");
		DateTime date1 = new DateTime(new Date());
		DateTime date2 = date1.plusDays(4);
		AA14DayRangeTimeSlots calendar = clientApi.bookedSlotsAPI()
														.getForCalendar()
															  .timeSlotsForRange(location.getOid(),
																	  			 AA14NumberOfAdjacentSlots.ONE,		// a single slot
																		  		 Range.open(date1.toDate(),date2.toDate()),
																		  		 true);		// slip date range to find first available slot	
		Assert.assertTrue(CollectionUtils.hasData(calendar.getDayTimeSlots()));
		
		try {
			Marshaller m = MarshallerBuilder.findTypesToMarshallAt(AA14AppCodes.API_APPCODE)
											.build();
			System.out.println("\n\n\n======>" + XMLStringSerializer.beautifyXMLString(m.forWriting().toXml(calendar)) + "\n\n\n\n");
		} catch(Throwable th) {
			th.printStackTrace();
		}
		
		
		// [3]: Tear down (reverse order)		
		locationFactory.tearDownCreatedMockObjs();
		serviceFactory.tearDownCreatedMockObjs();
		divFactory.tearDownCreatedMockObjs();
		orgFactory.tearDownCreatedMockObjs();
	}
}
