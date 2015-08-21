package eu.ginere.base.web.services;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.ginere.base.web.connectors.i18n.I18NConnector;
import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.listener.AbstractWebContextListener;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.menu.MenuManager;
import eu.ginere.base.web.servlet.ActionServlet;
import eu.ginere.base.web.servlet.info.ServletArgs;


public class ClearI18NCacheAction extends ActionServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String URI = "/servlet/util/ClearI18NCacheAction";
	private static final String DESCRIPTION = "Clear the I18N cache";
	

	@Override
	protected ServletArgs[] getArgs() {
		return ServletArgs.NO_ARGS;
	}
	
	@Override
	protected void doSimpleActionService(HttpServletRequest request,
							 HttpServletResponse response) throws ServletException {
		
		I18NConnector.clearCache();
		MenuManager.clearI18NCache();
	}

	
	
	@Override
	protected RightInterface[] getRights() throws ContextInitializedException {
//		return new RightInterface[]{AbstractWebContextListener.ADMIN_TECH_RIGHT};
		return AbstractWebContextListener.SUPER_ADMIN_TECH;
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
