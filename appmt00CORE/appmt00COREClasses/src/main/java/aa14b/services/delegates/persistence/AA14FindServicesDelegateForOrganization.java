package aa14b.services.delegates.persistence;

import javax.persistence.EntityManager;

import com.google.common.eventbus.EventBus;

import aa14b.db.find.AA14DBFindForOrganization;
import aa14f.api.interfaces.AA14FindServicesForOrganization;
import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.locale.Language;
import r01f.model.persistence.FindSummariesResult;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfigBuilder;
import r01f.securitycontext.SecurityContext;

/**
 * Service layer delegated type for CRUD (Create/Read/Update/Delete) operations
 */
public class AA14FindServicesDelegateForOrganization
	 extends AA14FindServicesDelegateForOrganizationalEntityBase<AA14OrganizationOID,AA14OrganizationID,AA14Organization>
  implements AA14FindServicesForOrganization {

/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14FindServicesDelegateForOrganization(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
												   final EntityManager entityManager,
											 	   final Marshaller marshaller,
	  			   		   	   	      		 	   final EventBus eventBus) {
		super(coreCfg,
			  AA14Organization.class,
			  new AA14DBFindForOrganization(DBModuleConfigBuilder.dbModuleConfigFrom(coreCfg),
					  						entityManager,
					  						marshaller));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindSummariesResult<AA14Organization> findSummaries(final SecurityContext securityContext,
															   final Language lang) {
		// params checking
		Language theLang = (lang != null && lang.in(Language.SPANISH,Language.BASQUE)) ? lang 
																					   : Language.SPANISH;
		// simply delegate
		return this.getServiceImplAs(AA14FindServicesForOrganization.class)
				   .findSummaries(securityContext,
						   		  theLang);
	}
}
