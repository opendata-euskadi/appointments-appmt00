package aa14b.calendar.orchestra.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@JsonIgnoreProperties({"notifications"})
@Accessors(prefix="_")
abstract class AA14OrchestraEntityListBase 
	implements AA14OrchestraModelObject {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@JsonProperty("meta")
	@Getter @Setter private AA14OrchestraMeta _meta;
}
