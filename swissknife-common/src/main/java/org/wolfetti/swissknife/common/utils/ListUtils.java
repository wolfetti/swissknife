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
package org.wolfetti.swissknife.common.utils;

import java.util.List;

/**
 * Classe che contiene una serie di funzioni utili per l'utilizzo degli oggetti di tipo {@link List}
 *
 * @author Fabio Frijo
 */
public class ListUtils {

	/**
	 * Questa classe contiene solo metodi statici
	 */
	private ListUtils() {}

	/**
	 * Controlla che una lista sia vuota o nulla.
	 *
	 * @param list
	 * @return
	 */
	public static boolean isEmptyOrNull(List<?> list){
		if(null == list){
			return true;
		}

		return list.isEmpty();
	}

	/**
	 * Controlla che una lista NON sia vuota o nulla.
	 *
	 * @param list
	 * @return
	 */
	public static boolean isNotEmptyOrNull(List<?> list){
		return !isEmptyOrNull(list);
	}
}
