package aa14f.model.config;

import aa14f.model.AA14ModelObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;

@MarshallType(as="qMaticOrchestraConfig")
@ConvertToDirtyStateTrackable			// changes in state are tracked
@Accessors(prefix="_")
@NoArgsConstructor 
public class AA14ScheduleOrchestraConfig 
  implements AA14ModelObject {

	private static final long serialVersionUID = 41430989014943567L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="enabled", 
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private boolean _enabled = true;	// true by default
	
	@MarshallField(as="branchId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _branchId;
	
	@MarshallField(as="serviceId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private String _serviceId;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ScheduleOrchestraConfig(final String branchId,final String serviceId) {
		this(true,	// enabled by default
			 branchId,serviceId);
	}
	public AA14ScheduleOrchestraConfig(final boolean enabled,
														 final String branchId,final String serviceId) {
		_enabled = enabled;
		_branchId = branchId;
		_serviceId = serviceId;
	}
}
