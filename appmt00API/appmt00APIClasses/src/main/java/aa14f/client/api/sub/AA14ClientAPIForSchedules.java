package aa14f.client.api.sub;

import java.util.Collection;
import java.util.Map;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14CRUDServicesForSchedule;
import aa14f.api.interfaces.AA14FindServicesForSchedule;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForScheduleCRUDServices;
import aa14f.client.api.sub.delegates.AA14ClientAPIDelegateForScheduleFindServices;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.ClientSubAPIBase;
import r01f.services.client.api.delegates.ClientAPIModelObjectChangesTrack;
import r01f.services.interfaces.ServiceInterface;

/**
 * Client implementation of services maintenance.
 */
@Accessors(prefix="_")
public class AA14ClientAPIForSchedules
     extends ClientSubAPIBase {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final AA14ClientAPIDelegateForScheduleCRUDServices _forCRUD;
	@Getter private final AA14ClientAPIDelegateForScheduleFindServices _forFind;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("rawtypes")
	public AA14ClientAPIForSchedules(final Provider<SecurityContext> securityContextProvider,
									 final Marshaller modelObjectsMarshaller,
									 final Map<Class,ServiceInterface> srvcIfaceMappings) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  srvcIfaceMappings);	// reference to other client apis

		_forCRUD = new AA14ClientAPIDelegateForScheduleCRUDServices(securityContextProvider,
														   		    modelObjectsMarshaller,
													 			 	this.getServiceInterfaceCoreImplOrProxy(AA14CRUDServicesForSchedule.class));
		_forFind = new AA14ClientAPIDelegateForScheduleFindServices(securityContextProvider,
																	modelObjectsMarshaller,
															  		this.getServiceInterfaceCoreImplOrProxy(AA14FindServicesForSchedule.class));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds schedules of the given type for the given business id
	 * @param orgEntityType
	 * @param businessId
	 * @return
	 */
	public Collection<AA14Schedule> findByBusinessId(final AA14BusinessID businessId) {
		Collection<AA14Schedule> outSchs = this.getServiceInterfaceCoreImplOrProxy(AA14FindServicesForSchedule.class)
											   .findByBusinessId(this.getSecurityContext(),
													   			 businessId)
											   .getOrThrow();
		ClientAPIModelObjectChangesTrack.startTrackingChangesOnLoaded(outSchs);
		return outSchs;
	}
}
