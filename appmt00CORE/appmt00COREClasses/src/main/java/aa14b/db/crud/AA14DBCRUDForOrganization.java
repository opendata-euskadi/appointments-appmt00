package aa14b.db.crud;

import javax.persistence.EntityManager;

import aa14b.db.entities.AA14DBEntityForOrganization;
import aa14f.api.interfaces.AA14CRUDServicesForOrganization;
import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import r01f.model.persistence.PersistencePerformedOperation;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.securitycontext.SecurityContext;

/**
 * Persistence layer
 */
public class AA14DBCRUDForOrganization
	 extends AA14DBCRUDForOrganizationalEntityBase<AA14OrganizationOID,AA14OrganizationID,AA14Organization,
	 											   AA14DBEntityForOrganization>
  implements AA14CRUDServicesForOrganization {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBCRUDForOrganization(final DBModuleConfig dbCfg,
									 final EntityManager entityManager,
									 final Marshaller marshaller) {
		super(dbCfg,
			  AA14Organization.class,AA14DBEntityForOrganization.class,
			  entityManager,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setDBEntityFieldsFromModelObject(final SecurityContext securityContext,
												 final AA14Organization org,final AA14DBEntityForOrganization dbEntity) {
		super.setDBEntityFieldsFromModelObject(securityContext, 
											   org,dbEntity);
		// hierarchy level
		dbEntity.setHierarchyLevel(1);	// used to return ordered results when searching (see AA14DBSearcherForEntityModelObject)
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void completeDBEntityBeforeCreateOrUpdate(final SecurityContext securityContext,
													 final PersistencePerformedOperation performedOp, 
													 final AA14Organization org,final AA14DBEntityForOrganization dbEntity) {
		// nothing
	}
}
