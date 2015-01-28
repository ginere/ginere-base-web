/**
 * Copyright: Angel-Ventura Mendo Gomez
 *	      ventura@free.fr
 *
 * $Id: SessionManager.java,v 1.5 2006/11/25 07:19:55 ventura Exp $
 */
package eu.ginere.base.web.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.log4j.Logger;

import eu.ginere.base.util.notification.Notify;
import eu.ginere.base.util.properties.GlobalFileProperties;
import eu.ginere.base.web.services.Login;


/**
 * Use this manager to manage session control an destroy.
 * For this manager to run the function initSession must be called altmoust one time per session.
 *
 * @author Angel Mendo
 * @version $Revision: 1.5 $
 */
public class SessionManager {
	public static final Logger log = Logger.getLogger(SessionManager.class);

	private static final String CONTEXT_SESSION_PARAMETER_NAME=SessionManager.class.getName()+".CONTEXT_SESSION_PARAMETER_NAME";
	
	public static final SessionManager MANAGER = new SessionManager();

	public static final int DEFAULT_SESSION_EXPIRATION_TIME_IN_SECONDS = 5*60; // 5 minutos

	// The session currenly created
	private long sessionNumber; 
	// The singleton of the Context Class. This is used to bounf and unbound context
	private final Context CONTEXT;

	// private static final Vector SESSION_LIST=new Vector();
	private final Map<String,AbstractSession> SESSION_HASH=new Hashtable<String,AbstractSession>();
	
	private boolean enabled=false;
	
	private SessionManager(){
		sessionNumber=0;
		CONTEXT=new Context(this);
		
		addInitSessionListener(RemoteHostSession.MANAGER);
		addDestroySessionListener(RemoteHostSession.MANAGER);
	}

	public void enable(){
		enabled = true;
	}

	public void disable(){
		enabled=false;
	}

	
	/**
	 * The one object of this class is stored into the session. This class implements the HttpSessionBindingListener
	 * The when this object is bounded or unbounded into the session the listeners will be called.
	 * This object is bounded and unbonded at session initialization and destroy. 
	 */
	static private class Context implements HttpSessionBindingListener{
		SessionManager manager;
		Context(SessionManager manager){
			this.manager=manager;
		}
		
		public void valueBound(HttpSessionBindingEvent event){
			manager.boundSession(event.getSession());
		}
		
		public void valueUnbound(HttpSessionBindingEvent event){
			manager.unboundSession(event.getSession());
		}
	}
	
	/**
	 * use this function to initialize the session. This function must be 
	 * called at least one time for each session. We use the request to 
	 * initializate several values of the session.
	 * If the session is allready initialized this do nothing
	 */
    public void initSession(HttpServletRequest request, HttpServletResponse response){

    	if (!enabled){
    		if (log.isDebugEnabled()){
    			log.debug("SessionManager is disabled");
    		}
    		return ;
    	}
    	// Todas las sessiones se gestionaran ...
//		if (!GlobalFileProperties.getBooleanValue(SessionManager.class,"ManageSessions",true)){
//			if (log.isInfoEnabled()){
//				log.info("Manage Session is desactived use property ManageSessions");
//			}
//			return ;
//		}

		HttpSession session=request.getSession();

		// session.setMaxInactiveInterval(30);

		if (!isSessionBound(session)){
			// Primero iniciamos nuestro objeto session que es el AbstractSession
			initSession(session,request,response);

			// Despues la hacer el bound con la session llamamos a los listeners
			// Setting the context into the ssesion 
			// when the session willbe bind or unbind the funtions 
			// of the interface will be called
			session.setAttribute(CONTEXT_SESSION_PARAMETER_NAME,CONTEXT);
			
			// Como la session ha sido inicializado, hacemos el update
			SessionAccesor.setLastUpdated(session);
			
			// Ya no es necesario porque le methodo egtSessio nos da si la 
			// sesion a sido recientemente creada ...
			// SessionAccesor.setFirstCall(session);
		} else {
			// Ya no es necesario porque le methodo egtSessio nos da si la 
			// sesion a sido recientemente creada ...
			// SessionAccesor.removeFirstCall(session);
		}

		// Siempre intentamos hacer un login por cookie, otra aplicacion a podido crear la cookie 
		// en la raiz del servidor.
		// El UUID ya esta almacenado en sesion porque es una de las priemras cosas que se hacen al iniciar la session
		Login.loginAndLogoutByCookie(request,response);
		session.setMaxInactiveInterval(GlobalFileProperties.getIntValue(SessionManager.class, "MaxInactiveIntervalInSeconds", DEFAULT_SESSION_EXPIRATION_TIME_IN_SECONDS));
		// will remove this ???, no we can use it to trace session
//		updateSession(session,request);		
	}

	/**
	 * if returns true the session has been already initialized,
	 * otherwise session must be creted or initialized.
	 */
    protected boolean isSessionBound(HttpSession session){
		if (session==null){
			return false;
		}

		Context context=(Context)session.getAttribute(CONTEXT_SESSION_PARAMETER_NAME);
		if (context==null){
			return false;
		} else {
			return true;
		}
    }

    protected void boundSession(HttpSession session){
		log.debug("Sesion IN:"+session.getId());
		sessionNumber++;		

		// first we count the session
		callInitSessionListener(session,sessionNumber);
    }

	/**
	 * this function destroys all concerning the session.
	 */
    protected void unboundSession(HttpSession session){
		// WARNING WE CAN NOT ACCESS the session values ????
		///
		String sessionId=session.getId();
//		AbstractSession sessionInterface=getSession(sessionId);

		try {
			// firs call the logout
			UserSession.removeUser(getSession(sessionId));
			
			// we call listener defore destroy it
			callDestroySessionListeners(session,sessionNumber);
			
			// This function tes is one user is loged and if any this will 
			// call the userLogout listener
			// UserManager.userLogout(session);

			Notify.debug(log, "Session Destroyed:'"+sessionId+"'");
		}catch(Exception e){
			log.error("While unboundSession for session id'"+sessionId+"'",e);
		} finally {
			// removes the sessionInterface
			removeSession(sessionId);

			// Destroy all stored attr into the session if necessaire
			log.debug("Sesion OUT:"+session.getId());
			sessionNumber--;
		}
    }




	/**
	 * This function calls all the init session listeners
	 * @param session The session to initialize
	 * @param sessionNumber including hte new one created
	 */
    private void callInitSessionListener(HttpSession session,long sessionNumber){
    	// AbstractSession sessionInterface=storeSession(session); ...
		AbstractSession sessionInterface=(AbstractSession)getSession(session.getId());

		for (InitSessionListener listener:initSessionListenerList){
			listener.init(session,sessionInterface,sessionNumber);
		}
//		for(Iterator i=initSessionListenerList.iterator();i.hasNext();){
//			InitSessionListener listener=(InitSessionListener)i.next();
//			listener.init(session,sessionNumber);
//		}
    }

	
    /**
     * Call all delete listeners
	 * @param session The session to destroy
	 * @param sessionNumber The session number including this one
	 */
	private void callDestroySessionListeners(HttpSession session,long sessionNumber){
		AbstractSession sessionInterface=getSession(session.getId());
		
		for (DestroySessionListener listener:destroySessionListenerList){
			listener.destroy(session,sessionInterface,sessionNumber);
		}
		
//		for(Iterator i=destroySessionListenerList.iterator();i.hasNext();){
//			DestroySessionListener listener=(DestroySessionListener)i.next();
//			listener.destroy(session,sessionNumber);
//		}
    }


	// The vector to store listeners. We use vector because synchronation
    private final Vector<InitSessionListener> initSessionListenerList=new Vector<InitSessionListener>();
    private final Vector<DestroySessionListener> destroySessionListenerList=new Vector<DestroySessionListener>();


	static public interface InitSessionListener{
		public void init(HttpSession session,AbstractSession sessionInterface,long sessionNumber);
    }

	static public interface DestroySessionListener{
		/**
		 * WARNING the session is invalidated when passed to this function
		 * there is several functions that can't be called like session.getAttribute()
		 * pay attention.
		 */
		public void destroy(HttpSession session,AbstractSession sessionInterface,long sessionNumber);
    }

    /**
     * Add one listener that will be called for each session to initialize
	 * @param listener
	 */
	public void addInitSessionListener(InitSessionListener listener){
		initSessionListenerList.add(listener);
    }

    /**
     * Add a listener that will be called for each session to destroy
	 * @param listener
	 */
	public void addDestroySessionListener(DestroySessionListener listener){
		destroySessionListenerList.add(listener);
    }
    
	
	/**
	 * Return the current number of sessions ...
	 * @return
	 */
	public long getSessionNumber() {
		return sessionNumber;
	}

	/**
	 * Returs true if the session is valide.
	 * This is usefull to test when the Attribute is unbound 
	 * if it is due to a session expiration or invalidation
	 */
	public boolean isSessionValide(HttpSession session){
		try {
			session.getAttribute("");
			return true;
		}catch(Exception e){
			return false;
		}
	}

	/**
	 * Store the session to be traced
	 */
	private AbstractSession storeSession(HttpSession session){
		AbstractSession sessionInterface=new AbstractSession(session);
		// SESSION_LIST.add(sessionInterface);
		SESSION_HASH.put(session.getId(),sessionInterface);
		
		return sessionInterface;
	}

	/**
	 * Store the session to be traced
	 */
	private void removeSession(String sessionId){
		// AbstractSession sessionInterface=(AbstractSession)SESSION_HASH.get(session.getId());
		
		// SESSION_LIST.remove(sessionInterface);
		SESSION_HASH.remove(sessionId);		
	}

	private void initSession(HttpSession session,HttpServletRequest request,HttpServletResponse response){
		AbstractSession sessionInterface=storeSession(session);

		// 		AbstractSession sessionInterface=(AbstractSession)getSession(session.getId());
		
		sessionInterface.init(request,response);
	}

//	private static void updateSession(HttpSession session,HttpServletRequest request){
//		AbstractSession sessionInterface=(AbstractSession)getSession(session.getId());
//		sessionInterface.update(request);
//	}

	/**
	 * Returns a list of session interface
	 */
	public List<AbstractSession> getSessionList(){
		// return (List)SESSION_LIST.clone();
		Collection<AbstractSession> c=SESSION_HASH.values();
		return new ArrayList<AbstractSession>(c);
	}
	
	public AbstractSession getSession(HttpServletRequest request){
		return getSession(request.getSession());
	}

	public AbstractSession getSession(HttpSession session){
		return getSession(session.getId());
	}

	public AbstractSession getSession(String sessionId){
		return (AbstractSession)SESSION_HASH.get(sessionId);
	}

	public void invalidate(String sessionId){
		AbstractSession sessionInterface=getSession(sessionId);
		if (sessionInterface!=null && (sessionInterface instanceof AbstractSession) ){
			((AbstractSession)sessionInterface).invalidate();
		}
		removeSession(sessionId);
	}

	public String getTraceMessage(HttpServletRequest request){		
		return getTraceMessage(request.getSession());
	}
	
	public String getTraceMessage(HttpSession session){		
		AbstractSession sessionInterface=getSession(session);
		return sessionInterface.getTraceMessage();
	}
	
//	/**
//	 * This sets this session to be traced, 
//	 * The events will be generated to this session.
//	 */
//	static public void traceSession(HttpSession session){
//		AbstractSession sessionInterface=getSession(session);
//
//		sessionInterface.trace();
//	}

	public String getUserAgentFamilly(HttpServletRequest request){
		return getUserAgentFamilly(request.getSession());
	}
	
	public String getUserAgentFamilly(HttpSession session){
		AbstractSession sessionInterface=getSession(session);
		return sessionInterface.getUserAgentFamilly();
	}

	public void notifyError(HttpServletRequest request, Exception e) {
		AbstractSession si=getSession(request);
		
		if (si!=null){
			si.setError(e);
			
			RemoteHostSession.MANAGER.addError(si.getRemoteAddr(),e);
		} else {
			log.info("Security error not notified to SessionManager and to RemoteHostManager",e);
		}
	}

	public void notifySpetialCall(HttpServletRequest request, String uri) {
		AbstractSession si=getSession(request);
		
		si.addSpetialCall(uri);
		
		RemoteHostSession.MANAGER.addSpetialCall(si.getRemoteAddr(),uri);
		
	}

}
