package aa14b.events;

import aa14f.model.AA14Appointment;
import aa14f.model.summaries.AA14SummarizedAppointment;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.model.PersistableModelObject;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Encapsulates all data about a message to be sent about an {@link AA14Appointment}
 * This model object is NOT persisted (it's NOT a {@link PersistableModelObject} instance) since it's composed
 * when handling the creation event from other model objects
 */
@MarshallType(as="notificationMessage")
@Accessors(prefix="_")
public class AA14NotificationMessageAboutAppointment
     extends AA14NotificationMessageBase {

	private static final long serialVersionUID = -3645072151660091233L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="appointment")
	@Getter @Setter private AA14SummarizedAppointment _appointment;
}
