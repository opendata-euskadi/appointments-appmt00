package aa14a.model.view;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@JsonAutoDetect(fieldVisibility = Visibility.ANY,
			    getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class AA14PersonLocatorCreationResponse 
  implements Serializable {

	private static final long serialVersionUID = 391820796888054928L;

/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@JsonProperty("ok") @SerializedName("ok")
	@Getter @Setter private boolean _ok;
	
	@JsonProperty("message") @SerializedName("message")
	@Getter @Setter private String _message;
	
	@JsonProperty("locator") @SerializedName("person-locator")
	@Getter @Setter private String _personLocator;
}
