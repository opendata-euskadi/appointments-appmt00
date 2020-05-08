package aa14b.events;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import aa14f.model.AA14ModelObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.model.PersistableModelObject;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.types.contact.EMail;
import r01f.types.contact.Phone;
import r01f.util.types.collections.CollectionUtils;

/**
 * Encapsulates all data about a message to be sent
 * This model object is NOT persisted (it's NOT a {@link PersistableModelObject} instance) since it's composed
 * when handling the creation event from other model objects
 */
@Accessors(prefix="_")
@Slf4j
public abstract class AA14NotificationMessageBase
		   implements AA14ModelObject {

	private static final long serialVersionUID = -7549631620318919359L;

/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="phones",
				   whenXml=@MarshallFieldAsXml(collectionElementName="phone"))
	@Getter @Setter private Collection<Phone> _phones;

	@MarshallField(as="mails",
				   whenXml=@MarshallFieldAsXml(collectionElementName="mail"))
	@Getter @Setter private Collection<EMail> _mails;	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return a {@link Collection} of sanitized phones (removes invalid phones an sets a valid format)
	 */
	public Collection<Phone> getPhonesSanitized() {
		if (CollectionUtils.isNullOrEmpty(_phones)) return null;
		Collection<Phone> outPhones = FluentIterable.from(_phones)
													// Filter NOT valid phones
												    .filter(new Predicate<Phone>() {
																		@Override
																		public boolean apply(final Phone phone) {
																			if (!phone.isValid()) {
																				log.warn("{} phone is NOT valid",phone.asString());
																				return false;
																			}
																			return true;
																		}
													  		})
													// put the phones in an standard format
													.transform(new Function<Phone,Phone>() {
																		@Override
																		public Phone apply(final Phone phone) {
																			return Phone.create(phone.asStringWithoutCountryCode());
																		}
													  		   })
													.toSet();
		return outPhones;
	}
}
