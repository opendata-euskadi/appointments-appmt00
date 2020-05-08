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
@Table(name="AA14NONBOOKABLET00")

@DiscriminatorValue("NON_BOOKABLE")

@NamedQueries({	
	// appointment by id
	@NamedQuery(name = "AA14DBEntityForNonBookablePeriodicSlotBySerieOid",
				query = "SELECT dbSlot " +
						  "FROM AA14DBEntityForNonBookableSlot dbSlot " +
						 "WHERE dbSlot._periodicSlotSerieOid = :serieOid ")
})

@Accessors(prefix="_")
@NoArgsConstructor
public class AA14DBEntityForNonBookableSlot
     extends AA14DBEntityForBookedSlotBase {

	private static final long serialVersionUID = 7697152902080922348L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Column(name="USER_ID",length=15,nullable=false) @Basic
	@Getter @Setter protected String _userCode;

	@Column(name="SUBJECT",length=200,nullable=false) @Basic
	@Getter @Setter protected String _subject;
	
	@Column(name="PERIODIC_SERIE_OID",length=OID.OID_LENGTH,nullable=true) @Basic
	@Getter @Setter protected String _periodicSlotSerieOid;
}
