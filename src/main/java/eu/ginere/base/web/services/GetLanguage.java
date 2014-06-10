package eu.ginere.base.web.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.util.i18n.Language;
import eu.ginere.base.web.connectors.i18n.I18NConnector;
import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.servlet.MainServlet;
import eu.ginere.base.web.servlet.SimpleDataServlet;
import eu.ginere.base.web.servlet.info.ServletArgs;

@SuppressWarnings("serial")
public class GetLanguage extends SimpleDataServlet{

	public static final String URI = "/servlet/util/GetLanguage";
	public static final String DESCRIPTION = "Returns the current language for this request";

	@Override
	protected ServletArgs[] getArgs() {
		return ServletArgs.NO_ARGS;
	}
	
	protected String getDefaultString(){
		return I18NConnector.getDefaultLanguage().langId;
	}
	@Override
	protected String doSimpleDataService(HttpServletRequest request,
									   HttpServletResponse response) throws ServletException, IOException,DaoManagerException {
		
		Language lang=getLanguage();
		
		return lang.getId();
	}
	
	
	@Override
	protected RightInterface[] getRights() throws ContextInitializedException {
		return MainServlet.PUBLIC_ACCESS;
	}


	@Override
	protected String getUri() {
		return URI;
	}

	@Override
	protected String getDescription() {
		return DESCRIPTION;
	}

}
