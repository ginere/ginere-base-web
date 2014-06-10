/**
 * Copyright: Angel-Ventura Mendo Gomez
 *	      ventura@free.fr
 *
 * $Id: InvalidateSession.java,v 1.2 2006/11/25 07:19:55 ventura Exp $
 */
package eu.ginere.base.web.session.services;

import eu.ginere.base.web.servlet.MainServlet;

/**
 * Use this servlet to invalidate a session
 *
 * @author Angel Mendo
 * @version $Revision: 1.2 $
 */
public abstract class InvalidateSession extends MainServlet {
//	public static final Logger log = Logger.getLogger(InvalidateSession.class);
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
//				SessionManager.invalidate(sessionId[i]);
//			}
//		}	
//	}
//	
//	
//	@Override
//	protected RightInterface[] getRights() throws ContextInitializedException {
//		return new RightInterface[]{AbstractWebContextListener.ADMIN_TECH_RIGHT};
//	}
}
