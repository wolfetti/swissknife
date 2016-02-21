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


/**
 * Eccezione sollevata nel momento in cui viene caricata la configurazione ma era già stata caricata
 * in precedenza
 *
 * @author Fabio Frijo
 */
public class AlreadyLoadedException extends LoadFault {
	private static final long serialVersionUID = 6419536670928972047L;
	private static final String MSG = "La configurazione era gia' stata caricata!";

	/**
	 *
	 */
	public AlreadyLoadedException() {
		super(MSG);
	}

	/**
	 * @param message
	 */
	public AlreadyLoadedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public AlreadyLoadedException(Throwable cause) {
		super(MSG, cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AlreadyLoadedException(String message, Throwable cause) {
		super(message, cause);
	}
}
