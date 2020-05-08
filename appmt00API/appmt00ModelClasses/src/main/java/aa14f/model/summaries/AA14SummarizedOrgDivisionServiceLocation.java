package aa14f.model.summaries;

import java.util.Collection;
import java.util.Iterator;

import aa14f.model.config.AA14OrgDivisionServiceLocation;
import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.contact.ContactPhone;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="summarizedOrgDivisionServiceLocation")
@Accessors(prefix="_")
public class AA14SummarizedOrgDivisionServiceLocation 
	 extends AA14SummarizedOrganizationalModelObjectBase<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID,AA14OrgDivisionServiceLocation,
	 											 	     AA14SummarizedOrgDivisionServiceLocation> {

	private static final long serialVersionUID = -4373243410730886004L;
/////////////////////////////////////////////////////////////////////////////////////////
//  SERIALIZABLE FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="country",escape=true)
	@Getter @Setter private String _country;
	
	@MarshallField(as="state",escape=true)
	@Getter @Setter private String _state;
	
	@MarshallField(as="county",escape=true)
	@Getter @Setter private String _county;
	
	@MarshallField(as="municipality",escape=true)
	@Getter @Setter private String _municipality;

	@MarshallField(as="street",escape=true)
	@Getter @Setter private String _street;
	
	@MarshallField(as="phone",escape=true)
	@Getter @Setter private String _phone;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14SummarizedOrgDivisionServiceLocation() {
		super(AA14OrgDivisionServiceLocation.class);
	}
	public static AA14SummarizedOrgDivisionServiceLocation create() {
		return new AA14SummarizedOrgDivisionServiceLocation();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  FLUENT API
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14SummarizedOrgDivisionServiceLocation country(final String country) {
		_country = country;
		return this;
	}
	public AA14SummarizedOrgDivisionServiceLocation state(final String state) {
		_state = state;
		return this;
	}
	public AA14SummarizedOrgDivisionServiceLocation county(final String county) {
		_county = county;
		return this;
	}
	public AA14SummarizedOrgDivisionServiceLocation municipality(final String municipality) {
		_municipality = municipality;
		return this;
	}
	public AA14SummarizedOrgDivisionServiceLocation steet(final String street) {
		_street = street;
		return this;
	}
	public AA14SummarizedOrgDivisionServiceLocation phones(Collection<ContactPhone> phones) {
		if (CollectionUtils.hasData(phones)) {
			StringBuilder phonesStr = new StringBuilder();
			for (Iterator<ContactPhone> phoneIt = phones.iterator(); phoneIt.hasNext(); ) {
				ContactPhone phone = phoneIt.next();
				if (phone.getNumber() == null) continue;
				phonesStr.append(phone.getNumber().asString());
				if (phoneIt.hasNext()) phonesStr.append(" / ");
			}
			if (Strings.isNOTNullOrEmpty(phonesStr)) _phone = phonesStr.toString();
		}
		return this;
	}
}
