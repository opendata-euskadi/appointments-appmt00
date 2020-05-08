package aa14f.model.config.business.builder;

import static aa14f.model.config.business.AA14BusinessConfigForBizilagun.BIZILAGUN_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBizilagun.LOC_COMUNIDADES_ALQUILERES_AR_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBizilagun.LOC_COMUNIDADES_ALQUILERES_BIZ_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBizilagun.LOC_COMUNIDADES_ALQUILERES_GI_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBizilagun.LOC_FIANZAS_DEPOSITO_CONTRATOS_AR_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBizilagun.ORG_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBizilagun.SCH_COMUNIDADES_ALQUILERES_AR_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBizilagun.SCH_COMUNIDADES_ALQUILERES_BIZ_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBizilagun.SCH_COMUNIDADES_ALQUILERES_GI_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBizilagun.SCH_FIANZAS_DEPOSITO_CONTRATOS_AR_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBizilagun.SERVICE_COMUNIDADES_ALQUILERES_ID;
import static aa14f.model.config.business.AA14BusinessConfigForBizilagun.SERVICE_FIANZAS_DEPOSITO_CONTRATOS_ID;

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
import r01f.locale.Language;
import r01f.types.Color;
import r01f.types.contact.ContactInfo;
import r01f.types.datetime.Time;
import r01f.types.geo.GeoOIDs.GeoZipCode;
import r01f.types.geo.GeoPosition;
import r01f.types.geo.GeoStreet;

@Slf4j
@Accessors(prefix="_")
public class AA14ConfigBuilderForBizilagun 
	 extends AA14ConfigBuilderForEJGVBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	// colors
	private static final Color COLOR_FIANZAS_DEPOSITO_CONTRATOS = Color.from("blue");
	private static final Color COLOR_COMUNIDADES_ALQUILERES = Color.from("black");
	
	// Texts
	private static final String FIANZAS_TXT_ES = "Depósito de fianzas y registro de contratos de arrendamiento de fincas urbanas";
	private static final String COMUNIDADES_TXT_ES = "Consultas sobre Comunidades y alquileres";
	
	private static final String FIANZAS_TXT_EU = "Fidantzak jartzea eta hiri finken errentamendu kontratuen erregistroa";
	private static final String COMUNIDADES_TXT_EU = "Komunitateak eta alokairuak";
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
			AA14OrgDivision division = _buildDivision(AA14BusinessID.BIZILAGUN,
													  org,
										  			  AA14OrgDivisionOID.supply(),BIZILAGUN_ID,
										  			  "Bizilagun","Bizilagun");
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
		// --- Service: Fianzas y depósito de contratos
		{
			AA14OrgDivisionService fianzasDepositoContratosService = _buildService(AA14BusinessID.BIZILAGUN,
																				   org,
															    				   this.within(divisions).find(BIZILAGUN_ID),
															    				   AA14OrgDivisionServiceOID.supply(),SERVICE_FIANZAS_DEPOSITO_CONTRATOS_ID,
															    				   FIANZAS_TXT_ES,FIANZAS_TXT_EU);
			outServices.add(fianzasDepositoContratosService);
			log.warn("\t\t[Service]: Created {}",
					 fianzasDepositoContratosService.getId());
		}
		// --- Service: Comunidades y Alquileres
		{
			AA14OrgDivisionService comunidadesAlquileresService = _buildService(AA14BusinessID.BIZILAGUN,
																			    org,
														     					this.within(divisions).find(BIZILAGUN_ID),
														     					AA14OrgDivisionServiceOID.supply(),SERVICE_COMUNIDADES_ALQUILERES_ID,
														     					COMUNIDADES_TXT_ES,COMUNIDADES_TXT_EU);
			outServices.add(comunidadesAlquileresService);
			log.warn("\t\t[Service]: Created {}",
					 comunidadesAlquileresService.getId());
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
		
		// --- Location: Fianzas y depósito de contratos AR
		{
			AA14OrgDivisionServiceLocation fianzasDepositoContratosAr = _buildLocation(AA14BusinessID.BIZILAGUN,
																					   org,
																					   this.within(divisions).find(BIZILAGUN_ID),
																					   this.within(services).find(SERVICE_FIANZAS_DEPOSITO_CONTRATOS_ID),
																					   AA14OrgDivisionServiceLocationOID.supply(),LOC_FIANZAS_DEPOSITO_CONTRATOS_AR_ID,
																					   FIANZAS_TXT_ES + " (AR)",FIANZAS_TXT_EU + " (AR)",
																					   _buildBiziLagunArabaGeoPosition(),
																					   _buildBiziLagunGasteizContactInfo(),
																					   COLOR_FIANZAS_DEPOSITO_CONTRATOS);
			outLocs.add(fianzasDepositoContratosAr);
			log.warn("\t\t\t[Location]: Created {}",
					 fianzasDepositoContratosAr.getId());
		}
		// --- Location: Comunidades y Alquileres AR
		{
			AA14OrgDivisionServiceLocation comunidadesAlquileresAr = _buildLocation(AA14BusinessID.BIZILAGUN,
																					org,
														 							this.within(divisions).find(BIZILAGUN_ID),
														 							this.within(services).find(SERVICE_COMUNIDADES_ALQUILERES_ID),
														 							AA14OrgDivisionServiceLocationOID.supply(),LOC_COMUNIDADES_ALQUILERES_AR_ID,
														 							COMUNIDADES_TXT_ES + " (AR)",COMUNIDADES_TXT_EU + " (AR)",
														 							_buildBiziLagunArabaGeoPosition(),
														 							_buildBiziLagunGasteizContactInfo(),
														 							COLOR_COMUNIDADES_ALQUILERES);
			outLocs.add(comunidadesAlquileresAr);
			log.warn("\t\t\t[Location]: Created {}",
					 comunidadesAlquileresAr.getId());
		}
		// --- Location: Comunidades y Alquileres GI
		{
			AA14OrgDivisionServiceLocation comunidadesAlquileresGi = _buildLocation(AA14BusinessID.BIZILAGUN,
																					org,
													 	 						    this.within(divisions).find(BIZILAGUN_ID),
													 	 						    this.within(services).find(SERVICE_COMUNIDADES_ALQUILERES_ID),
													 	 						    AA14OrgDivisionServiceLocationOID.supply(),LOC_COMUNIDADES_ALQUILERES_GI_ID,
													 	 						    COMUNIDADES_TXT_ES + " (GI)",COMUNIDADES_TXT_EU + " (GI)",
													 	 						    _buildBiziLagunGipuzkoaGeoPosition(),
													 	 						    _buildBiziLagunGipuzkoaContactInfo(),
													 	 						    COLOR_COMUNIDADES_ALQUILERES);
			outLocs.add(comunidadesAlquileresGi);
			log.warn("\t\t\t[Location]: Created {}",
					 comunidadesAlquileresGi.getId());
		}
		// --- Location: Comunidades y Alquileres BIZ
		{
			AA14OrgDivisionServiceLocation comunidadesAlquileresBiz = _buildLocation(AA14BusinessID.BIZILAGUN,
																					 org,
													 	  							 this.within(divisions).find(BIZILAGUN_ID),
													 	  							 this.within(services).find(SERVICE_COMUNIDADES_ALQUILERES_ID),
													 	  							 AA14OrgDivisionServiceLocationOID.supply(),LOC_COMUNIDADES_ALQUILERES_BIZ_ID,
													 	  							 COMUNIDADES_TXT_ES + " (BIZ)",COMUNIDADES_TXT_EU + " (BIZ)",
													 	  							 _buildBiziLagunBizkaiaGeoPosition(),
													 	  							 _buildBiziLagunBizkaiaContactInfo(),
													 	  							 COLOR_COMUNIDADES_ALQUILERES);
			outLocs.add(comunidadesAlquileresBiz);
			log.warn("\t\t\t[Location]: Created {}",
					 comunidadesAlquileresBiz.getId());
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
		
		// Schedule: Fianzas y depósito de contratos AR
		{
			AA14Schedule fianzasDepositoContratosSchAr = _buildSchedule(AA14BusinessID.BIZILAGUN,
																		SCH_FIANZAS_DEPOSITO_CONTRATOS_AR_ID,
															   			FIANZAS_TXT_ES + " SCHEDULE (AR)",FIANZAS_TXT_EU + " SCHEDULE (AR)",
															   			_createBizilagunScheduleBookingConfig(30,			// slot length
																	   								 		  3),			// max appointments in slot
															   			null,		// no qmatic orchestra config
															   			this.within(locs).find(LOC_FIANZAS_DEPOSITO_CONTRATOS_AR_ID));
			outSchs.add(fianzasDepositoContratosSchAr);
			log.warn("\t\t\t[Schedule]: Created {}",
					 fianzasDepositoContratosSchAr.getId());
		}
		// Schedule: Comunidades y Alquileres AR
		{
			AA14Schedule comunidadesAlquileresSchAr = _buildSchedule(AA14BusinessID.BIZILAGUN,
																	 SCH_COMUNIDADES_ALQUILERES_AR_ID,
																	 COMUNIDADES_TXT_ES + " SCHEDULE (AR)",COMUNIDADES_TXT_EU + " SCHEDULE (AR)",
																	 _createBizilagunScheduleBookingConfig(30,				// slot length
																								  		   2),				// max appointments in slot
																	 null,		// no qmatic orchestra config
																	 this.within(locs).find(LOC_COMUNIDADES_ALQUILERES_AR_ID));
			outSchs.add(comunidadesAlquileresSchAr);
			log.warn("\t\t\t[Schedule]: Created {}",
					 comunidadesAlquileresSchAr.getId());
		}
		// Schedule: Comunidades y Alquileres GI
		{
			AA14Schedule comunidadesAlquileresSchGi = _buildSchedule(AA14BusinessID.BIZILAGUN,
																	 SCH_COMUNIDADES_ALQUILERES_GI_ID,
																	 COMUNIDADES_TXT_ES + " SCHEDULE (GI)",COMUNIDADES_TXT_EU + " SCHEDULE (GI)",
																	 _createBizilagunScheduleBookingConfig(15,				// slot length
																								  		   1),				// max appointments in slot
																	 null,		// no qmatic orchestra config
																	 this.within(locs).find(LOC_COMUNIDADES_ALQUILERES_GI_ID));
			outSchs.add(comunidadesAlquileresSchGi);
			log.warn("\t\t\t[Schedule]: Created {}",
					 comunidadesAlquileresSchGi.getId());
		}
		// Schedule: Comunidades y Alquileres BIZ
		{
			AA14Schedule comunidadesAlquileresSchBiz = _buildSchedule(AA14BusinessID.BIZILAGUN,
																	  SCH_COMUNIDADES_ALQUILERES_BIZ_ID,
																	  COMUNIDADES_TXT_ES + " SCHEDULE (BIZ)",COMUNIDADES_TXT_EU + " SCHEDULE (BIZ)",
																	  _createBizilagunScheduleBookingConfig(15,				// slot length
																								  			1),				// max appointments in slot
																	  null,		// no qmatic orchestra config
																	  this.within(locs).find(LOC_COMUNIDADES_ALQUILERES_BIZ_ID));
			outSchs.add(comunidadesAlquileresSchBiz);
			log.warn("\t\t\t[Schedule]: Created {}",
					 comunidadesAlquileresSchBiz.getId());
		}
		return outSchs;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	private static AA14ScheduleBookingConfig _createBizilagunScheduleBookingConfig(final int slotLength,
																				   final int maxAppointmentsInSlot) {
		return new AA14ScheduleBookingConfig(Time.of("8:30"),		// day bookable range start 
				    						 Time.of("14:00"),		// day bookable range end
											 slotLength,			// slot length
											 maxAppointmentsInSlot,	// max appointments in slot
											 null);					// future booking limit (no limit)
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  TODO revisar localizaciones
/////////////////////////////////////////////////////////////////////////////////////////
	protected static GeoPosition _buildBiziLagunArabaGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(ARABA_COUNTY)
						  .withMunicipality(GASTEIZ)
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Samaniego, 2")
							  			   .withNameInLang(Language.BASQUE,"Samaniego, 2"))
						  .withZipCode(GeoZipCode.forId("01008"));
	}
	protected static ContactInfo _buildBiziLagunGasteizContactInfo() {
		return ContactInfo.create()
						 		.addPhones(_buildZuzenenanPhones());
	}
	protected static GeoPosition _buildBiziLagunGipuzkoaGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(GIPUZKOA_COUNTY)
						  .withMunicipality(DONOSTIA)
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Andia 13, 6ª planta")
							  			   .withNameInLang(Language.BASQUE,"Andia 13, 6. solairua"))
						  .withZipCode(GeoZipCode.forId("20004"));
	}
	protected static ContactInfo _buildBiziLagunGipuzkoaContactInfo() {
		return ContactInfo.create()
						 		.addPhones(_buildZuzenenanPhones());
	}
	protected static GeoPosition _buildBiziLagunBizkaiaGeoPosition() {
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
	protected static ContactInfo _buildBiziLagunBizkaiaContactInfo() {
		return ContactInfo.create()
						 		.addPhones(_buildZuzenenanPhones());
	}
}
