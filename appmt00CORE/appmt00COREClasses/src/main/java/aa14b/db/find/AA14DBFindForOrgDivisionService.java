package aa14b.db.find;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import aa14b.db.entities.AA14DBEntityForOrgDivisionService;
import aa14b.db.entities.AA14DBEntityForOrganizationalEntityBase;
import aa14f.api.interfaces.AA14FindServicesForOrgDivisionService;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
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
public class AA14DBFindForOrgDivisionService
	 extends AA14DBFindForOrganizationalEntityBase<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID,AA14OrgDivisionService,
	 								 AA14DBEntityForOrgDivisionService>
  implements AA14FindServicesForOrgDivisionService {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBFindForOrgDivisionService(final DBModuleConfig dbCfg,	
										   final EntityManager entityManager,
										   final Marshaller marshaller) {
		super(dbCfg,
			  AA14OrgDivisionService.class,AA14DBEntityForOrgDivisionService.class,
			  entityManager,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<AA14OrgDivisionService> findByOrgDivision(final SecurityContext securityContext,
										   					 	final AA14OrgDivisionOID divisionOid) {
		log.debug("> loading services in division {}",divisionOid);

		TypedQuery<AA14DBEntityForOrgDivisionService> query = this.getEntityManager()
															      .createNamedQuery("AA14DBEntitiesForServicesByDivision",
																	 		        AA14DBEntityForOrgDivisionService.class)
																  .setParameter("division",divisionOid.asString());
		query.setHint(QueryHints.READ_ONLY,HintValues.TRUE);
		Collection<AA14DBEntityForOrgDivisionService> entities = query.getResultList();
		FindResult<AA14OrgDivisionService> outEntities = FindResultBuilder.using(securityContext)
															          	  .on(_modelObjectType)
															          	  .foundDBEntities(entities)
															          	  .transformedToModelObjectsUsing(this);
		return outEntities;
	}
	@Override
	public FindSummariesResult<AA14OrgDivisionService> findSummariesByOrgDivision(final SecurityContext securityContext,
																  				  final AA14OrgDivisionOID divisionOid,
																  				  final Language lang) {
		log.info("Find summaries for all services in division {}",divisionOid);
		// [1] - Do the query
		TypedQuery<AA14DBEntityForOrganizationalEntityBase> qry = this.getEntityManager()
																	  .createNamedQuery("AA14DBEntitiesForServicesByDivision",
																				 		AA14DBEntityForOrganizationalEntityBase.class)
																	  .setParameter("division",divisionOid.asString());
		qry.setHint(QueryHints.READ_ONLY,HintValues.TRUE);
		Collection<AA14DBEntityForOrganizationalEntityBase> dbEntities = qry.getResultList();
		
		// [2] - Transform to summarized model objects
		FindSummariesResult<AA14OrgDivisionService> outSummaries = null; 
		outSummaries = FindSummariesResultBuilder.using(securityContext)
												 .on(_modelObjectType)
												 .foundDBEntities(dbEntities)
												 .transformedToSummarizedModelObjectUsing(this.dbEntityToSummaryTransformFunction(lang));
		// [3] - Return
		return outSummaries;
	}
}
