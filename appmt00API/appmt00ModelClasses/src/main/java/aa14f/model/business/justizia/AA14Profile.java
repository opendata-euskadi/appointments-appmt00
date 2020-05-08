package aa14f.model.business.justizia;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum AA14Profile
 implements EnumWithCode<Integer,AA14Profile> {
	PERFIL_ABOGADO(1),
	PERFIL_PROCURADOR(2),
	PERFIL_GRADUADO(3),
	PERFIL_CIUDADANIA(4),
	PERFIL_OTRO(5);
	
	@Getter private final Integer _code;
	@Getter private final Class<Integer> _codeType = Integer.class;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static final EnumWithCodeWrapper<Integer,AA14Profile> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(AA14Profile.class);
	
	@Override
	public boolean isIn(final AA14Profile... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final AA14Profile el) {
		return WRAPPER.is(this,el);
	}
	
	public static AA14Profile createFrom(final String val) {
		return WRAPPER.fromCode(Integer.parseInt(val));
	}
}
