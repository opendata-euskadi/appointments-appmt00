package aa14b.db.search;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.common.collect.Lists;

import aa14b.db.entities.AA14DBEntityForOrgDivision;
import aa14b.db.entities.AA14DBEntityForOrgDivisionService;
import aa14b.db.entities.AA14DBEntityForOrgDivisionServiceLocation;
import aa14b.db.entities.AA14DBEntityForOrganization;
import aa14b.db.entities.AA14DBEntityForOrganizationalEntityBase;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.search.AA14SearchFilter;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.model.metadata.FieldID;
import r01f.model.metadata.HasMetaDataForHasFullTextSummaryModelObject;
import r01f.model.search.query.SearchResultsOrdering;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.search.db.DBSearchQuery;
import r01f.persistence.search.db.DBSearchQueryToJPQLTranslator;
import r01f.persistence.search.db.IndexableFieldIDToDBEntityFieldTranslatorByDefault;
import r01f.persistence.search.db.TranslatesIndexableFieldIDToDBEntityField;
import r01f.persistence.search.db.TranslatesSearchFilterClauseToJPQLWherePredicate;
import r01f.util.types.StringConverter;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
public class AA14DBSearchQueryForModelObject 
	 extends DBSearchQuery<AA14SearchFilter,
	 					   AA14DBEntityForOrganizationalEntityBase> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14DBSearchQueryForModelObject(final DBModuleConfig dbConfig,
									 	   final EntityManager entityManager,
									 	   final Language uiLanguage) {
		super(AA14DBEntityForOrganizationalEntityBase.class, 
			  dbConfig,
			  entityManager,
			  uiLanguage,
			  new AA14DBSearchQueryToJPQLTranslator(AA14DBEntityForOrganizationalEntityBase.class,
					  								dbConfig,
					  								entityManager));
	}	
/////////////////////////////////////////////////////////////////////////////////////////
// 	Indexable Field to DBEntity field translator 
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * An indexable field id to db entity field name translator
	 */
	private static class AA14IndexableFieldIDToDBEntityFieldTranslator
		         extends IndexableFieldIDToDBEntityFieldTranslatorByDefault<AA14SearchFilter> {
		
		public AA14IndexableFieldIDToDBEntityFieldTranslator(final AA14SearchFilter filter) {
			super(filter);
		}
		@Override
		public String dbEntityFieldNameFor(final FieldID fieldId) {
			String outDBEntityFieldName = null;
			if (fieldId.is(FieldID.from(HasMetaDataForHasFullTextSummaryModelObject.SEARCHABLE_METADATA.FULL_TEXT))
			 || fieldId.is(FieldID.forId("name"))) {
				Language filteringLang = _filter.hasTextFilter() 
									  && _filter.getTextLanguage() != null ? _filter.getTextLanguage()
																		   : _filter.getUILanguage();
				if (filteringLang == null) filteringLang = Language.DEFAULT;
				outDBEntityFieldName = filteringLang.is(Language.SPANISH) ? "entity._nameSpanish"
																	      : "entity._nameBasque";
			} 
			else {
				outDBEntityFieldName = super.dbEntityFieldNameFor(fieldId);
			}
			return outDBEntityFieldName;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  JPQL QUERY COMPOSING
/////////////////////////////////////////////////////////////////////////////////////////
	private static class AA14DBSearchQueryToJPQLTranslator 
			     extends DBSearchQueryToJPQLTranslator<AA14SearchFilter,
	 					   							   AA14DBEntityForOrganizationalEntityBase> {
		public AA14DBSearchQueryToJPQLTranslator(final Class<AA14DBEntityForOrganizationalEntityBase> dbEntityType,
												 final DBModuleConfig dbModuleConfig, 
												 final EntityManager entityManager) {
			super(dbEntityType, 
				  dbModuleConfig, 
				  entityManager);
		}
		@Override
		protected String _composeWhereJpqlPredicates(final AA14SearchFilter filter,
													 final TranslatesSearchFilterClauseToJPQLWherePredicate filterClauseToJpqlPredicate) {
			// [1] - Compose the list of entities to filter
			Collection<String> filteredDBEntities = null;
			if (CollectionUtils.hasData(filter.getFilteredModelObjectTypes())) {
				// a filter is set
				filteredDBEntities = Lists.newArrayListWithExpectedSize(filter.getFilteredModelObjectTypes().size());
				if (filter.getFilteredModelObjectTypes().contains(AA14Organization.class)) filteredDBEntities.add(AA14DBEntityForOrganization.class.getSimpleName());
				if (filter.getFilteredModelObjectTypes().contains(AA14OrgDivision.class)) filteredDBEntities.add(AA14DBEntityForOrgDivision.class.getSimpleName());
				if (filter.getFilteredModelObjectTypes().contains(AA14OrgDivisionService.class)) filteredDBEntities.add(AA14DBEntityForOrgDivisionService.class.getSimpleName());
				if (filter.getFilteredModelObjectTypes().contains(AA14OrgDivisionServiceLocation.class)) filteredDBEntities.add(AA14DBEntityForOrgDivisionServiceLocation.class.getSimpleName());
			} else {
				// no filter set: return all entity types
				filteredDBEntities = Lists.newArrayList(AA14DBEntityForOrganization.class.getSimpleName(),
												   		AA14DBEntityForOrgDivision.class.getSimpleName(),
												   		AA14DBEntityForOrgDivisionService.class.getSimpleName(),
												   		AA14DBEntityForOrgDivisionServiceLocation.class.getSimpleName());
			}
			// Avoid load unnecessary entities, if we are filtering by an organization oid the organization entity isn't returned
			// (the same applies to divisions or services)	
			if (filter.getOrganizationOid() != null) {
				filteredDBEntities.remove(AA14DBEntityForOrganization.class.getSimpleName());
			} 
			if (filter.getOrgDivisionOid() != null) {
				filteredDBEntities.remove(AA14DBEntityForOrganization.class.getSimpleName());
				filteredDBEntities.remove(AA14DBEntityForOrgDivision.class.getSimpleName());
			} 
			if (filter.getOrgDivisionServiceOid() != null) {
				filteredDBEntities.remove(AA14DBEntityForOrganization.class.getSimpleName());
				filteredDBEntities.remove(AA14DBEntityForOrgDivision.class.getSimpleName());
				filteredDBEntities.remove(AA14DBEntityForOrgDivisionService.class.getSimpleName());
			}
			String entitiesToSearch = CollectionUtils.of(filteredDBEntities)
											  		 .toStringCommaSeparated();
			log.info("Filtering entities of types: {}",
					 entitiesToSearch);
					
			StringBuilder jpql = StringConverter.asStringBuilder(Strings.customized("(TYPE(entity) IN ({}) ",
																					AA14DBEntityForOrganizationalEntityBase.class.getSimpleName(),
																					entitiesToSearch));
			
			
			// [2] - Filter by text
			if (filter.hasTextFilter()) {
				String textFilter = filterClauseToJpqlPredicate.wherePredicateFrom(filter.getTextFilter());
				if (textFilter != null) {
					jpql.append(" AND ")
						.append(textFilter);
				}
			}

			// If we're filtering by OrgDivision by text show also the OrgDivision's OrgDivisionServices
	//		if (filter.hasTextFilter() && Strings.isNOTNullOrEmpty(filter.getText())
	//		 && entitiesToSearch.equals(AA14DBEntityForOrgDivision.class.getSimpleName())) {
	//			jpql.append("OR TYPE(entity) IN (").append(AA14DBEntityForOrgDivisionService.class.getSimpleName()).append(") ");
	//		}
			
			// [2] - Filter by organization & OrgDivision
			if (filter.getOrganizationOid() != null) jpql.append("AND entity._organizationOid = :orgOid ");
			if (filter.getOrgDivisionOid() != null)  jpql.append("AND entity._OrgDivisionOid = :locOid ");
					
			return jpql.toString();
		}
		@Override
		public void setJPAQueryParameters(final AA14SearchFilter filter,final Query qry) {
			if (filter.getOrganizationOid() != null) qry.setParameter("orgOid",filter.getOrganizationOid().asString());
			if (filter.getOrgDivisionOid() != null)  qry.setParameter("locOid",filter.getOrgDivisionOid().asString());
		}
		@Override
		protected String _composeJpqlOrderByClause(final AA14SearchFilter filter,
												   final Collection<SearchResultsOrdering> ordering,
												   final TranslatesIndexableFieldIDToDBEntityField translatesFieldToDBEntityField) {
			// order by _hierarchyLevel & name
			return Strings.customized(" ORDER BY entity._hierarchyLevel ASC," +
											   " entity.{} ASC," + 
											   " entity.{}",
									  translatesFieldToDBEntityField.dbEntityFieldNameFor(FieldID.forId("name")),
									  _dbEntityFieldNameForOid(translatesFieldToDBEntityField));
		}
	}
}
