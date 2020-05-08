package aa14f.model.metadata;

import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import r01f.locale.Language;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForType;

@MetaDataForType(modelObjTypeCode = AA14ModelObjectCodes.ORGANIZATION_MODEL_OBJ_TYPE_CODE,
		       	 description = {
	   					@DescInLang(language=Language.SPANISH,value="Organization"),
	   					@DescInLang(language=Language.BASQUE,value="[eu] Organization"),
	   					@DescInLang(language=Language.ENGLISH,value="Organization")
   			   	 })
public abstract class AA14MetaDataForOrganization
	 		  extends AA14MetaDataForModelObjectBase<AA14OrganizationOID,AA14OrganizationID>
		   implements AA14HasMetaDataForOrganization {
	// other fields
}
