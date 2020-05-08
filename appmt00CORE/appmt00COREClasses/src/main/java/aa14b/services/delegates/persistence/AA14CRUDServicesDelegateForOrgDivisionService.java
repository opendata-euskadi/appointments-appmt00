package aa14b.services.delegates.persistence;

import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14b.db.crud.AA14DBCRUDForOrgDivisionService;
import aa14f.api.interfaces.AA14CRUDServicesForOrgDivisionService;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfigBuilder;
import r01f.securitycontext.SecurityContext;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;

/**
 * Service layer delegated type for CRUD (Create/Read/Update/Delete) operations
 */
public class AA14CRUDServicesDelegateForOrgDivisionService
	 extends AA14CRUDServicesDelegateForOrganizationalEntityBase<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID,AA14OrgDivisionService>
  implements AA14CRUDServicesForOrgDivisionService {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14CRUDServicesDelegateForOrganization _orgCRUD;
	private final AA14CRUDServicesDelegateForOrgDivision _orgDivisionCRUD;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14CRUDServicesDelegateForOrgDivisionService(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
														 final EntityManager entityManager,
														 final Marshaller marshaller,
				  			   		   	   	      		 final EventBus eventBus) {
		super(coreCfg,
			  AA14OrgDivisionService.class,
			  entityManager,
			  new AA14DBCRUDForOrgDivisionService(DBModuleConfigBuilder.dbModuleConfigFrom(coreCfg),
					  							  entityManager,
					  							  marshaller),
			  eventBus);
		 _orgCRUD = new AA14CRUDServicesDelegateForOrganization(coreCfg,
				 												entityManager,
				 												marshaller,
				 												eventBus);
		 _orgDivisionCRUD = new AA14CRUDServicesDelegateForOrgDivision(coreCfg,
				 													   entityManager,
						 											   marshaller,
						 											   eventBus);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  VALIDATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ObjectValidationResult<AA14OrgDivisionService> validateModelObjBeforeCreateOrUpdate(final SecurityContext securityContext,
																							   final PersistenceRequestedOperation requestedOp,
																							   final AA14OrgDivisionService srvc) {
		// Ensure the hierarchy is correct
		if (srvc.getOrgRef() == null || srvc.getOrgDivisionRef() == null) { 
			return ObjectValidationResultBuilder.on(srvc)
												.isNotValidBecause("The organization and division are mandatory to create a {}",AA14OrgDivisionService.class.getSimpleName());
		}
		if (requestedOp.isIn(PersistenceRequestedOperation.CREATE,
							 PersistenceRequestedOperation.UPDATE)) {
			AA14OrganizationOID orgOid = srvc.getOrgRef().getOid();
			AA14OrgDivisionOID divOid = srvc.getOrgDivisionRef().getOid();
			
			// try to load the org & division by it's ids
			CRUDResult<AA14Organization> existingOrgByOidLoadResult = _orgCRUD.load(securityContext,
																			 	   orgOid);
			
			CRUDResult<AA14OrgDivision> existingDivByOidLoadResult = _orgDivisionCRUD.load(securityContext,
																				  		   divOid);
			if (existingOrgByOidLoadResult.hasFailed() || existingDivByOidLoadResult.hasFailed()) {
				return ObjectValidationResultBuilder.on(srvc)
													 .isNotValidBecause("The {} with oid={} sets an INVALID orgOid={} or divisionOid={}",
															 			srvc.getClass().getSimpleName(),srvc.getOid(),
															 			orgOid,divOid);					
			} else if (existingOrgByOidLoadResult.getOrThrow().getId().isNOT(srvc.getOrgRef().getId())
					|| existingDivByOidLoadResult.getOrThrow().getId().isNOT(srvc.getOrgDivisionRef().getId())) {
				return ObjectValidationResultBuilder.on(srvc)
													 .isNotValidBecause("The {} with oid={} sets an INVALID orgId={} or divisionId={}",
															 			srvc.getClass().getSimpleName(),srvc.getOid(),
															 			srvc.getOrgRef().getId(),srvc.getOrgDivisionRef().getId());	
			}
		}
		return super.validateModelObjBeforeCreateOrUpdate(securityContext, 
														  requestedOp,
														  srvc);
	}
}
