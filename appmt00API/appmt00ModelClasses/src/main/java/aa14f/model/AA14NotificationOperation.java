package aa14f.model;

import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;
import r01f.model.persistence.PersistencePerformedOperation;
import r01f.model.services.COREServiceMethod;

public enum AA14NotificationOperation
 implements EnumExtended<AA14NotificationOperation> {
	CREATE,
	UPDATE,
	DELETE,
	REMIND_TOMORROW,
	REMIND_TODAY,
	REMIND_PERSON_LOCATOR;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static final transient EnumExtendedWrapper<AA14NotificationOperation> DELEGATE = EnumExtendedWrapper.wrapEnumExtended(AA14NotificationOperation.class);
	
	@Override
	public boolean isIn(final AA14NotificationOperation... els) {
		return DELEGATE.isIn(this,els);
	}
	@Override
	public boolean is(final AA14NotificationOperation el) {
		return DELEGATE.is(this,el);
	}
	public boolean isNOTIn(final AA14NotificationOperation... els) {
		return DELEGATE.isNOTIn(this,els);
	}
	public boolean isNOT(final AA14NotificationOperation el) {
		return DELEGATE.isNOT(this,el);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static AA14NotificationOperation fromCalledMethod(final COREServiceMethod calledServiceMethod) {
		return AA14NotificationOperation.fromPersistencePerformedOperation(PersistencePerformedOperation.from(calledServiceMethod));
	}
	public static AA14NotificationOperation fromPersistencePerformedOperation(final PersistencePerformedOperation op) {
		if (op == PersistencePerformedOperation.CREATED) {
			return CREATE;
		}
		else if (op == PersistencePerformedOperation.UPDATED) {
			return UPDATE;
		}
		else if (op == PersistencePerformedOperation.DELETED) {
			return DELETE;
		}
		else {
			throw new IllegalArgumentException(op + " cannot be converted to a " + AA14NotificationOperation.class.getSimpleName());
		}
	}
}
