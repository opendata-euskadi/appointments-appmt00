package aa14f.model.summaries;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import aa14f.model.AA14Appointment;
import aa14f.model.AA14AppointmentSubject;
import aa14f.model.AA14BookedSlotType;
import aa14f.model.oids.AA14IDs.AA14PersonLocatorID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.contact.EMail;
import r01f.types.contact.NIFPersonID;
import r01f.types.contact.Person;
import r01f.types.contact.Phone;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="summarizedAppointment")
@Accessors(prefix="_")
public class AA14SummarizedAppointment 
	 extends AA14SummarizedBookedSlotBase<AA14Appointment,
	 									  AA14SummarizedAppointment> {

	private static final long serialVersionUID = -4373243410730886004L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="person") 
	@Getter @Setter private Person<NIFPersonID> _person;
	
	@MarshallField(as="personLocatorId",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AA14PersonLocatorID _personLocatorId;
	
	@MarshallField(as="subject",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private AA14AppointmentSubject _subject;
	
	@MarshallField(as="emails",
				   whenXml=@MarshallFieldAsXml(collectionElementName="email"))
	@Getter @Setter private Collection<EMail> _eMails;
	
	@MarshallField(as="phones",
				   whenXml=@MarshallFieldAsXml(collectionElementName="phone"))
	@Getter @Setter private Collection<Phone> _phones;	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14SummarizedAppointment() {
		super(AA14Appointment.class,
			  AA14BookedSlotType.APPOINTMENT);
	}
	public static AA14SummarizedAppointment create() {
		return new AA14SummarizedAppointment();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14SummarizedAppointment withPersonLocatorId(final AA14PersonLocatorID personLocatorId) {
		_personLocatorId = personLocatorId;
		return this;
	}
	public AA14SummarizedAppointment forPerson(final Person<NIFPersonID> person) {
		_person = person;
		return this;
	}
	public AA14SummarizedAppointment subject(final AA14AppointmentSubject subject) {
		_subject = subject;
		return this;
	}
	public AA14SummarizedAppointment withEMails(final Collection<EMail> emails) {
		_eMails = emails;
		return this;
	}
	public AA14SummarizedAppointment withPhones(final Collection<Phone> phones) {
		_phones = phones;
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the different {@link EMail}s from a bunch of appointments
	 * @param appointments
	 * @return
	 */
	public static Set<EMail> eMailSetOf(final Collection<AA14SummarizedAppointment> appointments) {
		if (CollectionUtils.isNullOrEmpty(appointments)) return null;
		Iterable<EMail> flattenMails = FluentIterable.from(appointments)
											 .transformAndConcat(new Function<AA14SummarizedAppointment,Collection<EMail>>() {
																		@Override
																		public Collection<EMail> apply(final AA14SummarizedAppointment appointment) {
																			return appointment.getEMails() != null ? appointment.getEMails()
																												   : Lists.<EMail>newArrayList();
																		}
													 			 });
		return Sets.newLinkedHashSet(flattenMails);
	}
	/**
	 * Returns the different {@link Phone}s from a bunch of appointments
	 * @param appointments
	 * @return
	 */
	public static Set<Phone> phoneSetOf(final Collection<AA14SummarizedAppointment> appointments) {
		if (CollectionUtils.isNullOrEmpty(appointments)) return null;
		Iterable<Phone> flattenPhones = FluentIterable.from(appointments)
											 .transformAndConcat(new Function<AA14SummarizedAppointment,Collection<Phone>>() {
																		@Override
																		public Collection<Phone> apply(final AA14SummarizedAppointment appointment) {
																			return appointment.getPhones() != null ? appointment.getPhones()
																												   : Lists.<Phone>newArrayList();
																		}
													 			 });
		return Sets.newLinkedHashSet(flattenPhones);
	}
}
