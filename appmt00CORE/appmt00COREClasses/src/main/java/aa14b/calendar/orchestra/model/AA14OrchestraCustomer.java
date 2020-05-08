package aa14b.calendar.orchestra.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import aa14b.calendar.orchestra.model.AA14OrchestraIDs.AA14OrchestraCustomerID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.types.contact.ContactInfo;
import r01f.types.contact.NIFPersonID;
import r01f.types.contact.Person;

@JsonIgnoreProperties({"custom"})
@Accessors(prefix="_")
public class AA14OrchestraCustomer
	 extends AA14OrchestraLocalizableEntityBase<AA14OrchestraCustomerID> {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@JsonProperty("identificationNumber")
	@Getter @Setter private String _identificationNumber;
	
	@JsonProperty("externalId")
	@Getter @Setter private NIFPersonID _nif;
	
	@JsonProperty("firstName")
	@Getter @Setter private String _firstName;
	
	@JsonProperty("lastName")
	@Getter @Setter private String _lastName;
	
	@JsonProperty("dateOfBirth")
	@Getter @Setter private Date _dateOfBirth;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static AA14OrchestraCustomer createFrom(final Person<NIFPersonID> person,final ContactInfo contactInfo) {
		AA14OrchestraCustomer customer = new AA14OrchestraCustomer();
		customer.setNif(person.getId());
		customer.setFirstName(person.getName());
		customer.setLastName(person.getSurname());
		customer.setEmail(contactInfo.getDefaultMailAddressOrAny());
		customer.setPhone(contactInfo.getDefaultPhoneOrAny());
		return customer;
	}
}
