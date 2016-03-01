package eu.ginere.base.web.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.util.notification.Notify;
import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.servlet.MainServlet;
import eu.ginere.base.web.servlet.info.ServletArgs;
import eu.ginere.base.web.session.AbstractSession;
import eu.ginere.base.web.session.SessionManager;


public class SendJSLog extends MainServlet {
	public static final Logger log = Logger.getLogger(SendJSLog.class);

	private static final long serialVersionUID = 1L;
	
	private static final String URI = "/servlet/util/SendJSLog";
	private static final String DESCRIPTION = "Receive a JS log notification";

	@Override
	protected ServletArgs[] getArgs() {
		return new ServletArgs[]{
				ServletArgs.getMandatoryStringParameter("level"),
				ServletArgs.getMandatoryStringParameter("msg")
				
		};	
	}
	
	@Override
	protected void doService(HttpServletRequest request,
							 HttpServletResponse response) throws ServletException,IOException,DaoManagerException{
		int level = getMandatoryIntParameter(request, "level");
		String msg = getMandatoryStringParameter(request, "msg");
		AbstractSession absSession=SessionManager.MANAGER.getSession(request,null);

		if (absSession!=null){
			msg=absSession.getTraceMessage()+"\nRemote Message JS:\n-----\n"+msg+"\n-----\n";
		} else {
			msg="NO_SESSION \nRemote Message JS:\n-----\n"+msg+"\n-----\n";
		}
		/*
		log.warn("--------------------------------------------");
		log.warn("Notifications level:"+level);
		log.warn(msg);
		log.warn("--------------------------------------------");
		*/

		if (level==0){
			Notify.fatal(log,"JavaScrip Msg:"+msg);
		} else if (level==3){
			Notify.error(log,"JavaScrip Msg:"+msg);
		} else if (level==4){
			Notify.warn(log,"JavaScrip Msg:"+msg);
		} else if (level==6){
			Notify.info(log,"JavaScrip Msg:"+msg);

		} else if (level==7){
			Notify.debug(log,"JavaScrip Msg:"+msg);
		} else {
			Notify.error(log,"Log level:"+level+" not found. for JavaScrip Msg:"+msg);
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
