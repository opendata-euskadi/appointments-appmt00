package aa14f.model.summaries;

import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14BookedSlotType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="summarizedBookedSlot")
@Accessors(prefix="_")
public class AA14SummarizedBookedSlot 
	 extends AA14SummarizedBookedSlotBase<AA14BookedSlot,
	 									  AA14SummarizedBookedSlot> {

	private static final long serialVersionUID = -4373243410730886004L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="subject",escape=true)
	@Getter @Setter private String _subject;
	
	@MarshallField(as="summary",escape=true)
	@Getter @Setter private String _summary;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14SummarizedBookedSlot(final AA14BookedSlotType type) {
		super(AA14BookedSlot.class,
			  type);
	}
	public static AA14SummarizedBookedSlot create(final AA14BookedSlotType type) {
		return new AA14SummarizedBookedSlot(type);
	}
	public AA14SummarizedBookedSlot subject(final String subject) {
		_subject = subject;
		return this;
	}
	public AA14SummarizedBookedSlot summary(final String summary) {
		_summary = summary;
		return this;
	}
}
