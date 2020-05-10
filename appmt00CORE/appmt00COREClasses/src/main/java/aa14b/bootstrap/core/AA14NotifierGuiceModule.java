package aa14b.bootstrap.core;

import java.util.Properties;

import javax.inject.Provider;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import aa14b.notifier.config.AA14SchedulerNotifierConfig;
import aa14b.notifier.scheduler.quartz.AA14NotifierMockSchedulerWhenDisabled;
import aa14b.notifier.scheduler.quartz.AA14NotifierQuartzSchedulerWrapper;
import aa14b.notifier.scheduler.quartz.AA14NotifierScheduler;
import aa14b.services.delegates.notifier.AA14NotifierServicesDelegateImpl;
import aa14b.services.delegates.notifier.AA14NotifierServicesEMailImpl;
import aa14b.services.delegates.notifier.AA14NotifierServicesLoggerImpl;
import aa14b.services.delegates.notifier.AA14NotifierServicesSMSImpl;
import aa14b.services.delegates.notifier.AA14NotifierServicesVoiceImpl;
import aa14f.common.internal.AA14AppCodes;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.scheduler.QuartzSchedulerGuiceModule;
import r01f.core.services.notifier.annotations.UseEMailNotifier;
import r01f.core.services.notifier.annotations.UseLogNotifier;
import r01f.core.services.notifier.annotations.UseMessagingNotifier;
import r01f.core.services.notifier.annotations.UseVoiceNotifier;
import r01f.core.services.notifier.bootstrap.NotifierGuiceModule;
import r01f.core.services.notifier.config.NotifiersConfigs;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.xmlproperties.XMLProperties;
import r01f.xmlproperties.XMLPropertiesForAppComponent;
import r01f.xmlproperties.annotations.XMLPropertiesComponent;

@Slf4j
@EqualsAndHashCode				// This is important for guice modules
     class AA14NotifierGuiceModule 
implements Module {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private final AA14SchedulerNotifierConfig _notifierSchedulerConfig;
	private final NotifiersConfigs _notifiersConfig;

/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14NotifierGuiceModule(// notifier scheduler
								   final AA14SchedulerNotifierConfig notifierSchedulerConfig,
								   // notifiers
								   final NotifiersConfigs notifiersConfig) {
		_notifierSchedulerConfig = notifierSchedulerConfig;
		
		_notifiersConfig = notifiersConfig;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MODULE
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void configure(final Binder binder) {
		////////// Notifier scheduler
		_bindScheduler(binder);
		
		
		////////// Notifiers
		// [0] - Notifier module
		binder.install(new NotifierGuiceModule(_notifiersConfig));

		
		// [0] - Log (event notifier services that just logs the event)
		binder.bind(AA14NotifierServicesDelegateImpl.class)
			  .annotatedWith(UseLogNotifier.class)
			  .to(AA14NotifierServicesLoggerImpl.class)
			  .in(Singleton.class);

		// [1] - EMail (see provider method below)
		binder.bind(AA14NotifierServicesDelegateImpl.class)
			  .annotatedWith(UseEMailNotifier.class)
			  .to(AA14NotifierServicesEMailImpl.class)		// gets injected with a java mail sender (see provider below)
			  .in(Singleton.class);	
		
		// [2] - Latinia (see provider method below)
		binder.bind(AA14NotifierServicesDelegateImpl.class)
			  .annotatedWith(UseMessagingNotifier.class)
			  .to(AA14NotifierServicesSMSImpl.class)	// gets injected with Latinia service (see provider below)
			  .in(Singleton.class);
		
		// [3] - Voice (see provider method below)
		binder.bind(AA14NotifierServicesDelegateImpl.class)
			  .annotatedWith(UseVoiceNotifier.class)
			  .to(AA14NotifierServicesVoiceImpl.class)		// gets injected with Twilio service (see provider below)
			  .in(Singleton.class);
				
		////////// Velocity engine to create the messages
		binder.bind(VelocityEngine.class)
			  .toProvider(new Provider<VelocityEngine>() {
									@Override
									public VelocityEngine get() {
										Properties velocityProps = new Properties();
										velocityProps.put(RuntimeConstants.DEFAULT_RUNTIME_LOG_NAME,"aa14velocity");
										velocityProps.put(RuntimeConstants.RESOURCE_LOADER,"classpath");
								        velocityProps.put("classpath.resource.loader.class",ClasspathResourceLoader.class.getName());

										VelocityEngine outVelocityEngine = new VelocityEngine();
										outVelocityEngine.setProperties(velocityProps);
										return outVelocityEngine;
									}
						  })
			  .in(Singleton.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PROPERTIES FOR PROVIDERS
/////////////////////////////////////////////////////////////////////////////////////////
	@Provides @XMLPropertiesComponent("notifier")
	XMLPropertiesForAppComponent provideXMLPropertiesForServices(final XMLProperties props) {
		XMLPropertiesForAppComponent outPropsForComponent = new XMLPropertiesForAppComponent(props.forApp(AA14AppCodes.CORE_APPCODE),
																							 AppComponent.forId("notifier"));
		return outPropsForComponent;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BIND SCHEDULER
/////////////////////////////////////////////////////////////////////////////////////////
	private void _bindScheduler(final Binder binder) {		
		// scheduler config
		log.warn("[Notifier Scheduler Config:]\n{}",
				 _notifierSchedulerConfig.debugInfo());
		binder.bind(AA14SchedulerNotifierConfig.class)
			  .toInstance(_notifierSchedulerConfig);
		
		// scheduler
		if (_notifierSchedulerConfig.getSchedulerConfig().isEnabled()) {
			// quartz 
			binder.install(new QuartzSchedulerGuiceModule(_notifierSchedulerConfig.getSchedulerConfig()));
			
			// render scheduler
			binder.bind(AA14NotifierScheduler.class)
				  .to(AA14NotifierQuartzSchedulerWrapper.class)
				  .in(Singleton.class);
			
		} else {
			// bind a mock impl 
			binder.bind(AA14NotifierScheduler.class)
				  .to(AA14NotifierMockSchedulerWhenDisabled.class)
				  .in(Singleton.class);
			
		}
	}
}
