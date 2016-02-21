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
package org.wolfetti.swissknife.db;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

import org.apache.commons.configuration.Configuration;
import org.wolfetti.swissknife.common.SKConstants;
import org.wolfetti.swissknife.common.log.Log;
import org.wolfetti.swissknife.common.log.LogFactory;
import org.wolfetti.swissknife.db.exceptions.DbInitializationException;
import org.wolfetti.swissknife.db.exceptions.DuplicatedKeyException;
import org.wolfetti.swissknife.db.exceptions.IllegalOperationException;
import org.wolfetti.swissknife.db.exceptions.InvalidFileException;
import org.wolfetti.swissknife.db.exceptions.SqlQueryException;
import org.wolfetti.swissknife.db.exceptions.TransactionException;
import org.wolfetti.swissknife.db.utils.PrimaryKeyExceptionHelper;

/**
 * Classe che interroga il database
 *
 * @author Fabio Frijo
 */
public abstract class DbConnector {

	/* ==================================== */
	/* ============== FIELDS ============== */
	/* ==================================== */
	/**
	 * Il logger
	 */
	protected Log log;

	/**
	 * La configurazione del connector
	 */
	protected Configuration config;

	/**
	 * La connessione al database.
	 */
	private Connection connection;

	/**
	 * Il numero di righe interessate dall'ultimo UPDATE effettuato.
	 */
	private int lastUpdatedRows = -1;

	/**
	 * L'ultimo ID inserito
	 */
	private int lastInsertId = -1;

	/**
	 * Flag che viene attivato dopo che le risorse sono state rilasciate dal metodo {@link #close()}
	 */
	private boolean isClosed = false;

	/**
	 * Flag che determina se &egrave; una transazione o un autocommit.
	 */
	protected boolean isTransaction = false;

	/**
	 * Lo statement utilizzato dalle query
	 */
	private Statement statement;

	/**
	 * La dimensione della fetch size (in righe)
	 */
	private int fetchSize;

	/* ========================================== */
	/* ============== CONSTRUCTORS ============== */
	/* ========================================== */

	/**
	 * Costruisce l'oggetto e imposta il flag per la transazione a <code>false</code>.
	 *
	 * @throws Exception Quando c'&egrave; un problema di intefacciamento con il database.
	 */
	protected DbConnector(Configuration config) {
		this(config, false);
	}

	/**
	 * Costruisce l'oggetto e imposta il flag per la transazione.
	 *
	 * @param config Il file di configurazione da utilizzare.
	 * @param isTransaction Setta l'autoCommit a <code>true</code> o a <code>false</code>.
	 *
	 * @throws Exception Quando c'&egrave; un problema di intefacciamento con il database.
	 */
	protected DbConnector(Configuration config, boolean isTransaction){
		log = LogFactory.getLog(this.getClass());
		this.isTransaction = isTransaction;
		this.config = config;

		try {
			this.setupConnection();
		}

		catch (DbInitializationException e) {
			throw e;
		}

		catch (Exception e) {
			throw new DbInitializationException(e);
		}
	}

	/* ===================================== */
	/* ============= ASTRATTI ============== */
	/* ===================================== */
	protected abstract Connection initConnection(Configuration config)
	throws DbInitializationException;

	/**
	 * Metodo astratto per clonare il connettore
	 *
	 * @param config
	 * 	La configurazione della connessione
	 *
	 * @param isTransaction
	 * 	<code>true</code> se si vuole attivare la transazione, altrimenti la connessione
	 * 	avr&agrave; l'autocommit attivato
	 *
	 * @return
	 * 	Il clone del dbconnector
	 */
	protected abstract DbConnector clone(Configuration config, boolean isTransaction);

	/* ==================================== */
	/* ============= GETTERS ============== */
	/* ==================================== */
	public Connection getConnection()
	throws Exception{
		this.reset();
		return connection;
	}

	/*
	 * (non-Javadoc)
	 * @see org.wolfetti.interfaces.db.DbConnector#isTransaction()
	 */
	public boolean isTransaction() {
		return isTransaction;
	}

	/*
	 * (non-Javadoc)
	 * @see org.wolfetti.interfaces.db.DbConnector#getLastUpdatedRows()
	 */
	public int getLastUpdatedRows() {
		return lastUpdatedRows;
	}

	/*
	 * (non-Javadoc)
	 * @see org.wolfetti.interfaces.db.DbConnector#getLastInsertId()
	 */
	public int getLastInsertId() {
		return lastInsertId;
	}

	/*
	 * (non-Javadoc)
	 * @see org.wolfetti.interfaces.db.DbConnector#isClosed()
	 */
	public boolean isClosed(){
		return isClosed || connection == null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.wolfetti.interfaces.db.DbConnector#getFetchSize()
	 */
	public int getFetchSize(){
		return fetchSize;
	}

	/* ==================================== */
	/* =============== SQL ================ */
	/* ==================================== */

	/*
	 * (non-Javadoc)
	 * @see org.wolfetti.interfaces.db.DbConnector#total(java.lang.String, java.lang.String)
	 */
//	TODO funzione di count
//	public long total(String totalKey, String sql)
//	throws Exception {
//		this.checkSqlKey(totalKey);
//
//		if(sql.toUpperCase().contains("ORDER BY")){
//			int index = sql.toUpperCase().lastIndexOf("ORDER BY");
//			sql = sql.substring(0, index).trim();
//			log.trace("Rimossa la clausola ORDER BY per aumentare la performance.");
//		}
//
//		log.trace("SQL da utilizzare per il conteggio: " + sql);
//
//		long total = 0L;
//		ResultSet rs = null;
//
//		try {
//			rs = this.query(SqlFormatter.format(false, sqlFile.getString(totalKey), sql));
//
//			if(rs.next()){
//				total = rs.getLong(1);
//			}
//
//		} finally {
//			ResultSetHelper.close(rs);
//		}
//
//		return total;
//	}

	/*
	 * (non-Javadoc)
	 * @see org.wolfetti.interfaces.db.DbConnector#query(java.lang.String)
	 */
	public ResultSet query(String sql)
	throws SqlQueryException {
		this.reset();

		ResultSet rs = null;
		try {
			log.debug(sql);
			rs = statement.executeQuery(sql);
			rs.setFetchSize(fetchSize);
		} catch (SQLException e) {
			throw new SqlQueryException(sql, "Errore durante la query di lettura", e);
		}

		return rs;
	}

	public void write(String sql)
	throws DuplicatedKeyException, SqlQueryException {
		this.reset();
		log.debug(sql);

		try {
			lastUpdatedRows = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

			ResultSet rs = statement.getGeneratedKeys();

			if (rs.next()) {
				lastInsertId = rs.getInt(1);
			}
		} catch (SQLException e) {
			if(PrimaryKeyExceptionHelper.check(e)){
				throw new DuplicatedKeyException(e.getMessage(), e);
			}

			throw new SqlQueryException(sql, "Errore durante la query di scrittura", e);
		}
	}

	public void write(String sql, File ... files)
	throws DuplicatedKeyException, SqlQueryException, InvalidFileException {
		if(files == null || files.length == 0){
			throw new InvalidFileException("Deve esserci almeno un file come parametro!");
		}

		this.reset();
		log.debug(sql);

		PreparedStatement ps = null;
		FileInputStream fis = null;
		try {

			int counter = 0;
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			for(File f : files){
				fis = new FileInputStream(f);
				ps.setBinaryStream(++counter, fis, f.length());
			}

			lastUpdatedRows = ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				lastInsertId = rs.getInt(1);
			}

		} catch (SQLException e) {
			if(PrimaryKeyExceptionHelper.check(e)){
				throw new DuplicatedKeyException(e.getMessage(), e);
			}

			throw new SqlQueryException(sql, "Errore durante la query di scrittura", e);
		} catch (FileNotFoundException e) {
			throw new InvalidFileException("Impossibile salvare il file", e);
		}

		// Liberazione delle risorse utilizzate.
		finally {
			try {
				if(ps != null){
					ps.close();
				}

				if(fis != null){
					fis.close();
				}
			}

			// Ignored closing exceptions
			catch (SQLException e) {
			}
			catch (IOException e) {
			}
		}
	}

	public void write(String sql, byte[] ... filesBytes)
	throws DuplicatedKeyException, SqlQueryException, InvalidFileException {
		if(filesBytes == null || filesBytes.length == 0){
			throw new InvalidFileException("Deve esserci almeno un file come parametro!");
		}

		this.reset();
		log.debug(sql);

		PreparedStatement ps = null;
		ByteArrayInputStream bais = null;
		try {

			int counter = 0;
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			for(byte[] ba : filesBytes){
				bais = new ByteArrayInputStream(ba);
				ps.setBinaryStream(++counter, bais, ba.length);
			}

			lastUpdatedRows = ps.executeUpdate();

			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				lastInsertId = rs.getInt(1);
			}

		} catch (SQLException e) {
			if(PrimaryKeyExceptionHelper.check(e)){
				throw new DuplicatedKeyException(e.getMessage(), e);
			}

			throw new SqlQueryException(sql, "Errore durante la query di scrittura", e);
		}

		// Liberazione delle risorse utilizzate.
		finally {
			try {
				if(ps != null){
					ps.close();
				}

				if(bais != null){
					bais.close();
				}
			}

			// Ignored closing exceptions
			catch (SQLException e) {
			}
			catch (IOException e) {
			}
		}
	}

	/* ==================================== */
	/* ============ TRANSACTION =========== */
	/* ==================================== */

	/*
	 * (non-Javadoc)
	 * @see org.wolfetti.interfaces.db.DbConnector#commit()
	 */
	public void commit()
	throws IllegalOperationException, TransactionException {
		this.reset();

		if(!isTransaction) {
			throw new IllegalOperationException("Non puoi fare commit se non sei in transazione!");
		}

		try {
			connection.commit();
		} catch (SQLException e) {
			throw new TransactionException("Impossibile effettuare il commit", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.wolfetti.interfaces.db.DbConnector#rollback()
	 */
	public void rollback()
	throws IllegalOperationException, TransactionException {
		this.reset();

		if(!isTransaction) {
			throw new IllegalOperationException("Non puoi fare rollback se non sei in transazione!");
		}

		try {
			connection.rollback();
		} catch (SQLException e) {
			throw new TransactionException("Impossibile effettuare il rollback", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.wolfetti.interfaces.db.DbConnector#rollback(java.sql.Savepoint)
	 */
	public void rollback(Savepoint savePoint)
	throws IllegalOperationException, TransactionException {
		this.reset();

		if(!isTransaction) {
			throw new IllegalOperationException("Non puoi fare rollback se non sei in transazione!");
		}

		try {
			connection.rollback(savePoint);
		} catch (SQLException e) {
			throw new TransactionException("Impossibile effettuare il rollback al savepoint '" + savePoint + "'", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.wolfetti.interfaces.db.DbConnector#savepoint()
	 */
	public Savepoint savepoint()
	throws IllegalOperationException, TransactionException {
		this.reset();

		if(!isTransaction){
			throw new IllegalOperationException("Non puoi creare un savepoint se non sei in transazione!");
		}

		try {
			return connection.setSavepoint();
		} catch (SQLException e) {
			throw new TransactionException("Impossibile creare il savepoint", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.wolfetti.interfaces.db.DbConnector#savepoint(java.lang.String)
	 */
	public Savepoint savepoint(String name)
	throws IllegalOperationException, TransactionException {
		this.reset();

		if(!isTransaction){
			throw new IllegalOperationException("Non puoi creare un savepoint se non sei in transazione!");
		}

		try {
			return connection.setSavepoint(name);
		} catch (SQLException e) {
			throw new TransactionException("Impossibile creare il savepoint '" + name + "'", e);
		}
	}

	/* ==================================== */
	/* ============= UTILITY ============== */
	/* ==================================== */
	@Override
	public DbConnector clone() {
		return this.clone(config, isTransaction);
	}

	/*
	 * (non-Javadoc)
	 * @see org.wolfetti.interfaces.db.DbConnector#close()
	 */
	public void close() {
		try {
			if(statement != null){
				statement.close();
			}

			if(connection != null && !connection.isClosed()) {
				connection.close();
			}

//			this.closeConnection();
		} catch (SQLException e) {
		} finally {
			statement = null;
			connection = null;
			isClosed = true;
		}
	}

	/* ==================================== */
	/* ============= PRIVATE ============== */
	/* ==================================== */

	/**
	 * Crea la connessione e imposta la transazione in base al valore passato al costruttore
	 */
	private void setupConnection(){
		// Creazione della connessione, demandata alle implementazioni di questa classe astratta
		connection = this.initConnection(config);

		if(isTransaction){
			try {
				connection.setAutoCommit(false);
			} catch (SQLException e) {
				throw new DbInitializationException("Transazione non supportata!", e);
			}
		}


		// Creazione dello statement
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			throw new DbInitializationException("Creazione dello statement fallita", e);
		}

		// Impostazione del fetch size
		try {
			if(config.getInt(SKConstants.CONF.DB.KEY_FETCHSIZE, 0) > 0){
				fetchSize = config.getInt(SKConstants.CONF.DB.KEY_FETCHSIZE);
				statement.setFetchSize(fetchSize);
			}

		} catch (SQLException e) {
		}
	}

	/**
	 * Controlla che la connessione sia attiva e la rinnova in caso di necessita'
	 */
	private void reset()
	throws IllegalOperationException, DbInitializationException {
		if(isClosed){
			throw new IllegalOperationException("Impossibile rinnovare la connessione dopo che il connector è stato chiuso!");
		}

		try {
			if(connection == null || connection.isClosed()){
				this.setupConnection();
			}
		} catch (Exception e) {
			throw new DbInitializationException("Impossibile rinnovare la connessione", e);
		}

		lastInsertId = 0;
		lastUpdatedRows = 0;
	}
}
