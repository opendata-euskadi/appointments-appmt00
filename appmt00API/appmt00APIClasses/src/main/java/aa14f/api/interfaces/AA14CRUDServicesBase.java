package aa14f.api.interfaces;

import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.CRUDResult;
import r01f.securitycontext.SecurityContext;
import r01f.services.interfaces.CRUDServicesForModelObject;

public interface AA14CRUDServicesBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,
									  M extends PersistableModelObject<O>> 
         extends CRUDServicesForModelObject<O,M>,
         		 AA14ServiceInterface {
	/**
	 * Loads an entity by it's id
	 * @param id
	 * @return
	 */
	public CRUDResult<M> loadById(final SecurityContext securityContext,
								  final ID id);
	
}
