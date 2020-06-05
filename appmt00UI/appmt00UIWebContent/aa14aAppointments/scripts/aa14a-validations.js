///////////////////////////////////////////////////////////////////////////////////////////////////
//	COMMON INPUT VALIDATION
///////////////////////////////////////////////////////////////////////////////////////////////////
function validarDatosPersonales() {	
	var msgErrores = validateCustomerData();	// see scripts/xx/aa14aForm.js
	if (msgErrores == "") {
		$("#aa14a_step2_error").hide();
		$("#aa14a_errores_datospersonales").hide();
		return true;
	} else {
		// show the error layer
		$("#aa14a_step2_error").show();
		$("#aa14a_errores_datospersonales").empty();
		$("#aa14a_errores_datospersonales").html(msgErrores);
		$("#aa14a_errores_datospersonales").show();
		return false;
	}
	return true;
};
function validateNIF(nif) {
	var success = false;
	$.ajax({
		  url		: '/' + appmtContextRoot + '/AA14ControllerServlet?R01HNoPortal=true',
		  data		: "op=VALIDATE_PERSONID&personId=" + nif,
		  type		: "get",
		  async		: false,	// IMPORTANT!! the success info is needed SYNCHRONOUSLY!
		  success	: function(validationResult) {
							success = validationResult;
					  },
		  error		: function (xhr, ajaxOptions, thrownError) {
			    			console.log("error while doing nif server-side validation");
			    			success = validationResult;
			  		  }
	});	
	console.log("...." + nif + " valid=" + success);
	return success;
}
function validarEmail(obj) {
	if( $(obj).val() != "" ) {
		if( $(obj).val() != "" ) {
			var regex = /[\w-\.]{2,}@([\w-]{2,}\.)*([\w-]{2,}\.)[\w-]{2,4}/;
				//Se utiliza la funcion test() nativa de JavaScript
			if (regex.test($("#email").val().trim())) {
				return true;
			} else {
				return false;
			}
		}	
	}
	return true;
}
function validarMovil(obj) {
	if( $(obj).val() != "" ) {
		var movil = $(obj).val().replace(" ", "");
		movil = movil.replace(/\s/g, '');
				
		if ($.isNumeric (movil)) {
			return true;
		} else { 
			return false;
		}
	}
	return true;
}
