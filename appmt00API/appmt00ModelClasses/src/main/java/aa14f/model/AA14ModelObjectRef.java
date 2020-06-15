package aa14f.model;

import java.io.Serializable;

import com.google.common.base.Objects;

import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * A reference to another org model object
 * @param <O>
 * @param <I>
 */
@MarshallType(as="modelObjectRef")
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class AA14ModelObjectRef<O extends AA14ModelObjectOID,I extends AA14ModelObjectID<O>> 
  implements Serializable {

	private static final long serialVersionUID = 7645814230977999836L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="oid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private O _oid;
	
	@MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private I _id;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof AA14ModelObjectRef)) return false;
		
		boolean outEqs = false;
		AA14ModelObjectRef<?,?> ro = (AA14ModelObjectRef<?,?>)obj;
		if (_oid != null && _id != null) {
			outEqs = _oid.is(ro.getOid()) 
				  && _id.is(ro.getId());
		} else if (_oid != null) {
			outEqs = _oid.is(ro.getOid())
				  && ro.getId() == null;
		} else if (_id != null) {
			outEqs = ro.getOid() == null
				  && _id.is(ro.getId());
		} else {
			outEqs = ro.getOid() == null 
				  && ro.getId() == null;
		}
		return outEqs;
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_oid,_id);
	}
}
