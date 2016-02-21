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
package org.wolfetti.swissknife.common.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Implementation of {@link Log} that maps directly to a
 * <strong>Logger</strong> for log4J version 1.2.
 * <p>
 * Initial configuration of the corresponding Logger instances should be done
 * in the usual manner, as outlined in the Log4J documentation.
 * <p>
 * The reason this logger is distinct from the 1.3 logger is that in version 1.2
 * of Log4J:
 * <ul>
 * <li>class Logger takes Priority parameters not Level parameters.
 * <li>class Level extends Priority
 * </ul>
 * Log4J1.3 is expected to change Level so it no longer extends Priority, which is
 * a non-binary-compatible change. The class generated by compiling this code against
 * log4j 1.2 will therefore not run against log4j 1.3.
 *
 * @author Fabio Frijo
 */
public class Log4jLogger implements Log {

	/**
     * The name of this log instance
     */
    protected String logName = null;

    /** The fully qualified name of the Log4jLogger class. */
    private static final String FQCN = Log4jLogger.class.getName();

    /** Log to this logger */
    private transient Logger logger = null;

    /** Minimal level for trace messages */
    private static Level traceLevel;

    static {
        // Releases of log4j1.2 >= 1.2.12 have Priority.TRACE available, earlier
        // versions do not. If TRACE is not available, then we have to map
        // calls to Log.trace(...) onto the DEBUG level.

        try {
            traceLevel = (Level) Level.class.getDeclaredField("TRACE").get(null);
        } catch(Exception ex) {
            // ok, trace not available
            traceLevel = Level.DEBUG;
        }
    }

    /**
	 * Default constructor.
	 * <br><br>
	 * <i><b>warning:</b> Level is ignored, is only for SystemLogger</i>
	 */
	public Log4jLogger(String name, int level) {
		logName         = name;
		logger          = this.getLogger();
	}

    /**
     * Logs a message with <code>org.apache.log4j.Priority.TRACE</code>.
     * When using a log4j version that does not support the <code>TRACE</code>
     * level, the message will be logged at the <code>DEBUG</code> level.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#trace(Object)
     */
    @Override
	public void trace(Object message) {
        this.getLogger().log(FQCN, traceLevel, message, null );
    }


    /**
     * Logs a message with <code>org.apache.log4j.Priority.TRACE</code>.
     * When using a log4j version that does not support the <code>TRACE</code>
     * level, the message will be logged at the <code>DEBUG</code> level.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#trace(Object, Throwable)
     */
    @Override
	public void trace(Object message, Throwable t) {
        this.getLogger().log(FQCN, traceLevel, message, t );
    }


    /**
     * Logs a message with <code>org.apache.log4j.Priority.DEBUG</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#debug(Object)
     */
    @Override
	public void debug(Object message) {
        this.getLogger().log(FQCN, Level.DEBUG, message, null );
    }

    /**
     * Logs a message with <code>org.apache.log4j.Priority.DEBUG</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#debug(Object, Throwable)
     */
    @Override
	public void debug(Object message, Throwable t) {
        this.getLogger().log(FQCN, Level.DEBUG, message, t );
    }


    /**
     * Logs a message with <code>org.apache.log4j.Priority.INFO</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#info(Object)
     */
    @Override
	public void info(Object message) {
        this.getLogger().log(FQCN, Level.INFO, message, null );
    }


    /**
     * Logs a message with <code>org.apache.log4j.Priority.INFO</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#info(Object, Throwable)
     */
    @Override
	public void info(Object message, Throwable t) {
        this.getLogger().log(FQCN, Level.INFO, message, t );
    }


    /**
     * Logs a message with <code>org.apache.log4j.Priority.WARN</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#warn(Object)
     */
    @Override
	public void warn(Object message) {
        this.getLogger().log(FQCN, Level.WARN, message, null );
    }


    /**
     * Logs a message with <code>org.apache.log4j.Priority.WARN</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#warn(Object, Throwable)
     */
    @Override
	public void warn(Object message, Throwable t) {
        this.getLogger().log(FQCN, Level.WARN, message, t );
    }


    /**
     * Logs a message with <code>org.apache.log4j.Priority.ERROR</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#error(Object)
     */
    @Override
	public void error(Object message) {
        this.getLogger().log(FQCN, Level.ERROR, message, null );
    }


    /**
     * Logs a message with <code>org.apache.log4j.Priority.ERROR</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#error(Object, Throwable)
     */
    @Override
	public void error(Object message, Throwable t) {
        this.getLogger().log(FQCN, Level.ERROR, message, t );
    }


    /**
     * Logs a message with <code>org.apache.log4j.Priority.FATAL</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#fatal(Object)
     */
    @Override
	public void fatal(Object message) {
        this.getLogger().log(FQCN, Level.FATAL, message, null );
    }


    /**
     * Logs a message with <code>org.apache.log4j.Priority.FATAL</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#fatal(Object, Throwable)
     */
    @Override
	public void fatal(Object message, Throwable t) {
        this.getLogger().log(FQCN, Level.FATAL, message, t);
    }

    /**
     * Return the native Logger instance we are using.
     */
    public Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(logName);
        }

        return logger;
    }

    /**
     * Check whether the Log4j Logger used is enabled for <code>DEBUG</code> priority.
     */
    @Override
	public boolean isDebugEnabled() {
        return this.getLogger().isEnabledFor(Level.DEBUG);
    }


     /**
     * Check whether the Log4j Logger used is enabled for <code>ERROR</code> priority.
     */
    @Override
	public boolean isErrorEnabled() {
        return this.getLogger().isEnabledFor(Level.ERROR);
    }


    /**
     * Check whether the Log4j Logger used is enabled for <code>FATAL</code> priority.
     */
    @Override
	public boolean isFatalEnabled() {
        return this.getLogger().isEnabledFor(Level.FATAL);
    }


    /**
     * Check whether the Log4j Logger used is enabled for <code>INFO</code> priority.
     */
    @Override
	public boolean isInfoEnabled() {
        return this.getLogger().isEnabledFor(Level.INFO);
    }


    /**
     * Check whether the Log4j Logger used is enabled for <code>TRACE</code> priority.
     * When using a log4j version that does not support the TRACE level, this call
     * will report whether <code>DEBUG</code> is enabled or not.
     */
    @Override
	public boolean isTraceEnabled() {
        return this.getLogger().isEnabledFor(traceLevel);
    }

    /**
     * Check whether the Log4j Logger used is enabled for <code>WARN</code> priority.
     */
    @Override
	public boolean isWarnEnabled() {
        return this.getLogger().isEnabledFor(Level.WARN);
    }
}
