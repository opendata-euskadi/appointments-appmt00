package aa14b.db.find;

import javax.persistence.EntityManager;

import aa14f.api.interfaces.AA14FindServicesBase;
import aa14f.model.AA14EntityModelObject;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.DBFindForModelObjectBase;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.entities.DBEntityForModelObject;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObject;

/**
 * Find layer
 */
abstract class AA14DBFindBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,M extends AA14EntityModelObject<O,ID>,
							  DB extends DBEntityForModelObject<DBPrimaryKeyForModelObject>>
	   extends DBFindForModelObjectBase<O,M,
	 								    DBPrimaryKeyForModelObject,DB>
    implements AA14FindServicesBase<O,ID,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBFindBase(final DBModuleConfig dbCfg,
						  final Class<M> modelObjectType,final Class<DB> dbEntityType,
		   				  final EntityManager entityManager,
		   				  final Marshaller marshaller) {
		super(dbCfg,
			  modelObjectType,dbEntityType,
			  entityManager,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
}
