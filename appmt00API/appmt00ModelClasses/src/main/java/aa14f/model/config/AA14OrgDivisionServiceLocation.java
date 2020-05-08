package aa14f.model.config;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import aa14f.model.AA14ModelObjectRef;
import aa14f.model.metadata.AA14MetaDataForOrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.summaries.AA14SummarizedOrgDivisionServiceLocation;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.locale.Language;
import r01f.model.metadata.annotations.ModelObjectData;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.contact.ContactInfo;
import r01f.types.geo.GeoPosition;
import r01f.util.types.collections.CollectionUtils;
import r01f.util.types.collections.Lists;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;

@ModelObjectData(AA14MetaDataForOrgDivisionServiceLocation.class)
@MarshallType(as="orgDivisionServiceLocation")
@ConvertToDirtyStateTrackable			// changes in state are tracked
@Accessors(prefix="_")
public class AA14OrgDivisionServiceLocation
     extends AA14OrganizationalModelObjectBase<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID,
     								   		   AA14OrgDivisionServiceLocation> {

	private static final long serialVersionUID = 6807946102046157627L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="organization")
	@Getter @Setter private AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID> _orgRef;
	
	@MarshallField(as="division")
	@Getter @Setter private AA14OrganizationalModelObjectRef<AA14OrgDivisionOID,AA14OrgDivisionID> _orgDivisionRef;
	
	@MarshallField(as="service")
	@Getter @Setter private AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID> _orgDivisionServiceRef;
	
	@MarshallField(as="schedules",
				   whenXml=@MarshallFieldAsXml(collectionElementName="ref"))
	@Getter @Setter private Collection<AA14ModelObjectRef<AA14ScheduleOID,AA14ScheduleID>> _schedulesRefs;
	
	@MarshallField(as="presentationConfig")
	@Getter @Setter private AA14OrgDivisionServiceLocationPresentationConfig _presentationConfig;
/////////////////////////////////////////////////////////////////////////////////////////
//  LOCATION
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="geoPosition")
	@Getter @Setter private GeoPosition _position;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONTACT 
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="contactInfo")
	@Getter @Setter private ContactInfo _contactInfo;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override @SuppressWarnings("unchecked")
	public AA14SummarizedOrgDivisionServiceLocation getSummarizedIn(final Language lang) {
		return AA14SummarizedOrgDivisionServiceLocation.create()
													   .withOid(_oid)
													   .withId(_id)
													   .named(_nameByLanguage.getFor(lang))		
   													   .country(_position != null && _position.getCountry() != null ? _position.getCountry().getNameIn(lang) : null)
													   .state(_position != null && _position.getState() != null ? _position.getState().getNameIn(lang) : null)
													   .county(_position != null && _position.getCounty() != null ? _position.getCounty().getNameIn(lang) : null)
													   .municipality(_position != null && _position.getMunicipality() != null ? _position.getMunicipality().getNameIn(lang) : null)
													   .steet(_position != null && _position.getStreet() != null ? _position.getStreet().getNameIn(lang) : null)
													   .phones(_contactInfo != null ? _contactInfo.getContactPhones() : null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Adds a location reference
	 * @param ref
	 */
	public void addScheduleRef(final AA14ModelObjectRef<AA14ScheduleOID,AA14ScheduleID> ref) {
		if (_schedulesRefs == null) _schedulesRefs = Lists.newArrayList();
		_schedulesRefs.add(ref);		
	}
	/**
	 * Adds a location reference
	 * @param oid
	 * @param id
	 */
	public void addScheduleRef(final AA14ScheduleOID oid,final AA14ScheduleID id) {
		AA14ModelObjectRef<AA14ScheduleOID,AA14ScheduleID> ref = new AA14ModelObjectRef<AA14ScheduleOID,AA14ScheduleID>(oid,id);
		this.addScheduleRef(ref);
	}
	public Collection<AA14ScheduleOID> getSchedulesOids() {
		if (CollectionUtils.isNullOrEmpty(_schedulesRefs)) return Lists.newArrayList();
		
		return FluentIterable.from(_schedulesRefs)
							 .transform(new Function<AA14ModelObjectRef<AA14ScheduleOID,AA14ScheduleID>,AA14ScheduleOID>() {
												@Override
												public AA14ScheduleOID apply(final AA14ModelObjectRef<AA14ScheduleOID,AA14ScheduleID> ref) {
													return ref.getOid();
												}
							 			})
							 .toList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  VALIDATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override 
	public ObjectValidationResult<AA14OrgDivisionServiceLocation> validate() {
		if (_orgRef == null || _orgRef.getOid() == null || _orgRef.getId() == null) {
			return ObjectValidationResultBuilder.on(this)
												.isNotValidBecause("The organization reference is NOT valid");
		}
		if (_orgDivisionRef == null || _orgDivisionRef.getOid() == null || _orgDivisionRef.getId() == null) {
			return ObjectValidationResultBuilder.on(this)
												.isNotValidBecause("The division reference is NOT valid");
		}
		if (_orgDivisionServiceRef == null || _orgDivisionServiceRef.getOid() == null || _orgDivisionServiceRef.getId() == null) {
			return ObjectValidationResultBuilder.on(this)
												.isNotValidBecause("The service reference is NOT valid");
		}	
		return super.validate();
	}
}
