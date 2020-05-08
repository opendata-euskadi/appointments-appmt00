package aa14f.model.business.blooddonation;

import aa14f.model.AA14AppointmentBusinessData;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="businessDataForBloodDonation")
@Accessors(prefix="_")
public class AA14BusinessDataForBloodDonation
  implements AA14AppointmentBusinessData {

	private static final long serialVersionUID = 4391088796571390661L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="notDonatedSince",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AA14LastBloodDonation _notDonatedSince;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14BusinessDataForBloodDonation() {
		// default no-args constructor
	}
	public AA14BusinessDataForBloodDonation(final AA14LastBloodDonation notDonatedSince) {
		_notDonatedSince = notDonatedSince;
	}
}
