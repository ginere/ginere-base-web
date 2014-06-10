package eu.ginere.base.web.connectors.users;

import java.util.List;

import eu.ginere.base.util.i18n.Language;

/**
 * Connectores de rights
 * 
 */
public interface UsersConnectorInterface {

	/**
	 * Devuelve una linea con el descriptivo del usuario.
	 * Dependiendo de la implementacion esta linea puede ser los nombres y apellidos, etc ...
	 * @param userId
	 * @return
	 */
	public String getUserInfoLine(String userId);

	public String getUserName(String userId);

	/**
	 * Devuelve el identificador de la lengua del usuario
	 * @param userId
	 * @return
	 */
	public Language getUserLanguage(String userId,Language defaultValue);

	/**
	 * Cuando el usuario cambia su lengua esto les permite actualizar la lengua
	 * 
	 * @param userId
	 * @param langId
	 */
	public void setUserLanguage(String userId,Language language);

	public List<String> getUserGroupNames(String userId);

	public boolean exists(String userId);

	/**
	 * cada conexion http que se realiza se verifica que el cokie del usuario es correcto.
	 * 
	 * @param userId
	 * @param uuid
	 * @param cookieValue
	 * @return
	 */
	public boolean validateLoginCookie(String userId, String uuid,String cookieValue);

	
	public boolean login(String userId, String password);

	/**
	 * This is called when session expires.
	 * 
	 * @param userId
	 */
	public void disconnect(String userId);
	
//	public void connect(String userId);

	String generateUUID();
	
	/**
	 * Una vez el usuario logeado se llama a esta funcion
	 * Genrates and stores the login cookie for the uuid.
	 * @param userId
	 * @return
	 */
	public String generateLoginCookie(String userId,String uuid);
	
//	public void removeLoginCookie(String userId,String uuid);
	
	/**
	 * Splicity disconect user. if all is true disconect user from all the devices.
	 * @param userId
	 * @param uuid
	 * @param all
	 */
	public void logout(String userId,String uuid,boolean all);

	/**
	 * Uses cookie for login and logout from session.
	 * If this is true, if a valid coockie is not present the server
	 * will disconect user. 
	 * 
	 * @return
	 */
	public boolean useCookiesForLogin();
	



}
