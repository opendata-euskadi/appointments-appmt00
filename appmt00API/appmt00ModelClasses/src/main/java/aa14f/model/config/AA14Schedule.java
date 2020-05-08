package aa14f.model.config;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import aa14f.model.AA14EntityModelObjectBase;
import aa14f.model.AA14ModelObjectRef;
import aa14f.model.metadata.AA14MetaDataForSchedule;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.summaries.AA14SummarizedSchedule;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.facets.LangDependentNamed;
import r01f.facets.LangDependentNamed.HasLangDependentNamedFacet;
import r01f.facets.delegates.LangDependentNamedDelegate;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsWrapper;
import r01f.model.metadata.annotations.ModelObjectData;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.collections.Lists;
import r01f.validation.ObjectValidationResult;
import r01f.validation.SelfValidates;

/**
 * Usually there's a [one-to-one relation] between the [schedule] and [location] (service)
 * ... BUT sometimes the SAME person works for TWO (or more) [services] (location)
 * so it can exist a [many-to-many] relation between the [schedule] and [location] (service)
 * 
 * <pre>
 * ONE-TO-ONE RELATION						MANY-TO-MANY RELATION
 * =====================================================================================
 *                                                  +------------+
 *    +------------+                                | location A |
 *    | location A |                                +-----^--^---+
 *    +-----^------+                                      |  |               +------------+
 *          |                                             +------------------+ schedule A |
 *          |           +------------+                    |  |               +------------+
 *          +-----------+ schedule A |                    |  |
 *                      +------------+                    |  |               +------------+
 *                                                        |  +---------------+ schedule A |
 *    +------------+                                      |  |               +------------+
 *    | location B |                                +-----v--v---+
 *    +-----^------+                                | location B |
 *          |                                       +------------+
 *          |           +------------+
 *          +-----------+ schedule B |
 *                      +------------+
 * </pre>
 * 
 * Users can ONLY be associated with a single [schedule] so if a certain user is working for more 
 * than a SINGLE [service] (location), a NEW [schedule] must be created and associated with all
 * [services] (location)
 * <pre>
 *          |  SRVC LOC 1   |   SRVC LOC 2    |   SRVC LOC 3
 *          |===============|=================|================
 *    SCH 1 |       X       |                 |               
 *    SCH 2 |               |       X         |      X
 *    SCH 3 |       X       |       X         |      X 
 * <pre>
 * If [sch3] has a single [resource] (user), when an [slot] is occupied either at  [srvc 1], [srvc 2] or [srvc 3],
 * the [slot] is NOT available 
 * 
 * So now when finding an available [slot] for a certain [service] (location) ALL [schedules] related with the
 * [service] (location) MUST be queried
 */
@ModelObjectData(AA14MetaDataForSchedule.class)
@MarshallType(as="schedule")
@ConvertToDirtyStateTrackable			// changes in state are tracked
@Accessors(prefix="_")
public class AA14Schedule
     extends AA14EntityModelObjectBase<AA14ScheduleOID,AA14ScheduleID,
     								   AA14Schedule>
  implements HasLangDependentNamedFacet,
  			 SelfValidates<AA14Schedule> {
	
	private static final long serialVersionUID = -8255678992528166761L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  COMMON FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="businessId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected AA14BusinessID _businessId;
	
	@MarshallField(as="name")
	@Getter @Setter protected LanguageTexts _nameByLanguage;
/////////////////////////////////////////////////////////////////////////////////////////
//  APPOINTMENT CONFIC
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="bookingConfig")
	@Getter @Setter private AA14ScheduleBookingConfig _bookingConfig;
/////////////////////////////////////////////////////////////////////////////////////////
//  ORCHESTRA CONFIG
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="orchestraConfig")
	@Getter @Setter private AA14ScheduleOrchestraConfig _orchestraConfig;
/////////////////////////////////////////////////////////////////////////////////////////
// 	LOCATIONS  
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallField(as="locations",
				   whenXml=@MarshallFieldAsXml(collectionElementName="loc"))
	@Getter @Setter private Collection<AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID>> _serviceLocationsRefs;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  REFERENCE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return a reference to this model object (a type that encapsulates the oid and the id)
	 */
	public AA14ModelObjectRef<AA14ScheduleOID,AA14ScheduleID> getReference() {
		return new AA14ModelObjectRef<AA14ScheduleOID,AA14ScheduleID>(_oid,_id);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14SummarizedSchedule getSummarizedIn(final Language lang) {
		return AA14SummarizedSchedule.create()
					   .withOid(_oid)
					   .withId(_id)
					   .named(_nameByLanguage.getFor(lang));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  HasLangDependentNamedFacet
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	@Getter private final transient LanguageTextsWrapper<AA14Schedule> _name = LanguageTextsWrapper.atHasLang(this);
	
	@Override
	public LangDependentNamed asLangDependentNamed() {
		return new LangDependentNamedDelegate<AA14Schedule>(this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Adds a location reference
	 * @param ref
	 */
	public void addServiceLocationRef(final AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID> ref) {
		if (_serviceLocationsRefs == null) _serviceLocationsRefs = Lists.newArrayList();
		_serviceLocationsRefs.add(ref);
	}
	/**
	 * Adds a location reference
	 * @param oid
	 * @param id
	 */
	public void addServiceLocationRef(final AA14OrgDivisionServiceLocationOID oid,final AA14OrgDivisionServiceLocationID id) {
		AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID> ref = new AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID>(oid,id);
		this.addServiceLocationRef(ref);
	}
	public Collection<AA14OrgDivisionServiceLocationOID> getServiceLocationsOids() {
		if (this.getServiceLocationsRefs() == null) return Lists.newArrayList();
		return FluentIterable.from(this.getServiceLocationsRefs())
						  .transform(new Function<AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID>,AA14OrgDivisionServiceLocationOID>() {

											@Override
											public AA14OrgDivisionServiceLocationOID apply(final AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID> ref) {
												return ref.getOid();
											}
						  			 })
						  .toList();
	}
	public Collection<AA14OrgDivisionServiceLocationID> getServiceLocationsIds() {
		if (this.getServiceLocationsRefs() == null) return Lists.newArrayList();
		return FluentIterable.from(this.getServiceLocationsRefs())
						  .transform(new Function<AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID>,AA14OrgDivisionServiceLocationID>() {
											@Override
											public AA14OrgDivisionServiceLocationID apply(final AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID> ref) {
												return ref.getId();
											}
						  			 })
						  .toList();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  VALIDATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override 
	public ObjectValidationResult<AA14Schedule> validate() {
		return AA14ScheduleValidators.createShceduleValidator()
								.validate(this);
	}
}
