package aa14b.db.entities;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.persistence.db.DBEntity;

@Entity @Cacheable(false)
@Table(name="AA14SCHEDULE_LOCATIONT00")
	@IdClass(AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedulePrimaryKey.class)

@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedule 
  implements DBEntity {

	private static final long serialVersionUID = 8169746881105703681L;

/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Id @Column(name="LOCATION_OID",length=OID.OID_LENGTH,nullable=false) @Basic
    @Getter @Setter protected String _locationOid;
	
	@Id @Column(name="SCHEDULE_OID",length=OID.OID_LENGTH,nullable=false) @Basic
    @Getter @Setter protected String _scheduleOid;	
}
