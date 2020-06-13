package aa14b.db.entities;

import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import aa14f.model.AA14BookedSlotType;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14Organization;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.persistence.db.DBEntityHasModelObjectDescriptor;
import r01f.persistence.db.entities.DBEntityBase;
import r01f.persistence.db.entities.DBEntityForModelObject;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObject;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObjectImpl;


@Entity @Cacheable(false)
@Table(name="AA14BOOKEDSLOTT00")
	@IdClass(DBPrimaryKeyForModelObjectImpl.class)

@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="TYPE", discriminatorType=DiscriminatorType.STRING)

@NamedQueries({	
	// appointment by id
	@NamedQuery(name = "AA14DBEntityForSlotById",
				query = "SELECT dbSlot " +
						  "FROM AA14DBEntityForBookedSlotBase dbSlot " +
						 "WHERE dbSlot._id = :id "),
	// Find all schedule's booked slots
	@NamedQuery(name = "AA14DBEntitiesForSlotBySchedule",
				query = "SELECT dbSlot " +
						  "FROM AA14DBEntityForBookedSlotBase dbSlot " +
						 "WHERE dbSlot._scheduleOid = :sch " +
						   "AND dbSlot._startDate BETWEEN :dayStart AND :dayEnd"),
	// Find all location's booked slots
	@NamedQuery(name = "AA14DBEntitiesForSlotByLocation",
				query = "SELECT dbSlot " +
						  "FROM AA14DBEntityForBookedSlotBase dbSlot " +
						 "WHERE dbSlot._orgDivisionServiceLocationOid = :loc " +
						   "AND dbSlot._startDate BETWEEN :dayStart AND :dayEnd"),
	// Find all location & schedule booked slots
	@NamedQuery(name = "AA14DBEntitiesForSlotByLocationAndSchedule",
				query = "SELECT dbSlot " +
						  "FROM AA14DBEntityForBookedSlotBase dbSlot " +
						 "WHERE dbSlot._orgDivisionServiceLocationOid = :loc " +
						   "AND dbSlot._scheduleOid = :sch " +
						   "AND dbSlot._startDate BETWEEN :dayStart AND :dayEnd"),
	// Find all schedule's booked slots that overlaps with a given range
	// [1] |-------------------------------------------|
	// [2] |-----------|
	// [3]                |------------|
	// [4]                                 |-----------|
	// DBSLOT>     [===============================]
	// test if date ranges overlaps: http://wiki.c2.com/?TestIfDateRangesOverlap
	//		( start1 <= end2 and start2 <= end1 )
	//				or
	//		( end2 >= start1 and start2 <= end1
	@NamedQuery(name = "AA14DBEntitiesForSlotOverlappingRange",
				query = "SELECT dbSlot " +
						  "FROM AA14DBEntityForBookedSlotBase dbSlot " +
						 "WHERE dbSlot._scheduleOid = :sch " +
						   "AND ( dbSlot._endDate > :start AND dbSlot._startDate < :end )")
})

@Accessors(prefix="_")
@NoArgsConstructor
public abstract class AA14DBEntityForBookedSlotBase
		      extends DBEntityBase
		   implements DBEntityForModelObject<DBPrimaryKeyForModelObject>,
		  			  DBEntityHasModelObjectDescriptor {
	
	private static final long serialVersionUID = -187718742261139982L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Id @Column(name="OID",length=OID.OID_LENGTH,nullable=false) @Basic
    @Getter @Setter protected String _oid;	

	@Column(name="ID",length=OID.OID_LENGTH * 4,nullable=false) @Basic
    @Getter @Setter protected String _id;				
	
	
	
	@Column(name="TYPE",nullable=false) @Enumerated(EnumType.STRING)
    @Getter @Setter protected AA14BookedSlotType _type;		
	
	
	
	@Column(name="ORGANIZATION_OID",length=OID.OID_LENGTH) @Basic
    @Getter @Setter protected String _organizationOid;

	@Column(name="DIVISION_OID",length=OID.OID_LENGTH) @Basic
    @Getter @Setter protected String _orgDivisionOid;
	
	@Column(name="SERVICE_OID",length=OID.OID_LENGTH) @Basic
    @Getter @Setter protected String _orgDivisionServiceOid;
	
	@Column(name="LOCATION_OID",length=OID.OID_LENGTH) @Basic
    @Getter @Setter protected String _orgDivisionServiceLocationOid;
	
	@Column(name="SCHEDULE_OID",length=OID.OID_LENGTH) @Basic
    @Getter @Setter protected String _scheduleOid;
	

	
	@Column(name="ORGANIZATION_ID",length=OID.OID_LENGTH) @Basic
    @Getter @Setter protected String _organizationId;	// full hierarchy id of the organization

	@Column(name="DIVISION_ID",length=OID.OID_LENGTH) @Basic
    @Getter @Setter protected String _orgDivisionId;	// full hierarchy id of the division
	
	@Column(name="SERVICE_ID",length=OID.OID_LENGTH) @Basic
    @Getter @Setter protected String _orgDivisionServiceId;	// full hierarchy id of the division
	
	@Column(name="LOCATION_ID",length=OID.OID_LENGTH) @Basic
    @Getter @Setter protected String _orgDivisionServiceLocationId;	// full hierarchy id of the division
	
	@Column(name="SCHEDULE_ID",length=OID.OID_LENGTH) @Basic
    @Getter @Setter protected String _scheduleId;

	
	
	@Column(name="YEAR",length=6,nullable=false) @Basic 
	@Getter @Setter protected int _year;

	@Column(name="MONTH_OF_YEAR",length=6,nullable=false) @Basic 
	@Getter @Setter protected int _monthOfYear;
	
	@Column(name="DAY_OF_MONTH",length=6,nullable=false) @Basic 
	@Getter @Setter protected int _dayOfMonth;
	
	@Column(name="HOUR_OF_DAY",length=6,nullable=false) @Basic 
	@Getter @Setter protected int _hourOfDay;
	
	@Column(name="MINUTES_OF_HOUR",length=6,nullable=false) @Basic 
	@Getter @Setter protected int _minuteOfHour;
	
	@Column(name="DURATION_IN_MINUTES",length=6,nullable=false) @Basic 
	@Getter @Setter protected int _durationInMinutes;
	
	@Column(name="START_DATE",
			insertable=true,updatable=true) @Temporal(TemporalType.TIMESTAMP) 
	@Getter @Setter protected Calendar _startDate; 			// http://www.developerscrappad.com/228/java/java-ee/ejb3-jpa-dealing-with-date-time-and-timestamp/
	
	@Column(name="END_DATE",
			insertable=true,updatable=true) @Temporal(TemporalType.TIMESTAMP) 
	@Getter @Setter protected Calendar _endDate; 			// http://www.developerscrappad.com/228/java/java-ee/ejb3-jpa-dealing-with-date-time-and-timestamp/
	
	@Column(name="DESCRIPTOR") @Lob @Basic(fetch=FetchType.EAGER) 
	@Getter @Setter protected String _descriptor;
	
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
/////////////////////////////////////////////////////////////////////////////////////////
//  UNI-DIRECTIONAL RELATIONSHIP WITH location
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * Parent location
//	 */
//	@ManyToOne(targetEntity=AA14DBEntityForOrgDivisionServiceLocation.class,		// not required but informative
//			   fetch=FetchType.LAZY)
//	@JoinColumns({
//		@JoinColumn(name = "LOCATION_OID",
//					referencedColumnName = "OID",
//					updatable = false,insertable = false,nullable = true)
//	})
//	@Getter private AA14DBEntityForOrgDivisionServiceLocation _location;
//
//	public void setLocation(final AA14DBEntityForOrgDivisionServiceLocation dbLoc) {
//		_organizationOid = dbLoc.getOrganizationOid();
//		_orgDivisionOid = dbLoc.getOrgDivisionOid();
//		_orgDivisionServiceOid = dbLoc.getOrgDivisionServiceOid();
//		_orgDivisionServiceLocationOid = dbLoc.getOid();
//		
//		_organizationId = dbLoc.getOrganizationId();
//		_orgDivisionId = dbLoc.getOrgDivisionId();
//		_orgDivisionServiceId = dbLoc.getOrgDivisionServiceId();
//		_orgDivisionServiceLocationId = dbLoc.getId();
//		
//		_location = dbLoc;
//	}
/////////////////////////////////////////////////////////////////////////////////////////
//  UNI-DIRECTIONAL RELATIONSHIP WITH schedule
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * Parent schedule
//	 */
//	@ManyToOne(targetEntity=AA14DBEntityForSchedule.class,		// not required but informative
//			   fetch=FetchType.LAZY)
//	@JoinColumns({
//		@JoinColumn(name = "SCHEDULE_OID",
//					referencedColumnName = "OID",
//					updatable = false,insertable = false,nullable = true)
//	})
//	@Getter private AA14DBEntityForSchedule _schedule;
//
//	public void setSchedule(final AA14DBEntityForSchedule dbSched) {
//		_scheduleOid = dbSched.getOid();
//		
//		_scheduleId = dbSched.getId();
//		
//		_schedule = dbSched;
//	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public DBPrimaryKeyForModelObject getDBEntityPrimaryKey() {
		return DBPrimaryKeyForModelObjectImpl.from(_oid);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This object typed as an entity model object: {@link AA14Organization}, {@link AA14OrgDivision} or {@link AA14OrgDivisionService}
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E extends AA14DBEntityForBookedSlotBase> E as(final Class<E> type) {
		return (E)this;
	}
}
