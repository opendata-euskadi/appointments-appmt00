$(document).ready(function(){
	
	//hide elements not relevant for each document to print
	if (window.nombreCapa && nombreCapa == "aa14a_capa_justificante") {
		$("#aa14a_capa_justificante").show();
		$("section.aa14a_resumen_cita>p.aa14a_datos-cita").addClass("aa14_justificante_datos_fecha"); //left align
		$("#aa14a_capa_justificante").show();
		$("#aa14a_capa_imprimir").hide();
		
		$("#exitHourProof").show();
		$("div#citaSol").hide();
	
		$("tr>td#datofijo").parent().hide();
		$("tr>td#datomovil").parent().hide();
		$("tr>td#datoemail").parent().hide();
	}
	else{
		$("#aa14a_capa_justificante").hide();
		
		$("section.aa14a_resumen_cita>p.aa14a_datos-cita").removeClass("aa14_justificante_datos_fecha"); //center align
		$("#aa14a_capa_justificante").hide();
		$("#aa14a_capa_imprimir").show();
		
		$("#exitHourProof").hide();
		$("div#citaSol").show();
	
		$("tr>td#datofijo").parent().show();
		$("tr>td#datomovil").parent().show();
		$("tr>td#datoemail").parent().show();
	}
	
	//button for proof of attendance
	if ($("#aa14a_print_proof_btn").length) {
		$("#aa14a_print_proof_btn").click(
			function() {
				window.print();
				return false;
			});
	}
	
	//Paint signature text and exit hour; see aa14a-main.js
	$.when(municipalityText).done(function (location) {
		if (nombreCapa == "aa14a_capa_justificante"){ 
			
			$("#currentHour").html("[" + moment().format('HH:mm') + "]");
			
			$("div#citaSol").hide();
			
			var txt =fechaYLugar.replace("{0}", location);
			txt = txt.replace("{1}", moment().format('LL') );
			txt = txt.replace("{2}", moment().format('HH:mm'));
			$("#aa14a_firma_fecha").html(txt); 
			
			$("#aa14a_firma_firmante").html(firmado.replace("{0}", userName)); //only admin users		
		}
	});
	 	
});
