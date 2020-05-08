function validateCustomerData() {
	var msgErrores="";

	console.log($("#aa14a_editAppointment_form #nif").val());
	// requestor
	if( $("#aa14a_editAppointment_form #nif").val() == "" || $("#aa14a_createAppointment_form #nif").val() == "" ) {
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+nifMsgError+"</p>";
	}
	if( $("#nombre").val() == "" ) {
		   msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+nombreMsgError+"</p>";
	}
	if( $("#apellidos").val() == "" ) {
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+apellidosMsgError+"</p>";
	}	
	
	// appointment
	if( $("#aa14a_editAppointment_dialog #codExp").val() == "" || $("#aa14a_createAppointment_form #codExp").val() == "") {
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+codExpMsgError+"</p>";
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
function getNumberOfAdjacentSlots() {
	return 1;
}
