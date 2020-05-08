package aa14f.model.metadata;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
abstract class AA14ModelObjectCodes {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public final static long MODEL_OBJ_TYPE_BASE_CODE = 100;
	
	public final static long ORGANIZATION_MODEL_OBJ_TYPE_CODE = MODEL_OBJ_TYPE_BASE_CODE + 10;
	public final static long ORG_DIVISION_MODEL_OBJ_TYPE_CODE = MODEL_OBJ_TYPE_BASE_CODE + 20;
	public final static long ORG_DIVISION_SERVICE_MODEL_OBJ_TYPE_CODE = MODEL_OBJ_TYPE_BASE_CODE + 30;
	public final static long ORG_DIVISION_SERVICE_LOCATION_MODEL_OBJ_TYPE_CODE = MODEL_OBJ_TYPE_BASE_CODE + 40;
	
	public final static long SCHEDULE_MODEL_OBJ_TYPE_CODE = MODEL_OBJ_TYPE_BASE_CODE + 50;
	
	public final static long SLOT_MODEL_OBJ_TYPE_CODE = MODEL_OBJ_TYPE_BASE_CODE + 200;
	public final static long APPOINTMENT_MODEL_OBJ_TYPE_CODE = SLOT_MODEL_OBJ_TYPE_CODE + 201;
	public final static long NON_BOOKABLE_SLOT_MODEL_OBJ_TYPE_CODE = SLOT_MODEL_OBJ_TYPE_CODE + 202;
}
