package eu.ginere.base.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.ginere.base.util.dao.DaoManagerException;

abstract public class SimpleBooleanServlet extends SimpleDataServlet {

	private static final long serialVersionUID = 1L;

	abstract protected boolean doSimpleBooleanService(
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DaoManagerException;

	protected String doSimpleDataService(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			DaoManagerException {
		if (doSimpleBooleanService(request, response)) {
			return TRUE;
		} else {
			return FALSE;
		}
	}

}
