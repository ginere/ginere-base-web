package eu.ginere.base.web.connectors.users;

import java.util.List;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import eu.ginere.base.util.i18n.Language;
import eu.ginere.base.web.connectors.i18n.I18NConnector;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.session.UserSession;


/**
 * Connectores de usuarios
 * 
 */
public class UsersConnector {
	static Logger log = Logger.getLogger(UsersConnector.class);
	
	private static UsersConnectorInterface connector=new DummyUsersConnector();

	private static final String USERS_CONNECTOR_CLASS_NAME = "UsersConnectorClassName";
	
	public static void init(ServletContext context) throws ContextInitializedException{
		log.info("UsersConnector initializing...");
		String className = context.getInitParameter(USERS_CONNECTOR_CLASS_NAME);

		if (className == null) {
			log.warn("Avoiding "+UsersConnector.class.getName()+" initialization, context parameter '"+USERS_CONNECTOR_CLASS_NAME+"' is null.");

			return ;
		} 
		
		try {
			Class<?> clazz=Class.forName(className);
			connector=(UsersConnectorInterface)clazz.newInstance();
			log.info("UsersConnector initialized with class:'"+className+"'");
		}catch (Exception e){
			log.error("Can NOT initialize UsersConnector with class:'"+className+"'",e);
			throw new ContextInitializedException("Can NOT initialize UsersConnector with class:'"+className+"'",e);
		}
		
		/*
		  Esto no es necesario por que los usuarios entran por login
		  o explicitamente por validatelogin cokkie por lo que el trabajo
		  hay que hacerlo en esos metodos ...
		// Ading this to the listener
		UserSession.addListener(new UserSession.UserConnectedListener() {
			
			@Override
			public void connected(String userId) {
				connector.connect(userId);
			}
		});
		*/

		// Ading this to the listener
		UserSession.addListener(new UserSession.UserDisconnectedListener() {
			
			@Override
			public void disconnected(String userId) {
				connector.disconnect(userId);
			}
		});
	}
	
	
//	/**
//	 * For the UsersSyncWebConnectorImpl
//	 * 
//	 * @param userId
//	 */
//	static void connect(String userId){
//		connector.connect(userId);
//	}

	/**
	 * For the UsersSyncWebConnectorImpl. Su utiliza en la expiracion de las
	 * sessiones para desconectar un usuario cuando su session ha expirado
	 * 
	 * @param userId
	 */
	static void disconnect(String userId){
		connector.disconnect(userId);
	}
	
	public static String getUserInfoLine(String userId){
		return connector.getUserInfoLine(userId);
	}
	
	public static String getUserName(String userId){
		return connector.getUserName(userId);
	}
	
	public static Language getUserLanguage(String userId,Language defaultvalue){
		return connector.getUserLanguage(userId,defaultvalue);
	}

	public static void setUserLanguage(String userId,Language language){
		connector.setUserLanguage(userId,language);
	}

	public static List<String> getUserGroupNames(String userId) {
		return connector.getUserGroupNames(userId);
	}

	public static boolean exists(String userId) {
		return connector.exists(userId);
	}

	/**
	 * Valida la cookie  y en caso de exito notifica que el usuario esta conectado.
	 * Este es el punto de entrada para los usuarios que estan autenticados por defecto
	 * con una cookie en su terminal
	 * 
	 * @param userId
	 * @param uuid
	 * @param cookieValue
	 * @return
	 */
	public static boolean validateLoginCookie(String userId,String uuid, String cookieValue) {
		return connector.validateLoginCookie(userId,uuid,cookieValue);
	}

	/**
	 * Returns the login cookie or if it does not exists, create a new one an return the newed created.
	 * @param userId
	 * @return
	 */
	public static String generateLoginCookie(String userId,String uuid) {
		return connector.generateLoginCookie(userId, uuid);
	}

	/**
	 * Este es el punto de entrada para cuando los usuarios explicitamente se conectan 
	 * a la aplicacion.
	 */
	public static boolean login(String userId, String password) {
		return connector.login(userId,password);
	}

//	public static void removeLoginCookie(String userId,String uuid) {
//		connector.removeLoginCookie(userId,uuid);
//	}

	/**
	 * Splicity disconect user. if all is true disconect user from all the devices.
	 * 
	 * @param userId
	 * @param uuid
	 * @param all
	 */
	public static void logout(String userId,String uuid,boolean all) {
//		connector.removeLoginCookie(userId,uuid);
		connector.logout(userId,uuid,all);
	}
	public static Language getLanguage(String userId) {
		Language language=getUserLanguage(userId,null);

		if (language==null || !I18NConnector.isAvailableLanguage(language)){
			return I18NConnector.getDefaultLanguage();
		} else {
			return language;
		}

	}

	public static String generateUUID() {
		return connector.generateUUID();
	}


	public static boolean useCookiesForLogin() {
		return connector.useCookiesForLogin();
	}
}
