package aa14b.db.entities;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.OID;


@Entity @Cacheable(false)
@Table(name="AA14APPOINTMENTT00")

@DiscriminatorValue("APPOINTMENT")

@NamedQueries({	
	// Find all appointments by personId
	@NamedQuery(name = "AA14DBEntityForAppointment.findPersonLocator",
				query = "SELECT DISTINCT new aa14b.db.entities.AA14DTOForPersonIdAndLocator(app._personId,app._contactEMail,app._personLocatorId) " +
						  "FROM AA14DBEntityForAppointment app " +
						 "WHERE app._personId = :personId " +
						   "AND app._contactEMail = :contactEMail"),
	// Find all appointments without person locator
	@NamedQuery(name = "AA14DBEntityForAppointment.findAppointmentsWithoutPersonLocator",
				query = "SELECT app " +
						  "FROM AA14DBEntityForAppointment app " +
						 "WHERE app._personLocatorId IS NULL " + 
						   "AND app._personId = :personId ")
})

@Accessors(prefix="_")
@NoArgsConstructor
public class AA14DBEntityForAppointment
     extends AA14DBEntityForBookedSlotBase {

	private static final long serialVersionUID = 7697152902080922348L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Column(name="PERSON_ID",length=15,nullable=false) @Basic
	@Getter @Setter protected String _personId;
	
	@Column(name="CONTACT_EMAIL",length=100,nullable=true) @Basic
	@Getter @Setter protected String _contactEMail;
	
	@Column(name="PERSON_LOCATOR_ID",length=OID.OID_LENGTH,nullable=true) @Basic
	@Getter @Setter protected String _personLocatorId;	

	@Column(name="PERSON_SUMMARY",length=200,nullable=false) @Basic
	@Getter @Setter protected String _personSummary;
	
	@Column(name="SUBJECT_ID",length=200,nullable=false) @Basic
	@Getter @Setter protected String _subjectId;
}
