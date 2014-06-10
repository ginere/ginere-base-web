package eu.ginere.base.web.connectors.rights;

/**
 * Connectores de rights
 * 
 */
public interface RightConnectorInterface {

	/**
	 * Verifica si un usuario posee un derecho. La realacion entre los usuarios
	 * y los derechos se hace mediante los grupos a los cuales pertenece un
	 * usuario
	 * 
	 * @param userId
	 * @param rightId
	 * @return
	 */
	public boolean hasRight(String userId, String rightId) ;
	
	public void subscriveApplicationRights(RightInterface[] applicationPermision);


}
