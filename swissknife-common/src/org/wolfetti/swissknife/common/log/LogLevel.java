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
package org.wolfetti.swissknife.common.log;

/**
 *
 *
 * @author Fabio Frijo
 *
 */
public final class LogLevel {

	public final static int OFF   = 0;
	public final static int TRACE = 1;
	public final static int DEBUG = 2;
	public final static int INFO  = 3;
	public final static int WARN  = 4;
	public final static int ERROR = 5;
	public final static int FATAL = 6;

	/**
	 * This class is a singleton.
	 */
	private LogLevel() {}
}
