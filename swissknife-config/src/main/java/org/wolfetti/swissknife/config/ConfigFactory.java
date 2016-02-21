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

import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.wolfetti.swissknife.config.exceptions.AlreadyLoadedException;
import org.wolfetti.swissknife.config.exceptions.LoadFault;
import org.wolfetti.swissknife.config.exceptions.ReloadFault;
import org.wolfetti.swissknife.db.DbConnector;

/**
 * Classe che istanzia e gestisce un unica configurazione globale applicativa.
 *
 * @author Fabio Frijo
 */
public class ConfigFactory {
	private static final int TYPE_PROPERTY = 0;
	private static final int TYPE_DATABASE = 1;

	private static int initializedType = -1;

	private static boolean reloadEnabled = false;
	
	private static Properties props;

	private static class DbReloader extends Reloader {

		@Override
		void reload() {
			DbConfiguration oldConf = (DbConfiguration) conf;

			conf = new DbConfiguration(
				oldConf.getDbConnector(),
				oldConf.getTableName(),
				oldConf.getContextColumnName(),
				oldConf.getKeyColumnName(),
				oldConf.getValueColumnName(),
				oldConf.getContextName()
			);

			oldConf = null;

			try {
				onConfigLoaded(TYPE_DATABASE);
			} catch (LoadFault e) {
				throw new ReloadFault("Errore durante la reinizializzazione della configurazione", e);
			}
		}
	};

	/**
	 * La singola istanza di configurazione.
	 */
	private static Configuration conf = null;

	/**
	 * Restituisce l'unica istanza di configurazione.
	 */
	public static Configuration getConfig() {
		return conf;
	}

	/**
	 * Controllo che non si stia cercando di ricaricare l'istanza.
	 */
	private static void initializationCheck()
	throws AlreadyLoadedException {
		if(conf != null){
			throw new AlreadyLoadedException();
		}
	}

	public static void enableAutoReload()
	throws LoadFault {
		if(conf == null){
			throw new LoadFault("La configurazione non è stata inizializzata!");
		}

		switch (initializedType) {
			case TYPE_PROPERTY:
				((PropertiesConfiguration) conf).setReloadingStrategy(new FileChangedReloadingStrategy());
			break;

			case TYPE_DATABASE:
				Reloader.start(new DbReloader());
			break;

			default:
				throw new IllegalArgumentException("Type non implementato! ["+initializedType+"]");
		}

		reloadEnabled = true;
	}

	/**
	 * Operazioni da eseguire ogni volta che la configurazione viene caricata.
	 * @throws LoadFault
	 *
	 * @throws ConfigurationException
	 * 	Quando la configurazione &egrave; <code>null</code>
	 */
	private static void onConfigLoaded(int type)
	throws LoadFault {

		// Se la configurazione e' null creo un eccezione.
		if(conf == null){
			throw new LoadFault("La configurazione e' NULL.");
		}
		
		@SuppressWarnings("unchecked")
		Iterator<String> keys = conf.getKeys();
		props = new Properties();
		while(keys.hasNext()){
			String key = keys.next();
			props.setProperty(key, conf.getString(key));
		}

		initializedType = type;
	}

	/**
	 * Rilascia tutte le risorse occupate e distrugge l'oggetto di configurazione
	 */
	public static void close(){
		if(reloadEnabled){
			Reloader.stop();
			reloadEnabled = false;
		}

		switch (initializedType) {
			case TYPE_DATABASE:
				((DbConfiguration) conf).closeDbConnector();
			break;
		}

		conf = null;
	}

	/**
	 * Inizializza la configurazione presente su database.
	 *
	 * @param conn
	 * 	L'{@link DbConnector} per connettersi al database.
	 *
	 * @param tableName
	 * 	Il nome della tabella di configurazione.
	 *
	 * @param keyCol
	 * 	Il nome della colonna contenente la chiave.
	 *
	 * @param valueCol
	 * 	Il nome della colonna contenente il valore.
	 */
	public static void initDatabase(DbConnector conn, String tableName, String keyCol, String valueCol)
	throws LoadFault {
		initializationCheck();
		conf = new DbConfiguration(conn, tableName, keyCol, valueCol);
		onConfigLoaded(TYPE_DATABASE);
	}

	/**
	 * Inizializza la configurazione presente su database.
	 *
	 * @param conn
	 * 	L'{@link DbConnector} per connettersi al database.
	 *
	 * @param tableName
	 * 	Il nome della tabella di configurazione.
	 *
	 * @param keyCol
	 * 	Il nome della colonna contenente la chiave.
	 *
	 * @param valueCol
	 * 	Il nome della colonna contenente il valore.
	 *
	 * @param contextCol
	 * 	Il nome della colonna contenente il contesto applicativo.
	 *
	 * @param context
	 * 	Il contesto applicativo da utilizzare.
	 */
	public static void initDatabase(DbConnector conn, String tableName, String context, String contextCol, String keyCol, String valueCol)
	throws LoadFault {
		initializationCheck();
		conf = new DbConfiguration(conn, tableName, contextCol, keyCol, valueCol, context);
		onConfigLoaded(TYPE_DATABASE);
	}

	/**
	 * Inizializza la configurazione presente su database, utilizzando come nomi (tabella e colonne)
	 * i defaults definiti nel file 'ddl_configuration.sql', presente nella cartella 'resources/sql'
	 * del progetto 'commons-lib'.
	 *
	 * @param conn
	 * 	L'{@link DbConnector} per connettersi al database.
	 */
	public static void initDatabase(DbConnector conn)
	throws LoadFault {
		initDatabase(
			conn, DbConfiguration.DEFAULT_TABLE_NAME,
			DbConfiguration.DEFAULT_KEY_COL, DbConfiguration.DEFAULT_VALUE_COL
		);
	}

	/**
	 * Inizializza la configurazione presente su database, utilizzando come nomi (tabella e colonne)
	 * i defaults definiti nel file 'ddl_configuration.sql', presente nella cartella 'resources/sql'
	 * del progetto 'commons-lib'.
	 *
	 * @param conn
	 * 	L'{@link DbConnector} per connettersi al database.
	 *
	 * @param context
	 * 	Il contesto applicativo da utilizzare.
	 */
	public static void initDatabase(DbConnector conn, String context)
	throws LoadFault {
		initDatabase(
			conn, DbConfiguration.DEFAULT_TABLE_NAME,
			context, DbConfiguration.DEFAULT_CONTEXT_COL,
			DbConfiguration.DEFAULT_KEY_COL, DbConfiguration.DEFAULT_VALUE_COL
		);
	}

	/**
	 * Lettura del file di configurazione applicativo
	 */
	public static void initProperties(String fileName)
	throws LoadFault {
		initializationCheck();

		try {
			PropertiesConfiguration p = new PropertiesConfiguration(fileName);
			p.setReloadingStrategy(new FileChangedReloadingStrategy());
			p.addConfigurationListener(new ConfigurationListener() {
				
				@Override
				public void configurationChanged(ConfigurationEvent event) {
					try {
						onConfigLoaded(TYPE_PROPERTY);
					} catch (LoadFault e) {
						e.printStackTrace();
					}
				}
			});
			
			conf = p;
			onConfigLoaded(TYPE_PROPERTY);
		} catch (ConfigurationException e) {
			throw new LoadFault("Errore durante il recupero del file '" + fileName + "'", e);
		}
	}
	
	public static Properties getProperties(){
		return props;
	}

	// Singleton
	private ConfigFactory() {}
}
