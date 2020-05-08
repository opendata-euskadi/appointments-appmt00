package aa14f.client.servicesproxy.rest;

import javax.inject.Inject;
import javax.inject.Singleton;

import aa14f.api.interfaces.AA14BookedSlotsCalendarServices;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14NumberOfAdjacentSlots;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import aa14f.model.timeslots.AA14DayRangeTimeSlots;
import r01f.exceptions.Throwables;
import r01f.httpclient.HttpResponse;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.servicesproxy.rest.DelegateForRawREST;
import r01f.services.client.servicesproxy.rest.RESTServicesForModelObjectProxyBase;
import r01f.types.datetime.DayOfMonth;
import r01f.types.datetime.MonthOfYear;
import r01f.types.datetime.Year;
import r01f.types.url.Url;
import r01f.types.url.UrlQueryString;
import r01f.types.url.UrlQueryStringParam;
import r01f.util.types.Strings;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import r01f.xmlproperties.annotations.XMLPropertiesComponent;


@Singleton
public class AA14RESTBookedSlotsCalendarServicesProxy
	 extends RESTServicesForModelObjectProxyBase<AA14SlotOID,AA14BookedSlot>
  implements AA14BookedSlotsCalendarServices,
  		     AA14RESTServiceProxy {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14RESTBookedSlotsCalendarServicesProxy(@XMLPropertiesComponent("client") final XMLPropertiesForAppComponent clientProps,
												    @ModelObjectsMarshaller 		  final Marshaller marshaller) {
		super(marshaller,
			  AA14BookedSlot.class,
			  new AA14RESTServiceResourceUrlPathBuilderForBookedSlotsCalendar(clientProps));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AA14DayRangeTimeSlots availableTimeSlotsForRange(final SecurityContext securityContext,
												   			final AA14ScheduleOID schOid,
												   			final AA14OrgDivisionServiceLocationOID prefLocOid,
												   			final AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   			final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   			final int numberOfDays,
												   			final boolean slipDateRangeToFindFirstAvailableSlot) {
		Url restResourceUrl = this.composeURIFor(this.getServicesRESTResourceUrlPathBuilderAs(AA14RESTServiceResourceUrlPathBuilderForBookedSlotsCalendar.class)
												   			  .pathOfTimeSlotsForRange(schOid,
												   					  				   prefLocOid,
												   					  				   year,monthOfYear,dayOfMonth),
												 UrlQueryString.fromParams(UrlQueryStringParam.of("numberOfDays",numberOfDays),
														 				   UrlQueryStringParam.of("numberOfAdjacentSlots",numberOfAdjacentSlots.getValue()),
														 				   UrlQueryStringParam.of("slipDateRangeToFindFirstAvailableSlot",slipDateRangeToFindFirstAvailableSlot)));
		String ctxXml = _marshaller.forWriting().toXml(securityContext);
		HttpResponse httpResponse = DelegateForRawREST.GET(restResourceUrl,
										 				   ctxXml);
		// map the response
		String responseStr = httpResponse.loadAsString();		// DO not move!!
		if (Strings.isNullOrEmpty(responseStr)) throw new IllegalStateException(Throwables.message("The REST service {} worked BUT it returned an EMPTY RESPONSE. This is a developer mistake! It MUST return the target entity data",
															   									   restResourceUrl));
		AA14DayRangeTimeSlots outTimeSlots = _marshaller.forReading().fromXml(responseStr,
																			  AA14DayRangeTimeSlots.class);

		return outTimeSlots;
	}
	@Override
	public AA14DayRangeTimeSlots availableTimeSlotsForRange(final SecurityContext securityContext,
												   			final AA14OrgDivisionServiceLocationOID locOid,
												   			final AA14ScheduleOID prefSchOid,
												   			final AA14NumberOfAdjacentSlots numberOfAdjacentSlots,
												   			final Year year,final MonthOfYear monthOfYear,final DayOfMonth dayOfMonth,
												   			final int numberOfDays,
												   			final boolean slipDateRangeToFindFirstAvailableSlot) {
		Url restResourceUrl = this.composeURIFor(this.getServicesRESTResourceUrlPathBuilderAs(AA14RESTServiceResourceUrlPathBuilderForBookedSlotsCalendar.class)
												   			  .pathOfTimeSlotsForRange(locOid,prefSchOid,
												   					  				   year,monthOfYear,dayOfMonth),
												 UrlQueryString.fromParams(UrlQueryStringParam.of("numberOfDays",numberOfDays),
														 				   UrlQueryStringParam.of("numberOfAdjacentSlots",numberOfAdjacentSlots.getValue()),
														 				   UrlQueryStringParam.of("slipDateRangeToFindFirstAvailableSlot",slipDateRangeToFindFirstAvailableSlot)));
		String ctxXml = _marshaller.forWriting().toXml(securityContext);
		HttpResponse httpResponse = DelegateForRawREST.GET(restResourceUrl,
										 				   ctxXml);
		// map the response
		String responseStr = httpResponse.loadAsString();		// DO not move!!
		if (Strings.isNullOrEmpty(responseStr)) throw new IllegalStateException(Throwables.message("The REST service {} worked BUT it returned an EMPTY RESPONSE. This is a developer mistake! It MUST return the target entity data",
															   									   restResourceUrl));
		AA14DayRangeTimeSlots outTimeSlots = _marshaller.forReading().fromXml(responseStr,
																			  AA14DayRangeTimeSlots.class);

		return outTimeSlots;
	}
}
