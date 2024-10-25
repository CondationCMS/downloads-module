package com.condation.cms.modules.downloads.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tomlj.TomlParseResult;

/**
 *
 * @author thmar
 */
@Slf4j
@RequiredArgsConstructor
public class URLStreamable implements Streamable {

	private final TomlParseResult result;

	private URLConnection connection;

	private URLConnection getConnection() throws IOException {
		if (connection == null) {
			try {
				URI url = new URI(result.getString("download.url"));

				connection = url.toURL().openConnection();
			} catch (URISyntaxException ex) {
				throw new IOException(ex);
			}
		}
		return connection;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return getConnection().getInputStream();
	}

	@Override
	public String getFileName() {
		return result.getString("download.filename");
	}

	@Override
	public long getSize() {
		try {
			return getConnection().getContentLengthLong();
		} catch (IOException ex) {
			log.error("", ex);
		}
		return 0;
	}

}
