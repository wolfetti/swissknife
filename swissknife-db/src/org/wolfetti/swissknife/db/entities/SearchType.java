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
package org.wolfetti.swissknife.db.entities;

/**
 * Enumerazione che rappresenta il tipo di ricerca da effettuare sul database.
 *
 * @author Fabio Frijo
 */
public enum SearchType {

	/**
	 * Tipo che rappresenta una ricerca di tutti i valori che iniziano con il valore in ingresso al metodo {@link #getLike(String)}
	 */
	STARTS("{VALUE}%"),

	/**
	 * Tipo che rappresenta una ricerca di tutti i valori che contengono il valore in ingresso al metodo {@link #getLike(String)}
	 */
	CONTAINS("%{VALUE}%"),

	/**
	 * Tipo che rappresenta una ricerca di tutti i valori che terminano con il valore in ingresso al metodo {@link #getLike(String)}
	 */
	ENDS("%{VALUE}");

	/**
	 * La formula utilizzata per effettuare la like
	 */
	private String likeExpression;

	/**
	 * Costruttore che imposta la formula per creare l'espressione LIKE appropriata per il tipo di ricerca.
	 *
	 * @param likeExpression
	 */
	private SearchType(String likeExpression){
		this.likeExpression = likeExpression;
	}

	/**
	 * Compila l'espressione LIKE con il valore corretto.
	 *
	 * @param value
	 * @return
	 */
	public String compile(String value){
		return this.likeExpression.replaceAll("{VALUE}", value);
	}
}
