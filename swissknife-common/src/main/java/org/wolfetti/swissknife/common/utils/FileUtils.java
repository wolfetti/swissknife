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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Properties;

import org.wolfetti.swissknife.common.logger.Log;
import org.wolfetti.swissknife.common.logger.LogFactory;
import org.wolfetti.swissknife.common.utils.exceptions.FileException;
import org.wolfetti.swissknife.common.utils.exceptions.MimeException;

/**
 * Serie di metodi statici utili alla gestione/utilizzo dei files
 *
 * @author Fabio Frijo
 */
public class FileUtils {

	/**
	 * Class logger
	 */
	private final static Log log = LogFactory.getLog(FileUtils.class);

	/**
	 * Private constructor, this class is a singleton.
	 */
	private FileUtils(){}

	/**
     * Locate a user-provided properties file.
     * <p>
     * The classpath of the specified classLoader (usually the context classloader)
     * is searched for properties files of the specified name. If none is found,
     * null is returned. If more than one is found, then the file with the greatest
     * value for its PRIORITY property is returned. If multiple files have the
     * same PRIORITY value then the first in the classpath is returned.
     * <p>
     * This differs from the 1.0.x releases; those always use the first one found.
     * However as the priority is a new field, this change is backwards compatible.
     * <p>
     * The purpose of the priority field is to allow a webserver administrator to
     * override logging settings in all webapps by placing a commons-logging.properties
     * file in a shared classpath location with a priority > 0; this overrides any
     * commons-logging.properties files without priorities which are in the
     * webapps. Webapps can also use explicit priorities to override a configuration
     * file in the shared classpath if needed.
     */
	public static final Properties getPropertiesFile(String fileName) {
    	return getPropertiesFile(new String[]{fileName});
    }

	/**
     * Locate a user-provided properties file.
     * <p>
     * The classpath of the specified classLoader (usually the context classloader)
     * is searched for properties files of the specified name. If none is found,
     * null is returned. If more than one is found, then the file with the greatest
     * value for its PRIORITY property is returned. If multiple files have the
     * same PRIORITY value then the first in the classpath is returned.
     * <p>
     * This differs from the 1.0.x releases; those always use the first one found.
     * However as the priority is a new field, this change is backwards compatible.
     * <p>
     * The purpose of the priority field is to allow a webserver administrator to
     * override logging settings in all webapps by placing a commons-logging.properties
     * file in a shared classpath location with a priority > 0; this overrides any
     * commons-logging.properties files without priorities which are in the
     * webapps. Webapps can also use explicit priorities to override a configuration
     * file in the shared classpath if needed.
     */
    public static final Properties getPropertiesFile(String[] possibleFileNames, String[] ... alternativeFileNames) {
    	ClassLoader classLoader = ReflectionUtils.getContextClassLoader(FileUtils.class);
    	return getPropertiesFile(classLoader, possibleFileNames, alternativeFileNames);
    }

	/**
     * Locate a user-provided properties file.
     * <p>
     * The classpath of the specified classLoader (usually the context classloader)
     * is searched for properties files of the specified name. If none is found,
     * null is returned. If more than one is found, then the file with the greatest
     * value for its PRIORITY property is returned. If multiple files have the
     * same PRIORITY value then the first in the classpath is returned.
     * <p>
     * This differs from the 1.0.x releases; those always use the first one found.
     * However as the priority is a new field, this change is backwards compatible.
     * <p>
     * The purpose of the priority field is to allow a webserver administrator to
     * override logging settings in all webapps by placing a commons-logging.properties
     * file in a shared classpath location with a priority > 0; this overrides any
     * commons-logging.properties files without priorities which are in the
     * webapps. Webapps can also use explicit priorities to override a configuration
     * file in the shared classpath if needed.
     */
    public static final Properties getPropertiesFile(ClassLoader classLoader, String fileName) {
    	return getPropertiesFile(classLoader, new String[]{fileName});
    }

	/**
     * Locate a user-provided properties file.
     * <p>
     * The classpath of the specified classLoader (usually the context classloader)
     * is searched for properties files of the specified name. If none is found,
     * null is returned. If more than one is found, then the file with the greatest
     * value for its PRIORITY property is returned. If multiple files have the
     * same PRIORITY value then the first in the classpath is returned.
     * <p>
     * This differs from the 1.0.x releases; those always use the first one found.
     * However as the priority is a new field, this change is backwards compatible.
     * <p>
     * The purpose of the priority field is to allow a webserver administrator to
     * override logging settings in all webapps by placing a commons-logging.properties
     * file in a shared classpath location with a priority > 0; this overrides any
     * commons-logging.properties files without priorities which are in the
     * webapps. Webapps can also use explicit priorities to override a configuration
     * file in the shared classpath if needed.
     */
    public static final Properties getPropertiesFile(ClassLoader classLoader, String[] possibleFileNames, String[] ... alternativeFileNames) {
        Properties props = null;
        LinkedList<String> allFileNames = new LinkedList<>();

        for(String name : possibleFileNames){
        	allFileNames.add(name);
        }

        for(String[] altFileNames : alternativeFileNames){
        	for(String name : altFileNames){
            	allFileNames.add(name);
            }
        }

        for(int i = 0; i < allFileNames.size() && props == null; i++){
        	String fileName = allFileNames.get(i);

	        try {
	            Enumeration<?> urls = getResources(classLoader, fileName);

	            if (urls == null) {
	                continue;
	            }

	            while (urls.hasMoreElements()) {
	                URL url = (URL) urls.nextElement();
	                Properties newProps = getProperties(url);

	                if (newProps != null) {
	                    if (props == null) {
	                        props = newProps;
	                        break;
	                    }
	                }
	            }
	        } catch (SecurityException e) {
	            // SecurityException thrown while trying to find/read config files.
	        }
        }

        return props;
    }

    /**
     * Given a URL that refers to a .properties file, load that file.
     * This is done under an AccessController so that this method will
     * succeed when this jarfile is privileged but the caller is not.
     * This method must therefore remain private to avoid security issues.
     * <p>
     * Null is returned if the URL cannot be opened.
     */
    private static Properties getProperties(final URL url) {
        PrivilegedAction<Object> action = new PrivilegedAction<Object>() {
            @Override
			public Object run() {
                try {
                    InputStream stream = url.openStream();
                    if (stream != null) {
                        Properties props = new Properties();
                        props.load(stream);
                        stream.close();
                        return props;
                    }
                } catch(IOException e) {
                    // Unable to read URL
                }

                return null;
            }
        };

        return (Properties) AccessController.doPrivileged(action);
    }

    /**
     * Given a filename, return an enumeration of URLs pointing to
     * all the occurrences of that filename in the classpath.
     * <p>
     * This is just like ClassLoader.getResources except that the
     * operation is done under an AccessController so that this method will
     * succeed when this jarfile is privileged but the caller is not.
     * This method must therefore remain private to avoid security issues.
     * <p>
     * If no instances are found, an Enumeration is returned whose
     * hasMoreElements method returns false (ie an "empty" enumeration).
     * If resources could not be listed for some reason, null is returned.
     */
    private static Enumeration<?> getResources(final ClassLoader loader, final String name) {
        PrivilegedAction<Object> action = new PrivilegedAction<Object>() {
            @Override
			public Object run() {
                try {
                    if (loader != null) {
                        return loader.getResources(name);
                    } else {
                        return ClassLoader.getSystemResources(name);
                    }
                } catch(IOException e) {
                    return null;
                } catch(NoSuchMethodError e) {
                    // we must be running on a 1.1 JVM which doesn't support
                    // ClassLoader.getSystemResources; just return null in
                    // this case.
                    return null;
                }
            }
        };

        Object result = AccessController.doPrivileged(action);
        return (Enumeration<?>) result;
    }

	/**
	 * Controllo che un determinato path sia una directory o meno
	 * @param path
	 * @return
	 */
	public static boolean isDirectory(String path){
		if(path == null){
			return false;
		}

		return isDirectory(Paths.get(path));
	}

	/**
	 * Controllo che un determinato path sia una directory o meno
	 * @param path
	 * @return
	 */
	public static boolean isDirectory(Path path){
		if(path == null){
			return false;
		}

		return isDirectory(path.toFile());
	}

	/**
	 * Controllo che un determinato path sia una directory o meno
	 * @param path
	 * @return
	 */
	public static boolean isDirectory(File path){
		if(path == null){
			return false;
		}

		return path.isDirectory();
	}
	
	/**
	 * Returns a list of files that have a particular extension contained into a source folder.
	 * 
	 * @param sourceFolder
	 * 	The folder to scan
	 * 
	 * @param extension
	 * 	The extension to scan
	 * 
	 * @return
	 * 	A list of files.
	 */
	public static File[] listFilesByExtension(String sourceFolder, final String extension)
	throws FileException {
		if(!isDirectory(sourceFolder)){
			throw new FileException("Source folder is not a directory!");
		}
		
		File dir = new File(sourceFolder);
		
		File[] files = dir.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith("." + extension);
		    }
		});
		
		return files;
	}

	/**
	 * Returns the MIME content type of a file.
	 * @param file
	 * @return
	 */
	public static String getMimeType(File file) throws FileException {
		try {
			log.trace("Trying to determine the MIME type of " + file.getName());
			return Files.probeContentType(file.toPath()).trim();
		} catch (IOException e) {
			throw new MimeException("Unable to find MIME type", e);
		}
	}

	/**
	 * Deletes a file, never throwing an exception. If file is a directory, delete it and all sub-directories.
	 * The difference between File.delete() and this method are:
	 * A directory to be deleted does not have to be empty.
	 * No exceptions are thrown when a file or directory cannot be deleted.
	 *
	 * @param file
	 * @return
	 */
	public static boolean deleteQuietly(File file) {
		if (file == null) {
			return false;
		}
		try {
			if (file.isDirectory()) {
				cleanDirectory(file);
			}
		} catch (Exception e) {
		}

		try {
			return file.delete();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Cleans a directory without deleting it.
	 *
	 * @param directory
	 * @throws FileException
	 */
	public static void cleanDirectory(String directory) throws FileException {
		if(directory == null){
			throw new FileException("directory is null");
		}
		
		cleanDirectory(Paths.get(directory).toFile());
	}
	
	/**
	 * Cleans a directory without deleting it.
	 *
	 * @param directory
	 * @throws FileException
	 */
	public static void cleanDirectory(File directory) throws FileException {
		if (!directory.exists()) {
			String message = directory + " does not exist";
			throw new FileException(message);
		}

		if (!directory.isDirectory()) {
			String message = directory + " is not a directory";
			throw new FileException(message);
		}

		File[] files = directory.listFiles();
		if (files == null) {  // null if security restricted
			throw new FileException("Failed to list contents of " + directory);
		}

		FileException exception = null;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			try {
				forceDelete(file);
			} catch (FileException e) {
				exception = e;
			}
		}

		if (null != exception) {
			throw exception;
		}
	}

	/**
	 * Deletes a file. If file is a directory, delete it and all sub-directories.
	 * The difference between File.delete() and this method are:
	 * A directory to be deleted does not have to be empty.
	 * You get exceptions when a file or directory cannot be deleted. (java.io.File methods returns a boolean)
	 *
	 * @param file
	 * @throws FileException
	 */
	public static void forceDelete(File file) throws FileException {
		if (file.isDirectory()) {
			deleteDirectory(file);
		} else {
			boolean filePresent = file.exists();
			if (!file.delete()) {
				if (!filePresent){
					throw new FileException("File does not exist: " + file);
				}

				throw new FileException("Unable to delete file: " + file);
			}
		}
	}

	/**
	 * Deletes a directory recursively.
	 *
	 * @param directory
	 * @throws FileException
	 */
	public static void deleteDirectory(File directory) throws FileException {
		if (!directory.exists()) {
			return;
		}

		cleanDirectory(directory);

		if (!directory.delete()) {
			throw new FileException("Unable to delete directory " + directory + ".");
		}
	}
}
