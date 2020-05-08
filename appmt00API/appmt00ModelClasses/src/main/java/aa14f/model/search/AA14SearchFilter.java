package aa14f.model.search;

import java.util.Collection;

import com.google.common.base.Preconditions;

import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14OrganizationalModelObject;
import aa14f.model.metadata.AA14HasMetaDataForOrgDivision;
import aa14f.model.metadata.AA14HasMetaDataForOrgDivisionService;
import aa14f.model.metadata.AA14HasMetaDataForOrgDivisionServiceLocation;
import aa14f.model.metadata.AA14HasMetaDataForOrganization;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import lombok.experimental.Accessors;
import r01f.model.ModelObject;
import r01f.model.metadata.FieldID;
import r01f.model.search.SearchFilterAsCriteriaString;
import r01f.model.search.SearchFilterForModelObjectBase;
import r01f.model.search.query.BooleanQueryClause.QueryClauseOccur;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.collections.CollectionUtils;


/**
 * A search filter for a {@link AA14OrganizationalModelObject} like {@link AA14Organization}, {@link AA14OrgDivision} or {@link AA14OrgDivisionService}
 * <pre class='brush:java'>
 * AA14SearchFilter filter = AA14SearchFilter.create()
 * 									.belongingTo(AA14OrganizationOID.forId("myOrg"))
 * 									.withText("text")
 * 									.in(Language.ENGLISH);
 * </pre>
 */
@MarshallType(as="searchFilter")
@Accessors(prefix="_")
public class AA14SearchFilter 
     extends SearchFilterForModelObjectBase<AA14SearchFilter> {

	private static final long serialVersionUID = -7328506874819631272L;

/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public AA14SearchFilter() {
		// a filter by all organizational entity model objects
		super(AA14Organization.class,
			  AA14OrgDivision.class,
			  AA14OrgDivisionService.class);
	}
	public AA14SearchFilter(final Class<? extends ModelObject>... modelObjectTypes) {
		super(modelObjectTypes);
	}
	/**
	 * Constructor needed to build a filter from the criteria string
	 * @param modelObjectType
	 */
	public AA14SearchFilter(final Collection<Class<? extends ModelObject>> modelObjectTypes) {
		super(modelObjectTypes);
	}
	/**
	 * Constructor used at REST services when a filter arrives as QueryParam:
	 * <pre class='brush:java'>
	 *		@GET 
	 *		public Response search(@HeaderParam("userContext") final AA14UserContext userContext,
	 *							   @QueryParam("filter")	   final AA14SearchFilterForEntity filter) {
	 *			...
	 *		}
 	 * </pre>
	 * any of these methods are used at {@link AA14SearchFilter}:
	 * <ul>
	 * 		<li>String based constructor</li>
	 * 		<li>valueOf(String) static method</li>
	 * 		<li>fromString(String) static method</li>
	 * </ul>
	 * @param str
	 */
	public static AA14SearchFilter valueOf(final String str) {
		return AA14SearchFilter.fromString(str);
	}
	public static AA14SearchFilter fromString(final String str) {
		return SearchFilterForModelObjectBase.fromCriteriaString(SearchFilterAsCriteriaString.of(str));
	}
	public static AA14SearchFilter create() {
		return new AA14SearchFilter();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the model object's type
	 * @param modelObjectType
	 * @return
	 */
	public AA14SearchFilter filterBy(final Class<? extends ModelObject>... modelObjTypes) {
		Collection<Class<? extends ModelObject>> modelObjTypesCol = CollectionUtils.of(modelObjTypes)
																				   .asSet();
		return this.filterBy(modelObjTypesCol);
	}
	/**
	 * Sets the model object's type
	 * @param modelObjectType
	 * @return
	 */
	public AA14SearchFilter filterBy(final Collection<Class<? extends ModelObject>> modelObjTypesCol) {
		Preconditions.checkArgument(CollectionUtils.hasData(modelObjTypesCol),"The modelObjectTypes cannot be null or empty");
		Preconditions.checkArgument(_checkModelObjType(modelObjTypesCol),"One of the received model object types to filter by is NOT valid: {}",modelObjTypesCol);
		
		return this.setModelObjectTypesToFilterBy(modelObjTypesCol);
	}
	/**
	 * Checks that the received model object types are of the correct type
	 * @param modelObjTypes
	 * @return
	 */
	private static boolean _checkModelObjType(final Collection<Class<? extends ModelObject>> modelObjTypes) {
		boolean outOK = true;
		for (Class<? extends ModelObject> modelObjType : modelObjTypes) {
			if (modelObjType == AA14Organization.class 
			 || modelObjType == AA14OrgDivision.class
			 || modelObjType == AA14OrgDivisionService.class
			 || modelObjType == AA14OrgDivisionServiceLocation.class) {
				continue;	// ok
			}
			// not valid model obj type
			outOK = false;
			break;
		}
		return outOK;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14SearchFilter belongingTo(final AA14OrganizationOID orgOid,
										final AA14OrgDivisionOID divisionOid,
										final AA14OrgDivisionServiceOID serviceOid,
										final AA14OrgDivisionServiceLocationOID locationOid) {
		Preconditions.checkArgument(orgOid != null && divisionOid != null && serviceOid != null,"The org, division, service or location oids cannot be null");
		FieldID fieldId = FieldID.from(AA14HasMetaDataForOrgDivisionServiceLocation.SEARCHABLE_METADATA.OID);
		this.belongingTo(orgOid,
						 divisionOid,
						 serviceOid);
		return this.getModifierWrapper()
					.addOrUpdateEqualsClause(fieldId,
											 locationOid,
									 		 QueryClauseOccur.MUST);
	}
	public AA14SearchFilter belongingTo(final AA14OrganizationOID orgOid,
										final AA14OrgDivisionOID divisionOid,
										final AA14OrgDivisionServiceOID serviceOid) {
		Preconditions.checkArgument(orgOid != null && divisionOid != null && serviceOid != null,"The org, division or service oids cannot be null");
		FieldID fieldId = FieldID.from(AA14HasMetaDataForOrgDivisionService.SEARCHABLE_METADATA.OID);
		this.belongingTo(orgOid,
						 divisionOid);
		
		return this.getModifierWrapper()
						.addOrUpdateEqualsClause(fieldId,
									 			 serviceOid,
									 			 QueryClauseOccur.MUST);
	}
	public AA14SearchFilter belongingTo(final AA14OrganizationOID orgOid,
										final AA14OrgDivisionOID orgDivisionOid) {
		Preconditions.checkArgument(orgOid != null && orgDivisionOid != null,"The organization or division oids cannot be null");
		FieldID fieldId = FieldID.from(AA14HasMetaDataForOrgDivision.SEARCHABLE_METADATA.OID);
		this.belongingTo(orgOid);
		
		return this.getModifierWrapper()
						.addOrUpdateEqualsClause(fieldId,
									 			 orgDivisionOid,
									 			 QueryClauseOccur.MUST);
	}
	public AA14SearchFilter belongingTo(final AA14OrganizationOID orgOid) {
		Preconditions.checkArgument(orgOid != null,"The organization oid cannot be null");
		FieldID fieldId = FieldID.from(AA14HasMetaDataForOrganization.SEARCHABLE_METADATA.OID);
		return this.getModifierWrapper()
						.addOrUpdateEqualsClause(fieldId,
									 			 orgOid,
									 			 QueryClauseOccur.MUST);
	}
	public AA14OrganizationOID getOrganizationOid() {
		FieldID fieldId = FieldID.from(AA14HasMetaDataForOrganization.SEARCHABLE_METADATA.OID);
		return this.getAccessorWrapper().queryClauses()
						.getValueOrNull(fieldId);
	}
	public AA14OrgDivisionOID getOrgDivisionOid() {
		FieldID fieldId = FieldID.from(AA14HasMetaDataForOrgDivision.SEARCHABLE_METADATA.OID);
		return this.getAccessorWrapper().queryClauses()
						.getValueOrNull(fieldId);
	}
	public AA14OrgDivisionServiceOID getOrgDivisionServiceOid() {
		FieldID fieldId = FieldID.from(AA14HasMetaDataForOrgDivisionService.SEARCHABLE_METADATA.OID);
		return this.getAccessorWrapper().queryClauses()
						.getValueOrNull(fieldId);
	}
	public AA14OrgDivisionServiceOID getOrgDivisionServiceLocationOid() {
		FieldID fieldId = FieldID.from(AA14HasMetaDataForOrgDivisionServiceLocation.SEARCHABLE_METADATA.OID);
		return this.getAccessorWrapper().queryClauses()
						.getValueOrNull(fieldId);
	}
}
