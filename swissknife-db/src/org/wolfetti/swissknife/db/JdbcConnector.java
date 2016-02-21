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
package org.wolfetti.swissknife.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.configuration.Configuration;
import org.wolfetti.swissknife.common.SKConstants;
import org.wolfetti.swissknife.db.exceptions.DbInitializationException;

/**
 * Classe che interroga il database sfruttando una connessione JDBC.
 *
 * @author Fabio Frijo
 */
public final class JdbcConnector extends DbConnector {

	/**
	 * Creazione di un connector JDBC in base alla configurazione creata applicativamente.
	 *
	 * @param config
	 * 	La configurazione della connessione
	 */
	public JdbcConnector(Configuration config){
		super(config);
	}

	/**
	 * Creazione di un connector JDBC in base alla configurazione creata applicativamente.
	 *
	 * @param config
	 * 	La configurazione della connessione
	 *
	 * @param isTransaction
	 * 	<code>true</code> se si vuole attivare la transazione, altrimenti la connessione
	 * 	avr&agrave; l'autocommit attivato
	 */
	public JdbcConnector(Configuration config, boolean isTransaction){
		super(config, isTransaction);
	}

	/*
	 * (non-Javadoc)
	 * @see org.wolfetti.lib.db.connector.DbConnector#initConnection(org.apache.commons.configuration.Configuration)
	 */
	@Override
	protected Connection initConnection(Configuration conf)
	throws DbInitializationException {
		String driver = conf.getString(SKConstants.CONF.DB.KEY_DRIVER);
		try {
			Class.forName(driver).newInstance();
			return DriverManager.getConnection(
				conf.getString(SKConstants.CONF.DB.KEY_URL),
				conf.getString(SKConstants.CONF.DB.KEY_USER),
				conf.getString(SKConstants.CONF.DB.KEY_PASSWORD)
			);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new DbInitializationException("Driver non valido: " + driver, e);
		} catch (SQLException e) {
			throw new DbInitializationException("Apertura della connessione fallita", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.wolfetti.lib.db.connector.DbConnector#clone(org.apache.commons.configuration.Configuration, boolean)
	 */
	@Override
	protected DbConnector clone(Configuration config, boolean isTransaction) {
		return new JdbcConnector(config, isTransaction);
	}
}
