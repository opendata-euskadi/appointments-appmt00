package aa14b.services.delegates.persistence;

import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14b.db.find.AA14DBFindForOrgDivisionServiceLocation;
import aa14f.api.interfaces.AA14FindServicesForOrgDivisionServiceLocation;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
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
public class AA14FindServicesDelegateForOrgDivisionServiceLocation
	 extends AA14FindServicesDelegateForOrganizationalEntityBase<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID,AA14OrgDivisionServiceLocation>
  implements AA14FindServicesForOrgDivisionServiceLocation {

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14FindServicesDelegateForOrgDivisionServiceLocation(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
																 final EntityManager entityManager,
														 		 final Marshaller marshaller,
				  			   		   	   	      		 		 final EventBus eventBus) {
		super(coreCfg,
			  AA14OrgDivisionServiceLocation.class,
			  new AA14DBFindForOrgDivisionServiceLocation(DBModuleConfigBuilder.dbModuleConfigFrom(coreCfg),
					  									  entityManager,
					  									  marshaller));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14OrgDivisionServiceLocation> findByOrgDivisionService(final SecurityContext securityContext,
															  				   final AA14OrgDivisionServiceOID serviceOid) {
		if (serviceOid == null) return FindResultBuilder.using(securityContext)
															.on(_modelObjectType)
															.errorFindingEntities()
																	.causedByClientBadRequest("The service id is mandatory to find locations by service");
		return this.getServiceImplAs(AA14FindServicesForOrgDivisionServiceLocation.class)
						.findByOrgDivisionService(securityContext,
										   		  serviceOid);
	}
	@Override
	public FindSummariesResult<AA14OrgDivisionServiceLocation> findSummariesByOrgDivisionService(final SecurityContext securityContext,
																  				  		 		 final AA14OrgDivisionServiceOID serviceOid,
																  				  		 		 final Language lang) {
		if (serviceOid == null) return FindSummariesResultBuilder.using(securityContext)
																	 .on(_modelObjectType)
																	 .errorFindingSummaries()
																				.causedByClientBadRequest("The service id is mandatory to find locations by service");
		Language theLang = (lang != null && lang.in(Language.SPANISH,Language.BASQUE)) ? lang 
																					   : Language.SPANISH;
		return this.getServiceImplAs(AA14FindServicesForOrgDivisionServiceLocation.class)
						.findSummariesByOrgDivisionService(securityContext,
										         		   serviceOid,theLang);
	}

}
