package aa14f.model.config.business.builder;

import static aa14f.model.config.business.AA14BusinessConfigForZuzenean.LOC_PRESENCIAL_AR_ID;
import static aa14f.model.config.business.AA14BusinessConfigForZuzenean.LOC_PRESENCIAL_BIZ_ID;
import static aa14f.model.config.business.AA14BusinessConfigForZuzenean.LOC_PRESENCIAL_GI_ID;
import static aa14f.model.config.business.AA14BusinessConfigForZuzenean.ORG_ID;
import static aa14f.model.config.business.AA14BusinessConfigForZuzenean.SCH_PRESENCIAL_AR_ID;
import static aa14f.model.config.business.AA14BusinessConfigForZuzenean.SCH_PRESENCIAL_BIZ_ID;
import static aa14f.model.config.business.AA14BusinessConfigForZuzenean.SCH_PRESENCIAL_GI_ID;
import static aa14f.model.config.business.AA14BusinessConfigForZuzenean.SERVICE_ID;
import static aa14f.model.config.business.AA14BusinessConfigForZuzenean.ZUZENEAN_ID;

import java.util.Collection;

import com.google.common.collect.Lists;

import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.AA14ScheduleBookingConfig;
import aa14f.model.config.business.AA14BusinessConfigForZuzenean;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.types.Color;
import r01f.types.contact.ContactInfo;
import r01f.types.datetime.Time;
import r01f.types.geo.GeoOIDs.GeoZipCode;
import r01f.types.geo.GeoPosition;
import r01f.types.geo.GeoStreet;

@Slf4j
@Accessors(prefix="_")
public class AA14ConfigBuilderForZuzenean 
	 extends AA14ConfigBuilderForEJGVBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	// colors
	private static final Color COLOR_PRESENCIAL = Color.from("indigo");
	
	// Texts
	private static final String PRESENCIAL_TXT_ES = "Atención presencial";
	private static final String PRESENCIAL_TXT_EU = "Aurrez aurre arreta";
	
/////////////////////////////////////////////////////////////////////////////////////////
//	ORGS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public AA14Organization createOrganizationConfig(final AA14BusinessID businessId) {
		// --- Organization
		AA14Organization org = _buildOrganization(businessId,
												  AA14OrganizationOID.supply(),ORG_ID,
												  "Eusko Jaurlaritza / Gobierno Vasco","Eusko Jaurlaritza / Gobierno Vasco");
		log.warn("[Org]: Created {}",
				  org.getId());
		return org;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DIVISION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Collection<AA14OrgDivision> createOrgDivisionConfigs(final AA14Organization org) {
		Collection<AA14OrgDivision> outDivs = Lists.newArrayListWithExpectedSize(1);
		// --- Division
		{
			AA14OrgDivision division = _buildDivision(AA14BusinessID.ZUZENEAN,
													  org,
										  			  AA14OrgDivisionOID.supply(),ZUZENEAN_ID,
										  			  "Zuzenean","Zuzenean");
			outDivs.add(division);
			log.warn("\t[Division]: Created {}",
					  division.getId());
		}
		return outDivs;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DIVISION SERVICES
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Collection<AA14OrgDivisionService> createOrgDivisionServicesConfigs(final AA14Organization org,
									  							      		   final Collection<AA14OrgDivision> divisions) {
		Collection<AA14OrgDivisionService> outServices = Lists.newArrayListWithExpectedSize(3);
		// --- Service: Presencial
		{
			AA14OrgDivisionService face2faceService =  _buildService(AA14BusinessID.ZUZENEAN,
																	 org,
														    		 this.within(divisions).find(ZUZENEAN_ID),
														    		 AA14OrgDivisionServiceOID.supply(),SERVICE_ID,
														    		 PRESENCIAL_TXT_ES,PRESENCIAL_TXT_EU);
			outServices.add(face2faceService);
			log.warn("\t\t[Service]: Created {}",
					 face2faceService.getId());
		}
		return outServices;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DIVISION SERVICES LOCATIONS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Collection<AA14OrgDivisionServiceLocation> createOrgDivisionServiceLocationsConfigs(final AA14Organization org,
																  							   final Collection<AA14OrgDivision> divisions,
																  							   final Collection<AA14OrgDivisionService> services) {
		Collection<AA14OrgDivisionServiceLocation> outLocs = Lists.newArrayListWithExpectedSize(3);
		
		// --- Location: Presencial AR
		{
			AA14OrgDivisionServiceLocation presencialAr = _buildLocation(AA14BusinessID.ZUZENEAN,
																		 org,
																		 this.within(divisions).find(ZUZENEAN_ID),
																		 this.within(services).find(SERVICE_ID),
																		 AA14OrgDivisionServiceLocationOID.supply(),LOC_PRESENCIAL_AR_ID,
																		 PRESENCIAL_TXT_ES + " (AR)",PRESENCIAL_TXT_EU + " (AR)",
																		 _buildZuzeneanArabaGeoPosition(),
																		 _buildBiziLagunGasteizContactInfo(),
																		 COLOR_PRESENCIAL);
			outLocs.add(presencialAr);
			log.warn("\t\t\t[Location]: Created {}",
					 presencialAr.getId());
		}
		// --- Location: Presencial BIZ
		{
			AA14OrgDivisionServiceLocation presencialBiz =  _buildLocation(AA14BusinessID.ZUZENEAN,
																		   org,
																		   this.within(divisions).find(ZUZENEAN_ID),
																		   this.within(services).find(SERVICE_ID),
																		   AA14OrgDivisionServiceLocationOID.supply(),LOC_PRESENCIAL_BIZ_ID,
																		   PRESENCIAL_TXT_ES + " (BIZ)",PRESENCIAL_TXT_EU + " (BIZ)",
																		   _buildZuzeneanBizkaiaGeoPosition(),
																		   _buildZuzeneanBizkaiaContactInfo(),
																		   COLOR_PRESENCIAL);
			outLocs.add(presencialBiz);
			log.warn("\t\t\t[Location]: Created {}",
					 presencialBiz.getId());
			
		}
		// --- Location: Presencial GI
		{
			AA14OrgDivisionServiceLocation presencialGi = _buildLocation(AA14BusinessID.ZUZENEAN,
																		 org,
																		 this.within(divisions).find(ZUZENEAN_ID),
																		 this.within(services).find(SERVICE_ID),
																		 AA14OrgDivisionServiceLocationOID.supply(),LOC_PRESENCIAL_GI_ID,
																		 PRESENCIAL_TXT_ES + " (GI)",PRESENCIAL_TXT_EU + " (GI)",
																		 _buildZuzeneanGipuzkoaGeoPosition(),
																		 _buildBiziLagunGipuzkoaContactInfo(),
																		 COLOR_PRESENCIAL);
			outLocs.add(presencialGi);
			log.warn("\t\t\t[Location]: Created {}",
					 presencialGi.getId());
		}
		return outLocs;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SCHEDULES
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Collection<AA14Schedule> createSchedulesConfigs(final AA14Organization org,
												  		   final Collection<AA14OrgDivision> divisions,
												  		   final Collection<AA14OrgDivisionService> services,
												  		   final Collection<AA14OrgDivisionServiceLocation> locs) {
		Collection<AA14Schedule> outSchs = Lists.newArrayListWithExpectedSize(3);
		
		// Schedule: Presencial AR
		{
			AA14Schedule presencialSchAr = _buildSchedule(AA14BusinessID.ZUZENEAN,
														  SCH_PRESENCIAL_AR_ID,
														  PRESENCIAL_TXT_ES + " SCHEDULE (AR)",PRESENCIAL_TXT_EU + " SCHEDULE (AR)",
														  _createZuzeneanScheduleBookingConfig(15,			// slot length
																   							   18),			// max appointments in slot
														  null,		// no qmatic orchestra config
														  this.within(locs).find(AA14BusinessConfigForZuzenean.LOC_PRESENCIAL_AR_ID));
			outSchs.add(presencialSchAr);
			log.warn("\t\t\t[Schedule]: Created {}",
					 presencialSchAr.getId());
		}
		// Schedule: Presencial BIZ
		{
			AA14Schedule presencialSchBiz = _buildSchedule(AA14BusinessID.ZUZENEAN,
														   SCH_PRESENCIAL_BIZ_ID,
														   PRESENCIAL_TXT_ES + " SCHEDULE (BIZ)",PRESENCIAL_TXT_EU + " SCHEDULE (BIZ)",
														   _createZuzeneanScheduleBookingConfig(15,			// slot length
																   								26),		// max appointments in slot
														   null,		// no qmatic orchestra config
														   this.within(locs).find(AA14BusinessConfigForZuzenean.LOC_PRESENCIAL_BIZ_ID));
			outSchs.add(presencialSchBiz);
			log.warn("\t\t\t[Schedule]: Created {}",
					 presencialSchBiz.getId());
		}
		// Schedule: Presencial GI
		{
			AA14Schedule presencialSchGi = _buildSchedule(AA14BusinessID.ZUZENEAN,
														  SCH_PRESENCIAL_GI_ID,
														  PRESENCIAL_TXT_ES + " SCHEDULE (GI)",PRESENCIAL_TXT_EU + " SCHEDULE (GI)",
														  _createZuzeneanScheduleBookingConfig(15,			// slot length
																   							   20),		// max appointments in slot
														  null,		// no qmatic orchestra config
														  this.within(locs).find(LOC_PRESENCIAL_GI_ID));
			outSchs.add(presencialSchGi);
			log.warn("\t\t\t[Schedule]: Created {}",
					 presencialSchGi.getId());
		}
		return outSchs;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	private static AA14ScheduleBookingConfig _createZuzeneanScheduleBookingConfig(final int slotLength,
																				  final int maxAppointmentsInSlot) {
		return new AA14ScheduleBookingConfig(Time.of("8:00"),		// day bookable range start 
				    						 Time.of("17:30"),		// day bookable range end
											 slotLength,			// slot length
											 maxAppointmentsInSlot,	// max appointments in slot
											 null);					// future booking limit (no limit)
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	protected static GeoPosition _buildZuzeneanArabaGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(ARABA_COUNTY)
						  .withMunicipality(GASTEIZ)
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Ramiro de Maeztu, 10 bajo")
							  			   .withNameInLang(Language.BASQUE,"Ramiro de Maeztu, 10 behea"))
						  .withZipCode(GeoZipCode.forId("01008"));
	}
	protected static ContactInfo _buildBiziLagunGasteizContactInfo() {
		return ContactInfo.create()
						 		.addPhones(_buildZuzenenanPhones());
	}
	protected static GeoPosition _buildZuzeneanGipuzkoaGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(GIPUZKOA_COUNTY)
						  .withMunicipality(DONOSTIA)
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Andia 13")
							  			   .withNameInLang(Language.BASQUE,"Andia 13"))
						  .withZipCode(GeoZipCode.forId("20004"));
	}
	protected static ContactInfo _buildBiziLagunGipuzkoaContactInfo() {
		return ContactInfo.create()
						 		.addPhones(_buildZuzenenanPhones());
	}
	protected static GeoPosition _buildZuzeneanBizkaiaGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(BIZKAIA_COUNTY)
						  .withMunicipality(BILBAO)
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Gran Vía, 85")
							  			   .withNameInLang(Language.BASQUE,"Gran Vía, 85"))
						  .withZipCode(GeoZipCode.forId("48011"));
	}
	protected static ContactInfo _buildZuzeneanBizkaiaContactInfo() {
		return ContactInfo.create()
						 		.addPhones(_buildZuzenenanPhones());
	}
}
