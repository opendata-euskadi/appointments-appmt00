package aa14b.calendar.orchestra.model;

import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import aa14b.calendar.orchestra.model.AA14OrchestraIDs.AA14OrchestraBranchID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsonIgnoreProperties({"latitude","longitude","custom"})
@Accessors(prefix="_")
public class AA14OrchestraBranch
	 extends AA14OrchestraLocalizableEntityBase<AA14OrchestraBranchID> {
	
	@JsonProperty("branchPrefix")
	@Getter @Setter private String _branchPrefix;

	@JsonProperty("name")
	@Getter @Setter private String _name;
	
	@JsonProperty("timeZone")
	@Getter @Setter private TimeZone _timeZone; 
}
