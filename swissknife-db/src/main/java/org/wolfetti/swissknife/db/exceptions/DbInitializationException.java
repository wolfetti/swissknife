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

public class DbInitializationException extends ApplicationRuntimeException {
	private static final long serialVersionUID = 7586590280893457099L;

	public DbInitializationException(Throwable cause){
		super("Errore durante l'inizializzazione della connessione al database", cause);
	}

	public DbInitializationException(String message, Throwable cause){
		super(message, cause);
	}

	public DbInitializationException(String message) {
		super(message);
	}
}
