/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
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
import com.condation.cms.api.db.DBFileSystem;
import com.condation.cms.modules.downloads.model.PathStreamable;
import com.condation.cms.modules.downloads.model.URLStreamable;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 *
 * @author thmar
 */
@ExtendWith(MockitoExtension.class)
public class DownloadResolverTest {

	@Mock
	DB db;

	@Mock
	DBFileSystem fileSystem;

	static HttpServer server;

	DownloadResolver sut;

	@BeforeAll
	public static void startServer() {
		InetSocketAddress address = new InetSocketAddress(8888);
		Path path = Path.of("server_root").toAbsolutePath();
		server = SimpleFileServer.createFileServer(address, path, SimpleFileServer.OutputLevel.VERBOSE);
		server.start();
	}

	@AfterAll
	public static void stopServer() {
		server.stop(0);
	}

	@BeforeEach
	public void setup() {
		Mockito.when(db.getFileSystem()).thenReturn(fileSystem);
		Mockito.when(fileSystem.resolve("downloads")).thenReturn(Path.of("downloads/"));

		sut = new DownloadResolver(db);
	}

	@Test
	public void local_download() throws IOException {

		var streamable = sut.resolve("license.zip");

		Assertions.assertThat(streamable)
				.isPresent();
		Assertions.assertThat(streamable.get()).isInstanceOf(PathStreamable.class);
		Assertions.assertThat(streamable.get().getFileName()).isEqualTo("license.zip");
		Assertions.assertThat(streamable.get().getSize()).isEqualTo(12401);
		try (var stream = streamable.get().getInputStream();) {
			Assertions.assertThat(stream).isNotNull().isNotEmpty();
		}
	}

	@Test
	public void remote_download() throws IOException {

		var streamable = sut.resolve("test");

		Assertions.assertThat(streamable)
				.isPresent();
		Assertions.assertThat(streamable.get()).isInstanceOf(URLStreamable.class);
		Assertions.assertThat(streamable.get().getFileName()).isEqualTo("test.zip");
		Assertions.assertThat(streamable.get().getSize()).isEqualTo(12401);
		try (var stream = streamable.get().getInputStream();) {
			Assertions.assertThat(stream).isNotNull().isNotEmpty();
		}
	}

}
