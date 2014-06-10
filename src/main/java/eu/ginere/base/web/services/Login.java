/**
 * Copyright: Angel-Ventura Mendo Gomez
 *	      ventura@free.fr
 *
 * $Id: Login.java,v 1.4 2006/05/07 14:42:40 ventura Exp $
 */
package eu.ginere.base.web.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.connectors.users.UsersConnector;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.servlet.MainServlet;
import eu.ginere.base.web.servlet.info.ServletArgs;
import eu.ginere.base.web.session.AbstractSession;
import eu.ginere.base.web.session.SessionAccesor;
import eu.ginere.base.web.util.CookieUtils;

/**
 * Use this SERVLET to login the user.
 * LOGIN: The name of the list to change. Mandatory.<br>
 * PASSWORD: The name of the column to push width the ascending order.<br>
 *
 * If error this set the error into the setAttribute Machinery
 * setAttribute(request,UserWorkFlowError.class.getName(),UserWorkFlowError);
 *
 * @author Angel Mendo
 * @version $Revision: 1.4 $
 */
@SuppressWarnings("serial")
public abstract class Login extends MainServlet {
	public static final Logger log = Logger.getLogger(AbstractSession.class);


	@Override
	protected ServletArgs[] getArgs() {
		return new ServletArgs[]{
				ServletArgs.getMandatoryStringParameter("id"),
				ServletArgs.getMandatoryStringParameter("password"),
				ServletArgs.getBooleanParameter("If true one login cookie will be sended back","remerberMe"),
				
		};	
	}
	
	
	protected abstract void loginOK(HttpServletRequest request,
			HttpServletResponse response, String userId) throws ServletException, IOException,DaoManagerException;
	
	protected abstract void loginKO(HttpServletRequest request,
			HttpServletResponse response)  throws ServletException, IOException,DaoManagerException ;
	
	
	protected void doService(HttpServletRequest request,
							 HttpServletResponse response)
		throws ServletException, IOException,DaoManagerException{
		
		// Retrieve form parameters
		String userId = getMandatoryStringParameter(request, "id");
		String password = getMandatoryStringParameter(request, "password");
		boolean rememberMe = getBooleanParameter(request, "rememberme", false);

//		boolean logged=UsersConnector.login(userId,password);
//		
//		if (logged){
//			MainServlet.setUserId(request,userId);
//			if (remember){
//				CookieUtils.storeUserLoginCookie(request,response,userId);
//			} 
//			loginOK(request,response,userId);
//		} else {
//			loginKO(request,response);
//		}
		
		login(request, response, userId, password, rememberMe);
		
	}
	
	public  void login(HttpServletRequest request,
					  HttpServletResponse response,
					  String userId,
					  String password,
					  boolean rememberMe)throws ServletException, IOException,DaoManagerException{		
		// Verificamos en BD que el login y el password coinciden
		boolean logged=UsersConnector.login(userId,password);
		
		if (logged){
			// Guaradmos el usuario en la session
			MainServlet.setUserId(request,userId);
			
			if (UsersConnector.useCookiesForLogin()){
				CookieUtils.storeUserLoginCookie(request,response,rememberMe,userId);
			}

			loginOK(request,response,userId);
		} else {
			loginKO(request,response);
		}		
	}


	@Override
	protected boolean enableSpetialSecurity() {
		return true;
	}
	
	@Override
	protected RightInterface[] getRights() throws ContextInitializedException {
		return MainServlet.PUBLIC_ACCESS;
	}

	
	/**
	 * un conteto gestiona las cookies del usuario. En cada peticion el resto de los contextos tienen que estar verificando
	 * Si el usuario se a conectado o desconectado.
	 * @param request
	 * @param response
	 */
	public static void loginAndLogoutByCookie(HttpServletRequest request,HttpServletResponse response){
		// El uuid ya esta en session por que es una de las primeras cosas que se hacen ...
//		String uuid=CookieUtils.getUUIDCookie(request);
		String uuid=SessionAccesor.getUUID(request);
		
		// Confiamos en el main servlet si ya hay un user ID es el valido ...
		String userId=MainServlet.getUserId(request);
		
		if (userId==null){
			// No hay usuario conectado, verificamos si se ha conectado
			// Buscamos el nuevo...
			String newUserId=CookieUtils.getUserIdFromCoockieAuth(request,response,uuid);
			
			if (newUserId!=null){
				// y lo insertamos  Y TAMBIEN NOTIFICAMOS QUE EL USUARIO SE HA CONECTADO
				MainServlet.setUserId(request,newUserId);
				// Notificamos que el usuario se ha conectado
			}
			return;
		} else {
			if (UsersConnector.useCookiesForLogin()) {
				// El usuario esta conectado, verificamos si hay una dexconexion ... Y TAMBIEN NOTIFICAMOS QUE EL USUARIO SE HA CONECTADO
				String newUserId=CookieUtils.getUserIdFromCoockieAuth(request,response,uuid);
	
				if (!StringUtils.equals(userId, newUserId)){
					// first removes the old user
					// Se supone que no hay que borrar la cookie de auth por que al hacer el logout ya se habra borrado.
					MainServlet.removeUser(request);
					
					// then if ther is a new one, we add into request
					if (newUserId!=null){//  Y TAMBIEN NOTIFICAMOS QUE EL USUARIO SE HA CONECTADO
						MainServlet.setUserId(request, newUserId);
					}
					
				} else {
					// the user is correctly logged
				}
			}
		}
	}
}
