package aa14f.api.interfaces;

import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.model.PersistableModelObject;
import r01f.services.interfaces.FindServicesForModelObject;

public interface AA14FindServicesBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,
									  M extends PersistableModelObject<O>> 
         extends FindServicesForModelObject<O,M>,
				 AA14ServiceInterface {
	// nothing
}
