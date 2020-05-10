package aa14a.bootstrap.ui;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

import aa14a.ui.portal.AA14AppEmbedServletFilter;
import aa14a.ui.servlet.AA14CalendarServlet;
import aa14a.ui.servlet.AA14ControllerServlet;
import aa14a.ui.servlet.AA14ModelObjectsLoadServlet;
import aa14a.ui.servlet.AA14SchedulePrintServlet;

abstract class AA14GuiceServletModuleBase 
	   extends ServletModule {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	protected void configureMainServlets() {
		// Portal page embed filter
		// Bind the filter as singleton (guice requires it)
		this.bind(AA14AppEmbedServletFilter.class)
			.in(Singleton.class);
		this.filterRegex(".*")
			.through(AA14AppEmbedServletFilter.class);
		
		// controller
		this.serve("/" + AA14ControllerServlet.class.getSimpleName())
			.with(AA14ControllerServlet.class);
		// calendar
		this.serve("/" + AA14CalendarServlet.class.getSimpleName())
			.with(AA14CalendarServlet.class);
		this.serve("/" + AA14SchedulePrintServlet.class.getSimpleName())
			.with(AA14SchedulePrintServlet.class);
		// view
		this.serve("/" + AA14ModelObjectsLoadServlet.class.getSimpleName())
			.with(AA14ModelObjectsLoadServlet.class);
	}
}
