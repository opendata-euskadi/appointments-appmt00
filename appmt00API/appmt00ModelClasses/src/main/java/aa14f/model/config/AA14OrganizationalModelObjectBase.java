package aa14f.model.config;

import aa14f.model.AA14EntityModelObjectBase;
import aa14f.model.oids.AA14IDs.AA14BusinessID;
import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.facets.FullTextSummarizable;
import r01f.facets.LangDependentNamed;
import r01f.facets.Summarizable;
import r01f.facets.builders.SummarizableBuilder;
import r01f.facets.delegates.LangDependentNamedDelegate;
import r01f.locale.LanguageTexts;
import r01f.locale.LanguageTextsWrapper;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.types.summary.SummaryBuilder;
import r01f.validation.ObjectValidationResult;
import r01f.validation.SelfValidates;

@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
public abstract class AA14OrganizationalModelObjectBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,
										 			   	SELF_TYPE extends AA14OrganizationalModelObjectBase<O,ID,SELF_TYPE>>
		      extends AA14EntityModelObjectBase<O,ID,SELF_TYPE>
		   implements AA14OrganizationalModelObject<O,ID>,
		   			  SelfValidates<SELF_TYPE> {

	private static final long serialVersionUID = 7579054159448752329L;

/////////////////////////////////////////////////////////////////////////////////////////
//  COMMON FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="businessId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected AA14BusinessID _businessId;
	
	
	@MarshallField(as="name")
	@Getter @Setter protected LanguageTexts _nameByLanguage;
	
	@MarshallField(as="notifierFromConfig")
	@Getter @Setter private AA14NotifierFromConfig _notifierFromConfig;
	
	@MarshallField(as="notifierMessageComposingConfig")
	@Getter @Setter private AA14NotifierMessageComposingConfig _notifierMessageComposingConfig;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  REFERENCE
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return a reference to this model object (a type that encapsulates the oid and the id)
	 */
	public AA14OrganizationalModelObjectRef<O,ID> getReference() {
		return new AA14OrganizationalModelObjectRef<O,ID>(_oid,_id);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  HasLangDependentNamedFacet
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	@Getter private final transient LanguageTextsWrapper<SELF_TYPE> _name = LanguageTextsWrapper.atHasLang((SELF_TYPE)this);
	
	@Override @SuppressWarnings("unchecked")
	public LangDependentNamed asLangDependentNamed() {
		return new LangDependentNamedDelegate<SELF_TYPE>((SELF_TYPE)this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SUMMARIES
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Summarizable asSummarizable() {
		return SummarizableBuilder.summarizableFrom(SummaryBuilder.languageDependent()
												  		.create(AA14OrganizationalModelObjectBase.this));
	}
	@Override
	public FullTextSummarizable asFullTextSummarizable() {
		return SummarizableBuilder.fullTextSummarizableFrom(this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  VALIDATION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override @SuppressWarnings("unchecked")
	public ObjectValidationResult<SELF_TYPE> validate() {
		Object organizationalModelObjectsValidators = AA14OrganizationalModelObjectsValidators.createOrgBaseValidator()
																							  .validate(this);
		return (ObjectValidationResult<SELF_TYPE>)organizationalModelObjectsValidators;
	}
}
