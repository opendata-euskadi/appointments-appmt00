package aa14f.model.config;

import aa14f.model.metadata.AA14MetaDataForOrganization;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import aa14f.model.summaries.AA14SummarizedOrganization;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.Language;
import r01f.model.metadata.annotations.ModelObjectData;
import r01f.objectstreamer.annotations.MarshallType;

@ModelObjectData(AA14MetaDataForOrganization.class)
@MarshallType(as="entity")
@ConvertToDirtyStateTrackable			// changes in state are tracked
@Accessors(prefix="_")
public class AA14Organization 
     extends AA14OrganizationalModelObjectBase<AA14OrganizationOID,AA14OrganizationID,
     								   		   AA14Organization> {

	private static final long serialVersionUID = 8349805975439486112L;

/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public AA14SummarizedOrganization getSummarizedIn(final Language lang) {
		return AA14SummarizedOrganization.create()
									     .withOid(_oid)
									     .withId(_id)
									     .named(_nameByLanguage.get(lang));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////

}
