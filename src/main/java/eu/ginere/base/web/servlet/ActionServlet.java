package eu.ginere.base.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.ginere.base.util.dao.DaoManagerException;

public abstract class ActionServlet extends MainServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doService(HttpServletRequest request,
							 HttpServletResponse response) throws ServletException, IOException,DaoManagerException {
		doSimpleActionService(request, response);

		return;
	}

	abstract protected void doSimpleActionService(HttpServletRequest request,
												  HttpServletResponse response) throws ServletException,IOException,DaoManagerException;

}
