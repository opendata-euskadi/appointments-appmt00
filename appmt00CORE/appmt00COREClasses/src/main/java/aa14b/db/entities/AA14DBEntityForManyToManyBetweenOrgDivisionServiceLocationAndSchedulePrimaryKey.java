package aa14b.db.entities;

import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.persistence.db.entities.primarykeys.DBPrimaryKeyForModelObject;
import r01f.util.types.Strings;

@Accessors(prefix="_")
@EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
public class AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedulePrimaryKey
  implements DBPrimaryKeyForModelObject {

	private static final long serialVersionUID = 6579341284155276783L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter protected String _locationOid;
	@Getter @Setter protected String _scheduleOid;	
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
    public static AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedulePrimaryKey from(final AA14OrgDivisionServiceLocationOID locOid,final AA14ScheduleOID schOid) {
    	return new AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedulePrimaryKey(locOid.asString(),schOid.asString());
    }
    public static AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedulePrimaryKey from(final String locOid,final String schOid) {
    	return new AA14DBEntityForManyToManyBetweenOrgDivisionServiceLocationAndSchedulePrimaryKey(locOid,schOid);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////    
	@Override
	public String asString() {
		return Strings.customized("{}/{}",_locationOid,_scheduleOid);
	}
}
