// inits the release booked slot buttons
// called from:
//		-the appointment edit dialog
//		-the non bookable slot edit dialog
function intReleaseBookedSlotButton() {
	// Release slot button
	if ($("#aa14_booked_slot_release_btn").length) {
		$("#aa14_booked_slot_release_btn").click(
												function() {
													var dialog = $("#aa14_booked_slot_release_dialog").dialog();
													dialog.dialog("open");
												});
	}
	// The user confirmation dialog 
	$("#aa14_booked_slot_release_dialog").dialog(
		{
			autoOpen	: false,
			resizable	: false,
		    height		: 200,
		    width		: 500,
		    modal		: true,
		    title		: titleMsg,
		    buttons: {
		        Ok:  	function() {
		        			var dataToSend = "op=LIBERAR_SLOT";
		        	
		        			// receive the data handed to the dialog with > dialog.data('slotOid',theOid);
		        			if ($("#aa14a_editAppointment_dialog").length 
		        			 && $("#aa14a_editAppointment_dialog").dialog('isOpen')) {
		        				
		        				var slotOid = $("#aa14a_editAppointment_dialog").data('slotOid');
		        				
		        				dataToSend = dataToSend + "&slotOid=" + slotOid;
		        				
		        			} else if ($("#aa14a_reserveSlot_dialog").length
		        					&& $("#aa14a_reserveSlot_dialog").dialog('isOpen')) {
		        				
		        				var slotOid = $("#aa14a_reserveSlot_dialog").data('slotOid');
		        				var periodicSerieOid = $("#aa14a_reserveSlot_dialog").data('periodicSerieOid');
		        				
		        				dataToSend = $("#aa14a_reservedSlot_delete_form").serialize();
		        			}
				        	$.ajax({
								  url			: '/aa14aUIWar/AA14ControllerServlet?R01HNoPortal=true',
								  data			: dataToSend,
								  type			: "get",
								  success		: function(responseText) {
									  					$("#aa14_booked_slot_release_response_dialog").dialog("open");	// operation success confirmation dialog
								  		    	  },
								  error			: function (xhr, ajaxOptions, thrownError) {
									  					console.log("error");
								  				  },
								  beforeSend	: function() {
												  		$body.addClass("loading"); 
								  				  },
								  complete		: function() {
											    		$body.removeClass("loading");  
											  	  }
							});
				        	$( this ).dialog("close");
		        		},
		        Cancel: function() {
		        			$( this ).dialog( "close" );
		        		}
		    }
		});
	// Slot release operation success dialog
	$( "#aa14_booked_slot_release_response_dialog" ).dialog(
														{
															autoOpen	: false,
														    modal		: true,
														    title		: titleConfirmacion,
														    buttons		: {
																	    	Ok: function() {
																		    		// close all dialogs
																	    			$(this).dialog("close");
																	    			if ($("#aa14a_editAppointment_dialog").length) {
																	    				$("#aa14a_editAppointment_dialog").dialog("close");
																	    			} 
																	    			if ($("#aa14a_reserveSlot_dialog").length) {
																	    				$("#aa14a_reserveSlot_dialog").dialog("close");
																	    			}
																		  			// refresh the calendar / search results if present
																		  			if ($("#calendar").length) {
																		  				refreshCalendar();
																					} else if ($("#aa14a_search_results").length) {
																						buscarCitas();
																					}
																	        	}
														    				}
													});
}