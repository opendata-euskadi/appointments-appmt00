package aa14f.model.config;

import aa14f.model.AA14ModelObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;

@MarshallType(as="notifierFromConfig")
@ConvertToDirtyStateTrackable			// changes in state are tracked
@Accessors(prefix="_")
public class AA14NotifierFromConfig 
  implements AA14ModelObject {

	private static final long serialVersionUID = 6620492665303397590L;
/////////////////////////////////////////////////////////////////////////////////////////
//	EMAIL 
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="emailFromAddress")
	@Getter @Setter private EMail _emailFromAddress;
	
	@MarshallField(as="emailFromAddressOwner")
	@Getter @Setter private String _emailFromAddressOwner;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	PHONE
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallField(as="smsFromPhoneNumber")
	@Getter @Setter private Phone _smsFromPhoneNumber;
	
	@MarshallField(as="smsFromPhoneNumberOwner")
	@Getter @Setter private String _smsFromPhoneOwner;
}

