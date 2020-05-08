package aa14b.db.find;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import aa14b.db.entities.AA14DBEntityForOrgDivision;
import aa14b.db.entities.AA14DBEntityForOrganizationalEntityBase;
import aa14f.api.interfaces.AA14FindServicesForOrgDivision;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindResultBuilder;
import r01f.model.persistence.FindSummariesResult;
import r01f.model.persistence.FindSummariesResultBuilder;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.securitycontext.SecurityContext;

/**
 * Persistence layer
 */
@Slf4j
public class AA14DBFindForOrgDivision
	 extends AA14DBFindForOrganizationalEntityBase<AA14OrgDivisionOID,AA14OrgDivisionID,AA14OrgDivision,
	 								 AA14DBEntityForOrgDivision>
  implements AA14FindServicesForOrgDivision {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBFindForOrgDivision(final DBModuleConfig dbCfg,
									final EntityManager entityManager,
									final Marshaller marshaller) {
		super(dbCfg,
			  AA14OrgDivision.class,AA14DBEntityForOrgDivision.class,
			  entityManager,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14OrgDivision> findByOrganization(final SecurityContext securityContext,
											  		   final AA14OrganizationOID orgOid) {
		log.debug("> loading OrgDivisions in organization {}",orgOid);

		TypedQuery<AA14DBEntityForOrgDivision> query = this.getEntityManager()
													        .createNamedQuery("AA14DBEntitiesForDivisionsByOrganization",
															  		          AA14DBEntityForOrgDivision.class)
															.setParameter("org",orgOid.asString());
		query.setHint(QueryHints.READ_ONLY,HintValues.TRUE);
		Collection<AA14DBEntityForOrgDivision> entities = query.getResultList();

		FindResult<AA14OrgDivision> outEntities = FindResultBuilder.using(securityContext)
													          	    .on(_modelObjectType)
													          	    .foundDBEntities(entities)
													          	    .transformedToModelObjectsUsing(this);
		return outEntities;
	}
	@Override
	public FindSummariesResult<AA14OrgDivision> findSummariesByOrganization(final SecurityContext securityContext,
																		 final AA14OrganizationOID orgOid,
																		 final Language lang) {
		log.info("Find summaries for all OrgDivisions in an organization");
		// [1] - Do the query
		TypedQuery<AA14DBEntityForOrganizationalEntityBase> qry = this.getEntityManager()
																	  .createNamedQuery("AA14DBEntitiesForDivisionsByOrganization",
																				 		AA14DBEntityForOrganizationalEntityBase.class)
																	  .setParameter("org",orgOid.asString());
		qry.setHint(QueryHints.READ_ONLY,HintValues.TRUE);
		Collection<AA14DBEntityForOrganizationalEntityBase> dbEntities = qry.getResultList();
		
		// [2] - Transform to summarized model objects
		FindSummariesResult<AA14OrgDivision> outSummaries = null; 
		outSummaries = FindSummariesResultBuilder.using(securityContext)
												 .on(_modelObjectType)
												 .foundDBEntities(dbEntities)
												 .transformedToSummarizedModelObjectUsing(this.dbEntityToSummaryTransformFunction(lang));
		// [3] - Return
		return outSummaries;
	}
}
