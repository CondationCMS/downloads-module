package com.github.thmarx.cms.modules.download.counter;

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
		
		long count = counterdb.getCount("test_count", "test_site", LocalDate.now());
		
		Assertions.assertThat(count).isEqualTo(3l);
	}
	
	@Test
	public void test_multi_thread() throws InterruptedException, ExecutionException {
		
		long oldCount = counterdb.getCount("test_multi_thread", "test_site", LocalDate.now());
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
		
		long newCount = counterdb.getCount("test_multi_thread", "test_site", LocalDate.now());
		
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
		
		long count1 =  counterdb.getCount("tracking", "demo_site", LocalDate.now());
		long count2 = counterdb.getCount("api_requests", "demo_site", LocalDate.now());
		
		Assertions.assertThat(count1).isEqualTo(1000);
		Assertions.assertThat(count2).isEqualTo(1000);
		
		long countAll = counterdb.getCountAllCounters4Month("demo_site", LocalDate.now().getYear(), LocalDate.now().getMonthValue());
		
		Assertions.assertThat(countAll).isEqualTo(2000);
	}
	
}
