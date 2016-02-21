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
package org.wolfetti.swissknife.common.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedHashMap;

import org.wolfetti.swissknife.common.utils.exceptions.ClassLoaderException;
import org.wolfetti.swissknife.common.utils.exceptions.ReflectionException;

/**
 * Classe contenente metodi utili all'utilizzo della reflection
 *
 * @author Fabio Frijo
 *
 */
public class ReflectionUtils {

	// Singleton
	private ReflectionUtils(){}

	/**
	 * La mappa che fa da cache ai PropertyDescriptors delle varie classi
	 */
	private static LinkedHashMap<String, LinkedHashMap<String, PropertyDescriptor>>
	classPropertyDescriptors = new LinkedHashMap<String, LinkedHashMap<String, PropertyDescriptor>>();

	/**
	 * Restituisce il propertyDescriptor giusto per l'oggetto e il field specificati
	 *
	 * @param fieldName
	 * 	Il nome del field per il quale restituire il propertyDescriptor
	 *
	 * @param target
	 * 	L'istanza dell'oggetto per il quale restituire il propertyDescriptor.
	 *
	 * @return
	 * 	Il propertyDescriptor corretto
	 *
	 * @throws IntrospectionException
	 * 	Quando in fase di memorizzazione dei propertyDescriptors di una classe viene sollevata
	 *  dall'introspector.
	 */
	private static PropertyDescriptor getPropertyDescriptor(String fieldName, Object target)
	throws IntrospectionException {
		Class<?> targetCls = target.getClass();
		String targetClsName = targetCls.getName();
		LinkedHashMap<String, PropertyDescriptor> objDescriptors = null;

		// Classe non ancora memorizzata
		if(!classPropertyDescriptors.containsKey(targetClsName)){
			objDescriptors = new LinkedHashMap<String, PropertyDescriptor>();

			BeanInfo beanInfo = Introspector.getBeanInfo(targetCls);
		    for(PropertyDescriptor pd : beanInfo.getPropertyDescriptors()){
		    	objDescriptors.put(pd.getName(), pd);
		    }

		    classPropertyDescriptors.put(targetClsName, objDescriptors);
		}

		// Classe già memorizzata
		else {
			objDescriptors = classPropertyDescriptors.get(targetClsName);
		}

		// Se non viene trovata nessuna proprietà causo un errore
		if(!objDescriptors.containsKey(fieldName)){
			throw new IllegalArgumentException(
	    		"Property '" + fieldName +
	    		"' non trovata nell'oggetto " +
	    		target.getClass().getName()
	    	);
		}

		return objDescriptors.get(fieldName);
	}

	/**
	 * Esegue il metodo specificato
	 *
	 * @param name
	 * 	Il nome del metodo da eseguire
	 *
	 * @param target
	 * 	L'istanza nella quale eseguire il metodo
	 *
	 * @param value
	 * 	Un eventuale valore da passare al metodo, pu&ograve; essere <code>null</code>
	 *
	 * @throws Exception
	 */
	public static void callMethod(String name, Object target, Object value)
	throws Exception {
		Method metodo = null;
		Class<?> targetCls = target.getClass();

	    try {
	    	if(value != null) {
	    		metodo = targetCls.getMethod(name, new Class[] {getParameterClass(value.getClass())});
	    	} else {
	    		metodo = targetCls.getMethod(name);
	    	}
	    } catch (NoSuchMethodException e) {
	    	// Ignored
	    }

	    if (metodo != null) {
	    	metodo.invoke(target, new Object[] {value});
	    }
	}

	/**
     * Esegue il set di una proprieta di un oggetto
     * @param name il nome della proprieta di cui fare il set.
     * @param target l'oggetto su cui effettuare lo modifica.
     * @param value il valore da modificare.
     */
	public static void setProperty(String name, Object target, Object value)
	throws Exception {
		PropertyDescriptor pd = getPropertyDescriptor(name, target);
		Method setter = pd.getWriteMethod();

		if(setter == null){
			throw new Exception("Il field " + pd.getName() + " non ha nessun setter.");
		}

		setter.invoke(target, new Object[] {value});
	}

	/**
     * Esegue la get di una propriet&agrave; di un oggetto
     * @param propertyName il nome della propriet&agrave; di cui leggere il valore
     * @param obj Target l'oggetto su cui fare la get
     */
	public static Object getProperty(String propertyName, Object objTarget)
	throws Exception {
		PropertyDescriptor pd = getPropertyDescriptor(propertyName, objTarget);
		Method getter = pd.getReadMethod();

		if(getter == null){
			throw new Exception("Il field " + pd.getName() + " non ha nessun getter.");
		}

		Object value = getter.invoke(objTarget, new Object[0]);

		return value;
	}

	/**
	 * Restituisce la classe corretta di un parametro
	 * @param type
	 * @return
	 */
	private static Class<?> getParameterClass(Class<?> type){
		if(type.equals(Integer.class)) {
			return int.class;
		} else if(type.equals(Double.class)) {
			return double.class;
		} else if(type.equals(Boolean.class)) {
			return boolean.class;
		} else {
			return type;
		}
	}

	/**
	 * Retuns the appropriate modifier parsed into s.
	 *
	 * @param s
	 * 	The string to parse
	 *
	 * @return
	 * 	The corresponding modifier
	 */
    public static int modifierFromString(String s) {
		int m = 0x0;
		if ("public".equals(s)) {
			m |= Modifier.PUBLIC;
		}
		else if ("protected".equals(s)) {
			m |= Modifier.PROTECTED;
    	}
		else if ("private".equals(s)) {
			m |= Modifier.PRIVATE;
		}
		else if ("static".equals(s)) {
			m |= Modifier.STATIC;
		}
		else if ("final".equals(s)) {
			m |= Modifier.FINAL;
		}
		else if ("transient".equals(s)) {
			m |= Modifier.TRANSIENT;
		}
		else if ("volatile".equals(s)) {
			m |= Modifier.VOLATILE;
		}
		return m;
    }

    /**
     * Creazione del messaggio di toString in base all'istanza fornita.
     *
     * @param instance
     * 	L'istanza dell'oggetto per il quale generare il toString();
     *
     * @return
     * 	es: com.emc2.Entity@0784982 [prop1=value1, prop2=value2]
     */
    public static String buildToString(Object instance){
    	Class<?> cls = instance.getClass();
    	StringBuffer sb = new StringBuffer(cls.getName() + "@" + System.identityHashCode(instance));
    	boolean first = true;

    	while(cls != null){
    		for(Field f : cls.getDeclaredFields()){
    			if(!Modifier.isStatic(f.getModifiers())){
	    			try {
						PropertyDescriptor pd = getPropertyDescriptor(f.getName(), instance);
						Method getter = pd.getReadMethod();

						if(getter != null){
							if(first){
								sb.append(" [");
								first = false;
							} else {
								sb.append(", ");
							}

							sb.append(f.getName());
							sb.append("=");

							try {
								sb.append(getProperty(f.getName(), instance));
							} catch (Exception e) {
								sb.append("<ERROR: " + e.getMessage() + ">");
							}
						}
					} catch (IntrospectionException e) {
						throw new ReflectionException("Errore durante il recupero del property descriptor: ", e);
					}
    			}
    		}

    		cls = cls.getSuperclass();
    	}

    	sb.append("]");
    	return sb.toString();
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
	 * @throws ClassLoaderException if there was some weird error while
	 * attempting to get the context classloader.
	 *
	 * @throws SecurityException if the current java security policy doesn't
	 * allow this class to access the context classloader.
	 */
	public static ClassLoader getContextClassLoader(final Class<?> clazz)
	throws ClassLoaderException {
		return (ClassLoader) AccessController.doPrivileged(
			new PrivilegedAction<Object>() {
				@Override
				public Object run() {
					ClassLoader classLoader = null;

					try {
						// Are we running on a JDK 1.2 or later system?
						Method method = Thread.class.getMethod("getContextClassLoader", (Class[]) null);

						// Get the thread context class loader (if there is one)
						try {
							classLoader = (ClassLoader) method.invoke(Thread.currentThread(), (Object[]) null);
						} catch (IllegalAccessException e) {
							throw new ClassLoaderException("Unexpected IllegalAccessException", e);
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
								throw new ClassLoaderException(
									"Unexpected InvocationTargetException",
									e.getTargetException()
								);
							}
						}
					} catch (NoSuchMethodException e) {
						// Assume we are running on JDK 1.1
						classLoader = getClassLoader(clazz);
					}

					// Return the selected class loader
					return classLoader;
				}
			}
		);
	}
}
