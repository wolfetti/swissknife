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

import org.wolfetti.swissknife.common.utils.StringUtils;
import org.wolfetti.swissknife.common.utils.exceptions.StringFormatException;

/**
 * Classe che effettua la formattazione della query in alternativa al messageFormat di java,
 * la cui configurabilità e complessità è un ostacolo in questo caso.
 *
 * @author Fabio Frijo
 *
 */
public final class SqlFormatter {

	private String sql;
	private Object[] values;

	/**
	 *
	 * @param sql
	 * @param values
	 */
	public SqlFormatter(boolean prepareValues, String sql, Object ... values){
		if(sql == null || sql.length() == 0){
			throw new StringFormatException("Null or empty SQL is not allowed.");
		}

		this.sql = sql;

		if(values != null && values.length > 0){
			if(prepareValues){
				this.values = prepareValues(values);
			} else {
				this.values = values;
			}
		}
	}

	/**
	 *
	 * @param sql
	 * @param values
	 */
	public SqlFormatter(String sql, Object ... values){
		this(true, sql, values);
	}

	/**
	 *
	 *
	 * @param sql
	 * @param values
	 * @return
	 */
	public static String format(String sql, Object ... values){
		return format(true, sql, values);
	}

	/**
	 *
	 *
	 * @param sql
	 * @param values
	 * @return
	 */
	public static String format(boolean prepareValues, String sql, Object ... values){
		return new SqlFormatter(prepareValues, sql, values).format();
	}

	/**
	 * This method replace all placeholders inside the SQL string with
	 * their appropriate values.
	 *
	 * @return
	 * 	The SQL string with all placeholders replaced.
	 *
	 * @throws StringFormatException
	 * 	When a placeholder doesn't have a value or when another error occours.
	 *
	 */
	public String format(){
		if(values == null || values.length == 0){
			return sql;
		}

		// Controllo se c'è qualche referenza ad un altra query NON presente nel file.
		for(int i = 0; i < sql.length(); i++){
			if(i > 0 && sql.charAt(i - 1) == '$'){
				String sqlPlaceHolder = "";
				i++;

				while(sql.charAt(i) != '}'){
					sqlPlaceHolder += sql.charAt(i);
					i++;
				}

				throw new StringFormatException("La query identificata con la chiave '" + sqlPlaceHolder + "' non esiste nel file delle query SQL.");
			}
		}

		return StringUtils.format(sql, values).replaceAll("'null'", "null");
	}

	/**
	 * Preparazione dei valori per il salvataggio della query SQL.
	 *
	 * @param values
	 * @return
	 */
	public static Object[] prepareValues(Object ... values){
		if(values == null || values.length == 0){
			throw new StringFormatException("Chiamato il metodo 'prepareValues' senza valori da preparare.");
		}

		for (int i = 0; i < values.length; i++){

			// Controlli da eseguire sui valori di tipo stringa.
			if(values[i] instanceof String){
				String s = (String) values[i];

				// Controllo gli apici singoli contenuti in eventuali stringe in ingresso al database
				if(s.contains("'")){
					s = s.replaceAll("'", "''");
				}

				values[i] = s;
			}
		}

		return values;
	}
}
