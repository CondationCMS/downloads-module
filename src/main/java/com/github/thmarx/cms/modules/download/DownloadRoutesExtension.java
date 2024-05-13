package com.github.thmarx.cms.modules.download;

import com.github.thmarx.cms.api.extensions.HttpRoutesExtensionPoint;
import com.github.thmarx.cms.api.extensions.Mapping;
import com.github.thmarx.modules.api.annotation.Extension;
import org.eclipse.jetty.http.pathmap.PathSpec;

/**
 *
 * @author t.marx
 */
@Extension(HttpRoutesExtensionPoint.class)
public class DownloadRoutesExtension extends HttpRoutesExtensionPoint {

	@Override
	public Mapping getMapping() {
		Mapping mapping = new Mapping();
		
		mapping.add(PathSpec.from("/downloads"), new DownloadHandler(DownloadsModule.DOWNLOAD_RESOLVER));
		
		return mapping;
	}
	
}
