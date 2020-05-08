package aa14f.client.api.sub.delegates;

import java.util.Collection;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14FindServicesForOrganizationalEntityBase;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.locale.Language;
import r01f.model.PersistableModelObject;
import r01f.model.persistence.FindResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;

public abstract class AA14ClientAPIDelegateForOrganizationalEntityFindServicesBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,
																 	 			   M extends PersistableModelObject<O>>
	 		  extends AA14ClientAPIDelegateForFindServicesBase<O,ID,M> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIDelegateForOrganizationalEntityFindServicesBase(final Provider<SecurityContext> securityContextProvider,
																		final Marshaller modelObjectsMarshaller,
													  	  				final AA14FindServicesForOrganizationalEntityBase<O,ID,M> findServicesProxy) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  findServicesProxy);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  EXTENSION METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Finds org entities of the given type for the given business id
	 * @param orgEntityType
	 * @param businessId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<M> findByBusinessId(final AA14BusinessID businessId) {
		FindResult<M> opResult = this.getServiceProxyAs(AA14FindServicesForOrganizationalEntityBase.class)
											.findByBusinessId(this.getSecurityContext(),
												   		  	  businessId);
		Collection<M> outOrgEntities = opResult.getOrThrow();
		return outOrgEntities;
	}
	/**
	 * Returns all entities that has a certain name in a language
	 * @param name
	 * @param lang
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<M> findByNameIn(final Language lang,
									  final String name) {
		FindResult<M> opResult = this.getServiceProxyAs(AA14FindServicesForOrganizationalEntityBase.class)
											.findByNameIn(this.getSecurityContext(),
												   		  lang,name);
		Collection<M> outOrgEntities = opResult.getOrThrow();
		return outOrgEntities;
	}
}
