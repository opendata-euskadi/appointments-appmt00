package aa14f.model.summaries;

import aa14f.model.AA14EntityModelObject;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.MODULE)
public abstract class AA14SummarizedModelObjectBase<O extends AA14ModelObjectOID,I extends AA14ModelObjectID<O>,M extends AA14EntityModelObject<O,I>,
													SELF_TYPE extends AA14SummarizedModelObjectBase<O,I,M,SELF_TYPE>>
    	   implements AA14SummarizedModelObject<O,I,M> {

	private static final long serialVersionUID = -8203773765925528330L;

/////////////////////////////////////////////////////////////////////////////////////////
//  NON SERIALIZABLE FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final transient Class<M> _modelObjectType;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  SERIALIZABLE FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="oid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private O _oid;
	
	@MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private I _id;
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT API
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public SELF_TYPE withOid(final O oid) {
		_oid = oid;
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE withId(final I id) {
		_id = id;
		return (SELF_TYPE)this;
	}
}
