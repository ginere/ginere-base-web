package eu.ginere.base.web.services;

import java.beans.IntrospectionException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.GsonBuilder;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.connectors.users.UsersConnector;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.servlet.JSONServlet;
import eu.ginere.base.web.servlet.MainServlet;
import eu.ginere.base.web.servlet.info.ServletArgs;


@WebServlet(asyncSupported = false, description = "Get user info of the user loged or return null if no user is loggeg", value = "/services/isUserLogged")
public class IsUserLogged extends JSONServlet{
	
	public static final Logger log = Logger.getLogger(IsUserLogged.class);

	private static final long serialVersionUID = 1L;
	
	private static final String URI = "/services/isUserLogged";
	private static final String DESCRIPTION = "return the userIs if any";

	@Override
	protected Object doTaskJSONService(HttpServletRequest request,
									   HttpServletResponse response) throws ServletException, IOException,DaoManagerException {

		String userId=MainServlet.getUserId(request);
		
		if (userId==null || !UsersConnector.exists(userId)){
			return "";
		} else {
			String ret=UsersConnector.getUserName(userId);
			return ret;
		}
	}
	@Override
	protected ServletArgs[] getArgs() {
		return ServletArgs.NO_ARGS;
	}
	
	@Override
	protected String configureGsonBuilder(GsonBuilder arg0)
			throws IntrospectionException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected RightInterface[] getRights() throws ContextInitializedException {
//		return MainServlet.USER_LOGGED;
		return null;
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
