package aa14f.model.config.business;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import aa14f.model.AA14ModelObject;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.AA14ScheduleBookingConfig;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.summaries.AA14SummarizedOrgHierarchy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Memoized;

@MarshallType(as="businessConfigs")
@Accessors(prefix="_")
public class AA14BusinessConfigs 
  implements AA14ModelObject {

	private static final long serialVersionUID = -1097396612901829931L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallField(as="configs",
				   whenXml=@MarshallFieldAsXml(collectionElementName="config"))
	@Getter @Setter private Collection<AA14BusinessConfig> _business;
/////////////////////////////////////////////////////////////////////////////////////////
//	MEMOIZED
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter(AccessLevel.PRIVATE) 
	private final transient Memoized<Map<AA14BusinessID,AA14BusinessConfig>> _memoizedBusinessByOid = 
						new Memoized<Map<AA14BusinessID,AA14BusinessConfig>>() {
								@Override
								public Map<AA14BusinessID,AA14BusinessConfig> supply() {
									Map<AA14BusinessID,AA14BusinessConfig> outMap = Maps.newHashMapWithExpectedSize(_business.size());
									for (AA14BusinessConfig b : _business) {
										outMap.put(b.getId(),b);
									}
									return outMap;
								}
						};
/////////////////////////////////////////////////////////////////////////////////////////
//	ACCESS
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14BusinessConfig getFor(final AA14BusinessID id) {
		return _memoizedBusinessByOid.get()
									 .get(id);
	}
	public AA14BusinessConfig getFor(final AA14OrganizationID orgId) {
		return FluentIterable.from(_memoizedBusinessByOid.get()
														 .values())
							 .firstMatch(new Predicate<AA14BusinessConfig>() {
												@Override
												public boolean apply(final AA14BusinessConfig cfg) {
													return cfg.getOrganization() != null 
														&& cfg.getOrganization().getId().is(orgId);
												}
							 		 	 })
							 .orNull();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LOCATION ACCESS
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrgDivisionServiceLocation getLocationFor(final AA14OrgDivisionServiceLocationOID oid) {
		if (_business == null) return null;
		AA14OrgDivisionServiceLocation outLoc = null;
		for (AA14BusinessConfig cfg : _business) {
			outLoc = cfg.getLocationFor(oid);
			if (outLoc != null) break;
		}
		return outLoc;
	}
	public AA14OrgDivisionServiceLocation getLocationFor(final AA14OrgDivisionServiceLocationID id) {
		if (_business == null) return null;
		AA14OrgDivisionServiceLocation outLoc = null;
		for (AA14BusinessConfig cfg : _business) {
			outLoc = cfg.getLocationFor(id);
			if (outLoc != null) break;
		}
		return outLoc;
	}
	public Collection<AA14OrgDivisionServiceLocation> getAllLocations() {
		if (_business == null) return null;
		Collection<AA14OrgDivisionServiceLocation> allLocs = Lists.newArrayList();
		for (AA14BusinessConfig cfg : _business) {
			if (cfg.getLocations() != null) allLocs.addAll(cfg.getLocations());
		}
		return allLocs;
	}
	/**
	 * Returns the locations for a given schedule
	 * @param schId
	 * @return
	 */
	public Collection<AA14OrgDivisionServiceLocationOID> getLocationsOidsFor(final AA14ScheduleID schId) {
		AA14Schedule sch = this.getScheduleFor(schId);
		return sch.getServiceLocationsOids();
	}
	/**
	 * Returns the locations for a given schedule
	 * @param schId
	 * @return
	 */
	public Collection<AA14OrgDivisionServiceLocationID> getLocationsIdsFor(final AA14ScheduleID schId) {
		AA14Schedule sch = this.getScheduleFor(schId);
		return sch.getServiceLocationsIds();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SCHEDULE
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14Schedule getScheduleFor(final AA14ScheduleOID schOid) {
		if (_business == null) return null;
		AA14Schedule outSch = null;
		for (AA14BusinessConfig cfg : _business) {
			outSch = cfg.getScheduleFor(schOid);
			if (outSch != null) break;
		}
		return outSch;
	}
	public AA14Schedule getScheduleFor(final AA14ScheduleID schId) {
		if (_business == null) return null;
		AA14Schedule outSch = null;
		for (AA14BusinessConfig cfg : _business) {
			outSch = cfg.getScheduleFor(schId);
			if (outSch != null) break;
		}
		return outSch;
	}
	public AA14ScheduleBookingConfig getScheduleBookingConfigFor(final AA14ScheduleOID schOid) {
		AA14Schedule sch = this.getScheduleFor(schOid);
		return sch.getBookingConfig();
	}
	public AA14ScheduleBookingConfig getScheduleBookingConfigFor(final AA14ScheduleID schId) {
		AA14Schedule sch = this.getScheduleFor(schId);
		return sch.getBookingConfig();
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
		if (_business == null) return null;
		AA14ScheduleBookingConfig outSch = null;
		for (AA14BusinessConfig cfg : _business) {
			outSch = cfg.getScheduleBookingConfigFor(locOid);
			if (outSch != null) break;
		}
		return outSch;
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
		if (_business == null) return null;
		AA14ScheduleBookingConfig outSch = null;
		for (AA14BusinessConfig cfg : _business) {
			outSch = cfg.getScheduleBookingConfigFor(locId);
			if (outSch != null) break;
		}
		return outSch;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ORG HIERARCH
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14SummarizedOrgHierarchy getSummarizedOrgHierarchyFor(final AA14OrgDivisionServiceLocationOID locOid,
																   final Language lang) {
		if (_business == null) return null;
		AA14SummarizedOrgHierarchy outHierarchy = null;
		for (AA14BusinessConfig cfg : _business) {
			outHierarchy = cfg.getSummarizedOrgHierarchyFor(locOid,lang);
			if (outHierarchy != null) break;
		}
		return outHierarchy;
	}
	public AA14SummarizedOrgHierarchy getSummarizedOrgHierarchyFor(final AA14OrgDivisionServiceLocationID locId,
																   final Language lang) {
		if (_business == null) return null;
		AA14SummarizedOrgHierarchy outHierarchy = null;
		for (AA14BusinessConfig cfg : _business) {
			outHierarchy = cfg.getSummarizedOrgHierarchyFor(locId,lang);
			if (outHierarchy != null) break;
		}
		return outHierarchy;
	}
}
