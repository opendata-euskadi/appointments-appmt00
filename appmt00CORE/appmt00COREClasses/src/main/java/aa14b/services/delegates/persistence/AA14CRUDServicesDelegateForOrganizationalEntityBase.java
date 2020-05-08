package aa14b.services.delegates.persistence;

import java.util.Date;

import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14f.api.interfaces.AA14BusinessConfigServices;
import aa14f.api.interfaces.AA14CRUDServicesForOrganizationalEntityBase;
import aa14f.model.config.AA14OrganizationalModelObject;
import aa14f.model.config.AA14OrganizationalModelObjectBase;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.persistence.db.DBCRUDForModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.services.callback.spec.COREServiceMethodCallbackSpec;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;

/**
 * Service layer delegated type for CRUD (Create/Read/Update/Delete) operations
 */
abstract class AA14CRUDServicesDelegateForOrganizationalEntityBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,M extends AA14OrganizationalModelObject<O,ID>>
	   extends AA14CRUDServicesDelegateBase<O,ID,M>
    implements AA14CRUDServicesForOrganizationalEntityBase<O,ID,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final EntityManager _entityManager;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14CRUDServicesDelegateForOrganizationalEntityBase(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
															   final Class<M> modelObjType,
															   final EntityManager entityManager,
									    					   final DBCRUDForModelObject<O,M> crud,
									    					   final EventBus eventBus) {
		super(coreCfg,
			  modelObjType,
			  crud,
			  eventBus);
		_entityManager = entityManager;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	OVERRIDE DB MUTATOR METHDOS TO UPDATE THE CONFIG LAST-UPDATE DATE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected CRUDResult<M> doUpdateOrCreate(final SecurityContext securityContext,
											 final M modelObj,
											 final PersistenceRequestedOperation requestedOperation, 
											 final COREServiceMethodCallbackSpec callbackSpec) {
		CRUDResult<M> outResult = super.doUpdateOrCreate(securityContext,
														 modelObj, 
														 requestedOperation, 
														 callbackSpec);
		if (outResult.hasSucceeded()) {
			// update the last-update timestamp
			AA14BusinessConfigServices cfgServices = new AA14BusinessConfigServicesDelegate(_entityManager);
			cfgServices.updateLastUpdateDate(securityContext,
											 new Date());
		}
		return outResult;
	}

	@Override
	public CRUDResult<M> delete(final SecurityContext securityContext, 
							    final O oid,
								final COREServiceMethodCallbackSpec callbackSpec) {
		// TODO Auto-generated method stub
		CRUDResult<M> outResult = super.delete(securityContext, 
											   oid, 
											   callbackSpec);
		if (outResult.hasSucceeded()) {
			// update the last-update timestamp
			AA14BusinessConfigServices cfgServices = new AA14BusinessConfigServicesDelegate(_entityManager);
			cfgServices.updateLastUpdateDate(securityContext,
											 new Date());
		}
		return outResult;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PARAMS VALIDATION ON CREATION / UPDATE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public ObjectValidationResult<M> validateModelObjBeforeCreateOrUpdate(final SecurityContext securityContext,
																	 	  final PersistenceRequestedOperation requestedOp,
																	 	  final M entity) {
		// Validate the model object
		ObjectValidationResult<?> modelObjectValidation = ((AA14OrganizationalModelObjectBase<?,?,?>)entity).validate();
		if (modelObjectValidation.isNOTValid()) {
			return (ObjectValidationResult<M>)modelObjectValidation;
		}
		// Validate that it does not exists another entity with the same id
		if (requestedOp.is(PersistenceRequestedOperation.CREATE)) {
			CRUDResult<M> existingEntityByIdLoadResult = this.loadById(securityContext,
																       entity.getId());
			if (existingEntityByIdLoadResult.hasSucceeded()) {
				return ObjectValidationResultBuilder.on(entity)
												    .isNotValidBecause("The {} org entity with oid={} is NOT valid because there cannot exists two org entities of type {} with the same id ({})",
														 			   _modelObjectType.getSimpleName(),entity.getOid(),_modelObjectType.getSimpleName(),entity.getId());
			}
		}
		return super.validateModelObjBeforeCreateOrUpdate(securityContext,
													      requestedOp,
														  entity);
	}
}
