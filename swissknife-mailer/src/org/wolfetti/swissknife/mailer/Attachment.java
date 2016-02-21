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

import java.io.File;

import org.wolfetti.swissknife.common.business.ApplicationEntity;

/**
 * Bean che rappresenta un allegato
 *
 * @author Fabio Frijo
 */
public class Attachment extends ApplicationEntity {
	private static final long serialVersionUID = -6113570929557775027L;

	private String mailId;
	private String fileName;
	private String mimeType;
	private long size;
	private File data;

	public Attachment(String mailId, String fileName, String mimeType, int size, File data) {
		super();
		this.mailId = mailId;
		this.fileName = fileName;
		this.mimeType = mimeType;
		this.size = size;
		this.data = data;
	}

	public Attachment(){
		this("", "", "", 0, null);
	}

	public String getMailId() {
		return mailId;
	}

	public void setMailId(String mailId) {
		this.mailId = mailId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public File getData() {
		return data;
	}

	public void setData(File data) {
		this.data = data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (data == null ? 0 : data.hashCode());
		result = prime * result
				+ (fileName == null ? 0 : fileName.hashCode());
		result = prime * result + (mailId == null ? 0 : mailId.hashCode());
		result = prime * result
				+ (mimeType == null ? 0 : mimeType.hashCode());
		result = prime * result + (int) (size ^ size >>> 32);
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
		Attachment other = (Attachment) obj;
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
		if (fileName == null) {
			if (other.fileName != null) {
				return false;
			}
		} else if (!fileName.equals(other.fileName)) {
			return false;
		}
		if (mailId == null) {
			if (other.mailId != null) {
				return false;
			}
		} else if (!mailId.equals(other.mailId)) {
			return false;
		}
		if (mimeType == null) {
			if (other.mimeType != null) {
				return false;
			}
		} else if (!mimeType.equals(other.mimeType)) {
			return false;
		}
		if (size != other.size) {
			return false;
		}
		return true;
	}
}
