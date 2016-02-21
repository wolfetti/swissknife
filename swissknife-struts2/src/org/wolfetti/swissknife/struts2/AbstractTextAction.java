package org.wolfetti.swissknife.struts2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


public abstract class AbstractTextAction extends AbstractAction {
	private static final long serialVersionUID = -1956024350204124252L;

	/**
	 * I dati in uscita all'action.
	 */
	private String response = "";
	
	/* METODI ASTRATTI */

	/**
	 * Prepara la risposta e la restituisce al chiamante.
	 * Serve alle varie superclassi che estendono 
	 * e customizzano i metodi di questa action.
	 */
	protected abstract String prepareResponse();
	
	/* METODI PROTECTED */
	@Override
	protected String doExecute() throws Throwable {
		try {
			action();
		} catch (Throwable t) {
			log.error("Errore durante l'esecuzione dell'action", t);
			processError(t);
		} finally {
			setResponse(prepareResponse());
		}

		return SUCCESS;
	}
	
	/**
	 * Setter per lo stream dei dati in uscita.
	 *
	 * @return <code>new ByteArrayInputStream(response.getBytes())</code>
	 */
	protected final void setResponse(String response) {
		this.log.debug("response = " + response);
		this.response = response;
	}

	/* METODI PUBLIC */
	/**
	 * Getter per lo stream dei dati in uscita.
	 *
	 * @return <code>new ByteArrayInputStream(response.getBytes())</code>
	 */
	public InputStream getResponse() {
		return new ByteArrayInputStream(this.response.getBytes());
	}
}
