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

import org.wolfetti.swissknife.common.business.ApplicationException;


/**
 * Exception raised when it fails to execute a SQL query
 *
 * @author Fabio Frijo
 *
 */
public class SqlQueryException extends ApplicationException {
	private static final long serialVersionUID = -7105873762038596678L;

	private String sql;

	/**
	 *
	 */
	public SqlQueryException(String sql) {
		this(sql, "Errore durante l'esecuzione della query");
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SqlQueryException(String sql, String message, Throwable cause) {
		super(message, cause);
		this.sql = sql;
	}

	/**
	 * @param message
	 */
	public SqlQueryException(String sql, String message) {
		super(message);
		this.sql = sql;
	}

	/**
	 * @return the sql
	 */
	public String getSql() {
		return this.sql;
	}
}
