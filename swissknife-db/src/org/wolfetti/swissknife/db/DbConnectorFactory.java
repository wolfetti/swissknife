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

import static org.wolfetti.swissknife.db.utils.DbConnectorUtils.getJdbcConfiguration;
import static org.wolfetti.swissknife.db.utils.DbConnectorUtils.getJndiConfiguration;
import static org.wolfetti.swissknife.db.utils.DbConnectorUtils.getPooledConfiguration;

import java.io.FileReader;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.wolfetti.swissknife.common.SKConstants;
import org.wolfetti.swissknife.common.SKConstants.CONF;
import org.wolfetti.swissknife.common.SKConstants.CONF.DB;
import org.wolfetti.swissknife.common.log.Log;
import org.wolfetti.swissknife.common.log.LogFactory;
import org.wolfetti.swissknife.common.utils.FileUtils;
import org.wolfetti.swissknife.common.utils.ReflectionUtils;
import org.wolfetti.swissknife.common.utils.StringUtils;
import org.wolfetti.swissknife.db.exceptions.DbConfigKeyException;
import org.wolfetti.swissknife.db.exceptions.DbConfigLoadException;
import org.wolfetti.swissknife.db.exceptions.DbInitializationException;

/**
 * Classe che inizializza il DbConnector e tutti gli elementi necessari.
 *
 * @author Fabio Frijo
 */
public class DbConnectorFactory {

	// Singleton
	private DbConnectorFactory(){}

	/**
	 * La singola istanza di configurazione
	 */
	private static Configuration config;

	/**
	 * Logger
	 */
	private static final Log log = LogFactory.getLog(DbConnectorFactory.class);
	

	/**
	 * Carica la configurazione dal file indicato nel path in ingresso
	 *
	 * @throws DbConfigLoadException
	 * 	Quando il file non viene caricato per qualche problema.
	 */
	public static void loadConfigurationFile(String filePath)
	throws DbConfigLoadException {
		Properties props = new Properties();
		
		try {
			props.load(new FileReader(filePath));
		} catch(Exception e){
			log.error(e.getMessage());
			throw new DbConfigLoadException(e.getMessage(), e);
		}
		
		loadConfigurationProperties(props);
	}
	

	/**
	 * Carica la configurazione dal file 'dbconnector.properties'
	 * TODO: Aggiornare javadoc!
	 *
	 * @throws DbConfigLoadException
	 * 	Quando il file non viene caricato per qualche problema.
	 */
	public static void loadConfigurationFile()
	throws DbConfigLoadException {
		Properties props = FileUtils.getPropertiesFile(
			ReflectionUtils.getContextClassLoader(DbConnectorFactory.class),
			CONF.GLOBAL_FILENAMES,
			DB.FILENAMES
		);
		
		loadConfigurationProperties(props);
	}

	/**
	 * Carica la configurazione dal file 'dbconnector.properties'
	 *
	 * @throws DbConfigLoadException
	 * 	Quando il file non viene caricato per qualche problema.
	 */
	public static void loadConfigurationProperties(Properties props)
	throws DbConfigLoadException {

		if(props == null) {
			throw new DbConfigLoadException("Unable to load configuration file.");
        }

		// FIXME Eliminare la dipendenza dalle commons -----------
		config = new PropertiesConfiguration();
		// -------------------------------------------------------

		Enumeration<?> names = props.propertyNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();

			if(name != null && name.startsWith(DB.PREFIX)){
				String value = props.getProperty(name);
				config.addProperty(name, value);
			}
		}
		
		if("POOL".equals(getConnectionType())){
			PooledConnector.ACQUIRE_INCREMENT = config.getInt(DB.KEY_POOL_ACQUIRE_INCREMENT, PooledConnector.ACQUIRE_INCREMENT);
			PooledConnector.MIN_POOL_SIZE = config.getInt(DB.KEY_MIN_POOLSIZE, PooledConnector.MIN_POOL_SIZE);
			PooledConnector.MAX_POOL_SIZE = config.getInt(DB.KEY_MAX_POOLSIZE, PooledConnector.MAX_POOL_SIZE);
			logPoolDebugInformations();
		}
	}
	
	/**
	 * Logga le informazioni di debug per il connection pool.
	 */
	private static void logPoolDebugInformations(){
		if(log.isDebugEnabled()){
			StringBuffer msg = new StringBuffer();
			
			msg.append(SKConstants.NEW_LINE);
			msg.append(SKConstants.NEW_LINE);
			msg.append("########## CONNECTION POOL DETAILS ##########");
			msg.append(SKConstants.NEW_LINE);
			msg.append("Minimal pool size = ");
			msg.append(PooledConnector.MIN_POOL_SIZE);
			msg.append(SKConstants.NEW_LINE);
			msg.append("Maximal pool size = ");
			msg.append(PooledConnector.MAX_POOL_SIZE);
			msg.append(SKConstants.NEW_LINE);
			msg.append("Acquire increment = ");
			msg.append(PooledConnector.ACQUIRE_INCREMENT);
			msg.append(SKConstants.NEW_LINE);
			msg.append("#############################################");
			msg.append(SKConstants.NEW_LINE);
			
			log.debug(msg.toString());
		}
	}

	/**
	 * Controllo che la configurazione esista
	 * e che abbia tutte le chiavi necessarie
	 */
	private static void checkConfiguration(String[] requiredKeys)
	throws DbConfigLoadException, DbConfigKeyException {
		if(config == null){
			throw new DbConfigLoadException("Configuration not loaded.");
		}

		for(String key : requiredKeys){
			if(!config.containsKey(key)){
				throw new DbConfigKeyException(key);
			}
		}
	}

	/**
	 *
	 * @param driver
	 * @param url
	 * @param user
	 * @param password
	 * @return
	 */
	public static DbConnector getJdbcConnector(String driver, String url, String user, String password) {
		return new JdbcConnector(getJdbcConfiguration(driver, url, user, password));
	}

	/**
	 *
	 * @param driver
	 * @param url
	 * @param user
	 * @param password
	 * @param isTransaction
	 * @return
	 */
	public static DbConnector getJdbcConnector(String driver, String url, String user, String password, boolean isTransaction) {
		return new JdbcConnector(getJdbcConfiguration(driver, url, user, password), isTransaction);
	}

	/**
	 *
	 * @param isTransaction
	 * @return
	 * @throws DbConfigLoadException
	 * @throws DbConfigKeyException
	 */
	private static DbConnector getJdbcConnector(boolean isTransaction)
	throws DbConfigLoadException, DbConfigKeyException {
		checkConfiguration(DB.JDBC_REQUIRED);
		return new JdbcConnector(config, isTransaction);
	}

	/**
	 *
	 * @param driver
	 * @param url
	 * @param user
	 * @param password
	 * @return
	 */
	public static DbConnector getPooledConnector(String driver, String url, String user, String password) {
		logPoolDebugInformations();
		return new PooledConnector(getPooledConfiguration(driver, url, user, password));
	}

	/**
	 *
	 * @param driver
	 * @param url
	 * @param user
	 * @param password
	 * @param isTransaction
	 * @return
	 */
	public static DbConnector getPooledConnector(String driver, String url, String user, String password, boolean isTransaction) {
		logPoolDebugInformations();
		return new PooledConnector(getPooledConfiguration(driver, url, user, password), isTransaction);
	}

	/**
	 *
	 * @param isTransaction
	 * @return
	 * @throws DbConfigLoadException
	 * @throws DbConfigKeyException
	 */
	private static DbConnector getPooledConnector(boolean isTransaction)
	throws DbConfigLoadException, DbConfigKeyException {
		checkConfiguration(DB.POOLED_REQUIRED);
		return new PooledConnector(config, isTransaction);
	}

	/**
	 *
	 * @param driver
	 * @param context
	 * @return
	 */
	public static DbConnector getJndiConnector(String driver, String context) {
		return new JndiConnector(getJndiConfiguration(driver, context));
	}

	/**
	 *
	 * @param driver
	 * @param context
	 * @param isTransaction
	 * @return
	 */
	public static DbConnector getJndiConnector(String driver, String context, boolean isTransaction) {
		return new JndiConnector(getJndiConfiguration(driver, context), isTransaction);
	}

	/**
	 *
	 * @param isTransaction
	 * @return
	 * @throws DbConfigLoadException
	 * @throws DbConfigKeyException
	 */
	private static DbConnector getJndiConnector(boolean isTransaction)
	throws DbConfigLoadException, DbConfigKeyException {
		checkConfiguration(DB.JNDI_REQUIRED);
		return new JndiConnector(config, isTransaction);
	}

	/**
	 * Istanzia un {@link DbConnector} in base alla configurazione effettuata su file.
	 * Il connettore restituito avra' il flag della transazione a <code>FALSE</code>
	 *
	 * @throws DbInitializationException
	 */
	public static DbConnector getDbConnector()
	throws DbInitializationException {
		return getDbConnector(false);
	}

	/**
	 * Istanzia un {@link DbConnector} in base alla configurazione effettuata su file.
	 *
	 * @throws DbInitializationException
	 */
	public static DbConnector getDbConnector(boolean isTransaction)
	throws DbInitializationException {
		if(config == null){
			loadConfigurationFile();
		}

		String type = getConnectionType();
		
		switch (type) {
			case "JDBC": return getJdbcConnector(isTransaction);
			case "JNDI": return getJndiConnector(isTransaction);
			case "POOL": return getPooledConnector(isTransaction);
		}

		throw new DbInitializationException("Tipo di connettore non riconosciuto: " + type);
	}
	
	/**
	 * Restituisce il tipo di connessione utilizzata
	 * 
	 * @return
	 */
	private static String getConnectionType(){
		if(config == null){
			loadConfigurationFile();
		}
		
		String type = config.getString(DB.KEY_TYPE);

		if(StringUtils.isEmptyOrNull(type)){
			log.warn(
				"Nessun tipo di connector specificato in configurazione. " +
				"Verra' utilizzato quello di default: " +
				DB.DEFAULT_TYPE
			);

			type = DB.DEFAULT_TYPE;
		} else {
			type = type.trim().toUpperCase();
		}
		
		return type;
	}
	
	/**
	 * Metodo che rilascia le risorse utilizzate
	 */
	public static void releaseResources(){
		String type = getConnectionType();
		switch (type) {
			case "JDBC": 
			case "JNDI": 
				log.debug("Release resources is not necessary for type " + type);
				break;
			case "POOL": 
				PooledConnector.releaseResources();
				break;
		}
	}
}
