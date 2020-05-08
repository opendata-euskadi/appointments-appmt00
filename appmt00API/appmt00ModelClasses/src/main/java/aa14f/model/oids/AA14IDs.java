package aa14f.model.oids;

import java.util.Random;

import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;
import r01f.guids.OIDs;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.contact.PersonID;

@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class AA14IDs {
/////////////////////////////////////////////////////////////////////////////////////////
// 	BASES
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	public static interface AA14ModelObjectID<O extends AA14ModelObjectOID>
					extends OIDTyped<String> {
		// nothing
	}
	@Immutable
	private static abstract class AA14ModelObjectIDBase<O extends AA14ModelObjectOID>
	              		  extends OIDBaseMutable<String> 	// normally this should extend OIDBaseImutable BUT it MUST have a default no-args constructor to be serializable
					   implements AA14ModelObjectID<O> {
		private static final long serialVersionUID = -2265379958676173576L;
		public AA14ModelObjectIDBase() {
			/* default no args constructor for serialization purposes */
		}
		public AA14ModelObjectIDBase(final String id) {
			super(id);
		}
	}
	public static interface AA14ModelObjectOrgID<O extends AA14ModelObjectOID>
					extends AA14ModelObjectID<O> {
		// a marker interface
	}
	private static abstract class AA14OrgEntityIDBase<O extends AA14ModelObjectOID> 
						  extends AA14ModelObjectIDBase<O> 
				       implements AA14ModelObjectOrgID<O> {

		private static final long serialVersionUID = -3628476292215645376L;
		
		public AA14OrgEntityIDBase() {
			/* default no args constructor for serialization purposes */
		}
		public AA14OrgEntityIDBase(final String id) {
			super(id);	
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BUSINESS CONFIG
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="businessId")
	@NoArgsConstructor
	public static class AA14BusinessID
				extends OIDBaseMutable<String> {
		private static final long serialVersionUID = -6041357044981312683L;
		
		public AA14BusinessID(final String oid) {
			super(oid);
		}
		public static AA14BusinessID valueOf(final String s) {
			return AA14BusinessID.forId(s);
		}
		public static AA14BusinessID fromString(final String s) {
			return AA14BusinessID.forId(s);
		}
		public static AA14BusinessID forId(final String id) {
			return new AA14BusinessID(id);
		}
		public static final AA14BusinessID TRAFIKOA = AA14BusinessID.forId("trafikoa");
		public static final AA14BusinessID BIZILAGUN = AA14BusinessID.forId("bizilagun");
		public static final AA14BusinessID BLOOD_DONATION = AA14BusinessID.forId("blood-donation");
		public static final AA14BusinessID MEDICAL_SERVICE = AA14BusinessID.forId("medical-service");
		public static final AA14BusinessID ZUZENEAN = AA14BusinessID.forId("zuzenean");
		public static final AA14BusinessID JUSTIZIA = AA14BusinessID.forId("justizia");
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	ORGANIZATION / DIVISION / SERVICE IDs
/////////////////////////////////////////////////////////////////////////////////////////	
	@Immutable
	@MarshallType(as="orgId")
	@NoArgsConstructor
	public static class AA14OrganizationID
				extends AA14OrgEntityIDBase<AA14OrganizationOID> {
		private static final long serialVersionUID = -7353520195270919008L;
		
		public static AA14OrganizationID ANY = AA14OrganizationID.forId("ANY_ORG");
		
		public AA14OrganizationID(final String oid) {
			super(oid);
		}
		public static AA14OrganizationID valueOf(final String s) {
			return AA14OrganizationID.forId(s);
		}
		public static AA14OrganizationID fromString(final String s) {
			return AA14OrganizationID.forId(s);
		}
		public static AA14OrganizationID forId(final String id) {
			return new AA14OrganizationID(id);
		}
	}
	@Immutable
	@MarshallType(as="orgDivisionId")
	@NoArgsConstructor
	public static class AA14OrgDivisionID
				extends AA14OrgEntityIDBase<AA14OrgDivisionOID> {
		private static final long serialVersionUID = -1981220256984650494L;
		
		public static AA14OrgDivisionID ANY = AA14OrgDivisionID.forId("ANY_DIV");
		
		public AA14OrgDivisionID(final String oid) {
			super(oid);
		}
		public static AA14OrgDivisionID valueOf(final String s) {
			return AA14OrgDivisionID.forId(s);
		}
		public static AA14OrgDivisionID fromString(final String s) {
			return AA14OrgDivisionID.forId(s);
		}
		public static AA14OrgDivisionID forId(final String id) {
			return new AA14OrgDivisionID(id);
		}
	}
	@Immutable
	@MarshallType(as="orgDivisionServiceId")
	@NoArgsConstructor
	public static class AA14OrgDivisionServiceID
				extends AA14OrgEntityIDBase<AA14OrgDivisionServiceOID> {
		private static final long serialVersionUID = -2138384034742827147L;
		
		public static AA14OrgDivisionServiceID ANY = AA14OrgDivisionServiceID.forId("ANY_SRVC");
		
		public AA14OrgDivisionServiceID(final String oid) {
			super(oid);
		}
		public static AA14OrgDivisionServiceID valueOf(final String s) {
			return AA14OrgDivisionServiceID.forId(s);
		}
		public static AA14OrgDivisionServiceID fromString(final String s) {
			return AA14OrgDivisionServiceID.forId(s);
		}
		public static AA14OrgDivisionServiceID forId(final String id) {
			return new AA14OrgDivisionServiceID(id);
		}
	}
	@Immutable
	@MarshallType(as="orgDivisionServiceLocationId")
	@NoArgsConstructor
	public static class AA14OrgDivisionServiceLocationID
				extends AA14OrgEntityIDBase<AA14OrgDivisionServiceLocationOID> {
		private static final long serialVersionUID = 4189717235942560549L;
		
		public static AA14OrgDivisionServiceLocationID ANY = AA14OrgDivisionServiceLocationID.forId("ANY_LOC"); 
		
		public AA14OrgDivisionServiceLocationID(final String oid) {
			super(oid);
		}
		public static AA14OrgDivisionServiceLocationID valueOf(final String s) {
			return AA14OrgDivisionServiceLocationID.forId(s);
		}
		public static AA14OrgDivisionServiceLocationID fromString(final String s) {
			return AA14OrgDivisionServiceLocationID.forId(s);
		}
		public static AA14OrgDivisionServiceLocationID forId(final String id) {
			return new AA14OrgDivisionServiceLocationID(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  SHCHEDULE & APPOINTMENT
/////////////////////////////////////////////////////////////////////////////////////////
	@Immutable
	@MarshallType(as="scheduleId")
	@NoArgsConstructor
	public static class AA14ScheduleID
				extends AA14OrgEntityIDBase<AA14ScheduleOID> {
		private static final long serialVersionUID = 9182088464945571204L;
		public AA14ScheduleID(final String oid) {
			super(oid);
		}
		public static AA14ScheduleID valueOf(final String s) {
			return AA14ScheduleID.forId(s);
		}
		public static AA14ScheduleID fromString(final String s) {
			return AA14ScheduleID.forId(s);
		}
		public static AA14ScheduleID forId(final String id) {
			return new AA14ScheduleID(id);
		}
	}
	@Immutable
	@MarshallType(as="slotId")
	@NoArgsConstructor
	public static class AA14SlotID
				extends AA14ModelObjectIDBase<AA14SlotOID> {
		private static final long serialVersionUID = 2865831424496553976L;
		public AA14SlotID(final String oid) {
			super(oid);
		}
		public static AA14SlotID valueOf(final String s) {
			return AA14SlotID.forId(s);
		}
		public static AA14SlotID fromString(final String s) {
			return AA14SlotID.forId(s);
		}
		public static AA14SlotID forId(final String id) {
			return new AA14SlotID(id);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LOCATORS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Immutable
	@MarshallType(as="personLocatorId")
	@NoArgsConstructor
	public static class AA14PersonLocatorID
				extends OIDBaseMutable<String> {
		private static final long serialVersionUID = 9182088464945571204L;
		public AA14PersonLocatorID(final String oid) {
			super(oid);
		}
		public static AA14PersonLocatorID valueOf(final String s) {
			return AA14PersonLocatorID.forId(s);
		}
		public static AA14PersonLocatorID fromString(final String s) {
			return AA14PersonLocatorID.forId(s);
		}
		public static AA14PersonLocatorID forId(final String id) {
			return new AA14PersonLocatorID(id);
		}
		public static AA14PersonLocatorID supplyFor(final PersonID personId) {
			// supply a random like: F9DEC741-E8EB-43B4-A91E-DE9C45FDCB4C  
			String idStr = OIDs.supplyOid();
			
			// get either the first 8 digits or the last 8 digits
			Random r = new Random(System.nanoTime());
			int i = r.nextInt((2 - 1) + 1) + 1;
			int firstHipen = idStr.indexOf("-");
			int secondHipen = idStr.indexOf("-",firstHipen + 1);
			int lastHipen = idStr.lastIndexOf("-");
			String token = i == 1 ? idStr.substring(0,firstHipen) + (idStr.substring(firstHipen+1,secondHipen))
								  : idStr.substring(lastHipen + 1);
			return AA14PersonLocatorID.forId(personId + "-" + token);
		}
	}
}

