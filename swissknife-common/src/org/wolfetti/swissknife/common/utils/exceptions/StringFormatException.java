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
package org.wolfetti.swissknife.common.utils.exceptions;

import org.wolfetti.swissknife.common.business.ApplicationRuntimeException;

/**
 * Eccezione sollevata nel momento in cui fallisce la formattazione di una stringa
 *
 * @author Fabio Frijo
 */
public class StringFormatException extends ApplicationRuntimeException {
	private static final long serialVersionUID = 917118851896362560L;

	public StringFormatException() {
		super();
	}

	public StringFormatException(String message) {
		super(message);
	}

	public StringFormatException(Throwable cause) {
		super(cause);
	}

	public StringFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
