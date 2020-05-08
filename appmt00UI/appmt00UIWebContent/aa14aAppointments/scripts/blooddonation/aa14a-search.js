function validateSearchParams() {
	var errorMsg="";
	if ($("#nif").val() == ""){
		errorMsg +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+msgErrorSearch+"</p>";
	}
	return errorMsg;
}