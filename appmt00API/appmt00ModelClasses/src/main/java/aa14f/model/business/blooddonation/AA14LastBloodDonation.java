package aa14f.model.business.blooddonation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum AA14LastBloodDonation
 implements EnumWithCode<Integer,AA14LastBloodDonation> {
	NEVER (-1),
	LESS_THAN_3_YEARS (1),
	MORE_THAN_3_YEARS (2);
	
	@Getter private final Integer _code;
	@Getter private final Class<Integer> _codeType = Integer.class;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static final EnumWithCodeWrapper<Integer,AA14LastBloodDonation> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(AA14LastBloodDonation.class);
	
	@Override
	public boolean isIn(final AA14LastBloodDonation... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final AA14LastBloodDonation el) {
		return WRAPPER.is(this,el);
	}
	
	public static AA14LastBloodDonation createFrom(final String val) {
		return WRAPPER.fromCode(Integer.parseInt(val));
	}
}
