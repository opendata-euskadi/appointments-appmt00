package aa14b.calendar.orchestra.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import aa14f.model.oids.AA14IDs.AA14SlotID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;

@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class AA14OrchestraIDs {
/////////////////////////////////////////////////////////////////////////////////////////
// 	BASES
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface AA14OrchestraID
					extends OIDTyped<String> {
		// just a marker interface
	}
	@Immutable
	private static abstract class AA14OrchestraIDBase
	              		  extends OIDBaseMutable<String> 	// normally this should extend OIDBaseImutable BUT it MUST have a default no-args constructor to be serializable
					   implements AA14OrchestraID {
		private static final long serialVersionUID = -2265379958676173576L;
		public AA14OrchestraIDBase() {
			/* default no args constructor for serialization purposes */
		}
		public AA14OrchestraIDBase(final String id) {
			super(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	BRANCH / SERVICE IDs
/////////////////////////////////////////////////////////////////////////////////////////	
	@Immutable
	@NoArgsConstructor
	public static class AA14OrchestraBranchID
			    extends AA14OrchestraIDBase {
		private static final long serialVersionUID = -3446323324999246594L;
		
		@JsonCreator
		public AA14OrchestraBranchID(final String oid) {
			super(oid);
		}
		public static AA14OrchestraBranchID valueOf(final String s) {
			return AA14OrchestraBranchID.forId(s);
		}
		public static AA14OrchestraBranchID forId(final String id) {
			return new AA14OrchestraBranchID(id);
		}
	}
	@Immutable
	@NoArgsConstructor
	public static class AA14OrchestraBranchServiceID
			    extends AA14OrchestraIDBase {
		private static final long serialVersionUID = -8068750408906142459L;
		
		@JsonCreator
		public AA14OrchestraBranchServiceID(final String oid) {
			super(oid);
		}
		public static AA14OrchestraBranchServiceID valueOf(final String s) {
			return AA14OrchestraBranchServiceID.forId(s);
		}
		public static AA14OrchestraBranchServiceID forId(final String id) {
			return new AA14OrchestraBranchServiceID(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	APPOINTMENT / CUSTOMER IDs
/////////////////////////////////////////////////////////////////////////////////////////	
	@Immutable
	@NoArgsConstructor
	public static class AA14OrchestraAppointmentID
			    extends AA14OrchestraIDBase {
		private static final long serialVersionUID = 5204129633787622422L;
		
		@JsonCreator
		public AA14OrchestraAppointmentID(final String oid) {
			super(oid);
		}
		public AA14SlotID toAppointmentId() {
			return AA14SlotID.forId(this.getId());
		}
		public static AA14OrchestraAppointmentID valueOf(final String s) {
			return AA14OrchestraAppointmentID.forId(s);
		}
		public static AA14OrchestraAppointmentID forId(final String id) {
			return new AA14OrchestraAppointmentID(id);
		}
	}
	@Immutable
	@NoArgsConstructor
	public static class AA14OrchestraCustomerID
			    extends AA14OrchestraIDBase {
		private static final long serialVersionUID = -472782489012960252L;

		@JsonCreator
		public AA14OrchestraCustomerID(final String oid) {
			super(oid);
		}
		public static AA14OrchestraCustomerID valueOf(final String s) {
			return AA14OrchestraCustomerID.forId(s);
		}
		public static AA14OrchestraCustomerID forId(final String id) {
			return new AA14OrchestraCustomerID(id);
		}
	}
}
