package aa14f.model.config.business.builder;

import static aa14f.model.config.business.AA14BusinessConfigForJustizia.JUSTIZIA_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.LOC_JUSTIZIA_REGISTRO_REPARTO_AM_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.LOC_JUSTIZIA_REGISTRO_REPARTO_AZ_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.LOC_JUSTIZIA_REGISTRO_REPARTO_BAL_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.LOC_JUSTIZIA_REGISTRO_REPARTO_BER_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.LOC_JUSTIZIA_REGISTRO_REPARTO_BIL_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.LOC_JUSTIZIA_REGISTRO_REPARTO_BK_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.LOC_JUSTIZIA_REGISTRO_REPARTO_DON_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.LOC_JUSTIZIA_REGISTRO_REPARTO_DUR_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.LOC_JUSTIZIA_REGISTRO_REPARTO_EIB_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.LOC_JUSTIZIA_REGISTRO_REPARTO_GER_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.LOC_JUSTIZIA_REGISTRO_REPARTO_GTX_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.LOC_JUSTIZIA_REGISTRO_REPARTO_IRU_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.LOC_JUSTIZIA_REGISTRO_REPARTO_TO_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.LOC_JUSTIZIA_REGISTRO_REPARTO_VI_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.ORG_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_AM_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_AZ_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_BAL_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_BER_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_BIL_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_BK_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_DON_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_DUR_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_EIB_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_GER_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_GTX_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_IRU_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_TO_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_VI_ID;
import static aa14f.model.config.business.AA14BusinessConfigForJustizia.SERVICE_ID;

import java.util.Collection;

import com.google.common.collect.Lists;

import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.AA14ScheduleBookingConfig;
import aa14f.model.config.AA14ScheduleBookingLimit;
import aa14f.model.config.business.AA14BusinessConfigForJustizia;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.LanguageTextsMapBacked;
import r01f.types.Color;
import r01f.types.contact.ContactInfo;
import r01f.types.contact.ContactInfoUsage;
import r01f.types.contact.ContactPhone;
import r01f.types.contact.EMail;
import r01f.types.datetime.Time;
import r01f.types.geo.GeoMunicipality;
import r01f.types.geo.GeoOIDs.GeoZipCode;
import r01f.types.geo.GeoPosition;
import r01f.types.geo.GeoStreet;
import r01f.util.types.Dates;

/**
 *  Configuration for Justizia Application
 *  One service with 14 locations/schedules 
 *  Each location matches with a judicial party for this municipality.
 *  	For each location, the judicial party is represented with the location municipality
 *  	In the location name the judicial party specific service name is located (instead of a more meaning text), for the appointment requestor to know
 *  	This location name is displayed in the front end as the "Location service", but internally all locations belong to the same service id. 
 *
 * Notifications for this app are sent with a "justizia" e-mail address instead of the default "zuzenean" one.
 */
@Slf4j
@Accessors(prefix="_")
public class AA14ConfigBuilderForJustizia 
	 extends AA14ConfigBuilderForEJGVBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	// colors
	private static final Color COLOR_JUSTIZIA = Color.from("2f5a7c"); 
	
	// Texts
	private static final String SRV_REGISTRO_REPARTO_TXT_ES = "Registro y Reparto - Atención presencial";
	private static final String SRV_REGISTRO_REPARTO_TXT_EU = "Erregistro eta banaketa - Aurrez-aurreko arreta";
	private static final String PROCEDURE_TXT_ES = "Presentación de escritos de inicio";
	private static final String PROCEDURE_TXT_EU = "Hasierako Idazkiak aurkezteko gunea";
	
	private static final String LOC_REGISTRO_REPARTO_TXT_ES = "Servicio de Registro y Reparto";
	private static final String LOC_REGISTRO_REPARTO_TXT_EU = "Erregistro eta Banaketa Zerbitzua";
	private static final String LOC_AT_PUBLICO_TXT_ES = "Servicio de Información al Público";
	private static final String LOC_AT_PUBLICO_TXT_EU = "Herritarrentzako Informazio Zerbitzua";
	private static final String LOC_REGISTRO_CIVIL_TXT_ES = "Decanato y Regisro Civil";
	private static final String LOC_REGISTRO_CIVIL_TXT_EU = "Dekanotza eta Erregistro Zibila";
	private static final String LOC_SERVICIOS_COMUNES_TXT_ES = "Servicios comunes";
	private static final String LOC_SERVICIOS_COMUNES_TXT_EU = "Zerbitzu Erkideak";
	private static final String LOC_SERVICIO_COMUN_PROCESAL_TXT_ES = "Servicio Común Procesal - Sección General";
	private static final String LOC_SERVICIO_COMUN_PROCESAL_TXT_EU = "Zerbitzu Erkide Prozesala - Atal Nagusia";
	private static final String SCH_REGISTRO_REPARTO_TXT_ES = "Registro y Reparto - Atención presencial";
	private static final String SCH_REGISTRO_REPARTO_TXT_EU = "Erregistro eta banaketa - Aurrez-aurreko arreta";
/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Custom e-mail address for notifications
     * @return	new email config
     */
	public static EMail getNotifierFrom() {
		return EMail.of("justiziaNoReply@justizia.eus");
	}
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
//	ORG DIVISION
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public Collection<AA14OrgDivision> createOrgDivisionConfigs(final AA14Organization org) {
		Collection<AA14OrgDivision> outDivs = Lists.newArrayListWithExpectedSize(1);		
		// --- Division
		{
			AA14OrgDivision division = _buildDivision(AA14BusinessID.JUSTIZIA,
													  org,
									  				  AA14OrgDivisionOID.supply(),JUSTIZIA_ID,
									  				  "Justizia","Justizia");
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
		// --- Service: Registration and delivery
		{
			AA14OrgDivisionService registrationAndDeliveryService = _buildService(AA14BusinessID.JUSTIZIA,
																				  org,
															    				  this.within(divisions).find(JUSTIZIA_ID),
															    				  AA14OrgDivisionServiceOID.supply(),SERVICE_ID,
															    				  SRV_REGISTRO_REPARTO_TXT_ES,SRV_REGISTRO_REPARTO_TXT_EU);
			registrationAndDeliveryService.setProcedure(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
															     .add(Language.SPANISH,PROCEDURE_TXT_ES)
															     .add(Language.BASQUE,PROCEDURE_TXT_EU));
			outServices.add(registrationAndDeliveryService);
			log.warn("\t\t[Service]: Created {}",
					 registrationAndDeliveryService.getId());
		}
		return outServices;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LOCATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Collection<AA14OrgDivisionServiceLocation> createOrgDivisionServiceLocationsConfigs(final AA14Organization org,
																  							   final Collection<AA14OrgDivision> divisions,
																  							   final Collection<AA14OrgDivisionService> services) {
		Collection<AA14OrgDivisionServiceLocation> outLocs = Lists.newArrayListWithExpectedSize(14);
		
		// --- Location: Registration and delivery Bilbao
		{
			AA14OrgDivisionServiceLocation bilbaoRDLocation = _buildLocation(AA14BusinessID.JUSTIZIA,
																			 org,
																			 this.within(divisions).find(JUSTIZIA_ID),
																			 this.within(services).find(SERVICE_ID),
																			 AA14OrgDivisionServiceLocationOID.supply(),AA14BusinessConfigForJustizia.LOC_JUSTIZIA_REGISTRO_REPARTO_BIL_ID,
																			 LOC_AT_PUBLICO_TXT_ES, LOC_AT_PUBLICO_TXT_EU,
																			 _buildJustiziaBilbaoGeoPosition(),
																			 _buildJustiziaBilbaoContactInfo(),
																			 COLOR_JUSTIZIA);
			outLocs.add(bilbaoRDLocation);
			log.warn("\t\t\t[Location]: Created {}",
					 bilbaoRDLocation.getId());
		}
		// --- Location: Registration and delivery Getxo
		{
			AA14OrgDivisionServiceLocation getxoRDLocation = _buildLocation(AA14BusinessID.JUSTIZIA,
																			org,
																		    this.within(divisions).find(JUSTIZIA_ID),
																		    this.within(services).find(SERVICE_ID),
																		    AA14OrgDivisionServiceLocationOID.supply(),LOC_JUSTIZIA_REGISTRO_REPARTO_GTX_ID,
																		    LOC_AT_PUBLICO_TXT_ES, LOC_AT_PUBLICO_TXT_EU,
																		    _buildJustiziaGetxoGeoPosition(),
																		    _buildJustiziaGetxoContactInfo(),
																		    COLOR_JUSTIZIA);
			outLocs.add(getxoRDLocation);
			log.warn("\t\t\t[Location]: Created {}",
					 getxoRDLocation.getId());
		}
		// --- Location: Registration and delivery Gernika-Lumo
		{
			AA14OrgDivisionServiceLocation gernikaRDLocation = _buildLocation(AA14BusinessID.JUSTIZIA,
																			  org,
																			  this.within(divisions).find(JUSTIZIA_ID),
																			  this.within(services).find(SERVICE_ID),
																			  AA14OrgDivisionServiceLocationOID.supply(),LOC_JUSTIZIA_REGISTRO_REPARTO_GER_ID,
																			  LOC_AT_PUBLICO_TXT_ES,LOC_AT_PUBLICO_TXT_EU,
																			  _buildJustiziaGernikaLumoGeoPosition(),
																			  _buildJustiziaGernikaLumoContactInfo(),
																			  COLOR_JUSTIZIA);
			outLocs.add(gernikaRDLocation);
			log.warn("\t\t\t[Location]: Created {}",
					 gernikaRDLocation.getId());
		}
		// --- Location: Registration and delivery Durango
		{
			AA14OrgDivisionServiceLocation durangoRDLocation = _buildLocation(AA14BusinessID.JUSTIZIA,
																			  org,
																			  this.within(divisions).find(JUSTIZIA_ID),
																			  this.within(services).find(SERVICE_ID),
																			  AA14OrgDivisionServiceLocationOID.supply(),LOC_JUSTIZIA_REGISTRO_REPARTO_DUR_ID,
																			  LOC_REGISTRO_CIVIL_TXT_ES, LOC_REGISTRO_CIVIL_TXT_EU,
																			  _buildJustiziaDurangoGeoPosition(),
																			  _buildJustiziaDurangoContactInfo(),
																			  COLOR_JUSTIZIA);
			outLocs.add(durangoRDLocation);
			log.warn("\t\t\t[Location]: Created {}",
					 durangoRDLocation.getId());			
		}
		// --- Location: Registration and delivery Balmaseda
		{
			AA14OrgDivisionServiceLocation balmasedaRDLocation = _buildLocation(AA14BusinessID.JUSTIZIA,
																				org,
																				this.within(divisions).find(JUSTIZIA_ID),
																				this.within(services).find(SERVICE_ID),
																				AA14OrgDivisionServiceLocationOID.supply(),LOC_JUSTIZIA_REGISTRO_REPARTO_BAL_ID,
																				LOC_SERVICIOS_COMUNES_TXT_ES + ", Balmaseda", LOC_SERVICIOS_COMUNES_TXT_EU + ", Balmaseda",
																				_buildJustiziaBalmasedaGeoPosition(),
																				_buildJustiziaBalmasedaContactInfo(),
																				COLOR_JUSTIZIA);
			outLocs.add(balmasedaRDLocation);
			log.warn("\t\t\t[Location]: Created {}",
					 balmasedaRDLocation.getId());
		}
		// --- Location: Registration and delivery Barakaldo
		{
			AA14OrgDivisionServiceLocation barakaldoRDLocation = _buildLocation(AA14BusinessID.JUSTIZIA,
																				org,
																				this.within(divisions).find(JUSTIZIA_ID),
																				this.within(services).find(SERVICE_ID),
																				AA14OrgDivisionServiceLocationOID.supply(),LOC_JUSTIZIA_REGISTRO_REPARTO_BK_ID,
																				LOC_AT_PUBLICO_TXT_ES, LOC_AT_PUBLICO_TXT_EU,
																				_buildJustiziaBarakaldoGeoPosition(),
																				_buildJustiziaBarakaldoContactInfo(),
																				COLOR_JUSTIZIA);
			outLocs.add(barakaldoRDLocation);
			log.warn("\t\t\t[Location]: Created {}",
					 barakaldoRDLocation.getId());
		}
		// --- Location: Registration and delivery Amurrio
		{
			AA14OrgDivisionServiceLocation amurrioRDLocation = _buildLocation(AA14BusinessID.JUSTIZIA,
																			  org,
																			  this.within(divisions).find(JUSTIZIA_ID),
																			  this.within(services).find(SERVICE_ID),
																			  AA14OrgDivisionServiceLocationOID.supply(),LOC_JUSTIZIA_REGISTRO_REPARTO_AM_ID,
																			  LOC_AT_PUBLICO_TXT_ES, LOC_AT_PUBLICO_TXT_EU,
																			  _buildJustiziaAmurrioGeoPosition(),
																			  _buildJustiziaAmurrioContactInfo(),
																			  COLOR_JUSTIZIA);
			outLocs.add(amurrioRDLocation);
			log.warn("\t\t\t[Location]: Created {}",
					 amurrioRDLocation.getId());
		}
		// --- Location: Registration and delivery Vitoria-Gasteiz
		{
			AA14OrgDivisionServiceLocation vitoriaRDLocation = _buildLocation(AA14BusinessID.JUSTIZIA,
																			  org,
																			  this.within(divisions).find(JUSTIZIA_ID),
																			  this.within(services).find(SERVICE_ID),
																			  AA14OrgDivisionServiceLocationOID.supply(),LOC_JUSTIZIA_REGISTRO_REPARTO_VI_ID,
																			  LOC_AT_PUBLICO_TXT_ES, LOC_AT_PUBLICO_TXT_EU,
																			  _buildJustiziaVitoriaGeoPosition(),
																			  _buildJustiziaVitoriaContactInfo(),
																			  COLOR_JUSTIZIA);
			outLocs.add(vitoriaRDLocation);
			log.warn("\t\t\t[Location]: Created {}",
					 vitoriaRDLocation.getId());
		}
		// --- Location: Registration and delivery Azpeitia
		{
			AA14OrgDivisionServiceLocation azpeitiaRDLocation = _buildLocation(AA14BusinessID.JUSTIZIA,
																			   org,
																			   this.within(divisions).find(JUSTIZIA_ID),
																			   this.within(services).find(SERVICE_ID),
																			   AA14OrgDivisionServiceLocationOID.supply(),LOC_JUSTIZIA_REGISTRO_REPARTO_AZ_ID,
																			   LOC_AT_PUBLICO_TXT_ES, LOC_AT_PUBLICO_TXT_EU,
																			   _buildJustiziaAzpeitiaGeoPosition(),
																			   _buildJustiziaAzpeitiaContactInfo(),
																			   COLOR_JUSTIZIA);
			outLocs.add(azpeitiaRDLocation);
			log.warn("\t\t\t[Location]: Created {}",
					 azpeitiaRDLocation.getId());
		}
		// --- Location: Registration and delivery Bergara
		{
			AA14OrgDivisionServiceLocation bergaraRDLocation = _buildLocation(AA14BusinessID.JUSTIZIA,
																			  org,
																			  this.within(divisions).find(JUSTIZIA_ID),
																			  this.within(services).find(SERVICE_ID),
																			  AA14OrgDivisionServiceLocationOID.supply(),LOC_JUSTIZIA_REGISTRO_REPARTO_BER_ID,
																			  LOC_REGISTRO_REPARTO_TXT_ES, LOC_REGISTRO_REPARTO_TXT_EU,
																			  _buildJustiziaBergaraGeoPosition(),
																			  _buildJustiziaBergaraContactInfo(),
																			  COLOR_JUSTIZIA);
			outLocs.add(bergaraRDLocation);
			log.warn("\t\t\t[Location]: Created {}",
					 bergaraRDLocation.getId());
		}
		// --- Location: Registration and delivery Irun
		{
			AA14OrgDivisionServiceLocation irunRDLocation = _buildLocation(AA14BusinessID.JUSTIZIA,
																		   org,
																		   this.within(divisions).find(JUSTIZIA_ID),
																		   this.within(services).find(SERVICE_ID),
																		   AA14OrgDivisionServiceLocationOID.supply(),LOC_JUSTIZIA_REGISTRO_REPARTO_IRU_ID,
																		   LOC_SERVICIO_COMUN_PROCESAL_TXT_ES,LOC_SERVICIO_COMUN_PROCESAL_TXT_EU,
																		   _buildJustiziaIrunGeoPosition(),
																		   _buildJustiziaIrunContactInfo(),
																		   COLOR_JUSTIZIA);
			outLocs.add(irunRDLocation);
			log.warn("\t\t\t[Location]: Created {}",
					 irunRDLocation.getId());
		}
		// --- Location: Registration and delivery Eibar
		{
			AA14OrgDivisionServiceLocation eibarRDLocation = _buildLocation(AA14BusinessID.JUSTIZIA,
																			org,
																			this.within(divisions).find(JUSTIZIA_ID),
																			this.within(services).find(SERVICE_ID),
																			AA14OrgDivisionServiceLocationOID.supply(),LOC_JUSTIZIA_REGISTRO_REPARTO_EIB_ID,
																			LOC_REGISTRO_CIVIL_TXT_ES, LOC_REGISTRO_CIVIL_TXT_EU,
																			_buildJustiziaEibarGeoPosition(),
																			_buildJustiziaEibarContactInfo(),
																			COLOR_JUSTIZIA);
			outLocs.add(eibarRDLocation);
			log.warn("\t\t\t[Location]: Created {}",
					 eibarRDLocation.getId());
		}
		// --- Location: Registration and delivery Tolosa
		{
			AA14OrgDivisionServiceLocation tolosaRDLocation = _buildLocation(AA14BusinessID.JUSTIZIA,
																			 org,
																			 this.within(divisions).find(JUSTIZIA_ID),
																			 this.within(services).find(SERVICE_ID),
																			 AA14OrgDivisionServiceLocationOID.supply(),LOC_JUSTIZIA_REGISTRO_REPARTO_TO_ID,
																			 LOC_AT_PUBLICO_TXT_ES, LOC_AT_PUBLICO_TXT_EU,
																			 _buildJustiziaTolosaGeoPosition(),
																			 _buildJustiziaTolosaContactInfo(),
																			 COLOR_JUSTIZIA);
			outLocs.add(tolosaRDLocation);
			log.warn("\t\t\t[Location]: Created {}",
					 tolosaRDLocation.getId());
		}
		// --- Location: Registration and delivery Donostia/San Sebastián
		{
			AA14OrgDivisionServiceLocation donostiaRDLocation = _buildLocation(AA14BusinessID.JUSTIZIA,
																			   org,
																			   this.within(divisions).find(JUSTIZIA_ID),
																			   this.within(services).find(SERVICE_ID),
																			   AA14OrgDivisionServiceLocationOID.supply(),LOC_JUSTIZIA_REGISTRO_REPARTO_DON_ID,
																			   LOC_AT_PUBLICO_TXT_ES, LOC_AT_PUBLICO_TXT_EU,
																			   _buildJustiziaDonostiaGeoPosition(),
																			   _buildJustiziaDonostiaContactInfo(),
																			   COLOR_JUSTIZIA);
			outLocs.add(donostiaRDLocation);
			log.warn("\t\t\t[Location]: Created {}",
					 donostiaRDLocation.getId());
		};
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
		
		// Schedule: Registration and delivery Bilbao
		{
			AA14Schedule bilbaoRDSch = _buildSchedule(AA14BusinessID.JUSTIZIA,
													  SCH_JUSTIZIA_REGISTRO_REPARTO_BIL_ID,
												  	  SCH_REGISTRO_REPARTO_TXT_ES + " SCHEDULE (Bilbao)",SCH_REGISTRO_REPARTO_TXT_EU + " SCHEDULE (Bilbao)",
												  	  _createJustiziaScheduleBookingConfig(2),
												  	  null,		// no qmatic orchestra config
												  	  this.within(locs).find(LOC_JUSTIZIA_REGISTRO_REPARTO_BIL_ID));
			outSchs.add(bilbaoRDSch);
			log.warn("\t\t\t[Schedule]: Created {}",
					 bilbaoRDSch.getId());
		}
		// Schedule: Registration and delivery Getxo
		{
			AA14Schedule getxoRDSch = _buildSchedule(AA14BusinessID.JUSTIZIA,
													 SCH_JUSTIZIA_REGISTRO_REPARTO_GTX_ID,
												  	 SCH_REGISTRO_REPARTO_TXT_ES + " SCHEDULE (Getxo)", SCH_REGISTRO_REPARTO_TXT_EU + " SCHEDULE (Getxo)",
												  	 _createJustiziaScheduleBookingConfig(2),
												  	 null,		// no qmatic orchestra config
												  	 this.within(locs).find(LOC_JUSTIZIA_REGISTRO_REPARTO_GTX_ID));
			outSchs.add(getxoRDSch);
			log.warn("\t\t\t[Schedule]: Created {}",
					 getxoRDSch.getId());
		}
		// Schedule: Registration and delivery Gernika-Lumo
		{
			AA14Schedule gernikaRDSch = _buildSchedule(AA14BusinessID.JUSTIZIA,
													   SCH_JUSTIZIA_REGISTRO_REPARTO_GER_ID,
												  	   SCH_REGISTRO_REPARTO_TXT_ES + " SCHEDULE (Gernika-Lumo)", SCH_REGISTRO_REPARTO_TXT_EU + " SCHEDULE (Gernika-Lumo)",
												  	   _createJustiziaScheduleBookingConfig(1),
												  	   null,		// no qmatic orchestra config
												  	   this.within(locs).find(LOC_JUSTIZIA_REGISTRO_REPARTO_GER_ID));
			outSchs.add(gernikaRDSch);
			log.warn("\t\t\t[Schedule]: Created {}",
					 gernikaRDSch.getId());
		}
		// Schedule: Registration and delivery Durango
		{
			AA14Schedule durangoRDSch = _buildSchedule(AA14BusinessID.JUSTIZIA,
													   SCH_JUSTIZIA_REGISTRO_REPARTO_DUR_ID,
												  	   SCH_REGISTRO_REPARTO_TXT_ES + " SCHEDULE (Durango)", SCH_REGISTRO_REPARTO_TXT_EU + " SCHEDULE (Durango)",
												  	   _createJustiziaScheduleBookingConfig(1),
												  	   null,		// no qmatic orchestra config
												  	   this.within(locs).find(LOC_JUSTIZIA_REGISTRO_REPARTO_DUR_ID));
			outSchs.add(durangoRDSch);
			log.warn("\t\t\t[Schedule]: Created {}",
					 durangoRDSch.getId());
		}
		// Schedule: Registration and delivery Balmaseda
		{
			AA14Schedule balmasedaRDSch = _buildSchedule(AA14BusinessID.JUSTIZIA,
														 SCH_JUSTIZIA_REGISTRO_REPARTO_BAL_ID,
												  		 SCH_REGISTRO_REPARTO_TXT_ES + " SCHEDULE (Balmaseda)", SCH_REGISTRO_REPARTO_TXT_EU + " SCHEDULE (Balmaseda)",
												  		 _createJustiziaScheduleBookingConfig(1),
												  		 null,		// no qmatic orchestra config
												  		 this.within(locs).find(LOC_JUSTIZIA_REGISTRO_REPARTO_BAL_ID));
			outSchs.add(balmasedaRDSch);
			log.warn("\t\t\t[Schedule]: Created {}",
					 balmasedaRDSch.getId());
		}
		// Schedule: Registration and delivery Barakaldo
		{
			AA14Schedule barakaldoRDSch = _buildSchedule(AA14BusinessID.JUSTIZIA,
														 SCH_JUSTIZIA_REGISTRO_REPARTO_BK_ID,
												  		 SCH_REGISTRO_REPARTO_TXT_ES + " SCHEDULE (Barakaldo)", SCH_REGISTRO_REPARTO_TXT_EU + " SCHEDULE (Barakaldo)",
												  		 _createJustiziaScheduleBookingConfig(2),
												  		 null,		// no qmatic orchestra config
												  		 this.within(locs).find(LOC_JUSTIZIA_REGISTRO_REPARTO_BK_ID));
			outSchs.add(barakaldoRDSch);
			log.warn("\t\t\t[Schedule]: Created {}",
					 barakaldoRDSch.getId());
		}
		// Schedule: Registration and delivery Amurrio
		{
			AA14Schedule amurrioRDSch = _buildSchedule(AA14BusinessID.JUSTIZIA,
													   SCH_JUSTIZIA_REGISTRO_REPARTO_AM_ID,
												  	   SCH_REGISTRO_REPARTO_TXT_ES + " SCHEDULE (Amurrio)",SCH_REGISTRO_REPARTO_TXT_EU + " SCHEDULE (Amurrio)",
												  	   _createJustiziaScheduleBookingConfig(1),
												  	   null,		// no qmatic orchestra config
												  	   this.within(locs).find(LOC_JUSTIZIA_REGISTRO_REPARTO_AM_ID));
			outSchs.add(amurrioRDSch);
			log.warn("\t\t\t[Schedule]: Created {}",
					 amurrioRDSch.getId());
		}
		// Schedule: Registration and delivery Vitoria-Gazteiz
		{
			AA14Schedule vitoriaRDSch = _buildSchedule(AA14BusinessID.JUSTIZIA,
													   SCH_JUSTIZIA_REGISTRO_REPARTO_VI_ID,
												  	   SCH_REGISTRO_REPARTO_TXT_ES + " SCHEDULE (Vitoria-Gazteiz)",SCH_REGISTRO_REPARTO_TXT_EU + " SCHEDULE (Vitoria-Gazteiz)",
												  	   _createJustiziaScheduleBookingConfig(2),
												  	   null,		// no qmatic orchestra config
												  	   this.within(locs).find(LOC_JUSTIZIA_REGISTRO_REPARTO_VI_ID));
			outSchs.add(vitoriaRDSch);
			log.warn("\t\t\t[Schedule]: Created {}",
					 vitoriaRDSch.getId());
		}
		// Schedule: Registration and delivery Azpeitia
		{
			AA14Schedule azpeitiaRDSch = _buildSchedule(AA14BusinessID.JUSTIZIA,
														SCH_JUSTIZIA_REGISTRO_REPARTO_AZ_ID,
												  		SCH_REGISTRO_REPARTO_TXT_ES + " SCHEDULE (Azpeitia)",SCH_REGISTRO_REPARTO_TXT_EU + " SCHEDULE (Azpeitia)",
												  		_createJustiziaScheduleBookingConfig(1),
												  		null,		// no qmatic orchestra config
												  		this.within(locs).find(LOC_JUSTIZIA_REGISTRO_REPARTO_AZ_ID));
			outSchs.add(azpeitiaRDSch);
			log.warn("\t\t\t[Schedule]: Created {}",
					 azpeitiaRDSch.getId());
		}
		// Schedule: Registration and delivery Bergara
		{
			AA14Schedule bergaraRDSch = _buildSchedule(AA14BusinessID.JUSTIZIA,
													   SCH_JUSTIZIA_REGISTRO_REPARTO_BER_ID,
												  	   SCH_REGISTRO_REPARTO_TXT_ES + " SCHEDULE (Bergara)",SCH_REGISTRO_REPARTO_TXT_EU + " SCHEDULE (Bergara)",
												  	   _createJustiziaScheduleBookingConfig(1),
												  	   null,		// no qmatic orchestra config
												  	   this.within(locs).find(LOC_JUSTIZIA_REGISTRO_REPARTO_BER_ID));
			outSchs.add(bergaraRDSch);
			log.warn("\t\t\t[Schedule]: Created {}",
					 bergaraRDSch.getId());
		}
		// Schedule: Registration and delivery Irun
		{
			AA14Schedule irunRDSch = _buildSchedule(AA14BusinessID.JUSTIZIA,
													SCH_JUSTIZIA_REGISTRO_REPARTO_IRU_ID,
												  	SCH_REGISTRO_REPARTO_TXT_ES + " SCHEDULE (Irun)",SCH_REGISTRO_REPARTO_TXT_EU + " SCHEDULE (Irun)",
												  	_createJustiziaScheduleBookingConfig(1),
												  	null,		// no qmatic orchestra config
												  	this.within(locs).find(LOC_JUSTIZIA_REGISTRO_REPARTO_IRU_ID));
			outSchs.add(irunRDSch);
			log.warn("\t\t\t[Schedule]: Created {}",
					 irunRDSch.getId());
		}
		// Schedule: Registration and delivery Eibar
		{
			AA14Schedule eibarRDSch = _buildSchedule(AA14BusinessID.JUSTIZIA,
													 SCH_JUSTIZIA_REGISTRO_REPARTO_EIB_ID,
												  	 SCH_REGISTRO_REPARTO_TXT_ES + " SCHEDULE (Eibar)",SCH_REGISTRO_REPARTO_TXT_EU + " SCHEDULE (Eibar)",
												  	 _createJustiziaScheduleBookingConfig(1),
												  	 null,		// no qmatic orchestra config
												  	 this.within(locs).find(LOC_JUSTIZIA_REGISTRO_REPARTO_EIB_ID));
			outSchs.add(eibarRDSch);
			log.warn("\t\t\t[Schedule]: Created {}",
					 eibarRDSch.getId());
		}
		// Schedule: Registration and delivery Tolosa
		{
			AA14Schedule tolosaRDSch = _buildSchedule(AA14BusinessID.JUSTIZIA,
													  SCH_JUSTIZIA_REGISTRO_REPARTO_TO_ID,
												  	  SCH_REGISTRO_REPARTO_TXT_ES + " SCHEDULE (Tolosa)",SCH_REGISTRO_REPARTO_TXT_EU + " SCHEDULE (Tolosa)",
												  	  _createJustiziaScheduleBookingConfig(1),
												  	  null,		// no qmatic orchestra config
												  	  this.within(locs).find(LOC_JUSTIZIA_REGISTRO_REPARTO_TO_ID));
			outSchs.add(tolosaRDSch);
			log.warn("\t\t\t[Schedule]: Created {}",
					 tolosaRDSch.getId());
		}
		// Schedule: Registration and delivery Donostia/San Sebastián
		{
			AA14Schedule donostiaRDSch = _buildSchedule(AA14BusinessID.JUSTIZIA,
														SCH_JUSTIZIA_REGISTRO_REPARTO_DON_ID,
												  		SCH_REGISTRO_REPARTO_TXT_ES + " SCHEDULE (Donostia/San Sebastián)",SCH_REGISTRO_REPARTO_TXT_EU + " SCHEDULE (Donostia-San Sebastián)",
												  		_createJustiziaScheduleBookingConfig(2),
												  		null,		// no qmatic orchestra config
												  		this.within(locs).find(LOC_JUSTIZIA_REGISTRO_REPARTO_DON_ID));
			outSchs.add(donostiaRDSch);
			log.warn("\t\t\t[Schedule]: Created {}",
					 donostiaRDSch.getId());
		}
		return outSchs;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////
	private static AA14ScheduleBookingConfig _createJustiziaScheduleBookingConfig(final int maxAppointments) {
		return new AA14ScheduleBookingConfig(Time.of("9:00"),		// day bookable range start 
				    						 Time.of("14:00"),		// day bookable range end
											 10,					// slot length
											 maxAppointments,		// max appointments in slot
											 new AA14ScheduleBookingLimit(Dates.fromFormatedString("31/12/2020", Dates.ES_DEFAULT_FORMAT)));// future booking limit
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	
	private GeoPosition _buildJustiziaBergaraGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(GIPUZKOA_COUNTY)
						  .withMunicipality(GeoMunicipality.create()
													  	   .withNameInLang(Language.SPANISH,"Bergara")
													  	   .withNameInLang(Language.BASQUE,"Bergara"))
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Ariznoa Enparantza, s/n, Planta Baja")
							  			   .withNameInLang(Language.BASQUE,"Ariznoa Enparantza, z/g, Beheko solairua"))
						  .withZipCode(GeoZipCode.forId("20570"));
	}
	private ContactInfo _buildJustiziaBergaraContactInfo() {
		return ContactInfo.create()
						  .addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
											  .withNumber("943038055"));
	}
	private ContactInfo _buildJustiziaAzpeitiaContactInfo() {
		return ContactInfo.create()
						  .addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
											  .withNumber("943025191"));
	}
	private GeoPosition _buildJustiziaAzpeitiaGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(GIPUZKOA_COUNTY)
						  .withMunicipality(GeoMunicipality.create()
													  	   .withNameInLang(Language.SPANISH,"Azpeitia")
													  	   .withNameInLang(Language.BASQUE,"Azpeitia"))
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Bº Sanjuandegi, Euskalerria 30")
							  			   .withNameInLang(Language.BASQUE,"Sanjuandegi Auz., Euskalerria 30"))
						  .withZipCode(GeoZipCode.forId("20530"));
	}
	private ContactInfo _buildJustiziaVitoriaContactInfo() {
		return ContactInfo.create()
						  .addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
											  .withNumber("945004897"));
	}
	private GeoPosition _buildJustiziaVitoriaGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(ARABA_COUNTY)
						  .withMunicipality(GASTEIZ)
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Avenida Gasteiz, 18, Planta de Acceso")
							  			   .withNameInLang(Language.BASQUE,"Gasteiz Etorbidea, 18, Sarbide solairua"))
						  .withZipCode(GeoZipCode.forId("01008"));
	}
	private ContactInfo _buildJustiziaAmurrioContactInfo() {
		return ContactInfo.create()
						  .addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
											  .withNumber("945026990"));
	}
	private GeoPosition _buildJustiziaAmurrioGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(ARABA_COUNTY)
						  .withMunicipality(GeoMunicipality.create()
													  	   .withNameInLang(Language.SPANISH,"Amurrio")
													  	   .withNameInLang(Language.BASQUE,"Amurrio"))
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Elexondo, 33, Planta Baja")
							  			   .withNameInLang(Language.BASQUE,"Elexondo, 33, Beheko solairua"))
						  .withZipCode(GeoZipCode.forId("01470"));
	}
	private ContactInfo _buildJustiziaBarakaldoContactInfo() {
		return ContactInfo.create()
						  .addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
											  .withNumber("944001000"));
	}
	private GeoPosition _buildJustiziaBarakaldoGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(BIZKAIA_COUNTY)
						  .withMunicipality(GeoMunicipality.create()
													  	   .withNameInLang(Language.SPANISH,"Barakaldo")
													  	   .withNameInLang(Language.BASQUE,"Barakaldo"))
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Bide Onera Plaza, s/n, Planta Baja")
							  			   .withNameInLang(Language.BASQUE,"Bide Onera Plaza, z/g, Beheko solairua"))
						  .withZipCode(GeoZipCode.forId("48901"));
	}
	private ContactInfo _buildJustiziaBalmasedaContactInfo() {
		return ContactInfo.create()
						  .addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
											  .withNumber("946039956"));
	}
	private GeoPosition _buildJustiziaBalmasedaGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(BIZKAIA_COUNTY)
						  .withMunicipality(GeoMunicipality.create()
													  	   .withNameInLang(Language.SPANISH,"Balmaseda")
													  	   .withNameInLang(Language.BASQUE,"Balmaseda"))
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Calle de la Estación, s/n, Primera planta")
							  			   .withNameInLang(Language.BASQUE,"Estacion Kalea, z/g, Lehenengo solairua"))
						  .withZipCode(GeoZipCode.forId("48800"));
	}
	private ContactInfo _buildJustiziaDurangoContactInfo() {
		return ContactInfo.create()
						  .addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
											  .withNumber("946030062"));
	}
	private GeoPosition _buildJustiziaDurangoGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(BIZKAIA_COUNTY)
						  .withMunicipality(GeoMunicipality.create()
													  	   .withNameInLang(Language.SPANISH,"Durango")
													  	   .withNameInLang(Language.BASQUE,"Durango"))
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Ezkurdi Plaza, s/n, Planta baja")
							  			   .withNameInLang(Language.BASQUE,"Ezkurdi Plaza, z/g, Beheko solairua"))
						  .withZipCode(GeoZipCode.forId("48200"));
	}
	private ContactInfo _buildJustiziaGernikaLumoContactInfo() {
		return ContactInfo.create()
						  .addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
											  .withNumber("946035773"));
	}
	private GeoPosition _buildJustiziaGernikaLumoGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(BIZKAIA_COUNTY)
						  .withMunicipality(GeoMunicipality.create()
													  	   .withNameInLang(Language.SPANISH,"Gernika-Lumo")
													  	   .withNameInLang(Language.BASQUE,"Gernika-Lumo"))
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Allende Salazar, 9, Planta baja")
							  			   .withNameInLang(Language.BASQUE,"Allende Salazar, 9, Beheko solairua"))
						  .withZipCode(GeoZipCode.forId("48300"));
	}
	private ContactInfo _buildJustiziaGetxoContactInfo() {
		return ContactInfo.create()
						  .addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
											  .withNumber("946023972"));
	}
	private GeoPosition _buildJustiziaGetxoGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(BIZKAIA_COUNTY)
						  .withMunicipality(GeoMunicipality.create()
													  	   .withNameInLang(Language.SPANISH,"Getxo")
													  	   .withNameInLang(Language.BASQUE,"Getxo"))
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Fueros, 10, Planta baja")
							  			   .withNameInLang(Language.BASQUE,"Fueros, 10, Beheko solairua"))
						  .withZipCode(GeoZipCode.forId("48992"));
	}
	private ContactInfo _buildJustiziaBilbaoContactInfo() {
		return ContactInfo.create()
						  .addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
											  .withNumber("944016650"));
	}
	private GeoPosition _buildJustiziaBilbaoGeoPosition() {
		
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(BIZKAIA_COUNTY)
						  .withMunicipality(GeoMunicipality.create()
													  	   .withNameInLang(Language.SPANISH,"Bilbao")
													  	   .withNameInLang(Language.BASQUE,"Bilbao"))
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Barroeta Aldamar, 10, Planta de Acceso")
							  			   .withNameInLang(Language.BASQUE,"Barroeta Aldamar, 10, Sarbide solairua"))
						  .withZipCode(GeoZipCode.forId("48001"));
	}
	private ContactInfo _buildJustiziaDonostiaContactInfo() {
		return ContactInfo.create()
						  .addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
											  .withNumber("943000700"));
	}
	private GeoPosition _buildJustiziaDonostiaGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(GIPUZKOA_COUNTY)
						  .withMunicipality(GeoMunicipality.create()
													  	   .withNameInLang(Language.SPANISH,"Donostia/San Sebastián")
													  	   .withNameInLang(Language.BASQUE,"Donostia/San Sebastián"))
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Teresa de Calcuta Plaza, 1 (Atotxa), Planta baja")
							  			   .withNameInLang(Language.BASQUE,"Teresa de Calcuta Plaza, 1 (Atotxa), Beheko solairua"))
						  .withZipCode(GeoZipCode.forId("20012"));
	}
	private ContactInfo _buildJustiziaTolosaContactInfo() {
		return ContactInfo.create()
						  .addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
											  .withNumber("943002600"));
	}
	private GeoPosition _buildJustiziaTolosaGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(GIPUZKOA_COUNTY)
						  .withMunicipality(GeoMunicipality.create()
													  	   .withNameInLang(Language.SPANISH,"Tolosa")
													  	   .withNameInLang(Language.BASQUE,"Tolosa"))
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"San Juan, 3, Planta Baja")
							  			   .withNameInLang(Language.BASQUE,"San Juan, 3, Beheko solairua"))
						  .withZipCode(GeoZipCode.forId("20400"));
	}
	private ContactInfo _buildJustiziaEibarContactInfo() {
		return ContactInfo.create()
						  .addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
											  .withNumber("943033404"));
	}
	private GeoPosition _buildJustiziaEibarGeoPosition() {
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(GIPUZKOA_COUNTY)
						  .withMunicipality(GeoMunicipality.create()
													  	   .withNameInLang(Language.SPANISH,"Eibar")
													  	   .withNameInLang(Language.BASQUE,"Eibar"))
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Juan Gisasola, 1, Planta baja")
							  			   .withNameInLang(Language.BASQUE,"Juan Gisasola, 1, Beheko solairua"))
						  .withZipCode(GeoZipCode.forId("20600"));
	}
	private ContactInfo _buildJustiziaIrunContactInfo() {
		return ContactInfo.create()
						  .addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
											  .withNumber("943020144"));
	}
	private GeoPosition _buildJustiziaIrunGeoPosition() {
		
		return GeoPosition.create()
						  .withCountry(SPAIN)
						  .withState(EUSKADI_STATE)
						  .withCounty(GIPUZKOA_COUNTY)
						  .withMunicipality(GeoMunicipality.create()
													  	   .withNameInLang(Language.SPANISH,"Irun")
													  	   .withNameInLang(Language.BASQUE,"Irun"))
						  .withStreet(GeoStreet.create()
									  	   .withNameInLang(Language.SPANISH,"Iparralde Etorbidea, 9, Planta de acceso")
							  			   .withNameInLang(Language.BASQUE,"Iparralde Etorbidea, 9, Sarbide solairua"))
						  .withZipCode(GeoZipCode.forId("20300"));
	}
}
