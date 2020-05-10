package aa14b.events;

import java.io.Serializable;

import com.google.common.eventbus.EventBus;

import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14IDs.AA14PersonLocatorID;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.events.COREServiceMethodExecEvents.COREServiceMethodExecOKEvent;
import r01f.locale.Language;
import r01f.securitycontext.SecurityContext;
import r01f.types.contact.EMail;
import r01f.types.contact.PersonID;

/**
 * An {@link EventBus} message to remind the [person locator id]
 */
@Accessors(prefix="_")
public class AA14PersonLocatorIDRemindMessage 
	 extends COREServiceMethodExecOKEvent
  implements Serializable {

	private static final long serialVersionUID = -8566091973791177319L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The org for which the user is requesting the [person locator] 
	 * (although the [person locator] is the same no matter the org)
	 */
	@Getter private final AA14OrganizationID _orgId;
	/**
	 * The [person id] whose [person locator] is needed
	 */
	@Getter private final PersonID _personId;
	/**
	 * The [contact mean] to which the [person locator] will be sent 
	 */
	@Getter private final EMail _contactEMail;
	/**
	 * The language
	 */
	@Getter private final Language _language;
	/**
	 * The [person locator]
	 */
	@Getter private final AA14PersonLocatorID _personLocatorId;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14PersonLocatorIDRemindMessage(final SecurityContext securityContext,
										    final AA14OrganizationID orgId,
										    final PersonID personId,final EMail contactEMail,final Language lang,
										    final AA14PersonLocatorID personLocatorId) {
		super(securityContext,
			  null,
			  null);
		_orgId = orgId;
		
		_personId = personId;
		_contactEMail = contactEMail;
		_language = lang;
		_personLocatorId = personLocatorId;
	}
}
