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
package org.wolfetti.swissknife.mailer.history;

import org.wolfetti.swissknife.db.DbConnector;
import org.wolfetti.swissknife.mailer.Mail;

/**
 * Handler che disabilita il salvataggio dello storico delle mail inviate.
 *
 * @author Fabio Frijo
 */
public class NoHistoryHandler implements HistoryHandler {

	/* (non-Javadoc)
	 * @see com.emc2.general.mailer.history.IHistoryHandler#saveHistory(org.wolfetti.dbconnector.IDbConnector, com.emc2.general.mailer.entity.Mail)
	 */
	@Override
	public void save(DbConnector dbc, Mail m) {}
}
