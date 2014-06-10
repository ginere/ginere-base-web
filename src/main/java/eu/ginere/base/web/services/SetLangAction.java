package eu.ginere.base.web.services;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.ginere.base.util.i18n.Language;
import eu.ginere.base.web.connectors.i18n.I18NConnector;
import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.servlet.ActionServlet;
import eu.ginere.base.web.servlet.MainServlet;
import eu.ginere.base.web.servlet.info.ServletArgs;


public class SetLangAction extends ActionServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String URI = "/servlet/util/Ping";
	private static final String DESCRIPTION = "Answer with the PONG string";

	@Override
	protected ServletArgs[] getArgs() {
		return new ServletArgs[]{
				ServletArgs.getMandatoryStringParameter("The Language Object ID ","languageId"),
		};	
	}
	
	@Override
	protected void doSimpleActionService(HttpServletRequest request,
							 HttpServletResponse response) throws ServletException {
		String languageId=getMandatoryStringParameter(request, "languageId");
		Language language=I18NConnector.getLanguageFromLanguageId(languageId, null);
		
		if (language!=null){
			setLanguage(request, language);
		}
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
