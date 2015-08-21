package eu.ginere.base.web.session.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.listener.AbstractWebContextListener;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.servlet.JSONServlet;
import eu.ginere.base.web.servlet.MainServlet;
import eu.ginere.base.web.servlet.info.ServletArgs;
import eu.ginere.base.web.session.AbstractSession;
import eu.ginere.base.web.session.RemoteHostSession;
import eu.ginere.base.web.session.RemoteHostSession.InnerError;
import eu.ginere.base.web.session.RemoteHostSession.SpetialCall;

@SuppressWarnings("serial")

@WebServlet(description="Returns the active sessions for each remote host",value="/servlet/util/GetRemoteHostSessionList", asyncSupported=false)
public class GetRemoteHostSessionList extends MainServlet{


	private static final String URI = "/servlet/util/GetRemoteHostSessionList";
	private static final String DESCRIPTION = "Returns the active sessions for each remote host";

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
		
		writer.println("Remote Host connected:");
		writer.print(RemoteHostSession.MANAGER.getremoteHostNumber());
		writer.println("</p>");
		
		List <String> remoteHostSessionList=RemoteHostSession.MANAGER.getRemoteHosSessiontList();
		
		for (String remoteHost:remoteHostSessionList){
			writer.println("Remote Host:<b>");
			writer.print(remoteHost);
			
			InnerError error=RemoteHostSession.MANAGER.getLastError(remoteHost);
			
			if (error!=null){
				writer.println("<pre>");
				// time
				writer.println(new Date(error.getTime()));
				
				// exception
				Exception e=error.getException();
				writer.println(e.getMessage());
				e.printStackTrace(writer);
				
				writer.println("</pre>");
			}
			
			SpetialCall spetialCall=RemoteHostSession.MANAGER.getLastSpetialCall(remoteHost);
			
			if (spetialCall!=null){
				writer.println("<pre>");
				// time
				writer.println(new Date(spetialCall.getTime()));
				
				// exception
				writer.println(spetialCall.getUri());
				
				writer.println("</pre>");
			}
			writer.println("</b></p>");
			
			
			List<AbstractSession> sessionList=RemoteHostSession.MANAGER.getRemoteHostSessions(remoteHost);
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
	protected RightInterface[] getRights() throws ContextInitializedException {
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
