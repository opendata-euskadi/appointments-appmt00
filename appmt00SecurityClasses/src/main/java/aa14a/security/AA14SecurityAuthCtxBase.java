package aa14a.security;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.ejie.xlnets.context.XLNetsAuthCtx;
import r01f.ejie.xlnets.servlet.XLNetsAuthServletFilter;
import r01f.guids.CommonOIDs.AppCode;
import r01f.guids.CommonOIDs.BuildingCode;
import r01f.guids.CommonOIDs.UserCode;
import r01f.types.contact.EMail;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Authorization context for AA14 built from the XLNetsAuthCtx that comes attached to the request as an attribute
 * The process is:
 * 		1.- get the XLNetsAuthCtx from the request
 * 		2.- build the AA14XLNetsAuthCtx from the XLNetsAuthCtx
 * This is called at a GLOBAL jsp inc that's included at EVERY app JSP (see aa14aGlobal.inc)
 * 
 * XLNets config
 * Tenant    		| PF | Profile
 * =================|====|========================================================
 * Trafikoa  		| 01 | Agentes Zuzenean 012
 *           		| 02 | Agentes Zuzenean BI
 *           		| 03 | Agentes Zuzenean AR
 *           		| 04 | Agentes Zuzenean GI
 *           		| 05 | Trafikoa CATIT
 *           		| 06 | Trafikoa BI
 *           		| 07 | Trafikoa AR
 *           		| 08 | Trafikoa GI
 * =================|====|========================================================
 * ---------------  | 09 | -- Acceso a la aplicacion --
 * =================|====|========================================================
 * Bizilagun 		| 10 | Finanzas y depositos AR
 *           		| 11 | Comunidades y Alquileres AR
 *           		| 12 | Fianzas y depositos BIZ
 *           		| 13 | Comunidades y Alquileres BIZ
 *           		| 14 | Fianzas y depositos GI
 *           		| 15 | Comunidades y Alquileres GI
 * =================|====|========================================================
 * Blood Donation 	| 16 | Admin blood donation
 *           		| 17 | Blood Donation AR
 *           		| 18 | Blood Donation BIZ
 *           		| 19 | Blood Donation GI
 * =================|====|========================================================
 * Medical Service 	| 20 | Admin medical Service
 */
@Slf4j
@Accessors(prefix="_")
abstract class AA14SecurityAuthCtxBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private final static String XLNETS_FN_ACCESS_PROFILE = "AA14A-PF-0009";
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	// Data from aa14b.xlnets.properties.xml
	private final String _targetId;			// ie: 'trafikoa'
	private final String _resourceId;		// ie: 'AA14A-FN-0001'
	private final Predicate<String> _profileIdFilter;
	private final Function<String,AA14UIUserScheduleData> _profileIdToUserScheduleData;
	
	
	@Getter private final UserCode _userCode;
	@Getter private final boolean _authenticated;
	@Getter private final AA14UIUserScheduleData _uiUserScheduleData;
	@Getter private final AA14UIUserPersonalData _uiUserPersonalData;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a AA14XLNetsAuthCtx from the XLNetsAuthCtx xlnets context attached to the request as an attribute
	 * Beware the function that transforms the XLNets profile id to a aa14a's schedule id (see aa14aGlobal.inc)
	 * @param targetId
	 * @param resourceId
	 * @param profileIdToUserScheduleData
	 * @param req
	 */
	public AA14SecurityAuthCtxBase(final String targetId,final String resourceId,
								   final Predicate<String> profileIdFilter,
							       final Function<String,AA14UIUserScheduleData> profileIdToUserScheduleData,
							       final HttpServletRequest req) {
		if (Strings.isNullOrEmpty(targetId) || Strings.isNullOrEmpty(resourceId) || profileIdToUserScheduleData == null) throw new IllegalArgumentException();
		_targetId = targetId;
		_resourceId = resourceId;
		_profileIdFilter = profileIdFilter;
		_profileIdToUserScheduleData = profileIdToUserScheduleData;
		
		// try to get the N38 auth context 
		XLNetsAuthCtx xlnetsAuthCtx = (XLNetsAuthCtx)req.getAttribute(XLNetsAuthServletFilter.AUTHCTX_REQUESTATTR);
		if (xlnetsAuthCtx == null) {
			xlnetsAuthCtx = (XLNetsAuthCtx)req.getSession(false)
												.getAttribute(XLNetsAuthServletFilter.AUTHCTX_SESSIONATTR);
		}
		
		Collection<String> profiles = null;
		String profileId = null;
		if (xlnetsAuthCtx != null) {
			// get the user code
			_userCode = xlnetsAuthCtx.getUserCode() != null ? xlnetsAuthCtx.getUserCode()
															: UserCode.forId("unknown");
			// get the profiles associated to AA14
			profiles = xlnetsAuthCtx.profilesOf(AppCode.forId("AA14A"));
			
			if (CollectionUtils.isNullOrEmpty(profiles)) { 
				// throw new IllegalStateException("NO profiles for AA14A!");
				log.info("NO xlnets profiles for AA14 found");
			} else {
				log.info("Found {} profiles for AA14: {}",
						 profiles.size(),profiles);
				// remove the profile associated to the access function
				profiles = new ArrayList<String>(profiles);
				profiles.remove(XLNETS_FN_ACCESS_PROFILE);			// app access-related profile
							
				// filter the profiles
				if (profiles.size() > 1) {
					profiles = FluentIterable.from(profiles)
												   .filter(_profileIdFilter)
												   .toList();
				}
				if (profiles.size() > 1) {
					log.error("The user with code {} has MORE THAN A SINGLE AA14 PROFILE: {}; should have a single one (except for the access profile AA14-PF-009)",
							  _userCode,profiles);
					profileId = CollectionUtils.<String>pickOneElement(profiles);
				} else {
					log.info("The user {} has a {} PROFILES: {}",
							 _userCode,
							 profiles != null ? profiles.size() : 0,
							 profiles);
					profileId = CollectionUtils.<String>pickOneElementOrNull(profiles);
				}
			}
		} else {
			_userCode = UserCode.forId("unknown");
		}
		
		// profile
		if (Strings.isNOTNullOrEmpty(profileId)) {
			log.info("Using profile with id={} for the user {} (available profiles: {})",
					 profileId,_userCode,profiles);
			_uiUserScheduleData = _profileIdToUserScheduleData.apply(profileId);
			_authenticated = true;
		} else {
			log.info("The user {} has a NO PROFILE: {}",
					 _userCode,profiles);
			_uiUserScheduleData = null;
			_authenticated = false;
		}
		// User info
		if (xlnetsAuthCtx != null) {
			_uiUserPersonalData = new AA14UIUserPersonalData(xlnetsAuthCtx.getUserDni(),
															 xlnetsAuthCtx.getUserName(),
															 xlnetsAuthCtx.getUserSurname(),
															 xlnetsAuthCtx.getUserTelephone(),
															 xlnetsAuthCtx.getUserMail(),
															 xlnetsAuthCtx.getWorkplaceCountyId(),
															 xlnetsAuthCtx.getWorkplaceBuildingCode());
			log.debug("User workplace building: {} / county: {}", 
						_uiUserPersonalData.getBuildingCode(),
						_uiUserPersonalData.getCountyId());
		}
		else {
			_uiUserPersonalData = null;
		}
		
		// log
		if (_uiUserScheduleData != null) {
			log.warn("User: {} > authenticated: {} / allowed schedules: {} / favorite schedule: {}",
					  _userCode,_authenticated,_uiUserScheduleData.getAllowedSchedules(),_uiUserScheduleData.getFavoriteScheduleId());
			
		} else {
			log.warn("NO xlnets auth context found: User: {} > authenticated: {}",
					 _userCode,_authenticated);
		}
		
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasScheduleAccess() {
		return _authenticated 
			&& CollectionUtils.hasData(this.getAllowedScheduleIds());
	}
	public AA14ScheduleID getFavoriteSheduleId() {
		return _uiUserScheduleData != null ? _uiUserScheduleData.getFavoriteScheduleId()
										   : null;
	}
	public boolean isFavoriteSchedule(final AA14ScheduleID schId) {
		return _uiUserScheduleData.getFavoriteScheduleId() != null ? _uiUserScheduleData.getFavoriteScheduleId().is(schId)
																   : false;
	}
	public Collection<AA14ScheduleID> getAllowedScheduleIds() {
		return _uiUserScheduleData != null ? _uiUserScheduleData.getAllowedSchedules()
										   : null;
	}
	public boolean isScheduleAllowed(final AA14ScheduleID schId) {
		return _uiUserScheduleData != null ? _uiUserScheduleData.isScheduleAllowed(schId)
										   : false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
	public static class AA14UIUserScheduleData {
		@Getter private final AA14ScheduleID _favoriteScheduleId;
		@Getter private final Collection<AA14ScheduleID> _allowedSchedules;
		
		public boolean isScheduleAllowed(final AA14ScheduleID schId) {
			return _allowedSchedules != null ? _allowedSchedules.contains(schId) 
											 : false;
		}
	}
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
	public static class AA14UIUserPersonalData {
		@Getter private final String _dni;
		@Getter private final String _name;
		@Getter private final String _surname;
		@Getter private final String _telephone;
		@Getter private final EMail _mail;
		@Getter private final GeoCountyID _countyId; 
		@Getter private final BuildingCode _buildingCode;
		
	}
}
