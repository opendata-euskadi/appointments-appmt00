package aa14b.services.delegates.persistence;

import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14b.db.find.AA14DBFindForOrgDivision;
import aa14f.api.interfaces.AA14FindServicesForOrgDivision;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
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
public class AA14FindServicesDelegateForOrgDivision
	 extends AA14FindServicesDelegateForOrganizationalEntityBase<AA14OrgDivisionOID,AA14OrgDivisionID,AA14OrgDivision>
  implements AA14FindServicesForOrgDivision {

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14FindServicesDelegateForOrgDivision(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
												  final EntityManager entityManager,
										 		  final Marshaller marshaller,
  			   		   	   	      		 		  final EventBus eventBus) {
		super(coreCfg,
			  AA14OrgDivision.class,
			  new AA14DBFindForOrgDivision(DBModuleConfigBuilder.dbModuleConfigFrom(coreCfg),
					  					   entityManager,
					  					   marshaller));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14OrgDivision> findByOrganization(final SecurityContext securityContext,
													   	  final AA14OrganizationOID orgOid) {
		if (orgOid == null) return FindResultBuilder.using(securityContext)
													.on(_modelObjectType)
													.errorFindingEntities()
															.causedByClientBadRequest("The organization id is mandatory to find OrgDivisions by organization");
		return this.getServiceImplAs(AA14FindServicesForOrgDivision.class)
						.findByOrganization(securityContext,
											orgOid);
	}
	@Override
	public FindSummariesResult<AA14OrgDivision> findSummariesByOrganization(final SecurityContext securityContext,
																		 	final AA14OrganizationOID orgOid,
																		 	final Language lang) {
		if (orgOid == null) return FindSummariesResultBuilder.using(securityContext)
															 .on(_modelObjectType)
															 .errorFindingSummaries()
																	.causedByClientBadRequest("The organization id is mandatory to find OrgDivisions by organization");
		Language theLang = (lang != null && lang.in(Language.SPANISH,Language.BASQUE)) ? lang 
																					   : Language.SPANISH;
		return this.getServiceImplAs(AA14FindServicesForOrgDivision.class)
					.findSummariesByOrganization(securityContext,
												 orgOid,theLang);
	}

}
