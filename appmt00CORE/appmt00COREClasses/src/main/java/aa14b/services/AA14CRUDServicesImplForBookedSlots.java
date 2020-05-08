package aa14b.services;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import org.joda.time.DateTime;

import com.google.common.eventbus.EventBus;
import com.google.inject.persist.Transactional;

import aa14b.calendar.AA14CalendarService;
import aa14b.db.crud.AA14DBCRUDForBookedSlot;
import aa14b.services.delegates.persistence.AA14CRUDServicesDelegateForBookedSlot;
import aa14b.services.internal.AA14SlotOverlappingValidatorService;
import aa14f.api.interfaces.AA14CRUDServicesForBookedSlot;
import aa14f.api.interfaces.AA14PersonLocatorServices;
import aa14f.model.AA14BookedSlot;
import aa14f.model.oids.AA14IDs.AA14SlotID;
import aa14f.model.oids.AA14OIDs.AA14PeriodicSlotSerieOID;
import aa14f.model.oids.AA14OIDs.AA14ScheduleOID;
import aa14f.model.oids.AA14OIDs.AA14SlotOID;
import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.guids.CommonOIDs.UserCode;
import r01f.model.annotations.ModelObjectsMarshaller;
import r01f.model.persistence.CRUDOnMultipleResult;
import r01f.objectstreamer.Marshaller;
import r01f.persistence.db.DBDAOProviderBase;
import r01f.persistence.db.config.DBModuleConfigBuilder;
import r01f.securitycontext.SecurityContext;
import r01f.services.persistence.ServiceDelegateProvider;

/**
 * Implements the persistence-related services which in turn are
 * delegated to {@link AA14CRUDServicesDelegateForBookedSlot}
 */
@Singleton
@Accessors(prefix="_")
public class AA14CRUDServicesImplForBookedSlots
     extends AA14CRUDServicesImplBase<AA14SlotOID,AA14SlotID,AA14BookedSlot>
  implements AA14CRUDServicesForBookedSlot {
/////////////////////////////////////////////////////////////////////////////////////////
//	DELEGATE PROVIDER: called at every services impl method to create a fresh new 
//					   EntityManager and avoid transactional issues
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final ServiceDelegateProvider<AA14CRUDServicesDelegateForBookedSlot> _delegateProvider;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Inject
	public AA14CRUDServicesImplForBookedSlots(							final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
											  @ModelObjectsMarshaller   final Marshaller modelObjectsMarshaller,
																		final EventBus eventBus,
																		final Provider<EntityManager> entityManagerProvider,
																		final AA14PersonLocatorServices personLocatorServices,
											  							final AA14CalendarService calendarService,
											  							final AA14SlotOverlappingValidatorService slotOverlappintValidatorService) {
		super(coreCfg,
			  modelObjectsMarshaller,
			  eventBus,
			  entityManagerProvider);
		_delegateProvider = new ServiceDelegateProvider<AA14CRUDServicesDelegateForBookedSlot>() {
									@Override
									public AA14CRUDServicesDelegateForBookedSlot createDelegate(final SecurityContext securityContext) {
										return new AA14CRUDServicesDelegateForBookedSlot(_coreConfig,
																						 AA14CRUDServicesImplForBookedSlots.this.getFreshNewEntityManager(),
																						 _modelObjectsMarshaller,_eventBus,
																						 personLocatorServices,
																						 calendarService,
																						 slotOverlappintValidatorService);
									}
							};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Singleton
	public static class AA14DBCRUDForBookedSlotProvider
			    extends DBDAOProviderBase<AA14DBCRUDForBookedSlot> {
		@Inject
		public AA14DBCRUDForBookedSlotProvider(						   final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg,
											   						   final EntityManager entityManager,
											   @ModelObjectsMarshaller final Marshaller marshaller) {
			super(coreCfg,
				  entityManager,
				  marshaller);
		}
		@Override
		public AA14DBCRUDForBookedSlot get() {
			return new AA14DBCRUDForBookedSlot(DBModuleConfigBuilder.dbModuleConfigFrom(_coreConfig),
					  					  	   _entityManager,
					  					  	   _modelObjectsMarshaller);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SERVICES EXTENSION
// 	IMPORTANT!!! Do NOT put any logic in these methods ONLY DELEGATE!!!
/////////////////////////////////////////////////////////////////////////////////////////
	@Transactional
	@Override
	public CRUDOnMultipleResult<AA14BookedSlot> createPeriodicNonBookableSlots(final SecurityContext securityContext,
											  								   final AA14ScheduleOID schOid,
											  								   final Date startDate,final Date endDate,
											  								   final DateTime timeStartNonBookable,final DateTime timeEndNonBookable,
											  								   final boolean sunday,final boolean monday,final boolean tuesday,final boolean wednesday,final boolean thursday,final boolean friday,final boolean saturday,
											  								   final String nonBookableSubject,
											  								   final UserCode userCode) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14CRUDServicesForBookedSlot.class)
							.createPeriodicNonBookableSlots(securityContext,
															schOid,
															startDate,endDate,
															timeStartNonBookable,timeEndNonBookable,
															sunday,monday,tuesday,wednesday,thursday,friday,saturday,
															nonBookableSubject, 
															userCode);
	}
	@Transactional
	@Override
	public CRUDOnMultipleResult<AA14BookedSlot> deletePeriodicNonBookableSlots(final SecurityContext securityContext,
																			   final AA14PeriodicSlotSerieOID serieOid) {
		return this.forSecurityContext(securityContext)
						.createDelegateAs(AA14CRUDServicesForBookedSlot.class)
							.deletePeriodicNonBookableSlots(securityContext,
															serieOid);
	}
}
