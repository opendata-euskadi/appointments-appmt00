package aa14b.services.delegates.persistence;

import com.google.common.eventbus.EventBus;

import aa14f.api.interfaces.AA14CRUDServicesBase;
import aa14f.model.AA14EntityModelObject;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.persistence.db.DBCRUDForModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.services.delegates.persistence.CRUDServicesForModelObjectDelegateBase;
import r01f.services.delegates.persistence.CompletesModelObjectBeforeCreateOrUpdate;
import r01f.services.delegates.persistence.ValidatesModelObjectBeforeCreateOrUpdate;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;

/**
 * Service layer delegated type for CRUD (Create/Read/Update/Delete) operations
 */
abstract class AA14CRUDServicesDelegateBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,M extends AA14EntityModelObject<O,ID>>
	   extends CRUDServicesForModelObjectDelegateBase<O,M>
    implements AA14CRUDServicesBase<O,ID,M>,
    		   ValidatesModelObjectBeforeCreateOrUpdate<M>,
  			   CompletesModelObjectBeforeCreateOrUpdate<M> {

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14CRUDServicesDelegateBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
										final Class<M> modelObjType,
									    final DBCRUDForModelObject<O,M> crud,
									    final EventBus eventBus) {
		super(coreCfg,
			  modelObjType,
			  crud,
			  eventBus);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDResult<M> loadById(final SecurityContext securityContext,
							      final ID id) {
		if (id == null) {
			return CRUDResultBuilder.using(securityContext)
								    .on(_modelObjectType)
								    .notLoaded()
						     		.becauseClientBadRequest("The entity id MUST not be null")
						     			.build();
		}
		CRUDResult<M> outResult = this.getServiceImplAs(AA14CRUDServicesBase.class)
											.loadById(securityContext,
													  id);
		return outResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PARAMS VALIDATION ON CREATION / UPDATE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public M completeModelObjBeforeCreateOrUpdate(final SecurityContext securityContext, 
												  final PersistenceRequestedOperation requestedOp,
												  final M modelObj) {
		return modelObj;
	}
	@Override
	public ObjectValidationResult<M> validateModelObjBeforeCreateOrUpdate(final SecurityContext securityContext,
																	      final PersistenceRequestedOperation requestedOp,
																	 	  final M entity) {
		ObjectValidationResult<M> outValid = null;

		// Validate the data
		if (entity.getOid() == null) {
			outValid = ObjectValidationResultBuilder.on(entity)
													.isNotValidBecause("The {} entity MUST have an oid",
														  			   entity.getClass().getSimpleName());
		}
		if (outValid == null
		 && entity.getId() == null) {
			outValid = ObjectValidationResultBuilder.on(entity)
												    .isNotValidBecause("The {} entity with oid={} has a NULL ID",
														 			   entity.getClass().getSimpleName(),entity.getOid());
		}
		// Validate that it does not exists another entity with the same id
		if (outValid == null
		 && requestedOp.is(PersistenceRequestedOperation.CREATE)) {
			CRUDResult<M> existingEntityByIdLoadResult = this.loadById(securityContext,
																       entity.getId());
			if (existingEntityByIdLoadResult.hasSucceeded()) {
				outValid = ObjectValidationResultBuilder.on(entity)
													    .isNotValidBecause("The {} entity with oid={} is NOT valid because there cannot exists two entities with the same id ({})",
															 			   entity.getClass().getSimpleName(),entity.getOid(),entity.getId());
			}
		}
		// no errors... it's valid
		if (outValid == null) {
			outValid = ObjectValidationResultBuilder.on(entity)
						   						 	.isValid();
		}
		return outValid;
	}


}
