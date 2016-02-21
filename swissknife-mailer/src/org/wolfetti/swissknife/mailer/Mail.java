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

import java.util.ArrayList;
import java.util.List;

import org.wolfetti.swissknife.common.business.ApplicationEntity;

/**
 * Bean che rappresenta un messaggio e-mail
 *
 * @author Fabio Frijo
 */
public class Mail extends ApplicationEntity {
	private static final long serialVersionUID = -7880515480591081025L;

	private String id;
	private String subject;
	private String body;
	private String from;
	private List<String> to;
	private List<String> cc;
	private List<String> bcc;
	private List<Attachment> attachments;
	private boolean html;

	public Mail(String id, String subject, String body, String from,
	List<String> to, List<String> cc, List<String> bcc, List<Attachment> attachments,
	boolean html) {
		super();
		this.id = id;
		this.subject = subject;
		this.body = body;
		this.from = from;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.attachments = attachments;
		this.html = html;
	}

	public Mail(){
		this(
			"",
			"",
			"",
			"",
			new ArrayList<String>(),
			new ArrayList<String>(),
			new ArrayList<String>(),
			new ArrayList<Attachment>(),
			true
		);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public void addTo(String to) {
		this.to.add(to);
	}

	public List<String> getCc() {
		return cc;
	}

	public void setCc(List<String> cc) {
		this.cc = cc;
	}

	public void addCc(String cc) {
		this.cc.add(cc);
	}

	public List<String> getBcc() {
		return bcc;
	}

	public void setBcc(List<String> bcc) {
		this.bcc = bcc;
	}

	public void addBcc(String bcc) {
		this.bcc.add(bcc);
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public void addAttachment(Attachment attachment) {
		attachments.add(attachment);
	}

	public boolean isHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (attachments == null ? 0 : attachments.hashCode());
		result = prime * result + (body == null ? 0 : body.hashCode());
		result = prime * result + (cc == null ? 0 : cc.hashCode());
		result = prime * result + (bcc == null ? 0 : bcc.hashCode());
		result = prime * result + (from == null ? 0 : from.hashCode());
		result = prime * result + (html ? 1231 : 1237);
		result = prime * result + (id == null ? 0 : id.hashCode());
		result = prime * result + (subject == null ? 0 : subject.hashCode());
		result = prime * result + (to == null ? 0 : to.hashCode());
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
		Mail other = (Mail) obj;
		if (attachments == null) {
			if (other.attachments != null) {
				return false;
			}
		} else if (!attachments.equals(other.attachments)) {
			return false;
		}
		if (body == null) {
			if (other.body != null) {
				return false;
			}
		} else if (!body.equals(other.body)) {
			return false;
		}
		if (cc == null) {
			if (other.cc != null) {
				return false;
			}
		} else if (!cc.equals(other.cc)) {
			return false;
		}
		if (bcc == null) {
			if (other.bcc != null) {
				return false;
			}
		} else if (!bcc.equals(other.bcc)) {
			return false;
		}
		if (from == null) {
			if (other.from != null) {
				return false;
			}
		} else if (!from.equals(other.from)) {
			return false;
		}
		if (html != other.html) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (subject == null) {
			if (other.subject != null) {
				return false;
			}
		} else if (!subject.equals(other.subject)) {
			return false;
		}
		if (to == null) {
			if (other.to != null) {
				return false;
			}
		} else if (!to.equals(other.to)) {
			return false;
		}
		return true;
	}
}
