/*
 * Copyright(c) 2013 Fabio Frijo.
 *
 * This file is part of swissknife-config.
 *
 * swissknife-config is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * swissknife-config is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with swissknife-config.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wolfetti.swissknife.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.PropertyConverter;
import org.wolfetti.swissknife.db.DbConnector;

/**
 *
 * @author Fabio Frijo
 */
public class DbConfiguration extends AbstractConfiguration {

    /** Constant for the default table name. */
	public static final String DEFAULT_TABLE_NAME = "config";

	/** Constant for the default key column name. */
	public static final String DEFAULT_KEY_COL = "key";

	/** Constant for the default value column name. */
	public static final String DEFAULT_VALUE_COL = "value";

	/** Constant for the default context column name. */
	public static final String DEFAULT_CONTEXT_COL = "context";

	/** The AbstractConnector to connect to the database. */
	private final DbConnector dbConnector;

	/** The name of the table containing the configurations. */
	private final String table;

	public final String getTableName(){
		return table;
	}

	/** The column containing the name of the configuration. (CONTEXT) */
	private final String nameColumn;

	public final String getContextColumnName(){
		return nameColumn;
	}

	/** The column containing the keys. */
	private final String keyColumn;

	public final String getKeyColumnName(){
		return keyColumn;
	}

	/** The column containing the values. */
	private final String valueColumn;

	public final String getValueColumnName(){
		return valueColumn;
	}

	/** The name of the configuration. (CONTEXT) */
	private final String name;

	public final String getContextName(){
		return name;
	}

	/** A flag whether commits should be performed by this configuration. */
	private final boolean doCommits;

	/**
	 * Build a configuration from a table containing multiple configurations.
	 * No commits are performed by the new configuration instance.
	 *
	 * @param dbConnector    the dbConnector to connect to the database
	 * @param table         the name of the table containing the configurations
	 * @param nameColumn    the column containing the name of the configuration
	 * @param keyColumn     the column containing the keys of the configuration
	 * @param valueColumn   the column containing the values of the configuration
	 * @param name          the name of the configuration
	 */
	public DbConfiguration(DbConnector dbConnector, String table, String nameColumn,
			String keyColumn, String valueColumn, String name)
	{
		this(dbConnector, table, nameColumn, keyColumn, valueColumn, name, false);
	}

	/**
	 * Creates a new instance of {@code DbConfiguration} that operates on
	 * a database table containing multiple configurations.
	 *
	 * @param dbConnector the {@code AbstractConnector} to connect to the database
	 * @param table the name of the table containing the configurations
	 * @param nameColumn the column containing the name of the configuration
	 * @param keyColumn the column containing the keys of the configuration
	 * @param valueColumn the column containing the values of the configuration
	 * @param name the name of the configuration
	 * @param commits a flag whether the configuration should perform a commit
	 *        after a database update
	 */
	public DbConfiguration(DbConnector dbConnector, String table,
			String nameColumn, String keyColumn, String valueColumn,
			String name, boolean commits)
	{
		this.dbConnector = dbConnector;
		this.table = table;
		this.nameColumn = nameColumn;
		this.keyColumn = keyColumn;
		this.valueColumn = valueColumn;
		this.name = name;
		doCommits = commits;
		this.addErrorLogListener();  // log errors per default
	}

	/**
	 * Build a configuration from a table.
	 *
	 * @param dbConnector    the dbConnector to connect to the database
	 * @param table         the name of the table containing the configurations
	 * @param keyColumn     the column containing the keys of the configuration
	 * @param valueColumn   the column containing the values of the configuration
	 */
	public DbConfiguration(DbConnector dbConnector, String table, String keyColumn, String valueColumn)
	{
		this(dbConnector, table, null, keyColumn, valueColumn, null);
	}

	/**
	 * Creates a new instance of {@code DbConfiguration} that
	 * operates on a database table containing a single configuration only.
	 *
	 * @param dbConnector the {@code AbstractConnector} to connect to the database
	 * @param table the name of the table containing the configurations
	 * @param keyColumn the column containing the keys of the configuration
	 * @param valueColumn the column containing the values of the configuration
	 * @param commits a flag whether the configuration should perform a commit
	 *        after a database update
	 */
	public DbConfiguration(DbConnector dbConnector, String table,
			String keyColumn, String valueColumn, boolean commits)
	{
		this(dbConnector, table, null, keyColumn, valueColumn, null, commits);
	}

	/**
	 * Chiude la connessione con il database.
	 */
	public void closeDbConnector(){
		this.getDbConnector().close();
	}

	/**
	 * Returns a flag whether this configuration performs commits after database
	 * updates.
	 *
	 * @return a flag whether commits are performed
	 */
	public boolean isDoCommits()
	{
		return doCommits;
	}

	/**
	 * Returns the value of the specified property. If this causes a database
	 * error, an error event will be generated of type
	 * {@code EVENT_READ_PROPERTY} with the causing exception. The
	 * event's {@code propertyName} is set to the passed in property key,
	 * the {@code propertyValue} is undefined.
	 *
	 * @param key the key of the desired property
	 * @return the value of this property
	 */
	@Override
	public Object getProperty(String key)
	{
		Object result = null;

		// build the query
		StringBuilder query = new StringBuilder("SELECT * FROM ");
		query.append("`").append(table).append("` WHERE ");
		query.append("`").append(keyColumn).append("`=?");
		if (nameColumn != null)
		{
			query.append(" AND `" + nameColumn + "`=?");
		}

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			conn = this.getConnection();

			// bind the parameters
			pstmt = conn.prepareStatement(query.toString());
			pstmt.setString(1, key);
			if (nameColumn != null)
			{
				pstmt.setString(2, name);
			}

			rs = pstmt.executeQuery();

			List<Object> results = new ArrayList<Object>();
			while (rs.next())
			{
				Object value = rs.getObject(valueColumn);
				if (this.isDelimiterParsingDisabled())
				{
					results.add(value);
				}
				else
				{
					// Split value if it contains the list delimiter
					Iterator<?> it = PropertyConverter.toIterator(value, this.getListDelimiter());
					while (it.hasNext())
					{
						results.add(it.next());
					}
				}
			}

			if (!results.isEmpty())
			{
				result = results.size() > 1 ? results : results.get(0);
			}
		}
		catch (Exception e)
		{
			this.fireError(EVENT_READ_PROPERTY, key, null, e);
		}
		finally
		{
			this.close(conn, pstmt, rs);
		}

		return result;
	}

	/**
	 * Adds a property to this configuration. If this causes a database error,
	 * an error event will be generated of type {@code EVENT_ADD_PROPERTY}
	 * with the causing exception. The event's {@code propertyName} is
	 * set to the passed in property key, the {@code propertyValue}
	 * points to the passed in value.
	 *
	 * @param key the property key
	 * @param obj the value of the property to add
	 */
	@Override
	protected void addPropertyDirect(String key, Object obj)
	{
		// build the query
		StringBuilder query = new StringBuilder("INSERT INTO " + table);
		if (nameColumn != null)
		{
			query.append(" (" + nameColumn + ", " + keyColumn + ", " + valueColumn + ") VALUES (?, ?, ?)");
		}
		else
		{
			query.append(" (" + keyColumn + ", " + valueColumn + ") VALUES (?, ?)");
		}

		Connection conn = null;
		PreparedStatement pstmt = null;

		try
		{
			conn = this.getConnection();

			// bind the parameters
			pstmt = conn.prepareStatement(query.toString());
			int index = 1;
			if (nameColumn != null)
			{
				pstmt.setString(index++, name);
			}
			pstmt.setString(index++, key);
			pstmt.setString(index++, String.valueOf(obj));

			pstmt.executeUpdate();
			this.commitIfRequired(conn);
		}
		catch (Exception e)
		{
			this.fireError(EVENT_ADD_PROPERTY, key, obj, e);
		}
		finally
		{
			// clean up
			this.close(conn, pstmt, null);
		}
	}

	/**
	 * Adds a property to this configuration. This implementation will
	 * temporarily disable list delimiter parsing, so that even if the value
	 * contains the list delimiter, only a single record will be written into
	 * the managed table. The implementation of {@code getProperty()}
	 * will take care about delimiters. So list delimiters are fully supported
	 * by {@code DbConfiguration}, but internally treated a bit
	 * differently.
	 *
	 * @param key the key of the new property
	 * @param value the value to be added
	 */
	@Override
	public void addProperty(String key, Object value)
	{
		boolean parsingFlag = this.isDelimiterParsingDisabled();
		try
		{
			if (value instanceof String)
			{
				// temporarily disable delimiter parsing
				this.setDelimiterParsingDisabled(true);
			}
			super.addProperty(key, value);
		}
		finally
		{
			this.setDelimiterParsingDisabled(parsingFlag);
		}
	}

	/**
	 * Checks if this configuration is empty. If this causes a database error,
	 * an error event will be generated of type {@code EVENT_READ_PROPERTY}
	 * with the causing exception. Both the event's {@code propertyName}
	 * and {@code propertyValue} will be undefined.
	 *
	 * @return a flag whether this configuration is empty.
	 */
	@Override
	public boolean isEmpty()
	{
		boolean empty = true;

		// build the query
		StringBuilder query = new StringBuilder("SELECT count(*) FROM `" + table + "`");
		if (nameColumn != null)
		{
			query.append(" WHERE `" + nameColumn + "`=?");
		}

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			conn = this.getConnection();

			// bind the parameters
			pstmt = conn.prepareStatement(query.toString());
			if (nameColumn != null)
			{
				pstmt.setString(1, name);
			}

			rs = pstmt.executeQuery();

			if (rs.next())
			{
				empty = rs.getInt(1) == 0;
			}
		}
		catch (Exception e)
		{
			this.fireError(EVENT_READ_PROPERTY, null, null, e);
		}
		finally
		{
			// clean up
			this.close(conn, pstmt, rs);
		}

		return empty;
	}

	/**
	 * Checks whether this configuration contains the specified key. If this
	 * causes a database error, an error event will be generated of type
	 * {@code EVENT_READ_PROPERTY} with the causing exception. The
	 * event's {@code propertyName} will be set to the passed in key, the
	 * {@code propertyValue} will be undefined.
	 *
	 * @param key the key to be checked
	 * @return a flag whether this key is defined
	 */
	@Override
	public boolean containsKey(String key)
	{
		boolean found = false;

		// build the query
		StringBuilder query = new StringBuilder("SELECT * FROM `" + table + "` WHERE `" + keyColumn + "`=?");
		if (nameColumn != null)
		{
			query.append(" AND `" + nameColumn + "`=?");
		}

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			conn = this.getConnection();

			// bind the parameters
			pstmt = conn.prepareStatement(query.toString());
			pstmt.setString(1, key);
			if (nameColumn != null)
			{
				pstmt.setString(2, name);
			}

			rs = pstmt.executeQuery();

			found = rs.next();
		}
		catch (Exception e)
		{
			this.fireError(EVENT_READ_PROPERTY, key, null, e);
		}
		finally
		{
			// clean up
			this.close(conn, pstmt, rs);
		}

		return found;
	}

	/**
	 * Removes the specified value from this configuration. If this causes a
	 * database error, an error event will be generated of type
	 * {@code EVENT_CLEAR_PROPERTY} with the causing exception. The
	 * event's {@code propertyName} will be set to the passed in key, the
	 * {@code propertyValue} will be undefined.
	 *
	 * @param key the key of the property to be removed
	 */
	@Override
	protected void clearPropertyDirect(String key)
	{
		// build the query
		StringBuilder query = new StringBuilder("DELETE FROM `" + table + "` WHERE `" + keyColumn + "`=?");
		if (nameColumn != null)
		{
			query.append(" AND " + nameColumn + "=?");
		}

		Connection conn = null;
		PreparedStatement pstmt = null;

		try
		{
			conn = this.getConnection();

			// bind the parameters
			pstmt = conn.prepareStatement(query.toString());
			pstmt.setString(1, key);
			if (nameColumn != null)
			{
				pstmt.setString(2, name);
			}

			pstmt.executeUpdate();
			this.commitIfRequired(conn);
		}
		catch (Exception e)
		{
			this.fireError(EVENT_CLEAR_PROPERTY, key, null, e);
		}
		finally
		{
			// clean up
			this.close(conn, pstmt, null);
		}
	}

	/**
	 * Removes all entries from this configuration. If this causes a database
	 * error, an error event will be generated of type
	 * {@code EVENT_CLEAR} with the causing exception. Both the
	 * event's {@code propertyName} and the {@code propertyValue}
	 * will be undefined.
	 */
	@Override
	public void clear()
	{
		this.fireEvent(EVENT_CLEAR, null, null, true);
		// build the query
		StringBuilder query = new StringBuilder("DELETE FROM `" + table + "`");
		if (nameColumn != null)
		{
			query.append(" WHERE `" + nameColumn + "`=?");
		}

		Connection conn = null;
		PreparedStatement pstmt = null;

		try
		{
			conn = this.getConnection();

			// bind the parameters
			pstmt = conn.prepareStatement(query.toString());
			if (nameColumn != null)
			{
				pstmt.setString(1, name);
			}

			pstmt.executeUpdate();
			this.commitIfRequired(conn);
		}
		catch (Exception e)
		{
			this.fireError(EVENT_CLEAR, null, null, e);
		}
		finally
		{
			// clean up
			this.close(conn, pstmt, null);
		}
		this.fireEvent(EVENT_CLEAR, null, null, false);
	}

	/**
	 * Returns an iterator with the names of all properties contained in this
	 * configuration. If this causes a database
	 * error, an error event will be generated of type
	 * {@code EVENT_READ_PROPERTY} with the causing exception. Both the
	 * event's {@code propertyName} and the {@code propertyValue}
	 * will be undefined.
	 * @return an iterator with the contained keys (an empty iterator in case
	 * of an error)
	 */
	@Override
	public Iterator<String> getKeys()
	{
		Collection<String> keys = new ArrayList<String>();

		// build the query
		StringBuilder query = new StringBuilder("SELECT DISTINCT `" + keyColumn + "` FROM `" + table + "`");
		if (nameColumn != null)
		{
			query.append(" WHERE `" + nameColumn + "`=?");
		}

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			conn = this.getConnection();

			// bind the parameters
			pstmt = conn.prepareStatement(query.toString());
			if (nameColumn != null)
			{
				pstmt.setString(1, name);
			}

			rs = pstmt.executeQuery();

			while (rs.next())
			{
				keys.add(rs.getString(1));
			}
		}
		catch (Exception e)
		{
			this.fireError(EVENT_READ_PROPERTY, null, null, e);
		}
		finally
		{
			// clean up
			this.close(conn, pstmt, rs);
		}

		return keys.iterator();
	}

	/**
	 * Returns the used {@code AbstractConnector} object.
	 *
	 * @return the data source
	 * @since 1.4
	 */
	public DbConnector getDbConnector()
	{
		return dbConnector;
	}

	/**
	 * Returns a {@code Connection} object. This method is called when
	 * ever the database is to be accessed. This implementation returns a
	 * connection from the current {@code AbstractConnector}.
	 *
	 * @return the {@code Connection} object to be used
	 * @throws SQLException if an error occurs
	 * @since 1.4
	 * class. To be removed in Commons Configuration 2.0
	 */
	private Connection getConnection()
	throws Exception {
		return this.getDbConnector().getConnection();
	}

	/**
	 * Close the specified database objects.
	 * Avoid closing if null and hide any SQLExceptions that occur.
	 *
	 * @param conn The database connection to close
	 * @param stmt The statement to close
	 * @param rs the result set to close
	 */
	private void close(Connection conn, Statement stmt, ResultSet rs)
	{
		try
		{
			if (rs != null)
			{
				rs.close();
			}
		}
		catch (SQLException e)
		{
			// TODO Implementazione logger
		}

		try
		{
			if (stmt != null)
			{
				stmt.close();
			}
		}
		catch (SQLException e)
		{
			// TODO Implementazione logger
		}

		try
		{
			if (conn != null)
			{
				conn.close();
			}
		}
		catch (SQLException e)
		{
			// TODO Implementazione logger
		}
	}

	/**
	 * Performs a commit if needed. This method is called after updates of the
	 * managed database table. If the configuration should perform commits, it
	 * does so now.
	 *
	 * @param conn the active connection
	 * @throws SQLException if an error occurs
	 */
	private void commitIfRequired(Connection conn) throws SQLException
	{
		if (this.isDoCommits())
		{
			conn.commit();
		}
	}
}
