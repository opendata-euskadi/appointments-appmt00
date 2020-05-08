package aa14b.bootstrap.core;

import aa14b.notifier.config.AA14NotifierConfigForEMail;
import aa14b.notifier.config.AA14NotifierConfigForSMS;
import aa14b.notifier.config.AA14NotifierConfigForVoice;
import aa14b.notifier.config.AA14SchedulerNotifierConfig;
import aa14b.services.AA14ServiceInterfaceImpl;
import aa14f.common.internal.AA14AppCodes;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigBuilder;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.bootstrap.services.config.core.ServicesCoreSubModuleBootstrapConfig;
import r01f.config.ContainsConfigData;
import r01f.core.services.notifier.config.NotifierConfigForEMail;
import r01f.core.services.notifier.config.NotifierConfigForLog;
import r01f.core.services.notifier.config.NotifierConfigForSMS;
import r01f.core.services.notifier.config.NotifierConfigForVoice;
import r01f.core.services.notifier.config.NotifierConfigProviders.NotifierAppDependentConfigProviderFromProperties;
import r01f.core.services.notifier.config.NotifierEnums.NotifierImpl;
import r01f.core.services.notifier.config.NotifiersConfigs;
import r01f.core.services.notifier.spi.NotifierSPIUtil;
import r01f.guids.CommonOIDs.AppComponent;
import r01f.patterns.IsBuilder;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.xmlproperties.XMLPropertiesForApp;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Builds bootstrap confif
 */
@Slf4j
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class AA14COREServicesBootstrapConfigBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static ServicesCoreBootstrapConfigWhenBeanExposed buildCoreBootstrapConfig(final XMLPropertiesForApp xmlProps) {
		XMLPropertiesForAppComponent dbXmlProps = xmlProps.forComponent(AppComponent.compose(AA14AppCodes.CORE_APPOINTMENTS_MOD,
																							 CoreModule.DBPERSISTENCE));
		XMLPropertiesForAppComponent notifierXmlProps = xmlProps.forComponent(AppComponent.compose(AA14AppCodes.CORE_APPOINTMENTS_MOD,
																							 	   CoreModule.NOTIFIER));
		// Load the notifiers config
		NotifiersConfigs notifiersCfg = _loadNotifiersConfig(notifierXmlProps);

		return ServicesCoreBootstrapConfigBuilder.forCoreAppAndModule(AA14AppCodes.CORE_APPCODE,AA14AppCodes.CORE_APPOINTMENTS_MOD)
	   				.beanImplemented()
	   					.bootstrappedBy(AA14ServicesBootstrapGuiceModule.class)
	   					.findServicesExtending(AA14ServiceInterfaceImpl.class)
	   					.withSubModulesConfigs(
	   							// db config
	   							ServicesCoreSubModuleBootstrapConfig.createForDBPersistenceSubModule(AA14DBModuleConfig.dbConfigFor(dbXmlProps)),
	   							
	   							////////// Notifier
	   							// notifier scheduler
	   							new ServicesCoreSubModuleBootstrapConfig<AA14SchedulerNotifierConfig>(AppComponent.forId("notifier.scheduler"),
	   																						 	    	 AA14SchedulerNotifierConfig.createFrom(notifierXmlProps)),
	   							
	   							// - notifier
	   							new ServicesCoreSubModuleBootstrapConfig<NotifiersConfigs>(CoreModule.NOTIFIER,
	   																					   notifiersCfg)
	   					).build();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PROPERTIES FOR PROVIDERS
/////////////////////////////////////////////////////////////////////////////////////////
	private static final NotifiersConfigs _loadNotifiersConfig(final XMLPropertiesForAppComponent props) {
		// Use java's SPI to get the config
		NotifierConfigForEMail forEMail = NotifierSPIUtil.emailNotifierConfigFrom(props,
																				  // creates the app dependent config part
																				  new NotifierAppDependentConfigProviderFromProperties() {
																							@Override
																							public ContainsConfigData provideConfigUsing(final NotifierImpl impl,
																																		 final XMLPropertiesForAppComponent props) {
																								return new AA14NotifierConfigForEMail(props);
																							}
																				  });
		NotifierConfigForSMS forSMS = NotifierSPIUtil.smsNotifierConfigFrom(props,
																			// creates the app dependent config part
																			new NotifierAppDependentConfigProviderFromProperties() {
																						@Override
																						public ContainsConfigData provideConfigUsing(final NotifierImpl impl,
																																	 final XMLPropertiesForAppComponent props) {
																							return new AA14NotifierConfigForSMS(props);
																						}
																			});
		NotifierConfigForVoice forVoice = NotifierSPIUtil.voiceNotifierConfigFrom(props,
																				  // creates the app dependent config part
																				  new NotifierAppDependentConfigProviderFromProperties() {
																							@Override
																							public ContainsConfigData provideConfigUsing(final NotifierImpl impl,
																																		 final XMLPropertiesForAppComponent props) {
																								return new AA14NotifierConfigForVoice(props);
																							}
																				  });

		NotifierConfigForLog forLog = new NotifierConfigForLog(props);


		// Assemble all notifier configs
		NotifiersConfigs outCfg = new NotifiersConfigs(forEMail,
													   forSMS,forVoice,
													   forLog,
													   null);		// no push notifier
		log.warn("Notifiers config: ");
		log.warn("\t\t-EMail: {}",forEMail != null
										? forEMail.isEnabled() ? "ENABLED" : "DISABLED"
										: "NULL");
		log.warn("\t\t-  SMS: {}",forSMS != null
										? forSMS.isEnabled() ? "ENABLED" : "DISABLED"
										: "NULL");
		log.warn("\t\t-Voice: {}",forVoice != null
										? forVoice.isEnabled() ? "ENABLED" : "DISABLED"
										: "NULL");
		log.warn("\t\t-  Log: {}",forLog != null
										? forLog.isEnabled() ? "ENABLED" : "DISABLED"
										: "NULL");

		return outCfg;
	}

}
