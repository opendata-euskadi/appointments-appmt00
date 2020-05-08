package aa14b.db.crud;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import aa14b.db.entities.AA14DBEntityForOrganizationalEntityBase;
import aa14f.api.interfaces.AA14CRUDServicesForOrganizationalEntityBase;
import aa14f.model.config.AA14OrganizationalModelObject;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.locale.Language;
import r01f.model.persistence.CRUDResult;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.securitycontext.SecurityContext;

/**
 * Persistence layer
 */
abstract class AA14DBCRUDForOrganizationalEntityBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,M extends AA14OrganizationalModelObject<O,ID>,
							  						 DB extends AA14DBEntityForOrganizationalEntityBase>
	   extends AA14DBCRUDBase<O,ID,M,
	 						  DB>
	implements AA14CRUDServicesForOrganizationalEntityBase<O,ID,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBCRUDForOrganizationalEntityBase(final DBModuleConfig dbCfg,
												 final Class<M> modelObjectType,final Class<DB> dbEntityType,
								   				 final EntityManager entityManager,
								   				 final Marshaller marshaller) {
		super(dbCfg,
			  modelObjectType,dbEntityType,
			  entityManager,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setDBEntityFieldsFromModelObject(final SecurityContext securityContext,
												 final M modelObj,final DB dbEntity) {
		// Oid
		dbEntity.setOid(modelObj.getOid().asString());
		dbEntity.setId(modelObj.getId().asString());
		
		// business id
		dbEntity.setBusinessId(modelObj.getBusinessId().asString());
		
		// Name
		if (modelObj.getName() != null) {
			dbEntity.setNameSpanish(modelObj.getNameByLanguage().getFor(Language.SPANISH));
			dbEntity.setNameBasque(modelObj.getNameByLanguage().getFor(Language.BASQUE));
		}
		
		// Descriptor
		dbEntity.setDescriptor(_modelObjectsMarshaller.forWriting().toXml(modelObj));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDResult<M> loadById(final SecurityContext securityContext,
								  final ID id) {
		// Do the query
		TypedQuery<DB> query = this.getEntityManager()
									    .createNamedQuery("AA14DBOrganizationalEntityById",
											  		      _DBEntityType)
										.setParameter("dbType",_DBEntityType)
										.setParameter("id",id.asString());
		Collection<DB> dbEntities = query.getResultList();

		// Return
		CRUDResult<M> outResult = _crudResultForSingleEntity(securityContext,
															 id,
															 dbEntities);
		return outResult;
	}
}
