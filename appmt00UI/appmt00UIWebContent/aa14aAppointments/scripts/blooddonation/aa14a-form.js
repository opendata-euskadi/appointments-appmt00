$(document).ready(function(){
	//we have an unique location so we skip step 1
	$("#aa14a_skip_step1").hide();
	$("#aa14a_step2 > div").removeClass("aa14a_titulo_paso_gris");
	$("#aa14a_step2 > div > span").text("1");
	$("#aa14a_step3 > div > span").text("2");
	$("#aa14a_step4 > div > span").text("3");
	$("input:radio[name='serviceLocId']:checked").trigger("click");
	$("#aa14a_step2_next").show();
	$("#aa14a_step2 > div.aa14a_bloque").show();
	$("#aa14a_createAppointment_form #aa14a_personal_info_frm").find("input:not([value]):first, textarea:empty:first").first().focus();
	

	//put a language dependent text for the selected radiobutton
	$("#aa14a_createAppointment_form input[type=radio][name='bloodDonation.notDonatedSince']").change(function() {
		//see /blooddonation/common/inc/aa14aVariablesIdiomaticas.inc
		var codeValue = $("input[type=radio][name='bloodDonation.notDonatedSince']:checked").val();
		$("#datotiempodesdeultimadonacion").html(getLastBloodDonationTextFromCodeValue(codeValue));
		//reset the date picker 
		var today = new Date();
		$("#date").datepicker("setDate",today);
		var locId = getSelectedServiceLocationId();
		var numberOfAdjacentSlots=getNumberOfAdjacentSlots();
		obtenerCitasLibres(today,
						   true,		// slip date range to find first available slot
						   numberOfAdjacentSlots,
						   locId);
	});	
	
	//Don't allow to modify data binded with the number of adjacent slots
	$("#aa14a_editAppointment_form input[type=radio][name='bloodDonation.notDonatedSince']").attr("disabled", true); 
	
});

/**
 * Validate customer data
 * <ul><li>applicant identification is mandatory</li>
 * <li>phone number is mandatory and must well formed</li>
 * <li>e-mail address is optional but must be well formed</li>
 * </ul>
 */
function validateCustomerData() {
	var msgErrores="";
	
	// requestor
	if( $("#aa14a_editAppointment_dialog #nif").val() == "" || $("#aa14a_createAppointment_form #nif").val() == "" ) {
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+nifMsgError+"</p>";
	}
	if( $("#nombre").val() == "" ) {
		   msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+nombreMsgError+"</p>";
	}
	if( $("#apellidos").val() == "" ) {
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+apellidosMsgError+"</p>";
	}
	
	//last time donating
	if( $("input[type=radio][name='bloodDonation.notDonatedSince']:checked").val() == undefined) {
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+lastTimeDonatingMsgError+"</p>";
	}
	
	// contact data
	if( $.trim($("#movil").val()) != "") {	
		if( !validarMovil("#movil")) {
			msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+movilMsgError+"</p>";
		}
	} else { 
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+movilEmptyMsgError+"</p>";
	}
	
	if( !validarEmail("#email")) {
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+emailMsgError+"</p>";
		
	}
	return msgErrores;
}

function generateLocator() {
	// not yet implemented
	return true;
}

// Return the number of slots needed for a single appointment
function getNumberOfAdjacentSlots(){
	
	
	if ($("#aa14a_createAppointment_form input[type=radio][name='bloodDonation.notDonatedSince']:checked").val() ==1){
		return 1;
	}
	else{
		return 2;
	}
}

//Correlates a code value for lastBloodDonation with its text representation
//For text representations see /blooddonation/common/inc/aa14aVariablesIdiomaticas.inc
function getLastBloodDonationTextFromCodeValue(codeValue){
	if (codeValue== -1){
		return NEVER;
	}
	else if (codeValue == 1){
		return LESS_THAN_3_YEARS;
	}
	else if (codeValue == 2){
		return MORE_THAN_3_YEARS;
	}
	else return "";
}

