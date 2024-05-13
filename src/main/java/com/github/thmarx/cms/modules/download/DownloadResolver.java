package com.github.thmarx.cms.modules.download;

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
