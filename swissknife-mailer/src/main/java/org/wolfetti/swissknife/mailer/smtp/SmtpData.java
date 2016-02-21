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

import org.wolfetti.swissknife.common.business.ApplicationEntity;

/**
 * Classe che contiene i dati per la connessione al server SMTP
 *
 * @author Fabio Frijo
 */
public class SmtpData extends ApplicationEntity {
	private static final long serialVersionUID = -493895753346997777L;

	private String host;
	private int port;
	private String username;
	private String password;
	private SmtpSecurity security;

	public SmtpData(String host, int port, String username, String password, SmtpSecurity security) {
		super();
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.security = security;
	}

	public SmtpData(){
		this("", 25, "", "", SmtpSecurity.PLAIN);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public SmtpSecurity getSecurity() {
		return security;
	}

	public void setSecurity(SmtpSecurity security) {
		this.security = security;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (host == null ? 0 : host.hashCode());
		result = prime * result
				+ (password == null ? 0 : password.hashCode());
		result = prime * result + port;
		result = prime * result
				+ (security == null ? 0 : security.hashCode());
		result = prime * result
				+ (username == null ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		SmtpData other = (SmtpData) obj;
		if (host == null) {
			if (other.host != null) {
				return false;
			}
		} else if (!host.equals(other.host)) {
			return false;
		}
		if (password == null) {
			if (other.password != null) {
				return false;
			}
		} else if (!password.equals(other.password)) {
			return false;
		}
		if (port != other.port) {
			return false;
		}
		if (security != other.security) {
			return false;
		}
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equals(other.username)) {
			return false;
		}
		return true;
	}
}
