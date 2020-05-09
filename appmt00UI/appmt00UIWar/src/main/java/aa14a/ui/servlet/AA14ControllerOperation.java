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
public enum AA14ControllerOperation 
 implements EnumWithCode<String,AA14ControllerOperation> {
	INIT_DB("INIT_DB"),
	RELOAD_CONFIG("RELOAD_CONFIG"),
	
	VALIDATE_MAX_WEEK_PERSON_APPOINTMENTS("VALIDATE_MAX_WEEK_PERSON_APPOINTMENTS"),
	VALIDATE_PERSONID("VALIDATE_PERSONID"),
	
	GENERATE_PERSON_LOCATOR("GENERATE_PERSON_LOCATOR"),
	REMIND_PERSON_LOCATOR("REMIND_PERSON_LOCATOR"),
	
	OBTENERCITAS("OBTENERCITAS"),
	
	CONFIRMARCITA("CONFIRMARCITA"),
	BUSCARCITAS("BUSCARCITAS"),
	ACTUALIZAR_FECHA_HORA_CITA("ACTUALIZAR_FECHA_HORA_CITA"),
	ACTUALIZAR_DATOS_PERSONALES_CITA("ACTUALIZAR_DATOS_PERSONALES_CITA"),
	
	SEND_NOTIFICATION("SEND_NOTIFICATION"),
	
	RESERVAR_SLOT("RESERVAR_SLOT"),
	LIBERAR_SLOT("LIBERAR_SLOT"),
	
	BUSCAR_PERSONAS("BUSCAR_PERSONAS");
	
	
	@Getter private final Class<String> _codeType = String.class;
	@Getter private final String _code;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static EnumWithCodeWrapper<String,AA14ControllerOperation> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(AA14ControllerOperation.class);
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean is(final AA14ControllerOperation other) {
		return WRAPPER.is(this,other);
	}
	@Override
	public boolean isIn(final AA14ControllerOperation... els) {
		return WRAPPER.isIn(this,els);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static AA14ControllerOperation from(final HttpServletRequest req) {
		AA14ControllerOperation outOp = null;
		
		String op = req.getParameter("op");
		if (Strings.isNOTNullOrEmpty(op) && WRAPPER.canBeFromCode(op)) outOp = WRAPPER.fromCode(op); 
		if (outOp == null) throw new IllegalArgumentException("The op request parameter was NOT received or is NOT valid");
		return outOp;
	}
}
