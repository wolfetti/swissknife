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
 *
 *
 * @author Fabio Frijo
 *
 */
public class ReflectionException extends ApplicationRuntimeException {
	private static final long serialVersionUID = -4519919613251043641L;

	/**
	 * @param message
	 */
	public ReflectionException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ReflectionException(String message, Throwable cause) {
		super(message, cause);
	}
}
