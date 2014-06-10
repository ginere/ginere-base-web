package eu.ginere.base.web.connectors.rights;

/**
 * Esta clase representa a los permisos disponibles en el sistema. Estos se
 * corresponden con funcionalidades del mismo por lo que los permisos no se
 * crean dinamicamente si no que se anyaden con la creacion de nuevas
 * funcionalidades.
 * 
 */
public interface RightInterface {

	/**
	 * cn. Identificador unico del objeto. Nombre corto del permiso.
	 * 
	 * @return the name
	 */
	public String getId();

	/**
	 * Descripcion del permiso. Campo opcional
	 * 
	 * @return the description
	 */
	public String getName();
	
	/**
	 * Una descripcion para que el encargado de gestionar los derechos sepa para que serive ese permiso
	 * @return
	 */
	public String getDescription();

	/**
	 * Se utiliza para
	 * borrar los permisos obsoletos
	 */
	public String getAppId();
}
