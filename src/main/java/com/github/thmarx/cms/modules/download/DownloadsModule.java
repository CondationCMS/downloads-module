package com.github.thmarx.cms.modules.download;

import com.github.thmarx.cms.api.feature.features.DBFeature;
import com.github.thmarx.cms.api.module.CMSModuleContext;
import com.github.thmarx.cms.api.module.CMSRequestContext;
import com.github.thmarx.cms.modules.download.counter.CounterDB;
import com.github.thmarx.cms.modules.download.counter.H2CounterDB;
import com.github.thmarx.modules.api.ModuleLifeCycleExtension;
import com.github.thmarx.modules.api.annotation.Extension;
import java.io.File;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author t.marx
 */
@Slf4j
@Extension(ModuleLifeCycleExtension.class)
public class DownloadsModule extends ModuleLifeCycleExtension<CMSModuleContext, CMSRequestContext> {

	public static DownloadResolver DOWNLOAD_RESOLVER;
	
	public static H2CounterDB COUNTER_DB;
	
	@Override
	public void init() {
	}

	@Override
	public void activate() {
		DOWNLOAD_RESOLVER = new DownloadResolver(getContext().get(DBFeature.class).db());
		COUNTER_DB = new H2CounterDB(configuration.getDataDir());
		COUNTER_DB.open();
	}

	@Override
	public void deactivate() {
		COUNTER_DB.close();
	}
}
