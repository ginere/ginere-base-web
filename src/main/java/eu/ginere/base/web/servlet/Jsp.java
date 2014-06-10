package eu.ginere.base.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.servlet.info.ServletArgs;

/**
 * Clase madre para los JSP
 *
 * @author A. Mendo
 */
public abstract class Jsp extends MainServlet {
	private static final long serialVersionUID = 1L;
	
	public static final Logger log = Logger.getLogger(Jsp.class);
	
	/**
	 * Entry point into service.
	 */
	public abstract void _jspService(HttpServletRequest request,
									 HttpServletResponse response) throws ServletException, IOException,DaoManagerException;

	protected void doService(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,DaoManagerException{
		log.warn("Llamando al metodo do service, no se tendria que llamar a este metodo");
		_jspService(request, response);
	}
	
	/**
	 * Esta funcion define los derechos de acceso al servlet.
	 * Por defecto los JSP son publicos
	 * 
	 * @return
	 * @throws ContextInitializedException
	 */
	protected RightInterface[] getRights()throws ContextInitializedException{
		return MainServlet.PUBLIC_ACCESS;
	}
	
	protected String getUri(){
		return null;
	}
	
	protected String getDescription(){
		return null;
	}
	protected ServletArgs[] getArgs(){
		return null;
	}
}
