package aa14f.model.metadata;

import aa14f.model.oids.AA14IDs.AA14OrgDivisionID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionOID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.model.metadata.FieldIDToken;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.Storage;

public interface AA14HasMetaDataForOrgDivision {
/////////////////////////////////////////////////////////////////////////////////////////
// SEARCH METADATAS
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public enum SEARCHABLE_METADATA
	 implements FieldIDToken {
		OID ("orgDivisionOid"),
		ID ("orgDivisionId");

		@Getter private final String _token;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////	
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Identificador único de la división de la organización"),
							@DescInLang(language=Language.BASQUE, value="[eu] Organization division's unique identifier"),
							@DescInLang(language=Language.ENGLISH, value="Organization division's unique identifier")
					  },
					  storage = @Storage(indexed=true, 
					  					 stored=true))
	public AA14OrgDivisionOID getOrgDivisionOid();
	
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Identificador único de negocio de la división de la organización"),
							@DescInLang(language=Language.BASQUE, value="[eu] Organization division's unique business identifier"),
							@DescInLang(language=Language.ENGLISH, value="Organization division's unique business identifier")
					  },
					  storage = @Storage(indexed=true, 
					  					 stored=true))
	public AA14OrgDivisionID getOrgDivisionId();
}
