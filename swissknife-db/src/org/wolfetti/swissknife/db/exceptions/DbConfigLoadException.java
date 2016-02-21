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

import org.wolfetti.swissknife.common.business.ApplicationRuntimeException;

/**
 * Eccezione sollevata quando c'è qualche problema nel caricamento della configurazione
 *
 * @author Fabio Frijo
 */
public class DbConfigLoadException extends ApplicationRuntimeException {
	private static final long serialVersionUID = 8968702866301357304L;

	/**
	 * @param message
	 */
	public DbConfigLoadException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DbConfigLoadException(String message, Throwable cause) {
		super(message, cause);
	}
}
