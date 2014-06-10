/**
 * Copyright: Angel-Ventura Mendo Gomez
 *	      ventura@free.fr
 *
 * $Id: UserSession.java,v 1.2 2006/11/25 07:19:55 ventura Exp $
 */
package eu.ginere.base.web.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import eu.ginere.base.util.lang.MapOfLists;

/**
 * This manageer is used to store the sessions of each remote host.
 * All users behind a proxy has the same remote address.
 *
 * @author Angel Mendo
 * @version $Revision: 1.2 $
 */
public class RemoteHostSession  implements SessionManager.InitSessionListener,SessionManager.DestroySessionListener{
	public static final Logger log = Logger.getLogger(RemoteHostSession.class);
	
	private final MapOfLists<String,AbstractSession> REMOTE_HOST_MAP=new MapOfLists<String,AbstractSession> ();

	private int remoteHostsConnected=0;

	public static RemoteHostSession MANAGER=new RemoteHostSession();

	private Hashtable<String, InnerError> errorCache=new Hashtable<String, InnerError>();
	private Hashtable<String, SpetialCall> spetialCallCache=new Hashtable<String, SpetialCall>();

	public class InnerError{
		public final long time;
		public final Exception exception;
		
		InnerError(Exception e){
			this.exception=e;
			this.time=System.currentTimeMillis();
		}

		public long getTime() {
			return time;
		}

		/**
		 * @return the exception
		 */
		public Exception getException() {
			return exception;
		}		
	}
	public class SpetialCall{
		public final long time;
		public final String uri;
		
		SpetialCall(String uri){
			this.uri=uri;
			this.time=System.currentTimeMillis();
		}

		public long getTime() {
			return time;
		}

		/**
		 * @return the exception
		 */
		public String getUri() {
			return uri;
		}		

		
	}
	
	private RemoteHostSession(){
	}
	
	public void init(HttpSession session,AbstractSession sessionIntrface,long sessionNumber){
		String remoteHost=sessionIntrface.getRemoteAddr();

		if (!isRemoteHostLogged(remoteHost)){
			remoteHostsConnected++;
		}

		REMOTE_HOST_MAP.put(remoteHost,sessionIntrface);		
	}

	public void destroy(HttpSession session,AbstractSession sessionIntrface,long sessionNumber){
		String remoteHost=sessionIntrface.getRemoteAddr();
		
		if (remoteHost!=null){
			removeHost(remoteHost, sessionIntrface);
		}
	}
	

	/**
	 * returns true if the user is logged
	 */
	public boolean isRemoteHostLogged(String user){
		return REMOTE_HOST_MAP.containsKey(user);
	}
	
	private void removeHost(String remoteHost,AbstractSession session){		
			
		String sessionId=session.getId();

		if (REMOTE_HOST_MAP.containsKey(remoteHost)){
			List<AbstractSession> list=REMOTE_HOST_MAP.get(remoteHost);

			// several user over the same proxy can remove ther sessions at the same time
			synchronized(list){
				if (list!=null && list.size()>0){
					for (Iterator<AbstractSession> i=list.iterator();i.hasNext();){
						AbstractSession current=(AbstractSession)i.next();
						if (sessionId.equals(current.getId())){
							i.remove();
						}
					}
					if (list.size()==0){
						REMOTE_HOST_MAP.remove(remoteHost);						
						remoteHostsConnected--;				
						errorCache.remove(remoteHost);
						spetialCallCache.remove(remoteHost);
					}
				} else {
					REMOTE_HOST_MAP.remove(remoteHost);
					log.warn(" The userid:"+remoteHost+" has an empty list:"+list);
				}
			}
		} 
	}

	/**
	 * Clonned can modify
	 */
	public List<AbstractSession> getRemoteHostSessions(String remoteHost){
		List<AbstractSession> list=REMOTE_HOST_MAP.get(remoteHost);
		if (list==null){
			return Collections.emptyList();
		} else {
			return new ArrayList<AbstractSession>(list);
		}
	}


	/**
	 * Clonned can modify
	 */
	public List<String> getRemoteHosSessiontList(){
		Set<String> set=REMOTE_HOST_MAP.keySet();
		if (set==null){
			return Collections.emptyList();
		} else {
			return new ArrayList<String>(set);
		}
	}

	public int getremoteHostNumber(){
		return remoteHostsConnected;
	}

	public long getLastErrorTime(String remoteAddress) {
		if (errorCache.containsKey(remoteAddress)){
			return errorCache.get(remoteAddress).getTime();
		} else {
			return 0;
		}
	}

	public InnerError getLastError(String remoteHost){
		if (errorCache.containsKey(remoteHost)){
			return errorCache.get(remoteHost);
		} else {
			return null;
		}
	}

	public void addError(String remoteAddr, Exception e) {
		InnerError error=new InnerError(e);
		
		errorCache.put(remoteAddr,error);		
	}

	public void addSpetialCall(String remoteAddr, String uri) {
		SpetialCall spetialCall=new SpetialCall(uri);
		
		spetialCallCache.put(remoteAddr,spetialCall);
	}

	public long getLastSpetialCallTime(String remoteHost) {
		if (spetialCallCache.containsKey(remoteHost)){
			return spetialCallCache.get(remoteHost).getTime();
		} else {
			return 0;
		}
	}
	
	public SpetialCall getLastSpetialCall(String remoteHost){
		if (spetialCallCache.containsKey(remoteHost)){
			return spetialCallCache.get(remoteHost);
		} else {
			return null;
		}
	}

}

