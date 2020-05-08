// see aa14aCustomerDetails.inc
function paintCustomAppointmentDetails(appointment) {
	console.log(appointment);
	if (appointment._personLocatorId && appointment._personLocatorId._id){ //backwards compatibility
		$("#datolocator").html(appointment._personLocatorId._id);
		$("#aa14a_editAppointment_form #locator").val(appointment._personLocatorId._id);
		$("#locatorRow").show();
	}
	
	//Set the text associated with the selected option for the combo
	//see /justizia/common/inc/aa14aVariablesIdiomaticas.inc
	if (appointment._businessData && appointment._businessData!=""){
		var selectedValue=getProfileEnumFromCodeValue(appointment._businessData._profile);
		$("#aa14a_editAppointment_form select[name='justizia.perfil']").val(selectedValue);
		$("#datoperfil").html(eval(appointment._businessData._profile));
	}
	else {
		$("#datoperfil").html("");
	}
	//readonly input is not sent
	$("#aa14a_editAppointment_form #nif").hide();
	$("#aa14a_editAppointment_form #nifTxt").val(appointment._person._id._id);
	$("#aa14a_editAppointment_form #nifTxt").prop("disabled", true);
	$("#aa14a_editAppointment_form #nifTxt").prop("readonly", true);
	$("#aa14a_editAppointment_form #locator").hide();
	
	$("#aa14a_editAppointment_form #locatorTxt").prop("disabled", true);
	$("#aa14a_editAppointment_form #locatorTxt").prop("readonly", true);
}

//Correlates a enum value for lastBloodDonation with code value
function getProfileEnumFromCodeValue(enumValue){
	
	if (enumValue == "PERFIL_ABOGADO"){
		return 1;
	}
	else if (enumValue == "PERFIL_PROCURADOR"){
		return 2;
	}
	else if (enumValue == "PERFIL_CIUDADANIA"){
		return 3;
	}
	else if (enumValue == "PERFIL_GRADUADO"){
		return 4;
	}
	else if (enumValue == "PERFIL_OTRO"){
		return 5;
	}
	else return "";
}