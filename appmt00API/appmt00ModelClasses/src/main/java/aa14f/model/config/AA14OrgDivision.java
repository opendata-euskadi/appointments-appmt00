package aa14f.model.config;

import aa14f.model.metadata.AA14MetaDataForOrgDivision;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import aa14f.model.summaries.AA14SummarizedOrgDivision;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.Language;
import r01f.model.metadata.annotations.ModelObjectData;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;

@ModelObjectData(AA14MetaDataForOrgDivision.class)
@MarshallType(as="orgDivision")
@ConvertToDirtyStateTrackable			// changes in state are tracked
@Accessors(prefix="_")
public class AA14OrgDivision
     extends AA14OrganizationalModelObjectBase<AA14OrgDivisionOID,AA14OrgDivisionID,
     								   		   AA14OrgDivision> {

	private static final long serialVersionUID = -6014693898907709134L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="organization")
	@Getter @Setter private AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID> _orgRef;

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public AA14SummarizedOrgDivision getSummarizedIn(final Language lang) {
		return AA14SummarizedOrgDivision.create()
									    .withOid(_oid)
									    .withId(_id)
									    .named(_nameByLanguage.get(lang));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  VALIDATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ObjectValidationResult<AA14OrgDivision> validate() {
		if (_orgRef == null || _orgRef.getOid() == null || _orgRef.getId() == null) {
			return ObjectValidationResultBuilder.on(this)
												.isNotValidBecause("The organization reference is NOT valid");
		}
		return super.validate();
	}
}
