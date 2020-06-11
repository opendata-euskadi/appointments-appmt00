package aa14f.model.search;

import java.util.Collection;
import java.util.Date;

import com.google.common.collect.Lists;

import aa14f.model.AA14BookedSlotType;
import aa14f.model.AA14ModelObject;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.Range;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="bookedSlotFilter")
@Accessors(prefix="_")
public class AA14BookedSlotFilter 
  implements AA14ModelObject,
  			 Debuggable {

	private static final long serialVersionUID = 5897311908265841357L;
/////////////////////////////////////////////////////////////////////////////////////////
//	ORGANIZATION & DIVISION & SERVICE & LOCATION
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="orgId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AA14OrganizationID _organizationId;
	
	@MarshallField(as="divisionId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AA14OrgDivisionID _divisionId;
	
	@MarshallField(as="serviceId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AA14OrgDivisionServiceID _serviceId;
	
	@MarshallField(as="locationId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AA14OrgDivisionServiceLocationID _serviceLocationId;
/////////////////////////////////////////////////////////////////////////////////////////
//	BOOKED SLOT TYPE
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="bookedSlotTypes",
				   whenXml=@MarshallFieldAsXml(collectionElementName="type"))
	@Getter @Setter private Collection<AA14BookedSlotType> _bookedSlotTypes;	
/////////////////////////////////////////////////////////////////////////////////////////
//	DATE RANGE
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallField(as="dateRange",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Range<Date> _dateRange;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14BookedSlotFilter() {
		// default no-args constructor
	}
	public AA14BookedSlotFilter(final AA14OrganizationID orgId,final AA14OrgDivisionID divId,final AA14OrgDivisionServiceID serviceId,final AA14OrgDivisionServiceLocationID serviceLocId,
								final Range<Date> dateRange,
								final Collection<AA14BookedSlotType> bookedSlotTypes) {
		_organizationId = orgId;
		_divisionId = divId;
		_serviceId = serviceId;
		_serviceLocationId = serviceLocId;
		_dateRange = dateRange;
		_bookedSlotTypes = bookedSlotTypes;
	}
	public AA14BookedSlotFilter(final AA14OrganizationID orgId,final AA14OrgDivisionID divId,final AA14OrgDivisionServiceID serviceId,final AA14OrgDivisionServiceLocationID serviceLocId,
								final Range<Date> dateRange,
								final AA14BookedSlotType... bookedSlotTypes) {
		this(orgId,divId,serviceId,serviceLocId,
			 dateRange,
			 CollectionUtils.hasData(bookedSlotTypes) ? Lists.newArrayList(bookedSlotTypes) : null);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("slot filter by orgId={} divisionId={} serviceId={} location={} dateRange={} bookedSlotTypes={}",
								  _organizationId,_divisionId,_serviceId,_serviceLocationId,
								  _dateRange != null ? _dateRange.asString() : null,
								  _bookedSlotTypes);
	}
}
