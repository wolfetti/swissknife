package org.wolfetti.swissknife.struts2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.wolfetti.swissknife.common.utils.FileUtils;
import org.wolfetti.swissknife.common.utils.exceptions.FileException;
import org.wolfetti.swissknife.struts2.exceptions.FileToStreamFault;

import com.opensymphony.xwork2.ActionSupport;

public abstract class AbstractFileAction extends AbstractAction {
	private static final long serialVersionUID = -4734204821443064730L;

	private InputStream response;
	private String fileName;
	private String contentType;
	private long contentLength;

	@Override
	protected void processError(Throwable t) {
		this.log.error("Errore durante l'esecuzione dell'action:", t);
	}

	@Override
	protected String doExecute() throws Throwable {
		try {
			this.action();
		} catch (Throwable t) {
			this.log.error("Errore durante l'esecuzione dell'action", t);
			this.processError(t);
			return ActionSupport.ERROR;
		}

		return "download";
	}

	protected void setResponse(String fileName, String filePath) 
	throws FileToStreamFault, FileException {
		this.setResponse(fileName, new File(filePath));
	}

	protected void setResponse(String fileName, File response) 
	throws FileToStreamFault, FileException {
		this.fileName = fileName;
		this.contentType = FileUtils.getMimeType(response);
		this.contentLength = response.length();
		
		try {
			this.response = new FileInputStream(response);
		} catch (FileNotFoundException e) {
			this.log.error("Errore durante la conversione del file in input stream:", e);
			throw new FileToStreamFault("Errore durante la conversione in input stream", e);
		}
	}
	
	protected void setResponse(String fileName, byte[] response, String contentType) {
		this.fileName = fileName;
		this.contentType = contentType;
		this.contentLength = response.length;
		this.response = new ByteArrayInputStream(response);
	}

	@Override
	public InputStream getResponse() {
		return this.response;
	}

	public String getFileName(){
		return this.fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public long getContentLength() {
		return contentLength;
	}
}
