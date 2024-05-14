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
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.concurrent.locks.ReentrantLock;
import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
public class H2CounterDB implements CounterDB {

	private static final Logger LOGGER = LoggerFactory.getLogger(H2CounterDB.class);

	private final File path;

	private JdbcConnectionPool pool;

	private final ReentrantLock createCounterLock = new ReentrantLock();

	public H2CounterDB(final File path) {
		this.path = path;
	}

	public void open() {
		try {
			Class.forName("org.h2.Driver");
			File dbFile = new File(path, "counter.db");
			pool = JdbcConnectionPool.create("jdbc:h2:" + dbFile.getAbsolutePath(), "sa", "sa");
			init();

		} catch (ClassNotFoundException ex) {
			LOGGER.error("", ex);
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Close the entities instance and shutdown the database connection.
	 */
	public void close() {
		defrag();
		pool.dispose();
	}

	private void defrag() {
		try (
				Connection connection = pool.getConnection(); Statement st = connection.createStatement()) {

			st.execute("shutdown defrag");

		} catch (SQLException ex) {
			throw new RuntimeException(ex);

		}
	}

	private void init() {
		try (Connection connection = pool.getConnection(); Statement st = connection.createStatement()) {
			st.execute("CREATE TABLE IF NOT EXISTS counters "
					+ "(counter VARCHAR(255), download VARCHAR(255), counter_year SMALLINT, counter_month TINYINT, counter_day TINYINT, counter_value long, "
					+ "PRIMARY KEY(counter, download, counter_year, counter_month, counter_day)"
					+ ")");
			connection.commit();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void clear(final String download) {
		try (Connection connection = pool.getConnection(); PreparedStatement st = connection.prepareStatement("DELETE FROM counters WHERE download = ?")) {
			st.setString(1, download);
			st.execute();
			connection.commit();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void ensureCounterExists(final Connection connection, final String counter, final String download, final LocalDate date) throws SQLException {
		if (!counter_exists(counter, download, date)) {
			try {
				createCounterLock.lock();
				if (!counter_exists(counter, download, date)) {
					try (PreparedStatement statement = connection.prepareStatement("INSERT INTO counters (counter, download, counter_year, counter_month, counter_day, counter_value) "
							+ "VALUES (?, ?, ?, ?, ?, 0)")) {
						statement.setString(1, counter);
						statement.setString(2, download);
						statement.setInt(3, date.getYear());
						statement.setInt(4, date.getMonthValue());
						statement.setInt(5, date.getDayOfMonth());

						statement.executeUpdate();
					}
				}
			} finally {
				createCounterLock.unlock();
			}
		}

	}

	@Override
	public void count(final String counter, final String download, final LocalDate date, final long increment) {
		try (Connection connection = pool.getConnection(); PreparedStatement update = connection.prepareStatement(
				"UPDATE counters SET counter_value = counter_value + ?  WHERE counter = ? AND download = ? AND counter_year = ? AND counter_month = ? AND counter_day = ?")) {

			ensureCounterExists(connection, counter, download, date);

			// update
			update.setLong(1, increment);
			update.setString(2, counter);
			update.setString(3, download);
			update.setInt(4, date.getYear());
			update.setInt(5, date.getMonthValue());
			update.setInt(6, date.getDayOfMonth());

			update.executeUpdate();

			connection.commit();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public long getCount(final String counter, final String download, final LocalDate date) {
		try (Connection connection = pool.getConnection(); PreparedStatement select = connection.prepareStatement(
				"select counter_value as counter_value from counters WHERE counter = ? AND download = ? AND counter_year = ? AND counter_month = ? AND counter_day = ?")) {
			select.setString(1, counter);
			select.setString(2, download);
			select.setInt(3, date.getYear());
			select.setInt(4, date.getMonthValue());
			select.setInt(5, date.getDayOfMonth());

			ResultSet result = select.executeQuery();

			if (result.next()) {
				return result.getLong("counter_value");
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return 0;
	}

	@Override
	public long getCountAllCounters4Month(final String download, final int year, final int month) {
		try (Connection connection = pool.getConnection(); PreparedStatement select = connection.prepareStatement(
				"select sum(counter_value) as counter_value from counters WHERE download = ? AND counter_year = ? AND counter_month = ?")) {
			select.setString(1, download);
			select.setInt(2, year);
			select.setInt(3, month);

			ResultSet result = select.executeQuery();

			if (result.next()) {
				return result.getLong("counter_value");
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return 0;
	}

	private boolean counter_exists(final String counter, final String download, final LocalDate date) {
		try (Connection connection = pool.getConnection(); PreparedStatement select = connection.prepareStatement(
				"select counter_value as counter_value from counters WHERE counter = ? AND download = ? AND counter_year = ? AND counter_month = ? AND counter_day = ?")) {
			select.setString(1, counter);
			select.setString(2, download);
			select.setInt(3, date.getYear());
			select.setInt(4, date.getMonthValue());
			select.setInt(5, date.getDayOfMonth());

			ResultSet result = select.executeQuery();

			if (result.next()) {
				return true;
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
		return false;
	}
}
