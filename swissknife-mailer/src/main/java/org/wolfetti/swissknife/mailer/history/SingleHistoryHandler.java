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

import java.io.File;

import org.wolfetti.swissknife.common.utils.StringUtils;
import org.wolfetti.swissknife.db.DbConnector;
import org.wolfetti.swissknife.db.utils.SqlFormatter;
import org.wolfetti.swissknife.mailer.Attachment;
import org.wolfetti.swissknife.mailer.Mail;
import org.wolfetti.swissknife.mailer.exceptions.MailerException;

/**
 * Handler dello storico gestito in una sola tabella per entit&agrave;.
 *
 * @author Fabio Frijo
 */
public class SingleHistoryHandler implements HistoryHandler {
	private final static String MAIL_SQL = "INSERT INTO `mail_sent` VALUES ('{0}','{1}','{2}','{3}','{4}','{5}','{6}',{7}, NOW())";
	private final static String ATTACHMENT_SQL = "INSERT INTO `mail_sent_files` VALUES ('{0}','{1}','{2}',{3},?)";

	/**
	 * @{inheritDocs}
	 */
	@Override
	public void save(DbConnector connector, Mail m)
	throws MailerException {
		String to = StringUtils.fromListToCsv(m.getTo());
		String cc = StringUtils.fromListToCsv(m.getCc());
		String bcc = StringUtils.fromListToCsv(m.getBcc());

		try {
			String mailSql = SqlFormatter.format(
				MAIL_SQL,
				m.getId(), m.getSubject(), m.getBody(),
				m.getFrom(), to, cc, bcc, m.isHtml()
			);

			connector.write(mailSql);

			for(Attachment a : m.getAttachments()){
				String attachSql = SqlFormatter.format(
					ATTACHMENT_SQL,
					a.getMailId(), a.getFileName(),
					a.getMimeType(), a.getSize()
				);

				connector.write(attachSql, new File[]{a.getData()});
			}

			if(connector.isTransaction()){
				connector.commit();
			}

		} catch (Exception e) {
			throw new MailerException("Impossibile effettuare il salvataggio della mail '" + m.getId() + "' nello storico: " + e.getMessage() , e);
		}
	}
}
