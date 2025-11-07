package com.condation.cms.modules.downloads.extensions;

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


import com.condation.cms.api.extensions.TemplateModelExtendingExtensionPoint;
import com.condation.cms.api.template.TemplateEngine;
import com.condation.cms.modules.downloads.Constants;
import com.condation.cms.modules.downloads.DownloadsModule;
import com.condation.modules.api.annotation.Extension;
import java.util.Map;

/**
 *
 * @author t.marx
 */
@Extension(TemplateModelExtendingExtensionPoint.class)
public class DownloadsTemplateModelExtension extends TemplateModelExtendingExtensionPoint {

	@Override
	public Map<String, Object> getModel() {
		return Map.of("downloads", new Downloads());
	}
	
	
	
	public static class Downloads {
		
		public String url (String download) {
			return "/downloads?download=" + download;
		}
		
		public String count (String download) {
			return String.valueOf(DownloadsModule.COUNTER_DB.getCountAll(Constants.Counters.DOWNLOADS, download));
		}
	}
}
