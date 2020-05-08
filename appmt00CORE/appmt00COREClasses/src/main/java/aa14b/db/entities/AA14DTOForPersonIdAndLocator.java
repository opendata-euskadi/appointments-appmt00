package aa14b.db.entities;

import java.io.Serializable;

import aa14f.model.oids.AA14IDs.AA14PersonLocatorID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.types.contact.NIFPersonID;
import r01f.types.contact.PersonID;
import r01f.util.types.Strings;

@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class AA14DTOForPersonIdAndLocator
  implements Serializable {

	private static final long serialVersionUID = 6389728342984476391L;

/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter protected String _personId;
	
	@Getter @Setter protected String _contactEMail;
	
	@Getter @Setter protected String _personLocatorId;
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public PersonID getPersonIdTyped() {
		return NIFPersonID.forId(_personId);
	}
	public AA14PersonLocatorID getPersonLocatorIdTyped() {
		return Strings.isNOTNullOrEmpty(_personLocatorId) ? AA14PersonLocatorID.forId(_personLocatorId) : null;
	}
}
