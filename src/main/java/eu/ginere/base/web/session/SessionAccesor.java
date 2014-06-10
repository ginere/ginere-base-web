/**
 * Copyright: Angel-Ventura Mendo Gomez
 *	      ventura@free.fr
 *
 * $Id: SessionAccesor.java,v 1.2 2006/11/25 07:19:55 ventura Exp $
 */
package eu.ginere.base.web.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import eu.ginere.base.util.i18n.Language;
import eu.ginere.base.util.notification.Notify;
import eu.ginere.base.web.util.CookieUtils;



/**
 * This is the only class to write and read into the session
 *
 * @author Angel Mendo
 * @version $Revision: 1.2 $
 */
public class SessionAccesor {
	public static final Logger log = Logger.getLogger(SessionAccesor.class);
	
	private static final String LANGUAGE_SESSION_PARAMETER_NAME =
		SessionAccesor.class.getName() + "LANG";
	private static final String LASTUPDATED_SESSION_PARAMETER_NAME =
		SessionAccesor.class.getName() + "LASTUPDATED";
//	private static final String COUNTRY_SESSION_PARAMETER_NAME =
//		SessionAccesor.class.getName() + "COUNTRY";
	private final static String LOGIN_SESSION_PARAMETER_NAME =
		SessionAccesor.class.getName() + "LOGIN";

	private final static String UUID_SESSION_PARAMETER_NAME =
		SessionAccesor.class.getName() + "UUID";
	
//	private final static String LOCALE_SESSION_PARAMETER_NAME =
//		"org.apache.struts.action.LOCALE";

	public static Language getLanguage(HttpSession session) {
		try {
			return (Language) session.getAttribute(LANGUAGE_SESSION_PARAMETER_NAME);
		}catch(IllegalStateException e){
			return null;
		}
	}
	
	public static void setLanguage(HttpSession session, Language lang) {
		
		Language current=getLanguage(session);
		
		if (current == null || !current.equals(lang)){
			session.setAttribute(LANGUAGE_SESSION_PARAMETER_NAME, lang);
			
			setLastUpdated(session);
		}
	}

	public static void setLastUpdated(HttpSession session) {
		session.setAttribute(LASTUPDATED_SESSION_PARAMETER_NAME, System.currentTimeMillis());
	}

	public static Long getLastUpdated(HttpSession session) {
		return (Long)session.getAttribute(LASTUPDATED_SESSION_PARAMETER_NAME);
	}



//	public static void setCountry(HttpSession session,
//								   String country) {
//		session.setAttribute(COUNTRY_SESSION_PARAMETER_NAME, country);
//	}
//
//	public static String getCountry(HttpSession session) {
//		try {
//			return (String) session.getAttribute(COUNTRY_SESSION_PARAMETER_NAME);
//		}catch(IllegalStateException e){
//			return null;
//		}
//	}

//	public static Locale getLocale(HttpSession session) {
//		try {
//			return (Locale) session.getAttribute(LOCALE_SESSION_PARAMETER_NAME);
//		}catch(IllegalStateException e){
//			return null;
//		}
//	}
//	
//	public static void setLocale(HttpSession session,Locale locale) {
//		session.setAttribute(LOCALE_SESSION_PARAMETER_NAME, locale);
//		AbstractSession.setLastModified(session);
//	}

	public static String getUserId(HttpSession session) {
		try {
			return (String) session.getAttribute(LOGIN_SESSION_PARAMETER_NAME);
		}catch(IllegalStateException e){
			return null;
		}
	}

	public static void removeUserId(HttpSession session) {
		String userId=getUserId(session);
		
		if (userId!=null){
			UserSession.removeUser(userId,SessionManager.MANAGER.getSession(session.getId()));
			session.removeAttribute(LOGIN_SESSION_PARAMETER_NAME);
			
			setLastUpdated(session);
		} else {
			log.warn("Removing null user id from session:"+session.getId());
		}
	}

	public static void setUserId(HttpSession session,String userId) {
		session.setAttribute(LOGIN_SESSION_PARAMETER_NAME, userId);
		UserSession.setUser(userId,SessionManager.MANAGER.getSession(session.getId()));
		
		setLastUpdated(session);
	}

//	public static void setFirstCall(HttpSession session) {
//		session.setAttribute(FIRST_SESSION_CALL_PARAMETER_NAME, "true");	
//	}
//
//	public static void removeFirstCall(HttpSession session) {
//		session.removeAttribute(FIRST_SESSION_CALL_PARAMETER_NAME);
//	}	
	
	public static boolean isFirstCall(HttpSession session) {
		return session.isNew();
	}

	public static void setUUID(HttpSession session, String uuid) {
		session.setAttribute(UUID_SESSION_PARAMETER_NAME, uuid);
		
//		setLastUpdated(session);
		
	}
	
//	/**
//	 * Use this:
//	 * 		String uuid=CookieUtils.getUUIDCookie(request,response);
//	 * 
//	 * The cookie may be chaneged
//	 * @param session
//	 * @return
//	 */
//	public static String getUUID(HttpSession session) {
//		return (String)session.getAttribute(UUID_SESSION_PARAMETER_NAME);
//	}

	public static String getUUID(HttpServletRequest request) {
//El problema es que si hay llamadas en paralelo a distintos contextos 
//Se pueden generar uuid distintos por lo que siempre hay que ir a 
//la cookie hasta que haya un punto de inicia sincrono.

		// Pero si se envia el uuid en parametro, este se guarda en la session y no hay aun uuid en la cokie para la primera llamada,
		// Lo que hay que hacer es que se busca en la cokkie y si no se encuentra se recupera de la session.
		
		String uuid=CookieUtils.getUUIDFromCookie(request);
		if (uuid==null){
			HttpSession session=request.getSession();
			uuid=(String)session.getAttribute(UUID_SESSION_PARAMETER_NAME);
			
			if (uuid==null){
				Notify.error(log,"The uuid no se encuentra ni en las cookies ni en la sesion. normalmente al iniciar la sesion se genera o se obtiene el uuid que se guarda en session");
			}
			
			return uuid;
			
		} else {
			return uuid;
		}
	}
}
