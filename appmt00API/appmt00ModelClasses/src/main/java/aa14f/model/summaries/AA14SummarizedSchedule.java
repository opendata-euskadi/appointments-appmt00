package aa14f.model.summaries;

import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="summarizedSchedule")
@Accessors(prefix="_")
public class AA14SummarizedSchedule 
	 extends AA14SummarizedModelObjectBase<AA14ScheduleOID,AA14ScheduleID,AA14Schedule,
	 									   AA14SummarizedSchedule> {

	private static final long serialVersionUID = -4373243410730886004L;
/////////////////////////////////////////////////////////////////////////////////////////
//  SERIALIZABLE FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="name",escape=true)
	@Getter @Setter private String _name;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14SummarizedSchedule() {
		super(AA14Schedule.class);
	}
	public static AA14SummarizedSchedule create() {
		return new AA14SummarizedSchedule();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT API
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14SummarizedSchedule named(final String name) {
		_name = name;
		return this;
	}
}
