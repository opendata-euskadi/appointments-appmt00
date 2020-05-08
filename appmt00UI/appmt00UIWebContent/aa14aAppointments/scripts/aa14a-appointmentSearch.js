///////////////////////////////////////////////////////////////////////////////////////////////////
//	BUSQUEDA DE CITAS
///////////////////////////////////////////////////////////////////////////////////////////////////
function initAppointmentSearch() {
    var datePickerOptions = {
			minDate:new Date(2016, 5, 1), // there are no appointments before 01/06/2016
			changeMonth: true,
			changeYear: true,
			yearRange: "c-10:c+10", // current +-10
		};
    if ($("#formBuscarCitas input[name='search_start_date']")) {
		$("#formBuscarCitas input[name='search_start_date']").datepicker(datePickerOptions);
		$("#formBuscarCitas input[name='search_end_date']").datepicker(datePickerOptions); 
		 //default range
		 // FIXME search fails with a preloaded start date
		 //$("#formBuscarCitas input[name='search_start_date']").datepicker("setDate",new Date());
    }
    
	// Cambiar datos dependiendo del servicio seleccionado
	$("input:radio[name='serviceLocId']").click(
		function() {
			// get the selected location id
			var locationId = getSelectedServiceLocationId();
				        
	        // update booking config (see aa14a-main.js)
	        _loadBookingConfigForLocation(locationId);
		});
	// search button
    $("#aa14a_realizar_busqueda").click(
        	function() {
    	    	var textErrors = "";
    	        var errValidacionCampos = false;
    	        $("#aa14a_errores_busqueda").hide();
    	        var textErrors = validateSearchParams();
    	        if (textErrors != "") {
    	    		//textErrors += '<p id ="aa14a_mensaje_faltan_datos_busqueda" class="aa14a_warning"><span class="fa fa-warning"></span>' + msgErrorSearch + '</p>';
    	    		errValidacionCampos = true;
    	        }
    	        if (errValidacionCampos) {
    	    		$("#aa14a_errores_busqueda").html(textErrors);
    	    		$("#aa14a_errores_busqueda").show();
    	        } else {
    	    		buscarCitas();
    	        }
        	});
    // remind locator
    if ($("#aa14a_recordar_localizador")) {
    	$("#aa14a_recordar_localizador").click(
    			function() {
    				// show a popup to enter the [NIF] + [EMAIL]
					var dialog = $("#aa14_locator_request_dialog").dialog({
																			title		: requestLocatorTitle,																			autoOpen	: false,
																			modal		: true,
																			width		: 'auto'
					    												  });
	    			dialog.dialog("open");
    			});
    	// pop-up: recover button 
    	if ($("#aa14a_btn_remind_locator")) {
    		$("#aa14a_btn_remind_locator").click(
    			function() {
    				$("#aa14a_btn_remind_locator").prop('disabled', true);
    				$.ajax({
    					  url		: '/aa14aUIWar/AA14ControllerServlet?R01HNoPortal=true' +
    					  												'&op=REMIND_PERSON_LOCATOR' +
    					  												'&orgId=' + orgId + // see aa14aBusqueda.inc for each initiative
    					  												'&lang=' + idioma,	// see aa14aJSDynamicVars.inc
    					  data		: $("#formRemindPersonLocator").serialize(),
    					  type		: "get",
    					  success	: function(success) {
    										
    						  				$("#recoverForm").hide();
    						  				$("#recoverForm input[name='personId']").val('');
    						  				$("#recoverForm input[name='email']").val('');
			
    										if (success) {
    						  					$("#aa14_recover_response_dialog #recoverMessage").html(requestLocatorSucess);
    						  				} else {
    						  					$("#aa14_recover_response_dialog #recoverMessage").html(requestLocatorError);
    						  				}
				  							$("#aa14_recover_response_dialog").show();
			    						  	
    					  		   	  },
    					  error		: function(xhr,ajaxOptions,thrownError) {
    						  			$("#recoverForm").hide();
    						  			$("#aa14_locator_recover_dialog #recoverMessage").html(msgErrorGenerico);
    						  			$("#aa14_locator_recover_dialog").show();
    							 	  } 
    				});
    			});
    		
 	    	$("#aa14_recover_response_dialog #aa14a_btn_close_locator").click(
					function(){
						$("#aa14_locator_request_dialog").dialog('close'); 
						$("#aa14a_btn_remind_locator").prop('disabled', false);
						$("#recoverForm").show();
						$("#aa14_recover_response_dialog").hide();
						
					});
    	}
    	
    }
}
function buscarCitas() {
	$("#op").val("BUSCARCITAS");
	$("#lang").val(idioma);		// see aa14aJSDynamicVars.inc
	console.log($("#formBuscarCitas").serialize());
	$.ajax({
		  url		: '/aa14aUIWar/AA14ControllerServlet?R01HNoPortal=true',
		  data		: $("#formBuscarCitas").serialize(),
		  type		: "get",
		  success	: function(responseText) {
			  				pintarTablaBusquedaCitas(responseText);
		  		   	  },
		  error		: function(xhr,ajaxOptions,thrownError) {
							$("#aa14a_errores_busqueda").hide();
							var msgErrores="";
							if (xhr.responseText==103) {
								  msgErrores ="<p class='aa14a_warning'><span class='fa fa-warning'></span>" + msgNoExiste + "</p>";
							      $("#aa14a_errores_busqueda").html(msgErrores);
								  $("#aa14a_errores_busqueda").show();
							} else {
								  msgErrores ="<p class='aa14a_warning'><span class='fa fa-warning'></span>" + msgErrorGenerico + "</p>";
								  $("#aa14a_errores_busqueda").html(msgErrores);
								  $("#aa14a_errores_busqueda").show();
							}
				 	  } 
	});
}
function pintarTablaBusquedaCitas(appointments) {
	var lang = idioma;		// see aa14aJSDynamicVars.inc
	$("#aa14a_search_results").show();
	$("#aa14a_search_results_items").empty();
	var trHTML = '';
	$(appointments).each(
			 function() {
        		trHTML += '<tr>';
        		if (this._division._id._id == "EJGV_JUSTIZIA"){ //Show different data for Justizia app
        			trHTML += '<td>' + this._location._municipality + '</td>';
        		}
        		else {
        			trHTML += '<td>' +
 				   			   	'<div>' + this._service._name + '</div>' +
 				   			   	'<div>' + this._location._county + '</div>' +
 				   			  '</td>';
        		}
        		trHTML += '<td>' + this._person._name + '</td>';
        		trHTML += '<td>' + this._person._surname1 + '</td>';
        		trHTML += '<td>' + this._person._id._id + '</td>';
        		trHTML += '<td>' + devolverFechaFormateada(this) + '</td>';
        		trHTML += '<td class="aa14a_th-center">' +
        						'<div class="aa14a_edit_btn" ' + 
        							 'slotOid="' + this._oid._id + '" ' +
        							 'locId="' + this._location._id._id + '" ' +
        							 'schId="' + this._schedule._id._id + '" ' + 
        							 'title="' + this._person._surname1 +", "+ this._person._name +'" ' + 
        							 'slotDate="' + this._year + "/" + this._monthOfYear + "/" + this._dayOfMonth + '">';
        		//Show the edit control only for current and future appointments
        		var now = moment();
        		var year = this._year._year;
        		var monthOfYear = this._monthOfYear._monthOfYear;
				var dayOfMonth = this._dayOfMonth._dayOfMonth;
				var appointmentDate = moment(new Date(year,monthOfYear-1,dayOfMonth));
				
				var isToday = appointmentDate.isSame(now,'year') && appointmentDate.isSame(now,'month') && appointmentDate.isSame(now,'day');
        		var today= new Date();
        		
        		if (isToday || appointmentDate.isAfter(now)){
        					trHTML +='<a href="#"><span class="fa fa-pencil-square-o"></span></a>';
        		}
        		trHTML+=		'</div>' + 
        				   '</td>';
        		trHTML += '</tr>';
			 });
	$("#aa14a_search_results_items").append(trHTML);
	
	// attach the on click event to the edit button
	$(".aa14a_edit_btn").each(
			function(index) {
				$(this).on("click",
						   function() {
								var slotOid = $(this).attr("slotOid");				// the slotId attr of the button
								var locId = $(this).attr("locId"); 		// the location id attr of the button
								var schId = $(this).attr("schId");					// the schedule id attr of the button
								var title = $(this).attr("title");					// requestor name
								
								var dateSplitted = $(this).attr("slotDate").split("/");
								var year = dateSplitted[0];
								var monthOfYear = dateSplitted[1] - 1;	// beware ZERO based!!
								var dayOfMonth = dateSplitted[2];
								var slotDate = new Date(year,monthOfYear,dayOfMonth);
//								
								openEditAppointmentDialog(slotOid,
														  locId,schId,
														  title,
														  slotDate);				
						})
			});
}
