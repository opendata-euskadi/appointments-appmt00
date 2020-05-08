package aa14f.model.oids;

import aa14f.common.internal.AA14;
import aa14f.model.config.AA14OrgDivisionServiceLocation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.CanBeScheduledOID;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;
import r01f.guids.PersistableObjectOID;
import r01f.guids.SuppliesOID;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.FactoryFrom;

/**
 * Appointments service identifiers definitions.
 */
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class AA14OIDs {
/////////////////////////////////////////////////////////////////////////////////////////
//	OIDs
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface AA14ModelObjectOID
					extends OIDTyped<String>,
							SuppliesOID,
							PersistableObjectOID {
		/* a marker interface */
	}
	@Immutable
	private static abstract class AA14ModelObjectOIDBase
	              		  extends OIDBaseMutable<String> 	// normally this should extend OIDBaseImutable BUT it MUST have a default no-args constructor to be serializable
					   implements AA14ModelObjectOID {
		private static final long serialVersionUID = -9164578219787647708L;
		public AA14ModelObjectOIDBase() {
			/* default no args constructor for serialization purposes */
		}
		public AA14ModelObjectOIDBase(final String id) {
			super(id);
		}
		/**
		 * Generates an oid
		 * @return the generated oid
		 */
		protected static String supplyId() {
			return AA14.generateGUID();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  ORGANIZATION / DIVISION / SERVICE 
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="orgOid")
	@NoArgsConstructor
	public static final class AA14OrganizationOID
					  extends AA14ModelObjectOIDBase {
		private static final long serialVersionUID = 4985595053081655476L;
		
		public AA14OrganizationOID(final String oid) {
			super(oid);
		}
		public static AA14OrganizationOID valueOf(final String s) {
			return AA14OrganizationOID.forId(s);
		}
		public static AA14OrganizationOID fromString(final String s) {
			return AA14OrganizationOID.forId(s);
		}
		public static AA14OrganizationOID forId(final String id) {
			return new AA14OrganizationOID(id);
		}
		public static AA14OrganizationOID supply() {
			return AA14OrganizationOID.forId(AA14ModelObjectOIDBase.supplyId());
		}
	}
	@Immutable
	@MarshallType(as="orgDivisionOid")
	@NoArgsConstructor
	public static final class AA14OrgDivisionOID
					  extends AA14ModelObjectOIDBase {
		private static final long serialVersionUID = -4480202236021085670L;
		
		public AA14OrgDivisionOID(final String oid) {
			super(oid);
		}
		public static AA14OrgDivisionOID valueOf(final String s) {
			return AA14OrgDivisionOID.forId(s);
		}
		public static AA14OrgDivisionOID fromString(final String s) {
			return AA14OrgDivisionOID.forId(s);
		}
		public static AA14OrgDivisionOID forId(final String id) {
			return new AA14OrgDivisionOID(id);
		}
		public static AA14OrgDivisionOID supply() {
			return AA14OrgDivisionOID.forId(AA14ModelObjectOIDBase.supplyId());
		}
	}
	@Immutable
	@MarshallType(as="orgDivisionServiceOid")
	@NoArgsConstructor
	public static final class AA14OrgDivisionServiceOID
					  extends AA14ModelObjectOIDBase {
		private static final long serialVersionUID = 7791733828828520678L;
		
		public AA14OrgDivisionServiceOID(final String oid) {
			super(oid);
		}
		public static AA14OrgDivisionServiceOID valueOf(final String s) {
			return AA14OrgDivisionServiceOID.forId(s);
		}
		public static AA14OrgDivisionServiceOID fromString(final String s) {
			return AA14OrgDivisionServiceOID.forId(s);
		}
		public static AA14OrgDivisionServiceOID forId(final String id) {
			return new AA14OrgDivisionServiceOID(id);
		}
		public static AA14OrgDivisionServiceOID supply() {
			return AA14OrgDivisionServiceOID.forId(AA14ModelObjectOIDBase.supplyId());
		}
	}
	@Immutable
	@MarshallType(as="orgDivisionServiceLocationOid")
	@NoArgsConstructor
	public static final class AA14OrgDivisionServiceLocationOID
					  extends AA14ModelObjectOIDBase 
			 implements CanBeScheduledOID {
		private static final long serialVersionUID = -4723392764569241773L;
		
		public AA14OrgDivisionServiceLocationOID(final String oid) {
			super(oid);
		}
		public static AA14OrgDivisionServiceLocationOID valueOf(final String s) {
			return AA14OrgDivisionServiceLocationOID.forId(s);
		}
		public static AA14OrgDivisionServiceLocationOID fromString(final String s) {
			return AA14OrgDivisionServiceLocationOID.forId(s);
		}
		public static AA14OrgDivisionServiceLocationOID forId(final String id) {
			return new AA14OrgDivisionServiceLocationOID(id);
		}
		public static AA14OrgDivisionServiceLocationOID supply() {
			return AA14OrgDivisionServiceLocationOID.forId(AA14ModelObjectOIDBase.supplyId());
		}
		@Override
		public Class<?> getScheduleableObjectType() {
			return AA14OrgDivisionServiceLocation.class;
		}
		public static FactoryFrom<String,AA14OrgDivisionServiceLocationOID> FACTORY = new FactoryFrom<String,AA14OrgDivisionServiceLocationOID>() {
																							@Override
																							public AA14OrgDivisionServiceLocationOID from(final String id) {
																								return AA14OrgDivisionServiceLocationOID.forId(id);
																							}
																					  };
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  APPOINTMENT
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="scheduleOid")
	@NoArgsConstructor
	public static final class AA14ScheduleOID
					  extends AA14ModelObjectOIDBase {
		private static final long serialVersionUID = 6886354897894941817L;
		public AA14ScheduleOID(final String oid) {
			super(oid);
		}
		public static AA14ScheduleOID valueOf(final String s) {
			return AA14ScheduleOID.forId(s);
		}
		public static AA14ScheduleOID fromString(final String s) {
			return AA14ScheduleOID.forId(s);
		}
		public static AA14ScheduleOID forId(final String id) {
			return new AA14ScheduleOID(id);
		}
		public static AA14ScheduleOID supply() {
			return AA14ScheduleOID.forId(AA14ModelObjectOIDBase.supplyId());
		}
	}
	/**
	 * OID for an slot
	 */
	@Immutable
	@MarshallType(as="slotOid")
	@NoArgsConstructor
	public static final class AA14SlotOID
					  extends AA14ModelObjectOIDBase {

		private static final long serialVersionUID = -6174381858416155366L;
		public AA14SlotOID(final String oid) {
			super(oid);
		}
		public static AA14SlotOID valueOf(final String s) {
			return AA14SlotOID.forId(s);
		}
		public static AA14SlotOID fromString(final String s) {
			return AA14SlotOID.forId(s);
		}
		public static AA14SlotOID forId(final String id) {
			return new AA14SlotOID(id);
		}
		public static AA14SlotOID supply() {
			return AA14SlotOID.forId(AA14ModelObjectOIDBase.supplyId());
		}
	}
	/**
	 * When a booked slot is a periodic one, all the individual slots
	 * are related by a serialOid
	 */
	@Immutable
	@MarshallType(as="periodicSlotSerieOid")
	@NoArgsConstructor
	public static final class AA14PeriodicSlotSerieOID
					  extends AA14ModelObjectOIDBase {
		private static final long serialVersionUID = -3605229847619599355L;
		public AA14PeriodicSlotSerieOID(final String oid) {
			super(oid);
		}
		public static AA14PeriodicSlotSerieOID valueOf(final String s) {
			return AA14PeriodicSlotSerieOID.forId(s);
		}
		public static AA14PeriodicSlotSerieOID fromString(final String s) {
			return AA14PeriodicSlotSerieOID.forId(s);
		}
		public static AA14PeriodicSlotSerieOID forId(final String id) {
			return new AA14PeriodicSlotSerieOID(id);
		}
		public static AA14PeriodicSlotSerieOID supply() {
			return AA14PeriodicSlotSerieOID.forId(AA14ModelObjectOIDBase.supplyId());
		}
	}
	/**
	 * OID for an appointment reason (what for)
	 */
	@Immutable
	@MarshallType(as="appointmentSubjectOid")
	@NoArgsConstructor
	public static final class AA14AppointmentSubjectID
					  extends AA14ModelObjectOIDBase {
		
		private static final long serialVersionUID = 7765956631090750067L;
		public AA14AppointmentSubjectID(final String oid) {
			super(oid);
		}
		public static AA14AppointmentSubjectID valueOf(final String s) {
			return AA14AppointmentSubjectID.forId(s);
		}
		public static AA14AppointmentSubjectID fromString(final String s) {
			return AA14AppointmentSubjectID.forId(s);
		}
		public static AA14AppointmentSubjectID forId(final String id) {
			return new AA14AppointmentSubjectID(id);
		}
		public static AA14AppointmentSubjectID supply() {
			return AA14AppointmentSubjectID.forId(AA14ModelObjectOIDBase.supplyId());
		}
	}
}
