package com.github.thmarx.cms.modules.download.counter;

import java.time.LocalDate;

/**
 *
 * @author marx
 */
public interface CounterDB {

	void clear(final String site);

	void count(final String counter, final String site, final LocalDate date, final long increment);

	long getCount(final String counter, final String site, final LocalDate date);

	long getCountAllCounters4Month(final String site, final int year, final int month);
	
}
