/*
 * Copyright(c) 2013 Fabio Frijo.
 *
 * This file is part of swissknife-db.
 *
 * swissknife-db is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * swissknife-db is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with swissknife-db.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wolfetti.swissknife.db.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wolfetti.swissknife.common.SKConstants;

public class ResultSetHelper {
	private ResultSetHelper(){}

	/**
	 * Conversione degli oggetti problematici in oggetti utilizzabili,
	 * ad esempio l'oggetto <code>java.</code>
	 * @param o
	 * @return
	 */
	public static Object getCleanValue(Object o){

		// Converto java.sql.Date in java.lang.String
		if(o instanceof java.sql.Date){
			return ((java.sql.Date) o).toString();
		}

		// Altri tipi di dato
		return o;
	}

	/**
	 * Chiude il ResultSet in ingresso, con tutti i controlli del caso.
	 */
	public static void close(ResultSet rs){

		// TODO Capire perchè C3P0 rompe le balle
		if(SKConstants.CONF.DB.C3P0_RS_NAME.equals(rs.getClass().getName())){
			return;
		}

		try {
			if(rs != null && !rs.isClosed()){
				rs.close();
			}

		} catch (SQLException e) {
			rs = null;
		}
	}
}
