// Inits the print appointments button
function initPrintAppointmentsButton() {
	$("#aa14a_print_appointments_btn").click(
			function() {
				// clear selection
				$("#aa14a_print_appointments_form input:radio").prop("checked", false);
				// show the print appointments dialog
				var dialog = $("#aa14a_print_appointments_dialog");
				dialog.dialog("open");
			});
}
// Inits the print appointments dialog
function initPrintAppointmentsDialog() {
	var dlgWith = $(window).width() - 140;
	var dlgHeigh = $(window).height() - 90;
    $("#aa14a_print_appointments_dialog").dialog({
										    	title		: printAppointmentsTitle,
    											autoOpen	: false,
										    	modal		: true,
										    	width		: 'auto',
										    	position	: {my: 'top', at: 'top+100', of: window},
										    	close		: function(event,ui) {
										    						// nothing
										    				  },
											    show		: {
															        effect: "blind",
															        duration: 1000
														      },
											    hide		: {
															        effect: "explode",
															        duration: 1000
											      			  }  
    										});
    
    // Date picker & time picker
    var datePickerOptions = {
    	    					minDate:new Date(2016, 5, 1), // there are no appointments before 01/06/2016
    							changeMonth: true,
    	    					changeYear: true,
    	    					yearRange: "c-10:c+10", // current +-10
    						};
	$("#aa14a_print_appointments_form input[name='print_start_date']").datepicker(datePickerOptions);
	$("#aa14a_print_appointments_form input[name='print_end_date']").datepicker(datePickerOptions);
    
	// Cancel print
	$("#aa14a_cancel_print_appointments_btn").click(
		function() {
			// hide the print form
			var dialog = $("#aa14a_print_appointments_dialog");
			dialog.dialog("close");
		});
	// Print Button
    $("#aa14a_do_print_appointments_btn").click(
    	function() {
    		// validate data
			var isChecked = $("input[name='serviceLocId']:checked").val();							 
			var booleanVlaueIsChecked = false;
			if (!isChecked) {
				// show the error layer
				$("#aa14a_print_appointments_form_errors").show();
			} else {
				$("#aa14a_print_appointments_form_errors").hide();
				
				// submmit the form
				console.log("[CalendarPrint]: " + $( "#aa14a_print_appointments_form" ).serialize());
				$( "#aa14a_print_appointments_form" ).submit();
			}
    	});
}

