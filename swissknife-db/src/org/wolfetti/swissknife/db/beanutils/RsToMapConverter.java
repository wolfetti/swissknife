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
package org.wolfetti.swissknife.db.beanutils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.wolfetti.swissknife.db.exceptions.ConverterException;
import org.wolfetti.swissknife.db.utils.ResultSetHelper;

/**
 * Classe che trasforma i risultati di una query in HashMap e in liste di HashMap
 *
 * @author Fabio Frijo
 */
public class RsToMapConverter implements ResultSetConverter {

	/**
	 * Restituisce un singolo elemento all'interno di una HashMap&lt;String, Object&gt;
	 *
	 * @param rs
	 * 	Il ResultSet da parsare
	 *
	 * @return
	 * 	Una HashMap contenente i nomi dei campi di database come chiave e il loro valore
	 *
	 * @throws SQLException
	 * 	Quando viene sollevata dal ResultSet
	 */
	public static Map<String, Object> getSingle(ResultSet rs) {
		return new RsToMapConverter().getSingleItem(rs);
	}

	/**
	 * Restituisce una lista di HashMap&lt;String, Object&gt corrispondenti ai risultati della query
	 *
	 * @param rs
	 * 	Il ResultSet da parsare
	 *
	 * @return
	 * 	Una List&lt;HashMap&lt;String, Object&gt&gt;.<br>
	 * 	Ogni mappa contiene i nomi dei campi di database come chiave e il loro valore e corrisponde a
	 * 	una row del ResultSet.
	 *
	 * @throws SQLException
	 * 	Quando viene sollevata dal ResultSet
	 */
	public static List<Map<String, Object>> getList(ResultSet rs){
		return new RsToMapConverter().getItemsList(rs);
	}

	/**
	 * Restituisce un singolo elemento all'interno di una HashMap&lt;String, Object&gt;
	 *
	 * @param rs
	 * 	Il ResultSet da parsare
	 *
	 * @return
	 * 	Una HashMap contenente i nomi dei campi di database come chiave e il loro valore
	 *
	 * @throws SQLException
	 * 	Quando viene sollevata dal ResultSet
	 */
	@Override
	public Map<String, Object> getSingleItem(ResultSet rs){
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			if(rs.next()){
				ResultSetMetaData md = rs.getMetaData();
				response = this.createMapItem(md, rs);
			}

		} catch (SQLException e) {
			throw new ConverterException("Errore durante il parsing del result set", e);
		} finally {
			ResultSetHelper.close(rs);
		}

		return response;
	}

	/**
	 * Restituisce una lista di HashMap&lt;String, Object&gt corrispondenti ai risultati della query
	 *
	 * @param rs
	 * 	Il ResultSet da parsare
	 *
	 * @return
	 * 	Una List&lt;HashMap&lt;String, Object&gt&gt;.<br>
	 * 	Ogni mappa contiene i nomi dei campi di database come chiave e il loro valore e corrisponde a
	 * 	una row del ResultSet.
	 *
	 * @throws SQLException
	 * 	Quando viene sollevata dal ResultSet
	 */
	@Override
	public List<Map<String, Object>> getItemsList(ResultSet rs) {
		List<Map<String, Object>> response = new LinkedList<Map<String,Object>>();

		try {
			ResultSetMetaData md = rs.getMetaData();

			while(rs.next()){
				response.add(this.createMapItem(md, rs));
			}
		} catch (SQLException e) {
			throw new ConverterException("Errore durante il parsing del result set", e);
		} finally {
			ResultSetHelper.close(rs);
		}

		return response;
	}

	/**
	 * Creazione dell'elemento che andra' a sostituire la riga di result set.
	 *
	 * @throws SQLException
	 * 	Quando viene sollevata dal ResultSet
	 */
	private Map<String, Object> createMapItem(ResultSetMetaData md, ResultSet rs) {
		Map<String, Object> item = new HashMap<String, Object>();

		try {
			for(int i = 1; i <= md.getColumnCount(); i++){
				item.put(
					md.getColumnLabel(i),
					ResultSetHelper.getCleanValue(rs.getObject(i))
				);
			}
		} catch (SQLException e) {
			throw new ConverterException("Impossibile creare la mappa associata al record di database", e);
		}

		return item;
	}
}
