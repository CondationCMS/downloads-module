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


import com.condation.cms.api.extensions.HttpHandler;
import com.condation.cms.api.utils.HTTPUtil;
import java.nio.file.Files;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

/**
 *
 * @author t.marx
 */
@RequiredArgsConstructor
public class DownloadHandler implements HttpHandler {

	private final DownloadResolver downloadResolver;

	@Override
	public boolean handle(final Request request, final Response response, final Callback callback) throws Exception {

		var parameters = HTTPUtil.queryParameters(request.getHttpURI().getQuery());

		if (!parameters.containsKey("download")) {
			response.setStatus(HttpStatus.BAD_REQUEST_400);
			callback.succeeded();
			return true;
		}
		final String filename = parameters.get("download").getFirst();

		var streamableOptional = downloadResolver.resolve(filename);

		if (streamableOptional.isEmpty()) {
			response.setStatus(HttpStatus.NOT_FOUND_404);
			callback.succeeded();
			return true;
		}
		
		var download = streamableOptional.get();

		DownloadsModule.COUNTER_DB.count(Constants.Counters.DOWNLOADS, filename, LocalDate.now(), 1);
		
		try (
				var inputStream = download.getInputStream(); 
				var outputStream = Content.Sink.asOutputStream(response);) {
			
			response.getHeaders().add("Content-Type", "application/octet-stream");
			response.getHeaders().add("Content-Disposition", "attachment; filename=" + download.getFileName());
			response.getHeaders().add("Content-Length", download.getSize());

			inputStream.transferTo(outputStream);
		}

		callback.succeeded();
		return true;
	}

}
