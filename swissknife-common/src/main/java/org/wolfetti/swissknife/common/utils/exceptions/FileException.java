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

import org.wolfetti.swissknife.common.business.ApplicationException;

/**
 * Eccezione sollevata quando c'è un qualche problema nella manipolazione dei files.
 *
 * @author Fabio Frijo
 *
 */
public class FileException extends ApplicationException {
	private static final long serialVersionUID = 4311400844523681969L;

	/**
	 * @param message
	 * @param cause
	 */
	public FileException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public FileException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public FileException(Throwable cause) {
		super(cause);
	}
}
