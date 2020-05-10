package aa14f.model.config.business.builder;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14OrgDivisionServiceLocationPresentationConfig;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14OrganizationalModelObject;
import aa14f.model.config.AA14OrganizationalModelObjectRef;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.AA14ScheduleBookingConfig;
import aa14f.model.config.AA14ScheduleOrchestraConfig;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14ModelObjectOrgID;
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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.LanguageTextsMapBacked;
import r01f.types.Color;
import r01f.types.contact.ContactInfo;
import r01f.types.geo.GeoCountry;
import r01f.types.geo.GeoCounty;
import r01f.types.geo.GeoMunicipality;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoPosition;
import r01f.types.geo.GeoState;
import r01f.util.types.collections.CollectionUtils;

@Accessors(prefix="_")
abstract class AA14ConfigBuilderBase 
	implements AA14ConfigBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	protected static final GeoCountyID ARABA_COUNTY_ID = GeoCountyID.forId(1);
	protected static final GeoCountyID BIZKAIA_COUNTY_ID = GeoCountyID.forId(48);
	protected static final GeoCountyID GIPUZKOA_COUNTY_ID = GeoCountyID.forId(20);
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final GeoCountry SPAIN = GeoCountry.create()
											  		 .withNameInLang(Language.SPANISH,"Espa�a")
											  		 .withNameInLang(Language.BASQUE,"Espania");
	public static final GeoState EUSKADI_STATE = GeoState.create()
												  		 .withNameInLang(Language.SPANISH,"Euskadi")
												  		 .withNameInLang(Language.BASQUE,"Euskadi");
	public static final GeoCounty BIZKAIA_COUNTY = GeoCounty.create(BIZKAIA_COUNTY_ID)
												  		  .withNameInLang(Language.SPANISH,"Bizkaia")
												  		  .withNameInLang(Language.BASQUE,"Bizkaia");
	public static final GeoMunicipality BILBAO = GeoMunicipality.create()
												  			 	.withNameInLang(Language.SPANISH,"Bilbao")
												  			 	.withNameInLang(Language.BASQUE,"Bilbao");
	public static final GeoCounty ARABA_COUNTY = GeoCounty.create(ARABA_COUNTY_ID)
												  		  .withNameInLang(Language.SPANISH,"Araba/Alava")
												  		  .withNameInLang(Language.BASQUE,"Araba/Alava");
	public static final GeoMunicipality GASTEIZ = GeoMunicipality.create()
													  			 	.withNameInLang(Language.SPANISH,"Vitoria-Gasteiz")
													  			 	.withNameInLang(Language.BASQUE,"Vitoria-Gasteiz");
	public static final GeoCounty GIPUZKOA_COUNTY = GeoCounty.create(GIPUZKOA_COUNTY_ID)
													  		  .withNameInLang(Language.SPANISH,"Gipuzkoa")
													  		  .withNameInLang(Language.BASQUE,"Gipuzkoa");
	public static final GeoMunicipality DONOSTIA = GeoMunicipality.create()
													  			 	.withNameInLang(Language.SPANISH,"Donostia-San Sebastián")
													  			 	.withNameInLang(Language.BASQUE,"Donostia-San Sebastián");
/////////////////////////////////////////////////////////////////////////////////////////
//	OBJECT FIND
/////////////////////////////////////////////////////////////////////////////////////////
	protected <C extends AA14OrganizationalModelObject<?,?>> AA14ConfigCollectionFindStep<C> within(final Collection<C> col) {
		return new AA14ConfigCollectionFindStep<C>(col);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	protected class AA14ConfigCollectionFindStep<C extends AA14OrganizationalModelObject<?,?>> {
		private final Collection<C> _col;
		public <ID extends AA14ModelObjectOrgID<?>> C find(final ID id) {
			if (CollectionUtils.isNullOrEmpty(_col)) return null;
			C outItem = null;
			for (C c : _col) {
				if (c.getId().is(id)) {
					outItem = c;
					break;
				}
			}
			return outItem;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	protected static AA14Organization _buildOrganization(final AA14BusinessID businessId,
														 final AA14OrganizationOID oid,final AA14OrganizationID id,
													     final String nameES,final String nameEU) {
		AA14Organization outOrg = new AA14Organization();
		outOrg.setOid(oid);
		outOrg.setId(id);
		outOrg.setBusinessId(businessId);
		outOrg.setNameByLanguage(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
										   .add(Language.SPANISH,nameES)
										   .add(Language.BASQUE,nameEU));
		return outOrg;
	}
	protected static AA14OrgDivision _buildDivision(final AA14BusinessID businessId,
													final AA14Organization org,
												  	final AA14OrgDivisionOID oid,final AA14OrgDivisionID id,
												  	final String nameES,final String nameEU) {
		AA14OrgDivision outDiv = new AA14OrgDivision();
		outDiv.setOid(oid);
		outDiv.setId(id);
		outDiv.setBusinessId(businessId);
		outDiv.setOrgRef(new AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID>(org.getOid(),org.getId()));
		outDiv.setNameByLanguage(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
										   .add(Language.SPANISH,nameES)
										   .add(Language.BASQUE,nameEU));
		return outDiv;
	}
	protected static AA14OrgDivisionService _buildService(final AA14BusinessID businessId,
														  final AA14Organization org,
												 		  final AA14OrgDivision division,
												 		  final AA14OrgDivisionServiceOID oid,final AA14OrgDivisionServiceID id,
												 		  final String nameES,final String nameEU) {
		AA14OrgDivisionService outService = new AA14OrgDivisionService();
		outService.setOid(oid);
		outService.setId(id);
		outService.setBusinessId(businessId);
		outService.setOrgRef(new AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID>(org.getOid(),org.getId()));
		outService.setOrgDivisionRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionOID,AA14OrgDivisionID>(division.getOid(),division.getId()));
		outService.setNameByLanguage(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
											     .add(Language.SPANISH,nameES)
											     .add(Language.BASQUE,nameEU));
		return outService;
	}
	protected static AA14OrgDivisionServiceLocation _buildLocation(final AA14BusinessID businessId,
																   final AA14Organization org,
												 		  		   final AA14OrgDivision division,
												 		  		   final AA14OrgDivisionService service,
												 		  		   final AA14OrgDivisionServiceLocationOID oid,final AA14OrgDivisionServiceLocationID locId,
												 		  		   final String locNameES,final String locNameEU,
												 		  		   final GeoPosition geoPosition,
												 		  		   final ContactInfo contactInfo,
												 		  		   final Color presentationColor) {
		AA14OrgDivisionServiceLocation outLoc = new AA14OrgDivisionServiceLocation();
		outLoc.setOid(oid);
		outLoc.setId(locId);
		outLoc.setBusinessId(businessId);
		outLoc.setOrgRef(new AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID>(org.getOid(),org.getId()));
		outLoc.setOrgDivisionRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionOID,AA14OrgDivisionID>(division.getOid(),division.getId()));
		outLoc.setOrgDivisionServiceRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID>(service.getOid(),service.getId()));
		outLoc.setNameByLanguage(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
										     .add(Language.SPANISH,locNameES)
										     .add(Language.BASQUE,locNameEU));
		outLoc.setPosition(geoPosition);
		outLoc.setContactInfo(contactInfo);
		outLoc.setPresentationConfig(new AA14OrgDivisionServiceLocationPresentationConfig(presentationColor));
		return outLoc;
	}
	protected static AA14Schedule _buildSchedule(final AA14BusinessID businessId,
												 final AA14ScheduleID scheduleId,
												 final String schES,final String schEU,
												 final AA14ScheduleBookingConfig schConfig,final AA14ScheduleOrchestraConfig orchestraConfig,
												 final AA14OrgDivisionServiceLocation... serviceLocs) {
		return _buildSchedule(businessId,
							  scheduleId, 
							  schES,schEU,
							  schConfig,orchestraConfig,
							  Arrays.asList(serviceLocs));
	}
	protected static AA14Schedule _buildSchedule(final AA14BusinessID businessId,
												 final AA14ScheduleID scheduleId,
												 final String schES,final String schEU,
												 final AA14ScheduleBookingConfig schConfig,final AA14ScheduleOrchestraConfig orchestraConfig,
												 final Collection<AA14OrgDivisionServiceLocation> serviceLocs) {
		AA14ScheduleOID oid = AA14ScheduleOID.supply();
		AA14Schedule outSch = new AA14Schedule();
		outSch.setOid(oid);
		outSch.setId(scheduleId);
		outSch.setBusinessId(businessId);
		outSch.setNameByLanguage(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
										     .add(Language.SPANISH,schES)
										     .add(Language.BASQUE,schEU));
		outSch.setServiceLocationsRefs(FluentIterable.from(serviceLocs)
													 .transform(new Function<AA14OrgDivisionServiceLocation,
															 				 AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID>>() {
																		@Override
																		public AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID, AA14OrgDivisionServiceLocationID> apply(final AA14OrgDivisionServiceLocation loc) {
																			return loc.getReference();
																		}
													 			})
													 .toList());
		outSch.setBookingConfig(schConfig);
		outSch.setOrchestraConfig(orchestraConfig);
		
		return outSch;
	}
}
