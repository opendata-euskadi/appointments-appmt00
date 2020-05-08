package aa14b.services.delegates.persistence;

import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14b.db.crud.AA14DBCRUDForOrgDivision;
import aa14f.api.interfaces.AA14CRUDServicesForOrgDivision;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
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
public class AA14CRUDServicesDelegateForOrgDivision
	 extends AA14CRUDServicesDelegateForOrganizationalEntityBase<AA14OrgDivisionOID,AA14OrgDivisionID,AA14OrgDivision>
  implements AA14CRUDServicesForOrgDivision {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14CRUDServicesDelegateForOrganization _orgCRUD;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14CRUDServicesDelegateForOrgDivision(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
												  final EntityManager entityManager,
											      final Marshaller marshaller,
				  			   		   	   	      final EventBus eventBus) {
		super(coreCfg,
			  AA14OrgDivision.class,
			  entityManager,
			  new AA14DBCRUDForOrgDivision(DBModuleConfigBuilder.dbModuleConfigFrom(coreCfg),
					  					   entityManager,
					  					   marshaller),
			  eventBus);
		_orgCRUD = new AA14CRUDServicesDelegateForOrganization(coreCfg,
															   entityManager,
															   marshaller,
															   eventBus);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  VALIDATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ObjectValidationResult<AA14OrgDivision> validateModelObjBeforeCreateOrUpdate(final SecurityContext securityContext,
																						final PersistenceRequestedOperation requestedOp,
																						final AA14OrgDivision div) {
		if (div.getOrgRef() == null) { 
			return ObjectValidationResultBuilder.on(div)
												.isNotValidBecause("The organization is mandatory to create a {}",AA14OrgDivision.class.getSimpleName());
		}
		if (requestedOp.isIn(PersistenceRequestedOperation.CREATE,
							 PersistenceRequestedOperation.UPDATE)) {
			AA14OrganizationOID orgOid = div.getOrgRef().getOid();
			
			// try to load the org by it's oid
			CRUDResult<AA14Organization> existingOrgByOidLoadResult = _orgCRUD.load(securityContext,
																			 	    orgOid);
			if (existingOrgByOidLoadResult.hasFailed()) {
				return ObjectValidationResultBuilder.on(div)
													 .isNotValidBecause("The {} with oid={} sets an INVALID (not existing) org oid={}",
															 			div.getClass().getSimpleName(),div.getOid(),
															 			orgOid);
			} else if (existingOrgByOidLoadResult.getOrThrow().getId().isNOT(div.getOrgRef().getId())) {
				return ObjectValidationResultBuilder.on(div)
													.isNotValidBecause("The {} with oid={} sets an INVALID (not existing) org id={}",
																	   div.getClass().getSimpleName(),div.getOid(),
																	   div.getOrgRef().getId());
			}
		}
		return super.validateModelObjBeforeCreateOrUpdate(securityContext, 
														  requestedOp,
														  div);
	}
}
