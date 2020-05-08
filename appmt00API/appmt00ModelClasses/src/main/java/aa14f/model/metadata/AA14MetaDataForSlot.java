package aa14f.model.metadata;

import java.util.Date;

import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.Getter;
import r01f.locale.Language;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.MetaDataForType;
import r01f.model.metadata.annotations.Storage;

@MetaDataForType(modelObjTypeCode = AA14ModelObjectCodes.SLOT_MODEL_OBJ_TYPE_CODE,
		       	 description = {
	   					@DescInLang(language=Language.SPANISH,value="Slot"),
	   					@DescInLang(language=Language.BASQUE,value="[eu] Slot"),
	   					@DescInLang(language=Language.ENGLISH,value="Slot")
   			   	 })
public abstract class AA14MetaDataForSlot
	          extends AA14MetaDataForModelObjectBase<AA14SlotOID,AA14SlotID>
	       implements AA14HasMetaDataForOrganization,
	       			  AA14HasMetaDataForOrgDivision,
	       			  AA14HasMetaDataForOrgDivisionService,
	       			  AA14HasMetaDataForOrgDivisionServiceLocation {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Fecha"),
							@DescInLang(language=Language.BASQUE, value="[eu] Fecha"),
							@DescInLang(language=Language.ENGLISH, value="Date")
					  },
					  storage = @Storage(indexed=true, 
					  					 stored=true))
	@Getter private Date _slotDate;
}
