package com.condation.cms.modules.downloads;

/*-
 * #%L
 * downloads-module
 * %%
 * Copyright (C) 2024 CondationCMS
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.condation.cms.api.configuration.configs.SiteConfiguration;
import com.condation.cms.api.feature.features.ConfigurationFeature;
import com.condation.cms.api.feature.features.DBFeature;
import com.condation.cms.api.module.SiteModuleContext;
import com.condation.cms.api.module.SiteRequestContext;
import com.condation.cms.modules.downloads.counter.CounterDB;
import com.condation.cms.modules.downloads.counter.H2CounterDB;
import com.condation.cms.modules.downloads.counter.MockCounterDB;
import com.condation.modules.api.ModuleLifeCycleExtension;
import com.condation.modules.api.annotation.Extension;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author t.marx
 */
@Slf4j
@Extension(ModuleLifeCycleExtension.class)
public class DownloadsModule extends ModuleLifeCycleExtension<SiteModuleContext, SiteRequestContext> {

	public static DownloadResolver DOWNLOAD_RESOLVER;

	public static CounterDB COUNTER_DB;

	@Override
	public void init() {
	}

	private boolean isCounterActive() {
		return getContext().get(ConfigurationFeature.class).configuration()
				.get(SiteConfiguration.class)
				.siteProperties()
				.getOrDefault("modules.downloads-module.counter", false);
	}

	@Override
	public void activate() {
		DOWNLOAD_RESOLVER = new DownloadResolver(getContext().get(DBFeature.class).db());
		if (isCounterActive()) {
			COUNTER_DB = new H2CounterDB(configuration.getDataDir());
			((H2CounterDB) COUNTER_DB).open();
		} else {
			COUNTER_DB = new MockCounterDB();
		}
	}

	@Override
	public void deactivate() {
		try {
			COUNTER_DB.close();
		} catch (Exception ex) {
			log.error("", ex);
		}
	}
}
