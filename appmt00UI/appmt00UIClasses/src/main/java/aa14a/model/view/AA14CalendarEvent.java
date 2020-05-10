package aa14a.model.view;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.Lists;
import com.google.common.collect.Range;

import aa14f.model.AA14BookedSlotType;
import aa14f.model.AA14PeriodicSlotData;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14PeriodicSlotSerieOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import aa14f.model.summaries.AA14SummarizedBookedSlot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.types.Color;
import r01f.util.types.Dates;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * see http://fullcalendar.io/docs/event_data/Event_Object/
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY,
			    getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class AA14CalendarEvent {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@JsonProperty("id")
	@Getter @Setter private String _id;
	
	@JsonProperty("slotOid")
	@Getter @Setter private String _slotOid;
	
	@JsonProperty("locId")
	@Getter @Setter private String _locOid;
	
	@JsonProperty("schId")
	@Getter @Setter private String _schOid;
	
	@JsonProperty("kind")
	@Getter @Setter private String _kind;
	
	@JsonProperty("title")
	@Getter @Setter private String _title;
	
	@JsonProperty("subject")
	@Getter @Setter private String _subject;
	
	@JsonProperty("details")
	@Getter @Setter private String _details;
	
	@JsonProperty("allDay")
	@Getter @Setter private boolean _allDay;
	
	@JsonProperty("periodicSlotSerieOid")
	@Getter @Setter private String _periodicSlotSerieOid;		
	
	@JsonProperty("start") @JsonFormat(shape=JsonFormat.Shape.STRING,pattern=Dates.ISO8601) // timezone="CET"
	@Getter @Setter private Date _start;
	
	@JsonProperty("end") @JsonFormat(shape=JsonFormat.Shape.STRING,pattern=Dates.ISO8601)	 // timezone="CET"
	@Getter @Setter private Date _end;
	
	@JsonProperty("color")
	@Getter @Setter private String _color;
	
	@JsonProperty("editable")
	@Getter @Setter private boolean _editable = false;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14CalendarEvent(final String slotOid,
							 final String locId,final String schId,
							 final AA14BookedSlotType type,
							 final Date start,final Date end,
							 final String title,final String subject,final String details,
							 final String periodicSlotSerieOid,
							 final Color color) {
		_id = slotOid;
		
		_locOid = locId;
		_schOid = schId;
		
		_kind = type.name();
		
		_title = title;
		_subject = subject;
		_details = details;
		
		_start = start;	
		_end = end;		
		
		_periodicSlotSerieOid = periodicSlotSerieOid;
		
		// set the slot color depending on the slot type
		_color = color.getCode();
	}
	public AA14CalendarEvent(final AA14SlotOID slotOid,
							 final AA14OrgDivisionServiceLocationID locId,final AA14ScheduleID schId,
							 final AA14BookedSlotType type,
							 final Range<Date> dateRange,
							 final String title,final String subject,final String details,
							 final AA14PeriodicSlotSerieOID periodicSlotSerieOid,
							 final Color color) {
		this(slotOid.asString(),
			 locId != null ? locId.asString() : null,schId.asString(),
			 type,
			 dateRange.lowerEndpoint(),dateRange.upperEndpoint(), //new DateTime(dateRange.upperEndpoint()).plusMinutes(30).toDate();
			 title,subject,details,
			 periodicSlotSerieOid != null ? periodicSlotSerieOid.asString()
					 					  : null,
			 color);
	}
	public AA14CalendarEvent(final AA14SummarizedBookedSlot slot,
//							 final I18NService i18nService,
							 final Language lang) {
		this(slot.getOid(),
			 slot.getLocation() != null ? slot.getLocation().getId() : null,slot.getSchedule().getId(),
			 slot.getType(),
			 slot.getDateRange(),
			 slot.getSummary(),slot.getSubject(),_bookedSlotDetails(slot,
//					 												i18nService,
					 												lang),
			 slot.getPeriodicSlotData() != null ? slot.getPeriodicSlotData().getSerieOid() 
					 							: null,
			 slot.getPresentationColor());
	}
	private static String _bookedSlotDetails(final AA14SummarizedBookedSlot slot,
//											 final I18NService i18nService,
											 final Language lang) {
		String outDetails = null;
		if (slot.isPeriodic()) {
			String template = lang == Language.BASQUE ? "Erreserba periodikoa {}-tik {}-ra {} guztiak {}-tik {}-ra"
													  : "Reserva periódica desde {} hasta {} todos los {} de {} a {}";
//			String template = i18nService.forLanguage(lang)
//										 .message("comun.serie.details");
			outDetails = Strings.customized(template, 
											slot.getPeriodicSlotData().getStartDateFormatted(lang),slot.getPeriodicSlotData().getEndDateFormatted(lang),
											_periodicSlotWeekDaysDetails(slot.getPeriodicSlotData(),
//																		 i18nService,
																		 lang),
											slot.getStartTimeFormatted(),slot.getEndTimeFormatted());
		} else {
			String template = lang == Language.BASQUE ? "{}-rako erreserba {}-tik {}-ra"
													  : "Reserva para {} de {} a {}";
//			String template = i18nService.forLanguage(lang)
//										 .message("comun.single.details");
			outDetails = Strings.customized(template, 
											slot.getStartDateFormatted(lang),
											slot.getStartTimeFormatted(),slot.getEndTimeFormatted());
		}
		return outDetails;
	}
	private static String _periodicSlotWeekDaysDetails(final AA14PeriodicSlotData periodicSlotData,
//													   final I18NService i18nService,
													   final Language lang) {
		Collection<String> weekDays = Lists.newArrayList();
		if (periodicSlotData.isSunday()) weekDays.add(lang == Language.BASQUE ? "Igandea" : "Domingo");
		if (periodicSlotData.isMonday()) weekDays.add(lang == Language.BASQUE ? "Astelehena" : "Lunes");
		if (periodicSlotData.isTuesday()) weekDays.add(lang == Language.BASQUE ? "Asteartea" : "Martes");
		if (periodicSlotData.isWednesday()) weekDays.add(lang == Language.BASQUE ? "Asteazkena" : "Miércoles");
		if (periodicSlotData.isThursday()) weekDays.add(lang == Language.BASQUE ? "Osteguna" : "Jueves");
		if (periodicSlotData.isFriday()) weekDays.add(lang == Language.BASQUE ? "Ostirala" : "Viernes");
		if (periodicSlotData.isSaturday()) weekDays.add(lang == Language.BASQUE ? "Larunbata" : "Sábado");
		
//		if (periodicSlotData.isSunday()) weekDays.add(i18nService.forLanguage(lang).message("comun.sunday"));
//		if (periodicSlotData.isMonday()) weekDays.add(i18nService.forLanguage(lang).message("comun.monday"));
//		if (periodicSlotData.isTuesday()) weekDays.add(i18nService.forLanguage(lang).message("comun.tuesday"));
//		if (periodicSlotData.isWednesday()) weekDays.add(i18nService.forLanguage(lang).message("comun.wednesday"));
//		if (periodicSlotData.isThursday()) weekDays.add(i18nService.forLanguage(lang).message("comun.thursday"));
//		if (periodicSlotData.isFriday()) weekDays.add(i18nService.forLanguage(lang).message("comun.friday"));
//		if (periodicSlotData.isSaturday()) weekDays.add(i18nService.forLanguage(lang).message("comun.saturday"));
		
		String outWeekDays = null;
		if (CollectionUtils.hasData(weekDays)) {
			StringBuilder weekDaysStr = new StringBuilder(weekDays.size() * 7);
			for (Iterator<String> weekDayIt = weekDays.iterator(); weekDayIt.hasNext(); ) {
				String weekDay = weekDayIt.next();
				weekDaysStr.append(weekDay);
				if (weekDayIt.hasNext()) weekDaysStr.append(", ");
			}
			outWeekDays = weekDaysStr.toString();
		}
		return outWeekDays;
	}
}
