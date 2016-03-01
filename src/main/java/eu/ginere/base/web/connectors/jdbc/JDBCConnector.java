package eu.ginere.base.web.connectors.jdbc;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import eu.ginere.base.web.listener.ContextInitializedException;


public class JDBCConnector {
	static final Logger log = Logger.getLogger(JDBCConnector.class);


	private static final String DATASOURCE_JNI_PROPERTIE_NAME = "jniDatasource";
	private static final String DATASOURCE_JNI_DEFAULTNAME = "jdbc/DB";

	private static final String JDBC_CONNECTOR_CLASS_NAME = "JdbcConnectorClassName";

	public static void init(ServletContext context)throws ContextInitializedException{
		String className = context.getInitParameter(JDBC_CONNECTOR_CLASS_NAME);

		if (className == null) {
			log.warn("Avoiding "+JDBCConnector.class.getName()+" initialization, context parameter '"+JDBC_CONNECTOR_CLASS_NAME+"' is null.");
		} else {
			init(context,className);
		}
	}

	public static void init(ServletContext context,String className)throws ContextInitializedException{
				
		log.info("Initializing the JDBCConector:'"+className+"' ...");
		try {
			Class clazz=Class.forName(className);
			JDBCConnectorInterface connector=(JDBCConnectorInterface)clazz.newInstance();
			
			String jniName = context.getInitParameter(DATASOURCE_JNI_PROPERTIE_NAME);
			if (jniName == null || "".equals(jniName)) {
				log.fatal("The property:'"
						  + DATASOURCE_JNI_PROPERTIE_NAME
						  + "' is not fount into the conextext properties, ussing the default datasource name:'"
						  + DATASOURCE_JNI_DEFAULTNAME + "'");
				jniName = DATASOURCE_JNI_DEFAULTNAME;
			} else {
				log.info("Using the datasource:'" + jniName + "'.");
			}
				
			try {
				// Inicializamos la datasource
				connector.setJniDataSource(jniName);
				
				
				if (!connector.testConnection()){
					log.fatal("Can NOT initialize connection to datasource:"+jniName);
				} else {
					log.info("Connection establiseh with datasource:"+jniName);
				}
				
			} catch (Exception e) {
				throw new ContextInitializedException("JDBCConnector, While initializing the data source:'" + jniName
													  + "'", e);
			}
			
			log.info("Connector initialized with class:'"+className+"'");
		}catch (ContextInitializedException e){
			throw e;
		}catch (Exception e){
			throw new ContextInitializedException("JDBCConnector, can not create object:'"+className+"'", e);
		}

	}

//	private static void initializeDataSource(ServletContext context) throws ContextInitializedException {
//		String jniName = context.getInitParameter(DATASOURCE_JNI_PROPERTIE_NAME);
//		if (jniName == null || "".equals(jniName)) {
//			log.fatal("La propiedad:'"
//					  + DATASOURCE_JNI_PROPERTIE_NAME
//					  + "' que define el nombre de la Datasource no se ha encontrado, utilizando el valor por defecto:'"
//					  + DATASOURCE_JNI_DEFAULTNAME + "'");
//			jniName = DATASOURCE_JNI_DEFAULTNAME;
//		} else {
//			log.info("Utilizando la source:'" + jniName + "'.");
//		}
//		
//		try {
//			// Inicializamos la datasource
//			DataSource datasource = JdbcManager.getJniDataSource(jniName);
//			
//			JdbcManager.setDataSource(datasource);
//			
//			if (!DaoManager.testConnection()){
//				log.fatal("Cann't initialize connection to datasource:"+jniName);
//			} else {
//				log.info("Connection establiseh with datasource:"+jniName);
//			}
//			
//		} catch (Exception e) {
//			throw new ContextInitializedException("Mientras se inicializaba la fuente de datos:'" + jniName
//												  + "'", e);
//		}
//	}

}
