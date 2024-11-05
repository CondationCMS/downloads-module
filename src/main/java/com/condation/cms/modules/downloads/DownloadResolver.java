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

import com.condation.cms.api.db.DB;
import com.condation.cms.api.utils.PathUtil;
import com.condation.cms.modules.downloads.model.PathStreamable;
import com.condation.cms.modules.downloads.model.Streamable;
import com.condation.cms.modules.downloads.model.URLStreamable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;
import org.tomlj.internal.TomlParser;

/**
 *
 * @author t.marx
 */
@Slf4j
@RequiredArgsConstructor
public class DownloadResolver {

	private final DB db;

	public Optional<Streamable> resolve(final String file) {
		final Path downloadsPath = db.getFileSystem().resolve("downloads");
		var path = downloadsPath.resolve(file);
		// security check
		if (!PathUtil.isChild(downloadsPath, path)) {
			return Optional.empty();
		}
		if (Files.exists(path)) {
			return Optional.of(new PathStreamable(path));
		}
		var urlStreamable = tryMeta(file);
		if (urlStreamable.isPresent()) {
			return urlStreamable;
		}

		return Optional.empty();
	}
	
	private Optional<Streamable> tryMeta (String filename) {
		final Path downloadsPath = db.getFileSystem().resolve("downloads");
		var path = downloadsPath.resolve(filename + ".meta.toml");
		if (Files.exists(path)) {
			try {
				TomlParseResult result = Toml.parse(path);
				final URLStreamable urlStreamable = new URLStreamable(result);
				if (urlStreamable.exists()) {
					return Optional.of(urlStreamable);
				}
			} catch (IOException ex) {
				log.error("", ex);
			}
		}
		
		return Optional.empty();
	}
}
