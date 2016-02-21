package org.wolfetti.swissknife.jasper.reports;

import java.io.File;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

import org.wolfetti.swissknife.common.logger.Log;
import org.wolfetti.swissknife.common.logger.LogFactory;
import org.wolfetti.swissknife.common.utils.FileUtils;
import org.wolfetti.swissknife.common.utils.exceptions.FileException;

/**
 * Reports builder utils class
 */
public class JRBuilder {
	
	// Logger
	private final static Log log = LogFactory.getLog(JRBuilder.class);

	// Singleton
	private JRBuilder(){}
	
	/**
	 * Build all jrxml files contained in a folder, the files are saved into same folder.
	 * 
	 * @param folderPath
	 * 	The full path to the folder that contains .jrxml files.
	 */
	public static void buildAll(String folderPath)
	throws JReportException {
		if(!FileUtils.isDirectory(folderPath)){
			log.warn(folderPath + " is not a directory!");
			return;
		}
		
		log.info("Building all jrxml files contained in " + folderPath);
		
		try {
			File[] files = FileUtils.listFilesByExtension(folderPath, "jrxml");
			
			for(File f : files){
				buildJrxml(f);
			}
			
		} catch (FileException e) {
			throw new JReportException("Error while retrieving list of files.", e);
		}
	}
	
	/**
	 * Build a single jrxml into the same folder where it's contained.
	 * 
	 * @param jrxmlFilePath
	 * 	The full path to the jrxml file to build.
	 */
	public static void buildJrxml(String jrxmlFilePath)
	throws JReportException {
		buildJrxml(new File(jrxmlFilePath));
	}
	
	/**
	 * Build a single jrxml into the same folder where it's contained.
	 * 
	 * @param jrxmlFile
	 * 	The jrxml file to build.
	 */
	public static void buildJrxml(File jrxmlFile)
	throws JReportException {
		String name = jrxmlFile.getName();
		String path = jrxmlFile.getAbsolutePath();
		
		if(!jrxmlFile.exists()){
			log.error("File " + path + " not exists!!");
		}
		
		log.trace("Building '" + name + "' ...");
		
		String dest = path.replace(name, name.replace("jrxml", "jasper"));
		log.trace("Destination file: " + dest);
		
		try {
			JasperCompileManager.compileReportToFile(path, dest);
			log.trace("File '" + name + "' built!");
		} catch (JRException e) {
			throw new JReportException("Errore durante la compilazione del file " + dest, e);
		}
	}
}
