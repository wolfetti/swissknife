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
package org.wolfetti.swissknife.config.utils;

import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.wolfetti.swissknife.common.SKConstants;

/**
 *
 *
 * @author Fabio Frijo
 *
 */
public class ConfigUtils {

	/**
	 * Private constructor, this class is a singleton.
	 */
	private ConfigUtils() {}

	/**
	 * Method that returns the contents of a {@link Configuration} object into a formatted string.
	 */
	public static String getValuesString(Configuration conf){
		return getValuesString("", conf);
	}

	/**
	 * Method that returns the contents of a {@link Configuration} object into a formatted string.
	 */
	public static String getValuesString(String prefix, Configuration conf){
		Iterator<?> it = conf.getKeys();

		StringBuffer sb = new StringBuffer(prefix).append(SKConstants.NEW_LINE);

		while(it.hasNext()){
			String key = String.valueOf(it.next());
			sb.append(SKConstants.TAB).append(key).append(" = ");

			if(conf.getStringArray(key).length > 1){
				String[] values = conf.getStringArray(key);

				for(int i = 0; i < values.length; i++){
					if(i > 0){
						sb.append(", ");
					}

					sb.append(values[i]);
				}

				sb.append(SKConstants.NEW_LINE);
			} else {
				sb.append(conf.getString(key)).append(SKConstants.NEW_LINE);
			}
		}

		return sb.toString();
	}
}
