package org.wolfetti.swissknife.struts2;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public abstract class AbstractJsonAction extends AbstractTextAction {
	private static final long serialVersionUID = 2569334228232146172L;

	/**
	 * Il messaggio di errore o di successo dell'action
	 */
	private String message = "";

	/**
	 * La variabile di controllo per la parte client
	 */
	private boolean success = false;

	/**
	 * Eventuali dati da restituire al chiamante
	 */
	private Object data = null;

	/* METODI PROTECTED */

	/**
	 * Trasforma una stringa rappresentante un JsonObject nel bean corrispondente.
	 *
	 * @param json
	 * @param beanClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T fromJsonToBean(String json, Class<T> beanClass){
		return (T) JSONObject.toBean(this.getJsonObject(json), beanClass);
	}

	/**
	 * Shortcut per un {@link JSONObject} dell'oggetto in ingresso.
	 *
	 * @return <code>{@link JSONObject#fromObject(Object) JSONObject.fromObject(data)}</code>
	 */
	protected JSONObject getJsonObject(Object o) {
		return JSONObject.fromObject(o);
	}

	/**
	 * Shortcut per un {@link JSONArray} dell'oggetto in ingresso.
	 *
	 * @return <code>{@link JSONArray#fromObject(Object) JSONArray.fromObject(data)}</code>
	 */
	protected JSONArray getJsonArray(Object o) {
		return JSONArray.fromObject(o);
	}

	/**
	 * @return Il messaggio di risposta da restituire al chiamante
	 */
	protected String getMessage() {
		return this.message;
	}

	/**
	 * @param message Il messaggio di risposta da restituire al chiamante
	 */
	protected void setMessage(String message) {
		this.message = message == null ? "" : message.trim();
	}

	/**
	 * Imposta il successo dell'action a <code>true</code>
	 */
	protected void setSuccess() {
		this.success = true;
	}

	/**
	 * Imposta il successo dell'action a <code>false</code>
	 */
	protected void setFailure() {
		this.success = false;
	}

	/**
	 * @param responseData L'oggetto contenente i dati da restituire al chiamante.
	 */
	protected void setData(Object data) {
		this.data = data;
	}

	/**
	 * Prepara la risposta e la restituisce al chiamante.
	 */
	@Override
	protected String prepareResponse(){
		this.log.debug("Preparazione del JSON di risposta in corso...");
		Map<String, Object> response = this.prepareResponseMap();
		return this.getJsonObject(response).toString();
	}

	/**
	 * Prepara la mappa contenente la risposta che verra'
	 * trasformata in JSON.
	 */
	protected Map<String, Object> prepareResponseMap(){
		Map<String, Object> response = new HashMap<String, Object>();

		response.put("session", super.getSessionTimings());
		response.put("success", this.success);
		response.put("message", this.message);

		if (this.data != null) {
			response.put("data", this.data);
		}

		return response;
	}

	/**
	 * Processing degli eventuali errori riscontrati.
	 */
	@Override
	protected void processError(Throwable t){
		this.data = null;
		this.setMessage("C'&egrave; stato un errore sul server. Consultare il log per i dettagli.");
		this.setFailure();
		this.setResponse(this.prepareResponse());
	}
}
