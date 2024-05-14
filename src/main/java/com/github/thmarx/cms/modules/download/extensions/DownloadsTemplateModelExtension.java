package com.github.thmarx.cms.modules.download.extensions;

/*-
 * #%L
 * downloads-module
 * %%
 * Copyright (C) 2024 Marx-Software
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

import com.github.thmarx.cms.api.extensions.TemplateModelExtendingExtentionPoint;
import com.github.thmarx.cms.api.template.TemplateEngine;
import com.github.thmarx.cms.modules.download.DownloadsModule;
import com.github.thmarx.modules.api.annotation.Extension;
import java.time.LocalDate;

/**
 *
 * @author t.marx
 */
@Extension(TemplateModelExtendingExtentionPoint.class)
public class DownloadsTemplateModelExtension extends TemplateModelExtendingExtentionPoint {

	@Override
	public void extendModel(TemplateEngine.Model model) {
		model.values.put("downloads", new Downloads());
	}
	
	public static class Downloads {
		
		public String url (String download) {
			return "/downloads?download=" + download;
		}
		
		public String count (String download) {
			return String.valueOf(DownloadsModule.COUNTER_DB.getCount("downloads", download, LocalDate.now()));
		}
	}
}
