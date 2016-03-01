package eu.ginere.base.web.connectors.rights;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.servlet.MainServlet;




/**
 * Connectores de rights
 * 
 */
public class RightConnector {
	static Logger log = Logger.getLogger(RightConnector.class);
	
	private static final String RIGHT_CONNECTOR_CLASS_NAME = "RightConnectorClassName";

	
	private static RightConnectorInterface connector=new DummyRightConnector();


	public static void init(ServletContext context) throws ContextInitializedException{
		log.info("RightConnector initializing...");
		String className = context.getInitParameter(RIGHT_CONNECTOR_CLASS_NAME);
		
		if (className == null) {
			log.warn("Avoiding "+RightConnector.class.getName()+" initialization, context parameter '"+RIGHT_CONNECTOR_CLASS_NAME+"' is null.");
			return ;
		} 

		try {
			Class<?> clazz=Class.forName(className);
			connector=(RightConnectorInterface)clazz.newInstance();
			log.info("RightConnector initialized with class:'"+className+"'");
		}catch (Exception e){
			log.error("Can NOT initialize RightConnector with class:'"+className+"'",e);
			throw new ContextInitializedException("Can NOT initialize RightConnector with class:'"+className+"'",e);
		}

	}	
	
	/**
	 * Verifica si un usuario posee un derecho. La realacion entre los usuarios
	 * y los derechos se hace mediante los grupos a los cuales pertenece un
	 * usuario
	 * 
	 * @param userId
	 * @param rightId
	 * @return
	 */
	public static boolean hasRight(String userId, RightInterface right) {
		return hasRight(userId, right.getId());
	}

	public static boolean hasRight(RightInterface right) {
		
		String userId=MainServlet.getThreadLocaluserId();
		if (userId!=null){
			return hasRight(userId, right);
		} else {
			return false;
		}
	}
	
	/**
	 * Verifica si un usuario posee un derecho. La realacion entre los usuarios
	 * y los derechos se hace mediante los grupos a los cuales pertenece un
	 * usuario
	 * 
	 * @param userId
	 * @param rightId
	 * @return
	 */
	public static boolean hasRight(String userId, String rightId) {
		if (rightId == null || "".equals(rightId)){
			return true;
		} else {
			return connector.hasRight(userId, rightId);
		}
	}

	public static void subscriveApplicationRights(
			RightInterface[] applicationPermision) {
		connector.subscriveApplicationRights(applicationPermision);		
	}

}
