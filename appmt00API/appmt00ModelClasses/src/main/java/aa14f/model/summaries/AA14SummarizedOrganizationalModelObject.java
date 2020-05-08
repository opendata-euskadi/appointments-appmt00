package aa14f.model.summaries;

import aa14f.model.config.AA14OrganizationalModelObject;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;

/**
 * Interface for an entity model object summary
 * @param <O>
 * @param <I>
 * @param <M>
 */
public interface AA14SummarizedOrganizationalModelObject<O extends AA14ModelObjectOID,I extends AA14ModelObjectID<O>,M extends AA14OrganizationalModelObject<O,I>>
	     extends AA14SummarizedModelObject<O,I,M> {
	
	public String getName();
	public void setName(final String name);
}
