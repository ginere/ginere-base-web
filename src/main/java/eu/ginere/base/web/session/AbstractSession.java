/**
 * Copyright: Angel-Ventura Mendo Gomez
 *	      ventura@free.fr
 *
 * $Id: AbstractSession.java,v 1.2 2006/11/25 07:19:55 ventura Exp $
 */
package eu.ginere.base.web.session;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import eu.ginere.base.util.i18n.Language;
import eu.ginere.base.util.notification.Notify;
import eu.ginere.base.web.servlet.MainServlet;
import eu.ginere.base.web.util.CookieUtils;
import eu.ginere.base.web.util.UserAgentManager;


/**
 * Use this manager to manage session control an destroy.
 * For this manager to run the function initSession must be called altmoust one time per session.
 *
 * @author Angel Mendo
 * @version $Revision: 1.2 $
 */
public class AbstractSession /* implements SessionInterface */ {
	public static final Logger log = Logger.getLogger(AbstractSession.class);

	private static final int ROBOT_MAX_INTERVAL=60; // in sec, the robot session expiration time 

	transient private final HttpSession session;

	private final Date creation;

	private String userAgent=null;
	private String remoteAddr=null;
	private String serverName=null;
	private String servletPath=null;
	private String lastUri=null;
	private String lastArgs=null;
//	private Language language=null;
	
//	private boolean traceSession=false;
	private boolean isRobot=false;
	private String userAgentFamilly=null;

	private Exception lastError=null;
	private long lastErrorTime=0;

	private String lastSpetialCall=null;
	private long lastSpetialCallTime=0;
	/**
	 * El user id the una sesion lo gestion el UserSessionManager
	 */
	private String userId;

	private String uuid;
	
	/**
	 * This is called when the session is bounded
	 * @param session
	 */
	AbstractSession(HttpSession session){
		this.session=session;
		this.creation=new Date(session.getCreationTime());
	}


	/**
	 * La primera vez que una session pasa por la request
	 * @param request
	 */
	public void init(HttpServletRequest request,HttpServletResponse response){
		userAgent=MainServlet.getUserAgent(request);
		remoteAddr=MainServlet.getRemoteAddress(request);
		serverName=request.getServerName();
		servletPath=request.getServletPath();
		lastUri=request.getRequestURI();
		lastArgs=request.getQueryString();
//		language=MainServlet.getLanguage(request);

		isRobot=UserAgentManager.isRobot(userAgent);
		userAgentFamilly=UserAgentManager.getUserAgentFamilly(userAgent);

		if (isRobot){
			session.setMaxInactiveInterval(ROBOT_MAX_INTERVAL);
		}
				
		uuid=CookieUtils.generateOrGetUUIDCookie(request, response);
		SessionAccesor.setUUID(session, uuid);
		
		Notify.debug(log, "Session Created:"+getTraceMessage());
		
//		session.setMaxInactiveInterval(5);
	}
	
	/**
	 * Function to set the values from requests.
	 * Se puede llamar para cada request
	 */
	public void update(HttpServletRequest request){
		serverName=request.getServerName();
		servletPath=request.getServletPath();
		lastUri=request.getRequestURI();
		lastArgs=request.getQueryString();
//		language=MainServlet.getLanguage(request);
		
//		if (traceSession){
//			SessionEventManager.push(SessionEventType.TRACE_SESSION,
//									 this,
//									 null);
//				/*
//				  EventManager.pushEvent(EventTypeEnum.TRACE_SESSION,
//				  RequestUtils.getUser(request),
//				  RequestUtils.getInstance(request),								   
//				  getTraceMessage(),
//				  getId());
//				*/
//		}
	}

	public String getId(){
		return session.getId();
	}

	/* (non-Javadoc)
	 * @see eu.ginere.base.web.session.SessionInterface#getUserId()
	 */
	public String getUserId(){
		return userId;
//		return SessionAccesor.getUserId(session);
	}
	
	public Date getCreationTime(){
		return creation;
	}

	public Date getLastAccessedTime() {
		return new Date(session.getLastAccessedTime());
	}
	
	public Date getLastUpdated() {
		Long l=SessionAccesor.getLastUpdated(session);
		
		if (l!=null){		
			return new Date(l);
		} else {
			return null;
		}
	}

	public int getMaxInactiveInterval() {
		return session.getMaxInactiveInterval();
	}

	/**
	 *
	 */
	public Language getLanguage(){
		return SessionAccesor.getLanguage(session);
	}

	/**
	 * Static Request Values. These values must be set width 
	 * initSession()
	 */
	public String getUserAgent(){
		return userAgent;
	}
	public String getRemoteAddr(){
		return remoteAddr;
	}
	public String getServerName(){
		return serverName;
	}

	/**
	 * Dynamic Request Values. These values must be setted 
	 * width updateSession
	 */
	public String getServletPath(){
		return servletPath;
	}
	public String getLastUri(){
		return lastUri;
	}
	public String getLastArgs(){
		return lastArgs;
	}

	/**
	 * Not a public function. Security problems.
	 * This must remove user before invalidatingthe session
	 */
	void invalidate() {
		try {
			session.invalidate();
		}catch(IllegalStateException e){
			log.warn(e);
		}
	}	

	/**
	 * returns the string to be displayed into a trace
	 * 	*/
	public String getTraceMessage(){
		StringBuilder buffer=new StringBuilder();
		buffer.append("Id:'");
		buffer.append(getId());
		buffer.append(" UUID:'");
		buffer.append(getUUID());
		buffer.append("' UserId:'");
		buffer.append(getUserId());
		buffer.append("' Familly:'");
		buffer.append(getUserAgentFamilly());
		buffer.append("' Agent:'");
		buffer.append(getUserAgent());
		buffer.append("' RemoteAddr:'");
		buffer.append(getRemoteAddr());
		buffer.append("' CreationTime:'");
		buffer.append(getCreationTime());
		buffer.append("' Lang:'");
		buffer.append(getLanguage());
		buffer.append("' lastUri:'");
		buffer.append(getLastUri());
		buffer.append("' ServletPath:'");
		buffer.append(getServletPath());
		buffer.append("' LastUpdated:'");
		buffer.append(getLastUpdated());
		buffer.append("' LastAccessed:'");
		buffer.append(getLastAccessedTime());
//		buffer.append("' InstanceId:'");
//		buffer.append(getInstanceId());
		buffer.append("' LastError:'");
		buffer.append(getLastError());
		buffer.append("' Date:'");
		buffer.append(new Date(getLastErrorTime()));
		
		buffer.append("' LastSpetialCall:'");
		buffer.append(getLastSpetialCall());
		buffer.append("' Date:'");
		buffer.append(new Date(getLastSpetialCallTime()));
		
		buffer.append("'");		

		return buffer.toString();
	}

	public String getUUID() {
		return uuid;
	}


	public String toString(){
		return getTraceMessage();
	}

//	/**
//	 * Returns if this session is beeing traced
//	 */
//	public boolean isTraced(){
//		return traceSession;
//	}

	public boolean isRobot(){
		return isRobot;
	}

	/**
	 * Returns the user agent familly associated to this session
	 */
	public String getUserAgentFamilly(){
		return userAgentFamilly;
	}

	public void setUser(String userId) {
		this.userId=userId;
	}


	public Exception getLastError(){
		return lastError;

	}

	public void setError(Exception e) {
		this.lastError=e;
		this.lastErrorTime=System.currentTimeMillis();
	}


	public long getLastErrorTime() {
		return lastErrorTime;
	}
	

//	public Date getLastErrorDate() {
//		return new Date(lastErrorTime);
//	}


	public void addSpetialCall(String uri) {
		this.lastSpetialCall=uri;
		this.lastSpetialCallTime=System.currentTimeMillis();
	}

	public long getLastSpetialCallTime() {
		return lastSpetialCallTime;
	}

	public String getLastSpetialCall() {
		return lastSpetialCall;
	}
	
	
	
//	/**
//	 * This active the trace for this sessions.
//	 * All server calls will be traced
//	 */
//	public void trace(){
//		traceSession=true;
//	}

//	/**
//	 * this set the last Accesed time to now
//	 */
//	public void update(){
//		getLang();
//	}
}
