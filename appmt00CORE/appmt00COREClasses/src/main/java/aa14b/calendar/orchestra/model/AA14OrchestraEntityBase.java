package aa14b.calendar.orchestra.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import aa14b.calendar.orchestra.model.AA14OrchestraIDs.AA14OrchestraID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
abstract class AA14OrchestraEntityBase<ID extends AA14OrchestraID> 
	implements AA14OrchestraModelObject {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@JsonProperty("publicId")
	@Getter @Setter private ID _id;
	
	@JsonProperty("branchPrefix")
	@Getter @Setter private String _branchPrefix;

	@JsonProperty("created")	
	@Getter @Setter private Date _createDate;
	
	@JsonProperty("updated")
	@Getter @Setter private Date _updateDate;
}
