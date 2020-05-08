package aa14a.ui.servlet;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import aa14f.model.config.business.AA14BusinessConfigForTrafikoa;
import r01f.util.types.collections.Lists;

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
public class AA14XLNetsAuthCtxForTrafikoa 
	 extends AA14XLNetsAuthCtxBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14XLNetsAuthCtxForTrafikoa(final HttpServletRequest req) {
		super("trafikoa","AA14A-FN-0001",		
			  // see aa14b.xlnets.properties.xml
			  new Predicate<String>() {
					@Override
					public boolean apply(final String profileId) {
						return profileId.equals("AA14A-PF-0001")
							|| profileId.equals("AA14A-PF-0002")
							|| profileId.equals("AA14A-PF-0003")
							|| profileId.equals("AA14A-PF-0004")
							|| profileId.equals("AA14A-PF-0005")
							|| profileId.equals("AA14A-PF-0006")
							|| profileId.equals("AA14A-PF-0007")
							|| profileId.equals("AA14A-PF-0008");
					}
			  },
			  new Function<String,AA14UIUserScheduleData>() {
						@Override
						public AA14UIUserScheduleData apply(final String profileId) {
							AA14UIUserScheduleData outUserSchData = null;
							//System.out.println("XLNets profile=" + profileId);
							
							// ----- ZUZENEAN
							if (profileId.equals("AA14A-PF-0001")) {	// 012
								// 012: no schedule, must select schedule
								return new AA14UIUserScheduleData(null,													// no favorite schedule
																  Lists.newArrayList(AA14BusinessConfigForTrafikoa.SCH_BILBAO_ID,
																		  			 AA14BusinessConfigForTrafikoa.SCH_GASTEIZ_ID,
																		  			 AA14BusinessConfigForTrafikoa.SCH_DONOSTIA_ID,
																		  			 AA14BusinessConfigForTrafikoa.SCH_BILBAO_CATIT_ID));	
							} else if (profileId.equals("AA14A-PF-0002")) {
								outUserSchData = new AA14UIUserScheduleData(AA14BusinessConfigForTrafikoa.SCH_BILBAO_ID,		// favorite schedule
																			Lists.newArrayList(AA14BusinessConfigForTrafikoa.SCH_BILBAO_ID,
																		  			 		   AA14BusinessConfigForTrafikoa.SCH_GASTEIZ_ID,
																		  			 		   AA14BusinessConfigForTrafikoa.SCH_DONOSTIA_ID,
																		  			 		   AA14BusinessConfigForTrafikoa.SCH_BILBAO_CATIT_ID));
							} else if (profileId.equals("AA14A-PF-0003")) {
								outUserSchData = new AA14UIUserScheduleData(AA14BusinessConfigForTrafikoa.SCH_GASTEIZ_ID,
																			Lists.newArrayList(AA14BusinessConfigForTrafikoa.SCH_BILBAO_ID,
																		  			 		   AA14BusinessConfigForTrafikoa.SCH_GASTEIZ_ID,
																		  			 		   AA14BusinessConfigForTrafikoa.SCH_DONOSTIA_ID,
																		  			 		   AA14BusinessConfigForTrafikoa.SCH_BILBAO_CATIT_ID));	
							} else if (profileId.equals("AA14A-PF-0004")) {
								outUserSchData = new AA14UIUserScheduleData(AA14BusinessConfigForTrafikoa.SCH_DONOSTIA_ID,
																			Lists.newArrayList(AA14BusinessConfigForTrafikoa.SCH_BILBAO_ID,
																		  			 		   AA14BusinessConfigForTrafikoa.SCH_GASTEIZ_ID,
																		  			 		   AA14BusinessConfigForTrafikoa.SCH_DONOSTIA_ID,
																		  			 		   AA14BusinessConfigForTrafikoa.SCH_BILBAO_CATIT_ID));	
							// ---- TRAFIKOA
							} else if (profileId.equals("AA14A-PF-0005")) {
								outUserSchData = new AA14UIUserScheduleData(AA14BusinessConfigForTrafikoa.SCH_BILBAO_CATIT_ID,
																			Lists.newArrayList(AA14BusinessConfigForTrafikoa.SCH_BILBAO_ID,
																		  			 		   AA14BusinessConfigForTrafikoa.SCH_GASTEIZ_ID,
																		  			 		   AA14BusinessConfigForTrafikoa.SCH_DONOSTIA_ID,
																		  			 		   AA14BusinessConfigForTrafikoa.SCH_BILBAO_CATIT_ID));
							} else if (profileId.equals("AA14A-PF-0006")) {
								outUserSchData = new AA14UIUserScheduleData(AA14BusinessConfigForTrafikoa.SCH_BILBAO_ID,
																			Lists.newArrayList(AA14BusinessConfigForTrafikoa.SCH_BILBAO_ID));	
							} else if (profileId.equals("AA14A-PF-0007")) {
								outUserSchData = new AA14UIUserScheduleData(AA14BusinessConfigForTrafikoa.SCH_GASTEIZ_ID,
																			Lists.newArrayList(AA14BusinessConfigForTrafikoa.SCH_GASTEIZ_ID));	
							} else if (profileId.equals("AA14A-PF-0008")) {
								outUserSchData = new AA14UIUserScheduleData(AA14BusinessConfigForTrafikoa.SCH_DONOSTIA_ID,
																			Lists.newArrayList(AA14BusinessConfigForTrafikoa.SCH_DONOSTIA_ID));
							} else {
								//throw new IllegalAccessError("Unknown schedule id for xlnets profileId=" + profileId);
							}
							return outUserSchData;
						}
			  },
			  req);
	}
}
