package aa14f.api.interfaces;

import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.model.PersistableModelObject;

public interface AA14CRUDServicesForOrganizationalEntityBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,
									  						 M extends PersistableModelObject<O>> 
         extends AA14CRUDServicesBase<O,ID,M> {
	// nothing
}
