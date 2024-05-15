package com.github.thmarx.cms.modules.download.counter;

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

import java.io.File;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;



/**
 *
 * @author t.marx
 */
public class H2CounterDBTest {
	
	private static H2CounterDB counterdb;

	@BeforeAll
	public static void setup () {
		counterdb = new H2CounterDB(new File("target/counter-" + System.currentTimeMillis()));
		counterdb.open();
	}
	
	@AfterAll
	public static void tearDown () {
		counterdb.close();
	}
	
	@Test
	public void test_count() {
		counterdb.count("test_count", "test_site", LocalDate.now(), 1);
		counterdb.count("test_count", "test_site", LocalDate.now(), 1);
		counterdb.count("test_count", "test_site", LocalDate.now(), 1);
		
		long count = counterdb.getCountAll("test_count", "test_site");
		
		Assertions.assertThat(count).isEqualTo(3l);
	}
	
	@Test
	public void test_multi_thread() throws InterruptedException, ExecutionException {
		
		long oldCount = counterdb.getCountAll("test_multi_thread", "test_site");
		int [] counts = new int [] {10, 3, 5, 10};
		
		long increment = 0;
		CompletableFuture [] futures = new CompletableFuture[counts.length];
		for (int i = 0; i < counts.length; i++) {
			final int iterations = counts[i];
			increment += counts[i];
			futures[i] = CompletableFuture.runAsync(() -> {
				for (int j = 0; j < iterations; j++) {
					counterdb.count("test_multi_thread", "test_site", LocalDate.now(), 1);
				}
						
			});
			
		}
		CompletableFuture.allOf(futures).get();
		
		long newCount = counterdb.getCountAll("test_multi_thread", "test_site");
		
		Assertions.assertThat(newCount).isEqualTo((oldCount + increment));
	}
	
	@Test
	public void test_multiple () {
		final String [] counters = new  String [] {"tracking", "api_requests"};
		
		for (String counter : counters) {
			for (int i = 0; i < 1000; i++) {
				counterdb.count(counter, "demo_site", LocalDate.now(), 1);
			}
		}
		
		long count1 =  counterdb.getCountAll("tracking", "demo_site");
		long count2 = counterdb.getCountAll("api_requests", "demo_site");
		
		Assertions.assertThat(count1).isEqualTo(1000);
		Assertions.assertThat(count2).isEqualTo(1000);
	}
	
}
