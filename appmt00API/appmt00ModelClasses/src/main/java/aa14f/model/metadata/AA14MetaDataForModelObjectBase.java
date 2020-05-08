package aa14f.model.metadata;

import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.locale.Language;
import r01f.model.metadata.HasMetaDataForHasFullTextSummaryModelObject;
import r01f.model.metadata.HasMetaDataForHasIDModelObject;
import r01f.model.metadata.HasMetaDataForHasSummaryModelObject;
import r01f.model.metadata.TypeMetaDataForPersistableModelObjectBase;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForType;
import r01f.types.summary.LangDependentSummary;

@MetaDataForType(modelObjTypeCode = AA14ModelObjectCodes.MODEL_OBJ_TYPE_BASE_CODE,
		       	 description = {
	   					@DescInLang(language=Language.SPANISH,value="Objeto del modelo"),
	   					@DescInLang(language=Language.BASQUE,value="[eu] Model Object"),
	   					@DescInLang(language=Language.ENGLISH,value="Model Object")
   			   	 })
public abstract class AA14MetaDataForModelObjectBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>>
       		  extends TypeMetaDataForPersistableModelObjectBase<O>
		   implements HasMetaDataForHasIDModelObject<ID>,
		   			  HasMetaDataForHasSummaryModelObject<LangDependentSummary>,
			   		  HasMetaDataForHasFullTextSummaryModelObject<LangDependentSummary> {
	// common fields if necessary
}
