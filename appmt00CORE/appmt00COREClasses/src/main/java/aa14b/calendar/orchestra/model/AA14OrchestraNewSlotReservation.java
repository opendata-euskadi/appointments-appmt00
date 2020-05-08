package aa14b.calendar.orchestra.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.guids.CommonOIDs.UserCode;
import r01f.types.contact.ContactInfo;
import r01f.types.contact.NIFPersonID;
import r01f.types.contact.Person;

@Accessors(prefix="_")
public class AA14OrchestraNewSlotReservation  {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@JsonProperty("title")
	@Getter @Setter private String _title;
	
	@JsonProperty("notes")
	@Getter @Setter private String _notes;
	
	@JsonProperty("customer")
	@Getter @Setter private Map<String,String> _customer;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static AA14OrchestraNewSlotReservation createFor(final String appointmentTitle,final String appointmentNotes,
													 		final Person<NIFPersonID> customer,final ContactInfo contactInfo) {
		AA14OrchestraNewSlotReservation orchestraSlotReservation = new AA14OrchestraNewSlotReservation();
		
		orchestraSlotReservation.setTitle(appointmentTitle);
		orchestraSlotReservation.setNotes(appointmentNotes);
	
		// Orchestra properties:
		//		- Identification:  	id / qpId / publicId
		// 		- Customer: 	   	identificationNumber / name / firstName / lastName / dateOfBirth
		// 		- Customer contact:	email / phone
		// 							addressLine1 / addressLine2 / addressCity / addressState / addressZip / addressCountry
		// 		- Customer Org:		externalId
		Map<String,String> customerProps = Maps.newHashMap();
		customerProps.put("identificationNumber",customer.getId().asString());
//		customerProps.put("externalId",customer.getId().asString());
		customerProps.put("firstName",customer.getName());
		customerProps.put("lastName",customer.getSurname());
		if (contactInfo != null) {
			if (contactInfo.getDefaultMailAddressOrAny() != null) customerProps.put("email",contactInfo.getDefaultMailAddressOrAny().asString());
			if (contactInfo.getDefaultPhoneOrAny() != null) 	  customerProps.put("phone",contactInfo.getDefaultPhoneOrAny().asString());
		}
		
		orchestraSlotReservation.setCustomer(customerProps);
		
		return orchestraSlotReservation;
	}
	public static AA14OrchestraNewSlotReservation createFor(final String subject,final String notes,
													 		final UserCode userCode) {
		AA14OrchestraNewSlotReservation orchestraSlotReservation = new AA14OrchestraNewSlotReservation();
		
		orchestraSlotReservation.setTitle(subject);
		orchestraSlotReservation.setNotes(notes);
	
		// Orchestra properties:
		//		- Identification:  	id / qpId / publicId
		// 		- Customer: 	   	identificationNumber / name / firstName / lastName / dateOfBirth
		// 		- Customer contact:	email / phone
		// 							addressLine1 / addressLine2 / addressCity / addressState / addressZip / addressCountry
		// 		- Customer Org:		externalId
		Map<String,String> customerProps = Maps.newHashMap();
//		customerProps.put("identificationNumber",userCode.asString());
//		customerProps.put("externalId",customer.getId().asString());
		
		orchestraSlotReservation.setCustomer(customerProps);
		
		return orchestraSlotReservation;
	}
}
