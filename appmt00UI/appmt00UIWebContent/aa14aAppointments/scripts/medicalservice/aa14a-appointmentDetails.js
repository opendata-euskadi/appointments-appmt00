$(document).ready(function(){
	if (window.nombreCapa && nombreCapa == "aa14a_capa_justificante") {
		$("#aa14a_capa_justificante").show();
	}
});
	

// see aa14aCustomerDetails.inc
function paintCustomAppointmentDetails(appointment) {
	console.log(appointment);
	
	//show requested date
	$("#citaSol").show();
	
	//Copy data into disabled input text components
	$("div.aa14a_formDataSection > p > input#display_nif").val(appointment._person._id._id);
	$("#display_nombre").val(appointment._person._name);
	$("#display_apellidos").val(appointment._person._surname1);
	
	// contact
	if (appointment._contactInfo
		&& appointment._contactInfo._phones) {
		$("#datofijo").html("");
		$("#datomovil").html("");
		 for (var i in appointment._contactInfo._phones) {
	            if (appointment._contactInfo._phones[i]._type=="NORMAL"
	            	&& appointment._contactInfo._phones[i]._number){
	            	$("#datofijo").html(appointment._contactInfo._phones[i]._number._id);
	            	
	            }
	            if (appointment._contactInfo._phones[i]._type=="MOBILE"
	            	&& appointment._contactInfo._phones[i]._number){
	            	$("#datomovil").html(appointment._contactInfo._phones[i]._number._id);
	            }
	     }
	}
	else{
		$("#datofijo").html("");
		$("#datomovil").html("");
	}
	
	//Customer info edit form
	if (appointment._contactInfo
			&& appointment._contactInfo._phones) {
			$("#aa14a_editAppointment_dialog #fijo").val("");
			$("#aa14a_editAppointment_dialog #movil").val("");
			 for (var i in appointment._contactInfo._phones) {
		            if (appointment._contactInfo._phones[i]._type=="NORMAL"
		            	&& appointment._contactInfo._phones[i]._number){
		            	$("#aa14a_editAppointment_dialog #fijo").val(appointment._contactInfo._phones[i]._number._id);
		            }
		            if (appointment._contactInfo._phones[i]._type=="MOBILE"
		            	&& appointment._contactInfo._phones[i]._number){
		            	$("#aa14a_editAppointment_dialog #movil").val(appointment._contactInfo._phones[i]._number._id);
		            }
		     }
		}else{
			$("#aa14a_editAppointment_dialog #fijo").val("");
			$("#aa14a_editAppointment_dialog #movil").val("");
		}
	if(appointment._subject._description){
		$("#aa14a_editAppointment_dialog #subjectDetail").html(appointment._subject._description);
		$("#aa14a_editAppointment_dialog #datosubjectDetail").html(appointment._subject._description);
	}
	else{
		$("#aa14a_editAppointment_dialog #subjectDetail").html("");
		$("#aa14a_editAppointment_dialog #datosubjectDetail").html("");
	}
}

