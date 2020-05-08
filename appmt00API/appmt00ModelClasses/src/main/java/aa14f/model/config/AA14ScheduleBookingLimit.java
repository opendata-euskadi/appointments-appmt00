package aa14f.model.config;

import java.util.Date;

import org.joda.time.LocalDate;

import aa14f.model.AA14ModelObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.DateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallDateFormat;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="bookingLimit")
@ConvertToDirtyStateTrackable			// changes in state are tracked
@Accessors(prefix="_")
@NoArgsConstructor
public class AA14ScheduleBookingLimit 
  implements AA14ModelObject {

	private static final long serialVersionUID = -981789336282245315L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="dateLimit",dateFormat=@MarshallDateFormat(use=DateFormat.ISO8601),
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Date _dateLimit;
	
	@MarshallField(as="daysInFutureLimit",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private int _daysInFutureLimit = -1;	// maximum number of days in the future
															// when an appointment can be booked
															// -1 = infinite (no limit)
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ScheduleBookingLimit(final int daysInFutureLimit) {
		_dateLimit = null;
		_daysInFutureLimit = daysInFutureLimit;
	}
	public AA14ScheduleBookingLimit(final Date dateLimit) {
		_dateLimit = dateLimit;
		_daysInFutureLimit = -1;
	}
	public AA14ScheduleBookingLimit(final Date dateLimit,final int daysInFutureLimit) {
		_dateLimit = dateLimit;
		_daysInFutureLimit = daysInFutureLimit;
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Checks if this object contains the same data as the given one
	 * @param other
	 * @return
	 */
	public boolean hasSameDataAs(final AA14ScheduleBookingLimit other) {
		// date limit
		if (this.getDateLimit() != null && other.getDateLimit() != null) {
			LocalDate thisDateLimit = new LocalDate(this.getDateLimit());
			LocalDate otherDateLimit = new LocalDate(other.getDateLimit());
			if (!thisDateLimit.isEqual(otherDateLimit)) return false;
		} 
		else if ( (this.getDateLimit() != null && other.getDateLimit() == null)
			   || (this.getDateLimit() == null && other.getDateLimit() != null) ) {
			return false;
		}
		// max days in future
		if (this.getDaysInFutureLimit() != other.getDaysInFutureLimit()) return false;
		
		return true;
	}
}
