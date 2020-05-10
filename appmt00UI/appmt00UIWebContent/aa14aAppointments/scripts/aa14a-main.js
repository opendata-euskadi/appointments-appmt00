// avoid an error in IE9 when using console.log
if (typeof console == "undefined") {
    window.console = {
        log: function () {}
    };
}

//see /medicalservice/aa14a-print
var municipalityText = $.Deferred();

$(document).ready(
function($) {
		
	$.ajaxSetup({ cache: false });		// prevent ajax calls caching!
	
	
	$body = $("body");
//	idioma=$("html").attr('lang');		// var idioma is defined at aa14aVariablesIdiomaticas.inc
	
	// Inicializar calendarios (opciones comunes para TODOS los calendarios)
	$.datepicker.setDefaults($.datepicker.regional[idioma]);
	var pickerOpts = {
						showOn: "button",				
						buttonImage: "/appcont/aa14aAppointments/images/aa14a-calendar.png",
						minDate: 0
					 };  
	$.datepicker.setDefaults(pickerOpts);
	
	//locale for moment.js
	moment.locale(idioma);
	
	// Bootstrap the UI
	_bootstrapUI();
	    
});
function _bootstrapUI() {
	// Pop Ups ===========================================================================
	// init the appointments print dialog
	if ($("#aa14a_print_appointments_dialog").length) {
		initPrintAppointmentsButton();
		initPrintAppointmentsDialog();
	}
	
	// init the schedule change radios
	// repaint the calendar with the new booking config for the schedule
	$("input:radio[name='schId']").change(
		function() {
			
			// get the selected location id
			var schId = getSelectedScheduleId();	// see aa14a-calendar.js
			
			// update the booking config and repaint the calendar entirely
			_loadBookingConfigForSchedule(schId, 
										  function() {
														$('#calendar').fullCalendar('destroy');
														paintCalendar();
														_removeCalendarEventsSource();
														_setCalendarEventsSource(schId);
														if ($("#aa14a_reserveSlot_dialog").length) {
															refreshTimepickers();
														}
													  });	
		});	
	
	// paint the calendar and refresh it
	if ($("#calendar").length) {
		// get the selected location id
		var schId = getSelectedScheduleId();	// see aa14a-calendar.js
		if (schId == undefined){
			$("input:radio[name='schId']:first").prop("checked", true);
			schId = getSelectedScheduleId();
		}
		_loadBookingConfigForSchedule(schId,
									  function() {
											// init the edit non bookable slot dialog
											if ($("#aa14a_editAppointment_dialog").length) {
												initEditAppointmentDialog();
											}
											// init the reserve slot dialog
											if ($("#aa14a_reserveSlot_dialog").length) {
												initReserveSlotDialog();
											}
											// init the release slot button (it's shared between edit appointment & reserve slot dialogs
											if ($("#aa14a_editAppointment_dialog").length
											 || $("#aa14a_reserveSlot_dialog").length) {
												intReleaseBookedSlotButton();
											}
			
											// paint the calendar
											paintCalendar();
											_removeCalendarEventsSource();
											_setCalendarEventsSource(schId);
									  });		
	}
	
	// appointment create form
	if ($("#aa14a_new_appointment_form").length) {
		initAppointmentForm();
	}
	
	// appointment search form
	if ($("#aa14_appointment_search").length) {
		initAppointmentSearch();
		
		// init the edit non bookable slot dialog
		initEditAppointmentDialog();
		// init the release slot button
		intReleaseBookedSlotButton();
	}


	
// OTROS ==============================================================================
	if (window.appointmentId) {
		paintAppointmentDetails(window.appointmentId);
	}
	if (window.nombreCapa && nombreCapa == "aa14a_capa_imprimir") {
		$("#aa14a_capa_imprimir").show();
	}
	if ($("#aa14a_print_btn").length) {
		$("#aa14a_print_btn").click(
			function() {
				window.print();
				return false;
			});
	}

}

///////////////////////////////////////////////////////////////////////////////////////////////////
//	SERVER ERROR DIALOG
///////////////////////////////////////////////////////////////////////////////////////////////////
function showServerErrorDialog(text,doAfterClose) {
	if ($("#aa14_server_error_dialog")) {
		$("#aa14_server_error_dialog > p > span").after(text);
		$("#aa14_server_error_dialog").dialog(
												{
													autoOpen	: false,
												    modal		: true,
												    title		: 'Server ERROR',
												    buttons		: {
															    	Ok: function() {
																    		// just close the dialog
															    			$(this).dialog("close");
															    			if (doAfterClose) doAfterClose();
															        	}
												    				}
											});
	}
}
///////////////////////////////////////////////////////////////////////////////////////////////////
//	SELECTED LOCATION
///////////////////////////////////////////////////////////////////////////////////////////////////
// Gets the service location from the form-selected option (see aa14aSolicitudLocation)
function getSelectedServiceLocationId() {
	var outLocId;
	if ($("input:radio[name='serviceLocId']").length) {
		outLocId = $("input:radio[name='serviceLocId']:checked").val()
	} else {
		console.log("WARNING!!! could NOT get the service location id!!");
	}
	return outLocId;
}
///////////////////////////////////////////////////////////////////////////////////////////////////
//	BOOKING CONFIG
///////////////////////////////////////////////////////////////////////////////////////////////////
// used at 
//		aa14a-appointmentDateTimeSelectionCalendar.js
//		aa14a-calendar.js
//		aa14a-nonBookableEditDialog.js
var bookingConfig = {
						calMinHour : 8,
						calMinMin  : 30,
						calMinTime : this.calMinHour + ':' + this.calMinMin,
						
						calMaxHour : 14,
						calMaxMin  : 00,
						calMaxTime : this.calMaxHour + ':' + this.calMaxMin,
						
						calSlotLength : 30		
						
					};
//Number of days to fetch forward for the caption
var numberOfNextDays= 5;

// updates the global var bookingConfig
function _updateBookingConfigWith(bookingCfg) {
	console.log(bookingCfg);
	bookingConfig.calMinHour = String("00" + bookingCfg._minBookableHour._hourOfDay).slice(-2);
	bookingConfig.calMinMin = String("00" + bookingCfg._minBookableHour._minuteOfHour).slice(-2);
	bookingConfig.calMinTime = bookingConfig.calMinHour + ":" + bookingConfig.calMinMin;
	
	bookingConfig.calMaxHour = bookingCfg._maxBookableHour._hourOfDay;
	bookingConfig.calMaxMin = bookingCfg._maxBookableHour._minuteOfHour;
	bookingConfig.calMaxTime = bookingConfig.calMaxHour + ':' + bookingConfig.calMaxMin;
	
	bookingConfig.calSlotLength = bookingCfg._slotDefaultLengthMinutes;	
}
// called from the appointment create form
function _loadBookingConfigForLocation(locId,
									   action) {
	console.log("...getting booking config for loc id=" + locId);
	$.ajax({
		  url		: '/' + appmt01UIWar + '/AA14ModelObjectsLoadServlet?R01HNoPortal=true',
		  data		: "op=OBTENERCONFIGCALENDARBYLOCATIONID&locId=" + locId,
		  type		: "get",
		  success	: function(bookingCfg) {
			  				_updateBookingConfigWith(bookingCfg);
			  				if (action) action();
					  },
		  error		: function (xhr, ajaxOptions, thrownError) {
			    			console.log("error");
			  		  }
	});	
}
// called from the main page (calendar)
function _loadBookingConfigForSchedule(schId,
									   action) {
	console.log("[Booking Config]: getting booking config for schedule id=" + schId + " (used to paint the calendar and non-bookable slot selection)");
	$.ajax({
		  url		: '/' + appmt01UIWar + '/AA14ModelObjectsLoadServlet?R01HNoPortal=true',
		  data		: "op=OBTENERCONFIGCALENDARBYSCHEDULEID&schId=" + schId,
		  type		: "get",
		  success	: function(bookingCfg) {
							console.log(bookingCfg);
			  				_updateBookingConfigWith(bookingCfg);
			  				if (action) {
			  					action();
			  				}
					  },
		  error		: function (xhr, ajaxOptions, thrownError) {
			    			console.log("error");
			  		  }
	});	
}
///////////////////////////////////////////////////////////////////////////////////////////////////
//	PINTAR DETALLES DE CITA
///////////////////////////////////////////////////////////////////////////////////////////////////
function paintAppointmentDetails(slotOid) {
$.ajax({
	  url		: '/' + appmt01UIWar + '/AA14ModelObjectsLoadServlet?R01HNoPortal=true',
	  data		: "op=OBTENERCITABYOID&appointmentOid=" + slotOid,
	  type		: "get",
	  success	: function(appointment) {
			  		
		  				// establecer los datos de la localización
			  			pintarDatosLocalizacionByOid(appointment._orgDivisionServiceLocationOid._id);
						
			  			// establecer los datos de la cita
			  			//TODO poner la fecha en el JSON en formato ISO para no tener que hacer esta conversion
			  			var createDateEn = moment();
			  			createDateEn.locale("en"); 
			  			createDateEn = moment(appointment._trackingInfo._createDate.replace(',', '')); //sample: Apr 5, 2019 1:50:17 PM
			  			$("#appointmentRequestedAt").html(moment(createDateEn).format("LLLL"));
			  			$("#aa14a_requestedDate").show();
			  			
						$("#citaSelec").html(devolverFechaFormateada(appointment));
			
						// requestor
						$("#datonif").html(appointment._person._id._id);
						$("#datonombre").html(appointment._person._name);
						$("#datoapellidos").html(appointment._person._surname1);
						
						// contact
						if (appointment._contactInfo) {
							if (appointment._contactInfo._mailAddresses 
							 && appointment._contactInfo._mailAddresses[0]._mail) {
								$("#datoemail").html(appointment._contactInfo._mailAddresses[0]._mail._id);
							}
							else{
								$("#datoemail").html("");
							}
							if (appointment._contactInfo._phones
							 && appointment._contactInfo._phones[0]._number) {
								$("#datomovil").html(appointment._contactInfo._phones[0]._number._id);
							}
							else{
								$("#datomovil").html("");
							}
						}
						else{
							$("#datoemail").html("");
							$("#datomovil").html("");
						}
						
						if (appointment._privateDetails) {
							$("#datoprivateDetails").html(appointment._privateDetails);
						}
						else{
							$("#datoprivateDetails").html("");
						}
						
						$("#numberOfAdjacentSlots").val(convertEnumToInt(appointment._numberOfAdjacentSlots));
						$("#localizador").html(appointment._id._id);
						
						
						//customer info edit form
						$("div.aa14a_formDataSection > p > input#nif").val(appointment._person._id._id);
						$("#nombre").val(appointment._person._name);
						$("#apellidos").val(appointment._person._surname1);

						if (appointment._contactInfo) {
							if (appointment._contactInfo._contactMails 
							 && appointment._contactInfo._contactMails[0]._mail) {
								//Pick the html from #datoemail imput to resolve the &#64; entity
								$("#email").val($("#datoemail").html());
							}
							else{
								$("#email").val("");
							}
							if (appointment._contactInfo._contactPhones
								&& appointment._contactInfo._contactPhones[0]._number) {
								$("#movil").val(appointment._contactInfo._contactPhones[0]._number._id);
							}
							else{
								$("#movil").val("");
							}
						}
						else{
							$("#movil").val("");
							$("#email").val("");
						}
						// appointment (see /trafikoa/aa14a-appointmentDetails.js or /bizilagun/aa14a-appointmentDetails.js)
						paintCustomAppointmentDetails(appointment);
	  			  },
	  error		: function (xhr, ajaxOptions, thrownError) {
		  				console.log("error");
	  			  }
});
}


///////////////////////////////////////////////////////////////////////////////////////////////////
//	LOCALIZACION
///////////////////////////////////////////////////////////////////////////////////////////////////
function pintarDatosLocalizacionById(locId) {
	var lang = (idioma == "eu" ? "BASQUE" : "SPANISH");
	$.ajax({
		  url		: '/' + appmt01UIWar + '/AA14ModelObjectsLoadServlet?R01HNoPortal=true',
		  data		: "op=OBTENERJERARQUIALOCALIZACIONBYID&locId=" + locId + "&lang=" + lang,
		  type		: "get",
		  success	: function(responseText) {
			  				setDatosTablaDetallesLocalizacion(responseText);
					  },
		  error		: function (xhr, ajaxOptions, thrownError) {
			    			console.log("error");
			  		  }
	});	
}
function pintarDatosLocalizacionByOid(locOid) {
	var lang = (idioma == "eu" ? "BASQUE" : "SPANISH");
	$.ajax({
		  url		: '/' + appmt01UIWar + '/AA14ModelObjectsLoadServlet?R01HNoPortal=true',
		  data		: "op=OBTENERJERARQUIALOCALIZACIONBYOID&locOid=" + locOid + "&lang=" + lang,
		  type		: "post",
		  success	: function(responseText) {
			  				setDatosTablaDetallesLocalizacion(responseText);
					  },
		  error		: function (xhr, ajaxOptions, thrownError) {
			    			console.log("error");
			  		  }
	});
}
function setDatosTablaDetallesLocalizacion(loc) {
	var lang = (idioma == "eu" ? "BASQUE" : "SPANISH");
	$("td[aa14val='aa14a_division']").each(function() {
											  $(this).html(loc._division._name);
										   });
	$("td[aa14val='aa14a_service']").each(function() {
					  						  $(this).html(loc._service._name);
										  });
	$("td[aa14val='aa14a_service_proc']").each(function() {
			  						  				$(this).html(loc._service._procedure);
									  	       });
	$("td[aa14val='aa14a_location']").each(function() {
						  						$(this).html(loc._location._county);
										   });
	$("td[aa14val='aa14a_loc_municipality']").each(function() {
					  									$(this).html(loc._location._municipality);
												   });
	
	$("td[aa14val='aa14a_loc_street']").each(function() {
					  							$(this).html(loc._location._street);
											 });
	$("td[aa14val='aa14a_loc_phone']").each(function() {
					  							$(this).html(loc._location._phone);
										    });
	//see medicalservice/aa14a-print.js
	municipalityText.resolve(loc._location._municipality);
	
	//see justizia/common/inc/aa14aLocationDetails.inc
	$("td[aa14val='aa14a_justizia_service']").each(function() {
		$(this).html(loc._location._name);
	});
	$("td[aa14val='aa14a_justizia_loc_judicial_party']").each(function() {
			$(this).html(loc._location._municipality);
	});
	$("td[aa14val='aa14a_justizia_service']").each(function() {
		$(this).html(loc._location._name);
});
	
}


//Returns the integer value for a Aa14NumberOfAdjacentSlots enum value
function convertEnumToInt(enumValue){
	
	if (enumValue=="ONE"){
		return 1;
	}
	else if (enumValue=="TWO"){
		return 2;
	}
	else if (enumValue=="THREE"){
		return 3;
	}
	else if (enumValue=="FOUR"){
		return 4;
	}
	else if (enumValue=="FIVE"){
		return 5;
	}
	else if (enumValue=="SIX"){
		return 6;
	}
	else if (enumValue=="SEVEN"){
		return 7;
	}
	else if (enumValue=="EIGHT"){
		return 8;
	}
	else if (enumValue=="NINE"){
		return 9;
	}
	else if (enumValue=="TEN"){
		return 10;
	}
	else {
		return 1; //default
	}
	
}