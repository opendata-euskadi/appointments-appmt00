package aa14b.db.search;

import com.google.common.base.Function;

import aa14b.db.entities.AA14DBEntityForOrgDivision;
import aa14b.db.entities.AA14DBEntityForOrgDivisionService;
import aa14b.db.entities.AA14DBEntityForOrgDivisionServiceLocation;
import aa14b.db.entities.AA14DBEntityForOrganization;
import aa14b.db.entities.AA14DBEntityForOrganizationalEntityBase;
import aa14f.api.cache.AA14BusinessConfigCache;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14OrganizationalModelObjectBase;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.search.AA14SearchResultItemForOrganizationalEntity;
import aa14f.model.summaries.AA14SummarizedModelObject;
import aa14f.model.summaries.AA14SummarizedOrgDivision;
import aa14f.model.summaries.AA14SummarizedOrgDivisionService;
import aa14f.model.summaries.AA14SummarizedOrgDivisionServiceLocation;
import aa14f.model.summaries.AA14SummarizedOrganization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.search.db.TransformsDBEntityToSearchResultItem;
import r01f.securitycontext.SecurityContext;

@Slf4j
@RequiredArgsConstructor
public class AA14DBOrganizationalEntityToSearchResultItemTransformer
  implements TransformsDBEntityToSearchResultItem<AA14DBEntityForOrganizationalEntityBase,AA14SearchResultItemForOrganizationalEntity> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Marshaller _marshaller;
	
//	private final AA14CRUDServicesForOrganization _orgCRUD;
//	private final AA14CRUDServicesForOrgDivision _orgDivCRUD;
//	private final AA14CRUDServicesForOrgDivisionService _orgDivSrvcCRUD;
//	private final AA14CRUDServicesForOrgDivisionServiceLocation _orgDivSrvcLocCRUD;
	
	private final AA14BusinessConfigCache _configCache;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns this transformer as a {@link Function}
	 * @param securityContext
	 * @return
	 */
	public Function<AA14DBEntityForOrganizationalEntityBase,
					AA14SearchResultItemForOrganizationalEntity> asTransformFuncion(final SecurityContext securityContext,
															 final Language uiLang) {
		return new Function<AA14DBEntityForOrganizationalEntityBase,
				 			AA14SearchResultItemForOrganizationalEntity>() {			
						@Override
						public AA14SearchResultItemForOrganizationalEntity apply(final AA14DBEntityForOrganizationalEntityBase dbEntity) {
							return AA14DBOrganizationalEntityToSearchResultItemTransformer.this.dbEntityToSearchResultItem(securityContext,
																		  									 dbEntity,
																		  									 uiLang);
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AA14SearchResultItemForOrganizationalEntity dbEntityToSearchResultItem(final SecurityContext securityContext,
														   						  final AA14DBEntityForOrganizationalEntityBase dbEntity,
														   						  final Language uiLang) {
		AA14SearchResultItemForOrganizationalEntity outItem = null;

		// [1] - object type & summaries
		if (dbEntity instanceof AA14DBEntityForOrganization) {
			outItem = new AA14SearchResultItemForOrganizationalEntity(AA14Organization.class);
			outItem.setOrganization(_orgSummaryIn(securityContext,
												  dbEntity,uiLang));
		}
		else if (dbEntity instanceof AA14DBEntityForOrgDivision) {
			outItem = new AA14SearchResultItemForOrganizationalEntity(AA14OrgDivision.class);
			outItem.setOrganization(_orgSummaryIn(securityContext,
												  dbEntity,uiLang));
			outItem.setOrgDivision(_divisionSummaryIn(securityContext,
													  dbEntity,uiLang));
		}
		else if (dbEntity instanceof AA14DBEntityForOrgDivisionService) {
			outItem = new AA14SearchResultItemForOrganizationalEntity(AA14OrgDivisionService.class);
			outItem.setOrganization(_orgSummaryIn(securityContext,
												  dbEntity,uiLang));
			outItem.setOrgDivision(_divisionSummaryIn(securityContext,
													  dbEntity,uiLang));
			outItem.setOrgDivisionService(_serviceSummaryIn(securityContext,
															dbEntity,uiLang));
		}
		else if (dbEntity instanceof AA14DBEntityForOrgDivisionServiceLocation) {
			outItem = new AA14SearchResultItemForOrganizationalEntity(AA14OrgDivisionServiceLocation.class);
			outItem.setOrganization(_orgSummaryIn(securityContext,
												  dbEntity,uiLang));
			outItem.setOrgDivision(_divisionSummaryIn(securityContext,
													  dbEntity,uiLang));
			outItem.setOrgDivisionService(_serviceSummaryIn(securityContext,
															dbEntity,uiLang));
			outItem.setOrgDivisionServiceLocation(_locationSummaryIn(securityContext,
																	 dbEntity,uiLang));
		} 
		else {
			throw new IllegalArgumentException(dbEntity.getClass() + " is NOT a supported entity type");
		}
		// [2] - Name (it's also at the summary for the object type)
		outItem.setName(dbEntity.getName());

		return outItem;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private AA14SummarizedOrganization _orgSummaryIn(final SecurityContext securityContext,
													 final AA14DBEntityForOrganizationalEntityBase dbEntity,
										 			 final Language lang) {
		AA14Organization org = null;
		if (dbEntity instanceof AA14DBEntityForOrganization) {
			AA14DBEntityForOrganization dbOrg = dbEntity.as(AA14DBEntityForOrganization.class);
			org = _marshaller.forReading()
							 .fromXml(dbOrg.getDescriptor(),
									  AA14Organization.class);
		}
		else if (dbEntity instanceof AA14DBEntityForOrgDivision) {
			AA14DBEntityForOrgDivision dbDiv = dbEntity.as(AA14DBEntityForOrgDivision.class);
			org = _configCache.getBusinessConfig()
							  .getFor(AA14BusinessID.forId(dbDiv.getBusinessId()))
							  .getOrganization();
			//org = _orgCRUD.load(securityContext,
			//					  AA14OrganizationOID.forId(dbDiv.getOrganizationOid()))
			//			    .getOrThrow();
		}
		else if (dbEntity instanceof AA14DBEntityForOrgDivisionService) {
			AA14DBEntityForOrgDivisionService dbService = dbEntity.as(AA14DBEntityForOrgDivisionService.class);
			org = _configCache.getBusinessConfig()
							  .getFor(AA14BusinessID.forId(dbService.getBusinessId()))
							  .getOrganization();
			//org = _orgCRUD.load(securityContext,
			//					  AA14OrganizationOID.forId(dbService.getOrganizationOid()))
			//			  .getOrThrow();
		} 
		else if (dbEntity instanceof AA14DBEntityForOrgDivisionServiceLocation) {
			AA14DBEntityForOrgDivisionServiceLocation dbLoc = dbEntity.as(AA14DBEntityForOrgDivisionServiceLocation.class);
			org = _configCache.getBusinessConfig()
							  .getFor(AA14BusinessID.forId(dbLoc.getBusinessId()))
							  .getOrganization();
			//org = _orgCRUD.load(securityContext,
			//					  AA14OrganizationOID.forId(dbLoc.getOrganizationOid()))
			//			    .getOrThrow();
		} 
		if (org == null) 	log.error("The {} DB entity with oid={} does NOT have {} info!",
						  			  dbEntity.getClass(),dbEntity.getOid(),AA14DBEntityForOrganization.class);
		return org != null ? org.getSummarizedIn(lang) : null;
	}
	private AA14SummarizedOrgDivision _divisionSummaryIn(final SecurityContext securityContext,
														 final AA14DBEntityForOrganizationalEntityBase dbEntity,
												 		 final Language lang) {
		AA14OrgDivision div = null;
		if (dbEntity instanceof AA14DBEntityForOrgDivision) {
			AA14DBEntityForOrgDivision dbDiv = dbEntity.as(AA14DBEntityForOrgDivision.class);
			div = _marshaller.forReading()
							 .fromXml(dbDiv.getDescriptor(),
									  AA14OrgDivision.class);
		}
		else if (dbEntity instanceof AA14DBEntityForOrgDivisionService) {
			AA14DBEntityForOrgDivisionService dbService = dbEntity.as(AA14DBEntityForOrgDivisionService.class);
			div = _configCache.getBusinessConfig()
							  .getFor(AA14BusinessID.forId(dbService.getBusinessId()))
							  .getDivisionFor(AA14OrgDivisionOID.forId(dbService.getOrgDivisionOid()));
			//div = _orgDivCRUD.load(securityContext,
			//					     AA14OrgDivisionOID.forId(dbService.getOrgDivisionOid()))
			//				   .getOrThrow();
		} 
		else if (dbEntity instanceof AA14DBEntityForOrgDivisionServiceLocation) {
			AA14DBEntityForOrgDivisionServiceLocation dbLoc = dbEntity.as(AA14DBEntityForOrgDivisionServiceLocation.class);
			div = _configCache.getBusinessConfig()
							  .getFor(AA14BusinessID.forId(dbLoc.getBusinessId()))
							  .getDivisionFor(AA14OrgDivisionOID.forId(dbLoc.getOrgDivisionOid()));
			//div = _orgDivCRUD.load(securityContext,
			//					     AA14OrgDivisionOID.forId(dbLoc.getOrgDivisionOid()))
			//			       .getOrThrow();
		}
		if (div == null) 	log.error("The {} DB entity with oid={} does NOT have {} info!",
						  			  dbEntity.getClass(),dbEntity.getOid(),AA14DBEntityForOrgDivision.class);
		return div != null ? div.getSummarizedIn(lang) : null;
	}
	private AA14SummarizedOrgDivisionService _serviceSummaryIn(final SecurityContext securityContext,
															   final AA14DBEntityForOrganizationalEntityBase dbEntity,
										 					   final Language lang) {
		AA14OrgDivisionService srvc = null;
		if (dbEntity instanceof AA14DBEntityForOrgDivisionService) {
			AA14DBEntityForOrgDivisionService dbService = dbEntity.as(AA14DBEntityForOrgDivisionService.class);
			srvc = _marshaller.forReading()
							  .fromXml(dbService.getDescriptor(),
									   AA14OrgDivisionService.class);
		} 
		else if (dbEntity instanceof AA14DBEntityForOrgDivisionServiceLocation) {
			AA14DBEntityForOrgDivisionServiceLocation dbLoc = dbEntity.as(AA14DBEntityForOrgDivisionServiceLocation.class);
			srvc = _configCache.getBusinessConfig()
							  .getFor(AA14BusinessID.forId(dbLoc.getBusinessId()))
							  .getServiceFor(AA14OrgDivisionServiceOID.forId(dbLoc.getOrgDivisionServiceOid()));
			//srvc = _orgDivSrvcCRUD.load(securityContext,
			//							  AA14OrgDivisionServiceOID.forId(dbLoc.getOrgDivisionServiceOid()))
			//			     	    .getOrThrow();
		}
		if (srvc == null) 	log.error("The {} DB entity with oid={} does NOT have {} info!",
						  			  dbEntity.getClass(),dbEntity.getOid(),AA14DBEntityForOrgDivisionService.class);
		return srvc != null ? srvc.getSummarizedIn(lang) : null;
	}
	@SuppressWarnings("unused")
	private AA14SummarizedOrgDivisionServiceLocation _locationSummaryIn(final SecurityContext securityContext,
																		final AA14DBEntityForOrganizationalEntityBase dbEntity,
										 					   		    final Language lang) {
		AA14OrgDivisionServiceLocation loc = null;
		if (dbEntity instanceof AA14DBEntityForOrgDivisionServiceLocation) {
			AA14DBEntityForOrgDivisionServiceLocation dbLoc = dbEntity.as(AA14DBEntityForOrgDivisionServiceLocation.class);
			loc = _marshaller.forReading()
							 .fromXml(dbLoc.getDescriptor(),
									  AA14OrgDivisionServiceLocation.class);
		}
		if (loc == null) log.error("The {} DB entity with oid={} does NOT have {} info!",
						       	   dbEntity.getClass(),dbEntity.getOid(),AA14DBEntityForOrgDivisionServiceLocation.class);
		return loc != null ? loc.getSummarizedIn(lang) : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@SuppressWarnings({ "unchecked","unused" })
	private <S extends AA14SummarizedModelObject<?,?,?>> S _orgModelObjectSummary(final SecurityContext securityContext,
																				  final AA14DBEntityForOrganizationalEntityBase dbEntity,
										 			 							  final Language lang) {
		// Create a summary from the dbEntity: transform it to a model object and get it summarized
		AA14OrganizationalModelObjectBase<?,?,?> modelObject = _marshaller.forReading().fromXml(dbEntity.getDescriptor(),
																								AA14OrganizationalModelObjectBase.class);
		return (S)modelObject.getSummarizedIn(lang);
	}
}