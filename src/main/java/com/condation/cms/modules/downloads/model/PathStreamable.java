package com.condation.cms.modules.downloads.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author thmar
 */
@RequiredArgsConstructor
@Slf4j
public class PathStreamable implements Streamable {
	
	public final Path path;

	@Override
	public InputStream getInputStream() throws IOException {
		return Files.newInputStream(path);
	}

	@Override
	public String getFileName() {
		return path.getFileName().toString();
	}

	@Override
	public long getSize() {
		try {
			return Files.size(path);
		} catch (IOException ex) {
			log.error("", ex);
		}
		return 0;
	}
	
	
}
