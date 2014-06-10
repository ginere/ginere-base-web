/**
 * Copyright: Angel-Ventura Mendo Gomez
 *	      ventura@free.fr
 *
 * $Id: Logout.java,v 1.4 2006/05/07 14:42:40 ventura Exp $
 */
package eu.ginere.base.web.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

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
 * @author Angel Mendo
 * @version $Revision: 1.4 $
 */
@WebServlet(asyncSupported = false, description = "Logout current logged user", value = "/logout")
public class Logout extends MainServlet {
	
	private static final long serialVersionUID = "$Revision: 1.4 $".hashCode();

	public static final Logger log = Logger.getLogger(AbstractSession.class);

	private static final String URI = "/logout";
	private static final String DESCRIPTION = "Disconect the current loged user and erased the login cookie if any";
	
	@Override
	protected ServletArgs[] getArgs() {
		return new ServletArgs[]{
				ServletArgs.getBooleanParameter("If true this disconnect user from all the devices","all"),
		};	
	}
	
	protected void doService(HttpServletRequest request,
							 HttpServletResponse response)throws ServletException, IOException{

		if (MainServlet.isUserConnected(request)){
			String userId=getUserId(request);
			String uuid=SessionAccesor.getUUID(request);
			boolean all = getBooleanParameter(request, "all", false);
//			String uuid=CookieUtils.getUUIDCookie(request);
			
			// Borra de la base de datos la cookie de conexion para ese usuario y ese uuid
			UsersConnector.logout(userId,uuid,all);
			
			if (UsersConnector.useCookiesForLogin()){
				CookieUtils.removeCoockieAuth(request,response);
			}
		}

		
		/**
		 * Don't remove the user values only log out the user ?
		 */
		MainServlet.removeUser(request);
		

		returnToRedirect(request,response);
	}

	@Override
	protected RightInterface[] getRights() throws ContextInitializedException {
		return MainServlet.USER_LOGGED;
	}
	
	@Override
	protected String getUri() {
		return URI;
	}

	@Override
	protected String getDescription() {
		return DESCRIPTION;
	}
}
