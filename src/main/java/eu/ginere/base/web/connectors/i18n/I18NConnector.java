package eu.ginere.base.web.connectors.i18n;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import eu.ginere.base.web.listener.ContextInitializedException;


public class I18NConnector extends eu.ginere.base.util.i18n.I18NConnector{
	static Logger log = Logger.getLogger(eu.ginere.base.util.i18n.I18NConnector.class);
	
	private static final String I18N_CONNECTOR_CLASS_NAME = "I18NConnectorClassName";

	public static void init(ServletContext context) throws ContextInitializedException{
		String className = context.getInitParameter(I18N_CONNECTOR_CLASS_NAME);

		log.info("Initializing the I18NConnector:'"+className+"' ...");

		if (className == null) {
			log.warn("Avoiding "+I18NConnector.class.getName()+" initialization, context parameter '"+I18N_CONNECTOR_CLASS_NAME+"' is null.");

			return ;
		} 

		try {
			eu.ginere.base.util.i18n.I18NConnector.init(className);
		}catch (Exception e){
			String message="I18NConnector,No se puede crear un objeto del tipo:'"+className+"'";
			log.error(message);
			throw new ContextInitializedException(message);
		}
	}
}
