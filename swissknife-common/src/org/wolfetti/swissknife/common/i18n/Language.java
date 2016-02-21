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
package org.wolfetti.swissknife.common.i18n;

public enum Language {
	IT("it", "Italiano"		,"dd/MM/yyyy" 	, "{\"de\":\"Italienisch\",\"it\":\"Italiano\",\"cn\":\"意大利的\",\"jp\":\"イタリア語\",\"fr\":\"Italien\",\"en\":\"Italian\",\"ru\":\"итальянский\",\"es\":\"Italiano\"}"),
	EN("en", "Inglese"		,"yyyy-MM-dd" 	, "{\"de\":\"Englisch\",\"it\":\"Inglese\",\"cn\":\"英语\",\"jp\":\"英語\",\"fr\":\"Anglais\",\"en\":\"English\",\"ru\":\"английский\",\"es\":\"Inglés\"}"),
	FR("fr", "Francese"		,"dd/MM/yyyy" 	, "{\"de\":\"Französisch\",\"it\":\"Francese\",\"cn\":\"法国人\",\"jp\":\"フランス語\",\"fr\":\"Français\",\"en\":\"French\",\"ru\":\"французский\",\"es\":\"Francés\"}"),
	DE("de", "Tedesco"		,"yyyy-MM-dd" 	, "{\"de\":\"Deutsch\",\"it\":\"Tedesco\",\"cn\":\"德国的\",\"jp\":\"ドイツ語\",\"fr\":\"Allemand\",\"en\":\"German\",\"ru\":\"немецкий\",\"es\":\"Alemán\"}"),
	ES("es", "Spagnolo"		,"dd/MM/yyyy" 	, "{\"de\":\"Spanisch\",\"it\":\"Spagnolo\",\"cn\":\"西班牙的\",\"jp\":\"スペイン語\",\"fr\":\"Espagnol\",\"en\":\"Spanish\",\"ru\":\"испанский\",\"es\":\"Español\"}"),
	RU("ru", "Russo"		,"dd.MM.yyyy" 	, "{\"de\":\"Russisch\",\"it\":\"Russo\",\"cn\":\"俄\",\"jp\":\"ロシア\",\"fr\":\"Russe\",\"en\":\"Russian\",\"ru\":\"русский\",\"es\":\"Ruso\"}"),
	CN("cn", "Cinese"		,"yyyy-MM-dd" 	, "{\"de\":\"Chinese\",\"it\":\"Cinese\",\"cn\":\"中国的\",\"jp\":\"中国人\",\"fr\":\"Chinois\",\"en\":\"Cinese\",\"ru\":\"китайский\",\"es\":\"Chino\"}"),
	JP("jp", "Giapponese"	,"yyyy-MM-dd" 	, "{\"de\":\"Japanisch\",\"it\":\"Giapponese\",\"cn\":\"日本\",\"jp\":\"日本人\",\"fr\":\"Japonais\",\"en\":\"Japanese\",\"ru\":\"японский\",\"es\":\"Japonés\"}");
	
	private String code;
	private String description;
	private String translations;
	private String dateFormat;
	
	private Language(String code, String description, String dateFormat, String translations){
		this.code = code;
		this.description = description;
		this.dateFormat = dateFormat;
		this.translations = translations;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public String getCode(){
		return this.code;
	}
	
	public String getTranslations() {
		return this.translations;
	}
	
	public String getDateFormat() {
		return dateFormat;
	}

	@Override
	public String toString(){
		return this.code;
	}
	
	/**
	 * Restituisce il valore corretto di quest'enum in base alla stringa in ingresso
	 */
	public static Language fromString(String language){
		for(Language l : values()){
			if(l.toString().equalsIgnoreCase(language)){
				return l;
			}
		}
		throw new IllegalArgumentException("Linguaggio '" + language + "' non supportato.");
	}
}
