 package aa14.batch.importing;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.inject.Injector;

import aa14.batch.AA14BatchProcessBase;
import aa14f.client.api.AA14ClientAPI;
import aa14f.model.config.AA14OrgDivision;
import aa14f.model.config.AA14OrgDivisionService;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.config.AA14OrgDivisionServiceLocationPresentationConfig;
import aa14f.model.config.AA14Organization;
import aa14f.model.config.AA14OrganizationalModelObjectRef;
import aa14f.model.config.business.AA14BusinessConfig;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.locale.LanguageTexts.LangTextNotFoundBehabior;
import r01f.locale.LanguageTextsMapBacked;
import r01f.types.Color;
import r01f.types.Path;
import r01f.types.contact.ContactInfo;
import r01f.types.contact.ContactInfoUsage;
import r01f.types.contact.ContactMail;
import r01f.types.contact.ContactPhone;
import r01f.types.geo.GeoCountry;
import r01f.types.geo.GeoCounty;
import r01f.types.geo.GeoMunicipality;
import r01f.types.geo.GeoOIDs.GeoCountyID;
import r01f.types.geo.GeoOIDs.GeoZipCode;
import r01f.types.geo.GeoPosition;
import r01f.types.geo.GeoState;
import r01f.types.geo.GeoStreet;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * Imports locations (with all its hiearchy) from a CSV file (; delimited)
 * The csv file MUST have the following structure:
 * 
 * 		BUSINESS_ID;ORG_ID;ORG_NAME_ES;ORG_NAME_EU;DIVISION_ID;DIVISION_NAME_ES;DIVISION_NAME_EU;SERVICE_ID;SERVICE_NAME_ES;SERVICE_NAME_EU;LOCATION_ID;LOCATION_NAME_ES;LOCATION_NAME_EU;STREET_ES;STREET_EU;ZIPCODE;MUNICIPALITY_ES;MUNICIPALITY_EU;COUNTY_OID;COUNTY_ES;COUNTY_EU;STATE_ES;STATE_EU;COUNTRY_ES;COUNTRY_EU;PHONE;E-MAIL;COLOR;
 *
 * Make sure the file has a suitable encoding (i.e: ISO-8859-1)
 * Configuration parameters: test = true will only process the file
 * 		   					 test = false will process the file AND persists any new config 
 * Run:
 * 		JVM argument: -javaagent:{dev-home}/local_libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true 
 */
@Slf4j
public class AA14BusinessConfigLocationsImportProcess 
	 extends AA14BatchProcessBase {
	
	public static boolean test = false;
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIN
/////////////////////////////////////////////////////////////////////////////////////////	
	public static void main(final String[] args) {
		try {
			Injector injector = _createInjector();
			AA14ClientAPI api = injector.getInstance(AA14ClientAPI.class);
			
			log.warn("Process is {}!!", (test? "SIMULATED (will not insert anything)" : "NOT SIMULATED. CHANGES will be MADE"));
			// import
			//Path csvFilePath = Path.from("/home/develop/projects_aa14/aa14b/aa14bDocs/test_data/2020_aa14_justizia_servicioInformacion_config.csv");
			Path csvFilePath = Path.from("c:/develop/projects/legacy/aa14/aa14bDocs/test_data/2020_aa14_justizia_servicioInformacion_config.csv");
			Collection<AA14BusinessConfig> createdData = _createBusinessConfigFromCSVFile(api,
																						 csvFilePath);
			log.info("{} lines procesed!", createdData.size());
		} catch(Throwable th) {
			th.printStackTrace(System.out);
			log.error("Error while importing CONFIG DATA: {}",
					  th.getMessage(),
					  th);	
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private static Collection<AA14BusinessConfig> _createBusinessConfigFromCSVFile(final AA14ClientAPI api,
																			  	   final Path filePath) throws IOException {
		log.info("[Business config import] file {}",
				 filePath.asAbsoluteString());
		
		// [1] - Load data from the CSV file
		Collection<AA14BusinessConfigCSVData> businessConfigAtCSV = _loadBusinessConfigFromCSVFile(filePath);
		
		log.info("...{} records will be imported",
				 businessConfigAtCSV);
		
		// [2] - Create the appointments
		Collection<AA14BusinessConfig> outCfgs = FluentIterable.from(businessConfigAtCSV)
													// filter nulls
													.filter(new Predicate<AA14BusinessConfigCSVData>() {
																	@Override
																	public boolean apply(final AA14BusinessConfigCSVData csvData) {
																		return csvData != null;
																	}
															})
													// Transform from CSV to business config
													.transform(new Function<AA14BusinessConfigCSVData,AA14BusinessConfig>() {
																		@Override
																		public AA14BusinessConfig apply(final AA14BusinessConfigCSVData appointmentCSVData) {
																			return _loadBussinesConfigFrom(appointmentCSVData);
																		}
															   })
													// filter nulls
													.filter(Predicates.notNull())
													// Persist
													.transform(new Function<AA14BusinessConfig,AA14BusinessConfig>() {
																		@Override
																		public AA14BusinessConfig apply(final AA14BusinessConfig businessConfig) {
																			if (test) return businessConfig;
																			return _saveBusinessConfig(api,
																									   businessConfig);
																		}
															   })
													.toList();
		return outCfgs;
	}
	@SuppressWarnings("resource")
	private static Collection<AA14BusinessConfigCSVData> _loadBusinessConfigFromCSVFile(final Path filePath) throws IOException {
		@Cleanup 
		Reader in = Files.newReader(new File(filePath.asAbsoluteString()),
									Charset.defaultCharset());
		Iterable<CSVRecord> csvRecords = CSVFormat.RFC4180
											   .withDelimiter(';')
											   .withHeader()
											   .parse(in);
		return FluentIterable.from(csvRecords)
					 .transform(new Function<CSVRecord,AA14BusinessConfigCSVData>() {
										@Override
										public AA14BusinessConfigCSVData apply(final CSVRecord csvRecord) {
											AA14CSVRecordWrapper record = new AA14CSVRecordWrapper(csvRecord);
											AA14BusinessConfigCSVData outCSVData = null;
											try {
												String businessId = record.get("BUSINESS_ID");
												String orgId = record.get("ORG_ID");
												String orgNameEs = record.get("ORG_NAME_ES");
												String orgNameEu = record.get("ORG_NAME_EU");
												String divisionId = record.get("DIVISION_ID");
												String divisionNameEs = record.get("DIVISION_NAME_ES");
												String divisionNameEu = record.get("DIVISION_NAME_EU");
												String serviceId = record.get("SERVICE_ID");
												String serviceNameEs = record.get("SERVICE_NAME_ES");
												String serviceNameEu = record.get("SERVICE_NAME_EU");
												String locationId = record.get("LOCATION_ID");
												String locationNameEs = record.get("LOCATION_NAME_ES");
												String locationNameEu = record.get("LOCATION_NAME_EU");
												String streetEs = record.get("STREET_ES");
												String streetEu = record.get("STREET_EU");
												String zipcode = record.get("ZIPCODE");
												String municipalityEs = record.get("MUNICIPALITY_ES");
												String municipalityEu = record.get("MUNICIPALITY_EU");
												String countyOid = record.get("COUNTY_OID");
												String countyEs = record.get("COUNTY_ES");
												String countyEu = record.get("COUNTY_EU");
												String stateEs = record.get("STATE_ES");
												String stateEu = record.get("STATE_EU");
												String countryEs = record.get("COUNTRY_ES");
												String countryEu = record.get("COUNTRY_EU");
												String phone = record.get("PHONE");
												String email = record.get("E-MAIL");
											 	String color = record.get("COLOR");
											
											    outCSVData = new AA14BusinessConfigCSVData(businessId, 
											    									       orgId, orgNameEs, orgNameEu, 
											    									       divisionId, divisionNameEs, divisionNameEu, 
											    									       serviceId, serviceNameEs, serviceNameEu, 
											    									       locationId, locationNameEs, locationNameEu, streetEs, streetEu, zipcode, municipalityEs, municipalityEu, Integer.valueOf(countyOid), countyEs, countyEu, stateEs, stateEu, countryEs, countryEu, phone, email, color);
											} catch(Throwable th) {
												th.printStackTrace(System.out);
												log.error("Erroneous csv record: {}",th.getMessage(),th);
											}
											return outCSVData;
										}
					 			})
					 .toList();
	}
	private static AA14BusinessConfig _loadBussinesConfigFrom(final AA14BusinessConfigCSVData businessConfigCSVData) {
		if (!businessConfigCSVData.isValid()) {
			log.info("\tNOT VALID RECORD!!");
			return null;
		}
		
		AA14BusinessConfig outCfg = new AA14BusinessConfig();
		outCfg.setId(AA14BusinessID.fromString(businessConfigCSVData.getBusinessId()));
		outCfg.setDivisions(Sets.newHashSet());
		outCfg.setServices(Sets.newHashSet());
		outCfg.setLocations(Sets.newHashSet());
		
		// Organization
		AA14Organization org = new AA14Organization();
		org.setBusinessId(outCfg.getId());
		org.setId(AA14OrganizationID.fromString(businessConfigCSVData.getOrgId()));
		org.setNameByLanguage(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
										   .add(Language.SPANISH,businessConfigCSVData.getOrgNameEs())
										   .add(Language.BASQUE,businessConfigCSVData.getOrgNameEu()));	
		outCfg.setOrganization(org);
		
		// Division
		AA14OrgDivision div = new AA14OrgDivision();
		div.setOid(AA14OrgDivisionOID.supply());
		div.setId(AA14OrgDivisionID.fromString(businessConfigCSVData.getDivisionId()));
		div.setBusinessId(outCfg.getId());
		div.setOrgRef(new AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID>(outCfg.getOrganization().getOid(),
																								   outCfg.getOrganization().getId()));
		div.setNameByLanguage(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
									   .add(Language.SPANISH,businessConfigCSVData.getOrgNameEs())
									   .add(Language.BASQUE,businessConfigCSVData.getOrgNameEu()));		
		outCfg.getDivisions()
			  .add(div);
		
		// Service
		AA14OrgDivisionService srvc = new AA14OrgDivisionService();
		srvc.setBusinessId(outCfg.getId());
		srvc.setId(AA14OrgDivisionServiceID.fromString(businessConfigCSVData.getServiceId()));
		srvc.setOrgRef(new AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID>(outCfg.getOrganization().getOid(),
																									outCfg.getOrganization().getId()));
		srvc.setOrgDivisionRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionOID,AA14OrgDivisionID>(div.getOid(),
																										  div.getId()));
		srvc.setNameByLanguage(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
									   .add(Language.SPANISH,businessConfigCSVData.getServiceNameEs())
									   .add(Language.BASQUE,businessConfigCSVData.getServiceNameEu()));		
		outCfg.getServices()
			  .add(srvc);
		
		// Location 
		AA14OrgDivisionServiceLocation loc = new AA14OrgDivisionServiceLocation();
		loc.setBusinessId(outCfg.getId());
		loc.setId(AA14OrgDivisionServiceLocationID.fromString(businessConfigCSVData.getLocationId()));
		loc.setOrgRef(new AA14OrganizationalModelObjectRef<AA14OrganizationOID,AA14OrganizationID>(outCfg.getOrganization().getOid(),
																								   outCfg.getOrganization().getId()));
		loc.setOrgDivisionRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionOID,AA14OrgDivisionID>(div.getOid(),
																										 div.getId()));
		loc.setOrgDivisionServiceRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID>(srvc.getOid(),
																															  srvc.getId()));
		loc.setNameByLanguage(new LanguageTextsMapBacked(LangTextNotFoundBehabior.RETURN_NULL)
									   .add(Language.SPANISH,businessConfigCSVData.getLocationNameEs())
									   .add(Language.BASQUE,businessConfigCSVData.getLocationNameEu()));	
		loc.setPosition(GeoPosition.create()
							  .withCountry(GeoCountry.create()
							  		 				 .withNameInLang(Language.SPANISH, businessConfigCSVData.getCountryEs())
							  		 				 .withNameInLang(Language.BASQUE, businessConfigCSVData.getCountryEu()))
							  .withState(GeoState.create()
								  		 		 .withNameInLang(Language.SPANISH,businessConfigCSVData.getStateEs())
								  		 		 .withNameInLang(Language.BASQUE, businessConfigCSVData.getStateEu()))
							  .withCounty(GeoCounty.create(GeoCountyID.forId(businessConfigCSVData.getCountyOid()))
									  		  	   .withNameInLang(Language.SPANISH, businessConfigCSVData.getCountyEs())
									  		  	   .withNameInLang(Language.BASQUE, businessConfigCSVData.getCountyEu()))
							  .withMunicipality(GeoMunicipality.create()
														  	   .withNameInLang(Language.SPANISH, businessConfigCSVData.getMunicipalityEs())
														  	   .withNameInLang(Language.BASQUE, businessConfigCSVData.getMunicipalityEu()))
							  .withStreet(GeoStreet.create()
										  	   .withNameInLang(Language.SPANISH, businessConfigCSVData.getStreetEs())
								  			   .withNameInLang(Language.BASQUE, businessConfigCSVData.getStreetEu()))
							  .withZipCode(GeoZipCode.forId(businessConfigCSVData.getZipcode())));
		
		if (Strings.isNOTNullOrEmpty(businessConfigCSVData.getPhone())) {
			ContactInfo contactInfo = loc.getContactInfo();
			if (contactInfo == null) {
				contactInfo = ContactInfo.create();
			}
			contactInfo.addPhone(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
				  							 .withNumber(businessConfigCSVData.getPhone()));
			loc.setContactInfo(contactInfo);
		}
		if (Strings.isNOTNullOrEmpty(businessConfigCSVData.getEmail())){
			ContactInfo contactInfo = loc.getContactInfo();
			if (contactInfo == null) {
				contactInfo = ContactInfo.create();
			}
			contactInfo.addMailAddress(ContactMail.createToBeUsedFor(ContactInfoUsage.WORK)
				  								  .mailTo(businessConfigCSVData.getEmail()));
			loc.setContactInfo(contactInfo);
		}
		loc.setPresentationConfig(new AA14OrgDivisionServiceLocationPresentationConfig(Color.from(businessConfigCSVData.getColor())));	
		
		outCfg.getLocations()
			  .add(loc);

		// return
		return outCfg;
	}
	private static AA14BusinessConfig _saveBusinessConfig(final AA14ClientAPI api,
													  	  final AA14BusinessConfig config) {
		AA14BusinessID businessId = config.getId();
		
		// --- data from CSV
		AA14Organization org =  config.getOrganization();
		AA14OrgDivision div = CollectionUtils.pickOneAndOnlyElement(config.getDivisions());
		AA14OrgDivisionService srvc = CollectionUtils.pickOneAndOnlyElement(config.getServices());
		AA14OrgDivisionServiceLocation loc = CollectionUtils.pickOneAndOnlyElement(config.getLocations());
		
		// --- Load previously existing recods (if present)
		AA14Organization existingOrg = api.organizationsAPI()
										  .getForCRUD()
										  .loadByIdOrNull(org.getId());
		AA14OrgDivision existingDiv = api.orgDivisionsAPI()
									     .getForCRUD()
									    .loadByIdOrNull(div.getId());
		AA14OrgDivisionService existingSrvc = api.orgDivisionServicesAPI()
												 .getForCRUD()
												 .loadByIdOrNull(srvc.getId());
		AA14OrgDivisionServiceLocation existingLoc = api.orgDivisionServiceLocationsAPI()
														.getForCRUD()
														.loadByIdOrNull(loc.getId());
		
		// --- Organization
		if (existingOrg == null) {
			AA14Organization savedOrg = api.organizationsAPI()
						  					.getForCRUD()
						  				    .save(org);
			existingOrg = savedOrg;					
			log.info("...saved org id={}",savedOrg.getId());
		}
		if (existingOrg == null) throw new IllegalStateException("org with id=" + org.getId() + " could not be found!");
		
		// --- Divisions
		if (existingDiv == null) {
			div.setOrgRef(existingOrg.getReference());	// ensure the correct org reference
			AA14OrgDivision savedDiv = api.orgDivisionsAPI()
											  .getForCRUD()
											  .save(div);
			existingDiv = savedDiv;	// beware! use the returned object (the oid might change)
			log.info("...saved div id={}",savedDiv.getId());
		}
		if (existingDiv == null) throw new IllegalStateException("division with id=" + div.getId() + " could not be found!");
		
		// --- Services
		if (existingSrvc == null) {
			srvc.setOrgRef(existingOrg.getReference());			// ensure the correct org reference
			srvc.setOrgDivisionRef(existingDiv.getReference());	// ensure the correct division reference
			AA14OrgDivisionService savedSrvc = api.orgDivisionServicesAPI()
											  		  .getForCRUD()
											  		  .save(srvc);
			existingSrvc = savedSrvc;	// beware! use the returned object (the oid might change)
			log.info("...saved srvc id={}",savedSrvc.getId());
		}
		if (existingSrvc == null) throw new IllegalStateException("service with id=" + srvc.getId() + " could not be found!");
		
		// --- Locations
		if (existingLoc == null) {
			loc.setOrgRef(existingOrg.getReference());					// ensure the correct org reference
			loc.setOrgDivisionRef(existingDiv.getReference());			// ensure the correct division reference
			loc.setOrgDivisionServiceRef(existingSrvc.getReference());	// ensure the correct service reference
			AA14OrgDivisionServiceLocation savedLoc = api.orgDivisionServiceLocationsAPI()
													  		  .getForCRUD()
													  		  .save(loc);
			existingLoc = savedLoc;		// beware! use the returned object (the oid might change)
			log.info("...saved loc id={}",savedLoc.getId());
		}
		if (existingLoc == null) throw new IllegalStateException("location with id=" + loc.getId() + " could not be found!");
		return config;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	private static class AA14BusinessConfigCSVData {
		@Getter private final String _businessId;
		@Getter private final String _orgId;
		@Getter private final String _orgNameEs;
		@Getter private final String _orgNameEu;
		@Getter private final String _divisionId;
		@Getter private final String _divisionNameEs;
		@Getter private final String _divisionNameEu;
		@Getter private final String _serviceId;
		@Getter private final String _serviceNameEs;
		@Getter private final String _serviceNameEu;
		@Getter private final String _locationId;
		@Getter private final String _locationNameEs;
		@Getter private final String _locationNameEu;
		@Getter private final String _streetEs;
		@Getter private final String _streetEu;
		@Getter private final String _zipcode;
		@Getter private final String _municipalityEs;
		@Getter private final String _municipalityEu;
		@Getter private final Integer _countyOid;
		@Getter private final String _countyEs;
		@Getter private final String _countyEu;
		@Getter private final String _stateEs;
		@Getter private final String _stateEu;
		@Getter private final String _countryEs;
		@Getter private final String _countryEu;
		@Getter private final String _phone;
		@Getter private final String _email;
		@Getter private final String _color;
		
		public boolean isValid() {
			
			return Strings.isNOTNullOrEmpty(_businessId)
				// Org
				&& Strings.isNOTNullOrEmpty(_orgId) 
				&& Strings.isNOTNullOrEmpty(_orgNameEs) 
				&& Strings.isNOTNullOrEmpty(_orgNameEu)
				// division
				&& Strings.isNOTNullOrEmpty(_divisionId)
				&& Strings.isNOTNullOrEmpty(_divisionNameEs) 
				&& Strings.isNOTNullOrEmpty(_divisionNameEu)
				// service
				&& Strings.isNOTNullOrEmpty(_serviceId) 
				&& Strings.isNOTNullOrEmpty(_serviceNameEs) 
				&& Strings.isNOTNullOrEmpty(_serviceNameEu) 
				// location
				&& Strings.isNOTNullOrEmpty(_locationId)
				&& Strings.isNOTNullOrEmpty(_locationNameEs)
				&& Strings.isNOTNullOrEmpty(_locationNameEu)
				&& Strings.isNOTNullOrEmpty(_streetEs)
				&& Strings.isNOTNullOrEmpty(_streetEu)
				&& Strings.isNOTNullOrEmpty(_zipcode) 
				&& Strings.isNOTNullOrEmpty(_municipalityEs) 
				&& Strings.isNOTNullOrEmpty(_municipalityEu) 
				&& _countyOid> 0
				&& Strings.isNOTNullOrEmpty(_countyEs) 
				&& Strings.isNOTNullOrEmpty(_countyEu) 
				&& Strings.isNOTNullOrEmpty(_stateEs) 
				&& Strings.isNOTNullOrEmpty(_stateEu) 
				&& Strings.isNOTNullOrEmpty(_countryEs) 
				&& Strings.isNOTNullOrEmpty(_countryEu);
		}
	}	
}
