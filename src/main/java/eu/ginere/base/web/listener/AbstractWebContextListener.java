package eu.ginere.base.web.listener;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.util.notification.Notify;
import eu.ginere.base.util.properties.GlobalFileProperties;
import eu.ginere.base.util.test.TestInterface;
import eu.ginere.base.util.test.TestResult;
import eu.ginere.base.web.connectors.file.FileConnector;
import eu.ginere.base.web.connectors.i18n.I18NConnector;
import eu.ginere.base.web.connectors.jdbc.JDBCConnector;
import eu.ginere.base.web.connectors.rights.RightConnector;
import eu.ginere.base.web.connectors.rights.RightImpl;
import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.connectors.users.UsersConnector;
import eu.ginere.base.web.servlet.MainServlet;

/**
 * Esta clase carga las propiedades del fichero de texto que se le indique
 * 
 * @author Michele Tagliaferri
 * 
 */
public abstract class AbstractWebContextListener implements ServletContextListener,TestInterface {

	static final Logger log = Logger.getLogger(AbstractWebContextListener.class);

	private static final String FILE_PATH_PROPERTIE_NAME = "GlobalPropertiesFilePath";


	private static final Hashtable <String,RightInterface>contextRights=new Hashtable<String,RightInterface>();
	private static final Vector<MainServlet>contextServlets=new Vector<MainServlet>();
	private static final Hashtable <String,MainServlet>contextServletsMap=new Hashtable<String,MainServlet>();
	
	
	public static final RightInterface ADMIN_TECH_RIGHT=new RightImpl("WEB-ADMIN-01","Mantenimiento Tecnico","Tareas tecnicas de mantenimiento, como actualizar las etiquetas multilingües, etc ...","WEB_COMMON");

	public static final RightInterface SUPER_ADMIN_TECH[]={
		ADMIN_TECH_RIGHT,
	};

	
	private static final RightInterface COMMON_APPLICATION_RIGHT_ARRAY[]={
		ADMIN_TECH_RIGHT,
	};
	
	private static long startTime = -1;
	private static Date startDate = null;
	
	/**
	 * Implementar esta funcion en caso de tener que liberar algo cuando se
	 * cierre la aplicacion
	 * 
	 * @param sce
	 * @param appName
	 *            Nombre del contexto de la aplicacion que se lanza
	 * @throws ContextInitializedException
	 */
	public abstract void webContextDestroyed(ServletContextEvent sce,String appName) throws ContextInitializedException,DaoManagerException;

	/**
	 * Implementar esta funcion en caso que tener que crear objetos cuando se
	 * lanze la aplicacion
	 * 
	 * @param sce
	 * @param appName
	 *            Nombre del contexto de la aplicacion que se lanza
	 * @throws ContextInitializedException
	 */
	public abstract void webContextInitialized(ServletContextEvent sce,String appName) throws ContextInitializedException,DaoManagerException;


	/**
	 * Devuelve la lista completa de permisos de la aplicacion para ser subcritos en el contenedor 
	 * de permisos. Los permisos tienen que estar definidos en el Contexta file Hijo de donde tirarran los servlets
	 */
	public abstract RightInterface[] getApplicationPermisions()throws ContextInitializedException;
	

	protected boolean doInitializeGlobalFilePropertiesPath(){
		return true;
	}

	protected boolean doInitializeJDBCConnector(){
		return true;
	}


	public void contextDestroyed(ServletContextEvent sce) {
		String appName = sce.getServletContext().getContextPath();
		Notify.warn(log,"Destrullendo el contexto de la plicacion:" + appName);
		
		// First destroy childs
		try {
			webContextDestroyed(sce,appName);
		} catch (Exception e) {
			Notify.fatal(log,"Mientras se destruia el contexto:'" + appName + "'", e);
		}
		// Then nothing to do, here possible stuff
	}

	/**
	 * Metodo que se llama al levantar la aplicacion web. El metodo lee un
	 * fichero XML de configuracion ("permission.xml") con las definiciones de
	 * permisos para la applicaciones y sincroniza estos nuevos permisos con los
	 * que ya existen en LDAP.
	 * 
	 * @author Michele Tagliaferri
	 * @param sce
	 *            Es el contexto de la aplicacion.
	 */
	public void contextInitialized(ServletContextEvent sce) {
		String appName = sce.getServletContext().getContextPath();
		log.warn("----------------------------------------------------");
		Notify.warn(log,"Inicializando el contexto de la plicacion:" + appName);
		
		/*
		 * Cogemos el contexto de la aplicacion a partir del context servlet
		 */
		ServletContext context = sce.getServletContext();

		try {
			// Inicializamos el fihero de propiedades
			if (doInitializeGlobalFilePropertiesPath()){
				initializeGlobalFilePropertiesPath(context);
			}

			// Inicializamos la datasource
			if (doInitializeJDBCConnector()){
				JDBCConnector.init(context);
			}


			// Initialize el I18n
			I18NConnector.init(context);

			// Incializamos el contexto de ficheros
			FileConnector.init(context);
			
			// Inicializamos el Ldap
			RightConnector.init(context);

			// Cargamos los permisos de la aplicacion
			loadApplicationPermisions(context,appName);
			

			// Inicializamos los usuarios
			UsersConnector.init(context);

			// Then call Childs
			webContextInitialized(sce,appName);
			
			
			log.warn("Context initialization finished:" + appName);
			log.warn("----------------------------------------------------");
			startDate=new Date();
			startTime=startDate.getTime();
			
			log.warn("----------------------------------------------------");
			log.warn("Executing test ...:" + appName);
			TestResult test=test();
			if (test.isOK()){
				log.warn(" Tests executed: OK");
			} else {
				log.warn(" Tests FAILS: ");
				log.warn(test);
				log.warn(" The application is not stoped. After considering the results of the test maybe  you should to stop it!. ");
			}
			log.warn("----------------------------------------------------");
						
		} catch (Exception e) {
			if (!GlobalFileProperties.getBooleanValue(AbstractWebContextListener.class, "Installing", false)) {	
				Notify.fatal(log,"Mientras se iniciaba el contexto:'" + appName + "'", e);
				throw new RuntimeException("Mientras se iniciaba el contexto:'" + appName + "'", e);
			} else {
				Notify.error(log,"Mientras se iniciaba el contexto:'" + appName + "'", e);				
			}
		}
	}

//	private void initializeRightConnector(ServletContext context) throws ContextInitializedException{
//		log.info("Inicializando el conector de permisos...");
//		String connectorInterfaceClassName = context.getInitParameter(RIGHT_CONNECTOR_CLASS_NAME);
//		RightConnector.init(connectorInterfaceClassName);
//		
//	}

	private void initializeGlobalFilePropertiesPath(ServletContext context) throws ContextInitializedException {
		/*
		 * Leemos en el fichero web.xml el path de configuraci�n
		 * "permissionFile"
		 */
		String filePath = context.getInitParameter(FILE_PATH_PROPERTIE_NAME);
		
		if (filePath == null || "".equals(filePath)) {
			log.warn("la variable :'" + FILE_PATH_PROPERTIE_NAME+ "' no ha sido inicializada en el web.xml");
		} else {
			log.info("Cargando el fichero de configuracion global en el path:'"+ filePath + "' definido en el web.xml.");
			GlobalFileProperties.setInitFilePath(filePath);
		}

	}

	/**
	 * Devuelve la lista completa de permisos de la aplicacion para ser subcritos en el contenedor 
	 * de permisos. Los permisos tienen que estar definidos en el Contexta file Hijo de donde tirarran los servlets
	 */
	private RightInterface[] getInnerApplicationPermisions(String appName)throws ContextInitializedException{
		RightInterface applicationRightArray[]=getApplicationPermisions();
		
		if (applicationRightArray==null || applicationRightArray.length==0){
			log.warn("No rights defined for application");
			return COMMON_APPLICATION_RIGHT_ARRAY;
		}
		
		RightInterface[] ret= new RightInterface[COMMON_APPLICATION_RIGHT_ARRAY.length+applicationRightArray.length];

		System.arraycopy(COMMON_APPLICATION_RIGHT_ARRAY, 0, ret, 
						 0, COMMON_APPLICATION_RIGHT_ARRAY.length);
		System.arraycopy(applicationRightArray, 0, ret, 
						 COMMON_APPLICATION_RIGHT_ARRAY.length, applicationRightArray.length);

		return ret;
	}

	/**
	 * Metodo utilizado para cargar en el directorio LDAP los permisos
	 * requeridos por la aplicacion
	 */
	public void loadApplicationPermisions(ServletContext context,String appName) throws ContextInitializedException {

		try {
			RightInterface applicationPermision[]=getInnerApplicationPermisions(appName);
			RightConnector.subscriveApplicationRights(applicationPermision);
		} catch (Exception e) {
			throw new ContextInitializedException(" No se ha podido inicializar el contexto de la aplicacion."
												  + " Error subscriviendo los permisos ",
												  e);

		}
	}

	public static void addServlet(MainServlet mainServlet) {
		String key=mainServlet.getClass().getName();
		
		if (!contextServletsMap.containsKey(key)){
			contextServlets.add(mainServlet);
			contextServletsMap.put(key,mainServlet);
		}
	}
	
	public static List<MainServlet> getServletList() {
		return contextServlets;
	}
	
	public static MainServlet getServlet(String id) {
		return contextServletsMap.get(id);
	}
	

	public static Date getStartTime() {
		return startDate;
	}

	public static long getSystemLastModified() {
		return startTime;
	}
}
