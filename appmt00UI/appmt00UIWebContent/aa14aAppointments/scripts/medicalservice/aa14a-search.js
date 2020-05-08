$(document).ready(function(){
	//we have an unique location so we skip step 1
	$("#formBuscarCitas input[name='search_start_date']").val('');
	$("#aa14a_history_date").hide();
	
	
	if ($("#history")){
		$($("#history")).change(function() {
			if ($("#history").prop("checked")){
				$("#formBuscarCitas input[name='search_start_date']").datepicker('setDate', new Date(2016, 5, 1)); // there are no appointments before 01/06/2016);
			}
			else {
				$("#formBuscarCitas input[name='search_start_date']").val('');
			}
		});
	}
	
});


function validateSearchParams() {
	var errorMsg="";
	if ($("#nif").val() == ""){
		errorMsg +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+nifMsgError+"</p>";
	}
	if (!getSelectedServiceLocationId()){
		if ($("#display_nif").is(":disabled")){
			if (canSelectAnyCounty){ //see medicalservice/common/aa14aBusqueda.inc for var initialization
				errorMsg +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+servicioMsgError+"</p>";
			}
			else {
				errorMsg +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+servicioMsgError2+"</p>";
			}
		}
		else{
			errorMsg +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+servicioMsgError+"</p>";
		}
	}
	return errorMsg;
}