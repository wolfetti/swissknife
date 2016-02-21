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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Una serie di metodi utili per lavorare con le Enumerations
 *
 * @author Fabio Frijo
 *
 */
public class EnumUtils {
	private EnumUtils(){}

	@SuppressWarnings("rawtypes")
	public static List<String> toList(Class<? extends Enum> e){
		List<String> response = new LinkedList<String>();

		for(Enum value : e.getEnumConstants()){
			response.add(value.toString());
		}

		return response;
	}

	@SuppressWarnings("rawtypes")
	public static List<Map<String, Object>> toList4Json(Class<? extends Enum> e, String ... keys){
		List<Map<String, Object>> response = new LinkedList<Map<String, Object>>();

		for(Enum value : e.getEnumConstants()){
			Map<String, Object> item = new HashMap<String, Object>();

			for(String key : keys){
				item.put(key, value);
			}

			response.add(item);
		}

		return response;
	}
}
