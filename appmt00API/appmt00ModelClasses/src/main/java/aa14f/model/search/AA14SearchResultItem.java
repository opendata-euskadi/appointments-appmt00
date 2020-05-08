package aa14f.model.search;

import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14OrganizationalModelObject;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import aa14f.model.summaries.AA14SummarizedOrgDivision;
import aa14f.model.summaries.AA14SummarizedOrgDivisionService;
import aa14f.model.summaries.AA14SummarizedOrgDivisionServiceLocation;
import aa14f.model.summaries.AA14SummarizedOrganization;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.locale.LanguageTexts;
import r01f.model.search.SearchResultItemForModelObjectBase;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * A search result item for an organizational entity like {@link AA14Organization}, {@link AA14Location} or {@link AA14Agent}
 */
@MarshallType(as="searchResultItemForOrganizationalEntity")
@Accessors(prefix="_")
@NoArgsConstructor
public class AA14SearchResultItem
	 extends SearchResultItemForModelObjectBase<AA14OrganizationalModelObject<?,?>> {

	private static final long serialVersionUID = 4169587420774250028L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="org")
	@Getter @Setter private AA14SummarizedOrganization _organization;

	@MarshallField(as="orgDivision")
	@Getter @Setter private AA14SummarizedOrgDivision _orgDivision;
	
	@MarshallField(as="orgDivisionService")
	@Getter @Setter private AA14SummarizedOrgDivisionService _orgDivisionService;
	
	@MarshallField(as="orgDivisionServiceLocation")
	@Getter @Setter private AA14SummarizedOrgDivisionServiceLocation _orgDivisionServiceLocation;
	
	@MarshallField(as="name")
	@Getter @Setter private LanguageTexts _name;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public AA14SearchResultItem(final Class<? extends AA14OrganizationalModelObject<?,?>> modelObjectType) {
		_modelObjectType = (Class<AA14OrganizationalModelObject<?,?>>)modelObjectType; 
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrganizationOID getOrganizationOid() {
		return _organization != null ? _organization.getOid() : null;
	}
	public AA14OrgDivisionOID getOrgDivisionOid() {
		return _orgDivision != null ? _orgDivision.getOid() : null;
	}
	public AA14OrgDivisionServiceOID getOrgDivisionServiceOid() {
		return _orgDivisionService != null ? _orgDivisionService.getOid() : null;
	}
	public AA14OrgDivisionServiceLocationOID getOrgDivisionServiceLocationOid() {
		return _orgDivisionServiceLocation != null ? _orgDivisionServiceLocation.getOid() : null;
	}
}
