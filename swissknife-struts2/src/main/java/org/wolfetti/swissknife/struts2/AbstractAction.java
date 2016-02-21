package org.wolfetti.swissknife.struts2;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.wolfetti.swissknife.common.logger.Log;
import org.wolfetti.swissknife.common.logger.LogFactory;
import org.wolfetti.swissknife.common.utils.DateTimeUtils;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Classe da estendere per creare le actions.
 *
 * Contiene tutti i metodi base utilizzati nelle actions.
 *
 * @author Fabio Frijo
 */
public abstract class AbstractAction extends ActionSupport {
	private static final long serialVersionUID = 2987202201035438553L;

	/**
	 * Il logger che e' protected, ma viene instanziato per ogni action.
	 */
	protected Log log = null;

	/** COSTRUTTORE */
	public AbstractAction() {
		super();
		this.log = LogFactory.getLog(this.getClass());
	}

	/* METODI ASTRATTI */

	/**
	 * Processing degli eventuali errori riscontrati.
	 */
	protected abstract void processError(Throwable t);

	/**
	 * Metodo che consiste nell'action vera e propria. Serve principalmente per
	 * semplificare la gestione delle actions limitando il codice scritto all'
	 * effettiva necessit&agrave; di business, e a gestire in maniera
	 * centralizzata
	 * eventuali errori ed eccezioni.
	 */
	protected abstract void action() throws Throwable;

	/**
	 * Metodo generico per processare i dati.
	 *
	 * Ogni subclasse astratta di questa classe deve implementare
	 * questo metodo in base alle sue esigenze.
	 */
	protected abstract String doExecute() throws Throwable;

	/**
	 * Getter per lo stream dei dati in uscita.
	 */
	public abstract InputStream getResponse();
	
	/**
	 * Metodo che forza lo sviluppatore a rilasciare le risorse utilizzate
	 */
	protected abstract void releaseResources();

	/* METODI PROTECTED */

	/**
	 * Restituisce delle info utili per i timings di sessione.<br><br>
	 *
	 * Nello specifico queste informazioni sono:<pre>
	 * + --------------------+----------------------------+
	 * | CHIAVE              | VALORE (in ms)             |
	 * + --------------------+----------------------------+
	 * | creationTime        | Inizio di sessione         |
	 * | maxInactiveInterval | Timeout di sessione        |
	 * | lastAccessedTime    | Ultimo accesso in sessione |
	 * + --------------------+----------------------------+
	 * </pre>
	 *
	 * @param session
	 * 	La sessione dalla quale ricavare le informazioni
	 *
	 * @return
	 * 	Un {@link HashMap} contenente chiavi e valori sopracitati.
	 */
	protected final Map<String, Object> getSessionTimings(){
		Map<String, Object> response = new HashMap<String, Object>();
		HttpSession session = this.getSession();

		// If session is null nicely exit
		if(session == null){
			response.put("message", "Session is null.");
		}

		// Put informations into response object
		else {
			response.put("creationTime", session.getCreationTime());
			response.put("maxInactiveInterval", session.getMaxInactiveInterval() * 1000);
			response.put("lastAccessedTime", session.getLastAccessedTime());
		}

		return response;
	}


	/**
	 * Restituisce la sessione corrente.
	 *
	 * @return <code>{@link #getServletRequest()}.getSession()</code>
	 */
	protected final HttpSession getSession() {
		return this.getServletRequest().getSession();
	}

	/**
	 * Restituisce la request corrente.
	 *
	 * @return <code>{@link ServletActionContext#getRequest() ServletActionContext.getRequest()}</code>
	 */
	protected final HttpServletRequest getServletRequest() {
		return ServletActionContext.getRequest();
	}

	/**
	 * Restituisce la response corrente.
	 *
	 * @return <code>{@link ServletActionContext#getResponse() ServletActionContext.getResponse()}</code>
	 */
	protected final HttpServletResponse getServletResponse() {
		return ServletActionContext.getResponse();
	}

	/**
	 * Restituisce la response corrente.
	 *
	 * @return <code>{@link #getSession()}.getServletContext()</code>
	 */
	protected final ServletContext getServletContext() {
		return this.getSession().getServletContext();
	}

	/**
	 * Restituisce il path desiderato.
	 *
	 * @param path il path relativo da cercare (es: WEB-INF)
	 *
	 * @return <code>{@link #getServletContext()}.getRealPath(path)</code>
	 */
	protected final String getRealPath(String path) {
		return this.getServletContext().getRealPath(path);
	}

	/* METODI PUBLIC */
	/**
	 * Override del metodo {@link ActionSupport#execute() execute()} ereditato
	 * da {@link ActionSupport}.
	 */
	@Override
	public String execute()
	throws Exception {
		long start = System.currentTimeMillis();
		String result = SUCCESS;

		if(this.log.isTraceEnabled()){
			Class<?> c = this.getClass();

			while (c != null) {
				boolean fieldsFound = false;
				StringBuffer sb = new StringBuffer("Public fields in class [" + c.getName() + "]:");

				for(Field f : c.getDeclaredFields()){
					if(Modifier.isPublic(f.getModifiers())){
						fieldsFound = true;
						sb.append("\n\t[[" + f.getName() + "]] ==> " + f.get(this));
					}
				}

				if(fieldsFound){
					this.log.trace(sb.toString());
				}

				c = c.getSuperclass();
			}
		}

		try {
			result = this.doExecute();
		} catch (Throwable t) {
			this.log.error(
				"Errore durante l'esecuzione dell'action " +
				this.getClass().getName() + ": " + t.getClass().getName(),
				t
			);

			this.processError(t);
		} finally {
			this.releaseResources();
		}

		this.log.info(
			"Action '" +
			this.getClass().getName() +
			"' eseguita in " +
			DateTimeUtils.getElapsedTime(start)
		);

		return result;
	}
}
