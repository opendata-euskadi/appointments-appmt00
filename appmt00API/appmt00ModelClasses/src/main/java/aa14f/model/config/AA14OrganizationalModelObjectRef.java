package aa14f.model.config;

import aa14f.model.AA14ModelObjectRef;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * A reference to another org model object
 * @param <O>
 * @param <I>
 */
@MarshallType(as="orgModelObjectRef")
@Accessors(prefix="_")
public class AA14OrganizationalModelObjectRef<O extends AA14ModelObjectOID,I extends AA14ModelObjectID<O>> 
     extends AA14ModelObjectRef<O,I> {

	private static final long serialVersionUID = -404532793178315035L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrganizationalModelObjectRef() {
		super();
	}
	public AA14OrganizationalModelObjectRef(final O oid,final I id) {
		super(oid,id);
	}
}
