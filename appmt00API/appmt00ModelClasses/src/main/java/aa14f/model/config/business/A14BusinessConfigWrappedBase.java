package aa14f.model.config.business;

import java.util.Collection;
import java.util.Map;

import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.AA14ScheduleBookingConfig;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.summaries.AA14SummarizedOrgHierarchy;
import r01f.locale.Language;

abstract class A14BusinessConfigWrappedBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	protected final AA14BusinessConfig _wrappedConfig;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public A14BusinessConfigWrappedBase(final AA14BusinessConfig wrapped) {
		_wrappedConfig = wrapped;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DIVISIONS
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrgDivision getDivisionFor(final AA14OrgDivisionOID divOid) {
		return _wrappedConfig.getDivisionFor(divOid);
	}
	public AA14OrgDivision getDivisionFor(final AA14OrgDivisionID divId) {
		return _wrappedConfig.getDivisionFor(divId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrgDivisionService getServiceFor(final AA14OrgDivisionServiceOID srvcOid) {
		return _wrappedConfig.getServiceFor(srvcOid);
	}
	public AA14OrgDivisionService getServiceFor(final AA14OrgDivisionServiceID srvcId) {
		return _wrappedConfig.getServiceFor(srvcId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LOCATIONS
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrgDivisionServiceLocation getLocationFor(final AA14OrgDivisionServiceLocationOID locOid) {
		return _wrappedConfig.getLocationFor(locOid);
	}
	public AA14OrgDivisionServiceLocation getLocationFor(final AA14OrgDivisionServiceLocationID locId) {
		return _wrappedConfig.getLocationFor(locId);
	}
	/**
	 * Returns the locations for a given schedule
	 * @param schId
	 * @return
	 */
	public Collection<AA14OrgDivisionServiceLocationOID> getLocationsOidsFor(final AA14ScheduleID schId) {
		return _wrappedConfig.getLocationsOidsFor(schId);
	}
	/**
	 * Returns the locations for a given schedule
	 * @param schId
	 * @return
	 */
	public Collection<AA14OrgDivisionServiceLocationID> getLocationsIdsFor(final AA14ScheduleID schId) {
		return _wrappedConfig.getLocationsIdsFor(schId);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SCHEDULES
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14Schedule getScheduleFor(final AA14ScheduleOID schOid) {
		return _wrappedConfig.getScheduleFor(schOid);
	}
	public AA14Schedule getScheduleFor(final AA14ScheduleID schId) {
		return _wrappedConfig.getScheduleFor(schId);
	}
	/**
	 * Loads the booking config for a certain location
	 *	   BEWARE!	The calendar / booking config is associated with the SCHEDULE, not with the LOCATION or SERVICE,
	 *				any location can be associated with multiple schedules BUT all of them MUST have
	 *				the same booking config
	 * @param locOid locationOid
	 * @return the booking config or null
	 */
	public AA14ScheduleBookingConfig getScheduleBookingConfigFor(final AA14OrgDivisionServiceLocationOID locOid) {
		return _wrappedConfig.getScheduleBookingConfigFor(locOid);
	}
	/**
	 * Loads the booking config for a certain location
	 *	   BEWARE!	The calendar / booking config is associated with the SCHEDULE, not with the LOCATION or SERVICE,
	 *				any location can be associated with multiple schedules BUT all of them MUST have
	 *				the same booking config
	 * @param locOid locationOid
	 * @return the booking config or null
	 */
	public AA14ScheduleBookingConfig getScheduleBookingConfigFor(final AA14OrgDivisionServiceLocationID locId) {
		return _wrappedConfig.getScheduleBookingConfigFor(locId);
	}
	/**
	 * Loads the booking config for a certain schedule
	 * @param schOid
	 * @return the booking config or null
	 */
	public AA14ScheduleBookingConfig getScheduleBookingConfigFor(final AA14ScheduleOID schOid) {
		return _wrappedConfig.getScheduleBookingConfigFor(schOid);
	}
	/**
	 * Loads the booking config for a certain schedule
	 * @param schId
	 * @return the booking config or null
	 */
	public AA14ScheduleBookingConfig getScheduleBookingConfigFor(final AA14ScheduleID schId) {
		return _wrappedConfig.getScheduleBookingConfigFor(schId);
	}
	public Map<AA14ScheduleOID,AA14Schedule> getSchedulesByOid() {
		return _wrappedConfig.getSchedulesByOid();
	}
	public Map<AA14ScheduleID,AA14Schedule> getSchedulesById() {
		return _wrappedConfig.getSchedulesById();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  HIERARCHY
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14SummarizedOrgHierarchy getSummarizedOrgHierarchyFor(final AA14OrgDivisionServiceLocationOID locOid,
																   final Language lang) {
		return _wrappedConfig.getSummarizedOrgHierarchyFor(locOid,lang);
	}
	public AA14SummarizedOrgHierarchy getSummarizedOrgHierarchyFor(final AA14OrgDivisionServiceLocationID locId,
																   final Language lang) {
		return _wrappedConfig.getSummarizedOrgHierarchyFor(locId,lang);
	}	
}
