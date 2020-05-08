package aa14b.calendar.orchestra.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import aa14b.calendar.orchestra.model.AA14OrchestraIDs.AA14OrchestraAppointmentID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.types.contact.ContactInfo;
import r01f.types.contact.NIFPersonID;
import r01f.types.contact.Person;

@JsonIgnoreProperties({"resource","custom"})
@Accessors(prefix="_")
public class AA14OrchestraBookedSlot 
	 extends AA14OrchestraEntityBase<AA14OrchestraAppointmentID> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@JsonProperty("title")
	@Getter @Setter private String _title;
	
	@JsonProperty("notes")
	@Getter @Setter private String _notes;
	
	@JsonProperty("allDay")	
	@Getter @Setter private boolean _allDay;
	
	@JsonProperty("start")	
	@Getter @Setter private Date _startDate;
	
	@JsonProperty("end")
	@Getter @Setter private Date _endDate;
	
	@JsonProperty("status")
	@Getter @Setter private int _status;
	
	@JsonProperty("customers")
	@Getter @Setter private List<AA14OrchestraCustomer> _customers;
	
	@JsonProperty("branch")
	@Getter @Setter AA14OrchestraBranch _branch;
	
	@JsonProperty("services")
	@Getter @Setter private List<AA14OrchestraBranchService> _branchServices;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static AA14OrchestraBookedSlot createFor(final String appointmentTitle,final String appointmentNotes,
													 final Person<NIFPersonID> customer,final ContactInfo contactInfo) {
		AA14OrchestraBookedSlot orchestraAppointment = new AA14OrchestraBookedSlot();
		
		orchestraAppointment.setTitle(appointmentTitle);
		orchestraAppointment.setNotes(appointmentNotes);
	
		orchestraAppointment.setCustomers(Lists.newArrayList(AA14OrchestraCustomer.createFrom(customer,
																							  contactInfo)));
		return orchestraAppointment;
	}
}
