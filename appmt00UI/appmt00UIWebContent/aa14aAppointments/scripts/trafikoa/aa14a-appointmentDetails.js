// see aa14aCustomerDetails.inc
function paintCustomAppointmentDetails(appointment) {
	$("#datocodExp").html(appointment._subject._id._id);
	//Customer info edit form
	$("div.aa14a_formDataSection > p > input#codExp").val(appointment._subject._id._id);
}