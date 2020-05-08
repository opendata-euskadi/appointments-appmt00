package aa14.test;

import com.google.common.collect.Iterables;

import aa14f.common.internal.AA14AppCodes;
import aa14f.model.config.AA14OrganizationalModelObjectRef;
import aa14f.model.config.AA14Schedule;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;

/**
 * jvm arguments: -javaagent:c:/develop/local-libs/aspectj/lib/aspectjweaver.jar -Daj.weaving.verbose=true -DR01_Home=c:/develop
 */
public class AA14MiscTest {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static void main(final String[] args) {
		Marshaller m = MarshallerBuilder.findTypesToMarshallAt(AA14AppCodes.API_APPCODE)
										.build();
		AA14Schedule sch = m.forReading()
							.fromXml(_buildXml(),AA14Schedule.class);
		AA14OrganizationalModelObjectRef<?,?> ref = Iterables.getFirst(sch.getServiceLocationsRefs(),null);
		System.out.println("=========>" + ref.getOid() + " > " + ref.getId());
		
		AA14Schedule other = _buildSchedule();
		String xml = m.forWriting()
					  .toXml(other);
		System.out.println("=========>" + xml);
		other = m.forReading()
				 .fromXml(xml,AA14Schedule.class);
		ref = Iterables.getFirst(other.getServiceLocationsRefs(),null);
		System.out.println("=========>" + ref.getOid() + " > " + ref.getId());
	}
	private static String _buildXml() {
		String xml = "<schedule id='SCH_BIZILAGUN_COMUNIDADES_ALQUILERES_AR' oid='aa14pdh15c8175c3491b9d86de43cb5c9c6e31b2bb'>" +
					 	"<bookingConfig minBookableHour='08:30:000' maxBookableHour='14:00:000' slotDefaultLengthMinutes='20' maxAppointmentsInSlot='2'/>" + 
					 	"<name>" +
					 		"<SPANISH>Consultas sobre Comunidades y alquileres SCHEDULE (AR)</SPANISH>" + 
					 		"<BASQUE>Komunitateak eta alokairuak SCHEDULE (AR)</BASQUE>" +
					 	"</name>" +
					 	"<locations>" +
					 		"<orgModelObjectRef>" +
					 			"<orgDivisionServiceLocationOid oid='aa14pdz15c8175c1fb1b9d86de6f3310b59039d3e3'/>" + 
					 			"<orgDivisionServiceLocationId oid='BIZILAGUN_COMUNIDADES_ALQUILERES_AR'/>" +
					 			"</orgModelObjectRef>" +
					 	"</locations>" +
					 "</schedule>";
		return xml;
	}
	private static AA14Schedule _buildSchedule() {
		AA14Schedule sch = new AA14Schedule();
		AA14OrgDivisionServiceLocationOID oid = AA14OrgDivisionServiceLocationOID.forId("theOid");
		AA14OrgDivisionServiceLocationID id = AA14OrgDivisionServiceLocationID.forId("theId");
		sch.addServiceLocationRef(new AA14OrganizationalModelObjectRef<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID>(oid,id));
		return sch;
	}
}
