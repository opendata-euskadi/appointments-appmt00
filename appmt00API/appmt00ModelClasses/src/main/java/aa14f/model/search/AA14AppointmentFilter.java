package aa14f.model.search;

import java.util.Date;

import aa14f.model.AA14ModelObject;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14PersonLocatorID;
import aa14f.model.oids.AA14OIDs.AA14AppointmentSubjectID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.Range;
import r01f.types.contact.PersonID;
import r01f.util.types.Strings;

@MarshallType(as="appointmentFilter")
@Accessors(prefix="_")
public class AA14AppointmentFilter 
  implements AA14ModelObject,
  			 Debuggable {

	private static final long serialVersionUID = 5897311908265841357L;
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICE & LOCATION
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="serviceId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AA14OrgDivisionServiceID _serviceId;
	
	@MarshallField(as="locationId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AA14OrgDivisionServiceLocationID _serviceLocationId;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	PERSON
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="personLocatorId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AA14PersonLocatorID _personLocatorId;
	
	@MarshallField(as="personId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private PersonID _personId;
	
	@MarshallField(as="subjectId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AA14AppointmentSubjectID _subjectId;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	DATE RANGE
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallField(as="dateRange",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Range<Date> _dateRange;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14AppointmentFilter() {
		// default no-args constructor
	}
	public AA14AppointmentFilter(final AA14OrgDivisionServiceID serviceId,final AA14OrgDivisionServiceLocationID serviceLocId,
								 final AA14PersonLocatorID personLocatorId,
								 final Range<Date> dateRange) {
		_serviceId = serviceId;
		_serviceLocationId = serviceLocId;
		_personLocatorId = personLocatorId;
		_dateRange = dateRange;
	}
	public AA14AppointmentFilter(final AA14OrgDivisionServiceID serviceId,final AA14OrgDivisionServiceLocationID serviceLocId,
								 final PersonID personId,
								 final Range<Date> dateRange) {
		_serviceId = serviceId;
		_serviceLocationId = serviceLocId;
		_personId = personId;
		_dateRange = dateRange;
	}
	public AA14AppointmentFilter(final AA14OrgDivisionServiceID serviceId,final AA14OrgDivisionServiceLocationID serviceLocId,
								 final AA14AppointmentSubjectID subjectId,
								 final Range<Date> dateRange) {
		_serviceId = serviceId;
		_serviceLocationId = serviceLocId;
		_subjectId = subjectId;
		_dateRange = dateRange;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("appointment filter by serviceId={} serviceLocationId={} personLocatorId={} personId={} subjectId={} dateRange={}",
								  _serviceId,_serviceLocationId,
								  _personLocatorId,_personId,
								  _subjectId,
								  _dateRange != null ? _dateRange.asString() : null);
	}
}
