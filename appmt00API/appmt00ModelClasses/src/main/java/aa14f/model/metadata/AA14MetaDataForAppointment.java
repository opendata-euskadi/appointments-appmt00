package aa14f.model.metadata;

import lombok.Getter;
import r01f.locale.Language;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.MetaDataForType;
import r01f.model.metadata.annotations.Storage;
import r01f.types.contact.NIFPersonID;

@MetaDataForType(modelObjTypeCode = AA14ModelObjectCodes.APPOINTMENT_MODEL_OBJ_TYPE_CODE,
		       	 description = {
	   					@DescInLang(language=Language.SPANISH,value="Cita"),
	   					@DescInLang(language=Language.BASQUE,value="[eu] Appointment"),
	   					@DescInLang(language=Language.ENGLISH,value="Appointment")
   			   	 })
public abstract class AA14MetaDataForAppointment
	 		  extends AA14MetaDataForSlot {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Identificador de la persona"),
							@DescInLang(language=Language.BASQUE, value="[eu] Person identifier"),
							@DescInLang(language=Language.ENGLISH, value="Person indentifier")
					  },
					  storage = @Storage(indexed=true, 
					  					 stored=true))
	@Getter private NIFPersonID _personId;
}
