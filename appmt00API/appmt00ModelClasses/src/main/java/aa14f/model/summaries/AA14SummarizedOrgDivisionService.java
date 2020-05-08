package aa14f.model.summaries;

import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="summarizedOrgDivisionService")
@Accessors(prefix="_")
public class AA14SummarizedOrgDivisionService 
	 extends AA14SummarizedOrganizationalModelObjectBase<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID,AA14OrgDivisionService,
	 											 		 AA14SummarizedOrgDivisionService> {

	private static final long serialVersionUID = -4373243410730886004L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="procedure",escape=true)
	@Getter @Setter private String _procedure;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14SummarizedOrgDivisionService() {
		super(AA14OrgDivisionService.class);
	}
	public static AA14SummarizedOrgDivisionService create() {
		return new AA14SummarizedOrgDivisionService();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14SummarizedOrgDivisionService managedProcedure(final String proc) {
		_procedure = proc;
		return this;
	}
}
