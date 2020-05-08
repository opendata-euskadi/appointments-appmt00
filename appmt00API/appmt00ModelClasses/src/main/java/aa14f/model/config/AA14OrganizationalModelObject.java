package aa14f.model.config;

import aa14f.model.AA14EntityModelObject;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import r01f.facets.LangDependentNamed.HasLangDependentNamedFacet;
import r01f.locale.Language;
import r01f.model.SummarizedModelObject;

/**
 * Interface for every AA14 organizational entity: {@link AA14Organization}, {@link AA14OrgDivision}, {@link AA14OrgDivisionService}
 * @param <O>
 */
public interface AA14OrganizationalModelObject<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>> 
		 extends AA14EntityModelObject<O,ID>,
				 HasLangDependentNamedFacet {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public AA14BusinessID getBusinessId();
	
	public void setBusinessId(final AA14BusinessID businessId);
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns a summary in a given language
	 * @param lang
	 * @return
	 */
	public <T extends AA14OrganizationalModelObject<O,ID>> SummarizedModelObject<T> getSummarizedIn(Language lang);
}
