package aa14b.services.delegates.persistence;

import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14b.db.crud.AA14DBCRUDForOrganization;
import aa14f.api.interfaces.AA14CRUDServicesForOrganization;
import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfigBuilder;
import r01f.securitycontext.SecurityContext;
import r01f.validation.ObjectValidationResult;

/**
 * Service layer delegated type for CRUD (Create/Read/Update/Delete) operations
 */
public class AA14CRUDServicesDelegateForOrganization
	 extends AA14CRUDServicesDelegateForOrganizationalEntityBase<AA14OrganizationOID,AA14OrganizationID,AA14Organization>
  implements AA14CRUDServicesForOrganization {

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14CRUDServicesDelegateForOrganization(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
												   final EntityManager entityManager,
											       final Marshaller marshaller,
				  			   		   	   	       final EventBus eventBus) {
		super(coreCfg,
			  AA14Organization.class,
			  entityManager,
			  new AA14DBCRUDForOrganization(DBModuleConfigBuilder.dbModuleConfigFrom(coreCfg),
					  						entityManager,
					  						marshaller),
			  eventBus);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PARAMS VALIDATION ON CREATION / UPDATE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ObjectValidationResult<AA14Organization> validateModelObjBeforeCreateOrUpdate(final SecurityContext securityContext,
																	 	  				 final PersistenceRequestedOperation requestedOp,
																	 	  				 final AA14Organization org) {
		return super.validateModelObjBeforeCreateOrUpdate(securityContext,
														  requestedOp,
														  org);
	}
}
