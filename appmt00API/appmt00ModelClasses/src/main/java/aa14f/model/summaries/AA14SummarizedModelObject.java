package aa14f.model.summaries;

import aa14f.model.AA14EntityModelObject;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.model.SummarizedModelObject;

/**
 * Interface for an entity model object summary
 * @param <O>
 * @param <I>
 * @param <M>
 */
public interface AA14SummarizedModelObject<O extends AA14ModelObjectOID,I extends AA14ModelObjectID<O>,M extends AA14EntityModelObject<O,I>>
	     extends SummarizedModelObject<M> {
	
	public O getOid();
	public void setOid(O oid);
	
	public I getId();
	public void setId(I id);
}
