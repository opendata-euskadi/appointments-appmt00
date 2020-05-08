package aa14f.model;

import org.joda.time.DateTime;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.reflection.ReflectionUtils;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;
import r01f.validation.Validates;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class AA14BookedSlotValidators {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static <S extends AA14BookedSlot> Validates<S> createSlotBaseValidator() {
		return new Validates<S>() {
					@Override
					public ObjectValidationResult<S> validate(final S slot) {
						// validate the location & schedule
						if (slot.getOrgDivisionServiceLocationOid() == null
						 && ReflectionUtils.isImplementing(slot.getClass(),AA14Appointment.class)) { 
							return ObjectValidationResultBuilder.on(slot)
											.isNotValidBecause("The location is mandatory to create an appointment");
						}
						if (slot.getScheduleOid() == null) { 
							return ObjectValidationResultBuilder.on(slot)
									   .isNotValidBecause("The schedule is mandatory to create an appointment");
						}
						
						// Validate the duration
						if (!AA14BookedSlotBase.DURATION_MINUTES_RANGE.contains(slot.getDurationMinutes())) { 
							return ObjectValidationResultBuilder.on(slot)
												.isNotValidBecause("The appointment duration must be within {}",
																   AA14BookedSlotBase.DURATION_MINUTES_RANGE);
						}
						
						// validate the date
						DateTime startDate = null;
						DateTime endDate = null;
						try {
							startDate = new DateTime(slot.getYear().getYear(),slot.getMonthOfYear().getMonthOfYear(),slot.getDayOfMonth().getDayOfMonth(),
													 slot.getHourOfDay().getHourOfDay(),slot.getMinuteOfHour().getMinuteOfHour());
							endDate = startDate.plusMinutes(slot.getDurationMinutes());
						} catch(IllegalArgumentException illArgEx) {
							return ObjectValidationResultBuilder.on(slot)
											 .isNotValidBecause("The year={}, monthOfYear={}, dayOfMonth={}, hourOfDay={}, minutesOfHour={} are NOT a valid date",
													 			slot.getYear(),slot.getMonthOfYear(),slot.getDayOfMonth(),
													 			slot.getHourOfDay(),slot.getMinuteOfHour());
						}
						// validate the duration does NOT spans the next day
						if (!startDate.toLocalDate().isEqual(endDate.toLocalDate())) { 
							return ObjectValidationResultBuilder.on(slot)
								 				.isNotValidBecause("The appointment start time plus it's duration spans the appointment to the next day");
						}
						
						// Valid
						return ObjectValidationResultBuilder.on(slot)
															.isValid();
					}
			   };
	}
}
