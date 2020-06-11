package aa14f.model;

import java.util.Date;

import com.google.common.collect.Range;

import aa14f.model.metadata.AA14MetaDataForAppointment;
import aa14f.model.oids.AA14IDs.AA14PersonLocatorID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.facets.FullTextSummarizable;
import r01f.facets.Summarizable;
import r01f.facets.builders.SummarizableBuilder;
import r01f.locale.Language;
import r01f.model.metadata.annotations.ModelObjectData;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.Color;
import r01f.types.contact.ContactInfo;
import r01f.types.contact.EMail;
import r01f.types.contact.NIFPersonID;
import r01f.types.contact.Person;
import r01f.types.contact.Phone;
import r01f.types.summary.Summary;
import r01f.types.summary.SummaryBuilder;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;
import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;


@ModelObjectData(AA14MetaDataForAppointment.class)
@MarshallType(as="slot",typeId="appointment")
@ConvertToDirtyStateTrackable			// changes in state are tracked
@Accessors(prefix="_")
public class AA14Appointment
     extends AA14BookedSlotBase<AA14Appointment> {

	private static final long serialVersionUID = -7122712793462931222L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTANTS
/////////////////////////////////////////////////////////////////////////////////////////
	public static final Color DEFAULT_PRESENTATION_COLOR = Color.from("grey");
	
/////////////////////////////////////////////////////////////////////////////////////////
//  APPOINTMENT
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="subject")
	@Getter @Setter private AA14AppointmentSubject _subject;	
	
	@MarshallField(as="numberOfPersonsInAppointment",escape=true)
	@Getter @Setter private String _numberOfPersonsInAppointment;
/////////////////////////////////////////////////////////////////////////////////////////
//  REQUESTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="person")
	@Getter @Setter private Person<NIFPersonID> _person;
	
	@MarshallField(as="personLocatorId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AA14PersonLocatorID _personLocatorId;
	
	@MarshallField(as="contactInfo")
	@Getter @Setter private ContactInfo _contactInfo;
	
	@MarshallField(as="privateDetails",escape=true)
	@Getter @Setter private String _privateDetails;
/////////////////////////////////////////////////////////////////////////////////////////
//  BUSINESS-SPECIFIC DATA
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="businessData")
	@Getter @Setter private AA14AppointmentBusinessData _businessData;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14Appointment() {
		super(AA14BookedSlotType.APPOINTMENT);
		//_numberOfAdjacentSlots = AA14NumberOfAdjacentSlots.ONE;
	}
	public AA14Appointment(final Date startDate,final int durationInMinutes) {
		this();
		this.setDate(startDate);
		this.setDurationMinutes(durationInMinutes);;
	}
	public AA14Appointment(final Range<Date> dateRange) {
		this();
		this.setDateRange(dateRange);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  APPOINTMENT DESCRIPTION
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getSubjectIn(final Language lang) {
		return _subject != null ? _subject.asSummarizable()
									   		 .getSummary()
									   		 .asString()
							    : "--no subject--";
	}
	@Override
	public String getSummaryIn(final Language lang) {
		return this.asSummarizable()
				   .getSummary()
				   .asString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EMAIL USED IN LOCATOR
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * The email to be used when computing the locator
	 * @return
	 */
	public EMail getPersonLocatorEMail() {
		if (_contactInfo == null || CollectionUtils.isNullOrEmpty(_contactInfo.getMailAddresses())) return null;
		
		// [1] - Default mail
		EMail defMail = _contactInfo.getDefaultMailAddress();
		if (defMail != null) return defMail;
		
		// [2] - If no default mail and just one email, return that email
		if (_contactInfo.getMailAddresses().size() == 1) return CollectionUtils.pickOneAndOnlyElement(_contactInfo.getMailAddresses());
		
		// [3] - If multiple mails, just return the first
		return CollectionUtils.firstOf(_contactInfo.getMailAddresses());
	}
	/**
	 * The phone to be used when computing the locator
	 * @return
	 */
	public Phone getPersonLocatorPhone() {
		if (_contactInfo == null || CollectionUtils.isNullOrEmpty(_contactInfo.getPhones())) return null;
		
		// [1] - Default phone
		Phone defPhone = _contactInfo.getDefaultPhone();
		if (defPhone != null) return defPhone;
		
		// [2] - If no default phone and just one phone, return that phone
		if (_contactInfo.getPhones().size() == 1) return CollectionUtils.pickOneAndOnlyElement(_contactInfo.getPhones());
		
		// [3] - If multiple phone, just return the first
		return CollectionUtils.firstOf(_contactInfo.getPhones());
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
		final String subjectSummary = _subject != null ? _subject.asSummarizable()
														   		 .getSummary()
														   		 .asString()
												 : null;
		final String personSummary = _person != null ? _person.asSummarizable()
															  .getSummary()
															  .asString()
											   : null;
		StringBuilder outSumm = new StringBuilder();
		if (subjectSummary != null && !subjectSummary.equals("-")) {
			outSumm.append("(").append(subjectSummary).append(")");
		}
		if (personSummary != null) {
			if (Strings.isNOTNullOrEmpty(outSumm)) outSumm.append(" > ");
			outSumm.append(personSummary);
		}
		return SummaryBuilder.languageInDependent()
							 .create(outSumm.toString());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUSINESS DATA
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public <D extends AA14AppointmentBusinessData> D getBusinessDataAs(final Class<D> type) {
		return _businessData != null ? (D)_businessData
									 : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  VALIDATION
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return true if the appointment is valid
	 */
	@Override
	public ObjectValidationResult<AA14Appointment> validate() {
		// common validations
		ObjectValidationResult<AA14Appointment> superValid = super.validate();
		if (superValid.isNOTValid()) return superValid;
		
		// Validate the subject
		if (_subject == null || _subject.getId() == null) {
			return ObjectValidationResultBuilder.on(this)
													 .isNotValidBecause("The {} with oid={} is NOT valid since the subject info is NOT enougth",
															 			this.getClass().getSimpleName(),this.getOid());
		}
		
		// Validate the person info
		if (_person == null || _person.getId() == null
		 || _contactInfo == null) {
			return ObjectValidationResultBuilder.on(this)
													 .isNotValidBecause("The {} with oid={} is NOT valid since the person info is NOT enougth",
															 			this.getClass().getSimpleName(),this.getOid());
		}
		
		// All OK
		return ObjectValidationResultBuilder.on(this)
										  		 .isValid();
	}
}
