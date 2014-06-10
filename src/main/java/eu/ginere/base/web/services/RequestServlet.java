package eu.ginere.base.web.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.servlet.MainServlet;
import eu.ginere.base.web.servlet.info.ServletArgs;


public class RequestServlet extends MainServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String URI = "/servlet/util/Ping";
	private static final String DESCRIPTION = "Answer with the PONG string";

	@Override
	protected ServletArgs[] getArgs() {
		return ServletArgs.NO_ARGS;
	}
	
	@Override
	protected void doService(HttpServletRequest request,
							 HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer=response.getWriter();

		writer.println("<html>");

		writer.println(" --- Date:" + (new Date()));
		writer.println("</p>");
		printHeaders(writer,request);

		writer.println(" --- ");
		writer.println("</p>");
		
		printParameters(writer,request);
		writer.println(" --- ");
		writer.println("</p>");
		
		
		writer.println(" RemoteAddr: "+request.getRemoteAddr());
		writer.println("</p>");
		writer.println(" getRemoteUser: "+request.getRemoteUser());
		writer.println("</p>");
		
		
		writer.println(" Cookies: ");
		
		Cookie array[]=request.getCookies();
		for (Cookie cookie:array){
			writer.println(" Name: "+cookie.getName());
			writer.println(" Path: "+cookie.getPath());
			writer.println(" Value: "+cookie.getValue());
			writer.println(" Comment: "+cookie.getComment());
			writer.println(" Domain: "+cookie.getDomain());
			writer.println(" MaxAge: "+cookie.getMaxAge());
			writer.println(" Version: "+cookie.getVersion());
			writer.println(" Secure: "+cookie.getSecure());
			
			
			writer.println("</p>");
				
		}

		writer.println("</html>");
	}

	public static void printHeaders(PrintWriter writer,HttpServletRequest request) {
		Enumeration enumeracion = request.getHeaderNames();

		while (enumeracion.hasMoreElements()) {
			String header = (String) enumeracion.nextElement();
			String value = request.getHeader(header);
			writer.println("Header:'" + header + "' value:'" + value + "'");
			writer.println("</p>");
			
		}

	}

	public static void printParameters(PrintWriter writer,HttpServletRequest request) {
		Enumeration enumeracion = request.getParameterNames();

		while (enumeracion.hasMoreElements()) {
			String header = (String) enumeracion.nextElement();
			String value = request.getParameter(header);
			writer.println("Parameter:'" + header + "' value:'" + value
					+ "'");
			writer.println("</p>");
			
		}
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
