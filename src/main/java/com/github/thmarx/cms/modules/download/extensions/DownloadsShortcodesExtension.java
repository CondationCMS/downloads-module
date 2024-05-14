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

import com.github.thmarx.cms.api.extensions.RegisterShortCodesExtensionPoint;
import com.github.thmarx.cms.api.model.Parameter;
import com.github.thmarx.cms.modules.download.DownloadsModule;
import com.github.thmarx.modules.api.annotation.Extension;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author t.marx
 */
@Extension(RegisterShortCodesExtensionPoint.class)
public class DownloadsShortcodesExtension extends RegisterShortCodesExtensionPoint {

	public static final String DOWNLOAD_LINK = "<a href=\"/downloads?download=DOWNLOAD_ID\">DOWNLOAD_TEXTCOUNTER_TEXT</a>";
	
	Function<Parameter, String> downloadLink = (param) -> {
		var text = (String)param.getOrDefault("text", "Download");
		var id = (String)param.getOrDefault("id", "");
		var count = (String)param.getOrDefault("count", "false");
		var countText = (String)param.getOrDefault("count_text", "${count} downloads");
		var counterValue = "";
		if (Boolean.parseBoolean(count)) {
			counterValue = countText.replace("${count}", "" + DownloadsModule.COUNTER_DB.getCount("downloads", id, LocalDate.now()));
		}
		return DOWNLOAD_LINK
				.replace("DOWNLOAD_ID", id)
				.replace("DOWNLOAD_TEXT", text)
				.replace("COUNTER_TEXT", counterValue);
	};
	
	@Override
	public Map<String, Function<Parameter, String>> shortCodes() {
		Map<String, Function<Parameter, String>> codes = new HashMap<>();
		
		codes.put("downloads_link", downloadLink);
		
		return codes;
	}
	
}
