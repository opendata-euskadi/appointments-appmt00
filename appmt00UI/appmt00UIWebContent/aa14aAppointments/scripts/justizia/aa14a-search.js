function validateSearchParams() {
	var errorMsg="";
	if ($("#formBuscarCitas #locator").length) {
		if ($("#formBuscarCitas #locator").val() == "") {
			errorMsg +="<p class='aa14a_warning'><span class='fa fa-warning'></span>" + msgLocatorRequiredError + "</p>";
		}
	}
	else if ($("#formBuscarCitas #nif").val() == "") {
		errorMsg += "<p class='aa14a_warning'><span class='fa fa-warning'></span>" + nifMsgError + "</p>";
	}
	
	return errorMsg;
}