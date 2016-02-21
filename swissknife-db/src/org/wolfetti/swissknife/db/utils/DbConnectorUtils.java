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
package org.wolfetti.swissknife.db.utils;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.wolfetti.swissknife.common.SKConstants;

/**
 * Utilities del db connector
 *
 * @author Fabio Frijo
 *
 */
public class DbConnectorUtils {
	private DbConnectorUtils() {}

	/**
	 * Creazione di un oggetto di configurazione per una connessione JDBC.
	 *
	 * @param driver
	 * 	La classe del driver da utilizzare
	 *
	 * @param url
	 * 	L'url del database server
	 *
	 * @param user
	 * 	Il nome dell'utente del db
	 *
	 * @param password
	 *  La password dell'utente del db
	 */
	public static Configuration getJdbcConfiguration(String driver, String url, String user, String password){
		Configuration conf = new BaseConfiguration();
		conf.addProperty(SKConstants.CONF.DB.KEY_DRIVER, driver);
		conf.addProperty(SKConstants.CONF.DB.KEY_URL, url);
		conf.addProperty(SKConstants.CONF.DB.KEY_USER, user);
		conf.addProperty(SKConstants.CONF.DB.KEY_PASSWORD, password);
		return conf;
	}

	/**
	 * Creazione di un oggetto di configurazione per una connessione JDBC pooled.
	 *
	 * @param driver
	 * 	La classe del driver da utilizzare
	 *
	 * @param url
	 * 	L'url del database server
	 *
	 * @param user
	 * 	Il nome dell'utente del db
	 *
	 * @param password
	 *  La password dell'utente del db
	 */
	public static Configuration getPooledConfiguration(String driver, String url, String user, String password){
		Configuration conf = new BaseConfiguration();
		conf.addProperty(SKConstants.CONF.DB.KEY_DRIVER, driver);
		conf.addProperty(SKConstants.CONF.DB.KEY_URL, url);
		conf.addProperty(SKConstants.CONF.DB.KEY_USER, user);
		conf.addProperty(SKConstants.CONF.DB.KEY_PASSWORD, password);
		return conf;
	}

	/**
	 * Creazione di un oggetto di configurazione per una connessione JDBC.
	 *
	 * @param driver
	 * 	La classe del driver da utilizzare
	 *
	 * @param context
	 * 	Il contesto JNDI dal quale recuperare la connessione
	 */
	public static Configuration getJndiConfiguration(String driver, String context){
		Configuration conf = new BaseConfiguration();
		conf.addProperty(SKConstants.CONF.DB.KEY_DRIVER, driver);
		conf.addProperty(SKConstants.CONF.DB.KEY_CONTEXT, context);
		return conf;
	}
}
