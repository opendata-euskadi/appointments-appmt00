package aa14b.db.search;

import javax.persistence.EntityManager;

import aa14b.db.entities.AA14DBEntityForOrganizationalEntityBase;
import aa14f.api.cache.AA14BusinessConfigCache;
import aa14f.model.search.AA14SearchFilterForOrganizationalEntity;
import aa14f.model.search.AA14SearchResultItemForOrganizationalEntity;
import r01f.objectstreamer.Marshaller;
import r01f.patterns.Factory;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.search.db.DBSearcherBase;

public class AA14DBSearcherForOrganizationalEntityModelObject
	 extends DBSearcherBase<AA14SearchFilterForOrganizationalEntity,AA14SearchResultItemForOrganizationalEntity,
							AA14DBEntityForOrganizationalEntityBase> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBSearcherForOrganizationalEntityModelObject(final DBModuleConfig dbModuleConfig,
															final EntityManager entityManager,
															final Marshaller marshaller,
															// Config cache
															final AA14BusinessConfigCache configCache) {
		super(entityManager,
			  new Factory<AA14DBSearchQueryForOrganizationalModelObject>() {
						@Override
						public AA14DBSearchQueryForOrganizationalModelObject create() {
							return new AA14DBSearchQueryForOrganizationalModelObject(dbModuleConfig,
																 	   entityManager,
																 	   null);			// the ui language
						}
			  },
			  new AA14DBOrganizationalEntityToSearchResultItemTransformer(marshaller,
					  													  configCache));
	}
}

