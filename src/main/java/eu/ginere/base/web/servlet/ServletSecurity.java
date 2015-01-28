package eu.ginere.base.web.servlet;

import java.io.IOException;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import eu.ginere.base.util.properties.GlobalFileProperties;
import eu.ginere.base.web.session.RemoteHostSession;
import eu.ginere.base.web.session.SessionAccesor;
import eu.ginere.base.web.session.SessionManager;

public class ServletSecurity {

	public static final Logger log = Logger.getLogger(ServletSecurity.class);
	
//	private static HashSet<String> privilegedRemoteClients;
//	private static String validRefered[];
	private static String CAPCHA_URL;

	
	private static boolean initialized=false;
	
	public static void init(){
//		privilegedRemoteClients=GlobalFileProperties.getPropertyMap(ServletSecurity.class, "PrivilegedRemoteClients");
//		validRefered=GlobalFileProperties.getPropertyList(ServletSecurity.class, "ValidRefered");
		CAPCHA_URL=GlobalFileProperties.getStringValue(ServletSecurity.class, "Captcha","http://en.wikipedia.org/wiki/CAPTCHA");
		initialized=true;
	}

	public static boolean isInitialized(){
		return initialized;
	}
	
	static public void isFirstSessionCall(HttpServletRequest request,MainServlet servlet) throws ServletSecurityException{
		// testing if there is a valid session
		HttpSession session = request.getSession();
		if (!SessionAccesor.isFirstCall(session)){
			return ;
		} 

		if (isPrivilegedRemoteClient(request,servlet)){
			return ;
		} else {
			throw ServletSecurityException.create("Do is the first session call:",request,servlet);
		}
	}
	
	
	static public void testReferer(HttpServletRequest request,MainServlet servlet)throws ServletSecurityException{	
		
		String validRefered[]=GlobalFileProperties.getPropertyList(ServletSecurity.class, "ValidRefered");
		
		String referer=request.getHeader("referer");

		if (validRefered==null || validRefered.length == 0){
			log.warn("Not valid refered defined, for uri:'"+servlet.getUri()+"'");
			return ;
		}
		
		// si tiene refered lo comprobamos
		if (referer!=null && !"".equals(referer)){
			for (String valid:validRefered){
				if (referer.startsWith(valid)){
					return ;
				}
			}
		}

		// no tiene el refered, o es invalido. verificamos que pertenecea los granted
		if (isPrivilegedRemoteClient(request,servlet)){
			return ;
		} else {
			throw ServletSecurityException.create("Do not have a valid referer",request,servlet);
		}
	}
	

	/**
	 * Test if the client make to many spetial calls
	 * @param request
	 * @param servlet
	 * @throws ServletSecurityException
	 */
	static public void testToManySpetialCalls(HttpServletRequest request,MainServlet servlet)throws ServletSecurityException{		
		if (isPrivilegedRemoteClient(request,servlet)){
			return ;
		} else {
			HttpSession session=request.getSession();
			
			testToManySessionSpetialCalls(session,servlet);
			
			testToManyRemoteHostSpetialCalls(session,servlet);
		}
	}
	
	private static void testToManySessionSpetialCalls(HttpSession session,MainServlet servlet) {
		long time=SessionManager.MANAGER.getSession(session).getLastSpetialCallTime();
//        int minErrorTime=GlobalFileProperties.getIntValue(ServletSecurity.class, "SessionMinSpetialCallLaps", 3000);
//        long timeToSleep=GlobalFileProperties.getIntValue(ServletSecurity.class, "SessionMinSpetialCallTimeToSleep", 10000);
        int minErrorTime=servlet.getTestToManySessionSpetialCallsLap();
        long timeToSleep=servlet.getTestToManySessionSpetialCallsPunish();

		if ( (System.currentTimeMillis()-time) <minErrorTime){
			log.warn("ToManySessionSpetialCalls For session ID:"+session.getId()+", uri:'"+servlet.getUri()+"'. Sleeping:"+timeToSleep);
			try {
				Thread.sleep(timeToSleep);
			} catch (InterruptedException e) {
			}
		}
	}
	
	private static void testToManyRemoteHostSpetialCalls(HttpSession session,MainServlet servlet) {
		String remoteAddress=SessionManager.MANAGER.getSession(session).getRemoteAddr();
		long time=RemoteHostSession.MANAGER.getLastSpetialCallTime(remoteAddress);
//		int minErrorTime=GlobalFileProperties.getIntValue(ServletSecurity.class, "RemoteHostMinSpetialCallLaps", 5000);
//		long timeToSleep=GlobalFileProperties.getIntValue(ServletSecurity.class, "RemoteHostMinSpetialCallTimeToSleep", 10000);
		int minErrorTime=servlet.getTestToManyRemoteHostSpetialCallsLap();
		long timeToSleep=servlet.getTestToManyRemoteHostSpetialCallsPunish();
		
		if ( (System.currentTimeMillis()-time) <minErrorTime){
			log.warn("ToManyRemoteHostSpetialCalls For remoteHost ID:"+remoteAddress+", uri:'"+servlet.getUri()+"'. Sleeping:"+timeToSleep);
			try {
				Thread.sleep(timeToSleep);
			} catch (InterruptedException e) {
			}
		}
	}
	
	/**
	 * Test if the session or the remote host had made to many errors.
	 * 
	 * @param request
	 * @param servlet
	 * @throws ServletSecurityException
	 */
	static public void testToManyErrors(HttpServletRequest request,MainServlet servlet)throws ServletSecurityException{		
		if (isPrivilegedRemoteClient(request,servlet)){
			return ;
		} else {
			HttpSession session=request.getSession();
			
			testToManySessionErrors(session,servlet);
			
			testToManyRemoteHostErrors(session,servlet);
		}
	}

	private static void testToManySessionErrors(HttpSession session,MainServlet servlet) {
		long time=SessionManager.MANAGER.getSession(session).getLastErrorTime();
		int minErrorTime=GlobalFileProperties.getIntValue(ServletSecurity.class, "SessionMinErrorTimeLaps", 1000);
		long timeToSleep=GlobalFileProperties.getIntValue(ServletSecurity.class, "SessionMinErrorTimeTimeToSleep", 2000);
		
		if ( (System.currentTimeMillis()-time) <minErrorTime){
			log.warn("ToManySessionError For session ID:"+session.getId()+", uri:'"+servlet.getUri()+"'. Sleeping:"+timeToSleep);
			try {
				Thread.sleep(timeToSleep);
			} catch (InterruptedException e) {
			}
		}
	}
	
	private static void testToManyRemoteHostErrors(HttpSession session,MainServlet servlet) {
		String remoteAddress=SessionManager.MANAGER.getSession(session).getRemoteAddr();
		long time=RemoteHostSession.MANAGER.getLastErrorTime(remoteAddress);
		int minErrorTime=GlobalFileProperties.getIntValue(ServletSecurity.class, "RemoteHostMinErrorTimeLaps", 1000);
		long timeToSleep=GlobalFileProperties.getIntValue(ServletSecurity.class, "RemoteHostMinErrorTimeTimeToSleep", 2000);
		
		if ( (System.currentTimeMillis()-time) <minErrorTime){
			log.warn("ToManySessionError For remoteHost ID:"+remoteAddress+", uri:'"+servlet.getUri()+"'. Sleeping:"+timeToSleep);
			try {
				Thread.sleep(timeToSleep);
			} catch (InterruptedException e) {
			}
		}
	}


	static public boolean isPrivilegedRemoteClient(HttpServletRequest request,MainServlet servlet){
		HashSet<String> privilegedRemoteClients=GlobalFileProperties.getPropertyMap(ServletSecurity.class, "PrivilegedRemoteClients");
		
		String remoteAddress=request.getRemoteAddr();
		if (privilegedRemoteClients.contains(remoteAddress)){
			if (log.isInfoEnabled()){
				log.info("Granted testHash Remote to RemoteAddress:"+remoteAddress+" servlet:"+servlet.getClass()+" uri:"+servlet.getUri());
			}
			return true;
		} else {
			return false;
		}		
	}

	
	static public void redirectToCapcha(HttpServletRequest request, HttpServletResponse response,MainServlet servlet) throws IOException{
		String remoteAddress=request.getRemoteAddr();
		log.warn("Sending to CAPTHA to RemoteAddress:"+remoteAddress+" servlet:"+servlet.getClass()+" uri:"+servlet.getUri());
		MainServlet.redirect(request, response, CAPCHA_URL);
	}
	
	/**
	 * This banne all que queries from this session. But the client is not informed
	 * 
	 * @param request
	 * @param response
	 * @param servlet
	 * @throws IOException
	 */
	static public void banneSesion(HttpServletRequest request, HttpServletResponse response,MainServlet servlet) throws IOException{
		// TODO
	}
	
	/**
	 * Disble request from this remote address. Attention On big corporate networks all the request come from a proxy.
	 * If you banned this addres, you banne all the users of the proxy.
	 * You hacve to test that this is not a big corporate.
	 * @param request
	 * @param response
	 * @param servlet
	 * @throws IOException
	 */
	static public void banneRemoteAddress(HttpServletRequest request, HttpServletResponse response,MainServlet servlet) throws IOException{
		// TODO
	}

	public static void noRobots(HttpServletRequest request,
			MainServlet mainServlet) throws ServletSecurityException{
		if (SessionManager.MANAGER.getSession(request).isRobot()){
			throw ServletSecurityException.create("No Robots allowed agent:'"+SessionManager.MANAGER.getSession(request).getUserAgent()+"'", request, mainServlet);
		}
		
	}
}
