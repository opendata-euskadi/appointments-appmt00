package aa14f.model.summaries;

import aa14f.model.config.AA14OrgDivision;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="summarizedOrgDivision")
public class AA14SummarizedOrgDivision 
	 extends AA14SummarizedOrganizationalModelObjectBase<AA14OrgDivisionOID,AA14OrgDivisionID,AA14OrgDivision,
	 											 		 AA14SummarizedOrgDivision> {

	private static final long serialVersionUID = -3813974507633776222L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14SummarizedOrgDivision() {
		super(AA14OrgDivision.class);
	}
	public static AA14SummarizedOrgDivision create() {
		return new AA14SummarizedOrgDivision();
	}
}
