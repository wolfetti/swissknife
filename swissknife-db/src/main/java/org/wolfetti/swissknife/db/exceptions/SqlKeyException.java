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
package org.wolfetti.swissknife.db.exceptions;


/**
 * Exception raised when a query is missing in the properties file.
 *
 * @author Fabio Frijo
 *
 */
public class SqlKeyException extends DbConfigKeyException {
	private static final long serialVersionUID = 3451765377306945141L;

	/**
	 * @param key
	 */
	public SqlKeyException(String key) {
		super(key);
	}

	/**
	 * @param key
	 * @param message
	 */
	public SqlKeyException(String key, String message) {
		super(key, message);
	}

	/**
	 * @param key
	 * @param cause
	 */
	public SqlKeyException(String key, Throwable cause) {
		super(key, cause);
	}

	/**
	 * @param key
	 * @param message
	 * @param cause
	 */
	public SqlKeyException(String key, String message, Throwable cause) {
		super(key, message, cause);
	}
}
