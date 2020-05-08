package aa14f.model.metadata;

import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import r01f.locale.Language;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForType;

@MetaDataForType(modelObjTypeCode = AA14ModelObjectCodes.ORG_DIVISION_SERVICE_MODEL_OBJ_TYPE_CODE,
		       	 description = {
	   					@DescInLang(language=Language.SPANISH,value="Servicio dentro de una división de una organización"),
	   					@DescInLang(language=Language.BASQUE,value="[eu] Organization division's service"),
	   					@DescInLang(language=Language.ENGLISH,value="Organization division's service")
   			   	 })
public abstract class AA14MetaDataForOrgDivisionService
	 		  extends AA14MetaDataForModelObjectBase<AA14OrgDivisionServiceOID,AA14OrgDivisionServiceID> 
		   implements AA14HasMetaDataForOrganization,
		   			  AA14HasMetaDataForOrgDivision {

}
