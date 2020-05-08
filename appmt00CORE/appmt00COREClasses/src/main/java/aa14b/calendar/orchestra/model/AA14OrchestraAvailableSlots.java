package aa14b.calendar.orchestra.model;

import java.util.List;

import org.joda.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Accessors(prefix="_")
public class AA14OrchestraAvailableSlots 
	 extends AA14OrchestraEntityListBase {
	
	@JsonProperty("times")
	@Getter @Setter private List<LocalTime> _slots;
}
