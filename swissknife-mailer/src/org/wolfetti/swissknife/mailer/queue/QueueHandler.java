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
package org.wolfetti.swissknife.mailer.queue;

import org.wolfetti.swissknife.db.DbConnector;
import org.wolfetti.swissknife.mailer.Mail;
import org.wolfetti.swissknife.mailer.exceptions.MailerException;

/**
 * Interfaccia che definisce il gestore della coda.
 *
 * @author Fabio Frijo
 */
public interface QueueHandler {

	/**
	 * Aggiunge la mail alla coda.
	 *
	 * @param connector
	 * 	Il {@link DbConnector} da utilizzare per interagire con il database.
	 * @param mail
	 * 	La mail da aggiungere alla coda.
	 */
	public void queue(DbConnector connector, Mail mail)
	throws MailerException;

	/**
	 * Rimuove la mail dalla coda.
	 *
	 * @param connector
	 * 	Il {@link DbConnector} da utilizzare per interagire con il database.
	 * @param mail
	 * 	La mail da rimuovere dalla coda.
	 */
	public void unqueue(DbConnector connector, Mail mail)
	throws MailerException;
}
