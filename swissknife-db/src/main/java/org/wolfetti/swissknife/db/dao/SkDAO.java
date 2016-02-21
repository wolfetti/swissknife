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
package org.wolfetti.swissknife.db.dao;

import java.sql.Savepoint;

import org.wolfetti.swissknife.common.logger.Log;
import org.wolfetti.swissknife.common.logger.LogFactory;
import org.wolfetti.swissknife.db.DbConnector;

/**
 * Classe che definisce una serie di metodi comuni a tutte le implementazioni dei vari tipi di DAO.
 *
 * @author Fabio Frijo
 */
public abstract class SkDAO {

	/**
	 * Valore per start e limit da utilizzare per la paginazione disabilitata
	 */
	public final static int NOT_PAGING = -1;

	/**
	 * Logger di classe
	 */
	protected Log log = null;

	/**
	 * Il {@link DbConnector} che verr&agrave; utilizzato per connettersi al database
	 */
	protected DbConnector connector;

	/**
	 * Parametro che indica la riga di inizio in un eventuale paginazione
	 */
	protected int start = -1;

	/**
	 * Parametro che indica il numero di records da restituire in paginazione
	 */
	protected int limit = -1;

	/**
	 * Parametro che indica il totale di records nel database, usato per la paginazione.
	 */
	protected long total = -1L;

	/**
	 * Costruttore che inizializza la variabile associata al
	 * connettore del database e inizializza il logger di classe.
	 */
	protected SkDAO(DbConnector connector){
		this.log = LogFactory.getLog(this.getClass());
		this.connector = connector;
	}

	/**
	 * Esegue la commit sul database
	 */
	public void commit()
	throws Exception {
		if(!this.connector.isTransaction()){
			this.log.debug("Transazione non attiva: nessun rollback eseguito");
			return;
		}
		
		this.log.debug("Commit in corso...");
		this.connector.commit();
		this.log.debug("Commit eseguito.");
	}

	/**
	 * Esegue la rollback sul database
	 */
	public void rollback()
	throws Exception {
		if(!this.connector.isTransaction()){
			this.log.warn("Transazione non attiva: nessun rollback eseguito");
			return;
		}
		
		this.log.debug("Rollback in corso...");
		this.connector.rollback();
		this.log.debug("Rollback eseguito.");
	}

	/**
	 * Esegue la rollback sul database
	 */
	public void rollback(Savepoint s)
	throws Exception {
		if(!this.connector.isTransaction()){
			this.log.warn("Transazione non attiva: nessun rollback eseguito");
			return;
		}
		
		this.log.debug("Rollback in corso...");
		this.connector.rollback(s);
		this.log.debug("Rollback eseguito.");
	}

	/**
	 * Esegue la rollback sul database
	 */
	public Savepoint savepoint()
	throws Exception {
		this.log.debug("Creazione di un Savepoint in corso...");
		Savepoint s = this.connector.savepoint();
		this.log.debug("Savepoint creato.");
		return s;
	}

	/**
	 * Esegue la rollback sul database
	 */
	public Savepoint savepoint(String name)
	throws Exception {
		this.log.debug("Creazione di un Savepoint denominato '" + name + "' in corso...");
		Savepoint s = this.connector.savepoint(name);
		this.log.debug("Savepoint creato.");
		return s;
	}

	/**
	 * Restituisce <code>true</code> se la paginazione $egrave; abilitata
	 */
	public final boolean isPagingEnabled(){
		return this.start > NOT_PAGING && this.limit > NOT_PAGING;
	}

	/**
	 * Formatta la query in ingresso aggiungendo i parametri di paginazione.
	 * @param sql
	 * @return
	 */
	protected String addPagination(String sql){
		// TODO Questo funziona solo su MySQL. Creare una funzione che formatta la query in base al tipo di database.
		return sql + " LIMIT " + this.start + ", " + this.limit;
	}

	/**
	 * @return the total
	 */
	public long getTotal() {
		return this.total;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @param limit the limit to set
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}
}
