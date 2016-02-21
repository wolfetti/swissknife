package org.wolfetti.swissknife.jasper.reports;

import org.wolfetti.swissknife.common.business.ApplicationRuntimeException;

public class JReportException extends ApplicationRuntimeException {
	private static final long serialVersionUID = -2727408721078742418L;

	public JReportException(String message) {
		super(message);
	}

	public JReportException(String message, Throwable cause) {
		super(message, cause);
	}
}
