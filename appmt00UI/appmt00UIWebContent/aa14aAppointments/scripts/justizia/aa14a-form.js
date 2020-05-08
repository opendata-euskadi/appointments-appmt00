$(document).ready(function(){
	// Step 1
	$("div[id^= 'loc_']").hide();
	$("input:radio[name='locTh']")
		.change(function() {
						var outLocTh;
						if ($("input:radio[name='locTh']").length) {
							outLocTh = $("input:radio[name='locTh']:checked").val()
						}
						// get the selected location group
						$("div[id^= 'loc_']").hide();
						$("div[id='loc_" + outLocTh + "']").show();
				  });
	
	// Step 2
	$("#aa14a_createAppointment_form #nifTxt").hide();
	$("#aa14a_createAppointment_form #locator").hide();
	$("#aa14a_createAppointment_form #locatorTxt").prop("readonly", true);
	$("#aa14a_createAppointment_form #locatorTxt").prop("disabled", true);
	$("#aa14a_createAppointment_form #nif")
		.change(function() {
					// When the nif changes reset the locator
					$("#aa14a_step2_next_btn").show();
				});
	$("#aa14a_createAppointment_form select[name='justizia.perfil']")
		.change(function() {
					//see /justizia/common/inc/aa14aVariablesIdiomaticas.inc
					var codeValue = $("#aa14a_createAppointment_form select[name='justizia.perfil']").val();
					$("#datoperfil").html(getPerfilTextFromCodeValue(codeValue));
				});
	
	// Change contact info
	$("#aa14a_editAppointment_form #nif").hide();
	$("#aa14a_editAppointment_form #nifTxt").val($("datonif").html());
	$("#aa14a_editAppointment_form #nifTxt").prop("disabled", true);
	$("#aa14a_editAppointment_form #nifTxt").prop("readonly", true);
	$("#aa14a_editAppointment_form #locator").hide();

});

function validateCustomerData() {
	var msgErrores="";
	// nif: data
	if ( $("#aa14a_editAppointment_dialog #nif").val() == ""		// when called from the edit form
	  || $("#aa14a_createAppointment_form #nif").val() == "" ) {	// when called from the create form
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+nifMsgError+"</p>";
	}
	// nif: server side format
	if ($("#aa14a_createAppointment_form #nif").val() 
	 && !validateNIF($("#aa14a_createAppointment_form #nif").val())) {
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+nifMsgError+"</p>";
	}
	// name
	if ( $("#nombre").val() == "" ) {
		   msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+nombreMsgError+"</p>";
	}
	if ( $("#apellidos").val() == "" ) {
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+apellidosMsgError+"</p>";
	}	
	// profile
	if ( $("#perfil").val() == undefined || $("#perfil").val() == "" ) {
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+perfilMsgError+"</p>";
	}
	// contact data
	if ( $.trim($("#movil").val()) != "") {	
		if( !validarMovil("#movil")) {
			msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+movilMsgError+"</p>";
		}
	} else { 
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+movilEmptyMsgError+"</p>";
	}
	
	if ($.trim($("#email").val()) != "") {
		if( !validarEmail("#email")) {
			msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+emailMsgError+"</p>";
		}
	} else {
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+emailEmptyMsgError+"</p>";
	}
	return msgErrores;
}

/**
 * Person locator...
 * @param nif
 * @returns
 */
function generateLocator() {
	// generate the locator
	var nif = $("#aa14a_createAppointment_form #nif").val();
	var email = $("#aa14a_createAppointment_form #email").val();
	
	console.log("[Locator]: getting locator for nif=" + nif + " email=" + email);
	var success = false;
	$.ajax({
		  url		: '/aa14aUIWar/AA14ControllerServlet?R01HNoPortal=true',
		  data		: "op=GENERATE_PERSON_LOCATOR&personId=" + nif + "&email=" + email,
		  type		: "get",
		  async		: false,	// IMPORTANT!! the success info is needed SYNCHRONOUSLY!
		  success	: function(validationResult) {
							if (validationResult.ok) {
								// create appointment
								$("#aa14a_createAppointment_form #locator").val(validationResult["person-locator"]);
								
								// edit appointment
								$("#aa14a_editAppointment_form #locator").val(validationResult["person-locator"]);
								$("#datolocator").html(validationResult["person-locator"]);
								
								success = true;
							}
							else {
								var dialog = $("#aa14_locator_response_dialog").dialog();
				    			$("#aa14_locator_response_dialog #locatorMessage").html(validationResult.message);
				    			dialog.dialog("open");
				    			
				    			success = false;
							}
					  },
		  error		: function (xhr, ajaxOptions, thrownError) {
							var dialog = $("#aa14_locator_response_dialog").dialog();
			    			$("#aa14_locator_response_dialog #locatorMessage").html(thrownError);
			    			dialog.dialog("open");
			    			
			    			success = false;
			  		  }
	});	
	if (!success) {
		var dialog = $("#aa14_locator_response_dialog").dialog();
		$("#aa14_locator_response_dialog #locatorMessage").html("Error generating the locator!");
		dialog.dialog("open");
		
		return;
	}
	return success;
}


// Return the number of slots needed for a single appointment
function getNumberOfAdjacentSlots(){
	return 1;
}

function getPerfilTextFromCodeValue(codeValue) {
	if (codeValue == 1) {
		return PERFIL_ABOGADO;
	}
	else if (codeValue == 2) {
		return PERFIL_PROCURADOR;
	}
	else if (codeValue == 3) {
		return PERFIL_CIUDADANIA;
	}
	else if (codeValue == 4) {
		return PERFIL_GRADUADO;
	}
	else if (codeValue == 5) {
		return PERFIL_OTRO;
	}
	else return "";
}
/**
 * Validates the maximum number of appointments for a user
 * @returns
 */
function validateMaxNumberOfAppointments() {
	var selectedDate = getAvailableSlotsCalendarSelectedSlotAsDate();	// selected slot
	var selectedDateMoment = moment(selectedDate);
	
	var locId = getSelectedServiceLocationId();
	var personId = $("#aa14a_createAppointment_form #nif").val();
	var year = selectedDateMoment.year();
	var weekOfYear = selectedDateMoment.week();
	
	var success = false;
	console.log("> check number of appointments of personId=" + personId + " at locId=" + locId + " " +
				  "at year=" + year + " week=" + weekOfYear);
	$.ajax({
		  url		: '/aa14aUIWar/AA14ControllerServlet?R01HNoPortal=true',
		  data		: "op=VALIDATE_MAX_WEEK_PERSON_APPOINTMENTS&locId=" + locId + 
		  													  "&personId=" + personId + 
		  													  "&year=" + year + "&weekOfYear=" + weekOfYear + 
		  													  "&max=1",
		  type		: "get",
		  async		: false,	// IMPORTANT!! the success info is needed SYNCHRONOUSLY!
		  success	: function(validationResult) {
			  				success = validationResult;
					  },
		  error		: function (xhr, ajaxOptions, thrownError) {
			  				success = false;
			  		  }
	});	
	if (!success) {
		var msgErrores ="<p class='aa14a_warning'><span class='fa fa-warning'></span>" + maxNumberOfApointmetsExceededMsg + ".</p>";
	    $("#aa14a_errores_citas").html(msgErrores);
		$("#aa14a_errores_citas").show();
	}
	return success;
}