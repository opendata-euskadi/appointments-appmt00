package aa14a.ui.servlet;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import aa14f.model.config.business.AA14BusinessConfigForZuzenean;

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
 * =================|====|========================================================
 * Zuzenean 	 	| 01 | Agentes Zuzenean 012
 *           		| 02 | Agentes Zuzenean BI
 *           		| 03 | Agentes Zuzenean AR
 *           		| 04 | Agentes Zuzenean GI
 */
public class AA14XLNetsAuthCtxForZuzenean 
	 extends AA14XLNetsAuthCtxBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14XLNetsAuthCtxForZuzenean(final HttpServletRequest req) {
				super("zuzenean","AA14A-FN-0001",		
			  // see aa14b.xlnets.properties.xml
			  new Predicate<String>() {
					@Override
					public boolean apply(final String profileId) {
						return profileId.equals("AA14A-PF-0001")
							|| profileId.equals("AA14A-PF-0002")
							|| profileId.equals("AA14A-PF-0003")
							|| profileId.equals("AA14A-PF-0004");
					}
			  },
			  new Function<String,AA14UIUserScheduleData>() {
						@Override
						public AA14UIUserScheduleData apply(final String profileId) {
							AA14UIUserScheduleData outUserSchData = null;
							
							// ----- ZUZENEAN
							if (profileId.equals("AA14A-PF-0001")) {	// 012
								// 012: no schedule, must select schedule
								return new AA14UIUserScheduleData(null,													// no favorite schedule
																  Lists.newArrayList(AA14BusinessConfigForZuzenean.SCH_PRESENCIAL_AR_ID,
																		  			 AA14BusinessConfigForZuzenean.SCH_PRESENCIAL_BIZ_ID,
																		  			 AA14BusinessConfigForZuzenean.SCH_PRESENCIAL_GI_ID));	
							} else if (profileId.equals("AA14A-PF-0002")) {
								return new AA14UIUserScheduleData(AA14BusinessConfigForZuzenean.SCH_PRESENCIAL_BIZ_ID,			
																  Lists.newArrayList(AA14BusinessConfigForZuzenean.SCH_PRESENCIAL_AR_ID,
																		  			 AA14BusinessConfigForZuzenean.SCH_PRESENCIAL_BIZ_ID,
																		  			 AA14BusinessConfigForZuzenean.SCH_PRESENCIAL_GI_ID));
							} else if (profileId.equals("AA14A-PF-0003")) {
								return new AA14UIUserScheduleData(AA14BusinessConfigForZuzenean.SCH_PRESENCIAL_AR_ID,			
																  Lists.newArrayList(AA14BusinessConfigForZuzenean.SCH_PRESENCIAL_AR_ID,
																		  			 AA14BusinessConfigForZuzenean.SCH_PRESENCIAL_BIZ_ID,
																		  			 AA14BusinessConfigForZuzenean.SCH_PRESENCIAL_GI_ID));
							} else if (profileId.equals("AA14A-PF-0004")) {
								return new AA14UIUserScheduleData(AA14BusinessConfigForZuzenean.SCH_PRESENCIAL_GI_ID,			
																  Lists.newArrayList(AA14BusinessConfigForZuzenean.SCH_PRESENCIAL_AR_ID,
																		  			 AA14BusinessConfigForZuzenean.SCH_PRESENCIAL_BIZ_ID,
																		  			 AA14BusinessConfigForZuzenean.SCH_PRESENCIAL_GI_ID));
							} else {
								//throw new IllegalAccessError("Unknown schedule id for xlnets profileId=" + profileId);
							}
							return outUserSchData;
						}
			  },
			  req);
	}
}
