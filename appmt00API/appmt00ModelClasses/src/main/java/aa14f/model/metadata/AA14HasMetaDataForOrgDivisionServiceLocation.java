package aa14f.model.metadata;

import aa14f.model.oids.AA14IDs.AA14OrgDivisionServiceLocationID;
import aa14f.model.oids.AA14OIDs.AA14OrgDivisionServiceLocationOID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.model.metadata.FieldIDToken;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.Storage;

public interface AA14HasMetaDataForOrgDivisionServiceLocation {
/////////////////////////////////////////////////////////////////////////////////////////
// 	SEARCH METADATAS
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public enum SEARCHABLE_METADATA
	 implements FieldIDToken {
		OID ("orgDivisionServiceLocationOid"),
		ID ("orgDivisionServiceLocationId");

		@Getter private final String _token;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////	
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Identificador único de la localización del servicio de la división de la organización"),
							@DescInLang(language=Language.BASQUE, value="[eu] Organization division service location's unique identifier"),
							@DescInLang(language=Language.ENGLISH, value="Organization division service location's unique identifier")
					  },
					  storage = @Storage(indexed=true, 
					  					 stored=true))
	public AA14OrgDivisionServiceLocationOID getOrgDivisionServiceLocationOid();
	
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Identificador único de negocio de la localización del servicio de la división de la organización"),
							@DescInLang(language=Language.BASQUE, value="[eu] Organization division service location's unique business identifier"),
							@DescInLang(language=Language.ENGLISH, value="Organization division service location's unique business identifier")
					  },
					  storage = @Storage(indexed=true, 
					  					 stored=true))
	public AA14OrgDivisionServiceLocationID getOrgDivisionServiceLocationId();
}
