/*
 * Copyright(c) 2013 Fabio Frijo.
 *
 * This file is part of swissknife-db.
 *
 * swissknife-db is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * swissknife-db is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with swissknife-db.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wolfetti.swissknife.db.dao;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.wolfetti.swissknife.common.utils.FileUtils;
import org.wolfetti.swissknife.common.utils.ReflectionUtils;
import org.wolfetti.swissknife.db.DbConnector;
import org.wolfetti.swissknife.db.beanutils.RsToBeanConverter;
import org.wolfetti.swissknife.db.beanutils.RsToMapConverter;
import org.wolfetti.swissknife.db.entities.Search;
import org.wolfetti.swissknife.db.exceptions.ConverterException;
import org.wolfetti.swissknife.db.exceptions.DuplicatedKeyException;
import org.wolfetti.swissknife.db.exceptions.InvalidFileException;
import org.wolfetti.swissknife.db.exceptions.SqlKeyException;
import org.wolfetti.swissknife.db.exceptions.SqlQueryException;
import org.wolfetti.swissknife.db.utils.SqlFormatter;

/**
 * Dao che prende in ingresso un property file ed esegue le query
 *
 * @author Fabio Frijo
 *
 */
public class PropertyFileDAO extends SkDAO {

	/**
	 * Nome del file delle query
	 */
	public static final String DEFAULT_SQL_FILE_NAME = "sql.properties";

	/**
	 * Il file contenente le query
	 */
	private Properties sqlQueries;

	/**
	 * Il nome del file delle query
	 */
	private String filename;

	/**
	 * Inizializzazione del DAO con le query presenti nel file 'sql.properties'.
	 *
	 * @param connector
	 * 	Il connettore per interrogare il DB
	 */
	public PropertyFileDAO(DbConnector connector){
		this(DEFAULT_SQL_FILE_NAME, connector);
	}

	/**
	 * Inizializzazione del DAO con le query presenti nel file indicato in ingresso.
	 *
	 * @param filename
	 * 	Il nome del file delle query
	 *
	 * @param connector
	 * 	Il connettore per interrogare il DB
	 */
	public PropertyFileDAO(String filename, DbConnector connector){
		super(connector);

		this.filename = filename;

		ClassLoader cl = ReflectionUtils.getContextClassLoader(this.getClass());
		this.sqlQueries = FileUtils.getPropertiesFile(cl, filename);

		if(null == this.sqlQueries){
			throw new InvalidFileException("Unable to find file '" + filename + "' in classpath.");
		}

		else if(this.sqlQueries.isEmpty()){
			this.log.warn("The SQL container file '" + filename + "' is empty.");
		}
	}

	/**
	 * Effettua una query controllando l'esistenza della stessa all'interno del file di properties
	 * e restituisce il {@link ResultSet} contenente il risultato.
	 *
	 * @param key
	 * @param values
	 * @return
	 * @throws SqlQueryException
	 */
	protected ResultSet runQuery(String key, Object... values)
	throws SqlQueryException {
		this.checkSqlKey(key);
		String sql = SqlFormatter.format(this.getSql(key), values);

		if(this.isPagingEnabled()){
			sql = this.addPagination(sql);
		}

		return this.connector.query(sql);
	}

	/**
	 * Esegue una SELECT e restituisce una lista di risultati, effettuando
	 * una ricerca tra i valori del database.
	 * @throws SqlQueryException
	 */
	protected ResultSet runSearch(Search searchObject, String key, Object... values)
	throws SqlQueryException {
		this.checkSqlKey(key);
		String sql = SqlFormatter.format(this.getSql(key), values);

		if(searchObject != null){
			this.log.debug("Oggetto di filtraggio: " + searchObject.toString());

			if(searchObject.isValid()){
				sql += searchObject.getQuery();
			}
		}

		if(this.isPagingEnabled()){
			sql = this.addPagination(sql);
		}

		return this.connector.query(sql);
	}

	/**
	 * Restituisce una lista di mappe rappresentanti un record su database
	 *
	 * @param key
	 * @param values
	 * @return
	 * @throws SqlQueryException
	 */
	public List<Map<String, Object>> getList(String key, Object ... values)
	throws SqlQueryException {
		ResultSet rs = this.runQuery(key, values);
		return RsToMapConverter.getList(rs);
	}

	/**
	 * Restituisce una lista di mappe rappresentanti un record su database
	 *
	 * @param key
	 * @param values
	 * @return
	 * @throws SqlQueryException
	 * @throws ConverterException
	 */
	public <T> List<T> getList(String key, Class<T> beanClass, Object ... values)
	throws SqlQueryException, ConverterException {
		ResultSet rs = this.runQuery(key, values);
		return RsToBeanConverter.getList(rs, beanClass);
	}

	/**
	 * Restituisce una mappa rappresentante un record su database.
	 *
	 * @param key
	 * @param values
	 * @return
	 * @throws SqlQueryException
	 * @throws SQLException
	 * @throws ConverterException
	 */
	public <T> T getSingle(String key, Class<T> beanClass, Object ... values)
	throws SqlQueryException, ConverterException {
		ResultSet rs = this.runQuery(key, values);
		return RsToBeanConverter.getSingle(rs, beanClass);
	}

	/**
	 * Restituisce una mappa rappresentante un record su database.
	 *
	 * @param key
	 * @param values
	 * @return
	 * @throws SqlQueryException
	 */
	public Map<String, Object> getSingle(String key, Object ... values)
	throws SqlQueryException {
		ResultSet rs = this.runQuery(key, values);
		return RsToMapConverter.getSingle(rs);
	}

	/**
	 * Scrive uno o pi&ugrave; records su database
	 *
	 * @param key
	 * @param values
	 * @throws DuplicatedKeyException
	 * @throws SqlQueryException
	 */
	public void write(String key, Object ... values)
	throws DuplicatedKeyException, SqlQueryException {
		this.checkSqlKey(key);
		this.connector.write(SqlFormatter.format(this.getSql(key), values));
	}

	/**
	 * Scrive uno o pi&ugrave; records su database, salvando eventuali files
	 *
	 * @param key
	 * @param files
	 * @param values
	 * @throws InvalidFileException
	 * @throws DuplicatedKeyException
	 * @throws SqlQueryException
	 */
	public void writeWithFile(String key, File[] files, Object ... values)
	throws InvalidFileException, DuplicatedKeyException, SqlQueryException {
		this.checkSqlKey(key);
		this.connector.write(SqlFormatter.format(this.getSql(key), values), files);
	}

	/**
	 * Scrive uno o pi&ugrave; records su database, salvando eventuali files (in formato byte array)
	 *
	 * @param key
	 * @param filesBytes
	 * @param values
	 * @throws InvalidFileException
	 * @throws DuplicatedKeyException
	 * @throws SqlQueryException
	 */
	public void writeWithFileBytes(String key, byte[][] filesBytes, Object ... values)
	throws InvalidFileException, DuplicatedKeyException, SqlQueryException {
		this.checkSqlKey(key);
		this.connector.write(SqlFormatter.format(this.getSql(key), values), filesBytes);
	}

	/**
	 * Esegue una SELECT e restituisce una lista di risultati, effettuando
	 * una ricerca tra i valori del database.
	 * @throws SqlQueryException
	 */
	public <T> List<T> search(Search searchObject, String key, Class<T> beanClass, Object ... values)
	throws SqlQueryException, ConverterException {
		ResultSet rs = this.runSearch(searchObject, key, values);
		return RsToBeanConverter.getList(rs, beanClass);
	}

	/**
	 * Esegue una SELECT e restituisce una lista di risultati, effettuando
	 * una ricerca tra i valori del database.
	 * @throws SqlQueryException
	 */
	public List<Map<String, Object>> search(Search searchObject, String key, Object... values)
	throws SqlQueryException{
		ResultSet rs = this.runSearch(searchObject, key, values);
		return RsToMapConverter.getList(rs);
	}

	/**
	 * Controlla l'esistenza della query indicata con la chiave in ingresso.
	 *
	 * @param key
	 * @throws SqlKeyException
	 */
	protected void checkSqlKey(String key)
	throws SqlKeyException {
		if(!this.sqlQueries.containsKey(key)){
			String message =
				"Impossibile eseguire la query identificata con la chiave '" + key +
				"': query non presente nel file " + this.filename
			;

			throw new SqlKeyException(key, message);
		}
	}

	/**
	 * Restituisce la query associata alla chiave {@code key}.
	 *
	 * @param key
	 * @return
	 */
	public String getSql(String key){
		return this.sqlQueries.getProperty(key);
	}

	/**
	 * Restituisce l'oggetto {@link Properties} che contiene tutte le query
	 *
	 * @return the sqlQueries
	 */
	public Properties getSqlQueries() {
		return this.sqlQueries;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return this.filename;
	}
}
