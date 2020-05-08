package aa14b.services.delegates.persistence;

import java.util.Collection;

import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14b.db.crud.AA14DBCRUDForOrgDivisionServiceLocation;
import aa14f.api.interfaces.AA14CRUDServicesForOrgDivisionServiceLocation;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.model.persistence.CRUDResult;
import r01f.model.persistence.CRUDResultBuilder;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfigBuilder;
import r01f.securitycontext.SecurityContext;
import r01f.util.types.collections.CollectionUtils;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;

/**
 * Service layer delegated type for CRUD (Create/Read/Update/Delete) operations
 */
public class AA14CRUDServicesDelegateForOrgDivisionServiceLocation
	 extends AA14CRUDServicesDelegateForOrganizationalEntityBase<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID,AA14OrgDivisionServiceLocation>
  implements AA14CRUDServicesForOrgDivisionServiceLocation {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14CRUDServicesDelegateForOrganization _orgCRUD;
	private final AA14CRUDServicesDelegateForOrgDivision _orgDivisionCRUD;
	private final AA14CRUDServicesDelegateForOrgDivisionService _orgDivisionServiceCRUD;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14CRUDServicesDelegateForOrgDivisionServiceLocation(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
																 final EntityManager entityManager,
														 		 final Marshaller marshaller,
				  			   		   	   	      		 		 final EventBus eventBus) {
		super(coreCfg,
			  AA14OrgDivisionServiceLocation.class,
			  entityManager,
			  new AA14DBCRUDForOrgDivisionServiceLocation(DBModuleConfigBuilder.dbModuleConfigFrom(coreCfg),
					  									  entityManager,
					  									  marshaller),
			  eventBus);
		_orgCRUD = new AA14CRUDServicesDelegateForOrganization(coreCfg,
															   entityManager,
															   marshaller,
															   eventBus);
		_orgDivisionCRUD =  new AA14CRUDServicesDelegateForOrgDivision(coreCfg,
																	   entityManager,
														   			   marshaller,
															   		   eventBus);
		_orgDivisionServiceCRUD = new AA14CRUDServicesDelegateForOrgDivisionService(coreCfg,
																					entityManager,
																	   			    marshaller,
																		   		    eventBus);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CRUDResult<AA14OrgDivisionServiceLocation> linkLocationToSchedules(final SecurityContext securityContext,
																			  final AA14OrgDivisionServiceLocationOID locOid,final Collection<AA14ScheduleOID> schOids) {
		// [0] - Check params
		if (locOid == null || CollectionUtils.isNullOrEmpty(schOids)) return CRUDResultBuilder.using(securityContext)
																							  .on(AA14OrgDivisionServiceLocation.class)
																							  .not(PersistenceRequestedOperation.UPDATE)
																							  .becauseClientBadRequest("location oid and schedule oids MUST NOT be null")
																							  .build();
		
		// [1] - Delegate
		return this.getServiceImplAs(AA14CRUDServicesForOrgDivisionServiceLocation.class)
						.linkLocationToSchedules(securityContext,
											     locOid,schOids);
	}
		
/////////////////////////////////////////////////////////////////////////////////////////
//  VALIDATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ObjectValidationResult<AA14OrgDivisionServiceLocation> validateModelObjBeforeCreateOrUpdate(final SecurityContext securityContext,
																							   		   final PersistenceRequestedOperation requestedOp,
																							   		   final AA14OrgDivisionServiceLocation loc) {
		// Ensure the hierarchy is correct
		if (loc.getOrgRef() == null || loc.getOrgDivisionRef() == null || loc.getOrgDivisionServiceRef() == null) { 
			return ObjectValidationResultBuilder.on(loc)
												.isNotValidBecause("The organization, division and service are mandatory to create a {}",AA14OrgDivisionServiceLocation.class.getSimpleName());
		}
		if (requestedOp.isIn(PersistenceRequestedOperation.CREATE,
							 PersistenceRequestedOperation.UPDATE)) {
			AA14OrganizationOID orgOid = loc.getOrgRef().getOid();
			AA14OrgDivisionOID divOid = loc.getOrgDivisionRef().getOid();
			AA14OrgDivisionServiceOID srvcOid = loc.getOrgDivisionServiceRef().getOid();
			
			// try to load the org, division & service by it's ids
			CRUDResult<AA14Organization> existingOrgByOidLoadResult = _orgCRUD.load(securityContext,
																			 	    orgOid);
			CRUDResult<AA14OrgDivision> existingDivByOidLoadResult = _orgDivisionCRUD.load(securityContext,
																				  		   divOid);
			CRUDResult<AA14OrgDivisionService> existingSrvcByOidLoadResult = _orgDivisionServiceCRUD.load(securityContext,
																				  		  				  srvcOid);
			
			if (existingOrgByOidLoadResult.hasFailed() || existingDivByOidLoadResult.hasFailed() || existingSrvcByOidLoadResult.hasFailed()) {
				return ObjectValidationResultBuilder.on(loc)
													 .isNotValidBecause("The {} with oid={} sets an INVALID orgOid={}, divisionOid={} or serviceOid={}",
															 			loc.getClass().getSimpleName(),loc.getOid(),
															 			orgOid,divOid,srvcOid);					
			} else if (existingOrgByOidLoadResult.getOrThrow().getId().isNOT(loc.getOrgRef().getId())
				    || existingDivByOidLoadResult.getOrThrow().getId().isNOT(loc.getOrgDivisionRef().getId())
				    || existingSrvcByOidLoadResult.getOrThrow().getId().isNOT(loc.getOrgDivisionServiceRef().getId())) {
				return ObjectValidationResultBuilder.on(loc)
													 .isNotValidBecause("The {} with oid={} sets an INVALID orgId={}, divisionId={} or serviceId={}",
															 			loc.getClass().getSimpleName(),loc.getOid(),
															 			loc.getOrgRef().getId(),loc.getOrgDivisionRef().getId(),loc.getOrgDivisionServiceRef().getId());	
			}
		}
		return super.validateModelObjBeforeCreateOrUpdate(securityContext,
														  requestedOp,
														  loc);
	}	
}
