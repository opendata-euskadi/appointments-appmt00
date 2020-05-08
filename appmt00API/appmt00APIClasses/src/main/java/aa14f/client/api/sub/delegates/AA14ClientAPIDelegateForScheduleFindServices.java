package aa14f.client.api.sub.delegates;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14FindServicesForSchedule;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;

public class AA14ClientAPIDelegateForScheduleFindServices
	 extends AA14ClientAPIDelegateForFindServicesBase<AA14ScheduleOID,AA14ScheduleID,AA14Schedule> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIDelegateForScheduleFindServices(final Provider<SecurityContext> securityContextProvider,
													    final Marshaller modelObjectsMarshaller,
													 	final AA14FindServicesForSchedule findServicesProxy) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  findServicesProxy);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
}
