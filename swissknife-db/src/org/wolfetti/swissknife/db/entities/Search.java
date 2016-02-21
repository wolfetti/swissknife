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

import java.util.List;

import org.wolfetti.swissknife.common.business.ApplicationEntity;
import org.wolfetti.swissknife.common.utils.ListUtils;
import org.wolfetti.swissknife.common.utils.StringUtils;

/**
 * Classe ideata per fare da container ai campi ed al tipo di eventuali ricerche implementate lato client.
 *
 * @author Fabio Frijo
 */
public final class Search extends ApplicationEntity {
	private static final long serialVersionUID = -7045794582870386779L;

	/**
	 * La stringa compilata per comporre la query SQL.
	 */
	private StringBuffer compiledQuery;

	/**
	 * La stringa da cercare all'interno del database.
	 */
	private String query;

	/**
	 * La stringa da cercare all'interno del database.
	 */
	private String sqlOperator;

	/**
	 * Le colonne del result set sulle quali effettuare la ricerca.
	 */
	private List<String> columns;

	/**
	 * Il tipo di ricerca da effettuare
	 */
	private SearchType searchType;

	/**
	 * Istanzia un oggetto di ricerca con i valori corrispondenti a quelli indicati nei parametri.
	 *
	 * @param query
	 * 	La stringa da ricercare su database
	 *
	 * @param sqlOperator
	 * 	L'operatore da utilizzare come prefisso per la query di ricerca, o <code>null</code> in caso che non sia
	 *  necessario nessun operatore.
	 *
	 * @param columns
	 * 	Una {@link List} contenente un elenco di colonne del result set sulle quali effettuare la ricerca.
	 *
	 * @param type
	 * 	Il {@link SearchType tipo di ricerca} da utilizzare.
	 *
	 */
	public Search(String query, String sqlOperator, List<String> columns, SearchType type) {
		super();
		this.columns = columns;
		this.searchType = type;
		this.query = query;
		this.sqlOperator = sqlOperator;
	}

	/**
	 * Istanzia un oggetto di ricerca con impostato il tipo di ricerca a {@link SearchType#CONTAINS CONTAINS}.
	 * La query di ricerca e le colonne sulle quali effettuare la ricerca vanno impostate, altrimenti questa
	 * istanza restituir&agrave; come query di ricerca soltanto <b>una stringa vuota</b>.
	 */
	public Search() {
		this(null, null, null, SearchType.CONTAINS);
	}

	/**
	 * Controllo che sia possibile compilare la query di ricerca.
	 *
	 * @return
	 */
	public boolean isValid(){
		return StringUtils.isNotEmptyOrNull(this.query) && ListUtils.isNotEmptyOrNull(this.columns);
	}

	/**
	 * Compila la stringa di ricerca in base al tipo di ricerca utilizzato.
	 *
	 * @param searchQuery
	 * @return
	 */
	private String compileSearchQuery(){

		// E' inutile eseguire la compilazione se la query e' NULL o vuota
		// o se non sono state specificate delle colonne sulle quali effettuare la ricerca.
		if(!this.isValid()){
			return "";
		}

		// Controllo che la query non sia stata compilata in precedenza.
		// In caso di sostituzione della query o del tipo di ricerca, la query compilata
		// viene riportata a NULL dai setters interessati, in maniera che al primo accesso
		// a questo metodo la query venga compilata ex novo.
		if(null == this.compiledQuery){

			String searchQuery = String.copyValueOf(this.query.toCharArray());
			this.compiledQuery = new StringBuffer((10 + searchQuery.length()) * this.columns.size());

			if(StringUtils.isNotEmptyOrNull(this.sqlOperator)){
				this.compiledQuery.append(" ");
				this.compiledQuery.append(this.sqlOperator);
			}

			this.compiledQuery.append(" (");

			/* ---------------------------------------------------------------------------------- */
			/* TODO Questo sotto e' valido solo per mysql. Implementare una risoluzione dinamica. */
			/* ---------------------------------------------------------------------------------- */
			/* ------------- */ searchQuery = searchQuery.replace("\\", "\\\\"); /* ------------- */
			/* ------------- */ searchQuery = searchQuery.replace("%",  "\\%");  /* ------------- */
			/* ------------- */ searchQuery = searchQuery.replace("_",  "\\_");  /* ------------- */
			/* ------------- */ searchQuery = searchQuery.replace("'",  "''");   /* ------------- */
			/* ---------------------------------------------------------------------------------- */

			searchQuery = this.searchType.compile(searchQuery);

			for (int i = 0, l = this.columns.size(); i < l; i++){
				if(i > 0){
					this.compiledQuery.append(" OR ");
				}

				this.compiledQuery.append(this.columns.get(i));
				this.compiledQuery.append(" = '");
				this.compiledQuery.append(searchQuery);
				this.compiledQuery.append("'");
			}

			this.compiledQuery.append(")");
		}

		return this.compiledQuery.toString();
	}

	/**
	 * @return the search
	 */
	public String getQuery() {
		return this.compileSearchQuery();
	}

	/**
	 * @param query the search to set
	 */
	public void setQuery(String query) {
		this.compiledQuery = null;
		this.query = query;
	}

	/**
	 * @return the columns
	 */
	public List<String> getColumns() {
		return this.columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	/**
	 * @return the type
	 */
	public SearchType getSearchType() {
		return this.searchType;
	}

	/**
	 * @param type the type to set
	 */
	public void setSearchType(SearchType type) {
		this.compiledQuery = null;
		this.searchType = type;
	}
}
