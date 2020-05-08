package aa14f.client.servicesproxy.rest;

import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceEndPointUrl;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceResourceUrlPathBuilderBase;
import r01f.services.client.servicesproxy.rest.RESTServiceResourceUrlPathBuilders.RESTServiceResourceUrlPathBuilderForModelObjectPersistenceBase;
import r01f.types.url.UrlPath;

/**
 * Base types for REST resources path building
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
abstract class AA14RESTServiceResourceUrlPathBuilderBases {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	static class AA14RESTServiceResourceUrlPathBuilderBase 
	     extends RESTServiceResourceUrlPathBuilderBase {
		public AA14RESTServiceResourceUrlPathBuilderBase(final RESTServiceEndPointUrl endPointUrl,
														 final UrlPath resourceUrlPath) {
			super(endPointUrl,
				  resourceUrlPath);
		}
	}
	static abstract class AA14RESTServiceResourceUrlPathBuilderForPersistenceBase<O extends AA14ModelObjectOID>
		 		  extends RESTServiceResourceUrlPathBuilderForModelObjectPersistenceBase<O> {
		public AA14RESTServiceResourceUrlPathBuilderForPersistenceBase(final RESTServiceEndPointUrl endPointUrl,
															 		   final UrlPath resource) {
			super(endPointUrl,	
				  resource);
		}
	}
	static abstract class AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>>
		 		  extends AA14RESTServiceResourceUrlPathBuilderForPersistenceBase<O> {
		public AA14RESTServiceResourceUrlPathBuilderForEntityPersistenceBase(final RESTServiceEndPointUrl endPointUrl,
																   			 final UrlPath resource) {
			super(endPointUrl,
				  resource);
		}
		public UrlPath pathOfEntityById(final ID id) {
			return this.pathOfAllEntities().joinedWith("byId",id);
		}
		public UrlPath pathOfEntityListByBusinessId(final AA14BusinessID businessId) {
			return this.pathOfAllEntities().joinedWith("byBusinessId",businessId);
		}
		public UrlPath pathOfEntityListByName() {
			return this.pathOfEntityList().joinedWith("byName");
		}
	}
}
