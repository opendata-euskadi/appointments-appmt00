package aa14b.db.search;

import com.google.common.base.Function;

import aa14b.db.entities.AA14DBEntityForOrgDivision;
import aa14b.db.entities.AA14DBEntityForOrgDivisionService;
import aa14b.db.entities.AA14DBEntityForOrgDivisionServiceLocation;
import aa14b.db.entities.AA14DBEntityForOrganization;
import aa14b.db.entities.AA14DBEntityForOrganizationalEntityBase;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14OrganizationalModelObjectBase;
import aa14f.model.search.AA14SearchResultItem;
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
public class AA14DBEntityToSearchResultItemTransformer
  implements TransformsDBEntityToSearchResultItem<AA14DBEntityForOrganizationalEntityBase,AA14SearchResultItem> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final Marshaller _marshaller;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns this transformer as a {@link Function}
	 * @param securityContext
	 * @return
	 */
	public Function<AA14DBEntityForOrganizationalEntityBase,
					AA14SearchResultItem> asTransformFuncion(final SecurityContext securityContext,
															 final Language uiLang) {
		return new Function<AA14DBEntityForOrganizationalEntityBase,
				 			AA14SearchResultItem>() {			
						@Override
						public AA14SearchResultItem apply(final AA14DBEntityForOrganizationalEntityBase dbEntity) {
							return AA14DBEntityToSearchResultItemTransformer.this.dbEntityToSearchResultItem(securityContext,
																		  									 dbEntity,
																		  									 uiLang);
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AA14SearchResultItem dbEntityToSearchResultItem(final SecurityContext securityContext,
														   final AA14DBEntityForOrganizationalEntityBase dbEntity,
														   final Language uiLang) {
		AA14SearchResultItem outItem = null;

		// [1] - object type & summaries
		if (dbEntity instanceof AA14DBEntityForOrganization) {
			outItem = new AA14SearchResultItem(AA14Organization.class);
			outItem.setOrganization(_orgSummaryIn(dbEntity,uiLang));
		}
		else if (dbEntity instanceof AA14DBEntityForOrgDivision) {
			outItem = new AA14SearchResultItem(AA14OrgDivision.class);
			outItem.setOrganization(_orgSummaryIn(dbEntity,uiLang));
			outItem.setOrgDivision(_divisionSummaryIn(dbEntity,uiLang));
		}
		else if (dbEntity instanceof AA14DBEntityForOrgDivisionService) {
			outItem = new AA14SearchResultItem(AA14OrgDivisionService.class);
			outItem.setOrganization(_orgSummaryIn(dbEntity,uiLang));
			outItem.setOrgDivision(_divisionSummaryIn(dbEntity,uiLang));
			outItem.setOrgDivisionService(_serviceSummaryIn(dbEntity,uiLang));
		}
		else if (dbEntity instanceof AA14DBEntityForOrgDivisionServiceLocation) {
			outItem = new AA14SearchResultItem(AA14OrgDivisionServiceLocation.class);
			outItem.setOrganization(_orgSummaryIn(dbEntity,uiLang));
			outItem.setOrgDivision(_divisionSummaryIn(dbEntity,uiLang));
			outItem.setOrgDivisionService(_serviceSummaryIn(dbEntity,uiLang));
			outItem.setOrgDivisionServiceLocation(_locationSummaryIn(dbEntity,uiLang));
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
	@SuppressWarnings("unchecked")
	private <S extends AA14SummarizedModelObject<?,?,?>> S _orgModelObjectSummary(final AA14DBEntityForOrganizationalEntityBase dbEntity,
										 			 							  final Language lang) {
		// Create a summary from the dbEntity: transform it to a model object and get it summarized
		AA14OrganizationalModelObjectBase<?,?,?> modelObject = _marshaller.forReading().fromXml(dbEntity.getDescriptor(),
																								AA14OrganizationalModelObjectBase.class);
		return (S)modelObject.getSummarizedIn(lang);
	}
	private AA14SummarizedOrganization _orgSummaryIn(final AA14DBEntityForOrganizationalEntityBase dbEntity,
										 			 final Language lang) {
		AA14DBEntityForOrganization dbOrg = null;
		if (dbEntity instanceof AA14DBEntityForOrganization) {
			dbOrg = dbEntity.as(AA14DBEntityForOrganization.class);
		}
		else if (dbEntity instanceof AA14DBEntityForOrgDivision) {
			AA14DBEntityForOrgDivision dbDiv = dbEntity.as(AA14DBEntityForOrgDivision.class);
			dbOrg = dbDiv.getOrganization();
		}
		else if (dbEntity instanceof AA14DBEntityForOrgDivisionService) {
			AA14DBEntityForOrgDivisionService dbService = dbEntity.as(AA14DBEntityForOrgDivisionService.class);
			dbOrg = dbService.getOrganization();
		} 
		if (dbOrg == null) 	log.error("The {} DB entity with oid={} does NOT have {} info!",
						  			  dbEntity.getClass(),dbEntity.getOid(),AA14DBEntityForOrganization.class);
		return _orgModelObjectSummary(dbEntity,lang);
	}
	private AA14SummarizedOrgDivision _divisionSummaryIn(final AA14DBEntityForOrganizationalEntityBase dbEntity,
												 		 final Language lang) {
		AA14DBEntityForOrgDivision dbDiv = null;
		if (dbEntity instanceof AA14DBEntityForOrgDivision) {
			dbDiv = dbEntity.as(AA14DBEntityForOrgDivision.class);
		}
		else if (dbEntity instanceof AA14DBEntityForOrgDivisionService) {
			AA14DBEntityForOrgDivisionService dbService = dbEntity.as(AA14DBEntityForOrgDivisionService.class);
			dbDiv = dbService.getOrgDivision();
		} 
		if (dbDiv == null) 	log.error("The {} DB entity with oid={} does NOT have {} info!",
						  			  dbEntity.getClass(),dbEntity.getOid(),AA14DBEntityForOrgDivision.class);
		return _orgModelObjectSummary(dbEntity,lang);
	}
	private AA14SummarizedOrgDivisionService _serviceSummaryIn(final AA14DBEntityForOrganizationalEntityBase dbEntity,
										 					   final Language lang) {
		AA14DBEntityForOrgDivisionService dbService = null;
		if (dbEntity instanceof AA14DBEntityForOrgDivisionService) {
			dbService = dbEntity.as(AA14DBEntityForOrgDivisionService.class);
		} 
		if (dbService == null) 	log.error("The {} DB entity with oid={} does NOT have {} info!",
						  			      dbEntity.getClass(),dbEntity.getOid(),AA14DBEntityForOrgDivisionService.class);
		return _orgModelObjectSummary(dbEntity,lang);
	}
	private AA14SummarizedOrgDivisionServiceLocation _locationSummaryIn(final AA14DBEntityForOrganizationalEntityBase dbEntity,
										 					   		    final Language lang) {
		AA14DBEntityForOrgDivisionServiceLocation dbLocation = null;
		if (dbEntity instanceof AA14DBEntityForOrgDivisionServiceLocation) {
			dbLocation = dbEntity.as(AA14DBEntityForOrgDivisionServiceLocation.class);
		}
		if (dbLocation == null) 	log.error("The {} DB entity with oid={} does NOT have {} info!",
						  			      	  dbEntity.getClass(),dbEntity.getOid(),AA14DBEntityForOrgDivisionServiceLocation.class);
		return _orgModelObjectSummary(dbEntity,lang);
	}
}
