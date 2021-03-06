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

/**
 *
 *
 * @author Fabio Frijo
 *
 */
public class ReloadFault extends ApplicationRuntimeException {
	private static final long serialVersionUID = 7099163170223243267L;


	/**
	 * @param message
	 */
	public ReloadFault(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ReloadFault(String message, Throwable cause) {
		super(message, cause);
	}
}
