package aa14a.ui.servlet;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import aa14f.model.config.business.AA14BusinessConfigForJustizia;
import lombok.extern.slf4j.Slf4j;

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
 * =================|====|========================================================
 * Justizia     	| 21 | Admin Justizia Escritos de inicio
 * 			     	| 22 | Justizia Escritos de inicio Bilbao
 * 			     	| 23 | Justizia Escritos de inicio Getxo
 * 			     	| 24 | Justizia Escritos de inicio Gernika-Lumo
 * 			     	| 25 | Justizia Escritos de inicio Durango
 * 			     	| 26 | Justizia Escritos de inicio Balmaseda
 * 			     	| 27 | Justizia Escritos de inicio Barakaldo
 * 			     	| 28 | Justizia Escritos de inicio Amurrio
 * 			     	| 29 | Justizia Escritos de inicio Vitoria-Gazteiz
 * 			     	| 30 | Justizia Escritos de inicio Azpeitia
 * 			     	| 31 | Justizia Escritos de inicio Bergara
 * 			     	| 32 | Justizia Escritos de inicio Irun
 * 			     	| 33 | Justizia Escritos de inicio Tolosa
 * 			     	| 34 | Justizia Escritos de inicio Donostia/San Sebastián
 * 
 * 
 * 
 */
@Slf4j
public class AA14XLNetsAuthCtxForJustizia 
	 extends AA14XLNetsAuthCtxBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14XLNetsAuthCtxForJustizia(final HttpServletRequest req) {
				super("justizia","AA14A-FN-0001",		
			  // see aa14b.xlnets.properties.xml
			  new Predicate<String>() {
					@Override
					public boolean apply(final String profileId) {
						return (profileId.equals("AA14A-PF-0021")		// admin
							|| profileId.equals("AA14A-PF-0022")		// Bilbao
							|| profileId.equals("AA14A-PF-0023")		// Getxo
							|| profileId.equals("AA14A-PF-0024")		// Gernika-Lumo
							|| profileId.equals("AA14A-PF-0025")		// Durango
							|| profileId.equals("AA14A-PF-0026")		// Balmaseda
							|| profileId.equals("AA14A-PF-0027")		// Barakaldo
							|| profileId.equals("AA14A-PF-0028")		// Amurrio
							|| profileId.equals("AA14A-PF-0029")		// Vitoria-Gazteiz
							|| profileId.equals("AA14A-PF-0030")		// Azpeitia
							|| profileId.equals("AA14A-PF-0031")		// Bergara
							|| profileId.equals("AA14A-PF-0033")		// Eibar
							|| profileId.equals("AA14A-PF-0034")		// Tolosa
							|| profileId.equals("AA14A-PF-0035"));		// Donostia/San Sebastián
					}
			  },
			  new Function<String,AA14UIUserScheduleData>() {
						@Override
						public AA14UIUserScheduleData apply(final String profileId) {
							AA14UIUserScheduleData outUserSchData = null;
							
							if (profileId.equals("AA14A-PF-0021")) {	// admin
								// no schedule, must select schedule
								return new AA14UIUserScheduleData(null,													// no favorite schedule
																  Lists.newArrayList(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_BIL_ID,
																					 AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_GTX_ID,
																					 AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_GER_ID,
																					 AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_DUR_ID,
																					 AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_BAL_ID,
																					 AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_BK_ID,
																					 AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_AM_ID,
																					 AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_VI_ID,
																					 AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_AZ_ID,
																					 AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_BER_ID,
																					 AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_IRU_ID,
																					 AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_EIB_ID,
																					 AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_TO_ID,
																					 AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_DON_ID));	
							} 
							else if (profileId.equals("AA14A-PF-0022")) { //Bilbao
								return new AA14UIUserScheduleData(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_BIL_ID,													
																  Lists.newArrayList(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_BIL_ID));
							}
							else if (profileId.equals("AA14A-PF-0023")) { //Getxo
								return new AA14UIUserScheduleData(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_GTX_ID,													
																  Lists.newArrayList(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_GTX_ID));
							}
							else if (profileId.equals("AA14A-PF-0024")) {//Gernika-Lumo
								return new AA14UIUserScheduleData(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_GER_ID,													
																  Lists.newArrayList(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_GER_ID));
							}
							else if (profileId.equals("AA14A-PF-0025")) {//Durango
								return new AA14UIUserScheduleData(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_DUR_ID,													
																  Lists.newArrayList(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_DUR_ID));
							}
							else if (profileId.equals("AA14A-PF-0026")) {//Balmaseda
								return new AA14UIUserScheduleData(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_BAL_ID,													
																  Lists.newArrayList(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_BAL_ID));
							}
							else if (profileId.equals("AA14A-PF-0027")) { //Barakaldo
								return new AA14UIUserScheduleData(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_BK_ID,													
																  Lists.newArrayList(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_BK_ID));
							}
							else if (profileId.equals("AA14A-PF-0028")) { //Amurrio
								return new AA14UIUserScheduleData(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_AM_ID,													
																  Lists.newArrayList(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_AM_ID));
							}
							else if (profileId.equals("AA14A-PF-0029")) { //Vitoria-Gazteiz
								return new AA14UIUserScheduleData(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_VI_ID,													
																  Lists.newArrayList(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_VI_ID));
							}
							else if (profileId.equals("AA14A-PF-0030")) {//Azpeitia
								return new AA14UIUserScheduleData(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_AZ_ID,													
																  Lists.newArrayList(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_AZ_ID));
							}
							else if (profileId.equals("AA14A-PF-0031")) {//Bergara
								return new AA14UIUserScheduleData(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_BER_ID,													
																  Lists.newArrayList(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_BER_ID));
							}
							else if (profileId.equals("AA14A-PF-0032")) {//Irun
								return new AA14UIUserScheduleData(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_IRU_ID,													
																  Lists.newArrayList(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_IRU_ID));
							}
							else if (profileId.equals("AA14A-PF-0033")) {//Eibar
								return new AA14UIUserScheduleData(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_EIB_ID,													
																  Lists.newArrayList(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_EIB_ID));
							}
							else if (profileId.equals("AA14A-PF-0034")) {//Tolosa
								return new AA14UIUserScheduleData(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_DON_ID,													
																  Lists.newArrayList(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_DON_ID));
							}
							else if (profileId.equals("AA14A-PF-0035")) {//Donostia/San Sebastián
								return new AA14UIUserScheduleData(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_DON_ID,													
																  Lists.newArrayList(AA14BusinessConfigForJustizia.SCH_JUSTIZIA_REGISTRO_REPARTO_DON_ID));
							}
							else {
								log.error("Unknown schedule id for xlnets profileId=" + profileId);
							}
							return outUserSchData;
						}
			  },
			  req);
	}
}
