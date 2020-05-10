package aa14f.client.api;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.inject.Inject;
import com.google.inject.Provider;

import aa14f.api.interfaces.AA14BusinessConfigServices;
import aa14f.api.interfaces.AA14NotifierServices;
import aa14f.api.interfaces.AA14PersonLocatorServices;
import aa14f.api.interfaces.AA14SearchServices;
import aa14f.client.api.sub.AA14ClientAPIForBookedSlots;
import aa14f.client.api.sub.AA14ClientAPIForBusinessConfigs;
import aa14f.client.api.sub.AA14ClientAPIForNotifier;
import aa14f.client.api.sub.AA14ClientAPIForOrgDivisionServiceLocations;
import aa14f.client.api.sub.AA14ClientAPIForOrgDivisionServices;
import aa14f.client.api.sub.AA14ClientAPIForOrgDivisions;
import aa14f.client.api.sub.AA14ClientAPIForOrganizations;
import aa14f.client.api.sub.AA14ClientAPIForPersonLocator;
import aa14f.client.api.sub.AA14ClientAPIForSchedules;
import aa14f.client.api.sub.AA14ClientAPIForSearch;
import aa14f.common.internal.AA14AppCodes;
import lombok.experimental.Accessors;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.ClientAPIImplBase;
import r01f.services.interfaces.ServiceInterface;



/**
 * Base type for every API implementation of appointments service.
 */
@Singleton
@Accessors(prefix="_")
public class AA14ClientAPI
     extends ClientAPIImplBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONFIG & ORG SUB-APIs (created at the constructor)
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Config API
	 */
	private final AA14ClientAPIForBusinessConfigs _configAPI;
	/**
	 * Organizations
	 */
	private final AA14ClientAPIForOrganizations _organizationsAPI;
	/**
	 * Divisions
	 */
	private final AA14ClientAPIForOrgDivisions _orgDivisionsAPI;
	/**
	 * Services
	 */
	private final AA14ClientAPIForOrgDivisionServices _orgDivisionServicesAPI;
	/**
	 * Locations
	 */
	private final AA14ClientAPIForOrgDivisionServiceLocations _orgDivisionServiceLocationsAPI;
	/**
	 * Schedules
	 */
	private final AA14ClientAPIForSchedules _schedulesAPI;
/////////////////////////////////////////////////////////////////////////////////////////
//  APPOINTMENTS SUB-APIs
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Appointments
	 */
	private final AA14ClientAPIForBookedSlots _bookedSlotsAPI;
/////////////////////////////////////////////////////////////////////////////////////////
//  SEARCH SUB-APIS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Entity search API
	 */
	private final AA14ClientAPIForSearch _searchAPI;
/////////////////////////////////////////////////////////////////////////////////////////
//	NOTIFICATION SUB-API 
/////////////////////////////////////////////////////////////////////////////////////////	
	private final AA14ClientAPIForNotifier _notifierAPI;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	PERSON LOCTOR
/////////////////////////////////////////////////////////////////////////////////////////	
	private final AA14ClientAPIForPersonLocator _personLocatorAPI;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject @SuppressWarnings("rawtypes")
	public AA14ClientAPI(						 final Provider<SecurityContext> securityContextProvider,
						 @ModelObjectsMarshaller final Marshaller modelObjectsMarshaller,
					     @Named(AA14AppCodes.API_APPCODE_STR) final Map<Class,ServiceInterface> srvcIfaceMappings) {	// comes from injection
		// Services proxy
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  srvcIfaceMappings);

		// Build every sub-api
		_configAPI = new AA14ClientAPIForBusinessConfigs(securityContextProvider,
														 modelObjectsMarshaller, 
														 this.getServiceInterfaceCoreImplOrProxy(AA14BusinessConfigServices.class));
		_organizationsAPI = new AA14ClientAPIForOrganizations(securityContextProvider,
															  modelObjectsMarshaller,
															  srvcIfaceMappings);						    
		_orgDivisionsAPI = new AA14ClientAPIForOrgDivisions(securityContextProvider,
															modelObjectsMarshaller,
				  									        srvcIfaceMappings);
		_orgDivisionServicesAPI = new AA14ClientAPIForOrgDivisionServices(securityContextProvider,
																		  modelObjectsMarshaller,
				  														  srvcIfaceMappings);
		_orgDivisionServiceLocationsAPI = new AA14ClientAPIForOrgDivisionServiceLocations(securityContextProvider,
																						  modelObjectsMarshaller,
																						  srvcIfaceMappings);
		_schedulesAPI = new AA14ClientAPIForSchedules(securityContextProvider,
													  modelObjectsMarshaller,
													  srvcIfaceMappings);
		
		_bookedSlotsAPI = new AA14ClientAPIForBookedSlots(securityContextProvider,
														  modelObjectsMarshaller,
														  srvcIfaceMappings);
		
		_searchAPI = new AA14ClientAPIForSearch(securityContextProvider,
												modelObjectsMarshaller,
												this.getServiceInterfaceCoreImplOrProxy(AA14SearchServices.class));
		
		_notifierAPI = new AA14ClientAPIForNotifier(securityContextProvider,
													modelObjectsMarshaller,
													this.getServiceInterfaceCoreImplOrProxy(AA14NotifierServices.class),
													this.getServiceInterfaceCoreImplOrProxy(AA14BusinessConfigServices.class));
		
		_personLocatorAPI = new AA14ClientAPIForPersonLocator(securityContextProvider,
														 	  modelObjectsMarshaller,
														 	  this.getServiceInterfaceCoreImplOrProxy(AA14PersonLocatorServices.class));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SUB-APIs
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIForBusinessConfigs configAPI() {
		return _configAPI;
	}
	public AA14ClientAPIForOrganizations organizationsAPI() {
		return _organizationsAPI;
	}
	public AA14ClientAPIForOrgDivisions orgDivisionsAPI() {
		return _orgDivisionsAPI;
	}
	public AA14ClientAPIForOrgDivisionServices orgDivisionServicesAPI() {
		return _orgDivisionServicesAPI;
	}
	public AA14ClientAPIForOrgDivisionServiceLocations orgDivisionServiceLocationsAPI() {
		return _orgDivisionServiceLocationsAPI;
	}
	public AA14ClientAPIForSchedules schedulesAPI() {
		return _schedulesAPI;
	}
	public AA14ClientAPIForBookedSlots bookedSlotsAPI() {
		return _bookedSlotsAPI;
	}	
	public AA14ClientAPIForSearch searchAPI() {
		return _searchAPI;
	}
	public AA14ClientAPIForNotifier notifierAPI() {
		return _notifierAPI;
	}
	public AA14ClientAPIForPersonLocator personLocatorAPI() {
		return _personLocatorAPI;
	}
}
