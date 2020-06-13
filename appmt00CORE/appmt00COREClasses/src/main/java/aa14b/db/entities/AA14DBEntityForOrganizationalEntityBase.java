package aa14b.db.entities;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14Organization;
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


@MappedSuperclass
@Entity @Cacheable(false)
@Table(name="AA14ENTITYT00")
	@IdClass(DBPrimaryKeyForModelObjectImpl.class)

@Inheritance(strategy=InheritanceType.JOINED)
	@DiscriminatorColumn(name="TYPE",discriminatorType=DiscriminatorType.STRING,length=3)

@NamedQueries({
	// Find different business
	@NamedQuery(name = "AA14DBOrganizationalEntityDifferentBusinessId",
				query = "SELECT DISTINCT new aa14f.model.oids.AA14IDs$AA14BusinessID(entity._businessId) " +
						  "FROM AA14DBEntityForOrganizationalEntityBase entity "),	
	// Find by business id
	@NamedQuery(name = "AA14DBOrganizationalEntityByBusinessId",
				query = "SELECT entity " +
						  "FROM AA14DBEntityForOrganizationalEntityBase entity " +
						 "WHERE TYPE(entity) = :dbType " + 
						   "AND entity._businessId = :businessId "),
	// Find by id
	@NamedQuery(name = "AA14DBOrganizationalEntityById",
				query = "SELECT entity " +
						  "FROM AA14DBEntityForOrganizationalEntityBase entity " +
						 "WHERE TYPE(entity) = :dbType " + 
						   "AND entity._id = :id "),
	// Find all organizations
	@NamedQuery(name = "AA14DBEntitiesForOrganization",
				query = "SELECT entity " +
						  "FROM AA14DBEntityForOrganizationalEntityBase entity " +
						 "WHERE TYPE(entity) = AA14DBEntityForOrganization"),
	// Find all organization's divisions
	@NamedQuery(name = "AA14DBEntitiesForDivisionsByOrganization",
				query = "SELECT entity " +
						  "FROM AA14DBEntityForOrganizationalEntityBase entity " +
						 "WHERE TYPE(entity) = AA14DBEntityForOrgDivision " +
						   "AND entity._organizationOid = :org"),
	// Find all division's services
	@NamedQuery(name = "AA14DBEntitiesForServicesByDivision",
				query = "SELECT entity " +
						  "FROM AA14DBEntityForOrganizationalEntityBase entity " +
						 "WHERE TYPE(entity) = AA14DBEntityForOrgDivisionService " + 
						   "AND entity._orgDivisionOid = :division"),
	// Find all service's location
	@NamedQuery(name = "AA14DBEntitiesForLocationsByService",
				query = "SELECT entity " +
						  "FROM AA14DBEntityForOrganizationalEntityBase entity " +
						 "WHERE TYPE(entity) = AA14DBEntityForOrgDivisionServiceLocation " + 
						   "AND entity._orgDivisionServiceOid = :service")
})

@Accessors(prefix="_")
@NoArgsConstructor
public abstract class AA14DBEntityForOrganizationalEntityBase
              extends DBEntityBase
           implements DBEntityForModelObject<DBPrimaryKeyForModelObject>,
           			  DBEntityHasModelObjectDescriptor {

	private static final long serialVersionUID = 7697152902080922348L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Id @Column(name="OID",length=OID.OID_LENGTH,nullable=false) @Basic
    @Getter @Setter protected String _oid;	
	
	@Column(name="ID",length=OID.OID_LENGTH * 3,nullable=false) @Basic
    @Getter @Setter protected String _id;				
	
	
	@Column(name="BUSINESS_ID",length=OID.OID_LENGTH,nullable=false) @Basic
    @Getter @Setter protected String _businessId;

	
	@Column(name="ORGANIZATION_OID",length=OID.OID_LENGTH) @Basic
    @Getter @Setter protected String _organizationOid;

	@Column(name="DIVISION_OID",length=OID.OID_LENGTH) @Basic
    @Getter @Setter protected String _orgDivisionOid;
	
	@Column(name="SERVICE_OID",length=OID.OID_LENGTH) @Basic
    @Getter @Setter protected String _orgDivisionServiceOid;
	
	
	
	@Column(name="DEEPTH") @Basic
	@Getter @Setter protected int _hierarchyLevel;		// used to return ordered results when searching (see AA14DBSearcherForEntityModelObject)
	
//	@Column(name="HIERARCHY_ID",length=OID.OID_LENGTH * 3,nullable=false) @Basic
//    @Getter @Setter protected String _hierarchyId;		// full hierarchy id (ie: foo/bar/blizz)
	

	
	@Column(name="ORGANIZATION_ID",length=OID.OID_LENGTH) @Basic
    @Getter @Setter protected String _organizationId;	// full hierarchy id of the organization

	@Column(name="DIVISION_ID",length=OID.OID_LENGTH) @Basic
    @Getter @Setter protected String _orgDivisionId;	// full hierarchy id of the division
	
	@Column(name="SERVICE_ID",length=OID.OID_LENGTH) @Basic
    @Getter @Setter protected String _orgDivisionServiceId;	// full hierarchy id of the division


	
	@Column(name="NAME_ES",length=200,nullable=false) @Basic
	@Getter @Setter protected String _nameSpanish;

	@Column(name="NAME_EU",length=200,nullable=false) @Basic
	@Getter @Setter protected String _nameBasque;
	
	@Column(name="DESCRIPTOR") @Lob @Basic(fetch=FetchType.EAGER) 
	@Getter @Setter protected String _descriptor;
/////////////////////////////////////////////////////////////////////////////////////////
//  BI-DIRECTIONAL RELATIONSHIP WITH organization
//	Beware to update BOTH SIDES of the relationship: http://en.wikibooks.org/wiki/Java_Persistence/Relationships#Object_corruption.2C_one_side_of_the_relationship_is_not_updated_after_updating_the_other_side
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * Parent organization
//	 */
//	@ManyToOne(targetEntity=AA14DBEntityForOrganization.class,		// not required but informative
//			   fetch=FetchType.LAZY)
//	@JoinColumns({
//		@JoinColumn(name = "ORGANIZATION_OID",
//					referencedColumnName = "OID",
//					updatable = false,insertable = false,nullable = true)
//	})
//	@Getter private AA14DBEntityForOrganization _organization;
//
//	public void setOrganization(final AA14DBEntityForOrganization dbOrg) {
//		_organizationOid = dbOrg.getOid();
//
//		if (this instanceof AA14DBEntityForOrgDivision) {
//			AA14DBEntityForOrgDivision dbDivision = this.as(AA14DBEntityForOrgDivision.class);
//			if (!dbOrg.containsOrgDivision(dbDivision)) {
//				dbOrg.addOrgDivision(dbDivision);		// update the other side of the relationship
//			}
//		} 
//		_organization = dbOrg;
//	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BI-DIRECTIONAL RELATIONSHIP WITH organizational divisions
//	Beware to update BOTH SIDES of the relationship: http://en.wikibooks.org/wiki/Java_Persistence/Relationships#Object_corruption.2C_one_side_of_the_relationship_is_not_updated_after_updating_the_other_side
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * Parent division
//	 */
//	@ManyToOne(targetEntity=AA14DBEntityForOrgDivision.class,		// not required but informative
//			   fetch=FetchType.LAZY)
//	@JoinColumns({
//		@JoinColumn(name = "DIVISION_OID",
//					referencedColumnName = "OID",
//					updatable = false,insertable = false,nullable = true)
//	})
//	@Getter private AA14DBEntityForOrgDivision _orgDivision;
//
//	public void setOrgDivision(final AA14DBEntityForOrgDivision dbDivision) {
//		_orgDivisionOid = dbDivision.getOid();
//
//		if (this instanceof AA14DBEntityForOrgDivisionService) {
//			AA14DBEntityForOrgDivisionService dbService = this.as(AA14DBEntityForOrgDivisionService.class);
//			if (!dbDivision.containsService(dbService)) dbDivision.addService(dbService);	// update the other side of the relationship
//		}
//		_orgDivision = dbDivision;
//	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BI-DIRECTIONAL RELATIONSHIP WITH organizational division services
//	Beware to update BOTH SIDES of the relationship: http://en.wikibooks.org/wiki/Java_Persistence/Relationships#Object_corruption.2C_one_side_of_the_relationship_is_not_updated_after_updating_the_other_side
/////////////////////////////////////////////////////////////////////////////////////////
//	/**
//	 * Parent service
//	 */
//	@ManyToOne(targetEntity=AA14DBEntityForOrgDivisionService.class,		// not required but informative
//			   fetch=FetchType.LAZY)
//	@JoinColumns({
//		@JoinColumn(name = "SERVICE_OID",
//					referencedColumnName = "OID",
//					updatable = false,insertable = false,nullable = true)
//	})
//	@Getter private AA14DBEntityForOrgDivisionService _orgDivisionService;
//
//	public void setOrgDivisionService(final AA14DBEntityForOrgDivisionService dbService) {
//		_orgDivisionServiceOid = dbService.getOid();
//
//		if (this instanceof AA14DBEntityForOrgDivisionServiceLocation) {
//			AA14DBEntityForOrgDivisionServiceLocation dbLocation = this.as(AA14DBEntityForOrgDivisionServiceLocation.class);
//			if (!dbService.containsLocation(dbLocation)) dbService.addLocation(dbLocation);	// update the other side of the relationship
//		}
//		_orgDivisionService = dbService;
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
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This object typed as an entity model object: {@link AA14Organization}, {@link AA14OrgDivision} or {@link AA14OrgDivisionService}
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E extends AA14DBEntityForOrganizationalEntityBase> E as(final Class<E> type) {
		return (E)this;
	}
}
