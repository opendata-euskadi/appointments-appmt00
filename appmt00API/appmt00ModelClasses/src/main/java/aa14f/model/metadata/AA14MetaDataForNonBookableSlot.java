package aa14f.model.metadata;

import r01f.locale.Language;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForType;

@MetaDataForType(modelObjTypeCode = AA14ModelObjectCodes.NON_BOOKABLE_SLOT_MODEL_OBJ_TYPE_CODE,
		       	 description = {
	   					@DescInLang(language=Language.SPANISH,value="Hueco NO citable"),
	   					@DescInLang(language=Language.BASQUE,value="[eu] Non-bookable slot"),
	   					@DescInLang(language=Language.ENGLISH,value="Non-bookable slot")
   			   	 })
public abstract class AA14MetaDataForNonBookableSlot
	 		  extends AA14MetaDataForSlot {
	// other properties
}
