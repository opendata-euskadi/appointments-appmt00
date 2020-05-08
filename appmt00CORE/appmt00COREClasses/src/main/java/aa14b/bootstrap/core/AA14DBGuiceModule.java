package aa14b.bootstrap.core;

import lombok.EqualsAndHashCode;
import r01f.bootstrap.persistence.DBGuiceModuleBase;
import r01f.persistence.db.config.DBModuleConfig;


@EqualsAndHashCode(callSuper=true)				// This is important for guice modules
  class AA14DBGuiceModule
extends DBGuiceModuleBase {
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public AA14DBGuiceModule(final DBModuleConfig cfg) {
		super(cfg);
	}
}
