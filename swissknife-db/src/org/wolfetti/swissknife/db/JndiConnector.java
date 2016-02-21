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
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.configuration.Configuration;
import org.wolfetti.swissknife.common.SKConstants;
import org.wolfetti.swissknife.db.exceptions.DbInitializationException;

/**
 * Classe che interroga il database sfruttando una connessione JNDI.
 *
 * @author Fabio Frijo
 *
 */
public final class JndiConnector extends DbConnector {
	private DataSource ds;

	/**
	 * Creazione di un connector JNDI in base alla configurazione creata applicativamente.
	 *
	 * @param config
	 * 	La configurazione della connessione
	 */
	public JndiConnector(Configuration config) {
		super(config);
	}

	/**
	 * Creazione di un connector JNDI in base alla configurazione creata applicativamente.
	 *
	 * @param config
	 * 	La configurazione della connessione
	 *
	 * @param isTransaction
	 * 	<code>true</code> se si vuole attivare la transazione, altrimenti la connessione
	 * 	avr&agrave; l'autocommit attivato
	 */
	public JndiConnector(Configuration config, boolean isTransaction) {
		super(config, isTransaction);
	}

	/*
	 * (non-Javadoc)
	 * @see org.wolfetti.lib.db.connector.DbConnector#initConnection(org.apache.commons.configuration.Configuration)
	 */
	@Override
	protected Connection initConnection(Configuration conf)
	throws DbInitializationException {
		String ctxName = conf.getString(SKConstants.CONF.DB.KEY_CONTEXT);
		try {
			InitialContext ic = new InitialContext();
			ds = (DataSource) ic.lookup(ctxName);
			return ds.getConnection();
		}
		catch (NamingException e) {
			throw new DbInitializationException("Context non valido: " + ctxName, e);
		}
		catch (SQLException e) {
			throw new DbInitializationException("Apertura della connessione fallita", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.wolfetti.lib.db.connector.DbConnector#clone(org.apache.commons.configuration.Configuration, boolean)
	 */
	@Override
	protected DbConnector clone(Configuration config, boolean isTransaction) {
		return new JndiConnector(config, isTransaction);
	}
}
