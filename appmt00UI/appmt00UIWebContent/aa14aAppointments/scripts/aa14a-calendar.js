// returns the selected schedule 
// (see aa14Init.jsp)
function getSelectedScheduleId() {
	var outSchId;
	if ($("input:radio[name='schId']").length) {
		outSchId = $("input:radio[name='schId']:checked").val()
	} else {
		console.log("WARNING!!! could NOT get the selected schedule id!!");
	}
	return outSchId;	
}


var calRefreshMilis = 15000;	// 15 sg

var currentCalendarEventSource;
var currentCalEventId = null;


// Inits the calendar 
function paintCalendar() {
	
	var calBusinessHourStart = bookingConfig.calMinHour + ':' + bookingConfig.calMinMin;				// ie: 08:30
	var calBusinessHourEnd = bookingConfig.calMaxHour + ':' + bookingConfig.calMaxMin;					// ie: 18:00
	
	var calMinTime = moment(calBusinessHourStart+":00", "HH:mm:ss")
	 				.subtract(bookingConfig.calSlotLength,'minutes').format('HH:mm');		// ie: 08:30:00	
	var calMaxTime = moment(calBusinessHourEnd+":00", "HH:mm:ss")
	 				.add(bookingConfig.calSlotLength,'minutes').format('HH:mm');				// ie: 18:00:00
	var numberOfSlotsPerHour = 60/bookingConfig.calSlotLength;
	var labelInterval = (numberOfSlotsPerHour % 2 == 0)? 30:10;
	
	$('#calendar').fullCalendar({
		timezone 		: 'local',		
		height			: bookingConfig.calSlotLength <= 30 ? 600 : 1000,
		header			: {
							left	: 'prev,next today',
							center	: 'title',
							right	: 'agendaDay,agendaWeek,month'
					   	  },
		allDaySlot		: false,
		slotEventOverlap: false,
		minTime			: calMinTime,		// ie: 08:30:00	
		maxTime			: calMaxTime,		// ie: 18:00:00
		slotDuration	: '00:' + bookingConfig.calSlotLength + ':00',		// slot duration (depends on the schedule default slot duration)
		slotLabelFormat:  bookingConfig.calSlotLength <= 30 ?'HH:mm':'HH(:mm)',
		slotLabelInterval	: {minutes:labelInterval},
		nowIndicator	: true,
		firstDay		: 1,				// monday
		defaultView		: 'agendaDay',
		weekNumbers		: true,	
		businessHours	: {
						    start	: calBusinessHourStart, 	// a start time (ie '08:30')
						    end		: calBusinessHourEnd, 		// an end time (ie '14:30')
						    dow		: [ 1, 2, 3, 4, 5 ]			// days of week. an array of zero-based day of week integers (0=Sunday)
						  },
		editable		: false,	// cannot edit events
		eventLimit		: true, 	// allow "more" link when too many events
		eventDestroy	: // log event destroying 
						  function(event,element,view) {
            					// console.log("\t... remove event with id=" + event.id);
        				  },
		eventClick		: // depending on the type of event open the editAppointment dialog or the reserve slot dialog
						  function(calEvent,jsEvent,view) {
								if (calEvent.kind == "APPOINTMENT") {
									// an appointment
									openEditAppointmentDialog(calEvent.id,
															  calEvent.locId,calEvent.schId,
															  calEvent.title,
															  new Date(calEvent.start));	
								} else {
									// a non-bookable slot
									openReserveSlotDialog(getSelectedScheduleId(),
														  calEvent.id,			
														  calEvent.start,calEvent.end,
														  calEvent.subject,calEvent.details,
														  calEvent.periodicSlotSerieOid);	
								}
						  },
		// create an appointment by clicking on the calendar
		dayClick		: function(date,allDay,jsEvent,view) {
								// see select
				  		  },
		// click & drag
		selectable		: true,		// Allows a user to highlight multiple days or timeslots by clicking and dragging
		selectOverlap 	: true,	// do not allow user to select where there's another event
		select			: function(start,end,jsEvent,view) {	// event raised when selecting by clicking and dragging
								openReserveSlotDialog(getSelectedScheduleId(),
													  null,		// no slotId... it's a new reserve
													  start,end,
													  null,		// no subject
													  null);	// no periodic serie oid
						  }
//		defaultDate		: '2016-01-12',		
		// fullCalendar will add the eventSource url the start & end params with an ios8061 date
//		eventSources	: [				              
//		               		eventSourceUrl
//		              	  ]
//		events			: [
//							{allDay:false,editable:false,id:'aa14lc8152a0d850231d98c66d8527fc13652ce21f',title:'Event',start:'2016-02-03T08:30Z',end:'2016-02-03T09:30Z'}
//		        		  ]
	});
}

// sets the calendar source
function _setCalendarEventsSource(schId) {
	if (schId == null || schId == "undefined") schId = getSelectedScheduleId();
	
	var eventSourceUrl = "/aa14aUIWar/AA14CalendarServlet?op=range_appointments&schId=" + schId +
														 "&lang=" + idioma + 	// see aa14aJSDynamicVars.inc
														 "&R01HNoPortal=true";
	console.log("[Calendar]: refreshing for: " + schId + " > " + eventSourceUrl);	
	// [2]: Add the new source
	currentCalendarEventSource = {
									id:schId,
									src:eventSourceUrl
								  };
	$("#calendar").fullCalendar('addEventSource',
								eventSourceUrl); 			// Add a new source
}
// removes the calendar source
function _removeCalendarEventsSource() {
	if (currentCalendarEventSource != null) {
		// a) Remove all events
		$("#calendar").fullCalendar("removeEvents"); 
		
		// b) remove the source
		// method1: see http://www.coderrific.com/forum/detail/removing_all_eventsources_in_jquery_fullcalendar_plugin							// Hide all events
		// $('#calendar').fullCalendar('removeEventSource',$('.Source').val()); 	// remove eventSource from stored hidden input
		
		// method2: see https://code.google.com/p/fullcalendar/issues/detail?id=678
		$("#calendar").fullCalendar("removeEventSource",
									 currentCalendarEventSource.src);  	
	} 
}

// An anonymous function that gets called periodically to refresh the calendar
(
		function() {
			var calendar = $("#calendar");
			var editAppointmentDialog = $("#aa14a_editAppointment_dialog");
			var reserveSlotDialog = $("#aa14a_reserveSlot_dialog");
			
			if (!calendar) {
				console.log("... NO calendar: do NOT refresh calendar");
				return;
			}
			var doRefresh = true;
			if (editAppointmentDialog && editAppointmentDialog.dialog("isOpen")) { 
				console.log("... editAppointmentDialog dialog is open: do NOT refresh calendar");
				doRefresh = false;
			}
			if (reserveSlotDialog && reserveSlotDialog.dialog("isOpen")) {
				console.log("... reserveSlotDialog dialog is open: do NOT refresh calendar");
				doRefresh = false;				
			}
			
			if (doRefresh) {
				console.log("Calendar periodic refresh (every " + calRefreshMilis + " milis)");
				refreshCalendar(getSelectedScheduleId());
			}
			setTimeout(arguments.callee,
					   calRefreshMilis);
		}
)();

// refreshes the calendar
function refreshCalendar(schId) {
	if (schId == null || schId == "undefined") schId = getSelectedScheduleId();
	
	if (schId == null || schId == "null" || schId == "undefined") {
		console.log("... not refreshing calendar since no schedule radio is selected");
		return;
	}
	
//	// [1]: Remove the previous sources
//	_removeCalendarEventsSource();
//	
//	// [2]: Add the new source
//	_setCalendarEventsSource(schId);
	
	$('#calendar').fullCalendar('refetchEvents');
}
///////////////////////////////////////////////////////////////////////////////////////////////////
//	CALENDAR EVENTS RETRIEVE
///////////////////////////////////////////////////////////////////////////////////////////////////
function calendarEventsAsTimeRanges(eventArray) {
	// ... transform it into an array of time ranges like `[['3:00am', '4:30am'], ['5:00pm', '8:00pm']]
	var timeRangesArray = [];
	$.each(eventArray, 
		   function(key,event) { 
				var timeRange = [event.start.format("HH:mm"),event.end.format("HH:mm")];
				timeRangesArray.push(timeRange);
		   });
	return timeRangesArray;
}

// Filters the events that the calendar has in it's memory to get only 
// the ones at the given date
function calendarEventsAt(date) {
	var eventsAt = $("#calendar").fullCalendar('clientEvents',
											   function(event) {
													return event.start.isSame(date,'day');
												})
								 .sort(calendarEventCompareFunction);	
	return eventsAt;
}
// Filters the events that the calendar has in it's memory to get only 
// the ones BEFORE the given one
function calendarEventsBefore(date) {
	var eventsBefore = $("#calendar").fullCalendar('clientEvents',
												   function(event) {	
														return event.start.isSame(date,'day')
														    && event.start.isBefore(date);
												   })
									 .sort(calendarEventCompareFunction);
	return eventsBefore;
}
// Filters the events that the calendar has in it's memory to get only 
// the ones AFTER the given one
function calendarEventsAfter(date) {
	var eventsAfter = $("#calendar").fullCalendar('clientEvents',
												  function(event) {	
														return event.start.isSame(date,'day')
														    && event.start.isAfter(date);
												  })
									.sort(calendarEventCompareFunction);
	return eventsAfter;
}
// a calendar event compare function
var calendarEventCompareFunction = function(event1,event2) {
										if (event1.start < event2.start) {
									 			return -1;
								 		} else if (event1.start > event2.start) {
								 			return 1;
								 		} else {
								 			return 0;
								 		}
									};

