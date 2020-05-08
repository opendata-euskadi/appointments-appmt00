// see aa14aCustomerDetails.inc
/**
 * Paint the radiobutton value from businessData
 * @param appointment	
 * @returns
 */
function paintCustomAppointmentDetails(appointment) {
	console.log(appointment);
	var textValue="";
	//Set the text associated with the selected option for the radiobutton
	//see /blooddonation/common/inc/aa14aVariablesIdiomaticas.inc
	if (appointment._businessData && appointment._businessData!=""){
		textValue=eval(appointment._businessData._notDonatedSince);
		var checkedValue=getLastBloodDonationEnumFromCodeValue(appointment._businessData._notDonatedSince);
		$("#aa14a_editAppointment_form input[type=radio][name='bloodDonation.notDonatedSince'][value=" + checkedValue +"]").attr("checked", true);
	}
	$("#datotiempodesdeultimadonacion").html(textValue);
	
}

//Correlates a enum value for lastBloodDonation with code value
function getLastBloodDonationEnumFromCodeValue(enumValue){
	if (enumValue == "NEVER"){
		return -1;
	}
	else if (enumValue == "LESS_THAN_3_YEARS"){
		return 1;
	}
	else if (enumValue == "MORE_THAN_3_YEARS"){
		return 2;
	}
	else return "";
}

