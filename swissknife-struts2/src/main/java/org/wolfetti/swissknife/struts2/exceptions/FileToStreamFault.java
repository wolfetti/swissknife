package org.wolfetti.swissknife.struts2.exceptions;

import org.wolfetti.swissknife.common.business.ApplicationRuntimeException;

public class FileToStreamFault extends ApplicationRuntimeException {
	private static final long serialVersionUID = 4658772152890885357L;

	public FileToStreamFault() {
		super();
	}

	public FileToStreamFault(String message, Throwable cause) {
		super(message, cause);
	}

	public FileToStreamFault(String message) {
		super(message);
	}

	public FileToStreamFault(Throwable cause) {
		super(cause);
	}
}
