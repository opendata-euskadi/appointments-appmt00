package aa14f.model.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;
import r01f.validation.Validates;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class AA14ScheduleValidators {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static Validates<AA14Schedule> createShceduleValidator() {
		return new Validates<AA14Schedule>() {
					@Override
					public ObjectValidationResult<AA14Schedule> validate(final AA14Schedule sch) {
						if (sch.getName() == null) {
							return ObjectValidationResultBuilder.on(sch)
																.isNotValidBecause("The schedule MUST have a name");			
						}
						if (sch.getBookingConfig() == null) {
							return ObjectValidationResultBuilder.on(sch)
																.isNotValidBecause("There's NO booking config");			
						}
						return ObjectValidationResultBuilder.on(sch)
															.isValid();
					}
			   };
	}
}
