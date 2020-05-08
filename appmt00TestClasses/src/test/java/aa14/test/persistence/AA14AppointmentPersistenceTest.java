package aa14.test.persistence;

import java.util.Collection;

import org.joda.time.LocalDate;
import org.junit.Assert;

import aa14.test.mockfactories.AA14MockAppointmentFactory;
import aa14f.client.api.AA14ClientAPI;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForBookedSlotFindServices;
import aa14f.model.AA14BookedSlot;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import aa14f.model.search.AA14AppointmentFilter;
import aa14f.model.summaries.AA14SummarizedAppointment;
import aa14f.model.summaries.AA14SummarizedBookedSlot;
import r01f.locale.Language;
import r01f.patterns.CommandOn;
import r01f.types.datetime.Year;
import r01f.util.types.collections.CollectionUtils;

public class AA14AppointmentPersistenceTest 
	 extends AA14TPersistableObjectTestBase<AA14SlotOID,AA14SlotID,AA14BookedSlot> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14Organization _organization;
	private final AA14OrgDivision _division;
	private final AA14OrgDivisionService _service;
	private final AA14OrgDivisionServiceLocation _location;
	private final AA14Schedule _schedule;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14AppointmentPersistenceTest(final AA14ClientAPI api,
		 			  		   		      final AA14Organization organization,
		 			  		   		      final AA14OrgDivision division,
		 			  		   		      final AA14OrgDivisionService service,
		 			  		   		      final AA14OrgDivisionServiceLocation location,
		 			  		   		      final AA14Schedule schedule) {
		super(AA14BookedSlot.class,new AA14MockAppointmentFactory(organization,
																  division,
																  service,
																  location,
																  schedule),
			  api.bookedSlotsAPI().getForCRUD(),api.bookedSlotsAPI().getForFind(),
			  1000l);		// sleep 1000 milis before deleting DB records to give time to background jobs (notifications) to complete
		_organization = organization;
		_division = division;
		_service = service;
		_location = location;
		_schedule = schedule;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  CRUD
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected CommandOn<AA14BookedSlot> _modelObjectStateUpdateCommand() {
		return new CommandOn<AA14BookedSlot>() {
						@Override
						public void executeOn(final AA14BookedSlot slot) {
							// Change the year
							slot.setYear(Year.of(2018));
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void testOtherFindMethods() {
		// [0]: Create 5 divisions
		_managesTestMockObjects.setUpMockObjs(5);
		
		LocalDate date = LocalDate.now();
		
		// [1]: Test
		AA14ClientAPIDelegateForBookedSlotFindServices findApi = getClientFindApiAs(AA14ClientAPIDelegateForBookedSlotFindServices.class);
		
		// Find appointments by location
		System.out.println("\n\n\n\n--------------------------------------------------------------------");
		System.out.println("[" + _location.getId() + " location appointments]");
		Collection<AA14BookedSlot> apps = findApi.findDayBookedSlotsFor(_location.getOid(),
																		date.getYear(),date.getMonthOfYear(),date.getDayOfMonth());
		
		Assert.assertTrue(CollectionUtils.hasData(apps));		
		for(AA14BookedSlot loc : apps) {
			System.out.println("\t-" + loc.getOid() + " > " + loc.asSummarizable()
																 .getSummary().asString());
		}
		
		// Find appointments summaries by location
		System.out.println("\n\n\n\n--------------------------------------------------------------------");
		System.out.println("[" + _location.getId() + " location appointments summaries]");
		Collection<AA14SummarizedBookedSlot> locApps = findApi.findDayBookedSlotsSummarizedFor(Language.DEFAULT,
																							   _location.getOid(),
																							   date.getYear(),date.getMonthOfYear(),date.getDayOfMonth());
		
		Assert.assertTrue(CollectionUtils.hasData(locApps));
		if (CollectionUtils.hasData(locApps)) {
			for (AA14SummarizedBookedSlot appSummary : locApps) {
				System.out.println("\t-" + appSummary.getOid() + " > " + appSummary.getId() + " > " + appSummary.getStartDate());
			}
		}
		
		// Find appointments summaries by person id 
		System.out.println("\n\n\n\n--------------------------------------------------------------------");
		System.out.println("[" + AA14MockAppointmentFactory.MOCK_PERSON_ID + " person appointments summaries]");
		AA14AppointmentFilter byPersonIdFilter = new AA14AppointmentFilter(null,null,	// no service & location
																		   AA14MockAppointmentFactory.MOCK_PERSON_ID,
																		   null);		// no date range
		Collection<AA14SummarizedAppointment> pesonApps = findApi.findAppointmentsBy(byPersonIdFilter,
																					 Language.DEFAULT);
		
		Assert.assertTrue(CollectionUtils.hasData(pesonApps));
		if (CollectionUtils.hasData(pesonApps)) {
			for (AA14SummarizedAppointment appSummary : pesonApps) {
				System.out.println("\t-" + appSummary.getOid() + " > " + appSummary.getId() + "(locatorId=" + appSummary.getPersonLocatorId() + ")" + " > " + appSummary.getStartDate());
			}
		}
		
		
		// [2]: Tear down created model objects
		_managesTestMockObjects.tearDownCreatedMockObjs();
	}
	@Override
	public void testOtherMethods() {
		// nothing
	}
}
