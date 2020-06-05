function getAvailableSlotsCalendarSelectedSlotId() {
	return  $("input:radio[name=radfree]:checked").val();
}
//// extracts the slot date from it's id
//(server side: AA14ControllerServletDelegateForAppointmentCreate)
function getAvailableSlotsCalendarSelectedSlotAsDate() {
	//  id=SLTID_{dd}_{MM}_{yyyy}_{HH}_{mm}_{schOid}
	//		[0]   [1] [2]   [3]   [4]  [5]   [6]
	//	Ej: id=SLTID_25_4_2019_11_0_6CD9FB63-B535-4819-BC58-4CAD429D4B45
	var selectedSlotId =  getAvailableSlotsCalendarSelectedSlotId();
	
	var idSplitted = selectedSlotId.split("_");
	var day = idSplitted[1]; 
	var month = idSplitted[2];
	var year = idSplitted[3];  
	var hour = idSplitted[4];
	var minutes = idSplitted[5];
	var schId = idSplitted[6];
	
	var fechaCita = new Date(year,month-1,day,hour,minutes);
	return fechaCita;
}
// // extracts the slot date from it's id
// (server side: AA14ControllerServletDelegateForAppointmentCreate)
function getAvailableSlotsCalendarSelectedSlotDate() {
	//  id=SLTID_{dd}_{MM}_{yyyy}_{HH}_{mm}_{schOid}
	//		[0]   [1] [2]   [3]   [4]  [5]   [6]
	//	Ej: id=SLTID_25_4_2019_11_0_6CD9FB63-B535-4819-BC58-4CAD429D4B45
	var selectedSlotId =  getAvailableSlotsCalendarSelectedSlotId();
	
	var idSplitted = selectedSlotId.split("_");
	var day = idSplitted[1]; 
	var month = idSplitted[2];
	var year = idSplitted[3];  
	var hour = idSplitted[4];
	var minutes = idSplitted[5];
	var schId = idSplitted[6];
	
	var fechaCita = new Date(year,month-1,day);
	hour = ("0" + hour).slice (-2);
	minutes = ("0" + minutes).slice (-2);
	if (idioma == "eu") {	// see aa14aJSDynamicVars.inc
		return diasSemana[fechaCita.getDay()]+" "+year+"/"+month+"/"+day+" ["+hour+":"+minutes+"]";
	} else {
		return diasSemana[fechaCita.getDay()]+" "+day+"/"+month+"/"+year+" ["+hour+":"+minutes+"]";
	}
}
// valida que se ha seleccionado un hueco
// (utilizado allí donde se utiliza el selector de huecos)
function validarCitaSeleccionada() {
	if ( !$("input:radio[name=radfree]").is(':checked')) { 
		var msgErrores = "<p class='aa14a_warning'><span class='fa fa-warning'></span>" + citaMsgError + "</p>"
		$("#aa14a_errores_citas").html(msgErrores);
		$("#aa14a_errores_citas").show();
		$("#aa14a_step3_error").show();
		return false;
	} else {
		$("#aa14a_step3_error").hide();
		$("#aa14a_errores_citas").hide();
		return true;
	}
}

///////////////////////////////////////////////////////////////////////////////////////////////////
//	SLOT TABLE
///////////////////////////////////////////////////////////////////////////////////////////////////
function getNextAvailableSlots(selectedDate, 
								numberOfAdjacentSlots, 
								serviceLocId, 
								schId,
								moreAvailable) {
	if (!moreAvailable){
		 return false;
	}
	selectedDate.setTime(selectedDate.getTime()+5*24*60*60*1000 );
	$("#date").datepicker( "setDate", fecha );
	if (schId && schId != ""){
		obtenerCitasLibres(selectedDate,
					       false,		// DO NOT slip date range to find first available slot
					       numberOfAdjacentSlots,
					       null,
					       schId);
	}
	else {
		obtenerCitasLibres(selectedDate,
				   		   false,		// DO NOT slip date range to find first available slot
				   		   numberOfAdjacentSlots,
				   		   serviceLocId);
	}
	
}
function getPreviousAvailableSlots(selectedDate, 
								  numberOfAdjacentSlots, 
								  serviceLocId,
								  schId) {
	if (selectedDate <= new Date()) {
		return false;
	}
	selectedDate.setTime(selectedDate.getTime()-5*24*60*60*1000 );
	if (selectedDate < new Date()){
		selectedDate = new Date();
	}
	$("#date").datepicker("setDate",selectedDate);
	if (schId && schId != ""){
		obtenerCitasLibres(selectedDate,
					       false,		// DO NOT slip date range to find first available slot
					       numberOfAdjacentSlots,
					       null,
					       schId);
	}
	else {
		obtenerCitasLibres(selectedDate,
				   		   false,		// DO NOT slip date range to find first available slot
				   		   numberOfAdjacentSlots,
				   		   serviceLocId);
	}
}
function obtenerCitasLibres(selectedDate,
							slipDateRangeToFindFirstAvailableSlot,
							numberOfAdjacentSlots,
							serviceLocId,
							schId) { //schedule id, optional only if serviceLocId is not null
	//Refresh the schedule config for calSlotLength
	if (serviceLocId){
		console.log("...get free slots: " + serviceLocId);
		_loadBookingConfigForLocation(serviceLocId,false);
	}
	else {
		console.log("...get free slots: " + schId);
		_loadBookingConfigForSchedule(schId,false);
	}
	
	var dd = selectedDate.getDate();
	var mm = selectedDate.getMonth()+1; // today=0!
	var yyyy = selectedDate.getFullYear();
	if (serviceLocId){
		console.log("...get free slots: " + serviceLocId);
	}
	else {
		console.log("...get free slots: " + schId);
	}
	$.ajax({
		  url		: '/' + appmtContextRoot + '/AA14ControllerServlet?R01HNoPortal=true',
		  data		: "op=OBTENERCITAS&year=" + yyyy + "&month=" + mm + "&day=" + dd + 
		  			    "&serviceLocId=" + serviceLocId + "&prefSchId=" + schId +			// locId + optional preferred schedule id
		  			    "&schId=" + schId +			// schId
		  			    "&numberOfAdjacentSlots=" + numberOfAdjacentSlots + //number of slots per appointment
		  			    "&slipDateRangeToFindFirstAvailableSlot=" + slipDateRangeToFindFirstAvailableSlot,		// slip date range to find first available slot
		  type		: "get",
		  success	: function(dayRangeTimeSlots) {
			  			  		if (dayRangeTimeSlots._dayTimeSlots.length>0){
			  			  			var firstDayWithAvailableSlots = new Date(_yearOf(dayRangeTimeSlots._dayTimeSlots[0]),
			  			  													  _monthOfYearOf(dayRangeTimeSlots._dayTimeSlots[0])-1,
			  			  													  _dayOfMonthOf(dayRangeTimeSlots._dayTimeSlots[0]));
			  			  			
			  			  			$("#date").datepicker("setDate",firstDayWithAvailableSlots);
			  			  			var allowPreviousDayNavigation = firstDayWithAvailableSlots > new Date();
			  			  			var allowNextDayNavigation = dayRangeTimeSlots._moreAvailable === true;
				  			  		// [1] - Paint the schedule; every cell has an id like: D{dayOfMonth}_{monthOfYear}_{year}_{hourOfDay}_{minuteOfHour}
	  			  					//	   and an style="ocupado"
	  			  					buildScheduleTable({ _dayTimeSlots : dayRangeTimeSlots._dayTimeSlots }, allowPreviousDayNavigation, allowNextDayNavigation);
				  			  
	  			  					// [2] - Update the schedule with the available slots
	  			  					//	   	a) Find the slot cell by it's id: D{dayOfMonth}_{monthOfYear}_{year}_{hourOfDay}_{minuteOfHour}
	  			  					//		b) set style=libre
	  			  					//		c) add an input[type=radio] with value=id_{scheduleOid} => D{dayOfMonth}_{monthOfYear}_{year}_{hourOfDay}_{minuteOfHour}_{scheduleOid}
	  			  					updateScheduleTableWithAvailableSlots({ _dayTimeSlots : dayRangeTimeSlots._dayTimeSlots }, numberOfAdjacentSlots);
	  			  					// [3] Set a link to previous days
	  			  					if (allowPreviousDayNavigation){
		  			  					$(".aa14a_menos_dias,.aa14_menos_dias_movil").click(function() {
							    	  															getPreviousAvailableSlots(firstDayWithAvailableSlots,
							    	  																					  numberOfAdjacentSlots,
							    	  																				      serviceLocId,
							    	  																				      schId);
							      												  			});
		  			  				}
	  			  					// [4] Set a link to next days
	  			  					if (allowNextDayNavigation){
		  			  					$(".aa14a_mas_dias,.aa14_mas_dias_movil").click(function() {
		  			  																			getNextAvailableSlots(firstDayWithAvailableSlots,
		  			  																								 numberOfAdjacentSlots, 
		  			  																								 serviceLocId, 
		  			  																								 schId,
		  			  																								 allowNextDayNavigation);
							      												  		});
		  			  				}
	  			  					//If after arranging slots there is nothing to reserve and no other days can't be requested
	  			  					//we hide the table
	  			  					var allBooked = countNumberOfAvailableSlotsPainted()==0;
	  			  					if (allBooked && !dayRangeTimeSlots._moreAvailable && fecha<= new Date()){
	  			  						$("div.aa14a_selector_fecha").hide();
	  			  						$("#aa14_calendar_instructions").hide();
	  			  						$("#aa14a_step3_next_btn").hide();
	  			  						$("#citas_table").empty();
	  			  						$("#aa14a_errores_citas").html("<p class='aa14a_warning'><span class='fa fa-warning'></span>" + noAvailableAppointments + "</p>");
	  			  						$("#aa14a_errores_citas").show();
	  			  					}
			  					}
			  					else{
			  						$("div.aa14a_selector_fecha").hide();
  			  						$("#aa14_calendar_instructions").hide();
  			  						$("#aa14a_step3_next_btn").hide();
  			  						$("#citas_table").empty();
  			  						$("#aa14a_errores_citas").html("<p class='aa14a_warning'><span class='fa fa-warning'></span>" + noAvailableAppointments + "</p>");
  			  						$("#aa14a_errores_citas").show();
			  					}
		  			  },
		  error		: function(xhr,ajaxOptions,thrownError) {
						  showServerErrorDialog(msgErrorGenerico,
					  				function() {
				  						gotoFormStep('#aa14a_step3');
			  						});
		  		 	  }
	});
};

// [1] - Paint the schedule; every cell has an id like: D{dayOfMonth}_{monthOfYear}_{year}_{hourOfDay}_{minuteOfHour}
//	     and an style="ocupado"
function buildScheduleTable(dayRangeTimeSlots, showPrevious, showNext, prefSchId) {
	$("div.aa14a_selector_fecha").show();
	$("#aa14_calendar_instructions").show();
	$("#aa14a_step3_next_btn").show();
	$("#aa14a_errores_citas").hide();
	$("#citas_table").empty();
	 var trHTML = '';
	  
	 /* === HEADER */
	 trHTML += '<thead>';
	 if (showPrevious){
		 trHTML += '<th data-title="" class="aa14a_menos_dias aa14_menos_dias_movil"><span class="fa fa-backward backward"></span>' + anterior + '</th>';
	 }
	 else{
		 trHTML += '<th data-title="" class="aa14a_menos_dias_disabled aa14_menos_dias_movil_disabled"><span class="fa fa-backward backward"></span>' + anterior + '</th>';
	 }
	 trHTML += '<th class="dateRange">' + tramo + '</th>';	

     $.each(dayRangeTimeSlots, 		// >>>> AA14DayRangeTimeSlots
    		function(i,item) {
	        	$.each(item,		// >>>> AA14DayTimeSlots
	        		   function(j,dayTimeSlot){
	        				if (idioma == 'eu') {	// see aa14aJSDynamicVars.inc
			        			fecha = _yearOf(dayTimeSlot) + '/' + ("0" + _monthOfYearOf(dayTimeSlot)).slice(-2) + '/' + ("0" + _dayOfMonthOf(dayTimeSlot)).slice(-2);
			        		} else {
			        			fecha = ("0" + _dayOfMonthOf(dayTimeSlot)).slice (-2) + '/' + ("0" + _monthOfYearOf(dayTimeSlot)).slice(-2) + '/' + _yearOf(dayTimeSlot);
			        		}
			        		var thisWeekDay = new Date(_yearOf(dayTimeSlot),_monthOfYearOf(dayTimeSlot)-1,_dayOfMonthOf(dayTimeSlot)).getDay();
			        		var weekendClassName="";
			        		//color the weekends differently
			        		if (thisWeekDay==0 || thisWeekDay==6){
			        			weekendClassName=" class=\"notAvailable\" ";
			        		}
			        		trHTML += '<th'+weekendClassName+'>' + diasSemana[thisWeekDay] + 
			        						   '<br/>' + 
			        						   fecha + 
			        				  '</th>';
			        	});
	        	
	        });	
     if (showNext){
    	 trHTML += '<th data-title="" class="aa14a_mas_dias aa14_mas_dias_movil"><span class="fa fa-forward forward"></span>' + siguiente + '</th></thead>';    
     }
     else{
    	 trHTML += '<th data-title="" class="aa14a_mas_dias_disabled aa14_mas_dias_movil_disabled"><span class="fa fa-forward forward"></span>' + siguiente + '</th></thead>';    
     }
     trHTML += '<tbody>';
     trHTML += '<tr class="aa14a_navegacion_dias_movil"><td>';
     trHTML += 		'<div class="aa14a_anterior_dia_movil"><span class="fa fa-backward backward aa14a_span_blanco_movil"></span></div>';
     trHTML += 		'<div class="aa14a_siguiente_dia_movil"><span class="fa fa-forward forward aa14a_span_blanco_movil"></span></div>';
     trHTML += '</td></tr>';

     /* === TRAMOS */     
     var start = moment().hours(bookingConfig.calMinHour).minutes(bookingConfig.calMinMin);
     var end = moment().hours(bookingConfig.calMaxHour).minutes(bookingConfig.calMaxMin);
     var curr = start;
     do {
    	 var next = moment(curr).add(bookingConfig.calSlotLength,'minutes');
    	 trHTML += buildSlotRangeCell(curr.format('HH:mm') + " - " + next.format('HH:mm'),		// horario
    			 			   parseInt(curr.hour()),parseInt(curr.minutes()),			// idtramo
    			 			   dayRangeTimeSlots);
    	 curr = next;
     } while ((curr.hour() < end.hour())
    	   || (curr.hour() == end.hour() && curr.minute() < end.minute()));
     
     

     trHTML += '</tbody>';
     $("#citas_table").append(trHTML);
};

// Paint timerange column
function buildSlotRangeCell(horario,
					 hourOfDay,minuteOfHour,
					 dayRangeTimeSlots) {
	/* Pintar tramo 9 a 9:30 */
    var tramoHTML = '<tr><td data-title="" class="aa14a_menos_dias aa14_menos_dias_movil">' +
    					'</td><td data-title=' + tramo + ' >' + horario + '</td>';
    $.each(dayRangeTimeSlots, 
    	   function(i,item) {
		    	$.each(item, 
		    		   function(j,dayTimeSlots) {
				    		if (idioma == 'eu') {	// see aa14aJSDynamicVars.inc
				    			fecha = _yearOf(dayTimeSlots) + '/' + _monthOfYearOf(dayTimeSlots) + '/' + _dayOfMonthOf(dayTimeSlots);
				    		} else {
				    			fecha = _dayOfMonthOf(dayTimeSlots) + '/' + _monthOfYearOf(dayTimeSlots) + '/' + _yearOf(dayTimeSlots);
				    		}
				    		var id = slotId(_yearOf(dayTimeSlots).toString(),_monthOfYearOf(dayTimeSlots).toString(),_dayOfMonthOf(dayTimeSlots).toString(),
				    						hourOfDay,minuteOfHour);
				    		tramoHTML += '<td  data-title="' + fecha + '" class="ocupado" id="' + id + '">' + ocupado + '</td>';
		    		   });
   			});
    tramoHTML += 		'<td data-title="" class="aa14a_mas_dias aa14_mas_dias_movil"></td>'; 
    tramoHTML += '</tr>';
    return tramoHTML;
}

//[2] - Update the schedule with the available slots
//a) Find the slot cell by it's id: D{dayOfMonth}_{monthOfYear}_{year}_{hourOfDay}_{minuteOfHour}
//b) set style=libre
//c) add an input[type=radio] with value=id_{scheduleOid} => D{dayOfMonth}_{monthOfYear}_{year}_{hourOfDay}_{minuteOfHour}_{scheduleOid}
//d) combine slots if we need to book more than one slot at a time
function updateScheduleTableWithAvailableSlots(dayRangeTimeSlots, numberOfAdjacentSlots) {
	//color green the free slots
	applyClassToSlots(dayRangeTimeSlots);
	//Combine cells in case the numberOfAdjacentSlots is >1
	if (numberOfAdjacentSlots>1){
		combineSlots(numberOfAdjacentSlots, dayRangeTimeSlots._dayTimeSlots.length);
	}
}


//Update the schedule with the available slots
function applyClassToSlots(dayRangeTimeSlots) {
	var now = moment();
	
	$.each(dayRangeTimeSlots,	// >>>> AA14DayRangeTimeSlots
	   function(i,item) {	
	    	$.each(item, 		// >>>> AA14DayTimeSlots
	    		   function(j,dayTimeSlots) {	
	    				$.each(dayTimeSlots._timeSlots, 	// >>>> AA14TimeSlot
		        			   function(k,timeSlot) {
	    						if (timeSlot._available){
		    								var year = _yearOf(dayTimeSlots).toString();
			        					
				        					var monthOfYear = _monthOfYearOf(dayTimeSlots).toString();
				        					var dayOfMonth = _dayOfMonthOf(dayTimeSlots).toString();
				        					var day = moment(new Date(year,monthOfYear-1,dayOfMonth));
				        					
				        					var isToday = day.isSame(now,'year') && day.isSame(now,'month') && day.isSame(now,'day');
				        					var todayStyleClass = isToday ? " today" : "";
				        					
							        		var id = slotId(year,monthOfYear,dayOfMonth,
							        						_hourOfDayOf(timeSlot),_minuteOfHourOf(timeSlot));
							        		var theSlotId = id + '_' + timeSlot._scheduleOid._id;
							        		
				        					var slotCell = $("#" + id);	
				        		
							        		slotCell.html('<input type="radio" value="' + theSlotId + '" name="radfree" class="libre' + todayStyleClass + '">' + libre + '</input>');
							        		slotCell.removeClass();											// removes the "ocupado" style
							        		slotCell.addClass("libre" + todayStyleClass);					// add the "libre" style
	    					}
		        		});
		});
	});

}


//Combine rows if a appointment needs more than one slot
function combineSlots(numberOfAdjacentSlots, numberOfDays) {
	var ancestorId=""; //select the table in the edit dialog or the table in the create dialog
	if($("#aa14a_editAppointment_dialog") && $("#aa14a_editAppointment_dialog").dialog("isOpen")==true){
		ancestorId = "#aa14a_editAppointment_form ";
	}

	var totalCols=numberOfDays + 3 ; //previous+date range + N days + next  
	var totalRows=$( ancestorId + "table#citas_table>tbody>tr").size(); //number of slots per day for this schedule
	var colInit=3; //skip previous and date range columns, index starts with 1
	var rowInit=2; //skip header row, index starts with 1		
	//loop through cells starting at the top left side of the table
	for (row = rowInit; row < totalRows; row++) {	
		for(col=colInit; col< totalCols; col++){
			//Check if this cell needs to be expanded
			var cell=$(ancestorId + "table#citas_table>tbody>tr:nth-child(" + row + ") > td:nth-child(" + col + ")");
			if (cell.prop('rowspan') && cell.prop("className") != "ocupado" && cell.text()!=" "){
				//mark the N cells below to be deleted later
				var nextSlotCellRowPosition = parseInt(row) + parseInt(numberOfAdjacentSlots) - 1;
				var nextSlotCell = $(ancestorId + "table#citas_table>tbody>tr:nth-child(" + nextSlotCellRowPosition + ") > td:nth-child("+col+")");
				
				if (nextSlotCell.prop("id")!=undefined){ 
					if (nextSlotCell.prop("id") == "SLTID_22_5_2020_13_50"){
						console.log("a");
					}
					for(gap=1; gap<numberOfAdjacentSlots && (row+gap)<totalRows+1; gap++){
						var belowCell =$(ancestorId + "table#citas_table>tbody>tr:nth-child(" + (row+gap) + ") > td:nth-child(" + col + ")");
						if (belowCell!= undefined){
							belowCell.text(" ") //mark this cell for deletion
						}
						else {
							console.log("found an orphan cell > mark not bookable");
							cell.prop("className", "ocupado");
						}
					}
					cell.prop("checked", true); //mark this cell to be expanded
				} 
			}
		}
	}
	//remove and expand designed cells
	$(ancestorId + "table#citas_table>tbody>tr>td").each(function (index){
													if ($(this).text()==" "){
														$(this).remove();
													}
													if ($(this).prop("checked")==true){
														$(this).prop("rowspan", numberOfAdjacentSlots)
														$(this).prop("checked", false); //remove flag
													}
													//set as non available orphan slots
													else if (($(this).prop("className")=="libre")
													|| ($(this).prop("className")=="libre today")){
														$(this).text(ocupado);
														$(this).prop("className", "ocupado");
													}
											});
}

//Counts the number of slots in the table painted as available
function countNumberOfAvailableSlotsPainted(){
	
	var ancestorId=""; //select the table in the edit dialog or the table in the create dialog
	if($("#aa14a_editAppointment_dialog") && $("#aa14a_editAppointment_dialog").dialog("isOpen")==true){
		ancestorId = "#aa14a_editAppointment_form ";
	}
	return $(ancestorId + "table#citas_table>tbody>tr>td [class^='libre']").size();
}

// creates the slot id
function slotId(year,monthOfYear,dayOfMonth,
				hourOfDay,minuteOfHour) {
	return "SLTID" + "_" + dayOfMonth + '_' + monthOfYear + '_' + year + '_' + hourOfDay + '_' + minuteOfHour;	
}


