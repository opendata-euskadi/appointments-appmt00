package aa14.batch.exporting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.google.common.io.Files;
import com.google.inject.Injector;

import aa14.batch.AA14BatchProcessBase;
import aa14f.client.api.AA14ClientAPI;
import aa14f.model.AA14Appointment;
import aa14f.model.AA14BookedSlot;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import aa14f.model.search.AA14BookedSlotFilter;
import aa14f.model.summaries.AA14SummarizedOrgHierarchy;
import lombok.extern.slf4j.Slf4j;
import r01f.locale.Language;
import r01f.types.Path;
import r01f.util.types.Dates;
import r01f.util.types.collections.CollectionUtils;

/**
 * Exports [Justizia] appointments to a CSV file (; delimited)
 * The csv have the following structure:
 * 
 * 		ORG_ID;DIVISON_ID;SRVC_ID;LOC_ID;SCH_ID;DATE;TIME;DURATION;YEAR;MONTH_OF_DNI/NIE;NAME;SURNAME;PHONE;EMAIL
 * 
 * where:
 * 		TYPE			: booked slot type
 * 		ORG_ID			: organization
 * 		DIVISION_ID		: division 
 * 		SRVC_ID			: service
 * 		LOC_ID			: location
 * 		SCH_ID			: schedule
 * 		START_DATETIME	: the dni date as yyyy/MM/dd HH:mm
 * 		END_DATETIME	: the time as HH:mm
 * 		DURATION		: duration
 * 		YEAR			: year
 * 		MONTH_OF_YEAR	: month of year
 * 		DAY_OF_MONTH	: day of month
 * 		HOUR_OF_DAY		: hour of day
 * 		MINUTE_OF_HOUR	: minute of hour
 * 		DNI				: DNI/NIE
 * 		NAME			: Name
 * 		SURNAME			: surname
 * 		PHONE			: phone
 * 		EMAIL			: email
 * 
 * Run:
 * 		JVM argument: -javaagent:d:/eclipse/local_libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true 
 */
@Slf4j
public class AA14JustiziaExporProcess 
	 extends AA14BatchProcessBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  MAIN
/////////////////////////////////////////////////////////////////////////////////////////	
	public static void main(final String[] args) {
//		AA14OrganizationID orgId = AA14BusinessConfigForJustizia.ORG_ID;
//		AA14OrgDivision diviId = AA14BusinessConfigForJustizia.JUSTIZIA_ID;
//		AA14OrgDivisionServiceID srvcId = AA14BusinessConfigForJustizia.SRV_REGISTRO_CIVIL_ID;	// SRV_REGISTRO_CIVIL_ID | SRV_SOJ_ID
//		AA14OrgDivisionServiceLocationID locId = AA14BusinessConfigForJustizia.LOC_JUSTIZIA_REGISTRO_CIVIL_BIL_ID;	// LOC_JUSTIZIA_REGISTRO_CIVIL_BIL_ID | LOC_JUSTIZIA_SOJ_BIL_ID
		AA14OrgDivisionServiceLocationID locId = AA14OrgDivisionServiceLocationID.forId("myLocId");
		
		try {
			Injector injector = _createInjector();
			AA14ClientAPI api = injector.getInstance(AA14ClientAPI.class);
			
			// import
			AA14BookedSlotFilter filter = new AA14BookedSlotFilter();			
			filter.setServiceLocationId(locId);		
			
			Path csvFilePath = Path.from("c:/develop/temp_dev/aa14/justizia-reg-civi-BI.csv");
			int exportCount = _createExportCSVFile(api,
												   csvFilePath,
												   filter);
			log.info("exported {} records",exportCount);
		} catch(Throwable th) {
			th.printStackTrace(System.out);
			log.error("Error while importing APPOINTMENT DATA: {}",
					  th.getMessage(),
					  th);	
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	private static int _createExportCSVFile(final AA14ClientAPI api,
											final Path filePath,
											final AA14BookedSlotFilter filter) throws IOException {
		Collection<AA14SlotOID> slotOids = api.bookedSlotsAPI()
											   .getForFind()
											   .findBookedSlotsBy(filter);		
		if (CollectionUtils.hasData(slotOids)) {
			File csvFile = new File(filePath.asAbsoluteString());
			BufferedWriter writer = Files.newWriter(csvFile,
													Charset.defaultCharset());
			CSVPrinter csvPrinter = new CSVPrinter(writer, 
												   CSVFormat.DEFAULT
												   			.withHeader("TYPE",
												   						"ORG_ID","DIVISION_ID","SERVICE_ID","LOCATION_ID",
												   						"SCHEDULE_ID", 
												   						"START_DATETIME","END_DATETIME",
												   						"DURATION",
												   						"YEAR","MONTH_OF_YEAR","DAY_OF_MONTH",
												   						"HOUR_OF_DAY","MINUTE_OF_HOUR",
												   						"DNI","NAME","SURNAME",
												   						"PHONE","EMAIL",
												   						"ACTOR","PROFILE"));
			for (AA14SlotOID slotOid : slotOids) {
				AA14BookedSlot slot = api.bookedSlotsAPI()
										 .getForCRUD()
										 .load(slotOid);
				AA14SummarizedOrgHierarchy orgInfo = api.configAPI()
														.getOrgHierarchyFor(slot.getOrgDivisionServiceLocationOid(),
																			Language.DEFAULT);
				AA14Schedule sch = api.schedulesAPI()
									  .getForCRUD()
									  .load(slot.getScheduleOid());
				
				log.info("export slot with oid={} of type={}",slotOid,slot.getType());
				
				AA14Appointment appointment = slot.isAppointment() ? slot.as(AA14Appointment.class) : null;				
				String dni = appointment != null
						  && appointment.getPerson() != null
						  && appointment.getPerson().getId() != null ? appointment.getPerson().getId().asString() : "";
				String name = appointment != null
						   && appointment.getPerson() != null
						   && appointment.getPerson().getName() != null ? appointment.getPerson().getName() : "";
				String surName = appointment != null
						   	  && appointment.getPerson() != null
						   	  && appointment.getPerson().getSurname() != null ? appointment.getPerson().getSurname() : "";
				String email = appointment != null 
							&& appointment.getPersonLocatorEMail() != null ? appointment.getPersonLocatorEMail().asString() : "";
				String phone = appointment != null 
							&& appointment.getPersonLocatorPhone() != null ? appointment.getPersonLocatorPhone().asString() : "";
//				AA14BusinessDataForJustizia businessData = appointment != null ? appointment.getBusinessDataAs(AA14BusinessDataForJustizia.class)
//																			   : null;
//				String actor = businessData != null ? businessData.getRequestorType() != null ? businessData.getRequestorType().name()
//																							  : ""
//													: ""; 
//				String profile = businessData != null ? businessData.getProfile() != null ? businessData.getProfile().name()
//																						  : ""
//													  : "";
				
				csvPrinter.printRecord(slot.getType(),
									   orgInfo.getOrganization().getId(),orgInfo.getDivision().getId(),orgInfo.getService().getId(),orgInfo.getLocation().getId(),
									   sch.getId(),
									   Dates.formatAsISO8601(slot.getStartDate()),Dates.formatAsISO8601(slot.getEndDate()),
									   slot.getDurationMinutes(),
									   slot.getYear(),slot.getMonthOfYear(),slot.getDayOfMonth(),
									   slot.getHourOfDay(),slot.getMinuteOfHour(),
									   dni,name,surName,
									   phone,email);
//									   actor,profile);
				csvPrinter.flush();
			}
			writer.flush();
			csvPrinter.close();
			writer.close();
		}
		return CollectionUtils.hasData(slotOids) ? slotOids.size() : 0;
	}
}
