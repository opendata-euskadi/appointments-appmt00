///////////////////////////////////////////////////////////////////////////////////////////////////
//	DATE FUNCTIONS
///////////////////////////////////////////////////////////////////////////////////////////////////
function _yearOf(slot) {
	return slot._year._year;
}
function _monthOfYearOf(slot) {
	return slot._monthOfYear._monthOfYear;
}
function _dayOfMonthOf(slot) {
	return slot._dayOfMonth._dayOfMonth;
}
function _hourOfDayOf(slot) {
	return slot._hourOfDay._hourOfDay;
}
function _minuteOfHourOf(slot) {
	return slot._minuteOfHour._minuteOfHour;
}
function devolverFechaFormateada(slot) {
	var year = _yearOf(slot); 
	var month = _monthOfYearOf(slot); 
	var day = _dayOfMonthOf(slot); 
	var hour = _hourOfDayOf(slot);
	var minutes = _minuteOfHourOf(slot);
	var fechaSlot = new Date(year,month-1,day);
	hour = ("0" + hour).slice(-2);
	minutes = ("0" + minutes).slice(-2);
	if (idioma == "eu") {	// see aa14aJSDynamicVars.inc
		return diasSemana[fechaSlot.getDay()]+" "+year+"/"+month+"/"+day+" ["+hour+":"+minutes+"]";
	} else {
		return diasSemana[fechaSlot.getDay()]+" "+day+"/"+month+"/"+year+" ["+hour+":"+minutes+"]";
	}
}
function isDateCast(txtDate) {
    if (txtDate == '') return false;
    
    var rxDatePattern = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;     //Checks for dd/mm/aaaa format.
    var dtArray = txtDate.match(rxDatePattern); 
    
    if (dtArray == null) return false;
    
	dtDay = dtArray[1];
	dtMonth= dtArray[3];
	dtYear = dtArray[5];         
    
    return checkDate(dtYear,dtMonth,dtDay);
}	
function isDateEus(txtDate) {
    if (txtDate == '') return false;
    
    var rxDatePattern = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/; 	    // Checks for yyyy/mm/dd format.
    var dtArray = txtDate.match(rxDatePattern); 
    
    if (dtArray == null) return false;
    
	dtYear = dtArray[1]; 
	dtMonth= dtArray[5];
	dtDay = dtArray[7];         
    
    return checkDate(dtYear,dtMonth,dtDay);
}
function checkDate(dtYear,dtMonth,dtDay) {
    if (dtMonth < 1 || dtMonth > 12) {
        return false;
    } else if (dtDay < 1 || dtDay> 31) {
        return false;
    } else if ((dtMonth==4 || dtMonth==6 || dtMonth==9 || dtMonth==11) && dtDay ==31) { 
        return false;
    } else if (dtMonth == 2) {
        var isleap = (dtYear % 4 == 0 && (dtYear % 100 != 0 || dtYear % 400 == 0));
        if (dtDay> 29 || (dtDay ==29 && !isleap)) return false;
    }
    return true;	
}


