package aa14b.db.find;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import aa14b.db.entities.AA14DBEntityForOrgDivisionServiceLocation;
import aa14b.db.entities.AA14DBEntityForOrganizationalEntityBase;
import aa14f.api.interfaces.AA14FindServicesForOrgDivisionServiceLocation;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
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
public class AA14DBFindForOrgDivisionServiceLocation
	 extends AA14DBFindForOrganizationalEntityBase<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID,AA14OrgDivisionServiceLocation,
	 								 AA14DBEntityForOrgDivisionServiceLocation>
  implements AA14FindServicesForOrgDivisionServiceLocation {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBFindForOrgDivisionServiceLocation(final DBModuleConfig dbCfg,
												   final EntityManager entityManager,
												   final Marshaller marshaller) {
		super(dbCfg,
			  AA14OrgDivisionServiceLocation.class,AA14DBEntityForOrgDivisionServiceLocation.class,
			  entityManager,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14OrgDivisionServiceLocation> findByOrgDivisionService(final SecurityContext securityContext,
										   					    			   final AA14OrgDivisionServiceOID serviceOid) {
		log.debug("> loading locations in service {}",serviceOid);

		TypedQuery<AA14DBEntityForOrgDivisionServiceLocation> query = this.getEntityManager()
																	        .createNamedQuery("AA14DBEntitiesForLocationsByService",
																			  		          AA14DBEntityForOrgDivisionServiceLocation.class)
																			.setParameter("service",serviceOid.asString());
		query.setHint(QueryHints.READ_ONLY,HintValues.TRUE);
		Collection<AA14DBEntityForOrgDivisionServiceLocation> entities = query.getResultList();
		FindResult<AA14OrgDivisionServiceLocation> outEntities = FindResultBuilder.using(securityContext)
																	          	  .on(_modelObjectType)
																	          	  .foundDBEntities(entities)
																	          	  .transformedToModelObjectsUsing(this);
		return outEntities;
	}
	@Override
	public FindSummariesResult<AA14OrgDivisionServiceLocation> findSummariesByOrgDivisionService(final SecurityContext securityContext,
																  								 final AA14OrgDivisionServiceOID serviceOid,
																  								 final Language lang) {
		log.info("Find summaries for all locations in service {}",serviceOid);
		// [1] - Do the query
		TypedQuery<AA14DBEntityForOrganizationalEntityBase> qry = this.getEntityManager()
																	  .createNamedQuery("AA14DBEntitiesForLocationsByService",
																				 		AA14DBEntityForOrganizationalEntityBase.class)
																	  .setParameter("service",serviceOid.asString());
		qry.setHint(QueryHints.READ_ONLY,HintValues.TRUE);
		Collection<AA14DBEntityForOrganizationalEntityBase> dbEntities = qry.getResultList();
		
		// [2] - Transform to summarized model objects
		FindSummariesResult<AA14OrgDivisionServiceLocation> outSummaries = null; 
		outSummaries = FindSummariesResultBuilder.using(securityContext)
												 .on(_modelObjectType)
												 .foundDBEntities(dbEntities)
												 .transformedToSummarizedModelObjectUsing(this.dbEntityToSummaryTransformFunction(lang));
		// [3] - Return
		return outSummaries;
	}
}
