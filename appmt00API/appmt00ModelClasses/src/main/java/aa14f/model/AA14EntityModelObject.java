package aa14f.model;

import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.facets.FullTextSummarizable.HasFullTextSummaryFacet;
import r01f.facets.Summarizable.HasSummaryFacet;
import r01f.model.IndexableModelObject;
import r01f.model.PersistableModelObject;

/**
 * Interface for every AA14 entity
 * @param <O>
 */
public interface AA14EntityModelObject<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>> 
		 extends PersistableModelObject<O>,	// is persistable
     	 		 IndexableModelObject,		// is indexable
     	 		 HasSummaryFacet,HasFullTextSummaryFacet,	// ... has full text
		 		 AA14ModelObject {
	
	public ID getId();
	public void setId(ID id);
}
