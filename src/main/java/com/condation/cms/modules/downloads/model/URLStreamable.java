package com.condation.cms.modules.downloads.model;

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
