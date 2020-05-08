package aa14b.calendar.orchestra.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Accessors(prefix="_")
public class AA14OrchestraAvailableSlotDates 
	 extends AA14OrchestraEntityListBase {
	@JsonProperty("dates")
	@Getter @Setter private List<Date> _dateList;
}
