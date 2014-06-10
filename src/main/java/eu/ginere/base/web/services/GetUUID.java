package eu.ginere.base.web.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.servlet.MainServlet;
import eu.ginere.base.web.servlet.SimpleDataServlet;
import eu.ginere.base.web.servlet.info.ServletArgs;
import eu.ginere.base.web.util.CookieUtils;


public class GetUUID extends SimpleDataServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String URI = "/servlet/util/uuid";
	private static final String DESCRIPTION = "Returns the uuid associated to this connection";

	@Override
	protected ServletArgs[] getArgs() {
		return ServletArgs.NO_ARGS;
	}
	
	@Override
	protected String doSimpleDataService(HttpServletRequest request,
										HttpServletResponse response) throws ServletException,IOException,DaoManagerException{

		return CookieUtils.generateOrGetUUIDCookie(request, response);
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
