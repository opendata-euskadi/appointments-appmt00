package aa14f.client.servicesproxy.rest;

import org.joda.time.LocalDate;

import aa14f.client.servicesproxy.rest.AA14RESTServiceResourceUrlPathBuilderBases.AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import r01f.bootstrap.services.config.client.ServicesCoreModuleExpositionAsRESTServices;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceEndPointUrl;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;
import r01f.types.url.UrlPath;
import r01f.util.types.Dates;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

  class AA14RESTServiceResourceUrlPathBuilderForBookedSlotsCalendar
extends AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase<AA14SlotOID,AA14SlotID> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14RESTServiceResourceUrlPathBuilderForBookedSlotsCalendar(final XMLPropertiesForAppComponent clientProps) {
		super(new RESTServiceEndPointUrl(clientProps,
										 "persistence"),
			  UrlPath.from("appointments/calendar"));
	}
	public AA14RESTServiceResourceUrlPathBuilderForBookedSlotsCalendar(final ServicesCoreModuleExpositionAsRESTServices coreModuleRESTExposition) {
		super(new RESTServiceEndPointUrl(coreModuleRESTExposition),
			  UrlPath.from("appointments/calendar"));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public UrlPath pathOfTimeSlotsForRange(final AA14ScheduleOID scheduleOid,final AA14OrgDivisionServiceLocationOID prefLocOid,
										   final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth) {
		LocalDate startDate = new LocalDate(year.getYear(),monthOfYear.getMonthOfYear(),dayOfMonth.getDayOfMonth());
		return this.pathOfAllEntities().joinedWith("bySchedule",
				   								   scheduleOid,
				   								   Dates.format(startDate.toDate(),"yyyy-MM-dd"),
				   								   "timeSlots");
	}
	public UrlPath pathOfTimeSlotsForRange(final AA14OrgDivisionServiceLocationOID locationOid,final AA14ScheduleOID prefSchOid,
										   final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth) {
		LocalDate startDate = new LocalDate(year.getYear(),monthOfYear.getMonthOfYear(),dayOfMonth.getDayOfMonth());
		return this.pathOfAllEntities().joinedWith("byLocation",
				   								   locationOid,
				   								   Dates.format(startDate.toDate(),"yyyy-MM-dd"),
				   								   "timeSlots");
	}
}