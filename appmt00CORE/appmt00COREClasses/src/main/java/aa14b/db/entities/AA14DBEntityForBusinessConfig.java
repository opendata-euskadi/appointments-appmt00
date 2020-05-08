package aa14b.db.entities;

import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.OID;
import r01f.persistence.db.DBEntity;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObjectImpl;


@Entity @Cacheable(false)
@Table(name="AA14BUSINESSCONFIGT00")
	@IdClass(DBPrimaryKeyForModelObjectImpl.class)
	
@Accessors(prefix="_")
@NoArgsConstructor
public class AA14DBEntityForBusinessConfig
  implements DBEntity {

	private static final long serialVersionUID = -8544023719877260333L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Identifier
	 */
	@Id @Column(name="OID",length=OID.OID_LENGTH,nullable=false) @Basic
	@Getter @Setter protected String _oid = "last.update";
	
	/**
	 * Last update date
	 */
	@Column(name="LASTMODIFIED_AT",
			insertable=false,updatable=true) @Temporal(TemporalType.TIMESTAMP) 
	@Getter @Setter protected Calendar _lastUpdateDate;		// http://www.developerscrappad.com/228/java/java-ee/ejb3-jpa-dealing-with-date-time-and-timestamp/
}
