package aa14b.calendar.orchestra.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import aa14b.calendar.orchestra.model.AA14OrchestraIDs.AA14OrchestraID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

@JsonIgnoreProperties({"custom"})
@Accessors(prefix="_")
abstract class AA14OrchestraLocalizableEntityBase<ID extends AA14OrchestraID>
	   extends AA14OrchestraEntityBase<ID> {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@JsonProperty("addressCountry")
	@Getter @Setter private String _country;
	
	@JsonProperty("addressState")
	@Getter @Setter private String _state;
	
	@JsonProperty("addressCity")
	@Getter @Setter private String _city;
	
	@JsonProperty("addressZip")
	@Getter @Setter private String _zip;
	
	@JsonProperty("addressLine1")
	@Getter @Setter private String _line1;
	
	@JsonProperty("addressLine2")
	@Getter @Setter private String _line2;
	
	@JsonProperty("phone")
	@Getter @Setter private Phone _phone;
	
	@JsonProperty("email")
	@Getter @Setter private EMail _email;
}
