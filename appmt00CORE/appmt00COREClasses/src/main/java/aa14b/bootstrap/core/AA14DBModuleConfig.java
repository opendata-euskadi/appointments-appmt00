package aa14b.bootstrap.core;

import javax.persistence.EntityManager;

import aa14b.db.entities.AA14DBEntityForOrganizationalEntityBase;
import r01f.bootstrap.services.config.core.ServicesCoreBootstrapConfigWhenBeanExposed;
import r01f.patterns.Factory;
import r01f.patterns.MemoizedUponFactory;
import r01f.persistence.db.config.DBModuleConfig;
import r01f.persistence.db.config.DBModuleConfigBase;
import r01f.persistence.db.config.DBModuleConfigBuilder;
import r01f.persistence.db.config.DBModuleConfigWrapper;
import r01f.services.ids.ServiceIDs.CoreModule;
import r01f.xmlproperties.XMLPropertiesForAppComponent;

/**
 * Wraps the {@link DBModuleConfig} in order to override the {@link DBModuleConfigBase}{@link #isFullTextSearchSupported(EntityManager)} method
 */
public class AA14DBModuleConfig 
	 extends DBModuleConfigWrapper {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Memoizes the full-text support so it's NOT computed again and again 
	 */
	private final MemoizedUponFactory<Boolean> _fullTextSearchSupported = new MemoizedUponFactory<Boolean>();
	 
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBModuleConfig(final DBModuleConfig _wrappedConfig) {
		super(_wrappedConfig);
	}
	public static final AA14DBModuleConfig dbConfigFor(final XMLPropertiesForAppComponent xmlProps) {
		DBModuleConfig dbModuleConfig = DBModuleConfigBuilder.dbConfigFor(xmlProps);
		return new AA14DBModuleConfig(dbModuleConfig);
	}
	public static AA14DBModuleConfig dbModuleConfigFrom(final ServicesCoreBootstrapConfigWhenBeanExposed coreCfg) {
		DBModuleConfig dbModuleConfig = coreCfg.getSubModuleConfigFor(CoreModule.DBPERSISTENCE);
		return new AA14DBModuleConfig(dbModuleConfig);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isFullTextSearchSupported(final EntityManager entityManager) {
		return _fullTextSearchSupported.get(new Factory<Boolean>() {
													@Override
													public Boolean create() {
														// tries to run a full-text search... if it fails full text search is NOT enabled
														return DBModuleConfigBase.<AA14DBEntityForOrganizationalEntityBase>testFullText(entityManager,
																			 										   					AA14DBEntityForOrganizationalEntityBase.class,
																			 										   					"_nameSpanish");	// any full-text indexed col
													}
											});
	}
}
