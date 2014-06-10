/**
 * Copyright: Angel-Ventura Mendo Gomez
 *	      ventura@free.fr
 *
 * $Id: UserSession.java,v 1.2 2006/11/25 07:19:55 ventura Exp $
 */
package eu.ginere.base.web.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.ginere.base.util.lang.MapOfLists;
import eu.ginere.base.util.notification.Notify;

/**
 * This allow to view the users logged
 *
 * @author Angel Mendo
 * @version $Revision: 1.2 $
 */
public class UserSession  {
	public static final Logger log = Logger.getLogger(UserSession.class);
	
	private static final MapOfLists<String,AbstractSession> USER_MAP=new MapOfLists<String,AbstractSession> ();

    private static final Vector<UserDisconnectedListener> userDisconnectedListenerList=new Vector<UserDisconnectedListener>();
    private static final Vector<UserConnectedListener> userConnectedListenerList=new Vector<UserConnectedListener>();

	static public interface UserDisconnectedListener{
		/**
		 * WARNING the session is invalidated when passed to this function
		 * there is several functions that can't be called like session.getAttribute()
		 * pay attention.
		 */
		public void disconnected(String userId);
    }

	static public interface UserConnectedListener{
		/**
		 * WARNING the session is invalidated when passed to this function
		 * there is several functions that can't be called like session.getAttribute()
		 * pay attention.
		 */
		public void connected(String userId);
    }


	private static int usersConnected=0;

	/**
	 * returns true if the user is logged
	 */
	static public boolean isUserLogged(String user){
		return USER_MAP.containsKey(user);
	}
	
	/**
	 * Try to remove the use id it exists
	 */
//	static private void removeUser(String sessionId){
//
//		Collection<List<AbstractSession>> values=USER_MAP.values();
//
//		for(Iterator<List<AbstractSession>>i=values.iterator();i.hasNext();){
//			List<AbstractSession> list=(List<AbstractSession>)i.next();
//			for(Iterator<AbstractSession> j=list.iterator();j.hasNext();){
//				AbstractSession current=(AbstractSession)j.next();
//				if (current==null || current.getId()==null || current.getId().equals(sessionId) ){
//					j.remove();
//					if (list.size()==0){
//						i.remove();
//						usersConnected--;
//					}
//					// return ;
//				}
//			}
//	
//		}
//	}
	
	static void removeUser(AbstractSession session){
		String userId=session.getUserId();
		
		if (userId!=null){
			removeUser(session.getUserId(), session);
		}
	}
	

	static void removeUser(String userId,AbstractSession session){		
	
		// the user Id is not null
		
		String sessionId=session.getId();

		if (USER_MAP.containsKey(userId)){
			List<AbstractSession> list=USER_MAP.get(userId);
			if (list!=null && list.size()>0){
				for (Iterator<AbstractSession> i=list.iterator();i.hasNext();){
					AbstractSession current=(AbstractSession)i.next();
					if (sessionId.equals(current.getId())){
						i.remove();
					}
				}
				if (list.size()==0){
					USER_MAP.remove(userId);
					usersConnected--;
					
					callUserDisconectedListeners(userId);
				}
			} else {
				USER_MAP.remove(userId);
				log.warn(" The userid:"+userId+" has an empty list:"+list);
			}
		} 


	}

	/**
	 * Clonned can modify
	 */
	static public List<AbstractSession> getUserSession(String userId){
		List<AbstractSession> list=USER_MAP.get(userId);
		if (list==null){
			return (List<AbstractSession>)Collections.EMPTY_LIST;
		} else {
			return new ArrayList<AbstractSession>(list);
		}
	}


	/**
	 * Clonned can modify
	 */
	static public List<String> getUserList(){
		Set<String> set=USER_MAP.keySet();
		if (set==null){
			return Collections.EMPTY_LIST;
		} else {
			return new ArrayList<String>(set);
		}
	}

	static void setUser(String userId,AbstractSession session){
		session.setUser(userId);
		if (!isUserLogged(userId)){
			callUserConectedListeners(userId);
			usersConnected++;
		}
		USER_MAP.put(userId,session);
	}

	
	static public int getUsersConnected(){
		return usersConnected;
	}
	

	static public void addListener(UserDisconnectedListener listener){
		userDisconnectedListenerList.add(listener);
    }

	static public void addListener(UserConnectedListener listener){
		userConnectedListenerList.add(listener);
    }

	private static void callUserDisconectedListeners(String userId){
		Notify.info(log, "User disconected:'"+userId+"'");
		for (UserDisconnectedListener listener:userDisconnectedListenerList){
			listener.disconnected(userId);
		}
    }

	private static void callUserConectedListeners(String userId){
		Notify.info(log, "User connected:'"+userId+"'");
		
		for (UserConnectedListener listener:userConnectedListenerList){
			listener.connected(userId);
		}
    }

}

