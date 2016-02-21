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
package org.wolfetti.swissknife.mailer.smtp;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import org.wolfetti.swissknife.common.utils.StringUtils;


/**
 * Classe utilizzata per l'autenticazione SMTP.
 * @author Fabio Frijo
 */
public class SmtpAuthenticator extends Authenticator {
	private SmtpData data;

	/**
	 * Costruttore che prende in ingresso i dati del server SMTP.
	 *
	 * @param data
	 */
	public SmtpAuthenticator(SmtpData data){
		this.data = data;
	}

	/**
	 * Restituisce l'oggetto per l'autenticazione
	 */
	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		if (StringUtils.isEmptyOrNull(data.getPassword())){
			return null;
		} else {
			return new PasswordAuthentication(data.getUsername(), data.getPassword());
		}
	}
}
