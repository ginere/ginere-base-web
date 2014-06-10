package eu.ginere.base.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

abstract public class RedirectServlet extends MainServlet {

	abstract protected String getUrl(HttpServletRequest request,
			HttpServletResponse response) throws ServletException;

	@Override
	/*
	 * * Realiza un forward a una url absoluta
	 */
	protected void doService(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		redirect(request, response, getUrl(request, response));
	}
}
