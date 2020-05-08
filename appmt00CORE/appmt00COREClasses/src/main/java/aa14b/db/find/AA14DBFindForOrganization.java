package aa14b.db.find;

import java.util.Collection;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import aa14b.db.entities.AA14DBEntityForOrganization;
import aa14b.db.entities.AA14DBEntityForOrganizationalEntityBase;
import aa14f.api.interfaces.AA14FindServicesForOrganization;
import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.model.persistence.FindSummariesResult;
import r01f.model.persistence.FindSummariesResultBuilder;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.securitycontext.SecurityContext;

/**
 * Persistence layer
 */
@Slf4j
public class AA14DBFindForOrganization
	 extends AA14DBFindForOrganizationalEntityBase<AA14OrganizationOID,AA14OrganizationID,AA14Organization,
	 								 			   AA14DBEntityForOrganization>
  implements AA14FindServicesForOrganization {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14DBFindForOrganization(final DBModuleConfig dbCfg,
									 final EntityManager entityManager,
									 final Marshaller marshaller) {
		super(dbCfg,
			  AA14Organization.class,AA14DBEntityForOrganization.class,
			  entityManager,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindSummariesResult<AA14Organization> findSummaries(final SecurityContext securityContext,
															   final Language lang) {
		log.info("Find all organization's summaries");
		// [1] - Do the query
		TypedQuery<AA14DBEntityForOrganizationalEntityBase> qry = this.getEntityManager()
																	  .createNamedQuery("AA14DBEntitiesForOrganization",
																				 		AA14DBEntityForOrganizationalEntityBase.class);
		qry.setHint(QueryHints.READ_ONLY,HintValues.TRUE);
		Collection<AA14DBEntityForOrganizationalEntityBase> dbEntities = qry.getResultList();
		
		// [2] - Transform to summarized model objects
		FindSummariesResult<AA14Organization> outSummaries = null; 
		outSummaries = FindSummariesResultBuilder.using(securityContext)
											   .on(_modelObjectType)
											   .foundDBEntities(dbEntities)
											   .transformedToSummarizedModelObjectUsing(this.dbEntityToSummaryTransformFunction(lang));
		// [3] - Return
		return outSummaries;
	}
}
