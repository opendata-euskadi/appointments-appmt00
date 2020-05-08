package aa14b.db.crud;

import javax.persistence.EntityManager;

import aa14b.db.entities.AA14DBEntityForOrgDivision;
import aa14b.db.entities.AA14DBEntityForOrgDivisionService;
import aa14b.db.entities.AA14DBEntityForOrganization;
import aa14f.api.interfaces.AA14CRUDServicesForOrgDivisionService;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import r01f.model.persistence.PersistencePerformedOperation;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObjectImpl;
import r01f.securitycontext.SecurityContext;

/**
 * Persistence layer
 */
public class AA14DBCRUDForOrgDivisionService
	 extends AA14DBCRUDForOrganizationalEntityBase<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID,AA14OrgDivisionService,
	 				        AA14DBEntityForOrgDivisionService>
  implements AA14CRUDServicesForOrgDivisionService {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBCRUDForOrgDivisionService(final DBModuleConfig dbCfg,
										   final EntityManager entityManager,
										   final Marshaller marshaller) {
		super(dbCfg,
			  AA14OrgDivisionService.class,AA14DBEntityForOrgDivisionService.class,
			  entityManager,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setDBEntityFieldsFromModelObject(final SecurityContext securityContext, 
												 final AA14OrgDivisionService service,final AA14DBEntityForOrgDivisionService dbEntity) {
		super.setDBEntityFieldsFromModelObject(securityContext,
											   service,dbEntity);
		
		// org reference
		dbEntity.setOrganizationOid(service.getOrgRef().getOid().asString());
		dbEntity.setOrganizationId(service.getOrgRef().getId().asString());
		
		// division reference
		dbEntity.setOrgDivisionOid(service.getOrgDivisionRef().getOid().asString());		
		dbEntity.setOrgDivisionId(service.getOrgDivisionRef().getId().asString());
		
		// hierarchy level
		dbEntity.setHierarchyLevel(3);	// used to return ordered results when searching (see AA14DBSearcherForEntityModelObject)
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void completeDBEntityBeforeCreateOrUpdate(final SecurityContext securityContext,
													 final PersistencePerformedOperation performedOp, 
													 final AA14OrgDivisionService service,final AA14DBEntityForOrgDivisionService dbService) {
		// load the organization & division entities
		AA14DBEntityForOrganization dbOrg = this.getEntityManager().find(AA14DBEntityForOrganization.class,
																		 new DBPrimaryKeyForModelObjectImpl(dbService.getOrganizationOid()));
		AA14DBEntityForOrgDivision dbDivision = this.getEntityManager().find(AA14DBEntityForOrgDivision.class,
																	 		 new DBPrimaryKeyForModelObjectImpl(dbService.getOrgDivisionOid()));
		// set the dependencies
		dbService.setOrganization(dbOrg);
		dbService.setOrgDivision(dbDivision);	
		
		// setting the service's dependent objects (org /division), also modifies the later since it's a BI-DIRECTIONAL relation
		// ... so the entity manager MUST be refreshed in order to avoid an optimistic locking exception
		this.getEntityManager().refresh(dbOrg);
		this.getEntityManager().refresh(dbDivision);
	}
}
