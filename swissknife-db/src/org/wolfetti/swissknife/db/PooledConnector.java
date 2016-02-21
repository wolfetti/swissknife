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

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.configuration.Configuration;
import org.wolfetti.swissknife.common.SKConstants;
import org.wolfetti.swissknife.db.exceptions.DbInitializationException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Classe che interroga il database sfruttando un pool di connessioni JDBC.
 *
 * @author Fabio Frijo
 */
public final class PooledConnector extends DbConnector {
	
	/**
	 * La configurazione del numero di connessioni da aprire ogni volta che viene
	 * richiesto un aumento di connessioni al connection pool.
	 */
	static int ACQUIRE_INCREMENT = 5;
	
	/**
	 * Il numero di connessioni iniziali del pool.
	 */
	static int MIN_POOL_SIZE = 5;
	
	/**
	 * Il numero di connessioni massime del pool.
	 */
	static int MAX_POOL_SIZE = 100;
	
	/**
	 * The unique instance of combo pooled datasource
	 */
	private static ComboPooledDataSource cpds = null;

	/**
	 * Creazione di un connector JDBC pooled in base alla configurazione creata applicativamente.
	 *
	 * @param config
	 * 	La configurazione della connessione
	 */
	protected PooledConnector(Configuration config) {
		super(config);
	}

	/**
	 * Creazione di un connector JDBC pooled in base alla configurazione creata applicativamente.
	 *
	 * @param config
	 * 	La configurazione della connessione
	 *
	 * @param isTransaction
	 * 	<code>true</code> se si vuole attivare la transazione, altrimenti la connessione
	 * 	avr&agrave; l'autocommit attivato
	 */
	protected PooledConnector(Configuration config, boolean isTransaction) {
		super(config, isTransaction);
	}

	/*
	 * (non-Javadoc)
	 * @see org.wolfetti.lib.db.connector.DbConnector#initConnection(org.apache.commons.configuration.Configuration)
	 */
	@Override
	protected Connection initConnection(Configuration conf)
	throws DbInitializationException {
		if(cpds != null){
			try {
				return cpds.getConnection();
			} catch (SQLException e) {
				throw new DbInitializationException("Apertura della connessione fallita", e);
			}
		}
		
		cpds = new ComboPooledDataSource();

		try {
			
			// Mandatory config options
			cpds.setDriverClass(conf.getString(SKConstants.CONF.DB.KEY_DRIVER));
			cpds.setJdbcUrl(conf.getString(SKConstants.CONF.DB.KEY_URL));
			cpds.setUser(conf.getString(SKConstants.CONF.DB.KEY_USER));
			cpds.setPassword(conf.getString(SKConstants.CONF.DB.KEY_PASSWORD));
			
			// Optional config options
			cpds.setAcquireIncrement(ACQUIRE_INCREMENT);
			cpds.setMinPoolSize(MIN_POOL_SIZE);
			cpds.setMaxPoolSize(MAX_POOL_SIZE);
			
			// Static configuration options
			cpds.setMaxIdleTime(600);

			return cpds.getConnection();
		} catch (PropertyVetoException e) {
			throw new DbInitializationException("Driver non valido: " + conf.getString(SKConstants.CONF.DB.KEY_DRIVER), e);
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
		return new PooledConnector(config, isTransaction);
	}

	/**
	 * Rilascia le connessioni utilizzate dal pool.
	 */
	public static void releaseResources() {
		if(null != cpds){
			cpds.setInitialPoolSize(0);
			cpds.setMaxPoolSize(0);
			cpds.setMaxIdleTime(60);
			cpds.resetPoolManager();
		}
	}
}
