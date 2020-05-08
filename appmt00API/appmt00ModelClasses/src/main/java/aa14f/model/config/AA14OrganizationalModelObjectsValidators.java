package aa14f.model.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.locale.Language;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;
import r01f.validation.Validates;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class AA14OrganizationalModelObjectsValidators {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static <O extends AA14OrganizationalModelObject<?,?>> Validates<O> createOrgBaseValidator() {
		return new Validates<O>() {
					@Override
					public ObjectValidationResult<O> validate(final O obj) {
						// The model object MUST have an ID
						if (obj.getId() == null) {
							return ObjectValidationResultBuilder.on(obj)
											.isNotValidBecause("The id is NOT valid");
						}
						// The model object MUST have a business id
						if (obj.getBusinessId() == null) {
							return ObjectValidationResultBuilder.on(obj)
											.isNotValidBecause("The business id is NOT valid");
						}
						// The model object MUST have a name
						if (obj.getName() == null 
						|| !obj.getNameByLanguage().isTextDefinedFor(Language.SPANISH,
																	 Language.BASQUE)) {
							return ObjectValidationResultBuilder.on(obj)
											.isNotValidBecause("The name is NOT valid");
						}
						return ObjectValidationResultBuilder.on(obj)
													.isValid();
					}
			   };
	}
}
