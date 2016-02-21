/*
 * Copyright(c) 2013 Fabio Frijo.
 *
 * This file is part of swissknife-mailer.
 *
 * swissknife-mailer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * swissknife-mailer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with swissknife-mailer.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wolfetti.swissknife.mailer;

import java.util.List;

import org.wolfetti.swissknife.db.DbConnector;
import org.wolfetti.swissknife.mailer.exceptions.MailerException;
import org.wolfetti.swissknife.mailer.history.HistoryHandler;
import org.wolfetti.swissknife.mailer.history.SingleHistoryHandler;
import org.wolfetti.swissknife.mailer.queue.QueueHandler;
import org.wolfetti.swissknife.mailer.queue.SingleQueueHandler;
import org.wolfetti.swissknife.mailer.smtp.SmtpData;

/**
 * Classe che definisce i metodi da utilizzare in un mailer.
 *
 * @author Fabio Frijo
 */
public abstract class AbstractMailer {

	/**
	 * I dati per la connessione al server SMTP
	 */
	private SmtpData smtpData;

	/**
	 * Il gestore del salvataggio dello storico delle mail inviate.
	 */
	private HistoryHandler historyHandler;

	/**
	 * Il gestore della coda delle mail.
	 */
	private QueueHandler queueHandler;

	/**
	 * Il {@link DbConnector} da utilizzare per interagire con il database.
	 */
	protected DbConnector connector;

	/**
	 * Il logger di istanza.
	 */
	// TODO logger

	/**
	 * Metodo che effettua l'invio della mail, gestendo la coda e lo storico.
	 *
	 * @param mail
	 * 	La mail da inviare
	 */
	public abstract void send(Mail mail)
	throws MailerException;

	/**
	 * Metodo che effettua l'invio della mail, gestendo la coda e lo storico.
	 *
	 * @param mails
	 * 	La lista di mail da inviare
	 */
	public abstract void send(List<Mail> mails)
	throws MailerException;

	/**
	 * Costruttore che imposta l'{@link HistoryHandler} a {@link SingleHistoryHandler} e
	 * l'{@link QueueHandler} a {@link SingleQueueHandler}.
	 *
	 * @param connector
	 * 	Il {@link DbConnector} con il quale effettuare le operazioni su database.
	 *
	 * @param data
	 * 	L'oggetto che contiene i dati per la connessione al server SMTP.
	 */
	protected AbstractMailer(DbConnector connector, SmtpData data){
		this(new SingleHistoryHandler(), new SingleQueueHandler(), connector, data);
	}

	/**
	 * Costruisce l'oggetto Mailer, utilizzando {@link HistoryHandler} e
	 * {@link QueueHandler} definiti dall'utilizzatore.
	 *
	 * @param historyHandler
	 * 	L'{@link HistoryHandler} da utilizzare per la gestione dello storico.
	 *
	 * @param queueHandler
	 * 	L'{@link QueueHandler} da utilizzare per la gestione della coda.
	 *
	 * @param connector
	 * 	Il {@link DbConnector} con il quale effettuare le operazioni su database.
	 *
	 * @param data
	 * 	L'oggetto che contiene i dati per la connessione al server SMTP.
	 */
	protected AbstractMailer(HistoryHandler historyHandler, QueueHandler queueHandler, DbConnector connector, SmtpData data){
		this.historyHandler = historyHandler;
		this.queueHandler = queueHandler;
		this.connector = connector;
		smtpData = data;
	}

	/**
	 * Impostazione programmatica dell'{@link HistoryHandler}.
	 *
	 * @param handler
	 * 	L'{@link HistoryHandler} da utilizzare per la gestione dello storico.
	 */
	public void setHistoryHandler(HistoryHandler handler){
		historyHandler = handler;
	}

	/**
	 * Impostazione programmatica dell'{@link QueueHandler}.
	 *
	 * @param handler
	 * 	L'{@link QueueHandler} da utilizzare per la gestione della coda.
	 */
	public void setQueueHandler(QueueHandler handler){
		queueHandler = handler;
	}

	/**
	 * Metodo che effettua il salvataggio della mail nella tabella di storico.
	 *
	 * @param m
	 * 	La mail da salvare nello storico
	 */
	protected void saveInHistory(Mail m)
	throws MailerException {
		historyHandler.save(connector, m);
	}

	/**
	 * Metodo che effettua il salvataggio della mail nella tabella di storico.
	 *
	 * @param m
	 * 	La mail da salvare nello storico
	 */
	protected void saveInQueue(Mail m)
	throws MailerException {
		queueHandler.add(connector, m);
	}

	/**
	 * Metodo che effettua il salvataggio della mail nella tabella di storico.
	 *
	 * @param m
	 * 	La mail da salvare nello storico
	 */
	protected void removeFromQueue(Mail m)
	throws MailerException {
		queueHandler.remove(connector, m);
	}

	/**
	 * Restitusce l'oggetto SmtpData per la connessione al server.
	 *
	 * @return
	 * 	L'oggetto {@link SmtpData}.
	 */
	protected SmtpData getSmtpData(){
		return smtpData;
	}
}
