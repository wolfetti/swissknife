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
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.wolfetti.swissknife.db.DbConnector;
import org.wolfetti.swissknife.mailer.exceptions.MailerException;
import org.wolfetti.swissknife.mailer.history.HistoryHandler;
import org.wolfetti.swissknife.mailer.history.SingleHistoryHandler;
import org.wolfetti.swissknife.mailer.queue.QueueHandler;
import org.wolfetti.swissknife.mailer.queue.SingleQueueHandler;
import org.wolfetti.swissknife.mailer.smtp.SmtpAuthenticator;
import org.wolfetti.swissknife.mailer.smtp.SmtpData;

/**
 * Mailer che effettua l'invio di una mail e salva lo storico in una tabella singola.
 *
 * @author Fabio Frijo
 */
public final class Mailer extends AbstractMailer {

	/**
	 * Autenticazione al server SMTP
	 */
	private SmtpAuthenticator auth;

	/**
	 * Costruttore che imposta l'{@link HistoryHandler} a {@link SingleHistoryHandler}
	 * e l'{@link QueueHandler} a {@link SingleQueueHandler}.
	 *
	 * @param connector
	 * 	Il {@link DbConnector} con il quale effettuare le operazioni su database.
	 *
	 * @param data
	 * 	L'oggetto che contiene i dati per la connessione al server SMTP.
	 */
	public Mailer(DbConnector connector, SmtpData data) {
		super(connector, data);
		auth = new SmtpAuthenticator(data);
	}

	/* (non-Javadoc)
	 * @see com.emc2.general.mailer.AbstractMailer#send(com.emc2.general.mailer.entity.Mail)
	 */
	@Override
	public void send(Mail mail)
	throws MailerException {
		try {
			super.saveInQueue(mail);

			Transport.send(this.getMimeMessage(mail));

			super.removeFromQueue(mail);

			super.saveInHistory(mail);
		} catch (Exception e){
			throw new MailerException("Impossibile inviare il messaggio e-mail <" + mail.getId() + "> " + e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see com.emc2.general.mailer.AbstractMailer#send(com.emc2.general.mailer.entity.Mail)
	 */
	@Override
	public void send(List<Mail> mails)
	throws MailerException {
		for(Mail m : mails){
			this.send(m);
		}
	}

	/**
	 * Metodo statico che istanzia un mailer e imposta il connector in ingresso come
	 * connessione al database.
	 *
	 * @param mail
	 * 	La mail da inviare
	 *
	 * @param connector
	 * 	Il {@link DbConnector} con il quale effettuare le operazioni su database.
	 *
	 * @param data
	 * 	L'oggetto che contiene i dati per la connessione al server SMTP.
	 */
	public static void send (Mail mail, DbConnector connector, SmtpData data)
	throws MailerException {
		Mailer m = new Mailer(connector, data);
		m.send(mail);

		if(connector.isTransaction()){
			try {
				connector.commit();
			} catch (Exception e){
				throw new MailerException("Impoossibile eseguire il commit: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Metodo statico che istanzia un mailer e imposta il connector in ingresso come
	 * connessione al database.
	 *
	 * @param mails
	 * 	La lista contenente le mail da inviare
	 *
	 * @param connector
	 * 	Il {@link DbConnector} con il quale effettuare le operazioni su database.
	 *
	 * @param data
	 * 	L'oggetto che contiene i dati per la connessione al server SMTP.
	 */
	public static void send (List<Mail> mails, DbConnector connector, SmtpData data)
	throws MailerException {
		Mailer m = new Mailer(connector, data);
		m.send(mails);

		if(connector.isTransaction()){
			try {
				connector.commit();
			} catch (Exception e){
				throw new MailerException("Impoossibile eseguire il commit: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Preparazione dell'oggetto properties con le configurazioni del
	 * server SMTP necessarie alla costruzione della mail.
	 *
	 * @return
	 * 	L'oggetto con le configurazioni
	 */
	private Properties getProperties() {
		SmtpData data = this.getSmtpData();
		Properties p = new Properties();

		p.put("mail.transport.protocol", "smtp");
		p.put("mail.smtp.auth", "true");
		p.put("mail.smtp.host", data.getHost());
		p.put("mail.smtp.port", data.getPort());

		return p;
	}

	/**
	 * Costruzione del messaggio mail vero e proprio
	 *
	 * @param mail
	 * 	L'entit&agrave; {@link Mail} contenente le informazioni
	 *
	 * @return
	 * 	Un {@link MimeMessage} completo e pronto all'invio.
	 *
	 * @throws AddressException
	 * @throws MessagingException
	 */
	private MimeMessage getMimeMessage(Mail mail)
	throws AddressException, MessagingException {
		MimeMessage m = new MimeMessage(
			Session.getDefaultInstance(this.getProperties(), auth)
		);

		// From
		m.setFrom(new InternetAddress(mail.getFrom()));

		// Subject
		m.setSubject(mail.getSubject());

		// Body & Attachments

		// Nessun allegato
		if(mail.getAttachments() == null || mail.getAttachments().isEmpty()){
			m.setContent(mail.getBody(), "text/html");
		}

		// Allegati presenti
		else {
			BodyPart bp = new MimeBodyPart();
			Multipart mp = new MimeMultipart();

			// Body
			bp.setContent(mail.getBody(), "text/html");
			mp.addBodyPart(bp);

			// Attachments
			for(Attachment a : mail.getAttachments()){
				bp = new MimeBodyPart();
				bp.setDataHandler(new DataHandler(new FileDataSource(a.getData())));
				bp.setFileName(a.getFileName());

				mp.addBodyPart(bp);
			}

			// Chiusura del messaggio
			m.setContent(mp);
		}

		// To
		for(String to : mail.getTo()){
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		}

		// CC
		for(String cc : mail.getCc()){
			m.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
		}

		// BCC
		for(String bcc : mail.getBcc()){
			m.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
		}

		return m;
	}
}
