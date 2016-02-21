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
package org.wolfetti.swissknife.config.exceptions;

import org.wolfetti.swissknife.common.business.ApplicationRuntimeException;
import org.wolfetti.swissknife.common.utils.StringUtils;

/**
 * Eccezione sollevata quando manca un elemento del file di configurazione
 *
 * @author Fabio Frijo
 */
public class MissingConfigurationKeyException extends ApplicationRuntimeException {
	private static final long serialVersionUID = 8010229744489293513L;

	private final static String MESSAGE = "L'opzione di configurazione di configurazione [{0}] non esiste!!!";

	private String key;

	/**
	 * @param key
	 */
	public MissingConfigurationKeyException(String key) {
		super(StringUtils.format(MESSAGE, key));
		this.key = key;
	}

	/**
	 * @param key
	 * @param message
	 */
	public MissingConfigurationKeyException(String key, String message) {
		super(message);
		this.key = key;
	}

	/**
	 * @param key
	 * @param cause
	 */
	public MissingConfigurationKeyException(String key, Throwable cause) {
		super(StringUtils.format(MESSAGE, key), cause);
		this.key = key;
	}

	/**
	 * @param key
	 * @param message
	 * @param cause
	 */
	public MissingConfigurationKeyException(String key, String message, Throwable cause) {
		super(message, cause);
		this.key = key;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
}
