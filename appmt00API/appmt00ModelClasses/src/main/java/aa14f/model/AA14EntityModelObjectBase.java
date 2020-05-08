package aa14f.model;

import aa14f.model.oids.AA14IDs.AA14ModelObjectID;
import aa14f.model.oids.AA14OIDs.AA14ModelObjectOID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.facets.FullTextSummarizable;
import r01f.facets.Summarizable;
import r01f.facets.builders.SummarizableBuilder;
import r01f.model.PersistableModelObjectBase;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.types.summary.SummaryBuilder;
import r01f.util.types.Strings;

@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
public abstract class AA14EntityModelObjectBase<O extends AA14ModelObjectOID,ID extends AA14ModelObjectID<O>,
										 		SELF_TYPE extends AA14EntityModelObjectBase<O,ID,SELF_TYPE>>
	          extends PersistableModelObjectBase<O,SELF_TYPE>
	  	   implements AA14EntityModelObject<O,ID> {

	private static final long serialVersionUID = 7579054159448752329L;

/////////////////////////////////////////////////////////////////////////////////////////
//  COMMON FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter protected ID _id;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	SUMMARIES
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Summarizable asSummarizable() {
		return SummarizableBuilder.summarizableFrom(SummaryBuilder.languageInDependent()
														  .create(Strings.customized("{} object with id={}",
																  					 AA14EntityModelObjectBase.this.getClass(),_id.asString())));
	}
	@Override
	public FullTextSummarizable asFullTextSummarizable() {
		return SummarizableBuilder.fullTextSummarizableFrom(this);
	}
}
