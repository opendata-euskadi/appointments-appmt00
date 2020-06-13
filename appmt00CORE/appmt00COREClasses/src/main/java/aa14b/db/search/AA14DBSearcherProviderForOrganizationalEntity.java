package aa14b.db.search;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import com.google.inject.TypeLiteral;

import aa14f.model.search.AA14SearchFilter;
import aa14f.model.search.AA14SearchResultItemForOrganizationalEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.bootstrap.persistence.SearcherProviderBinding;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.search.SearchFilter;
import r01f.model.search.SearchResultItem;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.search.Searcher;
import r01f.persistence.search.SearcherProvider;
import r01f.persistence.search.db.DBSearcherProviderBase;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class AA14DBSearcherProviderForOrganizationalEntity {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	static abstract class AA14DBSearcherProviderBase<F extends SearchFilter,I extends SearchResultItem>
	              extends DBSearcherProviderBase<F,I> {

		
		public AA14DBSearcherProviderBase(final Marshaller marshaller,
										  final DBModuleConfig dbModuleConfig,
										  final Provider<EntityManager> entityManagerProvider) {
			super(marshaller,
				  dbModuleConfig,
				  entityManagerProvider);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Singleton
	@Accessors(prefix="_")
	public static class AA14DBSearcherProvider
	            extends AA14DBSearcherProviderBase<AA14SearchFilter,AA14SearchResultItemForOrganizationalEntity> {
		@Inject
		public AA14DBSearcherProvider(@ModelObjectsMarshaller final Marshaller marshaller,
															  final DBModuleConfig dbModuleConfig,
															  final Provider<EntityManager> entityManagerProvider) {
			super(marshaller,
				  dbModuleConfig,
				  entityManagerProvider);
		}
		@Override
		public Searcher<AA14SearchFilter,AA14SearchResultItemForOrganizationalEntity> get() {
			return new AA14DBSearcherForOrganizationalEntityModelObject(_dbModuleConfig,
														  				_entityManagerProvider.get(),
														  				_marshaller);
		}
		public static SearcherProviderBinding<AA14SearchFilter,AA14SearchResultItemForOrganizationalEntity> createGuiceBinding() {
			return SearcherProviderBinding.of(new TypeLiteral<SearcherProvider<AA14SearchFilter,AA14SearchResultItemForOrganizationalEntity>>() { /* nothing */ },
											  AA14DBSearcherProvider.class);
		}
	}
}
