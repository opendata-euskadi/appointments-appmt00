package aa14f.model.config.business;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

import aa14f.model.AA14ModelObject;
import aa14f.model.config.AA14NotifierFromConfig;
import aa14f.model.config.AA14NotifierMessageComposingConfig;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.AA14ScheduleBookingConfig;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.summaries.AA14SummarizedOrgHierarchy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Memoized;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="businessConfig")
@Slf4j
@Accessors(prefix="_")
public class AA14BusinessConfig 
  implements AA14ModelObject {

	private static final long serialVersionUID = 1405445073425812594L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter AA14BusinessID _id;
	
	@MarshallField(as="organization")
	@Getter @Setter private AA14Organization _organization;
	
	@MarshallField(as="divisions",
				   whenXml=@MarshallFieldAsXml(collectionElementName="division"))
	@Getter @Setter private Collection<AA14OrgDivision> _divisions;
	
	@MarshallField(as="services",
				   whenXml=@MarshallFieldAsXml(collectionElementName="service"))
	@Getter @Setter private Collection<AA14OrgDivisionService> _services;
	
	@MarshallField(as="locations",
				   whenXml=@MarshallFieldAsXml(collectionElementName="location"))
	@Getter @Setter private Collection<AA14OrgDivisionServiceLocation> _locations;
	
	@MarshallField(as="schedules",
				   whenXml=@MarshallFieldAsXml(collectionElementName="schedule"))
	@Getter @Setter private Collection<AA14Schedule> _schedules;
/////////////////////////////////////////////////////////////////////////////////////////
//	MEMOIZED
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter(AccessLevel.PROTECTED) 
	private final transient Memoized<Map<AA14OrgDivisionOID,AA14OrgDivision>> _memoizedDivisionsByOid = 
						new Memoized<Map<AA14OrgDivisionOID,AA14OrgDivision>>() {
								@Override
								public Map<AA14OrgDivisionOID,AA14OrgDivision> supply() {
									Map<AA14OrgDivisionOID,AA14OrgDivision> outMap = Maps.newHashMapWithExpectedSize(_divisions.size());
									for (AA14OrgDivision div : _divisions) {
										outMap.put(div.getOid(),div);
									}
									return outMap;
								}
						};
	@Getter(AccessLevel.PROTECTED) 
	private final transient Memoized<Map<AA14OrgDivisionServiceOID,AA14OrgDivisionService>> _memoizedServicesByOid = 
						new Memoized<Map<AA14OrgDivisionServiceOID,AA14OrgDivisionService>>() {
								@Override
								public Map<AA14OrgDivisionServiceOID,AA14OrgDivisionService> supply() {
									Map<AA14OrgDivisionServiceOID,AA14OrgDivisionService> outMap = Maps.newHashMapWithExpectedSize(_services.size());
									for (AA14OrgDivisionService srvc : _services) {
										outMap.put(srvc.getOid(),srvc);
									}
									return outMap;
								}
						};
	@Getter(AccessLevel.PROTECTED) 
	private final transient Memoized<Map<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocation>> _memoizedLocationsByOid = 
						new Memoized<Map<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocation>>() {
								@Override
								public Map<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocation> supply() {
									Map<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocation> outMap = Maps.newHashMapWithExpectedSize(_locations.size());
									for (AA14OrgDivisionServiceLocation loc : _locations) {
										outMap.put(loc.getOid(),loc);
									}
									return outMap;
								}
						};
	@Getter(AccessLevel.PROTECTED) 
	private final Memoized<Map<AA14ScheduleOID,AA14Schedule>> _memoizedSchedulesByOid = 
						new Memoized<Map<AA14ScheduleOID,AA14Schedule>>() {
								@Override
								public Map<AA14ScheduleOID,AA14Schedule> supply() {
									Map<AA14ScheduleOID,AA14Schedule> outMap = Maps.newHashMapWithExpectedSize(_schedules.size());
									for (AA14Schedule sch : _schedules) {
										outMap.put(sch.getOid(),sch);
									}
									return outMap;
								}
						};
	private final Memoized<Map<AA14OrgDivisionServiceLocationOID,AA14ScheduleBookingConfig>> _scheduleBookingConfigByLocationOid = 
				new Memoized<Map<AA14OrgDivisionServiceLocationOID,AA14ScheduleBookingConfig>>() {
						@Override
						public Map<AA14OrgDivisionServiceLocationOID,AA14ScheduleBookingConfig> supply() {
							Map<AA14OrgDivisionServiceLocationOID,AA14ScheduleBookingConfig> outMap = Maps.newHashMapWithExpectedSize(_memoizedLocationsByOid.get().size());
							for (AA14OrgDivisionServiceLocation loc : _memoizedLocationsByOid.get().values()) {
								outMap.put(loc.getOid(),
										   _locationScheduleBookingConfigFor(loc));
							}
							return outMap;
						}
				};
	private final Memoized<Map<AA14OrgDivisionServiceLocationID,AA14ScheduleBookingConfig>> _scheduleBookingConfigByLocationId = 
				new Memoized<Map<AA14OrgDivisionServiceLocationID,AA14ScheduleBookingConfig>>() {
						@Override
						public Map<AA14OrgDivisionServiceLocationID,AA14ScheduleBookingConfig> supply() {
							Map<AA14OrgDivisionServiceLocationID,AA14ScheduleBookingConfig> outMap = Maps.newHashMapWithExpectedSize(_memoizedLocationsByOid.get().size());
							for (AA14OrgDivisionServiceLocation loc : _memoizedLocationsByOid.get().values()) {
								outMap.put(loc.getId(),
										   _locationScheduleBookingConfigFor(loc));
							}
							return outMap;
						}
				};
	private AA14ScheduleBookingConfig _locationScheduleBookingConfigFor(final AA14OrgDivisionServiceLocation loc) {
		Collection<AA14ScheduleOID> schOids = loc.getSchedulesOids();
		if (CollectionUtils.isNullOrEmpty(schOids)) throw new IllegalStateException("The location with oid/id=" + loc.getOid() + "/" + loc.getId() + " does NOT have any schedule!!");
								
		// load all schedules and ensure they all have the same booking config
		Collection<AA14ScheduleBookingConfig> bookingCfgs = FluentIterable.from(schOids)
																	.transform(new Function<AA14ScheduleOID,AA14ScheduleBookingConfig>() {
																						@Override
																						public AA14ScheduleBookingConfig apply(final AA14ScheduleOID schOid) {
																								return _memoizedSchedulesByOid.get()
																															  .get(schOid)
																														 	  .getBookingConfig();
																						}
																			   })
																	.toList();
		// ensure all schedule booking config are compatible
		AA14ScheduleBookingConfig outBookingCfg = CollectionUtils.pickOneElement(bookingCfgs);
		for (AA14ScheduleBookingConfig bookingCfg : bookingCfgs) {
			if (!bookingCfg.hasSameDataAs(outBookingCfg)) throw new IllegalStateException("Error in schedule booking config for location with oid/id=" + loc.getOid() + "/" + loc.getId() +
																						  " NOT all schedulles for this location has the same booking config");
		}
		// return 
		return outBookingCfg;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DIVISIONS
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrgDivision getDivisionFor(final AA14OrgDivisionOID divOid) {
		return this.getMemoizedDivisionsByOid().get()
											   .get(divOid);
	}
	public AA14OrgDivision getDivisionFor(final AA14OrgDivisionID divId) {
		return FluentIterable.from(this.getMemoizedDivisionsByOid().get().values())
							 .filter(new Predicate<AA14OrgDivision>() {
												@Override
												public boolean apply(final AA14OrgDivision div) {
													return div.getId().equals(divId);
												}
							 		 })
							 .first().orNull();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrgDivisionService getServiceFor(final AA14OrgDivisionServiceOID srvcOid) {
		return this.getMemoizedServicesByOid().get()
											  .get(srvcOid);
	}
	public AA14OrgDivisionService getServiceFor(final AA14OrgDivisionServiceID srvcId) {
		return FluentIterable.from(this.getMemoizedServicesByOid().get().values())
							 .filter(new Predicate<AA14OrgDivisionService>() {
												@Override
												public boolean apply(final AA14OrgDivisionService srvc) {
													return srvc.getId().equals(srvcId);
												}
							 		 })
							 .first().orNull();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LOCATIONS
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14OrgDivisionServiceLocation getLocationFor(final AA14OrgDivisionServiceLocationOID locOid) {
		return this.getMemoizedLocationsByOid().get()
							  		   		   .get(locOid);
	}
	public AA14OrgDivisionServiceLocation getLocationFor(final AA14OrgDivisionServiceLocationID locId) {
		return FluentIterable.from(this.getMemoizedLocationsByOid().get().values())
							 .filter(new Predicate<AA14OrgDivisionServiceLocation>() {
												@Override
												public boolean apply(final AA14OrgDivisionServiceLocation loc) {
													return loc.getId().equals(locId);
												}
							 		 })
							 .first().orNull();
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
//	SCHEDULES
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14Schedule getScheduleFor(final AA14ScheduleOID schOid) {
		return this.getMemoizedSchedulesByOid().get()
							  		   		   .get(schOid);
	}
	public AA14Schedule getScheduleFor(final AA14ScheduleID schId) {
		return FluentIterable.from(this.getMemoizedSchedulesByOid().get().values())
							 .filter(new Predicate<AA14Schedule>() {
												@Override
												public boolean apply(final AA14Schedule sch) {
													return sch.getId().equals(schId);
												}
							 		 })
							 .first().orNull();
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
		AA14ScheduleBookingConfig outCfg = _scheduleBookingConfigByLocationOid.get()
																			  .get(locOid);
		if (outCfg == null) log.error("There does NOT exists booking config for location with oid={}",locOid);
		return outCfg;
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
		AA14ScheduleBookingConfig outCfg = _scheduleBookingConfigByLocationId.get()
																			 .get(locId);
		if (outCfg == null) log.error("There does NOT exists booking config for location with id={}",locId);
		return outCfg;
	}
	/**
	 * Loads the booking config for a certain schedule
	 * @param schOid
	 * @return the booking config or null
	 */
	public AA14ScheduleBookingConfig getScheduleBookingConfigFor(final AA14ScheduleOID schOid) {
		AA14Schedule sch = this.getScheduleFor(schOid);
		return sch.getBookingConfig();
	}
	/**
	 * Loads the booking config for a certain schedule
	 * @param schId
	 * @return the booking config or null
	 */
	public AA14ScheduleBookingConfig getScheduleBookingConfigFor(final AA14ScheduleID schId) {
		AA14Schedule sch = this.getScheduleFor(schId);
		return sch.getBookingConfig();
	}
	public Map<AA14ScheduleOID,AA14Schedule> getSchedulesByOid() {
		return this.getMemoizedSchedulesByOid().get();
	}
	public Map<AA14ScheduleID,AA14Schedule> getSchedulesById() {
		Map<AA14ScheduleOID,AA14Schedule> schsByOid = this.getSchedulesByOid();
		Map<AA14ScheduleID,AA14Schedule> outMap = Maps.newHashMapWithExpectedSize(schsByOid.size());
		for (AA14Schedule sch : schsByOid.values()) {
			outMap.put(sch.getId(),sch);
		}
		return outMap;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  HIERARCHY
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14SummarizedOrgHierarchy getSummarizedOrgHierarchyFor(final AA14OrgDivisionServiceLocationOID locOid,
																   final Language lang) {
		Map<Language,AA14SummarizedOrgHierarchy> orgHierarchy = _summarizedOrgHierarchyByLocationOid.get()
							  		   				  				.get(locOid);
		return orgHierarchy != null ? orgHierarchy.get(lang) : null;
	}
	public AA14SummarizedOrgHierarchy getSummarizedOrgHierarchyFor(final AA14OrgDivisionServiceLocationID locId,
																   final Language lang) {
		return AA14BusinessConfig.filterOrgHierarchyForLocationWithId(_summarizedOrgHierarchyByLocationOid.get(),
																  	  locId,
																  	  lang);
	}
	@Getter private final Memoized<Map<AA14OrgDivisionServiceLocationOID,
					 	 	     Map<Language,AA14SummarizedOrgHierarchy>>> _summarizedOrgHierarchyByLocationOid = 
					 	 new Memoized<Map<AA14OrgDivisionServiceLocationOID,
					 					  Map<Language,AA14SummarizedOrgHierarchy>>>() {
									@Override
									public Map<AA14OrgDivisionServiceLocationOID,
											   Map<Language,AA14SummarizedOrgHierarchy>> supply() {
										Map<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocation> locsByOid = _memoizedLocationsByOid.get();
										Map<AA14OrgDivisionServiceLocationOID,
											Map<Language,AA14SummarizedOrgHierarchy>> outMap = Maps.newHashMapWithExpectedSize(locsByOid.size());
										
										for (AA14OrgDivisionServiceLocation loc : locsByOid.values()) {
											outMap.put(loc.getOid(),
													   _orgHierarchy(loc.getOrgRef().getOid(),
																	 loc.getOrgDivisionRef().getOid(),
																	 loc.getOrgDivisionServiceRef().getOid(),
																	 loc.getOid()));
										}
										return outMap;
									}
						};
	public static AA14SummarizedOrgHierarchy filterOrgHierarchyForLocationWithId(final Map<AA14OrgDivisionServiceLocationOID,
					 	 	     														   Map<Language,AA14SummarizedOrgHierarchy>> summarizedOrgHierarchyByLocationOid,
					 	 	     												 final AA14OrgDivisionServiceLocationID locId,
					 	 	     												 final Language lang) {
		Map<Language,AA14SummarizedOrgHierarchy> orgHierarchy = FluentIterable.from(summarizedOrgHierarchyByLocationOid.values())
																			  .filter(new Predicate<Map<Language,AA14SummarizedOrgHierarchy>>() {
																							@Override
																							public boolean apply(final Map<Language,AA14SummarizedOrgHierarchy> sumByLang) {
																								return sumByLang.get(lang) != null
																									&& sumByLang.get(lang).getLocation().getId().is(locId);
																							}
																			 		  })
																			  .first().orNull();
		if (orgHierarchy == null) log.error("There does NOT exists a location with id={}",locId);
		return orgHierarchy != null ? orgHierarchy.get(lang) : null;
	}
	protected Map<Language,AA14SummarizedOrgHierarchy> _orgHierarchy(final AA14OrganizationOID orgOid,final AA14OrgDivisionOID divOid,final AA14OrgDivisionServiceOID srvcOid,final AA14OrgDivisionServiceLocationOID locOid) {
		Map<Language,AA14SummarizedOrgHierarchy> outMap = Maps.newHashMapWithExpectedSize(2);
		outMap.put(Language.SPANISH,
				   _orgHierarchy(orgOid,divOid,srvcOid,locOid,
						   		 Language.SPANISH));
		outMap.put(Language.BASQUE,
				   _orgHierarchy(orgOid,divOid,srvcOid,locOid,
						   		 Language.BASQUE));
		return outMap;
	}
	protected AA14SummarizedOrgHierarchy _orgHierarchy(final AA14OrganizationOID orgOid,final AA14OrgDivisionOID divOid,final AA14OrgDivisionServiceOID srvcOid,final AA14OrgDivisionServiceLocationOID locOid,
													   final Language lang) {
		AA14SummarizedOrgHierarchy outHierarchy = new AA14SummarizedOrgHierarchy();
		
		AA14Organization org = this.getOrganization();
		AA14OrgDivision div = this.getDivisionFor(divOid);
		AA14OrgDivisionService srvc = this.getServiceFor(srvcOid);
		AA14OrgDivisionServiceLocation loc = this.getLocationFor(locOid);
		
		outHierarchy.setOrganization(org.getSummarizedIn(lang));
		outHierarchy.setDivision(div.getSummarizedIn(lang));
		outHierarchy.setService(srvc.getSummarizedIn(lang));
		outHierarchy.setLocation(loc.getSummarizedIn(lang));
		return outHierarchy;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	NOTIFICATION CONFIG
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14NotifierFromConfig getNotifierFromConfigFor(final AA14OrganizationOID orgOid,final AA14OrgDivisionOID divOid,final AA14OrgDivisionServiceOID srvcOid) {
		AA14Organization org = this.getOrganization();
		AA14OrgDivision div = this.getDivisionFor(divOid);
		AA14OrgDivisionService srvc = this.getServiceFor(srvcOid);
		
		return this.getNotifierFromConfigFor(org,div,srvc);
	}
	public AA14NotifierFromConfig getNotifierFromConfigFor(final AA14OrganizationID orgId,final AA14OrgDivisionID divId,final AA14OrgDivisionServiceID srvcId) {
		AA14Organization org = this.getOrganization();
		AA14OrgDivision div = this.getDivisionFor(divId);
		AA14OrgDivisionService srvc = this.getServiceFor(srvcId);
		
		return this.getNotifierFromConfigFor(org,div,srvc);
	}
	public AA14NotifierFromConfig getNotifierFromConfigFor(final AA14Organization org,final AA14OrgDivision div,final AA14OrgDivisionService srvc) {
		AA14NotifierFromConfig outCfg = srvc != null ? srvc.getNotifierFromConfig() : null;
		if (outCfg == null) outCfg = div != null ? div.getNotifierFromConfig() : null;
		if (outCfg == null) outCfg = org != null ? org.getNotifierFromConfig() : null;
		return outCfg;
	}
	public AA14NotifierMessageComposingConfig getNotifierMessageComposingConfigFor(final AA14OrganizationOID orgOid,final AA14OrgDivisionOID divOid,final AA14OrgDivisionServiceOID srvcOid) {
		AA14Organization org = this.getOrganization();
		AA14OrgDivision div = this.getDivisionFor(divOid);
		AA14OrgDivisionService srvc = this.getServiceFor(srvcOid);
		
		return this.getNotifierMessageComposingConfigFor(org,div,srvc);
	}
	public AA14NotifierMessageComposingConfig getNotifierMessageComposingConfigFor(final AA14OrganizationID orgId,final AA14OrgDivisionID divId,final AA14OrgDivisionServiceID srvcId) {
		AA14Organization org = this.getOrganization();
		AA14OrgDivision div = this.getDivisionFor(divId);
		AA14OrgDivisionService srvc = this.getServiceFor(srvcId);
		
		return this.getNotifierMessageComposingConfigFor(org,div,srvc);
	}
	public AA14NotifierMessageComposingConfig getNotifierMessageComposingConfigFor(final AA14Organization org,final AA14OrgDivision div,final AA14OrgDivisionService srvc) {
		AA14NotifierMessageComposingConfig outCfg = srvc != null ? srvc.getNotifierMessageComposingConfig() : null;
		if (outCfg == null) outCfg = div != null ? div.getNotifierMessageComposingConfig() : null;
		if (outCfg == null) outCfg = org != null ? org.getNotifierMessageComposingConfig() : null;
		return outCfg;
	}
}
