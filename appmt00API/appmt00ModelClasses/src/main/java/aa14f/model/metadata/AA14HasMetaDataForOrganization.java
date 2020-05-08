package aa14f.model.metadata;

import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14OIDs.AA14OrganizationOID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.locale.Language;
import r01f.model.metadata.FieldIDToken;
import r01f.model.metadata.annotations.DescInLang;
import r01f.model.metadata.annotations.MetaDataForField;
import r01f.model.metadata.annotations.Storage;

public interface AA14HasMetaDataForOrganization {
/////////////////////////////////////////////////////////////////////////////////////////
// 	SEARCH METADATAS
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public enum SEARCHABLE_METADATA
	 implements FieldIDToken {
		OID ("organizationOid"),
		ID ("organizationId");

		@Getter private final String _token;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	 
/////////////////////////////////////////////////////////////////////////////////////////	
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Identificador único de Organización"),
							@DescInLang(language=Language.BASQUE, value="[eu] Organization's unique identifier"),
							@DescInLang(language=Language.ENGLISH, value="Organization's unique identifier")
					  },
					  storage = @Storage(indexed=true, 
					  					 stored=true))
	public AA14OrganizationOID getOrganizationOid();
	
	@MetaDataForField(description = {
							@DescInLang(language=Language.SPANISH, value="Identificador único de negocio de la Organización"),
							@DescInLang(language=Language.BASQUE, value="[eu] Organization's unique business identifier"),
							@DescInLang(language=Language.ENGLISH, value="Organization's unique business identifier")
					  },
					  storage = @Storage(indexed=true, 
					  					 stored=true))
	public AA14OrganizationID getOrganizationId();
}
