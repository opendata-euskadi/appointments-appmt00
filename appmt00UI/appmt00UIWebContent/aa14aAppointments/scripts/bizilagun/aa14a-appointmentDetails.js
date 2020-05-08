// see aa14aCustomerDetails.inc
function paintCustomAppointmentDetails(appointment) {
	console.log(appointment);
	$("#datonumberOfPersons").html(appointment._numberOfPersonsInAppointment);
	$("#datosubjectDetail").html(appointment._subject._description);
	
	if (appointment._numberOfPersonsInAppointment){
		$("#numberOfPersons").val(appointment._numberOfPersonsInAppointment);
	}
	//FIXME
	if (appointment._subject._description){
		$("#subjectDetail").html(appointment._subject._description);
	}
}