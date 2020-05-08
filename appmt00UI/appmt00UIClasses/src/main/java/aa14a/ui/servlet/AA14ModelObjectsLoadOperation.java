package aa14a.ui.servlet;

import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;
import r01f.util.types.Strings;

@Accessors(prefix="_")
@RequiredArgsConstructor
      enum AA14ModelObjectsLoadOperation 
implements EnumWithCode<String,AA14ModelObjectsLoadOperation> {
	LOCATION_BY_OID("OBTENERLOCALIZACIONBYOID"),
	LOCATION_BY_ID("OBTENERLOCALIZACIONBYID"),
	lOCATION_ORG_HIERARCHY_BY_OID("OBTENERJERARQUIALOCALIZACIONBYOID"),
	lOCATION_ORG_HIERARCHY_BY_ID("OBTENERJERARQUIALOCALIZACIONBYID"),
	BOOKING_CONFIG_BY_LOCATION_OID("OBTENERCONFIGCALENDARBYLOCATIONOID"),
	BOOKING_CONFIG_BY_LOCATION_ID("OBTENERCONFIGCALENDARBYLOCATIONID"),
	BOOKING_CONFIG_BY_SCHEDULE_OID("OBTENERCONFIGCALENDARBYSCHEDULEOID"),
	BOOKING_CONFIG_BY_SCHEDULE_ID("OBTENERCONFIGCALENDARBYSCHEDULEID"),
	APPOINTMENT_BY_OID("OBTENERCITABYOID");
	
	@Getter private final Class<String> _codeType = String.class;
	@Getter private final String _code;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static EnumWithCodeWrapper<String,AA14ModelObjectsLoadOperation> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(AA14ModelObjectsLoadOperation.class);
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean is(final AA14ModelObjectsLoadOperation other) {
		return WRAPPER.is(this,other);
	}
	@Override
	public boolean isIn(final AA14ModelObjectsLoadOperation... els) {
		return WRAPPER.isIn(this,els);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static AA14ModelObjectsLoadOperation from(final HttpServletRequest req) {
		AA14ModelObjectsLoadOperation outOp = null;
		
		String op = req.getParameter("op");
		if (Strings.isNOTNullOrEmpty(op) && WRAPPER.canBeFromCode(op)) outOp = WRAPPER.fromCode(op); 
		if (outOp == null) throw new IllegalArgumentException("The op request parameter was NOT received or is NOT valid");
		return outOp;
	}
}
