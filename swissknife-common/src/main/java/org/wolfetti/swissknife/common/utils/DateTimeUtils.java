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

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.wolfetti.swissknife.common.utils.exceptions.StringFormatException;

public class DateTimeUtils {
	private DateTimeUtils(){}

	/**
	 * Restituisce una stringa formattata con il tempo in ingresso
	 * convertito in ore:minuti:secondi.millisecondi
	 *
	 * @param time Il valore di tempo da formattare
	 * @return Il tempo formattato
	 */
	public static String getTimeFormatted(long time) {
		StringBuffer response = new StringBuffer();
		String format = String.format("%%0%dd", 2);
		long elapsedTime = time / 1000;
		String seconds = String.format(format, elapsedTime % 60);
		String minutes = String.format(format, elapsedTime % 3600 / 60);
		String hours = String.format(format, elapsedTime / 3600);
		String milliseconds = String.valueOf(time - elapsedTime * 1000);

		boolean isFoundValue = false;
		try {
			if(Integer.parseInt(hours) > 0){
				response.append(hours + " ore, ");
				isFoundValue = true;
			}
		} catch(Exception e){}

		try {
			if(Integer.parseInt(minutes) > 0 || isFoundValue){
				response.append(minutes + " minuti, ");
				isFoundValue = true;
			}
		} catch(Exception e){}

		try {
			if(Integer.parseInt(seconds) > 0 || isFoundValue){
				response.append(seconds + " secondi e ");
			}
		} catch(Exception e){}

		return  response.toString() + milliseconds + " millisecondi.";
	}

	/**
	 * Effettua un conteggio di millisecondi tra il parametro in ingresso
	 * e il momento in cui la funzione viene chiamata.
	 *
	 * @param start Il tempo iniziale
	 * @return Una stringa formattata con il metodo {@link #getTimeFormatted(long)} che
	 * 	rappresenta il tempo trascorso tra il valore in ingresso e il momento
	 * 	della chiamata a questa funzione
	 */
	public static String getElapsedTime(long start) {
		return getTimeFormatted(System.currentTimeMillis() - start);
	}

	/**
	 * Calcola i giorni di differenza tra le due date in ingresso.
	 *
	 * @param d1 Data iniziale
	 * @param d2 Data finale
	 * @return Intero che indica il numero dei giorni tra le due date in ingresso
	 */
	public static int daysBetween(Date d1, Date d2) {
		return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}

	/**
	 * Aggiunge n giorni alla data in ingresso.
	 *
	 * @param date Data di partenza
	 * @param days Numero di giorni da aggiungere
	 * @return Nuova data
	 */
	public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

	/**
	 * Formatta la data in ingresso nel formato in ingresso
	 *
	 * @param date Data di partenza
	 * @param format Formato della data rappresentata dalla stringa in ouput
	 * @return Stringa indicante la data in ingresso nel formato scelto
	 */
	public static String formatDate(Date date, String format) {
		Format formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}

	/**
	 * Converte la stringa in ingresso nella data a seconda del formato dato
	 *
	 * @param date Data di partenza
	 * @param format Formato della data della stringa in input
	 * @return Data relativa alla stringa in ingresso
	 */
	public static Date parseDate(String date, String format) {
		try {
			return new SimpleDateFormat(format).parse(date);
		} catch (ParseException e) {
			throw new StringFormatException("Unable to parse date with format '" + format + "'.", e);
		}
	}
}
