package aa14f.model.summaries;

import aa14f.model.config.AA14OrganizationalModelObject;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;

@Accessors(prefix="_")
public abstract class AA14SummarizedOrganizationalModelObjectBase<O extends AA14ModelObjectOID,I extends AA14ModelObjectID<O>,M extends AA14OrganizationalModelObject<O,I>,
														  		  SELF_TYPE extends AA14SummarizedOrganizationalModelObjectBase<O,I,M,SELF_TYPE>>
			  extends AA14SummarizedModelObjectBase<O,I,M,SELF_TYPE>
    	   implements AA14SummarizedOrganizationalModelObject<O,I,M> {

	private static final long serialVersionUID = 6718745572987398280L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  SERIALIZABLE FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="businessId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AA14BusinessID _businessId;
	
	@MarshallField(as="name",escape=true)
	@Getter @Setter private String _name;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	AA14SummarizedOrganizationalModelObjectBase(final Class<M> modelObjectType) {
		super(modelObjectType);
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT API
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public SELF_TYPE withBusinessId(final AA14BusinessID businessId) {
		_businessId = businessId;
		return (SELF_TYPE)this;
	}
	@SuppressWarnings("unchecked")
	public SELF_TYPE named(final String name) {
		_name = name;
		return (SELF_TYPE)this;
	}
}
