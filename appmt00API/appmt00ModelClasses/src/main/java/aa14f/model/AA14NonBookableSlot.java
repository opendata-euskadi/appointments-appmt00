package aa14f.model;

import aa14f.model.metadata.AA14MetaDataForNonBookableSlot;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.facets.FullTextSummarizable;
import r01f.facets.Summarizable;
import r01f.facets.builders.SummarizableBuilder;
import r01f.guids.CommonOIDs.UserCode;
import r01f.locale.Language;
import r01f.model.metadata.annotations.ModelObjectData;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.Color;
import r01f.types.summary.Summary;
import r01f.types.summary.SummaryBuilder;
import r01f.util.types.Strings;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;


@ModelObjectData(AA14MetaDataForNonBookableSlot.class)
@MarshallType(as="slot",typeId="nonBookableSlot")
@ConvertToDirtyStateTrackable			// changes in state are tracked
@Accessors(prefix="_")
public class AA14NonBookableSlot
     extends AA14BookedSlotBase<AA14NonBookableSlot> {

	private static final long serialVersionUID = -7122712793462931222L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Color DEFAULT_PRESENTATION_COLOR = Color.from("red");

/////////////////////////////////////////////////////////////////////////////////////////
//  REASON
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="subject")
	@Getter @Setter private String _subject;
/////////////////////////////////////////////////////////////////////////////////////////
//  USER CODE
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="userCode")
	@Getter @Setter private UserCode _userCode;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14NonBookableSlot() {
		super(AA14BookedSlotType.NON_BOOKABLE);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  APPOINTMENT DESCRIPTION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getSubjectIn(final Language lang) {
		return _subject != null ? _subject
								: "--no subject--";
	}
	@Override
	public String getSummaryIn(final Language lang) {
		return this.asSummarizable()
				   .getSummary()
				   .asString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SUMMARIES
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Summarizable asSummarizable() {
		return SummarizableBuilder.summarizableFrom(_buildSummary());
	}
	@Override
	public FullTextSummarizable asFullTextSummarizable() {
		return SummarizableBuilder.fullTextSummarizableFrom(this);
	}	
	private Summary _buildSummary() {
		final String subjectSummary = _subject != null ? _subject
												 	   : null;
		final String personSummary = _userCode != null ? _userCode.toString()
											   		   : null;
		StringBuilder sb = new StringBuilder();
		if (subjectSummary != null) sb.append(subjectSummary);
		if (personSummary != null) sb.append(" > ").append(personSummary);
		return SummaryBuilder.languageInDependent()
							 .create(sb.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if the appointment is valid
	 */
	@Override
	public ObjectValidationResult<AA14NonBookableSlot> validate() {
		// common validations
		ObjectValidationResult<AA14NonBookableSlot> superValid = super.validate();
		if (superValid.isNOTValid()) return superValid;
		
		// Validate the subject
		if (Strings.isNullOrEmpty(_subject)) {
			return ObjectValidationResultBuilder.on(this)
										 .isNotValidBecause("The {} with oid={} is NOT valid since the subject info is NOT enougth",
												 			this.getClass().getSimpleName(),this.getOid());
		}
		
		// Validate the person info
		if (_userCode == null) {
			return ObjectValidationResultBuilder.on(this)
									 .isNotValidBecause("The {} with oid={} is NOT valid since the user code is NOT enougth",
											 			this.getClass().getSimpleName(),this.getOid());
		}
		
		// All OK
		return ObjectValidationResultBuilder.on(this)
										  	.isValid();
	}
}
