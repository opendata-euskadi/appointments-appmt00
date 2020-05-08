package aa14f.model.summaries;

import aa14f.model.config.AA14Organization;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="summarizedOrg")
public class AA14SummarizedOrganization 
	 extends AA14SummarizedOrganizationalModelObjectBase<AA14OrganizationOID,AA14OrganizationID,AA14Organization,
	 											 			   AA14SummarizedOrganization> {
	
	private static final long serialVersionUID = -514447944990677006L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14SummarizedOrganization() {
		super(AA14Organization.class);
	}
	public static AA14SummarizedOrganization create() {
		return new AA14SummarizedOrganization();
	}
}
