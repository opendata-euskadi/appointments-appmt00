package aa14f.model.metadata;

import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import r01f.locale.Language;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForType;

@MetaDataForType(modelObjTypeCode = AA14ModelObjectCodes.ORG_DIVISION_SERVICE_LOCATION_MODEL_OBJ_TYPE_CODE,
		       	 description = {
	   					@DescInLang(language=Language.SPANISH,value="Localización de un servicio dentro de una división de la organización"),
	   					@DescInLang(language=Language.BASQUE,value="[eu] Organization division service's location"),
	   					@DescInLang(language=Language.ENGLISH,value="Organization division service's location")
   			   	 })
public abstract class AA14MetaDataForOrgDivisionServiceLocation
	          extends AA14MetaDataForModelObjectBase<AA14OrgDivisionServiceLocationOID,AA14OrgDivisionServiceLocationID> 
		   implements AA14HasMetaDataForOrganization,
		   			  AA14HasMetaDataForOrgDivision,
		   			  AA14HasMetaDataForOrgDivisionService {
	// other fields
}
