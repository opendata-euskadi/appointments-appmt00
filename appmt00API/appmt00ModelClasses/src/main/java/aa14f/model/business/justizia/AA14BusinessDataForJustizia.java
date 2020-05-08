package aa14f.model.business.justizia;

import aa14f.model.AA14AppointmentBusinessData;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="businessDataForJustizia")
@Accessors(prefix="_")
public class AA14BusinessDataForJustizia
  implements AA14AppointmentBusinessData {

	private static final long serialVersionUID = -7393077601082876055L;
	/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="profile",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AA14Profile _profile;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14BusinessDataForJustizia() {
		// default no-args constructor
	}
	public AA14BusinessDataForJustizia(final AA14Profile profile) {
		_profile = profile;
	}
}
