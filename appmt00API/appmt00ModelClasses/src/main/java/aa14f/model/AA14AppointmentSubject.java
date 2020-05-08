package aa14f.model;

import aa14f.model.oids.AA14OIDs.AA14AppointmentSubjectID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.facets.FullTextSummarizable;
import r01f.facets.FullTextSummarizable.HasFullTextSummaryFacet;
import r01f.facets.Summarizable;
import r01f.facets.Summarizable.HasSummaryFacet;
import r01f.facets.builders.SummarizableBuilder;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.summary.SummaryBuilder;

@MarshallType(as="appointmentSubject")
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class AA14AppointmentSubject
  implements AA14ModelObject,
  			 HasSummaryFacet,
  			 HasFullTextSummaryFacet {

	private static final long serialVersionUID = 5723471916787685146L;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * An id for the appointment subject (ie an expedient)
	 */
	@MarshallField(as="id",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AA14AppointmentSubjectID _id;
	/**
	 * A description for the appointment subject
	 */
	@MarshallField(as="description",escape=true,
				   whenXml=@MarshallFieldAsXml(asParentElementValue=true))
	@Getter @Setter private String _description;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Summarizable asSummarizable() {
		// description (id)
		return SummarizableBuilder.summarizableFrom(SummaryBuilder.languageInDependent()
												  		.create(_id.asString()));
	}
	@Override
	public FullTextSummarizable asFullTextSummarizable() {
		return SummarizableBuilder.fullTextSummarizableFrom(this);	// the summary is also the full text summary
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
}
