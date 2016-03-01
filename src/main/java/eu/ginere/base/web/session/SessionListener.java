/**
 * Copyright: Angel-Ventura Mendo Gomez
 *	      ventura@free.fr
 *
 * $Id: SessionListener.java,v 1.1 2006/11/25 07:21:36 ventura Exp $
 */
package eu.ginere.base.web.session;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;


/**
 * Use this manager to manage session control an destroy.
 * For this manager to run the function initSession must be called altmoust one time per session.
 *
 * @author Angel Mendo
 * @version $Revision: 1.1 $
 */
public class SessionListener implements HttpSessionListener {
	public static final Logger log = Logger.getLogger(AbstractSession.class);

	public void sessionCreated(HttpSessionEvent event){
		HttpSession session=event.getSession();

		log.debug("Listener Sesion IN:"+session.getId());
//		log.debug("Listener lang :"+SessionAccesor.getLanguage(session));
		
	}

	public void sessionDestroyed(HttpSessionEvent event){
		HttpSession session=event.getSession();
		
		log.debug("Listener Sesion OUT:"+session.getId());
//		log.debug("Listener lang :"+SessionAccesor.getLanguage(session));		
	}
}
