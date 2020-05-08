package aa14f.client.api.sub.delegates;

import java.util.Date;

import org.joda.time.DateTime;

import com.google.inject.Provider;

import aa14f.api.interfaces.AA14CRUDServicesForBookedSlot;
import aa14f.model.AA14BookedSlot;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.oids.AA14OIDs.AA14PeriodicSlotSerieOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.persistence.CRUDOnMultipleResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;

public class AA14ClientAPIDelegateForBookedSlotCRUDServices
	 extends AA14ClientAPIDelegateForCRUDServicesBase<AA14SlotOID,AA14SlotID,AA14BookedSlot> {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ClientAPIDelegateForBookedSlotCRUDServices(final Provider<SecurityContext> securityContextProvider,
														  final Marshaller modelObjectsMarshaller,
														  final AA14CRUDServicesForBookedSlot crudServicesProxy) {
		super(securityContextProvider,
			  modelObjectsMarshaller,
			  crudServicesProxy);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates some periodic non-bookable slots
	 * @param schOid
	 * @param startDate
	 * @param endDate
	 * @param timeStartNonBookable
	 * @param timeEndNonBookable
	 * @param monday
	 * @param tuesday
	 * @param wednesday
	 * @param thursday
	 * @param friday
	 * @param nonBookableSubject
	 * @param userCode
	 * @return
	 */
	public int createPeriodicNonBookableSlots(final AA14ScheduleOID schOid,
							  				  final Date startDate,final Date endDate,
							  				  final DateTime timeStartNonBookable,final DateTime timeEndNonBookable,
							  				  final boolean sunday,final boolean monday,final boolean tuesday,final boolean wednesday,final boolean thursday,final boolean friday,final boolean saturday,
							  				  final String nonBookableSubject,
							  				  final UserCode userCode) {
		CRUDOnMultipleResult<AA14BookedSlot> multipleCRUDResult = this.getServiceProxyAs(AA14CRUDServicesForBookedSlot.class)
																			.createPeriodicNonBookableSlots(this.getSecurityContext(),
																											schOid,
																											startDate,endDate,
																											timeStartNonBookable,timeEndNonBookable,
																											sunday,monday,tuesday,wednesday,thursday,friday,saturday,
																											nonBookableSubject,
																											userCode);
		return multipleCRUDResult.getOrThrow().size();
	}
	/**
	 * Deletes some related periodic non-bookable slots
	 * @param serieOid
	 * @return
	 */
	public int deletePeriodicNonBookableSlots(final AA14PeriodicSlotSerieOID serieOid) {
		CRUDOnMultipleResult<AA14BookedSlot> multipleCRUDResult = this.getServiceProxyAs(AA14CRUDServicesForBookedSlot.class)
																					.deletePeriodicNonBookableSlots(this.getSecurityContext(),
																													serieOid);
		return multipleCRUDResult.getOrThrow().size();
	}
}
