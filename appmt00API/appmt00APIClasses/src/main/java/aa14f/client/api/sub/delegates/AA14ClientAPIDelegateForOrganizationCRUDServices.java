package aa14f.client.api.sub.delegates;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14CRUDServicesForOrganization;
import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;

public class AA14ClientAPIDelegateForOrganizationCRUDServices
	 extends AA14ClientAPIDelegateForOrganizationalEntityCRUDServicesBase<AA14OrganizationOID,AA14OrganizationID,AA14Organization> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIDelegateForOrganizationCRUDServices(final Provider<SecurityContext> securityContextProvider,
															final Marshaller modelObjectsMarshaller,
															final AA14CRUDServicesForOrganization crudServicesProxy) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  crudServicesProxy);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
}
