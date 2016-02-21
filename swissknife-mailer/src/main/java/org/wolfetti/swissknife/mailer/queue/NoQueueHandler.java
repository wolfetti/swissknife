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

/**
 * Handler che disabilita l'utilizzo della coda delle mail inviate.
 *
 * @author Fabio Frijo
 */
public class NoQueueHandler implements QueueHandler {

	/**
	 * @{inheritDocs}
	 */
	@Override
	public void add(DbConnector connector, Mail mail) {}

	/**
	 * @{inheritDocs}
	 */
	@Override
	public void remove(DbConnector connector, Mail mail) {}
}
