package com.github.thmarx.cms.modules.download;

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

import com.github.thmarx.cms.api.db.DB;
import com.github.thmarx.cms.api.utils.PathUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author t.marx
 */
@Slf4j
@RequiredArgsConstructor
public class DownloadResolver {

	private final DB db;

	public Optional<Path> resolve(final String file) {
		try {
			final Path downloadsPath = db.getFileSystem().resolve("downloads");
			var path = downloadsPath.resolve(file);
			if (!PathUtil.isChild(downloadsPath, path)) {
				return Optional.empty();
			}
			if (Files.exists(path)) {
				return Optional.of(path);
			}
			
		} catch (IOException ex) {
			log.error("", ex);
		}
		return Optional.empty();
	}
}
