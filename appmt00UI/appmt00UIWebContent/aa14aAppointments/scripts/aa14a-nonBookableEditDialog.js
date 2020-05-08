///////////////////////////////////////////////////////////////////////////////////////////////////
//	RESERVE SLOT DIALOG
///////////////////////////////////////////////////////////////////////////////////////////////////

// opens the reserve slot dialog
function openReserveSlotDialog(schId,
							   slotOid,
							   startDate,endDate,
							   subject,details,
							   periodicSerieOid) {
	// [1]: fix some things
	var theStartDate;
	var theEndDate;
	if (endDate) {
		// when clicking on a whole day, the start date is the clicked day at 00:00 and the end date is the next day at 00:00
		var dayAfterStartAt00 = moment(startDate).add(1,'days');
		if (endDate.format('YYYY/MM/DD HH:mm') == dayAfterStartAt00.format('YYYY/MM/DD HH:mm')) {
			// set start date at 08:30
			theStartDate = moment(new Date(startDate.year(),startDate.month(),startDate.date()))
							  .hour(bookingConfig.calMinHour).minute(bookingConfig.calMinMin);
			theEndDate = moment(new Date(endDate.year(),endDate.month(),endDate.date()))
							  .hour(bookingConfig.calMaxHour).minute(bookingConfig.calMaxMin);
		} else {
			theStartDate = startDate;
			theEndDate = endDate;
		}
	} else {
		theStartDate = startDate;
		theEndDate = moment(theStartDate).add(bookingConfig.calSlotLength,'minutes');
	}
	
	// [2]: Check if it's NOT weekend
	var isWeekend = (theStartDate.toDate().getDay() == 6) || (theStartDate.toDate().getDay() == 0);
	if (isWeekend) return;
	
	// [3]: Check if it's an acceptable range
	var slot = moment.range(theStartDate,theEndDate);		// the slot date range trying to be booked
	var bookableRange;
	
	// Check that the date is AFTER the current date
	var check = theStartDate;
	var now = moment();
	if (check.isSameOrAfter(now,'year') && check.isSameOrAfter(now,'month') && check.isAfter(now,'day')) {
		// a day after today
		var min = moment(check).hour(bookingConfig.calMinHour).minute(bookingConfig.calMinMin);	// 08:30
		var max = moment(check).hour(bookingConfig.calMaxHour).minute(bookingConfig.calMaxMin);	// 14:30
		bookableRange = moment.range(min,max)
    }
	else if (check.isSame(now,'year') && check.isSame(now,'month') && check.isSame(now,'day')) {
		if (now.hour() < bookingConfig.calMinHour || (now.hour() == bookingConfig.calMinHour && now.minutes() < bookingConfig.calMinMin)) {
			var min = moment(now).hour(bookingConfig.calMinHour).minute(bookingConfig.calMinMin);
			var max = moment(now).hour(bookingConfig.calMaxHour).minute(bookingConfig.calMaxHour);
			bookableRange = moment.range(min,max);
		} else if (now.hour() > bookingConfig.calMaxHour || (now.hour() == bookingConfig.calMaxHour && now.minutes() >= bookingConfig.calMaxMin)) {
			// not bookable
		} else {
			// same day: only times after now are selectable
			var min = now;											// now
			var max = moment(now).hour(bookingConfig.calMaxHour).minute(bookingConfig.calMaxMin);		// 14:30
			bookableRange = moment.range(min,max);
		}
    } 
	var bookable = (bookableRange && bookableRange.overlaps(slot));
	
	console.log("        Bookable range: " + (bookableRange ? bookableRange.toString() : "undefined"));
	console.log("            Slot range: " + slot.toString());
	console.log("Bookable contains slot: " + bookable);
	if (!bookable) return;
	

	// [3]: Set the ui
	// Set the date & start / end time
	
	$("#aa14a_reservedSlot_edit_form input[name='dateNonBookable']").datepicker("setDate",theStartDate.toDate());
	$("#aa14a_reservedSlot_edit_form input[name='timeStartNonBookable']").timepicker("setTime",theStartDate.toDate());	
	$("#aa14a_reservedSlot_edit_form input[name='timeEndNonBookable']").timepicker("setTime",theEndDate.toDate());
	
	// Adjusts the time end non bookable range depending on:
	//		- the start bookable range
	//		- the next event
	// ... so the time range AFTER the next event is NOT bookable
	if (false) {
		var startMoment = moment($("#aa14a_reservedSlot_edit_form input[name='timeStartNonBookable']")
											.timepicker('getTime',
														moment($("#aa14a_reservedSlot_edit_form input[name='dateNonBookable']").datepicker('getDate')).toDate()));
	    adjustTimeEndNonBookableRange(startMoment);
	    adjustTimeStartNonBookableRange(startMoment);
   
    
		// If there exists reserved slots do not enable reserve all day checkbox
	    $("#aa14a_reservedSlot_edit_form #reserveAllDay").attr('disabled',false);
		$("#aa14a_reservedSlot_edit_form input[name='reserveAllDay']").prop('checked',false);
		
		var thisDayEventsArray = calendarEventsAt($("#aa14a_reservedSlot_edit_form input[name='dateNonBookable']")
										.datepicker("getDate"));
		if (thisDayEventsArray !== undefined && thisDayEventsArray.length == 0) {
			// no reserved slots: the reserve all day checkbox can be enabled
			$("#aa14a_reservedSlot_edit_form input[name='reserveAllDay']").attr('disabled',false);
		} else {
			// reserved slots exists: the reserve all day checkbox cannot be enabled
			$("#aa14a_reservedSlot_edit_form input[name='reserveAllDay']").attr('disabled',true);
		}
	}
	
	// set the number of available slots to reserve
	var maxAppsInSlot = maxAppointmentsInSlotBySchId[schId];
	var $maxAppsInSlotSelect = $("#aa14a_reservedSlot_edit_form select[name='numResources']");
	$maxAppsInSlotSelect.empty(); // remove old options
	for(i = 1; i <= maxAppsInSlot; i++) {
	  $maxAppsInSlotSelect.append($("<option></option>")
			  			  .attr("value","M" + i)
			  			  .text(i));
	}
	
	
	// set the ui
	if (slotOid != null) {
		// existing slot
		$("#aa14a_reservedSlot_edit_form").hide();
		$("#aa14a_reservedSlot_delete_form").hide();
		$("#aa14a_existingReservedSlot div.aa14_button_bar").show();
		
		$("#aa14a_reservedSlot_edit_form input[name='dateNonBookable']").datepicker('disable');		// disable the date picker (non bookable slots date cannot be changed using this dialog)
		$("#aa14a_nonBookableRepeat").hide();			// cannot change the periodicity of existing slots
		
		if (periodicSerieOid != null) {
			$("#aa14a_removePeriodicNonBookable").show();
			$("#aa14a_edit_reservedSlot_btn").hide();		// if it's a periodic reserve, it cannot be edited
		} else {
			$("#aa14a_removePeriodicNonBookable").hide();
			$("#aa14a_edit_reservedSlot_btn").show();
		}
		
		// set the edit & remove buttons
		$("#aa14a_edit_reservedSlot_btn").click(
			function() {
				// show the edit form & hide the button bar
				$("#aa14a_reservedSlot_edit_form").show();
				$("#aa14a_existingReservedSlot div.aa14_button_bar").hide();
			});
		$("#aa14a_remove_reservedSlot_btn").unbind( "click" );
		$("#aa14a_remove_reservedSlot_btn").click(
			function() {
				if (periodicSerieOid != null) {
					// show the delete form & hide the button bar
					$("#aa14a_reservedSlot_delete_form").show();
					$("#aa14a_existingReservedSlot div.aa14_button_bar").hide();
				} else {
					var dialog = $("#aa14_booked_slot_release_dialog").dialog();				
					dialog.dialog("open");
				}
			});
		
	} else {
		// new slot
		$("#aa14a_reservedSlot_edit_form").show();
		$("#aa14a_reservedSlot_delete_form").hide();
		$("#aa14a_existingReservedSlot div.aa14_button_bar").hide();
		$("#aa14a_cancel_edit_reservedSlot_btn").hide();
		
		$("#aa14a_reservedSlot_edit_form input[name='dateNonBookable']").datepicker('enable');	// enable the date picker
		$("#aa14a_nonBookableRepeat").show();													// show the periodic slot controls		
		$("#aa14a_nonBookableRepeat .aa14a_nonBookableRange").hide();
		// clear the periodic data controls
		$("#aa14a_nonBookableRepeat input[type='checkbox'][name='periodicNonBookable']").attr('checked',false);
		$("#aa14a_nonBookableRepeat input[name='dateNonBookablePeriodicEnd']").datepicker('setDate','');
		$("#aa14a_nonBookableRepeat input.nonBookableWeekDay[type='checkbox']").each(function(index) {
																							$(this).attr('checked',false);
																					 });
	}	
	
	
	// set the slot id, schedule & subject
	$("#aa14a_reservedSlot_edit_form input[name='schId']").val(schId);
	$("#aa14a_reservedSlot_delete_form input[name='schId']").val(schId);
	$("#aa14a_reservedSlot_delete_form input[name='removePeriodicNonBookable'][value='removeThisBookedSlot']").prop("checked",true);					// by default 
	$("#aa14a_reservedSlot_delete_form input[name='removePeriodicNonBookable'][value='removeAllSerieRelatedBookedSlots']").prop("checked",false);
	if (slotOid != null) {
		// existing slot
		$("#aa14a_reservedSlot_details").text(details);
		
		$("#aa14a_reservedSlot_edit_form input[name='slotOid']").val(slotOid);
		$("#aa14a_reservedSlot_delete_form input[name='slotOid']").val(slotOid);
		
		if (periodicSerieOid != null) {
			$("#aa14a_reservedSlot_edit_form input[name='periodicSerieOid']").val(periodicSerieOid);
			$("#aa14a_reservedSlot_delete_form input[name='periodicSerieOid']").val(periodicSerieOid);
		} else {
			$("#aa14a_reservedSlot_edit_form input[name='periodicSerieOid']").val("");
			$("#aa14a_reservedSlot_delete_form input[name='periodicSerieOid']").val("");
		}

	} else {
		// new slot
		$("#aa14a_reservedSlot_details").text("");
		
		$("#aa14a_reservedSlot_edit_form input[name='slotOid']").val("");
		$("#aa14a_reservedSlot_delete_form input[name='slotOid']").val("");
		
		$("#aa14a_reservedSlot_edit_form input[name='periodicSerieOid']").val("");
		$("#aa14a_reservedSlot_delete_form input[name='periodicSerieOid']").val("");
	}	
	if (subject) {
		$("#aa14a_reservedSlot_edit_form input[name='subject']").val(subject);
	} else {
		$("#aa14a_reservedSlot_edit_form input[name='subject']").val("");
	}
		
	// Open the dialog && hand a parameter to the dialog
	var dialog = $("#aa14a_reserveSlot_dialog");
	dialog.dialog("open");
	if (slotOid != null) dialog.data('slotOid',slotOid);		
	if (periodicSerieOid != null) dialog.data('periodicSerieOid',periodicSerieOid);
}





// Inits the edit booked slot dialog
function initReserveSlotDialog() {
	var dlgWith = $(window).width() - 140;
	var dlgHeigh = $(window).height() - 90;
    $("#aa14a_reserveSlot_dialog").dialog({
										    	title		: titleNonBookable,
    											autoOpen	: false,
										    	modal		: true,
										    	width		: 'auto',
										    	position	: {my: 'top', at: 'top+100', of: window},
										    	close		: function(event,ui) {
										    					restoreReserveSlotDialogUIState();
										    				  },
											    show		: {
															        effect: "blind",
															        duration: 1000
														      },
											    hide		: {
															        effect: "explode",
															        duration: 1000
											      			  }  
    										});
    
    // Date picker & time picker
    _initNonBookableEditDialogTimeSelectors();
    
    // checkbox to reserve all day
    _initNonBookableEditDialogAllDayReserve();

	
	// periodic non-bookable reserve
    _initNonBookableEditDialogPeriodicReserve();

	// Cancel delete
	$("#aa14a_cancel_remove_reservedSlot_btn").click(
		function() {
			// show the edit form
			$("#aa14a_reservedSlot_delete_form").hide();
			$("#aa14a_existingReservedSlot div.aa14_button_bar").show();
		});
	// Delete Button
    $("#aa14_reserved_slot_release_btn").click(
    	function() {
    		hideNonBookableSlotEditError();
			var dialog = $("#aa14_booked_slot_release_dialog").dialog();				
			dialog.dialog("open");
    	});
	
	// Cancel edit
	$("#aa14a_cancel_edit_reservedSlot_btn").click(
		function() {
			hideNonBookableSlotEditError();
			$("#aa14a_reservedSlot_edit_form").hide();
			$("#aa14a_existingReservedSlot div.aa14_button_bar").show();
		});
	// Save Button
    $("#aa14a_update_reservedSlot_btn").click(
    	function() {
			if (validateSlotData()) { 
				var slotOid = $("#aa14a_reservedSlot_edit_form input[name='slotOid']").val();
				var schId = $("#aa14a_reservedSlot_edit_form input[name='schId']").val();
				var reservedSlotDate = $("#aa14a_reservedSlot_edit_form input[name='dateNonBookable']").datepicker('getDate');
				var reservedSlotStartTime = $("#aa14a_reservedSlot_edit_form input[name='timeStartNonBookable']").timepicker('getTime');
				var reservedSlotEndTime = $("#aa14a_reservedSlot_edit_form input[name='timeEndNonBookable']").timepicker('getTime');
				var reservedSlotSubject = $("#aa14a_reservedSlot_edit_form input[name='subject']").val();
				
				updateOrCreateReservedSlot({
											schId		: schId,
											oid			: slotOid,
											date		: reservedSlotDate,
											startTime	: reservedSlotStartTime,
											endTime		: reservedSlotEndTime,
											subject		: reservedSlotSubject,
											success		: function() {
													  			$("#aa14a_reserveSlot_dialog").dialog("close");
													  			// refresh the calendar if present
													  			if ($("#calendar").length) {
													  				refreshCalendar(schId);
																}
																restoreReserveSlotDialogUIState();
														  }
										   });
			}
    	});
}
var _initNonBookableEditDialogTimeSelectors = function() {
	// Date picker & time picker
	$("#aa14a_reservedSlot_edit_form input[name='dateNonBookable']").datepicker();
	
	$("#aa14a_reservedSlot_edit_form input[name='timeStartNonBookable']")
			.timepicker({
						    'minTime'		: bookingConfig.calMinTime,
						    'maxTime'		: bookingConfig.calMaxTime,
						    'step'			: String(bookingConfig.calSlotLength),
						    'useSelect'		: true,			// display as a combo
						    'forceRoundTime': true,
						    'timeFormat'	: 'H:i'
						  });
	
	$("#aa14a_reservedSlot_edit_form input[name='timeEndNonBookable']")
			.timepicker({
					    'minTime'		    : bookingConfig.calMinTime,
					    'maxTime'		    : bookingConfig.calMaxTime,
					    'step'				: String(bookingConfig.calSlotLength),
					    'showDuration'	    : true,
					    'useSelect'		    : true,			// display as a combo
					    'forceRoundTime'    : true,
					    'timeFormat'		: 'H:i'
				 		});
	
	$("#aa14a_reservedSlot_edit_form input[name='timeStartNonBookable']")
				.on('changeTime',
				    function() {
						var startMoment = moment($(this).timepicker('getTime',
																	moment($("#aa14a_reservedSlot_edit_form input[name='dateNonBookable']").datepicker('getDate')).toDate()));
				        console.log("Changed non bookable event start to: " + startMoment.format("HH:mm")); 
				        // set the start time
//				        $("#aa14a_reservedSlot_edit_form input[name='timeStartNonBookable']")
//		        		.timepicker('option', 
//		        				    'minTime',$(this).val());
				        // force the end time to be after the start time
				        $("#aa14a_reservedSlot_edit_form input[name='timeEndNonBookable']")
				        		.timepicker('option', 
				        				    'minTime',$(this).val());
						// Adjusts the time end non bookable range depending on:
						//		- the start bookable range
						//		- the next event
						// ... so the time range AFTER the next event is NOT bookable
				        adjustTimeEndNonBookableRange(startMoment);
				});
}
var _initNonBookableEditDialogAllDayReserve = function() {
	$("#aa14a_reservedSlot_edit_form input[name='reserveAllDay']").change(
			// enable / disable the time selector if all day reserve is selected
			function() {
				var disableTime;
		        if ($(this).is(":checked")) {
		        	disableTime = true;
		        	
		        	// set the end date to the end of the bookable time
		        	var today = new Date();
		        	var endBookable = new Date(today.getFullYear(),today.getMonth(),today.getDate(),bookingConfig.calMaxHour,bookingConfig.calMaxMin);
		        	$("#aa14a_reservedSlot_edit_form input[name='timeEndNonBookable']").timepicker('setTime',endBookable);
		        } else {
		        	disableTime = false;
		        }
		        //$("label[for='timeStartNonBookable']").css('display','none');
		        $("#aa14a_reservedSlot_edit_form input[name='timeStartNonBookable'] + *").prop('disabled',disableTime);
		        //$("label[for='timeEndNonBookable']").css('display','none');
		    	$("#aa14a_reservedSlot_edit_form input[name='timeEndNonBookable'] + *").prop('disabled',disableTime);
			});
}
var _initNonBookableEditDialogPeriodicReserve = function() {
	//$("#aa14a_reservedSlot_edit_form input[name='dateNonBookablePeriodicStart']").datepicker();
	$("#aa14a_reservedSlot_edit_form input[name='dateNonBookablePeriodicEnd']").datepicker();
	
	
	// checkbox to repeat reserve
	$("#aa14a_reservedSlot_edit_form input[name='periodicNonBookable']").change(
		//show / hide the repeat reservation area
		function() {
			if ($(this).is(":checked")) {
				$("#aa14a_reservedSlot_edit_form .aa14a_nonBookableRange").show();
			}
			else{
				$('#aa14a_reservedSlot_edit_form .aa14a_nonBookableRange').hide();
			}
		});
}



// restores the edit pop-up ui state
function restoreReserveSlotDialogUIState() {
	// nothing
}
// validates the slot reservation
function validateSlotData() {
	hideNonBookableSlotEditError();
	
	var schId = $("#aa14a_reservedSlot_edit_form input[name='schId']").val();
	var reservedSlotDate = $("#aa14a_reservedSlot_edit_form input[name='dateNonBookable']").datepicker('getDate');
	var reservedSlotStartTime = $("#aa14a_reservedSlot_edit_form input[name='timeStartNonBookable']").timepicker('getTime');
	var reservedSlotEndTime = $("#aa14a_reservedSlot_edit_form input[name='timeEndNonBookable']").timepicker('getTime');
	
	// note that if ( anyVar ) will return true if anyVar is NOT: null / undefined / NaN / "" / 0 / false
	var valid = schId && reservedSlotDate && reservedSlotStartTime && reservedSlotEndTime;
	
	if (!valid) {
		showNonBookableSlotEditError(nonBookableSlotNotValidMsgError);
		return false;
	}
	
	// validate periodic info
	if ($("#aa14a_reservedSlot_edit_form input[name='periodicNonBookable']").is(":checked")) {
		var dateNonBookablePeriodicEnd = $("#aa14a_reservedSlot_edit_form input[name='dateNonBookablePeriodicEnd']").val();
		var anyWeekDayChecked = $("#aa14a_reservedSlot_edit_form input[name='mondayNonBookablePeriodic'][value='mondayReserve']").is(":checked")
							 || $("#aa14a_reservedSlot_edit_form input[name='tuesdayNonBookablePeriodic'][value='tuesdayReserve']").is(":checked")
							 || $("#aa14a_reservedSlot_edit_form input[name='wednesdayNonBookablePeriodic'][value='wednesdayReserve']").is(":checked")
							 || $("#aa14a_reservedSlot_edit_form input[name='thursdayNonBookablePeriodic'][value='thursdayReserve']").is(":checked")
							 || $("#aa14a_reservedSlot_edit_form input[name='fridayNonBookablePeriodic'][value='fridayReserve']").is(":checked");
		if (!anyWeekDayChecked || !dateNonBookablePeriodicEnd) {
			showNonBookableSlotEditError(nonBookableSlotNoEndDateMsgError);
			return false;
		}
	}
	return true;
}
function showNonBookableSlotEditError(msg) {
	$("#aa14a_nonBookableEdit_error > p").html(msg);
	$("#aa14a_nonBookableEdit_error").show();
}
function hideNonBookableSlotEditError() {
	$("#aa14a_nonBookableEdit_error > p").html("");
	$("#aa14a_nonBookableEdit_error").hide();
}
// update or create a reserve
function updateOrCreateReservedSlot(reservedSlotData) {
	$("#aa14a_reservedSlot_edit_form input[name='op']").val("RESERVAR_SLOT");
	$("#aa14a_reservedSlot_edit_form input[name='dateNonBookable']").datepicker('enable');	// enable the date picker (if not enabled, it's not serialized!!)
	console.log("...update the date/time for reserved slot id=" + reservedSlotData.oid + ": " + $("#aa14a_reservedSlot_edit_form").serialize());
	$.ajax({
		  url		: '/aa14aUIWar/AA14ControllerServlet?R01HNoPortal=true',
		  data		: $("#aa14a_reservedSlot_edit_form").serialize(),
		  type		: "post",
		  success	: function(responseText) {			  				
				  			if (responseText) console.log("[OK] " + responseText + " slots created/updated");		//oid=" + responseText._oid._id);
			 				if (reservedSlotData.success == "undefined") return;
			 				reservedSlotData.success();
					   },
		  error		: function (xhr,ajaxOptions,thrownError) {
						  var msgErrores = "";
						  if (xhr.responseText == 105) {
							  console.log(">>> Booked slot!!!");
						  }  else {
							  showServerErrorDialog(msgErrorGenerico);
						  }
					  },
		  beforeSend: function() {
			  			  $("#aa14a_update_reserved_slot_btn").hide();
						  $body.addClass("loading"); 
					  },
	      complete	: function() {
	    	  			  $("#aa14a_update_reserved_slot_btn").show();
	    	  			  $body.removeClass("loading");  
	    	  		  }
	});
}
///////////////////////////////////////////////////////////////////////////////////////////////////
// NON-BOOKABLE SLOTS TIME ADJUST
// TODO move this logic to the server... now it just works because there can only exists an appointment in a slot but the logic will complicate when more than a single appointment can occupy a single slot
///////////////////////////////////////////////////////////////////////////////////////////////////
// Adjusts the time start/end non bookable range depending on:
//		- the start bookable range
//		- the next event
// ... so the time range AFTER the next event is NOT bookable
function adjustTimeStartNonBookableRange(startMoment) {
	var eventsBeforeStart = calendarEventsBefore(startMoment);
    if (eventsBeforeStart !== undefined && eventsBeforeStart.length > 0) {
    	var eventBefore = eventsBeforeStart[eventsBeforeStart.length-1];
    	var eventBeforeEnd = moment(eventBefore.end);
    	console.log("... event before ends at: " + eventBeforeEnd.format("HH:mm") + "; disable start time picking from " + eventBeforeEnd.format("HH:mm"));
    	var disabledTimeRange = [[bookingConfig.calMinHour + ':' + bookingConfig.calMinMin,eventBeforeEnd.format("HH:mm")]];
    	$("#aa14a_reservedSlot_edit_form input[name='timeStartNonBookable']")
    				.timepicker('option',
    							'disableTimeRanges',disabledTimeRange);	// disable booked time ranges (like `[['8:30am', '9:00am'], ['11:00am', '11:30am']])
    } else {
    	$("#aa14a_reservedSlot_edit_form input[name='timeStartNonBookable']")
    				.timepicker('option',
    						    'disableTimeRanges',[]);
    }
}
function adjustTimeEndNonBookableRange(startMoment) {
    // ensure the end time is NOT greater than the next booked slot
    var eventsAfterStart = calendarEventsAfter(startMoment);
    if (eventsAfterStart !== undefined && eventsAfterStart.length > 0) {
    	var nextEvent = eventsAfterStart[0];
    	if (nextEvent.id == $("#aa14a_reservedSlot_edit_form input[name='slotOid']").val()) return;
    	
    	var nextEventStart = moment(nextEvent.start).add(1,'minutes');
    	console.log("... next event starts at: " + nextEventStart.format("HH:mm") + "; disable end time picking from " + nextEventStart.format("HH:mm"));
    	var disabledTimeRange = [[nextEventStart.format("HH:mm"),bookingConfig.calMaxHour + ':' + (bookingConfig.calMaxMin+1)]];
    	$("#aa14a_reservedSlot_edit_form input[name='timeEndNonBookable']")
    				.timepicker('option',
    						    'disableTimeRanges',disabledTimeRange);	// disable booked time ranges (like `[['8:30am', '9:00am'], ['11:00am', '11:30am']])
    } else {
    	$("#aa14a_reservedSlot_edit_form input[name='timeEndNonBookable']")
    				.timepicker('option',
    						    'disableTimeRanges',[]);
    }
}
//**
// Refresh the options of the timepickers with a new booking config
//**
function refreshTimepickers(){
	 
	 //start time
	 $("#aa14a_reservedSlot_edit_form input[name='timeStartNonBookable']")
		.timepicker('option', 'minTime', bookingConfig.calMinTime);
	 $("#aa14a_reservedSlot_edit_form input[name='timeStartNonBookable']")
		.timepicker('option', 'maxTime', bookingConfig.calMaxTime);
	 $("#aa14a_reservedSlot_edit_form input[name='timeStartNonBookable']")
		.timepicker('option', 'step', String(bookingConfig.calSlotLength));
	 
	 //end time
	 $("#aa14a_reservedSlot_edit_form input[name='timeEndNonBookable']")
		.timepicker('option', 'minTime', bookingConfig.calMinTime);
	 $("#aa14a_reservedSlot_edit_form input[name='timeEndNonBookable']")
		.timepicker('option', 'maxTime', bookingConfig.calMaxTime);
	 $("#aa14a_reservedSlot_edit_form input[name='timeEndNonBookable']")
		.timepicker('option', 'step', String(bookingConfig.calSlotLength));
}


