package aa14b.calendar.orchestra.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Accessors(prefix="_")
public class AA14OrchestraBranchList 
	 extends AA14OrchestraEntityListBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@JsonProperty("branchList")
	@Getter @Setter private List<AA14OrchestraBranch> _branches;
}
