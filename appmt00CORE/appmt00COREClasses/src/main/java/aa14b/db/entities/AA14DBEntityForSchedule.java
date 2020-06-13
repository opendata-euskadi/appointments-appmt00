package aa14b.db.entities;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.locale.Language;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.services.LanguageTextsBuilder;
import r01f.persistence.db.DBEntityHasModelObjectDescriptor;
import r01f.persistence.db.entities.DBEntityBase;
import r01f.persistence.db.entities.DBEntityForModelObject;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObject;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObjectImpl;


@Entity @Cacheable(false)
@Table(name="AA14SCHEDULET00") 
	@IdClass(DBPrimaryKeyForModelObjectImpl.class)

@NamedQueries({
	// Find by business id
	@NamedQuery(name = "AA14DBScheduleByBusinessId",
				query = "SELECT entity " +
						  "FROM AA14DBEntityForSchedule entity " +
						 "WHERE entity._businessId = :businessId "),
	// Find by id
	@NamedQuery(name = "AA14DBScheduleById",
				query = "SELECT entity " +
						  "FROM AA14DBEntityForSchedule entity " +
						 "WHERE entity._id = :id ")
})
@Accessors(prefix="_")
@NoArgsConstructor
public class AA14DBEntityForSchedule
     extends DBEntityBase 
  implements DBEntityForModelObject<DBPrimaryKeyForModelObject>,
   			 DBEntityHasModelObjectDescriptor {

	private static final long serialVersionUID = 4903682914711075265L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Id @Column(name="OID",length=OID.OID_LENGTH,nullable=false) @Basic
    @Getter @Setter protected String _oid;	
	
	@Column(name="ID",length=OID.OID_LENGTH * 3,nullable=false) @Basic
    @Getter @Setter protected String _id;		
	
	@Column(name="BUSINESS_ID",length=OID.OID_LENGTH,nullable=false) @Basic
    @Getter @Setter protected String _businessId;
	
	@Column(name="NAME_ES",length=200,nullable=false) @Basic
	@Getter @Setter protected String _nameSpanish;

	@Column(name="NAME_EU",length=200,nullable=false) @Basic
	@Getter @Setter protected String _nameBasque;
	
	@Column(name="DESCRIPTOR") @Lob @Basic(fetch=FetchType.EAGER) 
	@Getter @Setter protected String _descriptor;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  BI-DIRECTIONAL RELATIONSHIP schedule -> location (ManyToMany)
//	Beware to update BOTH SIDES of the relationship: http://en.wikibooks.org/wiki/Java_Persistence/Relationships#Object_corruption.2C_one_side_of_the_relationship_is_not_updated_after_updating_the_other_side
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * Refered locations
//	 */
//	@ManyToMany(targetEntity=AA14DBEntityForOrgDivisionServiceLocation.class,	// not required but informative
//				cascade= {CascadeType.PERSIST},
//				fetch=FetchType.LAZY)
//	@JoinTable(name="AA14SCHEDULE_LOCATIONT00",
//	      	   joinColumns=@JoinColumn(name="SCHEDULE_OID", 				// the column on the JOIN TABLE
//	      	   						   referencedColumnName="OID"),			// the column on the SCHEDULE TABLE
//	      	   inverseJoinColumns=@JoinColumn(name="LOCATION_OID",			// the column on the JOIN TABLE
//	      	   								  referencedColumnName="OID"))	// the column on the LOCATION TABLE
//	@Getter private Collection<AA14DBEntityForOrgDivisionServiceLocation> _orgDivisionServiceLocations;
//	
//	public void addLocation(final AA14DBEntityForOrgDivisionServiceLocation dbLocation) {	
//		if (_orgDivisionServiceLocations == null) _orgDivisionServiceLocations = Lists.newArrayList();
//		_orgDivisionServiceLocations.add(dbLocation);
//		if (!dbLocation.containsSchedule(this)) dbLocation.addSchedule(this);		// update the other side of the relationship
//	}
//	public boolean containsLocation(final AA14DBEntityForOrgDivisionServiceLocation dbLocation) {
//		return _orgDivisionServiceLocations != null ? _orgDivisionServiceLocations.contains(dbLocation) : false;
//	}
/////////////////////////////////////////////////////////////////////////////////////////
//	PRIMARY KEY
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public DBPrimaryKeyForModelObject getDBEntityPrimaryKey() {
		return DBPrimaryKeyForModelObjectImpl.from(_oid);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the name in spanish and basque as a {@link LanguageTexts} object
	 */
	public LanguageTexts getName() {
		return LanguageTextsBuilder.createMapBacked()
										.withMissingLangTextBehavior(LangTextNotFoundBehabior.RETURN_NULL)
										.addForLang(Language.SPANISH,_nameSpanish)
										.addForLang(Language.BASQUE,_nameBasque)
										.build();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void _preCreate() {
		// nothing to do
	}
	@Override
	protected void _preUpdate() {
		// nothing to do
	}
}
