package aa14f.model.config.business.builder;

import static aa14f.model.config.business.AA14BusinessConfigForBloodDonation.DONATIONS_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBloodDonation.LOC_DONACIONES_SANGRE_AR_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBloodDonation.LOC_DONACIONES_SANGRE_BIZ_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBloodDonation.LOC_DONACIONES_SANGRE_GI_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBloodDonation.ORG_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBloodDonation.SCH_DONACIONES_SANGRE_AR_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBloodDonation.SCH_DONACIONES_SANGRE_BIZ_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBloodDonation.SCH_DONACIONES_SANGRE_GI_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBloodDonation.SERVICE_ID;

import java.util.Collection;

import org.joda.time.LocalDate;

import com.google.common.collect.Lists;

import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.AA14ScheduleBookingConfig;
import aa14f.model.config.AA14ScheduleBookingLimit;
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
public class AA14ConfigBuilderForBloodDonation 
	 extends AA14ConfigBuilderForEJGVBase { 
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	// colors
	private static final Color COLOR_DONACIONES_SANGRE = Color.from("DarkOrange");
	
	// Texts
	private static final String DONACION_SANGRE_TXT_ES = "Donación de sangre";
	private static final String DONACION_SANGRE_TXT_EU = "Odol-ematea";
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
//  DIVISIONS
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Collection<AA14OrgDivision> createOrgDivisionConfigs(final AA14Organization org) {
		Collection<AA14OrgDivision> outDivs = Lists.newArrayListWithExpectedSize(3);
		// --- Division
		{
			AA14OrgDivision division = _buildDivision(AA14BusinessID.BLOOD_DONATION,
													  org,
													  AA14OrgDivisionOID.supply(),DONATIONS_ID,
													  "Donaciones","Donazioak");
			outDivs.add(division);
			log.warn("\t[Division]: Created {}",
					  division.getId());
		}
		return outDivs;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Collection<AA14OrgDivisionService> createOrgDivisionServicesConfigs(final AA14Organization org,
									  							      		   final Collection<AA14OrgDivision> divisions) {
		Collection<AA14OrgDivisionService> outServices = Lists.newArrayListWithExpectedSize(3);
		// --- Service: Donacion de sangre
		{
			AA14OrgDivisionService bloodDonationService = _buildService(AA14BusinessID.BLOOD_DONATION,
																		org,
												     				    this.within(divisions).find(DONATIONS_ID),
												     				    AA14OrgDivisionServiceOID.supply(),SERVICE_ID,
												     				    DONACION_SANGRE_TXT_ES,DONACION_SANGRE_TXT_EU);
			outServices.add(bloodDonationService);
			log.warn("\t\t[Service]: Created {}",
					 bloodDonationService.getId());
		}
		return outServices;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LOCATIONS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Collection<AA14OrgDivisionServiceLocation> createOrgDivisionServiceLocationsConfigs(final AA14Organization org,
																  							   final Collection<AA14OrgDivision> divisions,
																  							   final Collection<AA14OrgDivisionService> services) {
		Collection<AA14OrgDivisionServiceLocation> outLocs = Lists.newArrayListWithExpectedSize(3);
		
		// --- Location: Donación de sangre AR
		{
			AA14OrgDivisionServiceLocation donacionSangreAr = _buildLocation(AA14BusinessID.BLOOD_DONATION,
																			 org,
																			 this.within(divisions).find(DONATIONS_ID),
																			 this.within(services).find(SERVICE_ID),
																			 AA14OrgDivisionServiceLocationOID.supply(),LOC_DONACIONES_SANGRE_AR_ID,
																			 DONACION_SANGRE_TXT_ES + " (AR)",DONACION_SANGRE_TXT_EU + " (AR)",
																			 _buildDonationsArabaGeoPosition(),
																			 _buildDonationsGasteizContactInfo(),
																			 COLOR_DONACIONES_SANGRE);
			outLocs.add(donacionSangreAr);
			log.warn("\t\t\t[Location]: Created {}",
					 donacionSangreAr.getId());
		}
		
		// --- Location: Donación de sangre GI
		{
			AA14OrgDivisionServiceLocation donacionSangreGi = _buildLocation(AA14BusinessID.BLOOD_DONATION,
																			 org,
												  							 this.within(divisions).find(DONATIONS_ID),
												  							 this.within(services).find(SERVICE_ID),
												  							 AA14OrgDivisionServiceLocationOID.supply(),LOC_DONACIONES_SANGRE_GI_ID,
												  							 DONACION_SANGRE_TXT_ES + " (GI)",DONACION_SANGRE_TXT_EU + " (GI)",
												  							 _buildDonationsGipuzkoaGeoPosition(),
												  							 _buildDonationsGipuzkoaContactInfo(),
												  							 COLOR_DONACIONES_SANGRE);
			outLocs.add(donacionSangreGi);
			log.warn("\t\t\t[Location]: Created {}",
					 donacionSangreGi.getId());
		}
		
		// --- Location: Donación de sangre BIZ
		{
			AA14OrgDivisionServiceLocation donacionSangreBiz = _buildLocation(AA14BusinessID.BLOOD_DONATION,
																			  org,
												   							  this.within(divisions).find(DONATIONS_ID),
												   							  this.within(services).find(SERVICE_ID),
												   							  AA14OrgDivisionServiceLocationOID.supply(),LOC_DONACIONES_SANGRE_BIZ_ID,
												   							  DONACION_SANGRE_TXT_ES + " (BIZ)",DONACION_SANGRE_TXT_EU + " (BIZ)",
												   							  _buildDonationsBizkaiaGeoPosition(),
												   							  _buildDonationsBizkaiaContactInfo(),
												   							  COLOR_DONACIONES_SANGRE);
			outLocs.add(donacionSangreBiz);
			log.warn("\t\t\t[Location]: Created {}",
					 donacionSangreBiz.getId());
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
		
		// Schedule: Donacion de sangre AR
		{
			AA14Schedule donacionSangreSchAr = _buildSchedule(AA14BusinessID.BLOOD_DONATION,
															  SCH_DONACIONES_SANGRE_AR_ID,
													 		  DONACION_SANGRE_TXT_ES + " SCHEDULE (AR)",DONACION_SANGRE_TXT_EU + " SCHEDULE (AR)",
													 		  _createDonationsScheduleBookingConfig(Time.of(9, 0, 0),	 // day bookable range start 
															   							   			Time.of(13, 30, 0),// day bookable range end 
															   							   			15,			 // slot length
															   							   			3),			 // max appointments in slot
													 		  null,		// no qmatic orchestra config
													 		  this.within(locs).find(LOC_DONACIONES_SANGRE_AR_ID));
			outSchs.add(donacionSangreSchAr);
			log.warn("\t\t\t[Location]: Created {}",
					 donacionSangreSchAr.getId());
		}
		// Schedule: Donacion de sangre GI
		{
			AA14Schedule donacionSangreSchGi = _buildSchedule(AA14BusinessID.BLOOD_DONATION,
															  SCH_DONACIONES_SANGRE_GI_ID,
												     		  DONACION_SANGRE_TXT_ES + " SCHEDULE (GI)",DONACION_SANGRE_TXT_EU + " SCHEDULE (GI)",
												     		  _createDonationsScheduleBookingConfig(20,			// slot length
														   								   			3),			// max appointments in slot
												     		  null,		// no qmatic orchestra config
												     		  this.within(locs).find(LOC_DONACIONES_SANGRE_GI_ID));
			outSchs.add(donacionSangreSchGi);
			log.warn("\t\t\t[Location]: Created {}",
					 donacionSangreSchGi.getId());
		}
		// Schedule: Donacion de sangre BIZ
		{
			AA14Schedule donacionSangreSchBiz = _buildSchedule(AA14BusinessID.BLOOD_DONATION,
															   SCH_DONACIONES_SANGRE_BIZ_ID,
													  		   DONACION_SANGRE_TXT_ES + " SCHEDULE (BIZ)",DONACION_SANGRE_TXT_EU + " SCHEDULE (BIZ)",
													  		   _createDonationsScheduleBookingConfig(20,			// slot length
															   										 3),			// max appointments in slot
													  		   null,		// no qmatic orchestra config
													  		   this.within(locs).find(LOC_DONACIONES_SANGRE_BIZ_ID));
			outSchs.add(donacionSangreSchBiz);
			log.warn("\t\t\t[Location]: Created {}",
					 donacionSangreSchBiz.getId());
		}
		return outSchs;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	private static AA14ScheduleBookingConfig _createDonationsScheduleBookingConfig(final int slotLength,
																				   final int maxAppointmentsInSlot) {
		return new AA14ScheduleBookingConfig(Time.of("8:30"),		// day bookable range start 
				    						 Time.of("14:00"),		// day bookable range end
											 slotLength,			// slot length
											 maxAppointmentsInSlot, // max appointments in slot
											 null); // future booking limit (days): 
	}
	
	private static AA14ScheduleBookingConfig _createDonationsScheduleBookingConfig(final Time dayBookableRangeStart,
																				   final Time dayBookableRangeEnd,
																				   final int slotLength,
																				   final int maxAppointmentsInSlot) {
		return new AA14ScheduleBookingConfig(dayBookableRangeStart,		// day bookable range start 
				    						 dayBookableRangeEnd,		// day bookable range end
											 slotLength,			// slot length
											 maxAppointmentsInSlot, // max appointments in slot
											 new AA14ScheduleBookingLimit(new LocalDate(2018,11,2).toDate())); // future booking limit (date): 
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Location addresses
/////////////////////////////////////////////////////////////////////////////////////////
	protected static GeoPosition _buildDonationsArabaGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(ARABA_COUNTY)
						  .withMunicipality(GASTEIZ)
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Calle Donostia San Sebastián, 2 (Lakua 2) ") //
							  			   .withNameInLang(Language.BASQUE,"Donostia San Sebastian Kalea, 2 (Lakua 2) "))
						  .withZipCode(GeoZipCode.forId("01010"));
	}
	//FIXME put here the correct phoneNumbers
	protected static ContactInfo _buildDonationsGasteizContactInfo() {
		return ContactInfo.create()
						 		.addPhones(_buildZuzenenanPhones());
	}
	//FIXME put here the correct address
	protected static GeoPosition _buildDonationsGipuzkoaGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(GIPUZKOA_COUNTY)
						  .withMunicipality(DONOSTIA)
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Andia, 13")
							  			   .withNameInLang(Language.BASQUE,"Andia, 13"))
						  .withZipCode(GeoZipCode.forId("20004"));
	}
	protected static ContactInfo _buildDonationsGipuzkoaContactInfo() {
		return ContactInfo.create()
						 		.addPhones(_buildZuzenenanPhones());
	}
	protected static GeoPosition _buildDonationsBizkaiaGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(BIZKAIA_COUNTY)
						  .withMunicipality(BILBAO)
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Gran Vía, 85")
							  			   .withNameInLang(Language.BASQUE,"Gran Via kalea, 2"))
						  .withZipCode(GeoZipCode.forId("48011"));
	}
	protected static ContactInfo _buildDonationsBizkaiaContactInfo() {
		return ContactInfo.create()
						 		.addPhones(_buildZuzenenanPhones());
	}
}
