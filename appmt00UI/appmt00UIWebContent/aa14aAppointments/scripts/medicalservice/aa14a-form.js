$(document).ready(function(){
	autoCompleteCustomerData();
	//hide customer data seach box in edit form
	$("#aa14a_editAppointment_form #search_box").hide();
	
	//Some aditional behaviour in the edit dialog
	$('#aa14a_editAppointment_dialog').on('dialogopen', function(event) {
														if ($("#subjectDetailsSection").length){
															$("#subjectDetailsSection").show();
															$("#aa14_sm_proof_print_btn").show();
														}
	});
	
	$("#aa14_fecha_hora_cita_change_btn").click(function(evt) {
													if ($("#subjectDetailsSection").length){
														$("#subjectDetailsSection").hide();
														$("#aa14_sm_proof_print_btn").hide();
													}
												});
	$("#aa14_change_customer_details_btn").click(function(evt) {
													if ($("#subjectDetailsSection").length){
														$("#subjectDetailsSection").hide();
														$("#aa14_sm_proof_print_btn").hide();
													}
												 });
	// Print proof document button
	$("#aa14_sm_proof_print_btn").click(
				function() {
					var lang = idioma;  	// see aa14aJSDynamicVars.inc
					var slotOid = $("#aa14a_editAppointment_dialog").data('slotOid');	// receive the date handed to the dialog with > dialog.data('slotOid',theOid);
					var url = 'aa14aConsulta.jsp?lang=' + lang + 
											   '&appointmentId='+ slotOid + 
											   '&nombreCapa=aa14a_capa_justificante' + 
											   '&R01HNoPortal=true'; //stand alone view
					window.location.href = url;		// = clicking on a link
				});
});

/**
 * Validate customer data
 * <ul><li>applicant identification is mandatory</li>
 * <li>phone number is mandatory and must well formed</li>
 * <li>e-mail address is optional but must be well formed</li>
 * </ul>
 */
function validateCustomerData() {
	var msgErrores="";
	
	// requestor
	if( $("#aa14a_editAppointment_dialog #nif").val() == "" || $("#aa14a_createAppointment_form #nif").val() == "" ) {
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+nifMsgError+"</p>";
	}
	if( $("#nombre").val() == "" ) {
		   msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+nombreMsgError+"</p>";
	}
	if( $("#apellidos").val() == "" ) {
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+apellidosMsgError+"</p>";
	}
	if( $("#subjectDetail").val() == "" ) {
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+motivoMsgError+"</p>";
	}
	
	// contact data
	if( !validarMovil("#fijo")) {
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+fijoMsgError+"</p>";
	}
	if( !validarMovil("#movil")) {
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+movilMsgError+"</p>";
	}
	if( !validarEmail("#email")) {
		msgErrores +="<p class='aa14a_warning'><span class='fa fa-warning'></span>"+emailMsgError+"</p>";	
	}
	return msgErrores;
}

function generateLocator() {
	// not yet implemented
	return true;
}

// Return the number of slots needed for a single appointment
function getNumberOfAdjacentSlots() {
	return 1;
}

function autoCompleteCustomerData() {
	$("#search_customer")
    // don't navigate away from the field on tab when selecting an item
    .bind( "keydown", function( event ) {
      if ( event.keyCode === $.ui.keyCode.TAB &&
          $( this ).data( "ui-autocomplete" ).menu.active ) {
        event.preventDefault();
      }        
    })
    .autocomplete({
      source: function( request, response ) {
        $.getJSON( "/aa14aUIWar/AA14ControllerServlet?R01HNoPortal=true", {
          op:"BUSCAR_PERSONAS",
          term:  request.term 
        }, response );
      },
      search: function() {
        // custom minLength
    	var valor=this.value;
        
        $("#nif").val("");
        $("#nif").change();
        
        $("#nombre").val("");
        $("#nombre").change();
        
        $("#apellidos").val("");
        $("#apellidos").change();
        
        $("#fijo").val("");
        $("#fijo").change();
        
        $("#email").val("");
        $("#email").change();
        
        //Min search characters
        if ( valor.length < 3 ) {
          return false;
        }
        
      },
      focus: function() {
        // prevent value inserted on focus
        return false;
      },
      select: function( event, ui ) {             
        $("#nif").val(ui.item.nif);
        $("#nif").change();
        
        $("#nombre").val(ui.item.name);
        $("#nombre").change();
        
        $("#apellidos").val(ui.item.surname);
        $("#apellidos").change();
        
        $("#fijo").val(ui.item.telephone);
        $("#fijo").change();
        
        $("#email").val(ui.item.mail);
        $("#email").change();
        
        // set the focus on the first visible empty input or textarea
        if ($("#nif").val() ==''){
        	$("#nif").focus();
        }
        else if ($("#nombre").val() ==''){
        	$("#nombre").focus();
        }
        else if ($("#apellidos").val() ==''){
        	$("#apellidos").focus();
        } 
        else if ($("#fijo").val() ==''){
        	$("#fijo").focus();
        }
        else {
        	$("#movil").focus();
        }
        	
      },
      response: function(event, ui) {
          if (!ui.content.length) {
              var noResult = { value: "", label: noDataMsg };
              ui.content.push(noResult);
          }
      }
    });
	// When clicking the [change] link show the hole selector
	$("#aa14_fecha_hora_cita_sm_change_btn").click(function(evt) {
		
													showSlotDateTimeSelectorTableInEditDialog(slotOid,
																							  appointmentStartDate);
												});
	// When clicking the [change] link show the hole selector
	$("#aa14_change_customer_sm_details_btn").click(function(evt) {
													showCustomerDetailInEditDialog(slotOid);
												 });
}

