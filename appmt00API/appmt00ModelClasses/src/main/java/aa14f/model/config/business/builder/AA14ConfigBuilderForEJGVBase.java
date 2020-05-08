package aa14f.model.config.business.builder;

import java.util.Collection;

import com.google.common.collect.Lists;

import lombok.experimental.Accessors;
import r01f.types.contact.ContactInfoUsage;
import r01f.types.contact.ContactPhone;
import r01f.types.geo.GeoOIDs.GeoCountyID;

@Accessors(prefix="_")
abstract class AA14ConfigBuilderForEJGVBase
	   extends AA14ConfigBuilderBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	protected static final GeoCountyID ARABA_COUNTY_ID = GeoCountyID.forId(1);
	protected static final GeoCountyID BIZKAIA_COUNTY_ID = GeoCountyID.forId(48);
	protected static final GeoCountyID GIPUZKOA_COUNTY_ID = GeoCountyID.forId(20);

/////////////////////////////////////////////////////////////////////////////////////////
//  METHODS
///////////////////////////////////////////////////////////////////////////////////////// 
	protected static Collection<ContactPhone> _buildZuzenenanPhones() {
		return Lists.newArrayList(ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
											  .withNumber("012"),
								  ContactPhone.createToBeUsedFor(ContactInfoUsage.WORK)
											  .withNumber("945018000"));
	}
}