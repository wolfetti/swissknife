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

import java.util.List;
import java.util.UUID;

import org.wolfetti.swissknife.common.utils.exceptions.StringFormatException;

/**
 * Classe che contiene una serie di metodi utili alla manipolazione
 * e alla generazione delle stringhe
 *
 * @author Fabio Frijo
 *
 */
public final class StringUtils {

	// Singleton
	private StringUtils(){}

	/**
	 * Questo metodo sostituisce tutti i placeholders all'interno di una
	 * stringa con gli appropriati valori.
	 * <br><br>
	 * I placeholders vanno rappresentati all'interno delle stringhe con un
	 * numero progressivo racchiuso tra parentesi, come nell'esempio:
	 * <br><br>
	 * <pre>
	 * 	INSERT INTO `tabella` VALUES ({0}, '{1}', '{2}')
	 * </pre>
	 * <br><br>
	 * I place holder possono essere ripetuti tutte le volte necessarie.
	 * Ovviamente lo stesso placeholder sarà sostituito dallo stesso valore.
	 * <br><br>
	 * Es:
	 * <br><pre>
	 * 	UPDATE `tabella` SET `id` = '{0}', `name` = '{1}' WHERE `id` = '{0}'
	 * </pre>
	 *
	 * @return
	 * 	La stringa con i valori sostituiti.
	 *
	 * @throws StringFormatException
	 * 	Quando un placeholder non ha un valore associato.
	 *
	 * @throws RuntimeException
	 * 	Quando c'&egrave; un errore non gestito.
	 */
	public static String format(String s, Object ... values)
	throws StringFormatException, RuntimeException {
		if(values == null || values.length == 0){
			return s;
		}

		StringBuffer result = new StringBuffer(s.length() * 2);
		StringBuffer temp = null;
		StringBuffer placeholder = null;

		boolean inBraces = false;
		int argumentNumber = 0;

		try {
			for(int i = 0; i < s.length(); i++){
				char c = s.charAt(i);

				// Trovato un valore da sostituire
				if(c == '{'){
					inBraces = true;
					temp = new StringBuffer(4);
					placeholder = new StringBuffer(8);
					placeholder.append(c);
				}

				// Finito il punto in cui va sostituito il valore,
				// effettuo la sostituzione vera e propria
				else if(c == '}'){
					placeholder.append(c);
					argumentNumber = Integer.parseInt(temp.toString());
					Object v = values[argumentNumber];
					result.append(v);
					inBraces = false;
				}

				// Creo la stringa contenente il numero di valore da sostituire.
				else if(inBraces){
					placeholder.append(c);

					if(c != '.' && c != ',' && c != '-' && Character.isDigit(c)){
						temp.append(c);
					}
				}

				// Parte da non sostituire
				else {
					result.append(c);
				}
			}
		}

		// Se il numero di argomento è maggiore o uguale al numero dei valori
		// significa che mancano dei valori nell'array.
		catch (ArrayIndexOutOfBoundsException e){
			throw new StringFormatException(
				"Il placeholder " + placeholder.toString() + " non ha un valore associato.",
				e
			);
		}

		catch (Throwable t){
			throw new StringFormatException("Errore sconosciuto:", t);
		}

		return result.toString();
	}

	/**
	 * Conversione di una lista di stringhe in un'unica stringa,
	 * utilizzando come separatore il carattere <code>,</code>
	 *
	 * @param values
	 * 	La lista da trasformare in stringa.
	 * @return
	 * 	Una stringa con tutti i valori separati da <code>,</code>
	 */
	public static String fromListToCsv(List<String> values){
		return fromListToCsv(values, ",");
	}

	/**
	 * Conversione di una lista di stringhe in un'unica stringa,
	 * utilizzando come separatore la stringa in ingresso.
	 *
	 * @param values
	 * 	La lista da trasformare in stringa.
	 * @param separator
	 * 	La stringa da usare come separatore
	 * @return
	 * 	Una stringa con tutti i valori separati dala stringa in ingresso.
	 */
	public static String fromListToCsv(List<String> values, String separator){
		StringBuffer sb = new StringBuffer();

		boolean first = true;
		for(String v : values){
			if(!first){
				sb.append(separator);
			}

			sb.append(v);

			if(first){
				first = false;
			}
		}

		return sb.toString();
	}

	/**
	 * @return
	 * 	<code>true</code> se la stringa in ingresso &egrave; vuota,
	 * 	contiene solo spazi o &egrave; <code>null</code>
	 */
	public static boolean isEmptyOrNull(String s){
		if(s == null){
			return true;
		}

		return "".equals(s.trim());
	}

	/**
	 * @return
	 * 	<code>true</code> se la stringa in ingresso non &egrave; vuota,
	 * 	non contiene solo spazi e non &egrave; <code>null</code>
	 */
	public static boolean isNotEmptyOrNull(String s){
		return !isEmptyOrNull(s);
	}

	/**
	 * Restituisce un id univoco alfanumerico randomico (ideale per chiavi database)
	 */
	public static String getRandomUniqueId(){
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}

	/**
	 * Restituisce un id univoco alfanumerico randomico (ideale per chiavi database)
	 *
	 * @param length
	 * 	La lunghezza della stringa in uscita
	 */
	public static String getRandomUniqueId(int length){
		String s = getRandomUniqueId();

		if(length > s.length()){
			return s;
		} else if(length == s.length()){
			return s;
		}

		return s.substring(0, length);
	}

	/**
	 * Restituisce la classe dell'oggetto + il codice esadecimale dell'oggetto per il
	 * metodo toString()
	 */
	public static String objectIdentifier(Object o){
		return o.getClass().getName() + "@" + Integer.toHexString(o.hashCode());
	}
	
	/**
	 * Funzione che esegue un trim su una stringa,
	 * nel caso la stringa sia nulla restituisce una stringa vuota. 
	 * @return
	 */
	public static String safeTrim(String s){
		if(isEmptyOrNull(s)){
			return "";
		}
		
		return s.trim();
	}
	
	/**
	 * Funzione che esegue un upperCase su una stringa,
	 * nel caso la stringa sia nulla restituisce una stringa vuota. 
	 * @return
	 */
	public static String safeUpperCase(String s){
		if(isEmptyOrNull(s)){
			return "";
		}
		
		return s.toUpperCase();
	}
	
	/**
	 * Funzione che esegue un lowerCase su una stringa,
	 * nel caso la stringa sia nulla restituisce una stringa vuota. 
	 * @return
	 */
	public static String safeLowerCase(String s){
		if(isEmptyOrNull(s)){
			return "";
		}
		
		return s.toLowerCase();
	}
}
