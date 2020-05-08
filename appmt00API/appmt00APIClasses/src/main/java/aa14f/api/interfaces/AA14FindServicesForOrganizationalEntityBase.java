package aa14f.api.interfaces;

import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.locale.Language;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.FindResult;
import r01f.securitycontext.SecurityContext;

public interface AA14FindServicesForOrganizationalEntityBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,
									  						 M extends PersistableModelObject<O>> 
         extends AA14FindServicesBase<O,ID,M> {
	/**
	 * Finds org entities of the given type for the given business id
	 * @param securityContext
	 * @param orgEntityType
	 * @param businessId
	 * @return
	 */
	public FindResult<M> findByBusinessId(final SecurityContext securityContext,
										  final AA14BusinessID businessId);
	/**
	 * Returns all entities by name and language
	 * @param securityContext
	 * @param userCode
	 * @return
	 */
	public FindResult<M> findByNameIn(final SecurityContext securityContext,
									  final Language lang,
									  final String name);
}
