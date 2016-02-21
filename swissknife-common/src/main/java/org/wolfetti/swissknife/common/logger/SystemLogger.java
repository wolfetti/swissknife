/*
 * Copyright (C) Fabio Frijo.
 *
 * This file is part of swissknife-common.
 *
 * swissknife-common is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * swissknife-common is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with swissknife-common.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wolfetti.swissknife.common.logger;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.wolfetti.swissknife.common.SKConstants;

/**
 * Simple console logger
 *
 * @author Fabio Frijo
 */
public class SystemLogger implements Log {

	 /**
	  * Include the instance name in the log message?
	  */
    static protected boolean showLongName = false;

    /**
     * Include the short name ( last component ) of the logger in the log
     * message. Defaults to true - otherwise we'll be lost in a flood of
     * messages without knowing who sends them.
     */
    static protected boolean showShortName = true;

    /**
     * The name of this log instance
     */
    protected String logName = null;

    /**
     * The current log level
     */
    protected int currentLogLevel;

    /**
     * The short name of this log instance
     */
    private String shortLogName = null;

    /**
     * The output writer stream
     */
    protected PrintStream writer = System.out;

	/**
	 * System logger constructor
	 */
	public SystemLogger(String name, int level) {
		logName = name;
		currentLogLevel = level;
	}

	/* (non-Javadoc)
	 * @see org.wolfetti.swissknife.common.logger.Log#isDebugEnabled()
	 */
	@Override
	public boolean isDebugEnabled() {
		return this.checkLogLevel(LogLevel.DEBUG);
	}

	/* (non-Javadoc)
	 * @see org.wolfetti.swissknife.common.logger.Log#isErrorEnabled()
	 */
	@Override
	public boolean isErrorEnabled() {
		return this.checkLogLevel(LogLevel.ERROR);
	}

	/* (non-Javadoc)
	 * @see org.wolfetti.swissknife.common.logger.Log#isFatalEnabled()
	 */
	@Override
	public boolean isFatalEnabled() {
		return this.checkLogLevel(LogLevel.FATAL);
	}

	/* (non-Javadoc)
	 * @see org.wolfetti.swissknife.common.logger.Log#isInfoEnabled()
	 */
	@Override
	public boolean isInfoEnabled() {
		return this.checkLogLevel(LogLevel.INFO);
	}

	/* (non-Javadoc)
	 * @see org.wolfetti.swissknife.common.logger.Log#isTraceEnabled()
	 */
	@Override
	public boolean isTraceEnabled() {
		return this.checkLogLevel(LogLevel.TRACE);
	}

	/* (non-Javadoc)
	 * @see org.wolfetti.swissknife.common.logger.Log#isWarnEnabled()
	 */
	@Override
	public boolean isWarnEnabled() {
		return this.checkLogLevel(LogLevel.WARN);
	}

	/**
	 * Check if a log level is enabled or not
	 *
	 * @param level The log level to check.
	 * @return
	 */
	protected boolean checkLogLevel(int level){
		switch (currentLogLevel) {
			case LogLevel.OFF	: return false;
			default				: return currentLogLevel <= level;
		}
	}

	/* (non-Javadoc)
	 * @see org.wolfetti.swissknife.common.logger.Log#trace(java.lang.Object)
	 */
	@Override
	public void trace(Object message) {
		this.trace(message, null);
	}

	/* (non-Javadoc)
	 * @see org.wolfetti.swissknife.common.logger.Log#trace(java.lang.Object, java.lang.Throwable)
	 */
	@Override
	public void trace(Object message, Throwable t) {
		if(this.isTraceEnabled()){
			this.log(LogLevel.TRACE, message, t);
		}
	}

	/* (non-Javadoc)
	 * @see org.wolfetti.swissknife.common.logger.Log#debug(java.lang.Object)
	 */
	@Override
	public void debug(Object message) {
		this.debug(message, null);
	}

	/* (non-Javadoc)
	 * @see org.wolfetti.swissknife.common.logger.Log#debug(java.lang.Object, java.lang.Throwable)
	 */
	@Override
	public void debug(Object message, Throwable t) {
		if(this.isDebugEnabled()){
			this.log(LogLevel.DEBUG, message, t);
		}
	}

	/* (non-Javadoc)
	 * @see org.wolfetti.swissknife.common.logger.Log#info(java.lang.Object)
	 */
	@Override
	public void info(Object message) {
		this.info(message, null);
	}

	/* (non-Javadoc)
	 * @see org.wolfetti.swissknife.common.logger.Log#info(java.lang.Object, java.lang.Throwable)
	 */
	@Override
	public void info(Object message, Throwable t) {
		if(this.isInfoEnabled()){
			this.log(LogLevel.INFO, message, t);
		}
	}

	/* (non-Javadoc)
	 * @see org.wolfetti.swissknife.common.logger.Log#warn(java.lang.Object)
	 */
	@Override
	public void warn(Object message) {
		this.warn(message, null);
	}

	/* (non-Javadoc)
	 * @see org.wolfetti.swissknife.common.logger.Log#warn(java.lang.Object, java.lang.Throwable)
	 */
	@Override
	public void warn(Object message, Throwable t) {
		if(this.isWarnEnabled()){
			this.log(LogLevel.WARN, message, t);
		}
	}

	/* (non-Javadoc)
	 * @see org.wolfetti.swissknife.common.logger.Log#error(java.lang.Object)
	 */
	@Override
	public void error(Object message) {
		this.error(message, null);
	}

	/* (non-Javadoc)
	 * @see org.wolfetti.swissknife.common.logger.Log#error(java.lang.Object, java.lang.Throwable)
	 */
	@Override
	public void error(Object message, Throwable t) {
		if(this.isErrorEnabled()){
			this.log(LogLevel.ERROR, message, t);
		}
	}

	/* (non-Javadoc)
	 * @see org.wolfetti.swissknife.common.logger.Log#fatal(java.lang.Object)
	 */
	@Override
	public void fatal(Object message) {
		this.fatal(message, null);
	}

	/* (non-Javadoc)
	 * @see org.wolfetti.swissknife.common.logger.Log#fatal(java.lang.Object, java.lang.Throwable)
	 */
	@Override
	public void fatal(Object message, Throwable t) {
		if(this.isFatalEnabled()){
			this.log(LogLevel.FATAL, message, t);
		}
	}

	/**
     * <p> Do the actual logging.
     * This method assembles the message
     * and then calls <code>write()</code> to cause it to be written.</p>
     *
     * @param level One of the {@link LogLevel}.XXX constants defining the log level
     * @param message The message itself (typically a String)
     * @param t The exception whose stack trace should be logged
     */
    protected void log(int level, Object message, Throwable t) {

        // Use a string buffer for better performance
        StringBuffer msgBuf = new StringBuffer();

        // Append a readable representation of the log level
        switch(level) {
            case LogLevel.TRACE: msgBuf.append("[TRACE] "); break;
            case LogLevel.DEBUG: msgBuf.append("[DEBUG] "); break;
            case LogLevel.INFO:  msgBuf.append(" [INFO] "); break;
            case LogLevel.WARN:  msgBuf.append(" [WARN] "); break;
            case LogLevel.ERROR: msgBuf.append("[ERROR] "); break;
            case LogLevel.FATAL: msgBuf.append("[FATAL] "); break;
        }

        // Append the name of the log instance if so configured
        if(showShortName) {
            if(shortLogName == null) {
                // Cut all but the last component of the name for both styles
                shortLogName = logName.substring(logName.lastIndexOf(".") + 1);
                shortLogName = shortLogName.substring(shortLogName.lastIndexOf("/") + 1);
            }

            msgBuf.append(String.valueOf(shortLogName)).append(" - ");
        } else if(showLongName) {
            msgBuf.append(String.valueOf(logName)).append(" - ");
        }

        // Append the message
        msgBuf.append(String.valueOf(message));

        // Append stack trace if not null
        if(t != null) {
            msgBuf.append(SKConstants.NEW_LINE);

            StringWriter sw = new java.io.StringWriter(1024);
            PrintWriter pw = new java.io.PrintWriter(sw);

            t.printStackTrace(pw);
            pw.close();

            msgBuf.append(sw.toString());
        }

        // Print to the appropriate destination
        writer.println(msgBuf.toString());
    }
}
