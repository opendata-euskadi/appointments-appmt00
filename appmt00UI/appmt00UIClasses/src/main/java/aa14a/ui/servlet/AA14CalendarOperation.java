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
      enum AA14CalendarOperation 
implements EnumWithCode<String,AA14CalendarOperation> {
	RANGE_APPOINTMENTS("range_appointments"),
	DAY_APPOINTMENTS("day_appointments"),
	WEEK_APPOINTMENTS("week_appointments"),
	MONTH_APPOINTMENTS("month_appointments");
	
	@Getter private final Class<String> _codeType = String.class;
	@Getter private final String _code;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static EnumWithCodeWrapper<String,AA14CalendarOperation> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(AA14CalendarOperation.class);
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean is(final AA14CalendarOperation other) {
		return WRAPPER.is(this,other);
	}
	@Override
	public boolean isIn(final AA14CalendarOperation... els) {
		return WRAPPER.isIn(this,els);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static AA14CalendarOperation from(final HttpServletRequest req) {
		AA14CalendarOperation outOp = null;
		
		String op = req.getParameter("op");
		if (Strings.isNOTNullOrEmpty(op) && WRAPPER.canBeFromCode(op)) outOp = WRAPPER.fromCode(op); 
		if (outOp == null) throw new IllegalArgumentException("The op request parameter was NOT received or is NOT valid");
		return outOp;
	}
}
