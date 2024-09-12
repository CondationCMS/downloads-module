package com.condation.cms.modules.downloads.counter;

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


import java.time.LocalDate;

/**
 *
 * @author t.marx
 */
public class MockCounterDB implements CounterDB {

	@Override
	public void clear(String site) {
	}

	@Override
	public void count(String download, String counter, LocalDate date, long increment) {
	}


	@Override
	public void close() throws Exception {
		
	}

	@Override
	public long getCountCurrentDay(String download, String counter) {
		return 0;
	}

	@Override
	public long getCountCurrentMonth(String download, String counter) {
		return 0;
	}

	@Override
	public long getCountCurrentYear(String download, String counter) {
		return 0;
	}

	@Override
	public long getCountAll(String download, String counter) {
		return 0;
	}

	
}
