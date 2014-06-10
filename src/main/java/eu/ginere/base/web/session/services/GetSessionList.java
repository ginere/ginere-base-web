package eu.ginere.base.web.session.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
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
import eu.ginere.base.web.session.SessionManager;

@SuppressWarnings("serial")
public class GetSessionList extends MainServlet{


	private static final String URI = "/servlet/util/GetSessionList";
	private static final String DESCRIPTION = "Returns the list of the active sessions";

	@Override
	protected ServletArgs[] getArgs() {
		return ServletArgs.NO_ARGS;		
	}
	
//	@Override
//	protected void configureGsonBuilder(GsonBuilder gsonBuilder) {
//		
//	}
//	
	
//	@Override
//	protected Object doTaskJSONService(HttpServletRequest request,
//			HttpServletResponse response) throws ServletException, IOException,
//			DaoManagerException {
//		
//		List<AbstractSession> list=SessionManager.getSessionList();
//		
//		List<String>ret=new ArrayList<String>(list.size());
//		
//		for (AbstractSession si:list){
//			ret.add(si.getTraceMessage());
//		}
//		
//		return ret;
//	}

	@Override
	protected void doService(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		response.setCharacterEncoding(JSONServlet.CHARSET);
		response.setContentType("text/html");
		
		PrintWriter writer=response.getWriter();
		writer.println("<html><body>");
		
		List<AbstractSession> sessionList=SessionManager.MANAGER.getSessionList();
		
		writer.println("Sessiones:");
		writer.print(sessionList.size());
		writer.println("</p>");
		
		for (AbstractSession si:sessionList){
//			writer.println("");
//			writer.print(si.getTraceMessage());
//			writer.println("</p>");					

			printSession(writer,si);
		}
		
		writer.println("</body></html>");

	}
	
	@Override
	protected RightInterface[] getRights() throws ContextInitializedException {
		return new RightInterface[]{AbstractWebContextListener.ADMIN_TECH_RIGHT};
	}
	
	@Override
	protected String getUri() {
		return URI;
	}

	@Override
	protected String getDescription() {
		return DESCRIPTION;
	}

	public static void printSession(PrintWriter writer, AbstractSession si) {
			writer.println("<pre>");

			writer.print("Id:");
			writer.println(si.getId());

			writer.print("UUID:");
			writer.println(si.getUUID());

			writer.print("Robot:");
			writer.println(si.isRobot());

			writer.print("UserId:");
			writer.println(si.getUserId());

			writer.print("RemoteAddr:");
			writer.println(si.getRemoteAddr());

			writer.print("Familly:");
			writer.println(si.getUserAgentFamilly());

			writer.print("Agent:");
			writer.println(si.getUserAgent());

			writer.print("Lang:");
			writer.println(si.getLanguage());

			writer.println(" --- ");

			writer.print("LastUri:");
			writer.println(si.getLastUri());

			writer.print("ServletPath:");
			writer.println(si.getServletPath());

			writer.println(" --- ");

			writer.print("CreationTime:");
			writer.println(si.getCreationTime());

			writer.print("LastAccessedTimee:");
			writer.println(si.getLastAccessedTime());

			writer.print("LastUpdated:");
			writer.println(si.getLastUpdated());

			writer.print("MaxInactiveInterval:");
			writer.println(si.getMaxInactiveInterval());

			writer.println(" --- ");

			writer.print("LastError:");
			writer.println(new Date(si.getLastErrorTime()));
			Exception e=si.getLastError();
			if (e!=null){
				writer.println(e.getMessage());
				e.printStackTrace(writer);
			}
			
			writer.print("LastSpetialCall:");
			writer.print(si.getLastSpetialCall());
			writer.print(" ");
			writer.println(new Date(si.getLastSpetialCallTime()));

			writer.println(" --- ");
			
			writer.println(si.getTraceMessage());
			writer.println("</pre>");
	}

}
