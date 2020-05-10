package aa14a.ui.servlet;

import aa14f.model.AA14Appointment;
import r01f.servlet.HttpRequestParamsWrapper;

/**
 * Sets appointment data from the request params
 */
public interface AA14AppointmentFromRequestBuilder {
	public void setAppointmentDataFromRequest(final HttpRequestParamsWrapper reqParams,final AA14Appointment appointment);
}
