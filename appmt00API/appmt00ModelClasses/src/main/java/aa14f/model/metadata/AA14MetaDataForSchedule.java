package aa14f.model.metadata;

import aa14f.model.oids.AA14IDs.AA14ScheduleID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import r01f.locale.Language;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForType;

@MetaDataForType(modelObjTypeCode = AA14ModelObjectCodes.SCHEDULE_MODEL_OBJ_TYPE_CODE,
		       	 description = {
	   					@DescInLang(language=Language.SPANISH,value="Calendario"),
	   					@DescInLang(language=Language.BASQUE,value="[eu] Calendario"),
	   					@DescInLang(language=Language.ENGLISH,value="Calendario")
   			   	 })
public abstract class AA14MetaDataForSchedule
	 		  extends AA14MetaDataForModelObjectBase<AA14ScheduleOID,AA14ScheduleID> {
	// other fields
}
