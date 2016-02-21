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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.wolfetti.swissknife.common.SKConstants;
import org.wolfetti.swissknife.common.logger.exceptions.LogInitializationException;
import org.wolfetti.swissknife.common.utils.FileUtils;

/**
 * LogFactory allow developers to create and manage log instances
 *
 * @author Fabio Frijo
 */
public final class LogFactory {

	/**
	 * Configuration attributes.
	 */
	private Hashtable<String, String> configuration = new Hashtable<String, String>();

	/**
	 * The {@link Log} instances that have
	 * already been created, keyed by logger name.
	 */
	private Hashtable<String, Log> instances = new Hashtable<String, Log>();

	/**
	 * The unique instance of this factory
	 */
	private static LogFactory logFactory;
	
	/**
	 * Flag used to avoid multiple setup of SystemLogger showXXXName variables.
	 */
	private static boolean sysLogShowNameSetted = false; 

	/**
	 * The current log level.
	 */
	private int currentLogLevel = SKConstants.CONF.LOG.DEFAULT_LEVEL;

	/**
	 * The one-argument constructor of the
	 * {@link Log}
	 * implementation class that will be used to create new instances.
	 * This value is initialized by <code>getLogConstructor()</code>,
	 * and then returned repeatedly.
	 */
	private Constructor<?> logConstructor = null;

	/**
	 * The signature of the Constructor to be used.
	 */
	private Class<?> logConstructorSignature[] = {
		java.lang.String.class,
		java.lang.Integer.TYPE
	};

	/**
	 * Private constructor, this class is a singeton.
	 */
	private LogFactory() {
		Properties props = FileUtils.getPropertiesFile(
			getContextClassLoaderInternal(),
			SKConstants.CONF.GLOBAL_FILENAMES,
			SKConstants.CONF.LOG.FILENAMES
		);

		if(props != null) {
            Enumeration<?> names = props.propertyNames();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();

                if(name != null && name.startsWith(SKConstants.CONF.LOG.PREFIX)){
                	String value = props.getProperty(name);
                	this.configuration.put(name, value);
                }
            }
        }
	}

	/**
	 * Close all resources.
	 */
	public static void closeAll(){
		if(logFactory == null){
			return;
		}

		logFactory.configuration.clear();
		logFactory.configuration = null;

		logFactory.instances.clear();
		logFactory.instances = null;

		logFactory = null;

		System.gc();
	}

	/**
	 * Attempts to load the given class, find a suitable constructor,
	 * and instantiate an instance of Log.
	 *
	 * @param logAdapterClassName classname of the Log implementation
	 *
	 * @param name  argument to pass to the Log implementation's
	 * constructor
	 *
	 * @param affectState  <code>true</code> if this object's state should
	 * be affected by this method call, <code>false</code> otherwise.
	 *
	 * @return  an instance of the given class, or null if the logging
	 * library associated with the specified adapter is not available.
	 *
	 * @throws LogInitializationException if there was a serious error with
	 * configuration and the handleFlawedDiscovery method decided this
	 * problem was fatal.
	 */
	private Log createLogFromClass(String logAdapterClassName, String name, int level, boolean affectState)
	throws LogInitializationException {
		Object[] params = { name, level };
		Log logAdapter = null;
		Constructor<?> constructor = null;
		ClassLoader currentCL = getBaseClassLoader();
		
		// For SystemLogger, if is configured the showName config parameter, it's setted here.
		if(!sysLogShowNameSetted){
			if(SKConstants.CONF.LOG.SYSLOG_NAME.equals(logAdapterClassName)){
				String nameConf = this.configuration.get(
					SKConstants.CONF.LOG.KEY_SYSLOG_SHOWNAME
				);
				
				if(nameConf != null){
					nameConf = nameConf.trim().toUpperCase();
					
					switch (nameConf) {
						case "NONE":
							SystemLogger.showShortName = false;
							SystemLogger.showLongName = false;
						break;
						
						case "FULL":
							SystemLogger.showShortName = false;
							SystemLogger.showLongName = true;
						break;
						
						case "SHORT":
						default:
							SystemLogger.showShortName = true;
							SystemLogger.showLongName = false;
						break;
					}
				}

				sysLogShowNameSetted = true;
			} 
		}

		for(;;) {
			try {
				Class<?> c = null;
				try {
					c = Class.forName(logAdapterClassName, true, currentCL);
				} catch (ClassNotFoundException originalClassNotFoundException) {
					// The current classloader was unable to find the log adapter
					// in this or any ancestor classloader. There's no point in
					// trying higher up in the hierarchy in this case..

					try {
						// Try the class classloader.
						// This may work in cases where the TCCL
						// does not contain the code executed or JCL.
						// This behaviour indicates that the application
						// classloading strategy is not consistent with the
						// Java 1.2 classloading guidelines but JCL can
						// and so should handle this case.
						c = Class.forName(logAdapterClassName);
					} catch (ClassNotFoundException secondaryClassNotFoundException) {
						// no point continuing: this adapter isn't available
						break;
					}
				}

				constructor = c.getConstructor(this.logConstructorSignature);
				Object o = constructor.newInstance(params);

				// Note that we do this test after trying to create an instance
				// [rather than testing Log.class.isAssignableFrom(c)] so that
				// we don't complain about Log hierarchy problems when the
				// adapter couldn't be instantiated anyway.
				if (o instanceof Log) {
					logAdapter = (Log) o;
					break;
				}
			} catch (NoClassDefFoundError e) {
				// We were able to load the adapter but it had references to
				// other classes that could not be found. This simply means that
				// the underlying logger library is not present in this or any
				// ancestor classloader. There's no point in trying higher up
				// in the hierarchy in this case..
				break;
			} catch (ExceptionInInitializerError e) {
				// A static initializer block or the initializer code associated
				// with a static variable on the log adapter class has thrown
				// an exception.
				//
				// We treat this as meaning the adapter's underlying logging
				// library could not be found.
				break;
			} catch(Throwable t) {
				throw new LogInitializationException(
					"An adapter class has been found and its underlying lib is present too, " +
					"but there are multiple Log interface classes available making it " +
					"impossible to cast to the type the caller wanted.",
				t);
			}

			if (currentCL == null) {
				break;
			}

			// try the parent classloader
			// currentCL = currentCL.getParent();
			currentCL = getParentClassLoader(currentCL);
		}

		if (logAdapter != null && affectState) {
			// We've succeeded, so set instance fields
			this.logConstructor = constructor;
		}

		return logAdapter;
	}

	/**
	 * Gets the user defined log level in case of SystemLogger utilization.
	 */
	private int getUserDefinedLogLevel(){
		String levelString = this.configuration.get(
			SKConstants.CONF.LOG.KEY_SYSLOG_LEVEL
		);

		if(levelString == null){
			return SKConstants.CONF.LOG.DEFAULT_LEVEL;
		}

		levelString = levelString.trim().toUpperCase();

		switch (levelString) {
			case "OFF"   : return LogLevel.OFF;
			case "TRACE" : return LogLevel.TRACE;
			case "DEBUG" : return LogLevel.DEBUG;
			case "INFO"  : return LogLevel.INFO;
			case "WARN"  : return LogLevel.WARN;
			case "ERROR" : return LogLevel.ERROR;
			case "FATAL" : return LogLevel.FATAL;
			default      : return SKConstants.CONF.LOG.DEFAULT_LEVEL;
		}
	}

	/**
	 * Initialize the unique istance of logFactory, if is not.
	 */
	private static void initializeLogFactory(){
		if(logFactory == null){
			logFactory = new LogFactory();
		}
	}

	/**
	 * Convenience method to return a named logger, without the application
	 * having to care about factories.
	 *
	 * @param clazz Class from which a log name will be derived
	 *
	 * @exception LogInitializationException if a suitable <code>Log</code>
	 *  instance cannot be returned
	 */
	public static Log getLog(Class<?> clazz)
	throws LogInitializationException {
		initializeLogFactory();
		return logFactory.getInstance(clazz, logFactory.currentLogLevel);
	}

	/**
	 * Convenience method to return a named logger, without the application
	 * having to care about factories.
	 *
	 * @param clazz Class from which a log name will be derived
	 *
	 * @exception LogInitializationException if a suitable <code>Log</code>
	 *  instance cannot be returned
	 */
	public static Log getLog(Class<?> clazz, int logLevel)
	throws LogInitializationException {
		initializeLogFactory();
		return logFactory.getInstance(clazz, logLevel);
	}

	/**
	 * Convenience method to return a named logger, without the application
	 * having to care about factories.
	 *
	 * @param name A log name
	 *
	 * @exception LogInitializationException if a suitable <code>Log</code>
	 *  instance cannot be returned
	 */
	public static Log getLog(String name, int logLevel)
	throws LogInitializationException {
		initializeLogFactory();
		return logFactory.getInstance(name, logLevel);
	}

	/**
	 * Convenience method to return a named logger, without the application
	 * having to care about factories.
	 *
	 * @param name A log name
	 *
	 * @exception LogInitializationException if a suitable <code>Log</code>
	 *  instance cannot be returned
	 */
	public static Log getLog(String name)
	throws LogInitializationException {
		initializeLogFactory();
		return logFactory.getInstance(name, logFactory.currentLogLevel);
	}

	/**
	 * Convenience method to derive a name from the specified class and
	 * call <code>getInstance(String)</code> with it.
	 *
	 * @param clazz Class for which a suitable Log name will be derived
	 *
	 * @exception LogInitializationException if a suitable <code>Log</code>
	 *  instance cannot be returned
	 */
	private Log getInstance(Class<?> clazz, int level)
	throws LogInitializationException {
		return getInstance(clazz.getName(), level);
	}

	/**
	 * Convenience method to derive a name from the specified class and
	 * call <code>getInstance(String)</code> with it.
	 *
	 * @param name Log name
	 *
	 * @exception LogInitializationException if a suitable <code>Log</code>
	 *  instance cannot be returned
	 */
	private Log getInstance(String name, int level)
	throws LogInitializationException {
		Log instance = this.instances.get(name);

		if (instance == null) {
			instance = this.newInstance(name, level);
			this.instances.put(name, instance);
		}

		return instance;
	}

	/**
	 * Create and return a new {@link Log}
	 * instance for the specified name.
	 *
	 * @param name Name of the new logger
	 *
	 * @exception LogInitializationException if a new instance cannot
	 *  be created
	 */
	private Log newInstance(String name, int level)
	throws LogInitializationException {
		Log instance = null;

		try {
			if (this.logConstructor == null) {
                instance = this.discoverLogImplementation(name, level);
            }
            else {
                Object params[] = { name, level };
                instance = (Log) this.logConstructor.newInstance(params);
            }

			return instance;

		} catch (InvocationTargetException e) {
			// A problem occurred invoking the Constructor or Method
			// previously discovered
			Throwable c = e.getTargetException();
			if (c != null) {
				throw new LogInitializationException(c.getMessage(), c);
			} else {
				throw new LogInitializationException(e.getMessage(), e);
			}
		} catch (Throwable t) {
			// A problem occurred invoking the Constructor or Method
			// previously discovered
			throw new LogInitializationException(t.getMessage(), t);
		}
	}

   /**
    * Attempts to create a Log instance for the given category name.
    * Follows the discovery process described in the class javadoc.
    *
    * @param name the name of the log category
    *
    * @throws LogInitializationException if an error in discovery occurs,
    * or if no adapter at all can be instantiated
    */
   private Log discoverLogImplementation(String name, int level)
   throws LogInitializationException {
       Log result = null;

       // See if the user specified the Log implementation to use
       String specifiedLogClassName = this.findUserSpecifiedLogClassName();

       if (specifiedLogClassName != null) {

    	   // If we are using SystemLogger we search for a user defined log level
           if(SKConstants.CONF.LOG.SYSLOG_NAME.equals(specifiedLogClassName)){
        	   this.currentLogLevel = this.getUserDefinedLogLevel();
           }

           result = this.createLogFromClass(specifiedLogClassName, name, level, true);

           if (result == null) {
               StringBuffer messageBuffer =  new StringBuffer("User-specified log class '");
               messageBuffer.append(specifiedLogClassName);
               messageBuffer.append("' cannot be found or is not useable.");

               throw new LogInitializationException(messageBuffer.toString());
           }

           return result;
       }

       // No user specified log; try to use SystemLogger
       this.currentLogLevel = this.getUserDefinedLogLevel();
       result = this.createLogFromClass(SKConstants.CONF.LOG.SYSLOG_NAME, name, level, true);

       if (result == null) {
           throw new LogInitializationException("No suitable Log implementation");
       }

       return result;
   }

   /**
    * Checks the attribute map for a Log implementation specified by the user
    * under the property name {@link SKConstants.CONF.LOG#K}.
    *
    * @return classname specified by the user, or <code>null</code>
    */
   private String findUserSpecifiedLogClassName() {
       String specifiedClass = this.configuration.get(SKConstants.CONF.LOG.KEY_LOGGER);

       // Remove any whitespace; it's never valid in a classname so its
       // presence just means a user mistake. As we know what they meant,
       // we may as well strip the spaces.
       if (specifiedClass != null) {
           specifiedClass = specifiedClass.trim();
       }

       return specifiedClass;
   }

	/**
	 * Calls LogFactory.directGetContextClassLoader under the control of an
	 * AccessController class. This means that java code running under a
	 * security manager that forbids access to ClassLoaders will still work
	 * if this class is given appropriate privileges, even when the caller
	 * doesn't have such privileges. Without using an AccessController, the
	 * the entire call stack must have the privilege before the call is
	 * allowed.
	 *
	 * @return the context classloader associated with the current thread,
	 * or null if security doesn't allow it.
	 *
	 * @throws LogInitializationException if there was some weird error while
	 * attempting to get the context classloader.
	 *
	 * @throws SecurityException if the current java security policy doesn't
	 * allow this class to access the context classloader.
	 */
	private static ClassLoader getContextClassLoaderInternal()
	throws LogInitializationException {
		return (ClassLoader) AccessController.doPrivileged(
			new PrivilegedAction<Object>() {
				@Override
				public Object run() {
					return directGetContextClassLoader();
				}
			}
		);
	}

	/**
	 * Return the classloader from which we should try to load the logging
	 * adapter classes.
	 * <p>
	 * This method usually returns the context classloader. However if it
	 * is discovered that the classloader which loaded this class is a child
	 * of the context classloader <i>and</i> the allowFlawedContext option
	 * has been set then the classloader which loaded this class is returned
	 * instead.
	 * <p>
	 * The only time when the classloader which loaded this class is a
	 * descendant (rather than the same as or an ancestor of the context
	 * classloader) is when an app has created custom classloaders but
	 * failed to correctly set the context classloader. This is a bug in
	 * the calling application; however we provide the option for JCL to
	 * simply generate a warning rather than fail outright.
	 *
	 */
	private static ClassLoader getBaseClassLoader()
	throws LogInitializationException {
		ClassLoader thisClassLoader = getClassLoader(LogFactory.class);
		ClassLoader contextClassLoader = getContextClassLoaderInternal();
		ClassLoader baseClassLoader = getLowestClassLoader(contextClassLoader, thisClassLoader);

		if (baseClassLoader == null) {

			// The two classloaders are not part of a parent child relationship.
			// In some classloading setups (e.g. JBoss with its
			// UnifiedLoaderRepository) this can still work, so if user hasn't
			// forbidden it, just return the contextClassLoader.
			throw new LogInitializationException(
				"Bad classloader hierarchy; LogFactoryImpl was loaded via" +
				" a classloader that is not related to the current context" +
				" classloader."
			);
		}

		if (baseClassLoader != contextClassLoader) {

			// We really should just use the contextClassLoader as the starting
			// point for scanning for log adapter classes. However it is expected
			// that there are a number of broken systems out there which create
			// custom classloaders but fail to set the context classloader so
			// we handle those flawed systems anyway.
			throw new LogInitializationException(
				"Bad classloader hierarchy; LogFactory was loaded via" +
				" a classloader that is not related to the current context" +
				" classloader."
			);
		}

		return baseClassLoader;
	}

	/**
	 * Safely get access to the classloader for the specified class.
	 * <p>
	 * Theoretically, calling getClassLoader can throw a security exception,
	 * and so should be done under an AccessController in order to provide
	 * maximum flexibility. However in practice people don't appear to use
	 * security policies that forbid getClassLoader calls. So for the moment
	 * all code is written to call this method rather than Class.getClassLoader,
	 * so that we could put AccessController stuff in this method without any
	 * disruption later if we need to.
	 * <p>
	 * Even when using an AccessController, however, this method can still
	 * throw SecurityException. Commons-logging basically relies on the
	 * ability to access classloaders, ie a policy that forbids all
	 * classloader access will also prevent commons-logging from working:
	 * currently this method will throw an exception preventing the entire app
	 * from starting up. Maybe it would be good to detect this situation and
	 * just disable all commons-logging? Not high priority though - as stated
	 * above, security policies that prevent classloader access aren't common.
	 * <p>
	 * Note that returning an object fetched via an AccessController would
	 * technically be a security flaw anyway; untrusted code that has access
	 * to a trusted JCL library could use it to fetch the classloader for
	 * a class even when forbidden to do so directly.
	 *
	 * @since 1.1
	 */
	private static ClassLoader getClassLoader(Class<?> clazz) {
		try {
			return clazz.getClassLoader();
		} catch(SecurityException ex) {
			throw ex;
		}
	}

	/**
	 * Given two related classloaders, return the one which is a child of
	 * the other.
	 * <p>
	 * @param c1 is a classloader (including the null classloader)
	 * @param c2 is a classloader (including the null classloader)
	 *
	 * @return c1 if it has c2 as an ancestor, c2 if it has c1 as an ancestor,
	 * and null if neither is an ancestor of the other.
	 */
	private static ClassLoader getLowestClassLoader(ClassLoader c1, ClassLoader c2) {
		if (c1 == null) {
			return c2;
		}

		if (c2 == null) {
			return c1;
		}

		ClassLoader current;

		// scan c1's ancestors to find c2
		current = c1;
		while (current != null) {
			if (current == c2) {
				return c1;
			}
			current = current.getParent();
		}

		// scan c2's ancestors to find c1
		current = c2;
		while (current != null) {
			if (current == c1) {
				return c2;
			}
			current = current.getParent();
		}

		return null;
	}

	/**
	 * Return the thread context class loader if available; otherwise return
	 * null.
	 * <p>
	 * Most/all code should call getContextClassLoaderInternal rather than
	 * calling this method directly.
	 * <p>
	 * The thread context class loader is available for JDK 1.2
	 * or later, if certain security conditions are met.
	 * <p>
	 * Note that no internal logging is done within this method because
	 * this method is called every time LogFactory.getLogger() is called,
	 * and we don't want too much output generated here.
	 *
	 * @exception LogInitializationException if a suitable class loader
	 * cannot be identified.
	 *
	 * @exception SecurityException if the java security policy forbids
	 * access to the context classloader from one of the classes in the
	 * current call stack.
	 * @since 1.1
	 */
	private static ClassLoader directGetContextClassLoader()
	throws LogInitializationException {
		ClassLoader classLoader = null;

		try {
			// Are we running on a JDK 1.2 or later system?
			Method method = Thread.class.getMethod("getContextClassLoader", (Class[]) null);

			// Get the thread context class loader (if there is one)
			try {
				classLoader = (ClassLoader) method.invoke(Thread.currentThread(), (Object[]) null);
			} catch (IllegalAccessException e) {
				throw new LogInitializationException("Unexpected IllegalAccessException", e);
			} catch (InvocationTargetException e) {
				/*
				 * InvocationTargetException is thrown by 'invoke' when
				 * the method being invoked (getContextClassLoader) throws
				 * an exception.
				 *
				 * getContextClassLoader() throws SecurityException when
				 * the context class loader isn't an ancestor of the
				 * calling class's class loader, or if security
				 * permissions are restricted.
				 *
				 * In the first case (not related), we want to ignore and
				 * keep going.  We cannot help but also ignore the second
				 * with the logic below, but other calls elsewhere (to
				 * obtain a class loader) will trigger this exception where
				 * we can make a distinction.
				 */
				if (e.getTargetException() instanceof SecurityException) {
					;  // ignore
				} else {
					// Capture 'e.getTargetException()' exception for details
					// alternate: log 'e.getTargetException()', and pass back 'e'.
					throw new LogInitializationException(
						"Unexpected InvocationTargetException",
						e.getTargetException()
					);
				}
			}
		} catch (NoSuchMethodException e) {
			// Assume we are running on JDK 1.1
			classLoader = getClassLoader(LogFactory.class);
		}

		// Return the selected class loader
		return classLoader;
	}

	/**
	 * Fetch the parent classloader of a specified classloader.
	 * <p>
	 * If a SecurityException occurs, null is returned.
	 * <p>
	 * Note that this method is non-static merely so logDiagnostic is available.
	 */
	private static ClassLoader getParentClassLoader(final ClassLoader cl) {
		try {
			return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<Object>() {
					@Override
					public Object run() {
						return cl.getParent();
					}
				}
			);
		} catch(SecurityException ex) {
			// Unable to obtain parent classloader
			return null;
		}
	}
}
