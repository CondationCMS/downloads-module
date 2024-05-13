package com.github.thmarx.cms.modules.download;

import com.github.thmarx.cms.api.extensions.HttpHandler;
import com.github.thmarx.cms.api.utils.HTTPUtil;
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

		var downloadPath = downloadResolver.resolve(filename);

		if (downloadPath.isEmpty()) {
			response.setStatus(HttpStatus.NOT_FOUND_404);
			callback.succeeded();
			return true;
		}

		DownloadsModule.COUNTER_DB.count("downloads", filename, LocalDate.now(), 1);
		
		try (
				var inputStream = Files.newInputStream(downloadPath.get()); 
				var outputStream = Content.Sink.asOutputStream(response);) {
			
			response.getHeaders().add("Content-Type", "application/octet-stream");
			response.getHeaders().add("Content-Disposition", "attachment; filename=" + filename);
			response.getHeaders().add("Content-Length", Files.size(downloadPath.get()));

			inputStream.transferTo(outputStream);
		}

		callback.succeeded();
		return true;
	}

}
