package aa14a.model.view;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import aa14f.model.summaries.AA14SummarizedBookedSlot;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.locale.Language;

@Accessors(prefix="_")
public class AA14CalendarEvents {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter @Setter private Collection<AA14CalendarEvent> _events;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14CalendarEvents() {
		_events = Lists.newLinkedList();
	}
	public AA14CalendarEvents(final Collection<AA14CalendarEvent> events) {
		_events = Lists.newLinkedList(events);
	}
	public AA14CalendarEvents(final AA14CalendarEvent... events) {
		_events = Lists.newLinkedList(Arrays.asList(events));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	public static AA14CalendarEvents of(final Collection<AA14CalendarEvent> events) {
		return new AA14CalendarEvents(events);
	}
	public static AA14CalendarEvents of(final AA14CalendarEvent... events) {
		return new AA14CalendarEvents(events);
	}
	public static AA14CalendarEvents from(final Collection<AA14SummarizedBookedSlot> bookedSlots,
//										  final I18NService i18nService,
										  final Language lang) {
		Collection<AA14CalendarEvent> events = FluentIterable.from(bookedSlots)
															 .transform(new Function<AA14SummarizedBookedSlot,AA14CalendarEvent>() {
																				@Override
																				public AA14CalendarEvent apply(final AA14SummarizedBookedSlot sumSlot) {
																					return new AA14CalendarEvent(sumSlot,
//																												 i18nService,
																												 lang);
																				}
															 			})
															 .toList();
		return new AA14CalendarEvents(events);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ADD
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Adds an event
	 * @param event
	 * @return
	 */
	public AA14CalendarEvents add(final AA14CalendarEvent event) {
		_events.add(event);
		return this;
	}
	/**
	 * Adds an event
	 * @param event
	 * @return
	 */
	public AA14CalendarEvents addAll(final AA14CalendarEvent... events) {
		return this.addAll(Arrays.asList(events));
	}
	/**
	 * Adds an event
	 * @param event
	 * @return
	 */
	public AA14CalendarEvents addAll(final Collection<AA14CalendarEvent> events) {
		_events.addAll(events);
		return this;
	}
}
