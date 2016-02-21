package org.wolfetti.swissknife.jasper.reports;

import java.io.InputStream;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

public class JReport {
	
	/**
	 * Genera un PDF a partire dal path in ingresso.
	 * 
	 * @param reportPath
	 * 	Il path del report da stampare.
	 * 
	 * @return
	 * 	Un array di bytes contenente il PDF.
	 */
	public static byte[] byteArrayPdf(String reportPath)
	throws JReportException {
		return byteArrayPdf(reportPath, new JREmptyDataSource());
	}
	
	/**
	 * Genera un PDF a partire dal path in ingresso.
	 * 
	 * @param reportPath
	 * @param ds
	 * @return
	 */
	public static byte[] byteArrayPdf(String reportPath, JRDataSource ds)
	throws JReportException {
		return byteArrayPdf(reportPath, ds, null);
	}
	
	/**
	 * Genera un PDF a partire dal path in ingresso.
	 * 
	 * @param reportPath
	 * @param params
	 * @return
	 */
	public static byte[] byteArrayPdf(String reportPath, Map<String, Object> params)
	throws JReportException {
		return byteArrayPdf(reportPath, new JREmptyDataSource(), params);
	}
	
	/**
	 * Genera un PDF a partire dal path in ingresso.
	 * 
	 * @param reportPath
	 * @param ds
	 * @param params
	 * @return
	 */
	public static byte[] byteArrayPdf(String reportPath, JRDataSource ds, Map<String, Object> params)
	throws JReportException {
		try {
			JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, params, ds);
			return byteArrayPdf(jasperPrint);
		} catch (JRException e) {
			throw new JReportException("Errore durante la generazione del report", e);
		}
	}
	
	/**
	 * Genera un PDF a partire dal path in ingresso.
	 * 
	 * @param report
	 * 	Il report da stampare.
	 * 
	 * @return
	 * 	Un array di bytes contenente il PDF.
	 */
	public static byte[] byteArrayPdf(InputStream report)
	throws JReportException {
		return byteArrayPdf(report, new JREmptyDataSource());
	}
	
	/**
	 * Genera un PDF a partire dal path in ingresso.
	 * 
	 * @param report
	 * 	Il report da stampare.
	 * @param ds
	 * @return
	 */
	public static byte[] byteArrayPdf(InputStream report, JRDataSource ds)
	throws JReportException {
		return byteArrayPdf(report, ds, null);
	}
	
	/**
	 * Genera un PDF a partire dal path in ingresso.
	 * 
	 * @param report
	 * 	Il report da stampare.
	 * @param params
	 * @return
	 */
	public static byte[] byteArrayPdf(InputStream report, Map<String, Object> params)
	throws JReportException {
		return byteArrayPdf(report, new JREmptyDataSource(), params);
	}
	
	/**
	 * Genera un PDF a partire dal path in ingresso.
	 * 
	 * @param report
	 * 	Il report da stampare.
	 * @param ds
	 * @param params
	 * @return
	 */
	public static byte[] byteArrayPdf(InputStream report, JRDataSource ds, Map<String, Object> params)
	throws JReportException {
		try {
			JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, ds);
			return byteArrayPdf(jasperPrint);
		} catch (JRException e) {
			throw new JReportException("Errore durante la generazione del report", e);
		}
	}
	
	/**
	 * Genera un PDF a partire dal jasper print in ingresso.
	 * 
	 * @param jasperPrint
	 * @return
	 */
	private static byte[] byteArrayPdf(JasperPrint jasperPrint)
	throws JReportException {
		
		try {
			return JasperExportManager.exportReportToPdf(jasperPrint);
		} catch (JRException e) {
			throw new JReportException("Errore durante l'esportazione del report", e);
		}
	}
}
