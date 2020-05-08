package aa14b.db.find;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import com.google.common.base.Function;

import aa14b.db.entities.AA14DBEntityForOrganizationalEntityBase;
import aa14f.api.interfaces.AA14FindServicesForOrganizationalEntityBase;
import aa14f.model.config.AA14OrganizationalModelObject;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import aa14f.model.summaries.AA14SummarizedOrganizationalModelObject;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.model.persistence.FindResult;
import r01f.model.persistence.FindResultBuilder;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.entities.DBEntityForModelObject;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObject;
import r01f.securitycontext.SecurityContext;
import r01f.util.types.Strings;

/**
 * Persistence layer
 */
@Slf4j
abstract class AA14DBFindForOrganizationalEntityBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,M extends AA14OrganizationalModelObject<O,ID>,
									   				 DB extends DBEntityForModelObject<DBPrimaryKeyForModelObject>>
	   extends AA14DBFindBase<O,ID,M,
	 						  DB>
    implements AA14FindServicesForOrganizationalEntityBase<O,ID,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBFindForOrganizationalEntityBase(final DBModuleConfig dbCfg,
												 final Class<M> modelObjectType,final Class<DB> dbEntityType,
								   				 final EntityManager entityManager,
								   				 final Marshaller marshaller) {
		super(dbCfg,
			  modelObjectType,dbEntityType,
			  entityManager,
			  marshaller);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DBEntity SUMMARY
/////////////////////////////////////////////////////////////////////////////////////////
	protected <S extends AA14SummarizedOrganizationalModelObject<O,ID,M>> 
			  Function<AA14DBEntityForOrganizationalEntityBase,S> dbEntityToSummaryTransformFunction(final Language lang) {
		return new Function<AA14DBEntityForOrganizationalEntityBase,S>() {
						@Override @SuppressWarnings("unchecked")
						public S apply(final AA14DBEntityForOrganizationalEntityBase dbEntity) {
							// Create a summary from the dbEntity: transform it to a model object and get it summarized
							M modelObject = _modelObjectsMarshaller.forReading().fromXml(dbEntity.getDescriptor(),
																						 _modelObjectType);
							return (S)modelObject.getSummarizedIn(lang);
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public FindResult<M> findByBusinessId(final SecurityContext securityContext,
										  final AA14BusinessID businessId) {
		log.debug("> loading org entities of type {} for businessId={}",
				  _DBEntityType.getSimpleName(),
				  businessId);

		TypedQuery<DB> query = this.getEntityManager()
							        .createNamedQuery("AA14DBOrganizationalEntityByBusinessId",
									  		          _DBEntityType)
							        .setParameter("dbType",_DBEntityType)
									.setParameter("businessId",businessId.asString());
		query.setHint(QueryHints.READ_ONLY,HintValues.TRUE);
		Collection<DB> entities = query.getResultList();
		
		FindResult<M> outEntities = FindResultBuilder.using(securityContext)
							          	    .on(_modelObjectType)
							          	    .foundDBEntities(entities)
							          	    .transformedToModelObjectsUsing(this);
		return outEntities;
	}
	@Override
	public FindResult<M> findByNameIn(final SecurityContext securityContext,
									  final Language lang,final String name) {
		log.debug("> loading org entities with name {} in {}",name,lang);

		// The @NamedQuery name at every db entity MUST follow a convention:
		// If the db entity type name is: AA14DBEntityForXXX, the @NamedQuery MUST be named: AA14DBEntitiesForXXXsByName{language}
		// ie: for AA14DBEntityForOrganization > AA14DBEntitiesForOrganizationsByNameSPANISH and AA14DBEntitiesForOrganizationsByNameBASQUE
		String queryName = Strings.customized("{}sByName{}",
								  			   _DBEntityType.getSimpleName()
										  				    .replaceAll("Entity","Entities"),
										  	   lang);
		String nameFilter = _sanitizeNameFilter(name);
		TypedQuery<DB> query = this.getEntityManager()
								        .createNamedQuery(queryName,
										  		          _DBEntityType)
										.setParameter("name",nameFilter);
		query.setHint(QueryHints.READ_ONLY,HintValues.TRUE);
		Collection<DB> entities = query.getResultList();

		FindResult<M> outEntities = FindResultBuilder.using(securityContext)
									          	     .on(_modelObjectType)
									          	     .foundDBEntities(entities)
									          	     .transformedToModelObjectsUsing(this);
		return outEntities;
	}
	private static String _sanitizeNameFilter(final String text) {
		String outSanitizedFilter = new String(text);
		outSanitizedFilter = outSanitizedFilter.replaceAll("\\*","%");
		if (!outSanitizedFilter.startsWith("%")) {
			outSanitizedFilter = "%" + outSanitizedFilter;
		}
		if (!outSanitizedFilter.endsWith("%")) {
			outSanitizedFilter = outSanitizedFilter + "%";
		}
		return outSanitizedFilter;
	}
}
