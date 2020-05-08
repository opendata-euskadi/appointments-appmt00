package aa14b.db.crud;

import javax.persistence.EntityManager;

import aa14f.api.interfaces.AA14CRUDServicesBase;
import aa14f.model.AA14EntityModelObject;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.CompletesDBEntityBeforeCreateOrUpdate;
import r01f.persistence.db.DBCRUDForModelObjectBase;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.entities.DBEntityForModelObject;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObject;

/**
 * Persistence layer
 */
abstract class AA14DBCRUDBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,M extends AA14EntityModelObject<O,ID>,
							  DB extends DBEntityForModelObject<DBPrimaryKeyForModelObject>>
	   extends DBCRUDForModelObjectBase<O,M,
	 								    DBPrimaryKeyForModelObject,DB>
    implements CompletesDBEntityBeforeCreateOrUpdate<M,DB>,
    		   AA14CRUDServicesBase<O,ID,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBCRUDBase(final DBModuleConfig dbCfg,
						  final Class<M> modelObjectType,final Class<DB> dbEntityType,
						  final EntityManager entityManager,
						  final Marshaller marshaller) {
		super(dbCfg,
			  modelObjectType,dbEntityType,
			  entityManager,
			  marshaller);
	}
}
