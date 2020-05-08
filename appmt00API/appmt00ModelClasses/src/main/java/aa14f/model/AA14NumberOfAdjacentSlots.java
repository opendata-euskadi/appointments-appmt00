package aa14f.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;
import r01f.enums.EnumWithCodeWrapper;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum AA14NumberOfAdjacentSlots
 implements EnumWithCode<Integer,AA14NumberOfAdjacentSlots> {
	ONE		(1),
	TWO		(2),
	THREE	(3),
	FOUR	(4),
	FIVE	(5),
	SIX		(6),
	SEVEN	(7),
	EIGHT	(8),
	NINE	(9),
	TEN		(10);
	
	@Getter private final Integer _code;
	@Getter private final Class<Integer> _codeType = Integer.class;
	
	private static EnumWithCodeWrapper<Integer,AA14NumberOfAdjacentSlots> WRAPPER = EnumWithCodeWrapper.wrapEnumWithCode(AA14NumberOfAdjacentSlots.class);

	@Override
	public boolean isIn(final AA14NumberOfAdjacentSlots... els) {
		return WRAPPER.isIn(this,els);
	}
	@Override
	public boolean is(final AA14NumberOfAdjacentSlots el) {
		return WRAPPER.is(this,el);
	}
	public int asInteger() {
		return _code;
	}
	public int getValue() {
		return _code;
	}
	public static AA14NumberOfAdjacentSlots fromCode(final int code) {
		return WRAPPER.fromCode(code);
	}
	public static AA14NumberOfAdjacentSlots fromString(final String str) {
		return WRAPPER.fromCode(Integer.parseInt(str));
	}
}
