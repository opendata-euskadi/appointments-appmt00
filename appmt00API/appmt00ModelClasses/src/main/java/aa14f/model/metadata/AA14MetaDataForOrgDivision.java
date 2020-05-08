package aa14f.model.metadata;

import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import r01f.locale.Language;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForType;

@MetaDataForType(modelObjTypeCode = AA14ModelObjectCodes.ORG_DIVISION_MODEL_OBJ_TYPE_CODE,
		       	 description = {
	   					@DescInLang(language=Language.SPANISH,value="División dentro de una organización"),
	   					@DescInLang(language=Language.BASQUE,value="[eu] Organization division"),
	   					@DescInLang(language=Language.ENGLISH,value="Organization Division")
   			   	 })
public abstract class AA14MetaDataForOrgDivision
	 		  extends AA14MetaDataForModelObjectBase<AA14OrgDivisionOID,AA14OrgDivisionID>
		   implements AA14HasMetaDataForOrgDivision {
	// any other field
}
