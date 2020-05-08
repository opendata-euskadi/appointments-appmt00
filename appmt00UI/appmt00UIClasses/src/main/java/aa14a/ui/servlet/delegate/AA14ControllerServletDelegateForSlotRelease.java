package aa14a.ui.servlet.delegate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aa14a.ui.servlet.AA14ControllerOperation;
import aa14a.ui.servlet.AA14ReqParamToType;
import aa14f.client.api.AA14ClientAPI;
import aa14f.model.AA14BookedSlot;
import aa14f.model.AA14BookedSlotType;
import aa14f.model.AA14NonBookableSlot;
import aa14f.model.oids.AA14OIDs.AA14PeriodicSlotSerieOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.servlet.HttpRequestParamsWrapper;
import r01f.util.types.Strings;

@Slf4j
@RequiredArgsConstructor
public class AA14ControllerServletDelegateForSlotRelease
	 extends AA14ControllerServletDelegateBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14ClientAPI _clientAPI;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void executeOp(final HttpServletRequest request,final HttpServletResponse response,
						  final AA14ControllerOperation op,final HttpRequestParamsWrapper reqParams) throws ServletException, 
																 											IOException {
		AA14SlotOID	slotOid = reqParams.getMandatoryParameter("slotOid")
									   .asOid(AA14SlotOID.class)
				 					   .using(AA14ReqParamToType.transform(AA14SlotOID.class));
		log.debug("[init]: Release slot with id={}----------------",
				 slotOid);		
		
		String whatToRemoveInPeriodicNonBookable = reqParams.getParameter("removePeriodicNonBookable")
																.asString()
																.orNull();
		if (Strings.isNullOrEmpty(whatToRemoveInPeriodicNonBookable)) {
			log.debug("The slot with oid={} will be deleted",
					  slotOid);
			_clientAPI.bookedSlotsAPI()
						.getForCRUD()
							.delete(slotOid);
		} else if (whatToRemoveInPeriodicNonBookable != null) {
			if (whatToRemoveInPeriodicNonBookable.equals("removeThisBookedSlot")) {
				log.debug("The slot with oid={} will be deleted",
						  slotOid);
				_clientAPI.bookedSlotsAPI()
							.getForCRUD()
								.delete(slotOid);
				
			} else if (whatToRemoveInPeriodicNonBookable.equals("removeAllSerieRelatedBookedSlots")) {
				AA14PeriodicSlotSerieOID serieOid = reqParams.getMandatoryParameter("periodicSerieOid")	
															 .asOid(AA14PeriodicSlotSerieOID.class)
									 					     .using(AA14ReqParamToType.transform(AA14PeriodicSlotSerieOID.class));
				// Ensure the serie oid is the received one
				// [a] - Load the slot
				AA14BookedSlot slot = _clientAPI.bookedSlotsAPI()
													.getForCRUD()
														.load(slotOid);
				// [b] - If it's a NON-bookable slot, check if it's a 
				//		 periodic non bookable slot, and if so, delete all the slot serie
				if (slot.getType() == AA14BookedSlotType.NON_BOOKABLE 
				&&  slot.as(AA14NonBookableSlot.class).isPeriodic()) {
					AA14PeriodicSlotSerieOID storedSerieOid = slot.as(AA14NonBookableSlot.class)
																    .getPeriodicSlotData()
																		.getSerieOid();
					if (serieOid.isNOT(storedSerieOid)) throw new IllegalArgumentException("The slot with oid=" + slot.getOid() + " belongs to a serie with oid=" + storedSerieOid + " BUT the received one is oid=" + serieOid);
					
					log.debug("The slot with oid={} is a non-bookable PERIODIC slot with serieOid={}: all serie-slots will be deleted",
							  slotOid,serieOid);
					int deleted = _clientAPI.bookedSlotsAPI()
												.getForCRUD()
													.deletePeriodicNonBookableSlots(serieOid);
					log.debug("\t...{} slots deleted",deleted);
					
				} else {
					throw new IllegalArgumentException("The slot with oid=" + slot.getOid() + " is supposed to be a non bookable periodic slot BUT it seems it's NOT");
				}
			} else {
				throw new IllegalArgumentException("The removePeriodicNonBookable does NOT hava an acceptable value");
			}
		}
		
		_returnJsonResponse(response, 
							slotOid); 
		
		log.debug("[end]: Release slot-----------------");
	}
}
