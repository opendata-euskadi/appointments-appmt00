package aa14b.services.delegates.persistence;

import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14b.db.find.AA14DBFindForOrgDivisionService;
import aa14f.api.interfaces.AA14FindServicesForOrgDivisionService;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.locale.Language;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindResultBuilder;
import r01f.model.persistence.FindSummariesResult;
import r01f.model.persistence.FindSummariesResultBuilder;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfigBuilder;
import r01f.securitycontext.SecurityContext;

/**
 * Service layer delegated type for CRUD (Create/Read/Update/Delete) operations
 */
public class AA14FindServicesDelegateForOrgDivisionService
	 extends AA14FindServicesDelegateForOrganizationalEntityBase<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID,AA14OrgDivisionService>
  implements AA14FindServicesForOrgDivisionService {

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14FindServicesDelegateForOrgDivisionService(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
														 final EntityManager entityManager,
												 		 final Marshaller marshaller,
		  			   		   	   	      		 		 final EventBus eventBus) {
		super(coreCfg,
			  AA14OrgDivisionService.class,
			  new AA14DBFindForOrgDivisionService(DBModuleConfigBuilder.dbModuleConfigFrom(coreCfg),
					  							  entityManager,
					  							  marshaller));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14OrgDivisionService> findByOrgDivision(final SecurityContext securityContext,
															  	final AA14OrgDivisionOID divisionOid) {
		if (divisionOid == null) return FindResultBuilder.using(securityContext)
															.on(_modelObjectType)
															.errorFindingEntities()
																	.causedByClientBadRequest("The division id is mandatory to find services by division");
		return this.getServiceImplAs(AA14FindServicesForOrgDivisionService.class)
						.findByOrgDivision(securityContext,
										   divisionOid);
	}
	@Override
	public FindSummariesResult<AA14OrgDivisionService> findSummariesByOrgDivision(final SecurityContext securityContext,
																  				  final AA14OrgDivisionOID divisionOid,
																  				  final Language lang) {
		if (divisionOid == null) return FindSummariesResultBuilder.using(securityContext)
																	 .on(_modelObjectType)
																	 .errorFindingSummaries()
																				.causedByClientBadRequest("The division id is mandatory to find services by division");
		Language theLang = (lang != null && lang.in(Language.SPANISH,Language.BASQUE)) ? lang 
																					   : Language.SPANISH;
		return this.getServiceImplAs(AA14FindServicesForOrgDivisionService.class)
						.findSummariesByOrgDivision(securityContext,
										         	divisionOid,theLang);
	}

}
