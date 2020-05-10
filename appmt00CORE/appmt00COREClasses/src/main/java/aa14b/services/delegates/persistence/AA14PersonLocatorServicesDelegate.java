package aa14b.services.delegates.persistence;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.eventbus.EventBus;

import aa14b.db.entities.AA14DTOForPersonIdAndLocator;
import aa14b.events.AA14PersonLocatorIDRemindMessage;
import aa14f.api.interfaces.AA14FindServicesForBookedSlot;
import aa14f.api.interfaces.AA14PersonLocatorServices;
import aa14f.model.oids.AA14IDs.AA14OrganizationID;
import aa14f.model.oids.AA14IDs.AA14PersonLocatorID;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.exceptions.ExceptionSeverity;
import r01f.locale.Language;
import r01f.model.persistence.PersistenceOperationExecResultBuilder;
import r01f.model.services.COREServiceErrorOrigin;
import r01f.model.services.COREServiceErrorType;
import r01f.model.services.COREServiceMethod;
import r01f.model.services.COREServiceMethodExecResult;
import r01f.objectstreamer.Marshaller;
import r01f.securitycontext.SecurityContext;
import r01f.services.delegates.persistence.PersistenceServicesDelegateBase;
import r01f.types.contact.EMail;
import r01f.types.contact.PersonID;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
@Accessors(prefix="_")
public class AA14PersonLocatorServicesDelegate
	 extends PersistenceServicesDelegateBase 
  implements AA14PersonLocatorServices {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final EntityManager _entityManager;
	private final AA14FindServicesForBookedSlot _bookedSlotsFind;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14PersonLocatorServicesDelegate(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
											 final EntityManager entityManager,
											 final Marshaller marshaller,
				  			   		   	   	 final EventBus eventBus,
				  			   		   	   	 final AA14FindServicesForBookedSlot bookedSlotsFind) {
		super(coreCfg,
			  null,			// no service impl
			  eventBus);	// no event bus
		_entityManager = entityManager;
		_bookedSlotsFind = bookedSlotsFind;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public COREServiceMethodExecResult<AA14PersonLocatorID> findPersonLocatorFor(final SecurityContext securityContext,
																	 			 final PersonID personId,final EMail contactEMail) {
		log.debug("> find locator for personId={}", 
				  personId);
		
		// find locator for personId + contactEMail
		TypedQuery<AA14DTOForPersonIdAndLocator> qry = _entityManager.createNamedQuery("AA14DBEntityForAppointment.findPersonLocator",
																					   AA14DTOForPersonIdAndLocator.class)
																	 .setParameter("personId",personId.asString())
																	 .setParameter("contactEMail",contactEMail.asString());
		qry.setHint(QueryHints.READ_ONLY,HintValues.TRUE);
		Collection<AA14DTOForPersonIdAndLocator> dbDTOs = qry.getResultList();
		log.debug("> ...found {} locators for personId/email={}/{}",
				  dbDTOs.size(),personId,contactEMail);
		
		if (CollectionUtils.isNullOrEmpty(dbDTOs)) return PersistenceOperationExecResultBuilder.using(securityContext)
																							   .executed(COREServiceMethod.named("findOrCreatePersonLocatorFor"))
																							   .returning(null);	// no locator found
		
		// there should NOT exist more than a single [person locator] 
		// filter NON-NULL dtos
		dbDTOs = FluentIterable.from(dbDTOs)
							   .filter(new Predicate<AA14DTOForPersonIdAndLocator>() {
												@Override
												public boolean apply(final AA14DTOForPersonIdAndLocator dto) {
													return Strings.isNOTNullOrEmpty(dto.getPersonLocatorId());
												}
							   		   })
							   .toList();
		if (dbDTOs.size() > 1) {
			String err = Strings.customized("The DB is in an INCONSISTENT state: there exists more than a single [person locator] for personId/email={}/{}",
					  						personId,contactEMail);
			log.error("Error while finding the [person locator]: {}",err);
			return PersistenceOperationExecResultBuilder.using(securityContext)
														.notExecuted(COREServiceMethod.named("findOrCreatePersonLocatorFor"))
														.because(err,COREServiceErrorType.originatedAt(COREServiceErrorOrigin.SERVER)
																						 .withName("illega db state: inconsistent data")
																						 .noCodes()
																						 .severity(ExceptionSeverity.FATAL)
																						 .build());
		} else if (dbDTOs.size() == 0) {
			return PersistenceOperationExecResultBuilder.using(securityContext)
														.executed(COREServiceMethod.named("findOrCreatePersonLocatorFor"))
													    .returning(null);	// no locator found
		} else {
			// there's a [person locator]: return it
			String personLocatorStr = CollectionUtils.<AA14DTOForPersonIdAndLocator>pickOneAndOnlyElement(dbDTOs).getPersonLocatorId();
			AA14PersonLocatorID outPersonLocator = Strings.isNOTNullOrEmpty(personLocatorStr) ? AA14PersonLocatorID.forId(personLocatorStr)
																							  : null;	
			return PersistenceOperationExecResultBuilder.using(securityContext)
													    .executed(COREServiceMethod.named("findOrCreatePersonLocatorFor"))
														.returning(outPersonLocator);
		}
	}
	@Override
	public COREServiceMethodExecResult<Boolean> remindPersonLocatorFor(final SecurityContext securityContext,
																	   final AA14OrganizationID orgId,
																	   final PersonID personId,final EMail contactEMail,final Language lang) {
		// [1] - A reminder with the [locator] can only be sent for those [appointments] whose
		//		 [email] matches the received one
		//		 ... so filter [appointments] with the received [email]
		COREServiceMethodExecResult<AA14PersonLocatorID> personLocatorFind = this.findPersonLocatorFor(securityContext, 
																									   personId,contactEMail);
		if (personLocatorFind.hasFailed()) {
			String err = Strings.customized("Error while retrieving locator for personId/eMail={}/{}",
					 						personId,contactEMail);
			return PersistenceOperationExecResultBuilder.using(securityContext)
														.notExecuted(COREServiceMethod.named("remindPersonLocatorFor"))
														.because(personLocatorFind.asCOREServiceMethodExecError());
		}
		
		// [2] - Send an [event bus] message that will be handled asynchronously to:
		//			2. Update every appointment WITHOUT the person locator
		//			1. Send an email with the [person locator id]
		AA14PersonLocatorID personLocatorId = personLocatorFind.getOrThrow();	// safe now
		if (personLocatorId != null) {
			AA14PersonLocatorIDRemindMessage event = new AA14PersonLocatorIDRemindMessage(securityContext,
																				  		  orgId,
																				  		  personId,contactEMail,lang,
																				  		  personLocatorId);
			log.info("... post an [event bus] message to recover [person locator] for personId={} and email={}",
					personId,contactEMail);
		
			_eventBus.post(event);
		}
		
		// [3] - Return
		return PersistenceOperationExecResultBuilder.using(securityContext)
													.executed(COREServiceMethod.named("remindPersonLocatorFor"))
													.returning(personLocatorId != null);	// exists person locator id
	}
}
