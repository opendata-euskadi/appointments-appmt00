package aa14b.bootstrap.core;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;

import aa14b.events.AA14CRUDOKEventListenersForNotificationEvents.AA14CRUDOKEventListenersForAppointmentLog;
import aa14b.events.AA14CRUDOKEventListenersForNotificationEvents.AA14CRUDOKEventListenersForAppointmentNotifyByEMail;
import aa14b.events.AA14CRUDOKEventListenersForNotificationEvents.AA14CRUDOKEventListenersForAppointmentNotifyByMessaging;
import aa14b.events.AA14EventListenerForPersonLocatorIDReminder;
import aa14b.notifier.config.AA14SchedulerNotifierConfig;
import aa14b.services.AA14BusinessConfigServicesImpl;
import aa14b.services.internal.AA14BookedSlotSummarizerService;
import aa14b.services.internal.AA14CORESideBusinessConfigServices;
import aa14b.services.internal.AA14SlotOverlappingValidatorService;
import lombok.EqualsAndHashCode;
import r01f.bootstrap.BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase;
import r01f.bootstrap.CoreServicesBootstrapGuiceModuleBindsEventListeners;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.core.services.notifier.config.NotifiersConfigs;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.inject.HasMoreBindings;
import r01f.persistence.db.config.DBModuleConfigBuilder;
import r01f.persistence.search.config.SearchModuleConfigBuilder;
import r01f.services.ids.ServiceIDs.CoreModule;


@EqualsAndHashCode(callSuper=true)				// This is important for guice modules
public class AA14ServicesBootstrapGuiceModule
     extends BeanImplementedPersistenceServicesCoreBootstrapGuiceModuleBase
  implements CoreServicesBootstrapGuiceModuleBindsEventListeners,
  			 HasMoreBindings {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14ServicesBootstrapGuiceModule(final ServicesCoreBootstrapConfigWhenBeanExposed coreBootstrapCfg) {
		super(coreBootstrapCfg,
			  new AA14DBGuiceModule(DBModuleConfigBuilder.dbModuleConfigFrom(coreBootstrapCfg)),
			  new AA14SearchGuiceModule(SearchModuleConfigBuilder.searchModuleConfigFrom(coreBootstrapCfg)),
			  new Module[] {
					new AA14NotifierGuiceModule(// notifier scheduler
												coreBootstrapCfg.<AA14SchedulerNotifierConfig>getSubModuleConfigFor(AppComponent.forId("notifier.scheduler")),
												// notifiers
												coreBootstrapCfg.<NotifiersConfigs>getSubModuleConfigFor(CoreModule.NOTIFIER)),
					new AA14CalendarGuiceModule()
			  });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configureMoreBindings(final Binder binder) {
		// core-side configs loader
		binder.bind(AA14CORESideBusinessConfigServices.class)
			  .to(AA14BusinessConfigServicesImpl.class)
			  .in(Singleton.class);
		
		// booked slots summarizer 
		binder.bind(AA14BookedSlotSummarizerService.class)
			  .in(Singleton.class);
		
		// slot overlapping validator
		binder.bind(AA14SlotOverlappingValidatorService.class)
			  .in(Singleton.class);
		
	}
	@Override
	public void bindEventListeners(final Binder binder) {
		// Bind notifiers event listeners
		// ... messaging (latinia)
		binder.bind(AA14CRUDOKEventListenersForAppointmentNotifyByMessaging.class)
			  .asEagerSingleton();
		// ... mail
		binder.bind(AA14CRUDOKEventListenersForAppointmentNotifyByEMail.class)
			  .asEagerSingleton();
		// ... log
		binder.bind(AA14CRUDOKEventListenersForAppointmentLog.class)
			  .asEagerSingleton();
		
		// Bind reminder event listener
		binder.bind(AA14EventListenerForPersonLocatorIDReminder.class)
			  .asEagerSingleton();
	}
}
