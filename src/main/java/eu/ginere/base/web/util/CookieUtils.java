/**
 * Copyright: Angel-Ventura Mendo Gomez
 *	      ventura@free.fr
 *
 * $Id: CookieUtils.java,v 1.2 2006/11/25 07:19:55 ventura Exp $
 */
package eu.ginere.base.web.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eu.ginere.base.util.properties.GlobalFileProperties;
import eu.ginere.base.web.connectors.users.UsersConnector;
import eu.ginere.base.web.session.SessionAccesor;

/**
 * Utilities for generating and reading cookies
 *
 * @author Angel Mendo
 * @version $Revision: 1.2 $
 */
public class CookieUtils {
	public static final Logger log = Logger.getLogger(UserAgentManager.class);
	
	private static final String COOKIE_UUID_NAME=GlobalFileProperties.getStringValue(CookieUtils.class, "UUIDCookieName","cgps.com.UUID");
	private static final String COOKIE_LOGIN_NAME=GlobalFileProperties.getStringValue(CookieUtils.class, "LoginCookieName","cgps.com.Login");
	private static final int COOKIE_MAX_AGE=Integer.MAX_VALUE; // 24*60*60*1000; // time in seconds

	public static void storeUserLoginCookie(HttpServletRequest request,
 											HttpServletResponse response,
 											boolean storeCookie,
											String userId){
		
		// First erase the old cookie if any
		removeCoockieAuth(request,response);
		
		// Then creatting a new one
//		String uuid=getUUIDCookie(request);
		String uuid=SessionAccesor.getUUID(request);
		String stringValue=UsersConnector.generateLoginCookie(userId, uuid);

//		if (stringValue == null){
//			// si hay un genrador salimo
//			return ;
//		}
		
		Cookie cookie=new Cookie(COOKIE_LOGIN_NAME,stringValue+" "+userId);
		cookie.setComment(userId);
		
		if (storeCookie){
			cookie.setMaxAge(COOKIE_MAX_AGE);
		} else {
			cookie.setMaxAge(-1);
		}
// Montado en apache a si que para todo el pat
//		cookie.setPath(request.getContextPath());
		cookie.setPath("/");
	
// No hay gestion de DNS.
//		
//		// for domaines like ventura.bloghispano.org , set .bloghispano.org
//		// domain name begins with a dot (.foo.com) and means that the cookie is
//		// visible to servers in a specified Domain Name System (DNS) zone (for 
//		// example, www.foo.com, but not a.b.foo.com). By default, cookies are 
//		// only returned to the server that sent them. 
//		String domain=request.getServerName();
//		int firstIndex=domain.indexOf('.');
//
//		if (firstIndex>=0){
//			int sencondIndex=domain.indexOf('.',firstIndex+1);
//			if (sencondIndex>=0){
//				int thirdIndex=domain.indexOf('.',sencondIndex+1);
//				if (thirdIndex<0){
//					domain=domain.substring(firstIndex);
//				}
//			}			
//		}

//		cookie.setDomain(domain);
		
		response.addCookie(cookie);

//
//		log.warn("STORE ++++++++++++++++++++++++++++++++++++++++++");
//		log.warn("++++++++++++++++++++++++++++++++++++++++++");
//		log.warn("++++++++++++++++++++++++++++++++++++++++++");
//		log.warn("StringValue:"+stringValue);
//		log.warn("COOKIE_MAX_AGE:"+COOKIE_MAX_AGE);
//		log.warn("userId:"+userId);
//		
//
//		log.warn("getName:"+cookie.getName());
//		log.warn("getDomain:"+cookie.getDomain());
//		log.warn("getPath:"+cookie.getPath());
//		log.warn("getSecure:"+cookie.getSecure());
//		log.warn("getValue:"+cookie.getValue());
//		log.warn("getComment:"+cookie.getComment());
//			
//		log.warn("++++++++++++++++++++++++++++++++++++++++++");
//		log.warn("++++++++++++++++++++++++++++++++++++++++++");
//		log.warn("++++++++++++++++++++++++++++++++++++++++++");

	}

	/**
	 * Returns the userId from cookie auth si la cookie es valida
	 */
 	public static String getUserIdFromCoockieAuth(HttpServletRequest request,
												  HttpServletResponse response,
												  String uuid){
		Cookie cookies[]=request.getCookies();
		if (cookies==null){
			return null;
		}
		for (int i=0;i<cookies.length;i++){			
			// Search for the right cookie. There is only one
			if (COOKIE_LOGIN_NAME.equals(cookies[i].getName())){
				String array[]=StringUtils.split(cookies[i].getValue()," ");

				if (array.length==2){					
					String userId=array[1];
					String value=array[0];

					// Valide that the user is rigth
					if (UsersConnector.exists(userId)){
						// validate the coockie is valide AND SETS DE USER AS CONNECTED
						if (UsersConnector.validateLoginCookie(userId,uuid,value)){
							return userId;
						} else {
							// remove the cookie is not valid
							cookies[i].setMaxAge(0);
							response.addCookie(cookies[i]);
						}
					} else {
						// remove the cookie the user is not valide
						cookies[i].setMaxAge(0);
						response.addCookie(cookies[i]);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the user interface if the cookie auth is valide
	 */
 	public static void removeCoockieAuth(HttpServletRequest request,
										 HttpServletResponse response){
		
		Cookie cookies[]=request.getCookies();
		if (cookies!=null){
			for (int i=0;i<cookies.length;i++){
				/*
				log.warn("REMOVE AUTH------------------------------------"+i);
				log.warn("getName:"+cookies[i].getName());
				log.warn("getDomain:"+cookies[i].getDomain());
				log.warn("getPath:"+cookies[i].getPath());
				log.warn("getSecure:"+cookies[i].getSecure());
				log.warn("getValue:"+cookies[i].getValue());
				log.warn("getComment:"+cookies[i].getComment());
				*/
				
				// Search for the right cookie. There is only one
				if (COOKIE_LOGIN_NAME.equals(cookies[i].getName())){
					// log.warn("REMOVING ------------------------------------"+i);
					
					cookies[i].setMaxAge(0);
					response.addCookie(cookies[i]);
				}
			}
		}
	}

	
//	public static String getUUIDCookie(HttpServletRequest request){
//		Cookie cookies[]=request.getCookies();
//		if (cookies==null){
//			return null;
//		}
//		for (int i=0;i<cookies.length;i++){
//			// Search for the right cookie. There is only one
//			if (COOKIE_UUID_NAME.equals(cookies[i].getName())){
//				String uuid=cookies[i].getValue();
////				SessionAccesor.setUUID(request.getSession(), uuid);
//				return uuid;
//			}
//		}
//		
//		return SessionAccesor.getUUID(request.getSession());
//	}
	
	
	/**
	 * Intenta recuperar el UUID de una cookie si no hay devuelve null
	 * @param request
	 * @return
	 */
	public static String getUUIDFromCookie(HttpServletRequest request){
		Cookie cookies[]=request.getCookies();
		if (cookies==null){
			return null;
		}
		for (int i=0;i<cookies.length;i++){
			// Search for the right cookie. There is only one
			if (COOKIE_UUID_NAME.equals(cookies[i].getName())){
				String uuid=cookies[i].getValue();
//				SessionAccesor.setUUID(request.getSession(), uuid);
				return uuid;
			}
		}
		
		return null;
	}
	
	/**
	 * Genera un UUID y lo guarda en una cookie para siempre ...
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String createUUIDCookie(HttpServletRequest request,
										  HttpServletResponse response){

		// puede que haya varias llamadas en paralelo para evitar que 
		// se generen varios uuid volvemos a preguntar por el uuid de la 
		// cookie. esto no soluciona el problema solo
		// lo palia

		String uuid=getUUIDFromCookie(request);

		if (uuid!=null){
			return uuid;
		} else {
			uuid=UsersConnector.generateUUID();
			//		SessionAccesor.setUUID(request.getSession(), uuid);
			
			Cookie cookie=new Cookie(COOKIE_UUID_NAME,uuid);
			cookie.setPath("/");
			cookie.setMaxAge(Integer.MAX_VALUE); // Cookie persistente ...
			response.addCookie(cookie);
			
			return uuid;
		}
	}

	/**
	 * Actualiza el uuid de la cookie con el valor pasado en param
	 * 
	 * @param request
	 * @param response
	 * @param uuid
	 * @return
	 */
	public static String updateUUIDCookie(HttpServletRequest request,
			HttpServletResponse response, String uuid) {
//		SessionAccesor.setUUID(request.getSession(), uuid);
		Cookie cookie = new Cookie(COOKIE_UUID_NAME, uuid);
		cookie.setPath("/");
		cookie.setMaxAge(Integer.MAX_VALUE); // not persitent cookie
		response.addCookie(cookie);

		return uuid;
	}

	/**
	 * 
	 * Genera una UUID cookie unica para cada navegador, si biene en la request, se usa la de la request
	 * 
	 * @param request
	 * @param response
	 */
	public static String generateOrGetUUIDCookie(HttpServletRequest request,
			HttpServletResponse response) {
		String uuid=getUUIDFromCookie(request);
		
		if (uuid!=null){
			// Ya tenemos un uuid en la cookie obtenemos el de la request
			String requestUUID=getStringParameter(request, "uuid", null);
			
			if (requestUUID == null){
				// si no hay nada volvemos y tomamos el de la cookie
				return uuid;
			} else if (StringUtils.equals(uuid, requestUUID)){
				// son iguales no hacemos nada
				return uuid;
			} else {
				// actualizamos la cookie pues ha cambiado. ... Esto es improbable
				updateUUIDCookie(request, response,requestUUID);
				log.warn("Changing uuid");
				
				return requestUUID;
			}
		} else {
			// No hay cokkie vemos si tenemos que generar una nueva o usamos la de la request
			String requestUUID=getStringParameter(request, "uuid", null);
			if (requestUUID==null){
				String newUUID=createUUIDCookie(request, response);

				return newUUID;
			} else {
				updateUUIDCookie(request, response,requestUUID);
				return requestUUID;
			}
		}
	}
	
	private static String getStringParameter(HttpServletRequest request,
			String parameterName, String defaultValue) {
		String value = request.getParameter(parameterName);
		if (value == null) {
			return defaultValue;
		} else if ("".equals(value.trim())) {
			return defaultValue;
		} else {
			return value.trim();
		}
	}
	
}
