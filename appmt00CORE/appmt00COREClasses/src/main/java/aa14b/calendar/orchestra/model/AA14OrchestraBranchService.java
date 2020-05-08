package aa14b.calendar.orchestra.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import aa14b.calendar.orchestra.model.AA14OrchestraIDs.AA14OrchestraBranchServiceID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsonIgnoreProperties({"custom"})
@Accessors(prefix="_")
public class AA14OrchestraBranchService 
	 extends AA14OrchestraEntityBase<AA14OrchestraBranchServiceID> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@JsonProperty("active")
	@Getter @Setter private boolean _active;
	
	@JsonProperty("publicEnabled")
	@Getter @Setter private boolean _publicEnabled;
	
	@JsonProperty("name")
	@Getter @Setter private String _name;
	
	@JsonProperty("duration")
	@Getter @Setter private int _duration;
	
	@JsonProperty("additionalCustomerDuration")
	@Getter @Setter private int _additionalCustomerDuration;
}
