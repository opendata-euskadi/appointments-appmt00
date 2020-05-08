package aa14f.model.config;

import org.joda.time.LocalTime;

import aa14f.model.AA14ModelObject;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.timeslots.AA14TimeSlot;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.aspects.interfaces.dirtytrack.NotDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.datetime.HourOfDay;
import r01f.types.datetime.MinuteOfHour;
import r01f.types.datetime.Time;

@MarshallType(as="bookingConfig")
@ConvertToDirtyStateTrackable			// changes in state are tracked
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class AA14ScheduleBookingConfig 
  implements AA14ModelObject {

	private static final long serialVersionUID = -1557445981289096336L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final int DEFAULT_SLOT_DURATION_IN_MINUTES = 30;	
	public static final Time DEFAULT_MIN_BOOKABLE_TIME = Time.of("8:30");
	public static final Time DEFAULT_MAX_BOOKABLE_TIME = Time.of("14:30");
	public static final int DEFAULT_MAX_APPOINTMENTS_IN_SLOT = 1;
	
	// Default booking config
	@NotDirtyStateTrackable
	public static final transient AA14ScheduleBookingConfig DEF_BOOKING_CONFIG = 
										new AA14ScheduleBookingConfig(Time.of("8:30"),		// day bookable range start 
																	  Time.of("14:30"),		// day bookable range end
																	  30,					// slot lenght
																	  1,					// max appointments in slot
																	  null);				// no future booking limit
	
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="minBookableHour",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Time _minBookableHour = DEFAULT_MIN_BOOKABLE_TIME;
	
	@MarshallField(as="maxBookableHour",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Time _maxBookableHour = DEFAULT_MAX_BOOKABLE_TIME;
	
	@MarshallField(as="slotDefaultLengthMinutes",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private int _slotDefaultLengthMinutes = DEFAULT_SLOT_DURATION_IN_MINUTES;
	
	@MarshallField(as="maxAppointmentsInSlot",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private int _maxAppointmentsInSlot = DEFAULT_MAX_APPOINTMENTS_IN_SLOT;
	
	@MarshallField(as="bookingLimit")
	@Getter @Setter private AA14ScheduleBookingLimit _bookingLimit;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if this object contains the same data as the given one
	 * @param other
	 * @return
	 */
	public boolean hasSameDataAs(final AA14ScheduleBookingConfig other) {
		// min bookable hour
		if (this.getMinBookableHour() != null && other.getMinBookableHour() != null
		 && !this.getMinBookableHour().equals(other.getMinBookableHour())) {
			return false;
		}
		if ((this.getMinBookableHour() != null && other.getMinBookableHour() == null)
		 || (this.getMinBookableHour() == null && other.getMinBookableHour() != null)) {
			return false;
		}
		// max bookable hour
		if (this.getMaxBookableHour() != null && other.getMaxBookableHour() != null
		 && !this.getMaxBookableHour().equals(other.getMaxBookableHour())) {
			return false;
		}
		if ((this.getMaxBookableHour() != null && other.getMaxBookableHour() == null)
		 || (this.getMaxBookableHour() == null && other.getMaxBookableHour() != null)) {
			return false;
		}
		// slot default length
		if (this.getSlotDefaultLengthMinutes() != other.getSlotDefaultLengthMinutes()) return false;
		
		// max appointments in slot
		if (this.getMaxAppointmentsInSlot() != other.getMaxAppointmentsInSlot()) return false;
		
		// max days in future
		if (this.getBookingLimit() != null
		 && other.getBookingLimit() != null
		 && !this.getBookingLimit().hasSameDataAs(other.getBookingLimit())) {
			return false;
		} 
		if ( (this.getBookingLimit() != null && other.getBookingLimit() == null)
			   || (this.getBookingLimit() == null && other.getBookingLimit() != null) ) {
			return false;
		}
		
		return true;
	}
	/**
	 * Returns an stream of day bookable slots based on the schedule config
	 * @param scheduleOid
	 * @param numberOfAdjacentSlots
	 * @return
	 */
	public Observable<AA14TimeSlot> getBookableTimeSlots(final AA14ScheduleOID scheduleOid) {
		Observable<AA14TimeSlot> slots = null; 
		slots = Observable.create(new ObservableOnSubscribe<AA14TimeSlot>() {
										@Override
										public void subscribe(final ObservableEmitter<AA14TimeSlot> emitter) throws Exception {
									        try {
									            if (!emitter.isDisposed()) {
									            	LocalTime startTime = new LocalTime(_minBookableHour.getHourOfDay(),_minBookableHour.getMinuteOfHour(),_minBookableHour.getSecondOfMinute());
									            	LocalTime endTime = new LocalTime(_maxBookableHour.getHourOfDay(),_maxBookableHour.getMinuteOfHour(),_maxBookableHour.getSecondOfMinute());
									            	
									            	LocalTime currTime = startTime;
									            	while(currTime.isBefore(endTime)) {
									            		// create the slot
									            		int slotSizeInMinutes = _slotDefaultLengthMinutes;	
									            		AA14TimeSlot bookableSlot = new AA14TimeSlot(scheduleOid,
									            													 HourOfDay.of(currTime.getHourOfDay()),MinuteOfHour.of(currTime.getMinuteOfHour()),
									            													 slotSizeInMinutes,
									            													 true);		// available
									            		// emit
									            		emitter.onNext(bookableSlot);
									            		// next
									            		currTime = currTime.plusMinutes(_slotDefaultLengthMinutes);
									            	}
									            	// it's done!!
									                emitter.onComplete();
									            }
									        } catch (Exception e) {
									            emitter.onError(e);
									        }
										}
								});
		return slots;
	}
}
