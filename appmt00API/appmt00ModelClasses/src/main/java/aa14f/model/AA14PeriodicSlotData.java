package aa14f.model;

import java.util.Date;

import org.joda.time.DateTimeConstants;

import aa14f.model.oids.AA14OIDs.AA14PeriodicSlotSerieOID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.locale.Language;
import r01f.model.ModelObject;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallIgnoredField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Memoized;
import r01f.types.BitMap;
import r01f.types.Range;
import r01f.util.types.Dates;

@MarshallType(as="periodicSlotData")
@Accessors(prefix="_")
public class AA14PeriodicSlotData 
  implements ModelObject,
  			 Debuggable {
	
	private static final long serialVersionUID = -4417403366081961840L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * When a non bookable slot is a periodic one, all the individual non-bookable slots
	 * are related by a serie oid
	 */
	@MarshallField(as="periodicSlotSerieOid",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected AA14PeriodicSlotSerieOID _serieOid;
	
	@MarshallField(as="dateRange",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected Range<Date> _dateRange;
	
	@MarshallField(as="weekDays",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected int _weekDaysBitMap = 0;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallIgnoredField
	private transient final Memoized<WeekDaysBitMap> _weekDaysBitMapWrapper = new Memoized<WeekDaysBitMap>() {
																						@Override
																						public WeekDaysBitMap supply() {
																							return new WeekDaysBitMap(_weekDaysBitMap);
																						}
															  				  };
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14PeriodicSlotData() {
		// no args constructor
	}
	public AA14PeriodicSlotData(final AA14PeriodicSlotSerieOID serieOid,
								final com.google.common.collect.Range<Date> dateRange,
								final boolean sunday,final boolean monday,final boolean tuesday,final boolean wednesday,final boolean thursday,final boolean friday,final boolean saturday) {
		this(serieOid,
			 new Range<Date>(dateRange),
			 sunday,monday,tuesday,wednesday,thursday,friday,saturday);
	}
	public AA14PeriodicSlotData(final AA14PeriodicSlotSerieOID serieOid,
								final Range<Date> dateRange,
								final boolean sunday,final boolean monday,final boolean tuesday,final boolean wednesday,final boolean thursday,final boolean friday,final boolean saturday) {
		_serieOid = serieOid;
		_dateRange = dateRange;
		
		if (sunday) this.setSunday();
		if (monday) this.setMonday();
		if (tuesday) this.setTuesday();
		if (wednesday) this.setWednesday();
		if (thursday) this.setThursday();
		if (friday) this.setFriday();
		if (saturday) this.setSaturday();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public String getStartDateFormatted(final Language lang) {
		return _dateRange != null ? _formatDate(_dateRange.lowerEndpoint(),lang)
								  : null;
	}
	public String getEndDateFormatted(final Language lang) {
		return _dateRange != null ? _formatDate(_dateRange.upperEndpoint(),lang)
								  : null;
	}
	private static String _formatDate(final Date date,final Language lang) {
		return date != null 
					? lang == Language.SPANISH ? Dates.format(date,"dd/MM/yyyy")
											   : Dates.format(date,"yyyy/MM/yyyy")
				    : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public void setSunday() {
		_weekDaysBitMapWrapper.get().setWeekDay(DateTimeConstants.SUNDAY-1);
	}
	public void setMonday() {
		_weekDaysBitMapWrapper.get().setWeekDay(DateTimeConstants.MONDAY-1);
	}
	public void setTuesday() {
		_weekDaysBitMapWrapper.get().setWeekDay(DateTimeConstants.TUESDAY-1);
	}
	public void setWednesday() {
		_weekDaysBitMapWrapper.get().setWeekDay(DateTimeConstants.WEDNESDAY-1);
	}
	public void setThursday() {
		_weekDaysBitMapWrapper.get().setWeekDay(DateTimeConstants.THURSDAY-1);
	}
	public void setFriday() {
		_weekDaysBitMapWrapper.get().setWeekDay(DateTimeConstants.FRIDAY-1);
	}
	public void setSaturday() {
		_weekDaysBitMapWrapper.get().setWeekDay(DateTimeConstants.SATURDAY-1);
	}
	
	public boolean isSunday() {
		return _weekDaysBitMapWrapper.get().isWeekDaySet(DateTimeConstants.SUNDAY-1);
	}
	public boolean isMonday() {
		return _weekDaysBitMapWrapper.get().isWeekDaySet(DateTimeConstants.MONDAY-1);
	}
	public boolean isTuesday() {
		return _weekDaysBitMapWrapper.get().isWeekDaySet(DateTimeConstants.TUESDAY-1);
	}
	public boolean isWednesday() {
		return _weekDaysBitMapWrapper.get().isWeekDaySet(DateTimeConstants.WEDNESDAY-1);
	}
	public boolean isThursday() {
		return _weekDaysBitMapWrapper.get().isWeekDaySet(DateTimeConstants.THURSDAY-1);
	}
	public boolean isFriday() {
		return _weekDaysBitMapWrapper.get().isWeekDaySet(DateTimeConstants.FRIDAY-1);
	}
	public boolean isSaturday() {
		return _weekDaysBitMapWrapper.get().isWeekDaySet(DateTimeConstants.SATURDAY-1);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		StringBuilder dbg = new StringBuilder();
		dbg.append("serieOid=").append(_serieOid).append(" (").append(_dateRange.getLowerBound()).append(" - ").append(_dateRange.getUpperBound());
		dbg.append("[SMTWTFS]=[");
		dbg.append(this.isSunday() ? "S" : "x");
		dbg.append(this.isMonday() ? "M" : "x");
		dbg.append(this.isTuesday() ? "T" : "x");
		dbg.append(this.isWednesday() ? "W" : "x");
		dbg.append(this.isThursday() ? "T" : "x");
		dbg.append(this.isFriday() ? "F" : "x");
		dbg.append(this.isSaturday() ? "S" : "x");
		dbg.append("]");
		return dbg;
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("serial")
	private class WeekDaysBitMap
		  extends BitMap {
		
		public WeekDaysBitMap(final int bitMap) {
			super(bitMap);
			_weekDaysBitMap = this.getBitMap();		// update the state data!
		}
		public void setWeekDay(final int bitIndex) {
			this.setBitAt(bitIndex);
			_weekDaysBitMap = this.getBitMap();		// update the state data!
		}
		public boolean isWeekDaySet(final int bitIndex) {
			return this.bitAt(bitIndex);
		}
	}
}
