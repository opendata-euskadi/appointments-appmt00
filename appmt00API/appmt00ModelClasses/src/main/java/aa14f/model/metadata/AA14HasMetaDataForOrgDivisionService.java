package aa14f.model.metadata;

import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceOID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.model.metadata.FieldIDToken;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.Storage;

public interface AA14HasMetaDataForOrgDivisionService {
/////////////////////////////////////////////////////////////////////////////////////////
// 	SEARCH METADATAS
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public enum SEARCHABLE_METADATA
	 implements FieldIDToken {
		OID ("orgDivisionServiceOid"),
		ID ("orgDivisionServiceId");

		@Getter private final String _token;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////	
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Identificador único del servicio de la división de la organización"),
							@DescInLang(language=Language.BASQUE, value="[eu] Organization division service's unique identifier"),
							@DescInLang(language=Language.ENGLISH, value="Organization division service's unique identifier")
					  },
					  storage = @Storage(indexed=true, 
					  					 stored=true))
	public AA14OrgDivisionServiceOID getOrgDivisionServiceOid();
	
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Identificador único de negocio del servicio de la división de la organización"),
							@DescInLang(language=Language.BASQUE, value="[eu] Organization division service's unique business identifier"),
							@DescInLang(language=Language.ENGLISH, value="Organization division service's unique business identifier")
					  },
					  storage = @Storage(indexed=true, 
					  					 stored=true))
	public AA14OrgDivisionServiceID getOrgDivisionServiceId();
}
