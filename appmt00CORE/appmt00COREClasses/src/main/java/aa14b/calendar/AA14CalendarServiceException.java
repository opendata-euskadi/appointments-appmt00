package aa14b.calendar;

import r01f.model.persistence.PersistenceException;
import r01f.model.persistence.PersistenceRequestedOperation;
import r01f.model.persistence.PersistenceServiceErrorTypes;
import r01f.model.services.COREServiceErrorType;

public class AA14CalendarServiceException 
	 extends PersistenceException {

	private static final long serialVersionUID = 3245897076136608704L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final int SLOT_OCCUPIED_EXT_ERROR_CODE = 1;
	public static final int GENERAL_ERROR_CODE = 99;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	AA14CalendarServiceException(final PersistenceRequestedOperation requestedOp,
								 final String msg,
								 final COREServiceErrorType errorType,final int extendedCode) {
		super(requestedOp,
			  errorType,extendedCode,
			  msg);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean wasBecauseSlotIsOccupied() {
		return this.is(PersistenceServiceErrorTypes.ENTITY_ALREADY_EXISTS)
			&& this.getExtendedCode() == 1;		// calendar appointment already exists
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  STATIC BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static AA14CalendarServiceException createForAppointmentSlotOccupied() {
		return new AA14CalendarServiceException(PersistenceRequestedOperation.CREATE,
												"The appointment slot is occupied",
												PersistenceServiceErrorTypes.ENTITY_ALREADY_EXISTS,SLOT_OCCUPIED_EXT_ERROR_CODE);// slot is occupied 
	}
	public static AA14CalendarServiceException createForCalendarUnavailable(final PersistenceRequestedOperation reqOp) {
		return new AA14CalendarServiceException(reqOp,
												"Calendar service is unavailable",
												PersistenceServiceErrorTypes.CLIENT_CANNOT_CONNECT_SERVER,GENERAL_ERROR_CODE);	// general error
	}
}
