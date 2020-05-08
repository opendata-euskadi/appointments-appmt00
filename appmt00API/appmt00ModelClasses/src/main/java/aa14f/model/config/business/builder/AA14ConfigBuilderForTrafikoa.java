package aa14f.model.config.business.builder;

import static aa14f.model.config.business.AA14BusinessConfigForTrafikoa.DIVISION_ID;
import static aa14f.model.config.business.AA14BusinessConfigForTrafikoa.LOC_BILBAO_CATIT_ID;
import static aa14f.model.config.business.AA14BusinessConfigForTrafikoa.LOC_BILBAO_ID;
import static aa14f.model.config.business.AA14BusinessConfigForTrafikoa.LOC_DONOSTIA_ID;
import static aa14f.model.config.business.AA14BusinessConfigForTrafikoa.LOC_GASTEIZ_ID;
import static aa14f.model.config.business.AA14BusinessConfigForTrafikoa.ORG_ID;
import static aa14f.model.config.business.AA14BusinessConfigForTrafikoa.SCH_BILBAO_CATIT_ID;
import static aa14f.model.config.business.AA14BusinessConfigForTrafikoa.SCH_BILBAO_ID;
import static aa14f.model.config.business.AA14BusinessConfigForTrafikoa.SCH_DONOSTIA_ID;
import static aa14f.model.config.business.AA14BusinessConfigForTrafikoa.SCH_GASTEIZ_ID;
import static aa14f.model.config.business.AA14BusinessConfigForTrafikoa.SERVICE_ID;

import java.util.Collection;

import com.google.common.collect.Lists;

import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14Schedule;
import aa14f.model.config.AA14ScheduleBookingConfig;
import aa14f.model.config.AA14ScheduleOrchestraConfig;
import aa14f.model.config.business.AA14BusinessConfigForTrafikoa;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.types.Color;
import r01f.types.contact.ContactInfo;
import r01f.types.contact.ContactInfoUsage;
import r01f.types.contact.ContactMail;
import r01f.types.geo.GeoPosition;
import r01f.types.geo.GeoStreet;

@Slf4j
@Accessors(prefix="_")
public class AA14ConfigBuilderForTrafikoa 
	 extends AA14ConfigBuilderForEJGVBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final Color COLOR = Color.from("blue"); 
	
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
			AA14OrgDivision division = _buildDivision(AA14BusinessID.TRAFIKOA,
													  org,
										  			  AA14OrgDivisionOID.supply(),AA14BusinessConfigForTrafikoa.DIVISION_ID,
										  			  "Departamento de Seguridad","Segurtasuna Saila");
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
		Collection<AA14OrgDivisionService> outServices = Lists.newArrayListWithExpectedSize(1);
		// --- Service
		{
			AA14OrgDivisionService service = _buildService(AA14BusinessID.TRAFIKOA,
														   org,
														   this.within(divisions).find(DIVISION_ID),
														   AA14OrgDivisionServiceOID.supply(),SERVICE_ID,
														   "Trafikoa","Trafikoa");
			outServices.add(service);
			log.warn("\t\t[Service]: Created {}",
					 service.getId());
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
		// --- Location: Bilbao
		{
			AA14OrgDivisionServiceLocation bilbao = _buildLocation(AA14BusinessID.TRAFIKOA,
																   org,
																   this.within(divisions).find(AA14BusinessConfigForTrafikoa.DIVISION_ID),
																   this.within(services).find(AA14BusinessConfigForTrafikoa.SERVICE_ID),
																   AA14OrgDivisionServiceLocationOID.supply(),LOC_BILBAO_ID,
																   "Tráfico Bilbao","Trafikoa Bilbao",
																   GeoPosition.create()
																	  .withCountry(SPAIN)
																	  .withState(EUSKADI_STATE)
																	  .withCounty(BIZKAIA_COUNTY)
																	  .withMunicipality(BILBAO)
																	  .withStreet(GeoStreet.create()
																				  	   .withNameInLang(Language.SPANISH,"Gran Vía 81, bajo (entrada por Mª Díaz de Haro)")
																		  			   .withNameInLang(Language.BASQUE,"Gran Vía kalea, 81, b (sarrera Mª Díaz de Haro kaletik)")),
																   ContactInfo.create()
																	 .addPhones(_buildZuzenenanPhones())
																	 .addMailAddress(_buildTrafikoaInternalMail(LOC_BILBAO_ID)),
																   COLOR);
			outLocs.add(bilbao);
			log.warn("\t\t\t[Location]: Created {}",
					 bilbao.getId());
		}
		// --- Location: Bilbao CATIT
		{
			AA14OrgDivisionServiceLocation bilbao_CATIT = _buildLocation(AA14BusinessID.TRAFIKOA,
																		 org,
																		 this.within(divisions).find(DIVISION_ID),
																		 this.within(services).find(SERVICE_ID),
																	   	 AA14OrgDivisionServiceLocationOID.supply(),LOC_BILBAO_CATIT_ID,
																	   	 "CATIT (Centro Automatizado de Tramitación de Infracciones de Tráfico)","TAIZA (Trafikoko Arau-Hausteak Izapidetzeko Zentro Automatizatua)",
																	   	 GeoPosition.create()	
																			  .withCountry(SPAIN)
																			  .withState(EUSKADI_STATE)
																			  .withCounty(BIZKAIA_COUNTY)
																			  .withMunicipality(BILBAO)
																			  .withStreet(GeoStreet.create()
																						  	   .withNameInLang(Language.SPANISH,"Calle Doctor Ornilla, 1A -Txurdinaga-")
																				  			   .withNameInLang(Language.BASQUE,"Doctor Ornilla kalea, 1A -Txurdinaga-")),
																		 ContactInfo.create()
																			 .addPhones(_buildZuzenenanPhones())
																				 .addMailAddress(_buildTrafikoaInternalMail(AA14BusinessConfigForTrafikoa.LOC_BILBAO_CATIT_ID)),
																		 COLOR);
			outLocs.add(bilbao_CATIT);
			log.warn("\t\t\t[Location]: Created {}",
					 bilbao_CATIT.getId());
		}
		// --- Location: Donostia
		{
			AA14OrgDivisionServiceLocation donostia = _buildLocation(AA14BusinessID.TRAFIKOA,
																	 org,
																	 this.within(divisions).find(DIVISION_ID),
																	 this.within(services).find(SERVICE_ID),
																   	 AA14OrgDivisionServiceLocationOID.supply(),LOC_DONOSTIA_ID,
																   	 "Tráfico Donostia-San Sebastian","Trafikoa Donostia-San Sebastian",
																   	 GeoPosition.create()
																		  .withCountry(SPAIN)
																		  .withState(EUSKADI_STATE)
																		  .withCounty(GIPUZKOA_COUNTY)
																		  .withMunicipality(DONOSTIA)
																		  .withStreet(GeoStreet.create()
																					  	   .withNameInLang(Language.SPANISH,"Avenida Vitoria-Gasteiz, 3, 3ª planta")
																			  			   .withNameInLang(Language.BASQUE,"Vitoria-Gasteiz etorbidea, 3, 3.solairua")),
																	 ContactInfo.create()
																		 .addPhones(_buildZuzenenanPhones())
																		 .addMailAddress(_buildTrafikoaInternalMail(AA14BusinessConfigForTrafikoa.LOC_DONOSTIA_ID)),
																	 COLOR);
			outLocs.add(donostia);
			log.warn("\t\t\t[Location]: Created {}",
					 donostia.getId());
		}
		// --- Location: Vitoria / Gasteiz
		{
			AA14OrgDivisionServiceLocation gasteiz = _buildLocation(AA14BusinessID.TRAFIKOA,
																	org,
																	this.within(divisions).find(DIVISION_ID),
																	this.within(services).find(SERVICE_ID),
																    AA14OrgDivisionServiceLocationOID.supply(),LOC_GASTEIZ_ID,
																    "Tráfico Vitoria-Gasteiz","Trafikoa Vitoria-Gasteiz",
																    GeoPosition.create()
																		  .withCountry(SPAIN)
																		  .withState(EUSKADI_STATE)
																		  .withCounty(ARABA_COUNTY)
																		  .withMunicipality(GASTEIZ)
																		  .withStreet(GeoStreet.create()
																					  	   .withNameInLang(Language.SPANISH,"Calle Samaniego, 2, 5ª planta")
																			  			   .withNameInLang(Language.BASQUE,"Samaniego kalea, 2, 5.solairua")),
																    ContactInfo.create()
																	 	  .addPhones(_buildZuzenenanPhones())
																						 .addMailAddress(_buildTrafikoaInternalMail(LOC_GASTEIZ_ID)),
																	COLOR);
			outLocs.add(gasteiz);
			log.warn("\t\t\t[Location]: Created {}",
					 gasteiz.getId());
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
		// --- Schedule: Bilbao
		{
			AA14Schedule bilbao_sched = _buildSchedule(AA14BusinessID.TRAFIKOA,
													   SCH_BILBAO_ID,
													   "Bilbao schedule","Bilbao schedule",
													   AA14ScheduleBookingConfig.DEF_BOOKING_CONFIG,
													   new AA14ScheduleOrchestraConfig(false,	// disable orchestra
																  					   "46",	// branch id
																		  			   "23"),	// service id
													   this.within(locs).find(LOC_BILBAO_ID));
			outSchs.add(bilbao_sched);
			log.warn("\t\t\t[Schedule]: Created {}",
					 bilbao_sched.getId());
		}
		// --- Schedule: Bilbao CATIT
		{
			AA14Schedule bilbao_CATIT_sched = _buildSchedule(AA14BusinessID.TRAFIKOA,
															 SCH_BILBAO_CATIT_ID,
															 "Bilbao CATIT schedule","Bilbao CATIT schedule",
															 AA14ScheduleBookingConfig.DEF_BOOKING_CONFIG,
															 new AA14ScheduleOrchestraConfig(false,	// disable orchestra
																		  					 "92",	// branch id
																		  					 "23"),	// service id
															 this.within(locs).find(LOC_BILBAO_CATIT_ID));
			outSchs.add(bilbao_CATIT_sched);
			log.warn("\t\t\t[Schedule]: Created {}",
					 bilbao_CATIT_sched.getId());
		}
		// --- Schedule: Donostia
		{
			AA14Schedule donostia_sched = _buildSchedule(AA14BusinessID.TRAFIKOA,
														 SCH_DONOSTIA_ID,
													     "Donostia schedule","Donostia schedule",
													 	 AA14ScheduleBookingConfig.DEF_BOOKING_CONFIG,
														 new AA14ScheduleOrchestraConfig(false,	// disable orchestra
																		  				 "47",	// branch id
																		  				 "23"),	// service id
														 this.within(locs).find(LOC_DONOSTIA_ID));
			outSchs.add(donostia_sched);
			log.warn("\t\t\t[Schedule]: Created {}",
					 donostia_sched.getId());
		}
		// --- Schedule: Vitoria / Gasteiz
		{
			AA14Schedule gasteiz_sched = _buildSchedule(AA14BusinessID.TRAFIKOA,
														SCH_GASTEIZ_ID,
													    "Gasteiz schedule","Gasteiz schedule",
													    AA14ScheduleBookingConfig.DEF_BOOKING_CONFIG,
													    new AA14ScheduleOrchestraConfig(false,	// disable orchestra
																		  			    "48",	// branch id
																		  			    "23"),	// service id
													    this.within(locs).find(AA14BusinessConfigForTrafikoa.LOC_GASTEIZ_ID));
			outSchs.add(gasteiz_sched);
			log.warn("\t\t\t[Schedule]: Created {}",
					 gasteiz_sched.getId());
		}
		return outSchs;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////

	protected static Collection<ContactMail> _buildTrafikoaInternalMail(final AA14OrgDivisionServiceLocationID locId) {
		if (locId.is(AA14BusinessConfigForTrafikoa.LOC_BILBAO_ID)) {
			return Lists.newArrayList(ContactMail.createToBeUsedFor(ContactInfoUsage.WORK)
												 .mailTo("trafikobi@trafikoa.eus"));
		} else if (locId.is(LOC_GASTEIZ_ID)) {
//			return Lists.newArrayList(ContactMail.createToBeUsedFor(ContactInfoUsage.WORK)
//												 .mailTo("trafikoar@trafikoa.eus"));
		} else if (locId.is(LOC_DONOSTIA_ID)) {
//			return Lists.newArrayList(ContactMail.createToBeUsedFor(ContactInfoUsage.WORK)
//												 .mailTo("trafikogi@trafikoa.eus"));
		} else if (locId.is(LOC_BILBAO_CATIT_ID)) {
//			return Lists.newArrayList(ContactMail.createToBeUsedFor(ContactInfoUsage.WORK)
//												 .mailTo("trafikocatit@trafikoa.eus"));
		}
		return null;
	}
}
