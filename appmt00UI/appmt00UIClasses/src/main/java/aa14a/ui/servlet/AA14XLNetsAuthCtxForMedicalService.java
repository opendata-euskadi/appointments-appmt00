package aa14a.ui.servlet;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import aa14f.model.config.business.AA14BusinessConfigForMedicalService;

/**
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
public class AA14XLNetsAuthCtxForMedicalService 
	 extends AA14XLNetsAuthCtxBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14XLNetsAuthCtxForMedicalService(final HttpServletRequest req) {
		super("medicalservice","AA14A-FN-0001",				
			  // see aa14b.xlnets.properties.xml
			  new Predicate<String>() {
					@Override
					public boolean apply(final String profileId) {
						return profileId.equals("AA14A-PF-0020");		// admin
					}
			  },
			  new Function<String,AA14UIUserScheduleData>() {
						@Override
						public AA14UIUserScheduleData apply(final String profileId) {
							AA14UIUserScheduleData outUserSchData = null;
							System.out.println(">>>> XLNets profile=" + profileId);
							
							// ---- MEDICAL SERVICE
							// admin
							if (profileId.equals("AA14A-PF-0020")) {
								outUserSchData = new AA14UIUserScheduleData(null,										// no favorite schedule
																			Lists.newArrayList(AA14BusinessConfigForMedicalService.SCH_DOCTOR_VISIT_AR_ID,
																							   AA14BusinessConfigForMedicalService.SCH_DOCTOR_VISIT_BIZ_ID,	
																							   AA14BusinessConfigForMedicalService.SCH_DOCTOR_VISIT_GI_ID,
																							   AA14BusinessConfigForMedicalService.SCH_NURSE_VISIT_AR_ID,
																							   AA14BusinessConfigForMedicalService.SCH_NURSE_VISIT_BIZ_ID,	
																							   AA14BusinessConfigForMedicalService.SCH_NURSE_VISIT_GI_ID
																							   ));	
							}
							else {
								//throw new IllegalAccessError("Unknown schedule id for xlnets profileId=" + profileId);
							}
							return outUserSchData;
						}
			  },
			  req);
	}
}
