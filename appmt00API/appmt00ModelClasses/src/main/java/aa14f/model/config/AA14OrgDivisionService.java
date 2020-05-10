package aa14f.model.config;

import aa14f.model.metadata.AA14MetaDataForOrgDivisionService;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import aa14f.model.summaries.AA14SummarizedOrgDivisionService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.model.metadata.annotations.ModelObjectData;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;

@ModelObjectData(AA14MetaDataForOrgDivisionService.class)
@MarshallType(as="orgDivisionService")
@ConvertToDirtyStateTrackable			// changes in state are tracked
@Accessors(prefix="_")
public class AA14OrgDivisionService
     extends AA14OrganizationalModelObjectBase<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID,
     								   		   AA14OrgDivisionService> {

	private static final long serialVersionUID = -6014693898907709134L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="organization")
	@Getter @Setter private AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID> _orgRef;
	
	@MarshallField(as="division")
	@Getter @Setter private AA14OrganizationalModelObjectRef<AA14OrgDivisionOID,AA14OrgDivisionID> _orgDivisionRef;
	
	@MarshallField(as="procedure")
	@Getter @Setter private LanguageTexts _procedure;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public AA14SummarizedOrgDivisionService getSummarizedIn(final Language lang) {
		return AA14SummarizedOrgDivisionService.create()
											   .withOid(_oid)
											   .withId(_id)	
											   .withBusinessId(this.getBusinessId())
											   .named(_nameByLanguage != null ? _nameByLanguage.get(lang) : null)
											   .managedProcedure(_procedure != null ? _procedure.get(lang) : null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  VALIDATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override 
	public ObjectValidationResult<AA14OrgDivisionService> validate() {
		if (_orgRef == null || _orgRef.getOid() == null || _orgRef.getId() == null) {
			return ObjectValidationResultBuilder.on(this)
												.isNotValidBecause("The organization reference is NOT valid");
		}
		if (_orgDivisionRef == null || _orgDivisionRef.getOid() == null || _orgDivisionRef.getId() == null) {
			return ObjectValidationResultBuilder.on(this)
												.isNotValidBecause("The division reference is NOT valid");
		}
		return super.validate();
	}
}
