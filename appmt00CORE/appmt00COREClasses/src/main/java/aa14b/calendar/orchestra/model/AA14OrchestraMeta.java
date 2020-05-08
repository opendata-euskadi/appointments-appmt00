package aa14b.calendar.orchestra.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@JsonIgnoreProperties({"arguments"})
@Accessors(prefix="_")
public class AA14OrchestraMeta 
  implements AA14OrchestraModelObject {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@JsonProperty("totalResults")
	@Getter @Setter private int _totalresults;
	
	@JsonProperty("start")
	@Getter @Setter private String _start;
	
	@JsonProperty("end")	
	@Getter @Setter private String _end;
	
	@JsonProperty("offset")
	@Getter @Setter private String _offset;
	
	@JsonProperty("limit")
	@Getter @Setter private String _limit;
	
	@JsonProperty("fields")
	@Getter @Setter private String _fields;	
}
