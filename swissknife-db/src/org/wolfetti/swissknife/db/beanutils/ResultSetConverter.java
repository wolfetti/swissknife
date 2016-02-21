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
import java.util.List;

import org.wolfetti.swissknife.db.exceptions.ConverterException;

/**
 * Interfaccia che va implementata da tutti le classi che devono convertire un
 * ResultSet in un oggetto particolare.
 *
 * @author Fabio Frijo
 */
public interface ResultSetConverter {

	/**
	 * Restituisce un singolo elemento.
	 *
	 * @param rs
	 * 	Il ResultSet da parsare
	 *
	 * @return
	 * 	Un singolo elemento corrispondente a una riga di ResultSet
	 *
	 * @throws ConverterException
	 * 	Quando un converter non riesce ad effettuare la conversione
	 */
	public Object getSingleItem(ResultSet rs)
	throws ConverterException;

	/**
	 * Restituisce una lista di elementi corrispondenti ai risultati della query.<br>
	 * Il tipo di elemento contenuto dalla lista dipende dall'implementazione di questa interfaccia.
	 *
	 * @param rs
	 * 	Il ResultSet da parsare
	 *
	 * @return
	 * 	Una lista di elementi corrispondenti ai risultati della query.
	 *
	 * @throws ConverterException
	 * 	Quando un converter non riesce ad effettuare la conversione
	 */
	public List<?> getItemsList(ResultSet rs)
	throws ConverterException;
}
