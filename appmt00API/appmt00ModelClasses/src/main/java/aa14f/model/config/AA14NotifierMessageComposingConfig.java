package aa14f.model.config;

import java.util.Collection;

import aa14f.model.AA14ModelObject;
import aa14f.model.AA14NotificationOperation;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.Path;
import r01f.util.types.Strings;

@MarshallType(as="notifierMessageComposingConfig")
@ConvertToDirtyStateTrackable			// changes in state are tracked
@Accessors(prefix="_")
@Slf4j
public class AA14NotifierMessageComposingConfig 
  implements AA14ModelObject {

	private static final long serialVersionUID = 6620492665303397590L;
/////////////////////////////////////////////////////////////////////////////////////////
//	EMAIL 
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="emailTemplateForCreate")
	@Getter @Setter private Path _emailTemplateForCreate;
	
	@MarshallField(as="emailTemplateForUpdate")
	@Getter @Setter private Path _emailTemplateForUpdate;
	
	@MarshallField(as="emailTemplateForDelete")
	@Getter @Setter private Path _emailTemplateForDelete;
	
	@MarshallField(as="emailTemplateForRemindTomorrow")
	@Getter @Setter private Path _emailTemplateForRemindTomorrow;
	
	@MarshallField(as="emailTemplateForRemindToday")
	@Getter @Setter private Path _emailTemplateForRemindToday;
	
	@MarshallField(as="emailTemplateForPersonIdLocatorRemind")
	@Getter @Setter private Path _emailTemplateForPersonIdLocatorRemind;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	PHONE
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallField(as="smsTemplateForCreate")
	@Getter @Setter private Path _smsTemplateForCreate;
	
	@MarshallField(as="smsTemplateForUpdate")
	@Getter @Setter private Path _smsTemplateForUpdate;
	
	@MarshallField(as="smsTemplateForDelete")
	@Getter @Setter private Path _smsTemplateForDelete;
	
	@MarshallField(as="smsTemplateForRemindTomorrow")
	@Getter @Setter private Path _smsTemplateForRemindTomorrow;
	
	@MarshallField(as="smsTemplateForRemindToday")
	@Getter @Setter private Path _smsTemplateForRemindToday;
	
	@MarshallField(as="smsTemplateForPersonIdLocatorRemind")
	@Getter @Setter private Path _smsTemplateForPersonIdLocatorRemind;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	MESSAGE COMPOSING
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallField(as="commonMessageData",
				   whenXml=@MarshallFieldAsXml(collectionElementName="data"))
	@Getter @Setter private Collection<AA14NotificationMessageData> _commonMessageData;
	
	@MarshallField(as="createMessageData",
				   whenXml=@MarshallFieldAsXml(collectionElementName="data"))
	@Getter @Setter private Collection<AA14NotificationMessageData> _createMessageData;
	
	@MarshallField(as="updateMessageData",
				   whenXml=@MarshallFieldAsXml(collectionElementName="data"))
	@Getter @Setter private Collection<AA14NotificationMessageData> _updateMessageData;
	
	@MarshallField(as="deleteMessageData",
				   whenXml=@MarshallFieldAsXml(collectionElementName="data"))
	@Getter @Setter private Collection<AA14NotificationMessageData> _deleteMessageData;
	
	@MarshallField(as="remindMessageData",
				   whenXml=@MarshallFieldAsXml(collectionElementName="data"))
	@Getter @Setter private Collection<AA14NotificationMessageData> _remindMessageData;
/////////////////////////////////////////////////////////////////////////////////////////
//	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public Path getEMailTemplateFor(final AA14NotificationOperation op) {
		Path outPath = null;
		switch (op) {
		case CREATE:
			outPath = _emailTemplateForCreate;
			break;
		case UPDATE:
			outPath = _emailTemplateForUpdate;
			break;
		case DELETE:
			outPath = _emailTemplateForDelete;
			break;
		case REMIND_TODAY:
			outPath = _emailTemplateForRemindToday;
			break;
		case REMIND_TOMORROW:
			outPath = _emailTemplateForRemindTomorrow;
			break;
		case REMIND_PERSON_LOCATOR:
			outPath = _emailTemplateForPersonIdLocatorRemind;
			break;
		default:
			log.warn("[business config]: NO  email template configured for {}",op);
			break;
		}
		return outPath != null ? outPath
							   : Path.from(Strings.customized("aa14b/notifier/email/AA14DefaultTemplate-{}.vm",
									   						  op));
	}
	public Path getSMSTemplateFor(final AA14NotificationOperation op) {
		Path outPath = null;
		switch (op) {
		case CREATE:
			outPath = _smsTemplateForCreate;
			break;
		case UPDATE:
			outPath = _smsTemplateForUpdate;
			break;
		case DELETE:
			outPath = _smsTemplateForDelete;
			break;
		case REMIND_TODAY:
			outPath = _smsTemplateForRemindToday;
			break;
		case REMIND_TOMORROW:
			outPath = _smsTemplateForRemindTomorrow;
			break;
		case REMIND_PERSON_LOCATOR:
			outPath = _smsTemplateForPersonIdLocatorRemind;
			break;
		default:
			log.warn("[business config]: NO  email template configured for {}",op);
			break;
		}
		return outPath != null ? outPath
							   : Path.from(Strings.customized("aa14b/notifier/sms/AA14DefaultTemplate-{}.vm",
									   						  op));
	}
}

