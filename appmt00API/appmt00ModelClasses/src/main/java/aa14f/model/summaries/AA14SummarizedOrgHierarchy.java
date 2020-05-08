package aa14f.model.summaries;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * Encapsulates the org hierarchy data
 * <pre>
 * 		[organization]
 * 			 + [division]
 * 					+ [service]
 * 						 + [location]
 */
@MarshallType(as="summarizedOrgHierarchy")
@Accessors(prefix="_")
public class AA14SummarizedOrgHierarchy 
  implements Serializable {

	private static final long serialVersionUID = 8079707472402763241L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="organization")
	@Getter @Setter private AA14SummarizedOrganization _organization;
	
	@MarshallField(as="division")
	@Getter @Setter private AA14SummarizedOrgDivision _division;
	
	@MarshallField(as="service")
	@Getter @Setter private AA14SummarizedOrgDivisionService _service;
	
	@MarshallField(as="location")
	@Getter @Setter private AA14SummarizedOrgDivisionServiceLocation _location;
}
