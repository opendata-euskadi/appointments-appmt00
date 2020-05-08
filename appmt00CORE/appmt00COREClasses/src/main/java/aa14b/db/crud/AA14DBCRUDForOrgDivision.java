package aa14b.db.crud;

import javax.persistence.EntityManager;

import aa14b.db.entities.AA14DBEntityForOrgDivision;
import aa14b.db.entities.AA14DBEntityForOrganization;
import aa14f.api.interfaces.AA14CRUDServicesForOrgDivision;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import r01f.model.persistence.PersistencePerformedOperation;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObjectImpl;
import r01f.securitycontext.SecurityContext;

/**
 * Persistence layer
 */
public class AA14DBCRUDForOrgDivision
	 extends AA14DBCRUDForOrganizationalEntityBase<AA14OrgDivisionOID,AA14OrgDivisionID,AA14OrgDivision,
	 											   AA14DBEntityForOrgDivision>
  implements AA14CRUDServicesForOrgDivision {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBCRUDForOrgDivision(final DBModuleConfig dbCfg,
									final EntityManager entityManager,
									final Marshaller marshaller) {
		super(dbCfg,
			  AA14OrgDivision.class,AA14DBEntityForOrgDivision.class,
			  entityManager,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setDBEntityFieldsFromModelObject(final SecurityContext securityContext, 
												 final AA14OrgDivision div,final AA14DBEntityForOrgDivision dbEntity) {
		super.setDBEntityFieldsFromModelObject(securityContext, 
											   div,dbEntity);
		
		// Organization reference
		dbEntity.setOrganizationOid(div.getOrgRef().getOid().asString());
		dbEntity.setOrganizationId(div.getOrgRef().getId().asString());
		
		// hierarchy level
		dbEntity.setHierarchyLevel(2);	// used to return ordered results when searching (see AA14DBSearcherForEntityModelObject)
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void completeDBEntityBeforeCreateOrUpdate(final SecurityContext securityContext,
													 final PersistencePerformedOperation performedOp, 
													 final AA14OrgDivision ordDiv,final AA14DBEntityForOrgDivision dbDivision) {
		// load the organization entity
		AA14DBEntityForOrganization dbOrg = this.getEntityManager().find(AA14DBEntityForOrganization.class,
																		 new DBPrimaryKeyForModelObjectImpl(dbDivision.getOrganizationOid()));
		// set the dependency
		dbDivision.setOrganization(dbOrg);
		
		// setting the division's dependent objects (org), also modifies the later since it's a BI-DIRECTIONAL relation
		// ... so the entity manager MUST be refreshed in order to avoid an optimistic locking exception
		this.getEntityManager().refresh(dbOrg);
	}
}
