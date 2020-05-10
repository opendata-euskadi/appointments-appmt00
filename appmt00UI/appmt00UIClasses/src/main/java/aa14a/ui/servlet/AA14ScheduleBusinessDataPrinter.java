package aa14a.ui.servlet;

import java.io.PrintWriter;

import aa14f.model.AA14Appointment;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import r01f.locale.Language;

/**
 * Interface for schedule printing
 */
public interface AA14ScheduleBusinessDataPrinter {
	public boolean shouldPrintAppointmentDuration(final AA14OrgDivisionServiceLocation loc);
	public void printBusinessData(final AA14OrgDivisionServiceLocation loc,
								  final AA14Appointment appointment,final PrintWriter w,
								  final Language lang);
}
 