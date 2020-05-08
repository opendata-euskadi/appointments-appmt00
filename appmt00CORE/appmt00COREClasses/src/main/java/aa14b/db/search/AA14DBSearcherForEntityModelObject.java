package aa14b.db.search;

import javax.persistence.EntityManager;

import aa14b.db.entities.AA14DBEntityForOrganizationalEntityBase;
import aa14f.model.search.AA14SearchFilter;
import aa14f.model.search.AA14SearchResultItem;
import r01f.objectstreamer.Marshaller;
import r01f.patterns.Factory;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.search.db.DBSearcherBase;

public class AA14DBSearcherForEntityModelObject
	 extends DBSearcherBase<AA14SearchFilter,AA14SearchResultItem,
							AA14DBEntityForOrganizationalEntityBase> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBSearcherForEntityModelObject(final DBModuleConfig dbModuleConfig,
							 				  final EntityManager entityManager,
							 				  final Marshaller marshaller) {
		super(entityManager,
			  new Factory<AA14DBSearchQueryForModelObject>() {
						@Override
						public AA14DBSearchQueryForModelObject create() {
							return new AA14DBSearchQueryForModelObject(dbModuleConfig,
																 	   entityManager,
																 	   null);			// the ui language
						}
			  },
			  new AA14DBEntityToSearchResultItemTransformer(marshaller));
	}
}

