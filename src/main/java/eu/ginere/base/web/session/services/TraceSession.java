/**
 * Copyright: Angel-Ventura Mendo Gomez
 *	      ventura@free.fr
 *
 * $Id: TraceSession.java,v 1.1 2006/05/07 14:47:04 ventura Exp $
 */
package eu.ginere.base.web.session.services;

import eu.ginere.base.web.servlet.MainServlet;

/**
 * Use this servlet to invalidate a session
 *
 * @author Angel Mendo
 * @version $Revision: 1.1 $
 */
public abstract class TraceSession extends MainServlet {
//	public static final Logger log = Logger.getLogger(TraceSession.class);
//
//	public static final String SESSION_ID = "ID";
//
//	protected void doService(HttpServletRequest request,
//							 HttpServletResponse response)
//		throws ServletException, IOException{		
//		String sessionId[] = getStringParameterArray(request, SESSION_ID);
//
//		for (int i=0;i<sessionId.length;i++){
//			if (sessionId[i]!=null){
//				List sessionList=SessionManager.getSessionList();
//				
//				for (Iterator it=sessionList.iterator();it.hasNext();){
//					AbstractSession sessionInterface=(AbstractSession)it.next();
//					
//					if (sessionInterface.getId().equals(sessionId[i])){
//						sessionInterface.trace();
//						break;
//					}
//				}
//			}
//		}	
//	}
//
//	@Override
//	protected RightInterface[] getRights() throws ContextInitializedException {
//		return new RightInterface[]{AbstractWebContextListener.ADMIN_TECH_RIGHT};
//	}

}
