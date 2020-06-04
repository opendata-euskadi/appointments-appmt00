///////////////////////////////////////////////////////////////////////////////////////////////////
//	FORMULARIO CREACION DE CITA
///////////////////////////////////////////////////////////////////////////////////////////////////
function initAppointmentForm() {
// Selector de fechas para la seleccion de hueco	
// Al cambiar el calendario de huecos, actualizar la tabla
	$("#date").datepicker();
    $("#date").on("change",
			      function () {
				    	var fechaSeleccionada = $("#date").datepicker( "getDate" );
				    	var locId = getSelectedServiceLocationId();
				    	var numberOfAdjacentSlots = getNumberOfAdjacentSlots();
				    	var schId = forAScheduleId(); // see aa14a-form.js
				    	if (schId != "") {
				    		obtenerCitasLibres(fechaSeleccionada,
				    					       false,		// DO NOT slip date range to find first available slot
				    					       numberOfAdjacentSlots,
				    					       null,
				    					       schId);
				    	}
				    	else {
				    		obtenerCitasLibres(fechaSeleccionada,
			    					   		   false,		// DO NOT slip date range to find first available slot
			    					   		   numberOfAdjacentSlots,
			    					   		   locId);
				    	}
				  });
	
	
// PASO 1: Service location
	// Cambiar datos dependiendo del servicio seleccionado
	$("input:radio[name='serviceLocId']").click(
		function() {
			// get the selected location id
			var locationId = getSelectedServiceLocationId();
			// location may be null for [medical service] if the xlnets data 
			// for the user doesn't give a delegation for the basque country
			// Very few users can meet this use case and its improbable 
			// that they will access this service but its controlled anyways
			if (locationId != "null"){
				// formulario de creación de citas (see aa14a-main.js)
		        pintarDatosLocalizacionById(locationId);
		        
		        // update booking config (see aa14a-main.js)
		        _loadBookingConfigForLocation(locationId);
	
				$("#aa14a_errores_servicio").hide();
				if ($("#aa14a_error_delegacion")){
					$("#aa14a_error_delegacion").hide();
				}
		        $("#servicio").show();
				$("#aa14a_step1_next").show();		// show the step1 button
				
				// refresh the appointment table if initialized
				if ($("#date").datepicker("getDate")!=null){
					var today = new Date();
					$("#date").datepicker("setDate",today);
					var locId = getSelectedServiceLocationId();
					var numberOfAdjacentSlots = getNumberOfAdjacentSlots();
					var schId = forAScheduleId(); // see aa14a-form.js
			    	if (schId != "") {
			    		obtenerCitasLibres(today,
			    					   	   true,		// slip date range to find first available slot
			    					   	   numberOfAdjacentSlots,
			    					   	   locId,
			    					   	   schId);
			    	}
			    	else {
			    		obtenerCitasLibres(today,
		    					   		   true,		// slip date range to find first available slot
		    					   		   numberOfAdjacentSlots,
		    					   		   locId);
			    	}
				}
			}
			else {
				var msgError = "";
				if (msgErrorNoDel != undefined && msgErrorNoDel == "NO_COUNTY_FOUND"){
					msgError="<p class='aa14a_warning'><span class='fa fa-warning'></span>" + msgNoCounty + "</p>";
				}
				else if (msgErrorNoDel != undefined && msgErrorNoDel == "COUNTY_NOT_BASQUE_COUNTRY"){
					msgError="<p class='aa14a_warning'><span class='fa fa-warning'></span>" + msgNoBasqueCountry + "</p>";
				}
				$("#aa14a_error_delegacion").html(msgError);
				$("#aa14a_error_delegacion").show();
			}
		});	
	// Botón [siguiente] paso 1 (location select)
	$("#aa14a_step1_next_btn").click(
		function() {
			if ($("#aa14a_error_delegacion")) {
				$("#aa14a_error_delegacion").hide();
			}
			$("#aa14a_errores_servicio").hide();
			
			var isChecked = $("input[name='serviceLocId']:checked").val();							 
			if (!isChecked) {
				// show the error layer
				var msgError="<p class='aa14a_warning'><span class='fa fa-warning'></span>" + servicioMsgError + "</p>";
				$("#aa14a_errores_servicio").html(msgError);
				$("#aa14a_errores_servicio").show();
			} else {
				// goto step 2
				gotoFormStep('#aa14a_step1',	// from
							 '#aa14a_step2');	// to
				// set the focus on the first visible empty input or textarea
				$("#aa14a_createAppointment_form div").find("input:not([value]):first, textarea:empty:first").first().focus();
			}				
		});	
		
// PASO 2: Datos de contacto
	// when the input changes, update the details (see step 4: appointment review)
	// beware that the private details (the editor content) is synced at tinyMCE's onchange
	$("input[type=text],input[type=email],input[type=tel],input[type=email],textarea, input[type=checkbox]")
			.change(function() {
							// see aa14aCustomerDetails.inc
							var selector = '#dato'+ $(this).attr('name');	// this refers to the INPUT and $(this) is the jquery wrapper arround that INPUT
							
							if ($(this).is(":checkbox")) {
								$(selector).html($(this).on?si:no); //Put Yes/No literals as text for checkbox values
							}
							else {
								$(selector).html($(this).val());
							}	
					});
	// validate
	$("#aa14a_step2_next_btn")
			.click(function() {
						if (validarDatosPersonales()) {		// see aa14a-validations.js
							// refresh the calendar
							var today = new Date();
							$("#date").datepicker("setDate",today);
							var locId = getSelectedServiceLocationId();
							var numberOfAdjacentSlots = getNumberOfAdjacentSlots();
							
					    	var schId = forAScheduleId(); //see aa14a-form.js
					    	if (schId != "") {
					    		obtenerCitasLibres(today,
					    					       true,		// slip date range to find first available slot
					    					       numberOfAdjacentSlots,
					    					       null,
					    					       schId);
					    	}
					    	else {
					    		obtenerCitasLibres(today,
				    					           false,		// slip date range to find first available slot
				    					           numberOfAdjacentSlots,
				    					           locId);
					    	}
							
							// goto step 3
							gotoFormStep('#aa14a_step2',	// from
										 '#aa14a_step3');	// to
						};
					});

// PASO 3: Seleccionar slot
	// Validación fecha Ojo! depende del idioma Castellano DDMMAAA Euskera AAAA/MM/DD
	$("#aa14a_step3_next_btn").click(		
		function() {
			if (validarCitaSeleccionada()) { 
				if (typeof validateMaxNumberOfAppointments === 'function') {	// does the validateMaxNumberOfAppointments exists?
					if (!validateMaxNumberOfAppointments()) {
						// return to step 3
						gotoFormStep('#aa14a_step3',
					 			 '#aa14a_step3');
					}
					else {
						// set the selected slot data at aa14aDetallesCita
						$("#citaSelec").html(getAvailableSlotsCalendarSelectedSlotDate());
						
						gotoFormStep('#aa14a_step3',
									 '#aa14a_step4');
					}
				}
				else {
					// set the selected slot data at aa14aDetallesCita
					$("#citaSelec").html(getAvailableSlotsCalendarSelectedSlotDate());
					
					gotoFormStep('#aa14a_step3',
								 '#aa14a_step4');
				}
			}
		});

// PASO 4: Confirmar cita: guardar
    $("#aa14a_step4_next_btn").click(
    	function() {  
    		var noError = true;
	    	if (!validarDatosPersonales())  {
	    		noError = false;
	    		$("html,body").animate({
									scrollTop: $("#aa14a_step2").offset().top	
	    					   },1000);
	    	}
    		if (!validarCitaSeleccionada()) {
    			noError = false;
				$("html,body").animate({
									scrollTop: $("#aa14a_step3").offset().top	
	    					   },1000);
    		}
    		if (!generateLocator()) {
    			noError = false;
    		}
	    	if (noError) {
	    		// everything ok!!
    			saveAppointment();
	    	}
    	});
}
// Guardar cita
function saveAppointment() {
	var slotId = getAvailableSlotsCalendarSelectedSlotId();
	
	$("#aa14a_createAppointment_form #op").val("CONFIRMARCITA");
	$("#appointmentDateTime").val(slotId);
	var numberOfAdjacentSlots = getNumberOfAdjacentSlots();
	if (tinyMCE) {
		// force tinyMCE to persist changes to text area
		tinyMCE.triggerSave();
	}
	console.log("...reserve slot at " + getAvailableSlotsCalendarSelectedSlotDate() + ": " + $("#aa14a_createAppointment_form").serialize());
	$.ajax({
		  url		: '/' + appmt01UIWar + '/AA14ControllerServlet?R01HNoPortal=true&numberOfAdjacentSlots='
			  			+ numberOfAdjacentSlots, //default 1
		  data		: $("#aa14a_createAppointment_form").serialize(),
		  type		: "post",
		  success	: function(responseText) {
			  			 console.log("[OK] oid=" + responseText._oid._id);
			  			 $("form")[0].reset();
						 
						  document.location.href = 'aa14aConsulta.jsp?appointmentId=' + responseText._oid._id + 
						  										    '&lang=' + $("#lang").val() +
						  										    '&nombreCapa=aa14a_capa_imprimir';
				   	  },
		  error		: function (xhr,ajaxOptions,thrownError) {
			  			  console.log("ERROR> " + xhr.responseText);
						  var msgErrores = "";
						  if (xhr.responseText.indexOf("ERROR_CODE=105") !== -1) {							 
							  msgErrores ="<p class='aa14a_warning'><span class='fa fa-warning'></span>" + citaMsgDuplicada + "</p>";
						      $("#aa14a_errores_citas").html(msgErrores);
							  $("#aa14a_errores_citas").show();
							  
							  // Get the available slots again
						      var fechaSeleccionada = $("#date").datepicker( "getDate" );
						      var location = getSelectedServiceLocationId();
						      var schId = forAScheduleId(); // see aa14a-form.js
						      if (schId != ""){
						    		obtenerCitasLibres(fechaSeleccionada,
						    					   false,		// DO NOT slip date range to find first available slot
						    					   numberOfAdjacentSlots,
						    					   null,
						    					   schId);
						      }
						      else {
						    		obtenerCitasLibres(fechaSeleccionada,
					    					   false,		// DO NOT slip date range to find first available slot
					    					   numberOfAdjacentSlots,
					    					   locId);
						     }
						    	
							 // goto step 5 & show the save button 
							 gotoFormStep('#aa14a_step3');
							 $("#aa14a_step4_next_btn").show();
						  }  else {
							  showServerErrorDialog(msgErrorGenerico,
									  				function() {
								  						gotoFormStep('#aa14a_step4');
							  						});
						  }
					  },
		  beforeSend: function() {
			  			  $("#aa14a_step4_next_btn").hide();
						  $body.addClass("loading"); 
					  },
		  complete	: function() {  
		    	  			$body.removeClass("loading");  
		    	  	  }
	});
}
// mostrar paso formulario
function gotoFormStep(stepFrom,stepTo) {
	if (stepTo == null) stepTo = stepFrom;
	if ($(stepTo + " > div").hasClass("aa14a_titulo_paso_gris")) {
		$(stepTo + " > div").removeClass("aa14a_titulo_paso_gris");
		$(stepTo + " > div.aa14a_bloque").show();
	}
	$(stepFrom + "_next").hide();	// hide the [next] button at the section we come from 
	$(stepTo + "_next").show();		// show the [next] button at the section we go to
	// show section we go to
	$("html,body").animate({
								scrollTop: $(stepTo).offset().top	
						    },1000);
};
///////////////////////////////////////////////////////////////////////////////////////////////////
// INPUT VALIDATION
///////////////////////////////////////////////////////////////////////////////////////////////////
function step2Validations() {	
	if (validarDatosPersonales) {
		$("#aa14a_step2_error").hide();
	}
	else{
		$("#aa14a_step2_error").show();
	}
};
