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

import org.wolfetti.swissknife.common.SKConstants;

/**
 *
 *
 *
 * @author Fabio Frijo
 *
 */
public class DebugUtils {

	/**
	 * Metodo che restituisce la stringa di inizializzazione delle applicazioni
	 */
	public static String getInitMessage(String name, String path){
		String start = "=== DEPLOY ";
		StringBuffer debugMessage = new StringBuffer(SKConstants.NEW_LINE + start);
		StringBuffer footer = new StringBuffer("==========");

		int count = path.length() + 7;

		for (int i = 0; i < count; i++){
			footer.append("=");

			if(start.length() + i == count){
				debugMessage.append(SKConstants.NEW_LINE);
				debugMessage.append("name : ");
				debugMessage.append(name);
				debugMessage.append(SKConstants.NEW_LINE);
				debugMessage.append("path : ");
				debugMessage.append(path);
				debugMessage.append(SKConstants.NEW_LINE);
				debugMessage.append(footer.toString());
				break;
			} else {
				debugMessage.append("=");
			}
		}

		return debugMessage.toString();
	}

}
