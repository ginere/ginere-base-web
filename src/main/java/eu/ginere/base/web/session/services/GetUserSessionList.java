package eu.ginere.base.web.session.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.listener.AbstractWebContextListener;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.servlet.JSONServlet;
import eu.ginere.base.web.servlet.MainServlet;
import eu.ginere.base.web.servlet.info.ServletArgs;
import eu.ginere.base.web.session.AbstractSession;
import eu.ginere.base.web.session.UserSession;

public class GetUserSessionList extends MainServlet{


	private static final String URI = "/servlet/util/GetUserSessionList";
	private static final String DESCRIPTION = "Returns the user active sessions";

	@Override
	protected ServletArgs[] getArgs() {
		return ServletArgs.NO_ARGS;		
	}
	

	
	@Override
	protected void doService(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		response.setCharacterEncoding(JSONServlet.CHARSET);
		response.setContentType("text/html");
		
		PrintWriter writer=response.getWriter();
		writer.println("<html><body>");
		
		writer.println("Users Connected");
		writer.print(UserSession.getUsersConnected());
		writer.println("</p>");
		
		List <String> userList=UserSession.getUserList();
		
		for (String userId:userList){
			writer.println("UsersId:<b>");
			writer.print(userId);
			writer.println("</b></p>");
			List<AbstractSession> sessionList=UserSession.getUserSession(userId);
			for (AbstractSession si:sessionList){
//				writer.println("");
//				writer.print(si.getTraceMessage());
//				writer.println("</p>");					

				GetSessionList.printSession(writer,si);
			}
		}
		
		writer.println("</body></html>");

	}

	@Override
	protected RightInterface[] getRights() {
//		return new RightInterface[]{AbstractWebContextListener.ADMIN_TECH_RIGHT};
		return AbstractWebContextListener.SUPER_ADMIN_TECH;
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
