package eu.ginere.base.web.connectors.file;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import eu.ginere.base.web.listener.ContextInitializedException;

public class FileConnector extends eu.ginere.base.util.file.FileConnector {
	static Logger log = Logger.getLogger(FileConnector.class);
	
	private static final String CONNECTOR_CLASS_NAME = "FileConnectorClassName";

	public static void init(ServletContext context) throws ContextInitializedException{
		String className = context.getInitParameter(CONNECTOR_CLASS_NAME);

		log.info("Initializing the FileConector:'"+className+"' ...");

		try {
			eu.ginere.base.util.file.FileConnector.init(className);
		}catch (Exception e){
			String message="FileConnector, No se puede crear un objeto del tipo:'"+className+"'";
			log.error(message);
			throw new ContextInitializedException(message);
		}
	}
}
