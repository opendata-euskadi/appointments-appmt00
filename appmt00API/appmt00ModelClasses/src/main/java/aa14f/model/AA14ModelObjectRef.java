package aa14f.model;

import java.io.Serializable;

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
}
