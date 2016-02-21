/*
 * Copyright (C) Fabio Frijo.
 *
 * This file is part of swissknife-common.
 *
 * swissknife-common is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * swissknife-common is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with swissknife-common.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wolfetti.swissknife.common;

import org.wolfetti.swissknife.common.log.LogLevel;

/**
 * Swiss Knife Lib constants
 *
 * @author Fabio Frijo
 *
 */
public final class SKConstants {

	/**
	 * Private constructor, this class is a singleton.
	 */
	private SKConstants() {}

	/**
	 * System-related new line character.
	 */
	public final static String NEW_LINE = System.getProperty("line.separator");

	/**
	 * System-related file separator character.
	 */
	public final static String FILE_SEPARATOR = System.getProperty("file.separator");

	/**
	 * Tabulation character.
	 */
	public final static String TAB = "\t";

	/**
	 * Configuration constants
	 *
	 *
	 * @author Fabio Frijo
	 */
	public static abstract class CONF {

		/**
		 * Configuration prefix
		 */
		public static final String GLOBAL_PREFIX = "sk";

		/**
		 * Global (all in one) library configuration filenames
		 */
		public final static String[] GLOBAL_FILENAMES = {
			"swissknife.properties",
			"sk.properties"
		};

		/**
		 * Struts 2 system configuration constants
		 *
		 *
		 * @author Fabio Frijo
		 */
		public static abstract class STRUTS2 {

			/**
			 * Configuration prefix
			 */
			public static final String PREFIX = SKConstants.CONF.GLOBAL_PREFIX + ".struts2";

			/**
			 * Specific (per module) configuration file names
			 */
			public static final String[] FILENAMES = {
				"swissknife-struts2.properties",
				"sk-struts2.properties"
			};
		}

		/**
		 * Mailer system configuration constants
		 *
		 *
		 * @author Fabio Frijo
		 */
		public static abstract class MAILER {

			/**
			 * Configuration prefix
			 */
			public static final String PREFIX = SKConstants.CONF.GLOBAL_PREFIX + ".mailer";

			/**
			 * Specific (per module) configuration file names
			 */
			public static final String[] FILENAMES = {
				"swissknife-mailer.properties",
				"sk-mailer.properties"
			};
		}

		/**
		 * Config system configuration constants
		 *
		 *
		 * @author Fabio Frijo
		 */
		public static abstract class CONFIG {

			/**
			 * Configuration prefix
			 */
			public static final String PREFIX = SKConstants.CONF.GLOBAL_PREFIX + ".config";

			/**
			 * Specific (per module) configuration file names
			 */
			public static final String[] FILENAMES = {
				"swissknife-config.properties",
				"sk-config.properties"
			};
		}

		/**
		 * Db system configuration constants
		 *
		 *
		 * @author Fabio Frijo
		 */
		public static abstract class DB {

			/**
			 * Configuration prefix
			 */
			public static final String PREFIX = SKConstants.CONF.GLOBAL_PREFIX + ".db";

			/**
			 * La chiave di configurazione del tipo di connettore da utilizzare
			 */
			public static final String KEY_TYPE = PREFIX + ".type";

			/**
			 * La chiave di configurazione del driver JDBC
			 */
			public static final String KEY_DRIVER = PREFIX + ".driver";

			/**
			 * La chiave di configurazione dell'url
			 */
			public static final String KEY_URL = PREFIX + ".url";

			/**
			 * La chiave di configurazione dell'utente per la connessione a DB
			 */
			public static final String KEY_USER = PREFIX + ".user";

			/**
			 * La chiave di configurazione della password per la connessione a DB
			 */
			public static final String KEY_PASSWORD = PREFIX + ".password";

			/**
			 * La chiave di configurazione del contesto del connection pool
			 */
			public static final String KEY_CONTEXT = PREFIX + ".context";

			/**
			 * La chiave di configurazione della dimensione di fetch degli statements
			 */
			public static final String KEY_FETCHSIZE = PREFIX + ".fetchsize";

			/**
			 * La chiave di configurazione della dimensione minima del pool
			 */
			public static final String KEY_MIN_POOLSIZE = PREFIX + ".minPoolSize";

			/**
			 * La chiave di configurazione della dimensione massima del pool
			 */
			public static final String KEY_MAX_POOLSIZE = PREFIX + ".maxPoolSize";

			/**
			 * La chiave di configurazione della dimensione di increment del pool
			 */
			public static final String KEY_POOL_ACQUIRE_INCREMENT = PREFIX + ".poolAcquireIncrement";

			/**
			 * Le chiavi di configurazione obbligatorie per il tipo di connessione JDBC
			 */
			public static final String[] JDBC_REQUIRED = {KEY_DRIVER, KEY_URL, KEY_USER, KEY_PASSWORD};

			/**
			 * Le chiavi di configurazione obbligatorie per il tipo di connessione JNDI
			 */
			public static final String[] JNDI_REQUIRED = {KEY_CONTEXT};

			/**
			 * Le chiavi di configurazione obbligatorie per il tipo di connessione JNDI
			 */
			public static final String[] POOLED_REQUIRED = {KEY_DRIVER, KEY_URL, KEY_USER, KEY_PASSWORD};

			/**
			 * Il tipo di connettore utilizzato se non ne viene specificato uno.
			 */
			public static final String DEFAULT_TYPE = "JDBC";

			/**
			 * La classe di result set utilizzata da c3p0
			 */
			public static final String C3P0_RS_NAME = "com.mchange.v2.c3p0.impl.NewProxyResultSet";

			/**
			 * Specific (per module) configuration file names
			 */
			public static final String[] FILENAMES = {
				"swissknife-db.properties",
				"sk-db.properties"
			};
		}

		/**
		 * Log system configuration constants
		 *
		 *
		 * @author Fabio Frijo
		 */
		public static abstract class LOG {

			/**
			 * The name of class that will be tried as logging adapter if is not configured by user.
			 */
			public static final String SYSLOG_NAME = "org.wolfetti.swissknife.common.log.SystemLogger";

			/**
			 * Configuration prefix
			 */
			public static final String PREFIX = SKConstants.CONF.GLOBAL_PREFIX + ".log";

			/**
			 * Configuration key for user defined logger class
			 */
			public static final String KEY_LOGGER = PREFIX + ".logger";

			/**
			 * Configuration key for user defined SystemLogger log level
			 */
			public static final String KEY_SYSLOG_LEVEL = PREFIX + ".syslog.level";

			/**
			 * Configuration key for append the name of the log instance if so configured.
			 * <br><br>
			 * Possible values are:<pre>
			 * 	<b>NONE</b> No log name is shown.
			 *  <b>SHORT</b> Short log name is shown. Usually this is the simple class name whithout package.
			 *  <b>FULL</b> Full log name is shown. Usually this is the class name whith package.
			 * </pre>
			 */
			public static final String KEY_SYSLOG_SHOWNAME = PREFIX + ".syslog.showName";

			/**
			 * The default log level.
			 */
			public static final int DEFAULT_LEVEL = LogLevel.INFO;

			/**
			 * Specific (per module) configuration file names
			 */
			public static final String[] FILENAMES = {
				"swissknife-log.properties",
				"sk-log.properties"
			};
		}
	};
}
