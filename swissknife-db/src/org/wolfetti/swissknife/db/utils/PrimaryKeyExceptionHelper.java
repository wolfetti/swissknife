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


/**
 * Enum che data un eccezione in ingresso, controlla che sia un'eccezione da chiave primaria o meno.
 *
 * @author Fabio Frijo
 */
public enum PrimaryKeyExceptionHelper {
	MYSQL(
		"com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException",
		"com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException"
	);

	private String[] classes;

	private PrimaryKeyExceptionHelper(String ... classes){
		this.classes = classes;
	}

	/**
	 * Restituisce l'elenco delle classi di eccezione per questo driver.
	 * @return
	 */
	public String[] getClasses(){
		return classes;
	}

	/**
	 * Controllo che l'eccezione in ingresso (o una delle sue cause) sia un istanza
	 * di una classe di PK exception.
	 *
	 * @param t
	 * 	L'istanza dell'eccezione da controllare
	 *
	 * @return
	 * 	<code>true</code> Se la classe dell'eccezione &egrave; mappata come classe di PK exception.
	 */
	public static boolean check(Throwable t){
		for(PrimaryKeyExceptionHelper pkeh : values()){
			if(check(pkeh, t)){
				return true;
			}
		}

		return false;
	}

	/**
	 * Controllo che l'eccezione in ingresso (o una delle sue cause) sia un istanza
	 * di una classe di PK exception.
	 *
	 * @param t
	 * 	L'istanza dell'eccezione da controllare
	 *
	 * @return
	 * 	<code>true</code> Se la classe dell'eccezione &egrave; mappata come classe di PK exception.
	 */
	public static boolean check(PrimaryKeyExceptionHelper helper, Throwable t){
		String className = t.getClass().getName();

		for(String pkExName : helper.classes){
			if(pkExName.equals(className)){
				return true;
			}
		}

		if(t.getCause() != null){
			return check(helper, t.getCause());
		}

		return false;
	}
}
