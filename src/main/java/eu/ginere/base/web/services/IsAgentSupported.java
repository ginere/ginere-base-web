package eu.ginere.base.web.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.servlet.MainServlet;
import eu.ginere.base.web.servlet.SimpleBooleanServlet;
import eu.ginere.base.web.servlet.info.ServletArgs;
import eu.ginere.base.web.util.UserAgentManager;


public class IsAgentSupported extends SimpleBooleanServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String URI = "/servlet/util/IsAgentSupported";
	private static final String DESCRIPTION = "Return true or false if the agent is supported";

	@Override
	protected ServletArgs[] getArgs() {
		return ServletArgs.NO_ARGS;
	}
	
	@Override
	protected boolean doSimpleBooleanService(HttpServletRequest request,
			  HttpServletResponse response) throws ServletException,IOException,DaoManagerException{
		String agent=getUserAgent(request);
		
		return UserAgentManager.isSupported(agent);
	}
	
	@Override
	protected RightInterface[] getRights() throws ContextInitializedException {
		return MainServlet.PUBLIC_ACCESS;
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
