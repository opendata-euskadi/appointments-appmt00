package aa14f.client.api.sub.delegates;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14CRUDServicesBase;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.CRUDResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.client.api.delegates.ClientAPIDelegateForModelObjectCRUDServices;

public class AA14ClientAPIDelegateForCRUDServicesBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,M extends PersistableModelObject<O>>
	 extends ClientAPIDelegateForModelObjectCRUDServices<O,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIDelegateForCRUDServicesBase(final Provider<SecurityContext> securityContextProvider,
													final Marshaller modelObjectsMarshaller,
												    final AA14CRUDServicesBase<O,ID,M> crudServicesProxy) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  crudServicesProxy);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Loads an entity by it's id
	 * @param id
	 * @return
	 */
	public M loadById(final ID id) {
		CRUDResult<M> opResult = this.getServiceProxyAs(AA14CRUDServicesBase.class)
											.loadById(this.getSecurityContext(),
												   	  id);
		M outEntity = opResult.getOrThrow();
		return outEntity;
	}	
	public M loadByIdOrNull(final ID id) {
		CRUDResult<M> opResult = this.getServiceProxyAs(AA14CRUDServicesBase.class)
											.loadById(this.getSecurityContext(),
												   	  id);
		M outEntity = null;
		if (opResult.hasSucceeded()) {
			outEntity = opResult.getOrThrow();
		} else if (!opResult.asCRUDError()
							.wasBecauseClientRequestedEntityWasNOTFound()) {
			opResult.asCRUDError().throwAsPersistenceException();
		}
		return outEntity;
	}
}
