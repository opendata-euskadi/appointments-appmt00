///////////////////////////////////////////////////////////////////////////////////////////////////
//	EDIT APPOINTMENT DIALOG
///////////////////////////////////////////////////////////////////////////////////////////////////
// Inits the edit appointment dialog
function initEditAppointmentDialog() {

	// Selector de fechas para la seleccion de hueco
	$("#date").datepicker();
	
	// when changing the date selector, refresh the slots table
    $("#date").on("change",
			      function () {
    					// get data from the dialog where the date selector resides
    					// (were set when opening the dialog see aa14a-appointmentSearch.js)
    					var dialog = $("#aa14a_editAppointment_dialog").dialog();
    					
    					var slotOid = dialog.data('slotOid');
    					var locId = dialog.data('locId');
    					var schId = dialog.data('schId');
    					var numberOfAdjacentSlots = $("#numberOfAdjacentSlots").val();	
				    	var fechaSeleccionada = $("#date").datepicker( "getDate" );
				    	
				    	obtenerCitasLibres(fechaSeleccionada,
				    					   false,		// do NOT slip range to find first available slot
				    					   numberOfAdjacentSlots,
				    					   locId,
				    					   schId);
				  });
	
	var dlgWith = $(window).width() - 140;
	var dlgHeigh = $(window).height() - 90;
	
    $("#aa14a_editAppointment_dialog").dialog({
										    	autoOpen	: false,
										    	modal		: true,
										    	width		: dlgWith,		// auto
										    	position	: {my: 'top', at: 'top+50', of: window},
										    	close		: function(event,ui) {
										    						restoreAppointmentEditDialogUIState();
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
    
	
    // Change the customer details
	$("#aa14a_change_customer_details_btn").click(		
			function() {
				if (validarDatosPersonales()) { 
					var dialog = $("#aa14a_editAppointment_dialog").dialog();
					var slotOid = dialog.data('slotOid');
					var locId = dialog.data('locationId');
					var schId = dialog.data('schId');
					
					var newSelectedSlotId = getAvailableSlotsCalendarSelectedSlotId();
					updateAppointmentCustomerData({
												oid			: slotOid,
												success		: function(appointment) {
																	
																	$("#aa14a_editAppointment_dialog").dialog("close");
																	
																	// refresh the calendar / search results if present
														  			if ($("#calendar").length) {
														  				refreshCalendar(schId);
																	} else if ($("#aa14a_search_results").length) {
																		buscarCitas();
																	}
																	// show again dialog buttons & hide the calendar and next button
																	restoreAppointmentEditDialogUIState();
															  }
											   });
				}
			});
	
    // Change the appointment date
	$("#aa14a_change_booked_slot_date_time_btn").click(		
			function() {
				if (validarCitaSeleccionada()) { 
					var dialog = $("#aa14a_editAppointment_dialog").dialog();
					var slotOid = dialog.data('slotOid');
					var locId = dialog.data('locationId');
					var schId = dialog.data('schId');
					
					var newSelectedSlotId = getAvailableSlotsCalendarSelectedSlotId();
					updateAppointmentDateTime({
												oid			: slotOid,
												newSlotId	: newSelectedSlotId,
												success		: function() {
																	$("#aa14a_editAppointment_dialog").dialog("close");
																	// refresh the calendar / search results if present
														  			if ($("#calendar").length) {
														  				refreshCalendar(schId);
																	} else if ($("#aa14a_search_results").length) {
																		buscarCitas();
																	}
																	// show again dialog buttons & hide the calendar and next button
																	restoreAppointmentEditDialogUIState();
															  }
											   });
				}
			});
	// Print an appointment button
	$("#aa14_booked_slot_print_btn").click(
				function() {
					var lang = idioma;  	// see aa14aJSDynamicVars.inc
					var slotOid = $("#aa14a_editAppointment_dialog").data('slotOid');	// receive the date handed to the dialog with > dialog.data('slotOid',theOid);
					var url = 'aa14aConsulta.jsp?lang=' + lang + 
											   '&appointmentId='+ slotOid + 
											   '&nombreCapa=aa14a_capa_imprimir';
					//window.location.replace(url);	//  = HTTP redirect
					window.location.href = url;		// = clicking on a link
				});
}
// restores the edit pop-up ui state
function restoreAppointmentEditDialogUIState() {
	// show again dialog buttons & hide the calendar and next button
	$("#aa14_change_customer_details_btn").show();
	$("#aa14_fecha_hora_cita_change_btn").show();
	$("#aa14_booked_slot_print_btn").show();
	$("#aa14_booked_slot_release_btn").show();
	$("#aa14a_editAppointment_form > div.aa14a_bloque").hide();
	$("#aa14a_editAppointment_form > div.aa14a_customer_details").hide();
	$("#aa14a_change_customer_details_btn").show();
	$("#aa14a_change_booked_slot_date_time_btn").hide();
	$("#aa14a_change_customer_details_btn").hide();
}
// actualizar la fecha de una cita
function updateAppointmentDateTime(appointmentData) {
	$("#aa14a_editAppointment_form #op").val("ACTUALIZAR_FECHA_HORA_CITA");
	$("#slotOid").val(appointmentData.oid);
	$("#appointmentDateTime").val(appointmentData.newSlotId);
	
	console.log("...update the date/time for slot " + appointmentData.oid + " to " + getAvailableSlotsCalendarSelectedSlotDate() + ": " + $("#aa14a_editAppointment_form").serialize());
	$.ajax({
		  url		: '/aa14aUIWar/AA14ControllerServlet?R01HNoPortal=true',
		  data		: $("#aa14a_editAppointment_form").serialize(),
		  type		: "post",
		  success	: function(responseText) {
			 				if (appointmentData.success == "undefined") return;
			 				console.log("[OK] oid=" + responseText._oid._id);
			 				
			 				// restore the ui
		    	  			$("#aa14a_change_booked_slot_date_time_btn").show();
		    	  			$("#aa14a_errores_citas").html("");
							$("#aa14a_errores_citas").hide();
							
							// call the success function
		    	  			appointmentData.success();	
		    	  			
					   },
		  error		: function (xhr,ajaxOptions,thrownError) {
			  			  console.log("ERROR> " + xhr.responseText);
						  var msgErrores = "";
						  if (xhr.responseText.indexOf("ERROR_CODE=105") !== -1) {							 
							  msgErrores ="<p class='aa14a_warning'><span class='fa fa-warning'></span>" + citaMsgDuplicada + "</p>";
						      $("#aa14a_errores_citas").html(msgErrores);
							  $("#aa14a_errores_citas").show();
							  
							  // Get the available slots again
							  var dialog = $("#aa14a_editAppointment_dialog").dialog();
	    					  var locId = dialog.data('locId');
	    					  var schId = dialog.data('schId');
	    					
					    	  var fechaSeleccionada = $("#date").datepicker( "getDate" );
	
					    	  var numberOfAdjacentSlots = $("#numberOfAdjacentSlots").val();	
						      obtenerCitasLibres(fechaSeleccionada,
						    		  			 false,		// do NOT slip range to find first available slot
						    		  			 numberOfAdjacentSlots,
						    					 locId,schId);
						    	
							  // show the save button 
							  $("#aa14a_change_booked_slot_date_time_btn").show();
						  }  else {
							  showServerErrorDialog(msgErrorGenerico,
									  				function() {
								  						gotoFormStep('#aa14a_step4');
							  						});
						  }
					  },
		  beforeSend: function() {
			  			  $("#aa14a_change_booked_slot_date_time_btn").hide();
						  $body.addClass("loading"); 
					  },
	      complete	: function() {
	    	  			  $body.removeClass("loading");		
		    	  	  }
	});
}

//actualizar los datos personales
function updateAppointmentCustomerData(appointmentData) {
	$("#aa14a_editAppointment_form #op").val("ACTUALIZAR_DATOS_PERSONALES_CITA");
	$("#slotOid").val(appointmentData.oid);
	
	console.log("...update the customer data for slot " + appointmentData.oid + ": " + $("#aa14a_editAppointment_form").serialize());
	$.ajax({
		  url		: '/aa14aUIWar/AA14ControllerServlet?R01HNoPortal=true',
		  data		: $("#aa14a_editAppointment_form").serialize(),
		  type		: "post",
		  success	: function(responseText) {
				  			console.log("[OK] oid=" + responseText._oid._id);
			 				if (appointmentData.success == "undefined") return;
			 				
			 				// restore the ui
		    	  			$("#aa14a_change_customer_details_btn").show();
		    	  			$("#aa14a_errores_datospersonales").html("");
							$("#aa14a_errores_datospersonales").hide();
							
							// call the success function
		    	  			appointmentData.success(responseText);	
					   },
		  error		: function (xhr,ajaxOptions,thrownError) {
			  			  console.log("ERROR> " + xhr.responseText);
						  var msgErrores = "";
						  if (xhr.responseText.indexOf("ERROR_CODE=105") !== -1) {							 
							  msgErrores ="<p class='aa14a_warning'><span class='fa fa-warning'></span>" + citaMsgDuplicada + "</p>";
						      $("#aa14a_errores_datospersonales").html(msgErrores);
							  $("#aa14a_errores_datospersonales").show();
							  
							  // Get the available slots again
							  var dialog = $("#aa14a_editAppointment_dialog").dialog();
	    					  var locId = dialog.data('locId');
	    					  var schId = dialog.data('schId');
	    					  var numberOfAdjacentSlots = $("#numberOfAdjacentSlots").val();	
	    					
					    	  var fechaSeleccionada = $("#date").datepicker( "getDate" );
					    	 
						      obtenerCitasLibres(fechaSeleccionada,
						    		  			 false,		// do NOT slip range to find first available slot
						    		  			 numberOfAdjacentSlots,
						    					 locId,schId);
							  
							  // show the save button 
							  $("#aa14a_change_customer_details_btn").show();
						  }  else {
							  showServerErrorDialog(msgErrorGenerico,
									  				function() {
								  						gotoFormStep('#aa14a_step4');
							  						});
						  }
					  },
		  beforeSend: function() {
			  			  $("#aa14a_change_booked_slot_date_time_btn").hide();
						  $body.addClass("loading"); 
					  },
	      complete	: function() {
	    	  			  $body.removeClass("loading");		
		    	  	  }
	});
}
// opens the edit appointment dialog
function openEditAppointmentDialog(slotOid,
								   locId,schId,
								   title,
								   appointmentStartDate) {
		var dialog = $("#aa14a_editAppointment_dialog");
		
		dialog.dialog("option","title",title);
		
		// hand a parameter to the dialog
		dialog.data('slotOid',slotOid);	
		dialog.data('locId',locId);
		dialog.data('schId',schId);
		
		// load the appointment data
		paintAppointmentDetails(slotOid);
		
		dialog.data('schId',schId);
		
		// When clicking the [change] link show the hole selector
		$("#aa14_fecha_hora_cita_change_btn").click(function(evt) {
														showSlotDateTimeSelectorTableInEditDialog(slotOid,
																								  appointmentStartDate);
													});
		// When clicking the [change] link show the hole selector
		$("#aa14_change_customer_details_btn").click(function(evt) {
														showCustomerDetailInEditDialog(slotOid);
													 });
		
		dialog.dialog("open");
		console.log("Opening the edit form for event with id=" + slotOid);
}
// Show the slot selector table
function showSlotDateTimeSelectorTableInEditDialog(slotOid,
			    						  		   date) {
	// hide non necessary buttons
	$("#aa14_change_customer_details_btn").hide();
	$("#aa14_fecha_hora_cita_change_btn").hide();
	$("#aa14_booked_slot_print_btn").hide();
	$("#aa14_booked_slot_release_btn").hide();
	$("#aa14a_editAppointment_form > div.aa14a_fecha_hora_cita").show();
	// show the calendar and the next button
	$("#date").datepicker('setDate',date);
	$("#date").trigger("change");	// force onchange event
	$("#aa14a_change_booked_slot_date_time_btn").show();
}

//Show the slot selector table
function showCustomerDetailInEditDialog(slotOid) {
	// hide non necessary buttons
	$("#aa14_change_customer_details_btn").hide();
	$("#aa14_fecha_hora_cita_change_btn").hide();
	$("#aa14_booked_slot_print_btn").hide();
	$("#aa14_booked_slot_release_btn").hide();
	$("#aa14_change_customer_details_btn").hide();
	
	// show the data and the next button
	$("#aa14a_editAppointment_form > div#aa14a_customer_details").show();
	$("#aa14a_change_customer_details_btn").show();
	
}
