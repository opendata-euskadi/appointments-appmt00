package aa14b.services.delegates.notifier;

import aa14b.events.AA14NotificationMessageAboutAppointment;
import aa14f.model.AA14NotificationOperation;
import r01f.core.notifier.Notifier;

public interface AA14NotifierServicesDelegateImpl
		 extends Notifier<AA14NotificationMessageAboutAppointment> {
	/**
	 * Sends an appointmente create / update or delete notification message
	 * @param op
	 * @param notifMsg
	 */
	public void sendNotification(final AA14NotificationOperation op,
								 final AA14NotificationMessageAboutAppointment notifMsg);
}
