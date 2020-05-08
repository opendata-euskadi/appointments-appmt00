package aa14f.model.config.business.builder;

import static aa14f.model.config.business.AA14BusinessConfigForMedicalService.LOC_DOCTOR_VISIT_AR_ID;
import static aa14f.model.config.business.AA14BusinessConfigForMedicalService.LOC_DOCTOR_VISIT_BIZ_ID;
import static aa14f.model.config.business.AA14BusinessConfigForMedicalService.LOC_DOCTOR_VISIT_GI_ID;
import static aa14f.model.config.business.AA14BusinessConfigForMedicalService.LOC_NURSE_VISIT_AR_ID;
import static aa14f.model.config.business.AA14BusinessConfigForMedicalService.LOC_NURSE_VISIT_BIZ_ID;
import static aa14f.model.config.business.AA14BusinessConfigForMedicalService.LOC_NURSE_VISIT_GI_ID;
import static aa14f.model.config.business.AA14BusinessConfigForMedicalService.MEDICAL_SERVICE_ID;
import static aa14f.model.config.business.AA14BusinessConfigForMedicalService.ORG_ID;
import static aa14f.model.config.business.AA14BusinessConfigForMedicalService.SCH_DOCTOR_VISIT_AR_ID;
import static aa14f.model.config.business.AA14BusinessConfigForMedicalService.SCH_DOCTOR_VISIT_BIZ_ID;
import static aa14f.model.config.business.AA14BusinessConfigForMedicalService.SCH_DOCTOR_VISIT_GI_ID;
import static aa14f.model.config.business.AA14BusinessConfigForMedicalService.SCH_NURSE_VISIT_AR_ID;
import static aa14f.model.config.business.AA14BusinessConfigForMedicalService.SCH_NURSE_VISIT_BIZ_ID;
import static aa14f.model.config.business.AA14BusinessConfigForMedicalService.SCH_NURSE_VISIT_GI_ID;
import static aa14f.model.config.business.AA14BusinessConfigForMedicalService.SERVICE_DOCTOR_VISIT_ID;
import static aa14f.model.config.business.AA14BusinessConfigForMedicalService.SERVICE_NURSE_VISIT_ID;

import java.util.Collection;

import com.google.common.collect.Lists;

import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.AA14ScheduleBookingConfig;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.guids.CommonOIDs.BuildingCode;
import r01f.locale.Language;
import r01f.types.Color;
import r01f.types.contact.ContactInfo;
import r01f.types.contact.ContactInfoUsage;
import r01f.types.contact.ContactMail;
import r01f.types.contact.ContactPhone;
import r01f.types.datetime.Time;
import r01f.types.geo.GeoOIDs.GeoZipCode;
import r01f.types.geo.GeoPosition;
import r01f.types.geo.GeoStreet;

@Slf4j
@Accessors(prefix="_")
public class AA14ConfigBuilderForMedicalService 
	 extends AA14ConfigBuilderForEJGVBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	// Users from this building can select any location
	public static final BuildingCode CENTRO_EDUCATIVO_ZUMARRAGA_BUILDING_CODE = BuildingCode.forId("005357");
	
	// colors
	private static final Color DOCTOR_VISIT_AR_COLOR= Color.from("Teal");
	private static final Color DOCTOR_VISIT_GI_COLOR = Color.from("Indigo");
	private static final Color DOCTOR_VISIT_BIZ_COLOR = Color.from("SaddleBrown"); 
	
	private static final Color NURSE_VISIT_AR_COLOR = Color.from("MediumVioletRed");
	private static final Color NURSE_VISIT_GI_COLOR = Color.from("DarkOrange");
	private static final Color NURSE_VISIT_BIZ_COLOR = Color.from("DarkGreen");

	// Texts
	private static final String MEDICAL_SERVICE_TXT_ES = "Servicio médico";
	private static final String MEDICAL_SERVICE_TXT_EU = "Zerbitzu medikoa";
	
	private static final String DOCTOR_VISIT_TXT_ES = "Consulta médica";
	private static final String DOCTOR_VISIT_TXT_EU = "Medikuaren kontsulta";
	
	private static final String NURSE_VISIT_TXT_ES = "Consulta de enfermería";
	private static final String NURSE_VISIT_TXT_EU = "Erizaintzako kontsulta";
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
		Collection<AA14OrgDivision> outDivs = Lists.newArrayListWithExpectedSize(1);
		// --- Division
		{
			AA14OrgDivision division = _buildDivision(AA14BusinessID.MEDICAL_SERVICE,
													  org,
									  				  AA14OrgDivisionOID.supply(),MEDICAL_SERVICE_ID,
									  				  MEDICAL_SERVICE_TXT_ES,MEDICAL_SERVICE_TXT_EU);
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
		// --- Service: Doctor visit
		{
			AA14OrgDivisionService doctorVisitService = _buildService(AA14BusinessID.MEDICAL_SERVICE,
																	  org,
												     				  this.within(divisions).find(MEDICAL_SERVICE_ID),
												     				  AA14OrgDivisionServiceOID.supply(),SERVICE_DOCTOR_VISIT_ID,
												     				  DOCTOR_VISIT_TXT_ES,DOCTOR_VISIT_TXT_EU);
			outServices.add(doctorVisitService);
			log.warn("\t\t[Service]: Created {}",
					 doctorVisitService.getId());
		}
		// --- Service: Nurse visit
		{
			AA14OrgDivisionService nurseVisitService = _buildService(AA14BusinessID.MEDICAL_SERVICE,
																	 org,
												     				 this.within(divisions).find(MEDICAL_SERVICE_ID),
												     				 AA14OrgDivisionServiceOID.supply(),SERVICE_NURSE_VISIT_ID,
												     				 NURSE_VISIT_TXT_ES,NURSE_VISIT_TXT_EU);
			outServices.add(nurseVisitService);
			log.warn("\t\t[Service]: Created {}",
					 nurseVisitService.getId());
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
		Collection<AA14OrgDivisionServiceLocation> outLocs = Lists.newArrayListWithExpectedSize(6);
		
		// --- Location: Consulta médica AR
		{
			AA14OrgDivisionServiceLocation doctorVisitARLocation = _buildLocation(AA14BusinessID.MEDICAL_SERVICE,
																				  org,
																			  	  this.within(divisions).find(MEDICAL_SERVICE_ID),
																			  	  this.within(services).find(SERVICE_DOCTOR_VISIT_ID),
																			  	  AA14OrgDivisionServiceLocationOID.supply(),LOC_DOCTOR_VISIT_AR_ID,
																			  	  DOCTOR_VISIT_TXT_ES + " (AR)",DOCTOR_VISIT_TXT_EU + " (AR)",
																			  	  _buildMedicalServiceArabaGeoPosition(),
																			  	  _buildArabaDoctorVisitContactInfo(),
																			  	  DOCTOR_VISIT_AR_COLOR);
			outLocs.add(doctorVisitARLocation);
			log.warn("\t\t\t[Location]: Created {}",
					 doctorVisitARLocation.getId());
		}
		// --- Location: Consulta médica BIZ
		{
			AA14OrgDivisionServiceLocation doctorVisitBIZLocation = _buildLocation(AA14BusinessID.MEDICAL_SERVICE,
																				   org,
												  								   this.within(divisions).find(MEDICAL_SERVICE_ID),
												  								   this.within(services).find(SERVICE_DOCTOR_VISIT_ID),
												  								   AA14OrgDivisionServiceLocationOID.supply(),LOC_DOCTOR_VISIT_BIZ_ID,
												  								   DOCTOR_VISIT_TXT_ES + " (BIZ)",DOCTOR_VISIT_TXT_EU + " (BIZ)",
												  								   _buildMedicalServiceBizkaiaGeoPosition(),
												  								   _buildBizkaiaDoctorVisitContactInfo(),
												  								   DOCTOR_VISIT_BIZ_COLOR);
			outLocs.add(doctorVisitBIZLocation);
			log.warn("\t\t\t[Location]: Created {}",
					 doctorVisitBIZLocation.getId());
		}
		// --- Location: Consulta médica GI
		{
			AA14OrgDivisionServiceLocation doctorVisitGILocation = _buildLocation(AA14BusinessID.MEDICAL_SERVICE,
																				  org,
																			  	  this.within(divisions).find(MEDICAL_SERVICE_ID),
																			  	  this.within(services).find(SERVICE_DOCTOR_VISIT_ID),
																			  	  AA14OrgDivisionServiceLocationOID.supply(),LOC_DOCTOR_VISIT_GI_ID,
																			  	  DOCTOR_VISIT_TXT_ES + " (GI)",DOCTOR_VISIT_TXT_EU + " (GI)",
																			  	  _buildMedicalServiceGipuzkoaGeoPosition(),
																			  	  _buildGuipuzkoaDoctorVisitContactInfo(),
																			  	  DOCTOR_VISIT_GI_COLOR);
			outLocs.add(doctorVisitGILocation);
			log.warn("\t\t\t[Location]: Created {}",
					 doctorVisitGILocation.getId());
		}
		
		// --- Location: Consulta de enfermería AR
		{
			AA14OrgDivisionServiceLocation nurseVisitARLocation = _buildLocation(AA14BusinessID.MEDICAL_SERVICE,
																				 org,
												  	  							 this.within(divisions).find(MEDICAL_SERVICE_ID),
												  	  							 this.within(services).find(SERVICE_NURSE_VISIT_ID),
												  	  							 AA14OrgDivisionServiceLocationOID.supply(),LOC_NURSE_VISIT_AR_ID,
												  	  							 NURSE_VISIT_TXT_ES + " (AR)",NURSE_VISIT_TXT_EU + " (AR)",
												  	  							 _buildMedicalServiceArabaGeoPosition(),
												  	  							 _buildArabaNurseVisitContactInfo(),
												  	  							 NURSE_VISIT_AR_COLOR);
			outLocs.add(nurseVisitARLocation);
			log.warn("\t\t\t[Location]: Created {}",
					 nurseVisitARLocation.getId());
		}
		// --- Location: Consulta de enfermería BIZ
		{
			AA14OrgDivisionServiceLocation nurseVisitBIZLocation = _buildLocation(AA14BusinessID.MEDICAL_SERVICE,
																				  org,
												  	   							  this.within(divisions).find(MEDICAL_SERVICE_ID),
												  	   							  this.within(services).find(SERVICE_NURSE_VISIT_ID),
												  	   							  AA14OrgDivisionServiceLocationOID.supply(),LOC_NURSE_VISIT_BIZ_ID,
												  	   							  NURSE_VISIT_TXT_ES + " (BIZ)",NURSE_VISIT_TXT_EU + " (BIZ)",
												  	   							  _buildMedicalServiceBizkaiaGeoPosition(),
												  	   							  _buildBizkaiaNurseVisitContactInfo(),
												  	   							  NURSE_VISIT_BIZ_COLOR);
			outLocs.add(nurseVisitBIZLocation);
			log.warn("\t\t\t[Location]: Created {}",
					 nurseVisitBIZLocation.getId());
		}
		// --- Location: Consulta de enfermería GI
		{
			AA14OrgDivisionServiceLocation nurseVisitGILocation = _buildLocation(AA14BusinessID.MEDICAL_SERVICE,
																				 org,
													  							 this.within(divisions).find(MEDICAL_SERVICE_ID),
													  							 this.within(services).find(SERVICE_NURSE_VISIT_ID),
													  							 AA14OrgDivisionServiceLocationOID.supply(),LOC_NURSE_VISIT_GI_ID,
													  							 NURSE_VISIT_TXT_ES + " (GI)",NURSE_VISIT_TXT_EU + " (GI)",
													  							 _buildMedicalServiceGipuzkoaGeoPosition(),
													  							 _buildGuipuzkoaNurseVisitContactInfo(),
													  							 NURSE_VISIT_GI_COLOR);
			outLocs.add(nurseVisitGILocation);
			log.warn("\t\t\t[Location]: Created {}",
					 nurseVisitGILocation.getId());
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
		Collection<AA14Schedule> outSchs = Lists.newArrayListWithExpectedSize(6);
		
		// Schedule: Consulta médica AR
		{
			AA14Schedule doctorVisitServiceSchAr = _buildSchedule(AA14BusinessID.MEDICAL_SERVICE,
																  SCH_DOCTOR_VISIT_AR_ID,
													 			  DOCTOR_VISIT_TXT_ES + " SCHEDULE (AR)",DOCTOR_VISIT_TXT_ES + " SCHEDULE (AR)",
													 			  _createMedicalServiceScheduleBookingConfig(Time.of(12, 0, 0),	 // day bookable range start 
															   							   					 Time.of(16, 30, 0),// day bookable range end 
															   							   					 20,			 // slot length
															   							   					 2),			 // max appointments in slot
													 			  null,		// no qmatic orchestra config
													 			  this.within(locs).find(LOC_DOCTOR_VISIT_AR_ID));
			outSchs.add(doctorVisitServiceSchAr);
			log.warn("\t\t\t[Schedule]: Created {}",
					 doctorVisitServiceSchAr.getId());
		}
		// Schedule: Consulta médica GI
		{
			AA14Schedule doctorVisitServiceSchGi = _buildSchedule(AA14BusinessID.MEDICAL_SERVICE,
																  SCH_DOCTOR_VISIT_GI_ID,
												     			  DOCTOR_VISIT_TXT_ES + " SCHEDULE (GI)",DOCTOR_VISIT_TXT_ES + " SCHEDULE (GI)",
												     			  _createMedicalServiceScheduleBookingConfig(Time.of(8, 0, 0),	 // day bookable range start 
															   							   					 Time.of(10, 0, 0),// day bookable range end  
															   							   					 20,			 // slot length
															   							   					 1),			 // max appointments in slot
												     			  null,		// no qmatic orchestra config
												     			  this.within(locs).find(LOC_DOCTOR_VISIT_GI_ID));
			outSchs.add(doctorVisitServiceSchGi);
			log.warn("\t\t\t[Schedule]: Created {}",
					 doctorVisitServiceSchGi.getId());
		}
		// Schedule: Consulta médica BIZ
		{
			AA14Schedule doctorVisitServiceSchBiz = _buildSchedule(AA14BusinessID.MEDICAL_SERVICE,
																   SCH_DOCTOR_VISIT_BIZ_ID,
													  			   DOCTOR_VISIT_TXT_ES + " SCHEDULE (BIZ)",DOCTOR_VISIT_TXT_ES + " SCHEDULE (BIZ)",
													  			   _createMedicalServiceScheduleBookingConfig(Time.of(12, 0, 0),	 // day bookable range start 
															   							   					  Time.of(16, 30, 0),// day bookable range end 
															   							   					  20,			 // slot length
															   							   					  1),			 // max appointments in slot
													  			   null,		// no qmatic orchestra config
													  			   this.within(locs).find(LOC_DOCTOR_VISIT_BIZ_ID));
			outSchs.add(doctorVisitServiceSchBiz);
			log.warn("\t\t\t[Schedule]: Created {}",
					 doctorVisitServiceSchBiz.getId());
		}
		
		// Schedule: Consulta de enfermería AR
		{
			AA14Schedule nurseVisitServiceSchAr = _buildSchedule(AA14BusinessID.MEDICAL_SERVICE,
																 SCH_NURSE_VISIT_AR_ID,
													 			 NURSE_VISIT_TXT_ES + " SCHEDULE (AR)",NURSE_VISIT_TXT_ES + " SCHEDULE (AR)",
													 			 _createMedicalServiceScheduleBookingConfig(Time.of(12, 00, 0),	 // day bookable range start 
															   							   					Time.of(16, 30, 0),// day bookable range end 
															   							   					15,			 // slot length
															   							   					1),			 // max appointments in slot
													 			 null,		// no qmatic orchestra config
													 			 this.within(locs).find(LOC_NURSE_VISIT_AR_ID));
			outSchs.add(nurseVisitServiceSchAr);
			log.warn("\t\t\t[Schedule]: Created {}",
					 nurseVisitServiceSchAr.getId());
		}
		// Schedule: Servicio de enfermería BIZ
		{
			AA14Schedule nurseVisitServiceSchBiz = _buildSchedule(AA14BusinessID.MEDICAL_SERVICE,
																  SCH_NURSE_VISIT_BIZ_ID,
													  			  NURSE_VISIT_TXT_ES + " SCHEDULE (BIZ)",NURSE_VISIT_TXT_ES + " SCHEDULE (BIZ)",
													  			  _createMedicalServiceScheduleBookingConfig(Time.of(12, 0, 0),	// day bookable range start 
															   							   					 Time.of(16, 30, 0),// day bookable range end 
															   							   					 15,			 	// slot length
															   							   					 1),			 	// max appointments in slot
													  			  null,		// no qmatic orchestra config
													  			  this.within(locs).find(LOC_NURSE_VISIT_BIZ_ID));
			outSchs.add(nurseVisitServiceSchBiz);
			log.warn("\t\t\t[Schedule]: Created {}",
					 nurseVisitServiceSchBiz.getId());
		}
		// Schedule: Consulta de enfermería GI
		{
			AA14Schedule nurseVisitServiceSchGi = _buildSchedule(AA14BusinessID.MEDICAL_SERVICE,
																 SCH_NURSE_VISIT_GI_ID,
												     			 NURSE_VISIT_TXT_ES + " SCHEDULE (GI)",NURSE_VISIT_TXT_ES + " SCHEDULE (GI)",
												     			 _createMedicalServiceScheduleBookingConfig(Time.of(10, 0, 0),	 // day bookable range start 
															   							   					Time.of(12, 00, 0),// day bookable range end 
															   							   					15,			 // slot length
															   							   					1),			 // max appointments in slot
												     			 null,		// no qmatic orchestra config
												     			 this.within(locs).find(LOC_NURSE_VISIT_GI_ID));
			outSchs.add(nurseVisitServiceSchGi);
			log.warn("\t\t\t[Schedule]: Created {}",
					 nurseVisitServiceSchGi.getId());
		}
		return outSchs;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	private static AA14ScheduleBookingConfig _createMedicalServiceScheduleBookingConfig(final Time dayBookableRangeStart,
																				   	    final Time dayBookableRangeEnd,
																				   	    final int slotLength,
																				   	    final int maxAppointmentsInSlot) {
		return new AA14ScheduleBookingConfig(dayBookableRangeStart,		// day bookable range start 
				    						 dayBookableRangeEnd,		// day bookable range end
											 slotLength,			// slot length
											 maxAppointmentsInSlot, // max appointments in slot
											 null); // future booking limit (date): 
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  Location addresses
/////////////////////////////////////////////////////////////////////////////////////////
	protected static GeoPosition _buildMedicalServiceArabaGeoPosition() {
		
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(ARABA_COUNTY)
						  .withMunicipality(GASTEIZ)
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Lakua - Calle Donostia San Sebastián, 1") //
							  			   .withNameInLang(Language.BASQUE,"Lakua - Donostia San Sebastian Kalea, 1"))
						  .withZipCode(GeoZipCode.forId("01010"));
	}
	
	protected static ContactInfo _buildArabaDoctorVisitContactInfo() {
		return ContactInfo.create()
						 		.addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK).withNumber("945018689"))
						 		.addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK).withNumber("945018663"))
						 		.addMailAddress(ContactMail.createToBeUsedFor(ContactInfoUsage.WORK).mailTo("ms-lopezmartin@euskadi.eus"));
	}
	protected static ContactInfo _buildArabaNurseVisitContactInfo() {
		return ContactInfo.create()
								.addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK).withNumber("945018609"))
						 		.addMailAddress(ContactMail.createToBeUsedFor(ContactInfoUsage.WORK).mailTo("elisa-diaz@euskadi.eus"));
	}
	protected static GeoPosition _buildMedicalServiceGipuzkoaGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(GIPUZKOA_COUNTY)
						  .withMunicipality(DONOSTIA)
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Sede Gobierno Vasco - Calle Andia, 13 3º planta")
							  			   .withNameInLang(Language.BASQUE,"Eusko jaularitzaren egoitza - Andia Kalea, 13 3. solairua"))
						  .withZipCode(GeoZipCode.forId("20004"));
	}
	protected static ContactInfo _buildGuipuzkoaDoctorVisitContactInfo() {
		return ContactInfo.create()
						 		.addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK).withNumber("943022807"))
						 		.addMailAddress(ContactMail.createToBeUsedFor(ContactInfoUsage.WORK).mailTo("i-urangairibar@euskadi.eus"));
	}
	protected static ContactInfo _buildGuipuzkoaNurseVisitContactInfo() {
		return ContactInfo.create()
						 		.addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK).withNumber("943023307"))
						 		.addMailAddress(ContactMail.createToBeUsedFor(ContactInfoUsage.WORK).mailTo("belen-gonzalez@euskadi.eus"));
	}
	protected static GeoPosition _buildMedicalServiceBizkaiaGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(BIZKAIA_COUNTY)
						  .withMunicipality(BILBAO)
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Sede Gobierno Vasco - Calle Gran Ví­a, 85 8º planta")
							  			   .withNameInLang(Language.BASQUE,"Eusko jaularitzaren egoitza - Gran Vi­a kalea, 85 8. solairua"))
						  .withZipCode(GeoZipCode.forId("48011"));
	}
	protected static ContactInfo _buildBizkaiaDoctorVisitContactInfo() {
		return ContactInfo.create()
						 		.addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK).withNumber("944031848"))
						 		.addMailAddress(ContactMail.createToBeUsedFor(ContactInfoUsage.WORK).mailTo("ml-uriarte@euskadi.eus"));
	}
	protected static ContactInfo _buildBizkaiaNurseVisitContactInfo() {
		return ContactInfo.create()
						 		.addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK).withNumber("944031008"))
						 		.addMailAddress(ContactMail.createToBeUsedFor(ContactInfoUsage.WORK).mailTo("m-sanchezarizaleta@euskadi.eus"));
	}
	
}
