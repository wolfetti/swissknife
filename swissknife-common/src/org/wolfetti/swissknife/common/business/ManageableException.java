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
package org.wolfetti.swissknife.common.business;

/**
 * Interfaccia che viene implementata da tutte le superclassi di exception generiche
 * e definisce una serie di metodi di utility per la gestione dell'eccezione
 *
 * @author Fabio Frijo
 */
public interface ManageableException {

	/**
	 * Metodo che deve tornare true o false in base al fatto che vada loggato l'errore o meno
	 */
	public boolean mustLogged();

	/**
	 * Metodo che deve tornare true o false se quest'eccezione è un eccezione di runtime
	 */
	public boolean isRuntime();
}
